package Server.DAL;

import Entities.Launcher;
import Entities.LauncherDestructor;
import Entities.MissileDestructor;

import java.sql.*;
import java.time.LocalDateTime;

public class SQLServices implements DBServicesInterface {
	private Connection connection;
	private String URL = "jdbc:mysql://localhost/war?user=root&password=root123";

	private SQLServices() {
		createConnection();
	}

	public void createConnection() {
		try {
			connection = DriverManager.getConnection(URL);
			createTables();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createTables() {

		String launchersTable, missileDestructorTable, destructLauncherTable;
		String launchingMissileDetails, hitMissileDetails;
		String launchingMissileDestructorDetails, hitMissileDestructorDetails;
		String destructingLauncherDetails, hitDestructingLauncherDetails;

		launchersTable = "CREATE TABLE " + LAUNCHERS_TABLE + " (id INT not NULL AUTO_INCREMENT,"
				+ " idLauncher VARCHAR(255) not NULL, " + " isHidden VARCHAR(255), " + " PRIMARY KEY ( id ))";

		missileDestructorTable = "CREATE TABLE " + MISSILE_DESTRUCTORS_TABLE + " (id INT not NULL AUTO_INCREMENT,"
				+ " idMissileDestructor VARCHAR(255) not NULL, " + " PRIMARY KEY ( id ))";

		destructLauncherTable = "CREATE TABLE " + LAUNCHER_DESTRUCTORS_TABLE + " (id INT not NULL AUTO_INCREMENT,"
				+ " idLauncherDestructor VARCHAR(255) not NULL, " + " type VARCHAR(255), " + " PRIMARY KEY ( id ))";

		launchingMissileDetails = "CREATE TABLE " + LAUNCHING_MISSILE_DETAILS + " (id INT not NULL AUTO_INCREMENT,"
				+ " launcherId VARCHAR(255) not NULL, " + " missileId VARCHAR(255) not NULL, "
				+ " destinationId VARCHAR(255) not NULL, " + " time DATE , " + " PRIMARY KEY ( id ))";

		hitMissileDetails = "CREATE TABLE " + HIT_MISSILE_DETAILS + " (id INT not NULL AUTO_INCREMENT,"
				+ " launcherId VARCHAR(255) not NULL, " + " missileId VARCHAR(255) not NULL, "
				+ " destinationId VARCHAR(255) not NULL, " + " hit VARCHAR(255), " + " damage DOUBLE , "
				+ " time VARCHAR(255), " + " PRIMARY KEY ( id ))";

		launchingMissileDestructorDetails = "CREATE TABLE " + LAUNCHING_MISSILE_DESTRUCTOR_DETAILS
				+ " (id INT not NULL AUTO_INCREMENT," + " missileLauncherDestructorId VARCHAR(255) not NULL, "
				+ " missileId VARCHAR(255) not NULL, " + " missileTargetId VARCHAR(255) not NULL, "
				+ " time VARCHAR(255), " + " PRIMARY KEY ( id ))";

		hitMissileDestructorDetails = "CREATE TABLE " + HIT_MISSILE_DESTRUCTOR_DETAILS
				+ " (id INT not NULL AUTO_INCREMENT," + " missileLauncherDestructorId VARCHAR(255) not NULL, "
				+ " missileId VARCHAR(255) not NULL, " + " missileTargetId VARCHAR(255) not NULL, "
				+ " hit VARCHAR(255), " + " time VARCHAR(255), " + " PRIMARY KEY ( id ))";

		destructingLauncherDetails = "CREATE TABLE " + DESTRUCTING_LAUNCHER_DETAILS
				+ " (id INT not NULL AUTO_INCREMENT," + " destructLauncherId VARCHAR(255) not NULL, "
				+ " launcher VARCHAR(255) not NULL, " + " time VARCHAR(255), " + " PRIMARY KEY ( id ))";

		hitDestructingLauncherDetails = "CREATE TABLE " + HIT_DESTRUCTING_LAUNCHER_DETAILS
				+ " (id INT not NULL AUTO_INCREMENT," + "destructLauncherId VARCHAR(255) not NULL, "
				+ " launcher VARCHAR(255) not NULL, " + " hit VARCHAR(255), " + " time VARCHAR(255), "
				+ " PRIMARY KEY ( id ))";

		try {
			PreparedStatement preparedStatement = connection.prepareStatement(launchersTable);
			preparedStatement.executeUpdate(launchersTable);

			preparedStatement = connection.prepareStatement(missileDestructorTable);
			preparedStatement.executeUpdate(missileDestructorTable);

			preparedStatement = connection.prepareStatement(destructLauncherTable);
			preparedStatement.executeUpdate(destructLauncherTable);

			preparedStatement = connection.prepareStatement(launchingMissileDetails);
			preparedStatement.executeUpdate(launchingMissileDetails);

			preparedStatement = connection.prepareStatement(hitMissileDetails);
			preparedStatement.executeUpdate(hitMissileDetails, Statement.RETURN_GENERATED_KEYS);

			preparedStatement = connection.prepareStatement(launchingMissileDestructorDetails);
			preparedStatement.executeUpdate(launchingMissileDestructorDetails);

			preparedStatement = connection.prepareStatement(hitMissileDestructorDetails);
			preparedStatement.executeUpdate(hitMissileDestructorDetails, Statement.RETURN_GENERATED_KEYS);

			preparedStatement = connection.prepareStatement(destructingLauncherDetails);
			preparedStatement.executeUpdate(destructingLauncherDetails);

			preparedStatement = connection.prepareStatement(hitDestructingLauncherDetails);
			preparedStatement.executeUpdate(hitDestructingLauncherDetails, Statement.RETURN_GENERATED_KEYS);

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void saveMissileLauncher(Launcher launcher) {
		try {
			synchronized (connection) {
				if (!connection.isClosed()) {
					PreparedStatement preparedStatement = connection.prepareStatement(
							"INSERT INTO `" + LAUNCHERS_TABLE + "`(`idLauncher`, `isHidden`) VALUES (?,?)");
					preparedStatement.setString(1, launcher.getId());
					preparedStatement.setString(2, (launcher.isHidden() ? "true" : "false"));
					preparedStatement.executeUpdate();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveMissileDestructor(MissileDestructor missileDestructor) {
		try {
			synchronized (connection) {
				if (!connection.isClosed()) {
					PreparedStatement preparedStatement = connection.prepareStatement(
							"INSERT INTO `" + MISSILE_DESTRUCTORS_TABLE + "`(`idMissileDestructor`) VALUES (?)");
					preparedStatement.setString(1, missileDestructor.getId());
					preparedStatement.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveLauncherDestructor(LauncherDestructor launcherDestructor) {
		try {
			synchronized (connection) {
				if (!connection.isClosed()) {
					PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `"
							+ LAUNCHER_DESTRUCTORS_TABLE + "`(`idLauncherDestructor`,`type`) VALUES (?,?)");
					preparedStatement.setString(1, launcherDestructor.getId());
					preparedStatement.setString(2, launcherDestructor.getType().toString());
					preparedStatement.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveLaunchingMissileDetails(String launcherId, String missileId, String destinationId,
			LocalDateTime time) {
		try {
			synchronized (connection) {
				if (!connection.isClosed()) {
					PreparedStatement preparedStatement = connection
							.prepareStatement("INSERT INTO `" + LAUNCHING_MISSILE_DETAILS
									+ "`(`launcherId`, `missileId`, `destinationId`, `time`) VALUES (?,?,?,?)");
					preparedStatement.setString(1, launcherId);
					preparedStatement.setString(2, missileId);
					preparedStatement.setString(3, destinationId);
					preparedStatement.setString(4, time.format(FORMATTER));
					preparedStatement.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveHitMissileDetails(String launcherId, String missileId, String destinationId, boolean hit,
			double damage, LocalDateTime time) {
		try {
			synchronized (connection) {
				if (!connection.isClosed()) {
					PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `"
							+ HIT_MISSILE_DETAILS
							+ "`(`launcherId`, `missileId`, `destinationId`, `hit`, `damage`, `time`) VALUES (?,?,?,?,?,?)");
					preparedStatement.setString(1, launcherId);
					preparedStatement.setString(2, missileId);
					preparedStatement.setString(3, destinationId);
					preparedStatement.setString(4, (hit ? "true" : "false"));
					preparedStatement.setDouble(5, damage);
					preparedStatement.setString(6, time.format(FORMATTER));
					preparedStatement.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveLaunchingMissileDestructorDetails(String missileLauncherDestructorId, String missileId,
			String missileTargetId, LocalDateTime time) {
		try {
			synchronized (connection) {
				if (!connection.isClosed()) {
					PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `"
							+ LAUNCHING_MISSILE_DESTRUCTOR_DETAILS
							+ "`(`missileLauncherDestructorId`, `missileId`, `missileTargetId`, `time`) VALUES (?,?,?,?)");
					preparedStatement.setString(1, missileLauncherDestructorId);
					preparedStatement.setString(2, missileId);
					preparedStatement.setString(3, missileTargetId);
					preparedStatement.setString(4, time.format(FORMATTER));
					preparedStatement.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveHitMissileDestructorDetails(String missileLauncherDestructorId, String missileId,
			String missileTargetId, boolean hit, LocalDateTime time) {
		try {
			synchronized (connection) {
				if (!connection.isClosed()) {
					PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO `"
							+ HIT_MISSILE_DESTRUCTOR_DETAILS
							+ "`(`missileLauncherDestructorId`, `missileId`, `missileTargetId`, `hit`, `time`) VALUES (?,?,?,?,?)");
					preparedStatement.setString(1, missileLauncherDestructorId);
					preparedStatement.setString(2, missileId);
					preparedStatement.setString(3, missileTargetId);
					preparedStatement.setString(4, (hit ? "true" : "false"));
					preparedStatement.setString(5, time.format(FORMATTER));
					preparedStatement.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveDestructingLauncherDetails(String destructLauncherId, String launcher, LocalDateTime time) {
		try {
			synchronized (connection) {
				if (!connection.isClosed()) {
					PreparedStatement preparedStatement = connection
							.prepareStatement("INSERT INTO `" + DESTRUCTING_LAUNCHER_DETAILS
									+ "`(`destructLauncherId`, `launcher`, `time`) VALUES (?,?,?)");
					preparedStatement.setString(1, destructLauncherId);
					preparedStatement.setString(2, launcher);
					preparedStatement.setString(3, time.format(FORMATTER));
					preparedStatement.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveHitDestructingLauncherDetails(String destructLauncherId, String launcher, boolean hit,
			LocalDateTime time) {
		try {
			synchronized (connection) {
				if (!connection.isClosed()) {
					PreparedStatement preparedStatement = connection
							.prepareStatement("INSERT INTO `" + HIT_DESTRUCTING_LAUNCHER_DETAILS
									+ "`(`destructLauncherId`, `launcher`, `hit`, `time`) VALUES (?,?,?,?)");
					preparedStatement.setString(1, destructLauncherId);
					preparedStatement.setString(2, launcher);
					preparedStatement.setString(3, (hit ? "true" : "false"));
					preparedStatement.setString(4, time.format(FORMATTER));
					preparedStatement.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void dropTables() {
		String sql = "DROP TABLE IF EXISTS " + LAUNCHERS_TABLE + "," + MISSILE_DESTRUCTORS_TABLE + ","
				+ LAUNCHER_DESTRUCTORS_TABLE + "," + LAUNCHING_MISSILE_DETAILS + "," + HIT_MISSILE_DETAILS + ","
				+ LAUNCHING_MISSILE_DESTRUCTOR_DETAILS + "," + HIT_MISSILE_DESTRUCTOR_DETAILS + ","
				+ DESTRUCTING_LAUNCHER_DETAILS + "," + HIT_DESTRUCTING_LAUNCHER_DETAILS;
		try {
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.executeUpdate(sql);
			exit();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void exit() {
		try {
			synchronized (connection) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
