package Server.War.BusinessLogic.controllers;

import Entities.MissileDestructor;

public class MissileDestructorController extends DestructorController<MissileController> {
    //private MissileDestructor missileDestructor;

    public MissileDestructorController(MissileDestructor missileDestructor){
        super(missileDestructor);
    }

    //Noa: there is no need to override this method
/*    @Override
    public boolean destruct(MissileController destructibleController, long destructTime) {
        boolean succeed = destructibleController.destruct();

        //the target Missile is stored in the Destructor Entity anyway
        getDestructor().addDestructedWeapon(
                destructibleController.getMissile(),destructTime);

        if(succeed)
            getDestructor().destruct(destructibleController.getMissile(), destructTime);

        return succeed;
    }*/
}