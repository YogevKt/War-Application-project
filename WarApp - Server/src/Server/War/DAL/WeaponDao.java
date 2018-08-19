package Server.War.DAL;

import Entities.Weapon;

public interface WeaponDao <T extends Weapon> {
    void save(T t);

    T load(String id);

    void update(T t);
}
