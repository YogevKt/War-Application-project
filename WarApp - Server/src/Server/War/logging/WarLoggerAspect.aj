package Server.War.logging;


import Entities.*;
import Server.War.BusinessLogic.WarController;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public aspect WarLoggerAspect {
    private Logger logger = Logger.getLogger("Server.War Log");


    public WarLoggerAspect() throws IOException {
        logger.setUseParentHandlers(false);
        Handler handler = new FileHandler("War.log");
        handler.setFormatter(new WarLoggerFormatter());
        logger.addHandler(handler);
    }

    private String weaponPrefix(Weapon weapon){
        return String.format("%-18s %-8s",weapon.getClass().getSimpleName(), " ["+weapon.getId()+"] ");
    }

    private <T extends Weapon> void createLogFile(T weapon){
        try {
            String name = weaponPrefix(weapon);
            Handler handler = new FileHandler( weapon.getClass().getSimpleName()+" ["+weapon.getId()+"]" + ".log");
            handler.setFilter( record -> record.getMessage().contains("["+weapon.getId()+"]"));
            handler.setFormatter(new WarLoggerFormatter());
            logger.addHandler(handler);

            logger.info(name + "CREATED");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    pointcut createLauncherFile(Launcher launcher) : execution (public void addLauncher(Launcher)) && args(launcher);

    before(Launcher launcher): createLauncherFile(launcher){
        createLogFile(launcher);
    }

    pointcut createMissileDestructorFile(MissileDestructor missileDestructor) : execution(* addMissileDestructor(MissileDestructor)) && args(missileDestructor);

    before(MissileDestructor missileDestructor): createMissileDestructorFile(missileDestructor){
        createLogFile(missileDestructor);
    }

    pointcut createLauncherDestructorFile(LauncherDestructor launcherDestructor) : execution(* addLauncherDestructor(LauncherDestructor)) && args(launcherDestructor);

    before(LauncherDestructor launcherDestructor): createLauncherDestructorFile(launcherDestructor){
        createLogFile(launcherDestructor);
    }

    pointcut launchMissileLog(Launcher launcher, Destination destination, double potentialDamage, long maxFlightTime):
            execution(public boolean launchMissile(Launcher, Destination, double, long))
            && args(launcher, destination, potentialDamage, maxFlightTime) ;


    before(Launcher launcher, Destination destination, double potentialDamage, long maxFlightTime):
            launchMissileLog(launcher, destination, potentialDamage, maxFlightTime){
        if(!launcher.isDestructed()) {
            String logMessage = weaponPrefix(launcher) + "Launched a missile at: " + WarController.getInstance().getTime() + ", Destination:" + destination;
            logger.info(logMessage);
        }

    }

    after(Launcher launcher, Destination destination, double potentialDamage, long maxFlightTime) returning (boolean hit) :
            launchMissileLog(launcher, destination, potentialDamage, maxFlightTime){
        String logMessage = weaponPrefix(launcher) + "";

        if(!launcher.isDestructed()) {
            if (hit)
                logMessage += "Missile Hit! damage: " + potentialDamage;
            else
                logMessage += "Missile Destructed!";

            logMessage += " at: " + WarController.getInstance().getTime();

            logger.info(logMessage);
        }
    }


    pointcut destructMissileLog(MissileDestructor missileDestructor, Missile missile, long time):
            execution (boolean destructMissile(MissileDestructor, Missile, long))
                    && args(missileDestructor, missile, time);

    before(MissileDestructor missileDestructor, Missile missile, long time):
            destructMissileLog(missileDestructor, missile, time){
        String logMessage = weaponPrefix(missileDestructor)+ "Try to destruct missile: " + missile.getId();
        logMessage += " at: " + WarController.getInstance().getTime();
        logger.info(logMessage);
    }

    after(MissileDestructor missileDestructor, Missile missile, long time) returning (boolean destructed):
            destructMissileLog(missileDestructor, missile, time){
        String logMessage = weaponPrefix(missileDestructor) + "Destruction of missile " + missile.getId() +": ";

        if(destructed)
            logMessage += "Succeeded!";
        else
            logMessage += "Missed!";

        logger.info(logMessage);
    }

    pointcut destructLauncherLog(LauncherDestructor launcherDestructor, Launcher launcher, int time):
            execution (boolean destructLauncher(LauncherDestructor, Launcher, int))
                    && args(launcherDestructor, launcher, time);

    before(LauncherDestructor launcherDestructor, Launcher launcher, int time):
            destructLauncherLog(launcherDestructor, launcher, time){
        String logMessage = weaponPrefix(launcherDestructor) + "Trying to destruct launcher " + launcher.getId();

        logMessage += " at: " + WarController.getInstance().getTime();
        logger.info(logMessage);
    }

    after(LauncherDestructor launcherDestructor, Launcher launcher, int time) returning (boolean destructed):
            destructLauncherLog(launcherDestructor, launcher, time){
        String logMessage = weaponPrefix(launcherDestructor) + "Destruction of launcher " + launcher.getId()+": ";

        if(destructed)
            logMessage += "Succeeded!";
        else
            logMessage += "Missed!";

        logMessage += " at: " + WarController.getInstance().getTime();
        logger.info(logMessage);
    }
}
