package Client.View.EntitiesViews;

import Entities.Launcher;


public class LauncherView extends WeaponView<Launcher> {
    private static final String IMAGE_PATH = "Client/View/images/launcher 120.png";
    public LauncherView(Launcher weapon) {
        super(weapon, IMAGE_PATH);
        if(weapon.isHidden())
            setOpacity(0.5);
    }
}
