package Server.War.BusinessLogic.controllers;

import Entities.Missile;

import java.util.concurrent.TimeUnit;

public class MissileController extends DestructibleController<Missile>{
    private long maxFlightTime;

    private LauncherController launcher;

    public MissileController(Missile missile, long maxFlightTime, LauncherController launcher) {
        super(missile);
        setMaxFlightTime(maxFlightTime);
        setLauncher(launcher);
    }

    public long getMaxFlightTime() {
        return maxFlightTime;
    }

    public Missile getMissile(){
        return getDestructibleWeapon();
    }

    public LauncherController getLauncher() {
        return launcher;
    }

    private void setMaxFlightTime(long maxFlightTime) {
        if(maxFlightTime <= 0)
            throw new IllegalArgumentException("Illegal flight time: " + maxFlightTime + ", max flight time must be positive!");

        this.maxFlightTime = maxFlightTime;
    }

    public void setLauncher(LauncherController launcher) {
        this.launcher = launcher;
    }

    public boolean isDestructed(){
        return getDestructibleWeapon().isDestructed();
    }

    @Override
    public boolean destruct() {
        if(getLock().tryLock()) {
            getMissile().destruct();
            //stop MissileController await()
            getCondition().signalAll();
            getLock().unlock();

            return true;
        }
        getCondition().signalAll();
        return false;
    }

    @Override
    public Boolean call() throws Exception {
        boolean succeed = false;
        getLock().lock();
        try {
            boolean timeout = !getCondition().await(maxFlightTime, TimeUnit.SECONDS);
            if(timeout){
                succeed = true;
            }else {
                getDestructibleWeapon().destruct();
            }
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }finally {
            getLock().unlock();
            synchronized (launcher)
            {
                launcher.notifyAll();
            }
        }
        return succeed;
    }
}