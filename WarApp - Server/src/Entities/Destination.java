package Entities;

import java.io.Serializable;

public class Destination implements Serializable {
    private static final long serialVersionUID = 1L;

    private String dest;

    public Destination(String dest){
        setDest(dest);
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    @Override
    public String toString() {
        return dest;
    }
}
