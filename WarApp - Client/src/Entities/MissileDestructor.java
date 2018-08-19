package Entities;

import java.io.Serializable;
import java.util.HashMap;

public class MissileDestructor extends Destructor<Missile> implements Serializable {
    private static final long serialVersionUID = 1L;

    public MissileDestructor(){
        super();
    }

    public MissileDestructor(String id){
        super(id);
    }

    private HashMap<Missile, Long> destructedMissiles = new HashMap<>();

}
