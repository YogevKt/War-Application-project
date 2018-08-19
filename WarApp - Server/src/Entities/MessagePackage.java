package Entities;

import java.io.Serializable;
import java.util.ArrayList;

public class MessagePackage implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Choice {JSON, ADD_LAUNCHER, ADD_LAUNCHER_DESTRUCTOR, ADD_MISSILE_DESTRUCTOR, LAUNCH_MISSILE,
        DESTRUCT_LAUNCHER_DESTRUCTOR, DESTRUCT_MISSILE, DESTRUCT_LAUNCHER, SHOW_STATS, GET_MISSILE_LAUNCHERS,GET_MISSILE_DESTRUCTORS,GET_LAUNCHER_DESTRUCTORS
        ,GET_ACTIVE_MISSILES,GET_TIME,START_TIME, EXIT, MISSILE_HIT, LAUNCHER_DESTRUCTED_HIT,
        LAUNCHER_WAS_HIDDEN,SQL,MONGODB, END_OF_WAR}

    private Choice choice;

    private ArrayList<Object> parameters;

    public MessagePackage(Choice choice) {
        this.choice = choice;
        parameters = new ArrayList<>();

    }

    public void addParameter(Object obj){
        parameters.add(obj);
    }

    public ArrayList<Object> getParamsObjects() {
        return parameters;
    }

    public Choice getChoice() {
        return choice;
    }

    public void setChoice(Choice choice) {
        this.choice = choice;
    }

    public ArrayList<Object> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<Object> parameters) {
        this.parameters = parameters;
    }

}

