package Server.War.BusinessLogic.controllers;

import Entities.LauncherDestructor;

public class LauncherDestructorController extends DestructorController<LauncherController> {
    private LauncherDestructor launcherDestructor;

    public LauncherDestructorController(LauncherDestructor launcherDestructor){
        super(launcherDestructor);
    }

/*

    @Override
    public boolean destruct(LauncherController destructibleController, long destructTime) {
        boolean succeed = destructibleController.destruct();

        if(succeed)
            launcherDestructor.destruct(destructibleController.getLauncher(), destructTime);
            return succeed;
    }
*/
}
