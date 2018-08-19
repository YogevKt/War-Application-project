package Server.DAL;

import Entities.Launcher;
import Entities.LauncherDestructor;
import Entities.MissileDestructor;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MongoServices implements DBServicesInterface {
    private MongoClient mongoClient;
    private MongoDatabase databaseMongo;


    private MongoServices() {
        mongoClient = new MongoClient("localhost", 27017);
        databaseMongo = mongoClient.getDatabase("war");
    }


    @Override
    public void saveMissileLauncher(Launcher launcher) {
        MongoCollection<Document> collection = databaseMongo.getCollection(LAUNCHERS_TABLE);
        Document document = new Document("id", launcher.getId())
                .append("isHidden",launcher.isHidden());
        collection.insertOne(document);
    }

    @Override
    public void saveMissileDestructor(MissileDestructor missileDestructor) {
        MongoCollection<Document> collection = databaseMongo.getCollection(MISSILE_DESTRUCTORS_TABLE);
        Document document = new Document("id", missileDestructor.getId());
        collection.insertOne(document);
    }

    @Override
    public void saveLauncherDestructor(LauncherDestructor launcherDestructor) {
        MongoCollection<Document> collection = databaseMongo.getCollection(LAUNCHER_DESTRUCTORS_TABLE);
        Document document = new Document("id", launcherDestructor.getId())
                .append("type",launcherDestructor.getType().toString());
        collection.insertOne(document);
    }
    
    @Override
    public void saveLaunchingMissileDetails(String launcherId, String missileId, String destinationId, LocalDateTime time) {
        MongoCollection<Document> collection = databaseMongo.getCollection(LAUNCHING_MISSILE_DETAILS);
        Document document = new Document("luncherId", launcherId)
                .append("missileId",missileId)
                .append("targetId",destinationId)
                .append("time",time.format(FORMATTER));
        collection.insertOne(document);
    }

    @Override
    public void saveHitMissileDetails(String launcherId, String missileId, String destinationId, boolean isHit, double damage, LocalDateTime time) {
        MongoCollection<Document> collection = databaseMongo.getCollection(HIT_MISSILE_DETAILS);
        Document document = new Document("luncherId", launcherId)
                .append("missileId",missileId)
                .append("targetId",destinationId)
                .append("isHit",isHit)
                .append("damage",damage)
                .append("time",time.format(FORMATTER));
        collection.insertOne(document);
    }

    @Override
    public void saveLaunchingMissileDestructorDetails(String missileLauncherDestructorId, String missileId, String missileTargetId, LocalDateTime time) {
        MongoCollection<Document> collection = databaseMongo.getCollection(LAUNCHING_MISSILE_DESTRUCTOR_DETAILS);
        Document document = new Document("missileLauncherDestructorId", missileLauncherDestructorId)
                .append("missileId",missileId)
                .append("missileTargetId",missileTargetId)
                .append("time",time.format(FORMATTER));
        collection.insertOne(document);
    }

    @Override
    public void saveHitMissileDestructorDetails(String missileLauncherDestructorId, String missileId, String missileTargetId, boolean hit, LocalDateTime time) {
        MongoCollection<Document> collection = databaseMongo.getCollection(HIT_MISSILE_DESTRUCTOR_DETAILS);
        Document document = new Document("missileLauncherDestructorId", missileLauncherDestructorId)
                .append("missileId",missileId)
                .append("missileTargetId",missileTargetId)
                .append("hit", hit)
                .append("time",time.format(FORMATTER));
        collection.insertOne(document);
    }

    @Override
    public void saveDestructingLauncherDetails(String destructLauncherId, String launcher, LocalDateTime time) {
        MongoCollection<Document> collection = databaseMongo.getCollection(DESTRUCTING_LAUNCHER_DETAILS);
        Document document = new Document("destructLauncherId", destructLauncherId)
                .append("launcher",launcher)
                .append("time",time.format(FORMATTER));
        collection.insertOne(document);
    }

    @Override
    public void saveHitDestructingLauncherDetails(String destructLauncherId, String launcher, boolean hit, LocalDateTime time) {
        MongoCollection<Document> collection = databaseMongo.getCollection(HIT_DESTRUCTING_LAUNCHER_DETAILS);
        Document document = new Document("destructLauncherId", destructLauncherId)
                .append("launcher",launcher)
                .append("hit", hit)
                .append("time",time.format(FORMATTER));
        collection.insertOne(document);
    }
    
    @Override
    public void dropTables() {
        databaseMongo.drop();
        exit();
    }


	@Override
	public void exit() {
		mongoClient.close();
	}
    
    

}
