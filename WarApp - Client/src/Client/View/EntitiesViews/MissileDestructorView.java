package Client.View.EntitiesViews;

import Entities.MissileDestructor;

public class MissileDestructorView extends WeaponView<MissileDestructor> {
    private static final String IMAGE_PATH = "Client/View/images/icecreamtruck.png";
    public MissileDestructorView(MissileDestructor weapon) {
        super(weapon, IMAGE_PATH);
    }
}
