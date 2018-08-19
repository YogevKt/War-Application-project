package Entities;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Destructible extends Weapon implements Serializable {
    private static final long serialVersionUID = 1L;

    private AtomicBoolean isDestructed = new AtomicBoolean(false);

    public Destructible(String id) {
        super(id);
    }

    public boolean isDestructed() {
        return isDestructed.get();
    }

    public void destruct() {
        isDestructed.set(true);
    }

    @Override
    public String toString() {
        return String.format("%s Destructed: %4s",super.toString(), isDestructed());
    }
}
