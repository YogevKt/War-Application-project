package Server.War.DAL;

import Entities.Launcher;
import Entities.LauncherDestructor;
import Entities.Missile;
import Entities.MissileDestructor;

public class WarDaoImpl implements WarDao {
    private WeaponDao<Launcher> launcherDao;
    private WeaponDao<Missile> missileDao;

    @Override
    public void saveLauncher(Launcher launcher) {
        launcherDao.save(launcher);
    }

    @Override
    public void saveLauncherDestructor(LauncherDestructor launcherDestructor) {

    }

    @Override
    public void saveMissileDestructor(MissileDestructor missileDestructor) {

    }

    @Override
    public void saveMissile(Missile missile) {
        missileDao.save(missile);
    }
}
