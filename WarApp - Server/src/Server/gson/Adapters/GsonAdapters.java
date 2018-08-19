package Server.gson.Adapters;

import Entities.Destination;
import Entities.LauncherDestructor;
import Entities.Missile;

public class GsonAdapters {
    public Entities.Missile adaptMissile(Server.gson.entities.Missile gMissile){
        return new Missile(gMissile.getId(),
                            gMissile.getDamage(),
                            new Destination(gMissile.getDestination()),
                            gMissile.getFlyTime(),
                            gMissile.getLaunchTime());
    }

    public Entities.Launcher adaptLauncher(Server.gson.entities.Launcher gLauncher){
        if(gLauncher.isHidden()){
            return new Entities.HideableLauncher(gLauncher.getId());
        }
        else{
            return new Entities.Launcher(gLauncher.getId());
        }
    }

    public Entities.MissileDestructor adaptMissileDestructor(Server.gson.entities.Destructor gMissileDestructor){
        return new Entities.MissileDestructor(gMissileDestructor.getId());
    }

    public Entities.LauncherDestructor adaptLauncherDestructor(Server.gson.entities.Destructor_ glauncherDestructor){
        Entities.LauncherDestructor.DestructorType type;
        switch (glauncherDestructor.getType()) {
            case "plane":
                type = LauncherDestructor.DestructorType.PLANE;
                break;
            case "shipA":
                type = LauncherDestructor.DestructorType.SHIP;
                break;
            default:
                type = null;
                break;
        }
        return new Entities.LauncherDestructor(type);
    }
}
