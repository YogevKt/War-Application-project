package Server.War.BusinessLogic.controllers;

import Entities.Destructor;

public abstract class DestructorController<T extends DestructibleController> {

    private Destructor destructor;

    public DestructorController(Destructor destructor){
        setDestructor(destructor);
    }

    public Destructor getDestructor() {
        return destructor;
    }

    public void setDestructor(Destructor destructor) {
        this.destructor = destructor;
    }

    public boolean destruct(T destructibleController, long time){
        boolean succeed = destructibleController.destruct();

        //the target DestructibleWeapon is stored in the Destructor Entity anyway
        getDestructor().addDestructedWeapon(
                destructibleController.getDestructibleWeapon(),time);

        if(succeed)
            destructor.destruct(destructibleController.getDestructibleWeapon());

        return succeed;
    }

}
