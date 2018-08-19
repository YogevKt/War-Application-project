
package Server.gson.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MissileDestructors {

    @SerializedName("destructor")
    @Expose
    private List<Destructor> destructor = null;

    public List<Destructor> getDestructor() {
        return destructor;
    }

    public void setDestructor(List<Destructor> destructor) {
        this.destructor = destructor;
    }

}
