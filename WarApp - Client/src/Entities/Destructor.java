package Entities;

import java.io.Serializable;
import java.util.HashMap;

public abstract class Destructor <T extends Destructible> extends Weapon implements Serializable {
    private static final long serialVersionUID = 1L;

    private static int idGenerator = 201;
    private HashMap<T, Long> destructedWeapons;// key: ID, value: time

    public Destructor(){
        super("D" + idGenerator++);
        destructedWeapons = new HashMap<>();
    }

    public Destructor(String id){
        super(id);
        destructedWeapons = new HashMap<>();
    }
    public void destruct(T destructibleWeapon){
        destructibleWeapon.destruct();
    }

    public void addDestructedWeapon(T destructedWeapon, long time){
        destructedWeapons.put(destructedWeapon, time);
    }

    @Override
    public String toString() {
        return String.format("%s Destructed Weapons: %2d",super.toString(), destructedWeapons.size());
    }
}
