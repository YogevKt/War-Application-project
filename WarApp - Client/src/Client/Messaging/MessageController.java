package Client.Messaging;

import Client.WarObserver.WarObserver;
import Entities.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class MessageController implements WarControllerFacade {

    private Gson gson;
    private static MessageController messageController = null;
    private static Socket socket = null;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static ReceiveMessage receiveMessage;
    private static Thread threadRecieveMessage;

    private MessageController() {
        gson = new Gson();
    }

    public static MessageController getInstance() {
        if (messageController == null) {
            messageController = new MessageController();
            connectToServer();
        }
        return messageController;
    }

    public static void connectToServer() {
        if (socket == null || socket.isClosed()) {
            try {
                socket = new Socket("localhost", 9898);
                System.out.println("Connected to server: " + socket.getInetAddress().getHostAddress());
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());
                receiveMessage = new ReceiveMessage();
                threadRecieveMessage = new Thread(receiveMessage);
                threadRecieveMessage.start();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void readFromJson() {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.JSON);
        sendMessage(messagePackage);
    }

    @Override
    public void addLauncher(Launcher launcher) {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.ADD_LAUNCHER);
        messagePackage.addParameter(launcher);
        sendMessage(messagePackage);
    }

    @Override
    public void addLauncherDestructor(LauncherDestructor launcherDestructor) {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.ADD_LAUNCHER_DESTRUCTOR);
        messagePackage.addParameter(launcherDestructor);
        sendMessage(messagePackage);
    }

    @Override
    public void addMissileDestructor(MissileDestructor missileDestructor) {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.ADD_MISSILE_DESTRUCTOR);
        messagePackage.addParameter(missileDestructor);
        sendMessage(messagePackage);
    }

    @Override
    public boolean launchMissile(Launcher launcher, Destination destination, double potentialDamage) throws InterruptedException, ExecutionException {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.LAUNCH_MISSILE);
        messagePackage.addParameter(launcher);
        messagePackage.addParameter(destination);
        messagePackage.addParameter(potentialDamage);
        sendMessage(messagePackage);
        return false;
    }

    @Override
    public boolean launchMissile(Launcher launcher, Destination destination, double potentialDamage, long maxFlightTime) throws InterruptedException, ExecutionException {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.LAUNCH_MISSILE);
        messagePackage.addParameter(launcher);
        messagePackage.addParameter(destination);
        messagePackage.addParameter(potentialDamage);
        messagePackage.addParameter(maxFlightTime);
        sendMessage(messagePackage);
        return false;
    }

    @Override
    public boolean launchMissile(Launcher launcher, Missile missile) {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.LAUNCH_MISSILE);
        messagePackage.addParameter(launcher);
        messagePackage.addParameter(missile);
        sendMessage(messagePackage);
        return false;
    }

    @Override
    public boolean destructMissile(MissileDestructor missileDestructor, Missile missile) {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.DESTRUCT_MISSILE);
        messagePackage.addParameter(missileDestructor);
        messagePackage.addParameter(missile);
        sendMessage(messagePackage);
        return false;
    }

    @Override
    public boolean destructMissile(MissileDestructor missileDestructor, Missile missile, long time) {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.DESTRUCT_MISSILE);
        messagePackage.addParameter(missileDestructor);
        messagePackage.addParameter(missile);
        messagePackage.addParameter(time);
        sendMessage(messagePackage);
        return false;
    }

    @Override
    public boolean destructLauncher(LauncherDestructor launcherDestructor, Launcher launcher) throws InterruptedException {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.DESTRUCT_LAUNCHER);
        messagePackage.addParameter(launcherDestructor);
        messagePackage.addParameter(launcher);
        sendMessage(messagePackage);
        return false;
    }

    @Override
    public boolean destructLauncher(LauncherDestructor launcherDestructor, Launcher launcher, int time) throws InterruptedException {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.DESTRUCT_LAUNCHER);
        messagePackage.addParameter(launcherDestructor);
        messagePackage.addParameter(launcher);
        messagePackage.addParameter(time);
        sendMessage(messagePackage);
        return false;
    }

    @Override
    public Statistics getStatistics() {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.SHOW_STATS);
        sendMessage(messagePackage);

        messagePackage = receiveMessage();
        return (Statistics) messagePackage.getParameters().get(0);

    }

    @Override
    public void exit() {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.EXIT);
        sendMessage(messagePackage);
    }

    @Override
    public Set<Launcher> retrieveLaunchers() {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.GET_MISSILE_LAUNCHERS);
        sendMessage(messagePackage);

        messagePackage = receiveMessage();
        ArrayList<Launcher> arr = (ArrayList<Launcher>)messagePackage.getParameters().get(0);

        return new HashSet<>(arr);
    }

    @Override
    public Set<MissileDestructor> retrieveMissileDestructors() {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.GET_MISSILE_DESTRUCTORS);
        sendMessage(messagePackage);

        messagePackage = receiveMessage();
        ArrayList<MissileDestructor> arr = (ArrayList<MissileDestructor>)messagePackage.getParameters().get(0);

        return new HashSet<>(arr);
    }

    @Override
    public Set<LauncherDestructor> retrieveLauncherDestructors() {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.GET_LAUNCHER_DESTRUCTORS);
        sendMessage(messagePackage);

        messagePackage = receiveMessage();
        ArrayList<LauncherDestructor> arr = (ArrayList<LauncherDestructor>)messagePackage.getParameters().get(0);

        return new HashSet<>(arr);
    }

    @Override
    public Set<Missile> retrieveActiveMissiles() {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.GET_ACTIVE_MISSILES);
        sendMessage(messagePackage);

        messagePackage = receiveMessage();
        ArrayList<Missile> arr = (ArrayList<Missile>)messagePackage.getParameters().get(0);

        return new HashSet<>(arr);
    }

    @Override
    public void subscribe(WarObserver subscriber) {
        receiveMessage.subscribe(subscriber);
    }

    @Override
    public void unsubscribe(WarObserver subscriber) {
        receiveMessage.unsubscribe(subscriber);
    }

    public void startTime() {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.START_TIME);
        sendMessage(messagePackage);
    }

    @Override
    public long getTime() {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.GET_TIME);
        sendMessage(messagePackage);

        messagePackage = receiveMessage();
        return (long) messagePackage.getParameters().get(0);
    }

    public void setDBChoice(String choice){
        MessagePackage messagePackage;
        switch (choice){
            case "SQL":
                messagePackage = new MessagePackage(MessagePackage.Choice.SQL);
                sendMessage(messagePackage);
                break;
            case "MONGODB":
                messagePackage = new MessagePackage(MessagePackage.Choice.MONGODB);
                sendMessage(messagePackage);
                break;
        }
    }

    private void sendMessage(MessagePackage msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private MessagePackage receiveMessage() {
        MessagePackage messagePackage = null;
        try {
            messagePackage = (MessagePackage) in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return messagePackage;
    }

}
