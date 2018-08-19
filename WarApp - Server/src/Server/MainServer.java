package Server;

import Entities.*;
import Server.DAL.DBController;
import Server.DAL.DBController.DBtype;
import Server.Messaging.MessageController;
import Server.War.BusinessLogic.WarController;
import Server.gson.GsonReader;
import Server.gson.JsonHandler;
import Server.gson.JsonReaderFacade;
import com.google.gson.Gson;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.*;

public class MainServer {
    private static ObjectOutputStream out = null;
    private static ObjectInputStream in = null;

    public static void main(String[] args) {

        try {
            final ServerSocket serverSocket = new ServerSocket(9898);
            System.out.println("Server waits for clients...");

            while(true){
                final Socket socket = serverSocket.accept();

                //For every client a new thread is created
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MessagePackage messagePackage;
                        MessagePackage.Choice choice;
                        String clientAddress = "";
                        Gson gson = new Gson();

                       

                        try{
                            clientAddress = socket.getInetAddress() + ":" +socket.getPort();
                            System.out.println("client connected to server from: "+clientAddress);
                            out = new ObjectOutputStream(socket.getOutputStream());
                            in = new ObjectInputStream(socket.getInputStream());
                            MessageController.getInstance();

                            do {      
                                messagePackage = ((MessagePackage)in.readObject());
                                choice = messagePackage.getChoice();
                                menu(choice, messagePackage.getParameters(), out, gson);

                            }while (choice != MessagePackage.Choice.EXIT);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } finally {
                            //Close the connection
                            try {
                                in.close();
                                out.close();
                                socket.close();
                                System.out.println("Connection with "+ clientAddress+" was close");

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                System.out.println("Server waits for another clients");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void menu(MessagePackage.Choice choice, ArrayList<Object> messageParams,
                             ObjectOutputStream out, Gson gson) throws ExecutionException, InterruptedException {

        switch (choice){
		    case SQL: {
		        DBController.getInstance().setDbServicesInterface(DBtype.SQL);
		        break;
		    }
		    case MONGODB: {
		        DBController.getInstance().setDbServicesInterface(DBtype.MongoDB);
		        break;
		    }
            case JSON: {
                JsonReaderFacade jsonReader = new GsonReader();
                JsonHandler jsonHandler = new JsonHandler(jsonReader.readJson());
                Executors.newSingleThreadExecutor().execute(jsonHandler);
                break;
            }

            case START_TIME: {
                WarController.startTime();
                break;
            }

            case ADD_MISSILE_DESTRUCTOR: {
                MissileDestructor missileDestructor = (MissileDestructor)messageParams.get(0);
                WarController.getInstance().addMissileDestructor(missileDestructor);
                break;
            }

            case ADD_LAUNCHER: {
                Launcher launcher = (Launcher)messageParams.get(0);
                WarController.getInstance().addLauncher(launcher);
                break;
            }

            case ADD_LAUNCHER_DESTRUCTOR: {
                LauncherDestructor launcherDestructor = (LauncherDestructor)messageParams.get(0);
                WarController.getInstance().addLauncherDestructor(launcherDestructor);
                break;
            }

            case GET_MISSILE_DESTRUCTORS: {
                MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.GET_MISSILE_DESTRUCTORS);

                ArrayList<MissileDestructor> arr = new ArrayList<>();
                for(MissileDestructor md : WarController.getInstance().retrieveMissileDestructors())
                    arr.add(md);
                messagePackage.addParameter(arr);

                sendMessage(out,messagePackage);
                break;
            }

            case GET_MISSILE_LAUNCHERS: {
                MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.GET_MISSILE_LAUNCHERS);

                ArrayList<Launcher> arr = new ArrayList<>();
                for(Launcher l : WarController.getInstance().retrieveLaunchers())
                    arr.add(l);
                messagePackage.addParameter(arr);

                sendMessage(out,messagePackage);
                break;
            }

            case GET_TIME: {
                MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.GET_TIME);
                messagePackage.addParameter(WarController.getInstance().getTime());
                sendMessage(out,messagePackage);
                break;
            }

            case GET_ACTIVE_MISSILES: {
                MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.GET_ACTIVE_MISSILES);

                ArrayList<Missile> arr = new ArrayList<>();
                for(Missile m : WarController.getInstance().retrieveActiveMissiles())
                    arr.add(m);
                messagePackage.addParameter(arr);

                sendMessage(out,messagePackage);
                break;
            }

            case GET_LAUNCHER_DESTRUCTORS: {
                MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.GET_LAUNCHER_DESTRUCTORS);

                ArrayList<LauncherDestructor> arr = new ArrayList<>();
                for(LauncherDestructor ld : WarController.getInstance().retrieveLauncherDestructors())
                    arr.add(ld);
                messagePackage.addParameter(arr);

                sendMessage(out,messagePackage);
                break;
            }

            case LAUNCH_MISSILE: {
                Launcher launcher = (Launcher)messageParams.get(0);
                Destination destination;
                double potentialDamage;
                long maxFlightTime;
                Missile missile;

                ExecutorService executor = Executors.newFixedThreadPool(1);


                switch (messageParams.size()){
                    case 2:
                        missile = (Missile)messageParams.get(1);
                       executor.submit(new Callable<Boolean>() {
                            @Override
                            public Boolean call() {
                                return WarController.getInstance().launchMissile(launcher, missile);
                            }
                        });
                        break;
                    case 3:
                        destination = (Destination)messageParams.get(1);
                        potentialDamage = (double)messageParams.get(2);
                        executor.submit(new Callable<Boolean>() {
                            @Override
                            public Boolean call() {
                                try {
                                    return WarController.getInstance().launchMissile(launcher, destination,potentialDamage);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        });
                        break;
                    case 4:
                        destination = (Destination)messageParams.get(1);
                        potentialDamage = (double)messageParams.get(2);
                        maxFlightTime = (long)messageParams.get(3);
                        executor.submit(new Callable<Boolean>() {
                            @Override
                            public Boolean call() {
                                try {
                                    return WarController.getInstance().launchMissile(launcher, destination, potentialDamage, maxFlightTime);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        });
                        break;
                }
                break;
            }

            case DESTRUCT_LAUNCHER_DESTRUCTOR:
                break;

            case DESTRUCT_MISSILE: {
                MissileDestructor missileDestructor = (MissileDestructor)messageParams.get(0);
                Missile missile = (Missile)messageParams.get(1);

                ExecutorService executor = Executors.newFixedThreadPool(1);
                Future<Boolean> future = null;

                switch (messageParams.size()){
                    case 2:
                        future = executor.submit(new Callable<Boolean>() {
                            @Override
                            public Boolean call() {
                                return WarController.getInstance().destructMissile(missileDestructor, missile);
                            }
                        });
                        break;
                    case 3:
                        long time = (long)messageParams.get(2);
                        future = executor.submit(new Callable<Boolean>() {
                            @Override
                            public Boolean call() {
                                return WarController.getInstance().destructMissile(missileDestructor, missile, time);
                            }
                        });
                        break;
                }
                break;
            }

            case DESTRUCT_LAUNCHER: {
                LauncherDestructor launcherDestructor  = (LauncherDestructor)messageParams.get(0);
                Launcher launcher  = (Launcher)messageParams.get(1);

                ExecutorService executor = Executors.newFixedThreadPool(1);
                Future<Boolean> future = null;

                switch (messageParams.size()){
                    case 2:
                        future = executor.submit(new Callable<Boolean>() {
                            @Override
                            public Boolean call() {
                                try {
                                    return WarController.getInstance().destructLauncher(launcherDestructor, launcher);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        });
                        break;
                    case 3:
                        int time = (int)messageParams.get(2);
                        future = executor.submit(new Callable<Boolean>() {
                            @Override
                            public Boolean call() {
                                try {
                                    return WarController.getInstance().destructLauncher(launcherDestructor, launcher, time);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                return false;
                            }
                        });
                        break;
                }
                break;
            }

            case SHOW_STATS: {
                MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.SHOW_STATS);
                messagePackage.addParameter(WarController.getInstance().getStatistics());
                sendMessage(out,messagePackage);
                break;
            }

            case EXIT:{
            	WarController.getInstance().exit();
                MessageController.getInstance().closeConnection();
                break;
            }
        }
    }

    private static void sendMessage(ObjectOutputStream out, MessagePackage msg) {
        try{
            out.writeObject(msg);
            out.flush();
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

}

