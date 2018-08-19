package Client.Messaging;

import Client.WarObserver.WarObservable;
import Client.WarObserver.WarObserver;
import Entities.*;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ReceiveMessage extends WarObservable implements Runnable{
    private Gson gson;
    private ObjectInputStream in;
    private Socket socket = null;
    private boolean fContinue = true;

    public ReceiveMessage(){
        gson = new Gson();

    }

    @Override
    public void run() {

        try {
            MessagePackage messagePackage;
            socket = new Socket("localhost", 9896);
            System.out.println("Connected to server: " + socket.getInetAddress().getHostAddress());
            in = new ObjectInputStream(socket.getInputStream());

            while (fContinue){
                messagePackage = ((MessagePackage) in.readObject());
                menu(messagePackage.getChoice(), messagePackage.getParameters());

            }
        } catch (InterruptedIOException e) {

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void menu(MessagePackage.Choice choice, ArrayList<Object> messageParams){

        switch (choice){
            case ADD_LAUNCHER: {
                Launcher launcher = (Launcher)messageParams.get(0);
                publish(subscriber -> subscriber.launcherWasAdded(launcher));
                break;
            }

            case ADD_LAUNCHER_DESTRUCTOR: {
                LauncherDestructor launcherDestructor = (LauncherDestructor)messageParams.get(0);
                publish(subscriber -> subscriber.launcherDestructorWasAdded(launcherDestructor));
                break;
            }

            case ADD_MISSILE_DESTRUCTOR: {
                MissileDestructor missileDestructor = (MissileDestructor)messageParams.get(0);
                publish(subscriber -> subscriber.missileDestructorWasAdded(missileDestructor));
                break;
            }

            case LAUNCH_MISSILE: {
                Launcher launcher = (Launcher)messageParams.get(0);
                Missile missile = (Missile)messageParams.get(1);
                Destination destination = (Destination)messageParams.get(2);
                publish(subscriber -> subscriber.missileLaunched(launcher, missile, destination));
                break;
            }

            case MISSILE_HIT: {
                Launcher launcher = (Launcher)messageParams.get(0);
                Missile missile = (Missile)messageParams.get(1);
                Destination destination = (Destination)messageParams.get(2);
                publish(subscriber -> subscriber.missileHit(launcher, missile, missile.getDestination()));
                break;
            }

            case DESTRUCT_MISSILE: {
                MissileDestructor missileDestructor = (MissileDestructor)messageParams.get(0);
                Missile missile = (Missile)messageParams.get(1);
                publish(subscriber -> subscriber.missileDestructed(missileDestructor, missile));
                break;
            }

            case DESTRUCT_LAUNCHER: {
                LauncherDestructor launcherDestructor = (LauncherDestructor)messageParams.get(0);
                Launcher launcher = (Launcher)messageParams.get(1);
                int destructionTime = (int)messageParams.get(2);
                publish(subscriber -> subscriber.tryToDestructLauncher(launcherDestructor, launcher, destructionTime));
                break;
            }

            case LAUNCHER_DESTRUCTED_HIT: {
                LauncherDestructor launcherDestructor = (LauncherDestructor)messageParams.get(0);
                Launcher launcher = (Launcher)messageParams.get(1);
                publish(subscriber -> subscriber.launcherDestructed(launcherDestructor, launcher));
                break;
            }

            case LAUNCHER_WAS_HIDDEN: {
                LauncherDestructor launcherDestructor = (LauncherDestructor)messageParams.get(0);
                Launcher launcher = (Launcher)messageParams.get(1);
                publish(subscriber -> subscriber.launcherWasHidden(launcherDestructor, launcher));
                break;
            }

            case EXIT: {
                publish(WarObserver::endOfWar);
                fContinue = false;
            }
        }

    }

}
