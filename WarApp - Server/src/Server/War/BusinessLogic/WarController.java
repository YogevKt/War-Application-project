package Server.War.BusinessLogic;

import Entities.*;
import Server.DAL.DBController;
import Server.DAL.DBServicesInterface;
import Server.Messaging.MessageController;
import Server.War.BusinessLogic.controllers.LauncherController;
import Server.War.BusinessLogic.controllers.LauncherDestructorController;
import Server.War.BusinessLogic.controllers.MissileController;
import Server.War.BusinessLogic.controllers.MissileDestructorController;
import Server.gson.JsonHandler;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;


public class WarController implements WarControllerFacade { //Singleton
    private static WarController warController = null;
    
    private static LocalTime startRunTime = LocalTime.now();
    private HashMap<Launcher, LauncherController> missileLaunchers;
    private HashMap<MissileDestructor, MissileDestructorController> missileDestructors;
    private HashMap<LauncherDestructor, LauncherDestructorController> launcherDestructors;
    private DBServicesInterface dbService;

    private MessageController messageController;
    //private WarDao dao;

    private Statistics statistics = new Statistics();

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private WarController(){
        missileLaunchers = new HashMap<>();
        missileDestructors = new HashMap<>();
        launcherDestructors = new HashMap<>();
        messageController = MessageController.getInstance();

        dbService = DBController.getInstance().getDbServicesInterface();
    }

    public static void startTime(){
        startRunTime = LocalTime.now();
    }

    public static WarControllerFacade getInstance(){
        if(warController == null)
            warController = new WarController();

        return warController;
    }

    @Override
    public void addLauncher(Launcher launcher){
        LauncherController launcherController = new LauncherController(launcher);
        executorService.submit(launcherController);
        missileLaunchers.put(launcher, launcherController);

        dbService.saveMissileLauncher(launcher);
        messageController.launcherWasAdded(launcher);
    }

    @Override
    public void addLauncherDestructor(LauncherDestructor launcherDestructor) {
        launcherDestructors.put(launcherDestructor, new LauncherDestructorController(launcherDestructor));

        dbService.saveLauncherDestructor(launcherDestructor);
        messageController.launcherDestructorWasAdded(launcherDestructor);
    }

    @Override
    public void addMissileDestructor(MissileDestructor missileDestructor) {
        missileDestructors.put(missileDestructor, new MissileDestructorController(missileDestructor));

        dbService.saveMissileDestructor(missileDestructor);
        messageController.missileDestructorWasAdded(missileDestructor);
    }

    @Override
    public boolean launchMissile(Launcher launcher, Destination destination, double potentialDamage) {
        final int MAX_TIME = 11;

        int maxFlightTime = new Random().nextInt(MAX_TIME) + 4;
        return launchMissile(launcher, destination, potentialDamage, maxFlightTime);
    }

    @Override
    public boolean launchMissile(Launcher launcher, Destination destination, double potentialDamage, long maxFlightTime) {
        Missile missile = new Missile(potentialDamage, destination, maxFlightTime, getTime());

        return launchMissile(launcher, missile);
    }

    @Override
    public boolean launchMissile(Launcher launcher, Missile missile){
        boolean hit = false;
        LauncherController launcherController = missileLaunchers.getOrDefault(launcher, null);

        if(launcherController == null){
            return false;
        }

        messageController.missileLaunched(launcher, missile, missile.getDestination());

        statistics.increaseNumOfLaunchedMissiles();

        try {
            hit = executorService.submit(() -> launching(launcherController, missile, missile.getFlightTime())).get();

            if(hit){
                messageController.missileHit(launcher, missile, missile.getDestination());
            }

        }catch (Exception e){
            
        }

        return hit;
    }

    private boolean launching(LauncherController launcherController, Missile missile, long maxFlightTime){
        try {
            boolean hit = false;
            if(!launcherController.isDestructed()) {
                dbService.saveLaunchingMissileDetails(launcherController.getLauncher().getId(),
                        missile.getId(), missile.getDestination().getDest(),
                        LocalDateTime.now());

                hit = launcherController.launch(missile, maxFlightTime);

                dbService.saveHitMissileDetails(launcherController.getLauncher().getId(),
                        missile.getId(), missile.getDestination().getDest(), hit,
                        missile.getDamage(), LocalDateTime.now());
            }

            if(hit) {


                statistics.increaseNumOfHits();
                statistics.raiseDamage(missile.getDamage());
            }
            return hit;
        } catch (InterruptedException |ExecutionException e1) {
            //e1.printStackTrace();
            return false;
        } catch (IllegalAccessException e) {
			//e.printStackTrace();
        	return false;
		}
    }



    @Override
    public boolean destructMissile(MissileDestructor missileDestructor, Missile missile) {
        final int MAX = 5, OFFSET = 1;
        return destructMissile(missileDestructor, missile, new Random().nextInt(MAX) + OFFSET);
    }

