package Server.Messaging;

import Entities.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MessageController implements WarObserver {
    private static MessageController messageController = null;
    private static ObjectOutputStream out;
    private static Socket socket;
    private static ServerSocket serverSocket;


    public static MessageController getInstance(){
        if(messageController == null) {
            messageController  = new MessageController();
            connect();
        }
        return messageController;
    }

    private static void connect(){
        try {
            serverSocket = new ServerSocket(9896);
            socket = serverSocket.accept();
            String clientAddress = "";

            clientAddress = socket.getInetAddress() + ":" +socket.getPort();
            System.out.println("client connected to server from: "+clientAddress);
            out = new ObjectOutputStream(socket.getOutputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        String clientAddress = socket.getInetAddress() + ":" +socket.getPort();

        try {
            out.close();
            socket.close();
            System.out.println("Connection with "+ clientAddress+" was close");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(MessagePackage msg) {
        try{
            synchronized (out) {
                if(!socket.isClosed()) {
                	out.writeObject(msg);
                    out.flush();
                }
            }
        }
        catch(IOException ioException){
            ioException.printStackTrace();
        }
    }

    @Override
    public Void launcherWasAdded(Launcher launcher) {
        MessagePackage messagePackage =  new MessagePackage(MessagePackage.Choice.ADD_LAUNCHER);
        messagePackage.addParameter(launcher);
        sendMessage(messagePackage);
        return null;
    }

    @Override
    public Void launcherDestructorWasAdded(LauncherDestructor launcherDestructor) {
        MessagePackage messagePackage =  new MessagePackage(MessagePackage.Choice.ADD_LAUNCHER_DESTRUCTOR);
        messagePackage.addParameter(launcherDestructor);
        sendMessage(messagePackage);
        return null;
    }

    @Override
    public Void missileDestructorWasAdded(MissileDestructor missileDestructor) {
        MessagePackage messagePackage =  new MessagePackage(MessagePackage.Choice.ADD_MISSILE_DESTRUCTOR);
        messagePackage.addParameter(missileDestructor);
        sendMessage(messagePackage);
        return null;
    }

    @Override
    public Void missileLaunched(Launcher launcher, Missile missile, Destination destination) {
        MessagePackage messagePackage =  new MessagePackage(MessagePackage.Choice.LAUNCH_MISSILE);
        messagePackage.addParameter(launcher);
        messagePackage.addParameter(missile);
        messagePackage.addParameter(destination);
        sendMessage(messagePackage);
        return null;
    }

    @Override
    public Void missileHit(Launcher launcher, Missile missile, Destination destination) {
        MessagePackage messagePackage =  new MessagePackage(MessagePackage.Choice.MISSILE_HIT);
        messagePackage.addParameter(launcher);
        messagePackage.addParameter(missile);
        messagePackage.addParameter(destination);
        sendMessage(messagePackage);
        return null;
    }

    @Override
    public Void missileDestructed(MissileDestructor missileDestructor, Missile missile) {
        MessagePackage messagePackage =  new MessagePackage(MessagePackage.Choice.DESTRUCT_MISSILE);
        messagePackage.addParameter(missileDestructor);
        messagePackage.addParameter(missile);
        sendMessage(messagePackage);
        return null;
    }

    @Override
    public Void tryToDestructLauncher(LauncherDestructor launcherDestructor, Launcher launcher, int time) {
        MessagePackage messagePackage =  new MessagePackage(MessagePackage.Choice.DESTRUCT_LAUNCHER);
        messagePackage.addParameter(launcherDestructor);
        messagePackage.addParameter(launcher);
        messagePackage.addParameter(time);
        sendMessage(messagePackage);
        return null;
    }

    @Override
    public Void launcherDestructed(LauncherDestructor launcherDestructor, Launcher launcher) {
        MessagePackage messagePackage = new MessagePackage(MessagePackage.Choice.LAUNCHER_DESTRUCTED_HIT);
        messagePackage.addParameter(launcherDestructor);
        messagePackage.addParameter(launcher);
        sendMessage(messagePackage);
        return null;
    }

    @Override
    public Void launcherWasHidden(LauncherDestructor launcherDestructor, Launcher launcher) {
        MessagePackage messagePackage =  new MessagePackage(MessagePackage.Choice.LAUNCHER_WAS_HIDDEN);
        messagePackage.addParameter(launcherDestructor);
        messagePackage.addParameter(launcher);
        sendMessage(messagePackage);
        return null;
    }


    @Override
    public Void endOfWar() {
        MessagePackage messagePackage =  new MessagePackage(MessagePackage.Choice.EXIT);
        sendMessage(messagePackage);
        try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
    }
}
