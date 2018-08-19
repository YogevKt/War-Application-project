
package Server.gson.entities;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MissileLauncherDestructors {

    @SerializedName("destructor")
    @Expose
    private List<Destructor_> destructor = null;

    public List<Destructor_> getDestructor() {
        return destructor;
    }

    public void setDestructor(List<Destructor_> destructor) {
        this.destructor = destructor;
    }

}
