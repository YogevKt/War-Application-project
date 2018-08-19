package Server.War.BusinessLogic.controllers;

import Entities.Launcher;
import Entities.Missile;

import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class LauncherController extends DestructibleController<Launcher> implements Iterable<MissileController>{
    private HashMap<Missile, MissileController> missileControllers = new HashMap<>();
    private MissileController activeMissileController = null;

    public LauncherController(Launcher destructibleWeapon) {
        super(destructibleWeapon);
    }

    public Launcher getLauncher() {
        return getDestructibleWeapon();
    }

    @Override
    public boolean isDestructed(){
        return getDestructibleWeapon().isDestructed();
    }

    @Override
    public boolean destruct() {

        if(getLauncher().isDestructed())
            return false;

        if(getLauncher().isHidden())
            return false;

       if(getLock().tryLock()) {
           getLauncher().destruct();
           getCondition().signalAll();
           getLock().unlock();
           return true;
       }
       return false;
    }

    public synchronized boolean launch(Missile missile, long maxFlightTime) throws InterruptedException, ExecutionException, IllegalAccessException {
        while(isCurrentlyLaunching());

        //create new missile&missileController
        getLauncher().addMissile(missile);
        MissileController missileController = new MissileController(missile, maxFlightTime,this);
        missileControllers.put(missile, missileController);
        setActiveMissileController(missileController);
        setCurrentlyLaunching(true);

        //start missile flying
        ExecutorService missileFlyingThread = Executors.newSingleThreadExecutor();
        Future<Boolean> missileHit = missileFlyingThread.submit(missileController);

        //waiting for hit/destruct
        synchronized (this){
            wait();
        }

        setCurrentlyLaunching(false);
        setActiveMissileController(null);

       // System.out.println("Launch completed!\n");
        //getLaunchingLock().unlock();

        return missileHit.get();
    }

    public MissileController getActiveMissileController() {
        return activeMissileController;
    }

    private void setActiveMissileController(MissileController activeMissileController) {
        this.activeMissileController = activeMissileController;
    }

    public boolean isCurrentlyLaunching() {
        return getLauncher().isCurrentlyLaunching();
    }

    public void setCurrentlyLaunching(boolean currentlyLaunching) {
        getLauncher().setCurrentlyLaunching(currentlyLaunching);
    }

    @Override
    public Iterator<MissileController> iterator() {
        return missileControllers.values().iterator();
    }

    public MissileController getMissileController(Missile missile){
        if(!missileControllers.containsKey(missile))
            throw new NoSuchElementException("Missile id was not found in this launcher controller");

        return missileControllers.get(missile);
    }

    @Override
    public Boolean call() throws Exception {
        getLock().lock();
        try {
            getCondition().await();
        }finally {
            getLock().unlock();
        }
        return true;
    }

}