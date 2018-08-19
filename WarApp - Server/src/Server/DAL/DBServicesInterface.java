package Server.DAL;

import Entities.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface DBServicesInterface {

    String LAUNCHERS_TABLE =  "Launchers";
    String MISSILE_DESTRUCTORS_TABLE =  "MissileDestructors";
    String LAUNCHER_DESTRUCTORS_TABLE =  "LauncherDestructors";
    String LAUNCHING_MISSILE_DETAILS =  "LaunchingMissileDetails";
    String HIT_MISSILE_DETAILS =  "HitMissileDetails";
    String LAUNCHING_MISSILE_DESTRUCTOR_DETAILS =  "LaunchingMissileDestructorDetails";
    String HIT_MISSILE_DESTRUCTOR_DETAILS =  "HitMissileDestructorDetails";
    String DESTRUCTING_LAUNCHER_DETAILS =  "DestructingLauncherDetails";
    String HIT_DESTRUCTING_LAUNCHER_DETAILS =  "HitDestructingLauncherDetails";

    DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    void saveMissileLauncher(Launcher launcher);
    void saveMissileDestructor(MissileDestructor missileDestructor);
    void saveLauncherDestructor(LauncherDestructor launcherDestructor);

    void saveLaunchingMissileDetails(String launcherId, String missileId, String destinationId, LocalDateTime time);
    void saveHitMissileDetails(String launcherId, String missileId, String destinationId, boolean isHit, double damage, LocalDateTime time);

    void saveLaunchingMissileDestructorDetails(String missileLauncherDestructorId, String missileId, String missileTargetId, LocalDateTime time);
    void saveHitMissileDestructorDetails(String missileLauncherDestructorId, String missileId, String missileTargetId, boolean hit, LocalDateTime time);


    void saveDestructingLauncherDetails(String destructLauncherId,String launcher, LocalDateTime time);
    void saveHitDestructingLauncherDetails(String destructLauncherId,String launcher, boolean hit, LocalDateTime time);

    void dropTables();
    void exit();

}