    @Override
    public boolean destructMissile(MissileDestructor missileDestructor, Missile missile, long time) {
        boolean destruct = false;
        long timeFromLaunch;
        MissileDestructorController missileDestructorController = missileDestructors.get(missileDestructor);

        for(LauncherController launcherController: missileLaunchers.values()){
            if(launcherController.isCurrentlyLaunching()){
                MissileController missileController = launcherController.getActiveMissileController();

                if(missileController.getMissile().equals(missile)){

                    messageController.missileDestructed(missileDestructor, missile);

                    sleepSpecificTime(time);

                    timeFromLaunch = getTime() - missileController.getMissile().getLaunchTime();

                    Future<Boolean> destructionFuture = executorService.submit(() ->
                            destructingMissile(missileDestructorController, missileController, timeFromLaunch));
                    try {
                        destruct = destructionFuture.get();
                    } catch (InterruptedException | ExecutionException e) {
                        //e.printStackTrace();
                    }
                    return destruct;
                }
            }
        }
        return false;
    }

    private boolean destructingMissile(MissileDestructorController missileDestructorController,
                                       MissileController missileController, long timeFromLaunch){
        dbService.saveLaunchingMissileDestructorDetails(missileDestructorController.getDestructor().getId(),
                missileController.getMissile().getId(), missileController.getMissile().getDestination().getDest(),
                LocalDateTime.now());

        boolean destruct = missileDestructorController.destruct(missileController, timeFromLaunch); //assumption: always FALSE!

        dbService.saveHitMissileDestructorDetails(missileDestructorController.getDestructor().getId(),
                missileController.getMissile().getId(), missileController.getMissile().getDestination().getDest(),
                destruct, LocalDateTime.now());

        if(destruct) {

            statistics.increaseNumOfDestructedMissiles();
        }

        return destruct;
    }

    @Override
    public boolean destructLauncher(LauncherDestructor launcherDestructor, Launcher launcher) throws InterruptedException {
        final int MAX_TIME = 5, TIME_OFFSET = 1;
        int destructionTime = new Random().nextInt(MAX_TIME) + TIME_OFFSET;
        return destructLauncher(launcherDestructor, launcher, destructionTime);
    }

    @Override
    public boolean destructLauncher(LauncherDestructor launcherDestructor, Launcher launcher, int destructionTime) throws InterruptedException {
        boolean succeed;

        LauncherController launcherController = missileLaunchers.getOrDefault(launcher, null);

        if(launcherController == null)
            throw new IllegalArgumentException("launcher doesn't not exist or already destructed");
        LauncherDestructorController launcherDestructorController =
                launcherDestructors.get(launcherDestructor);

        dbService.saveDestructingLauncherDetails(launcherDestructor.getId(), launcher.getId(),LocalDateTime.now());
        messageController.tryToDestructLauncher(launcherDestructor, launcher, destructionTime);

        sleepSpecificTime(destructionTime);

        succeed = launcherDestructorController.destruct(launcherController, getTime());

        dbService.saveHitDestructingLauncherDetails(launcherDestructor.getId(), launcher.getId(),succeed, LocalDateTime.now());

        if(succeed){
            messageController.launcherDestructed(launcherDestructor, launcher);

            missileLaunchers.remove(launcher);
            statistics.increaseNumOfDestructedLaunchers();
        }else{

            messageController.launcherWasHidden(launcherDestructor, launcher);
        }

        return succeed;
    }

    @Override
    public Statistics getStatistics() {
        return statistics;
    }

    @Override
    public void exit() {
    	JsonHandler.terminateJsonHandler();
    	executorService.shutdownNow();
        messageController.endOfWar();
        dbService.dropTables();
    }

    @Override
    public Set<Launcher> retrieveLaunchers() {
        return missileLaunchers.keySet();
    }

    @Override
    public Set<MissileDestructor> retrieveMissileDestructors() {
        return missileDestructors.keySet();
    }

    @Override
    public Set<LauncherDestructor> retrieveLauncherDestructors() {
        return launcherDestructors.keySet();
    }

    @Override
    public Set<Missile> retrieveActiveMissiles() {
        Set<Missile> activeMissiles = new HashSet<>();
        for(LauncherController launcherController : missileLaunchers.values()){
            if(launcherController.isCurrentlyLaunching()) {
                activeMissiles.add(launcherController.getActiveMissileController().getMissile());
            }
        }

        return activeMissiles;
    }

    public long getTime(){
        return SECONDS.between(startRunTime,LocalTime.now());
    }

    private void sleepSpecificTime(long seconds){
        try {
            Thread.sleep(1000 * seconds);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }catch (IllegalArgumentException e){

        }
    }
}
