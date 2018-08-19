package Entities;

import java.io.Serializable;

public class HideableLauncher extends Launcher implements Serializable {
    private static final long serialVersionUID = 1L;

    public HideableLauncher() {
        super();
    }
    public HideableLauncher(String id) {
        super(id);
    }
    @Override
    public boolean isHidden() {
        return !isCurrentlyLaunching();
    }

    @Override
    public String toString() {
        return String.format("%s, Hideable",super.toString());
    }

}
