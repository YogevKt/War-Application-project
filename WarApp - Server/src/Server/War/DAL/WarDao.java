package Server.War.DAL;

import Entities.Launcher;
import Entities.LauncherDestructor;
import Entities.Missile;
import Entities.MissileDestructor;

public interface WarDao {
    void saveLauncher(Launcher launcher);

    void saveLauncherDestructor(LauncherDestructor launcherDestructor);

    void saveMissileDestructor(MissileDestructor missileDestructor);

    void saveMissile(Missile missile);
}
