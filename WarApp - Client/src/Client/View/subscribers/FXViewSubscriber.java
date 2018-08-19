package Client.View.subscribers;

import Entities.*;
import Client.View.EntitiesViews.*;
import Client.View.MainWindowController;
import Client.WarObserver.WarObserver;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleGroup;

import java.util.HashMap;

public class FXViewSubscriber implements WarObserver {

    private MainWindowController fxController;
    private HashMap<String, LauncherView> launcherToView = new HashMap<>();
    private HashMap<String, MissileView> missileToView = new HashMap<>();
    private HashMap<String, MissileDestructorView> missileDestructorToView = new HashMap<>();
    private HashMap<String, LauncherDestructorView> launcherDestructorToView = new HashMap<>();

    private ToggleGroup launchersToggleGroup = new ToggleGroup(),
            missileDestructorToggleGroup = new ToggleGroup(),
            launcherDestructorsToggleGroup = new ToggleGroup();


    public FXViewSubscriber(MainWindowController fxController) {
        setFxController(fxController);
    }

    @Override
    public Void launcherWasAdded(Launcher launcher) {

       Platform.runLater(() -> {
           LauncherView launcherView = new LauncherView(launcher);
           launcherToView.put(launcher.getId(), launcherView);
           fxController.getLaunchersBox().getChildren().add(launcherView);
           launcherView.setToggleGroup(launchersToggleGroup);
       });
        return null;
    }

    @Override
    public Void launcherDestructorWasAdded(LauncherDestructor launcherDestructor) {
        Platform.runLater(() -> {
            LauncherDestructorView launcherDestructorView
                    = new LauncherDestructorView(launcherDestructor);
            launcherDestructorToView.put(launcherDestructor.getId(), launcherDestructorView);

            if (launcherDestructor.getType().equals(LauncherDestructor.DestructorType.PLANE)) {
                fxController.getPlanesBox().getChildren().add(launcherDestructorView);
            } else {
                fxController.getShipsBox().getChildren().add(launcherDestructorView);
            }
            launcherDestructorView.setToggleGroup(launcherDestructorsToggleGroup);
        });
        return null;
    }

    @Override
    public Void missileDestructorWasAdded(MissileDestructor missileDestructor) {
        Platform.runLater(() -> {
            MissileDestructorView missileDestructorView = new MissileDestructorView(missileDestructor);
            fxController.getMissileDestructorsBox().getChildren().add(missileDestructorView);
            missileDestructorToView.put(missileDestructor.getId(), missileDestructorView);
            missileDestructorView.setToggleGroup(missileDestructorToggleGroup);
        });
        return null;
    }

    @Override
    public Void missileLaunched(Launcher launcher, Missile missile, Destination destination) {
        Platform.runLater(() ->{
            MissileView missileView = new MissileView(missile, "Client/View/images/Cupcake - medium.png");
            missileToView.put(missile.getId(), missileView);
            LauncherView launcherView = launcherToView.getOrDefault(launcher.getId(), null);
            TargetView targetView = getTargetView(destination);
            if(launcherView != null)
                fxController.launchingAnimation(launcherView, targetView, missileView, missile.getFlightTime());
            else {
                new Alert(Alert.AlertType.ERROR, "Invalid Launcher").showAndWait();
            }
        });
        return null;
    }

    @Override
    public Void missileHit(Launcher launcher, Missile missile, Destination destination) {
        TargetView targetView = getTargetView(destination);
        LauncherView launcherView = launcherToView.getOrDefault(launcher.getId(), null);
        MissileView missileView = missileToView.getOrDefault(missile.getId(), null);

        Platform.runLater(() ->
        fxController.launchHitAnimation(launcherView, targetView, fxController.getTargetsBox().getChildren() ,missileView));
        missileView.setVisible(false);

        return null;
    }


    private TargetView getTargetView(Destination destination){
        TargetView targetView;
        ObservableList<Node> filtered = fxController.getTargetsBox().getChildren()
                .filtered(target -> ((TargetView)target).getWeapon().getDest().equals(destination.getDest()));
        if(filtered.size() != 0) {
            targetView = (TargetView) filtered.get(0);
        }else{
            targetView = new TargetView(destination);
            fxController.getTargetsBox().getChildren().add(targetView);
        }

        return targetView;
    }

    @Override
    public Void missileDestructed(MissileDestructor missileDestructor, Missile missile) {
        Platform.runLater(() -> {
            MissileView missileView = new MissileView("Client/View/images/Swiss-chocolate.png");
            MissileDestructorView missileDestructorView =
                    missileDestructorToView.getOrDefault(missileDestructor.getId(), null);
            MissileView targetView =
                    missileToView.getOrDefault(missile.getId(), null);
            PathTransition pathTransition =
                    fxController.launchingAnimation(missileDestructorView, targetView, missileView, 1);
            pathTransition.setOnFinished(e -> {
                        missileView.setVisible(false);
                        fxController.launchHitAnimation(missileDestructorView, targetView, fxController.getContainer().getChildren(), missileView);
                    }
            );
        });
        return null;
    }

    @Override
    public Void tryToDestructLauncher(LauncherDestructor launcherDestructor, Launcher launcher, int time) {
        Platform.runLater(() -> {
            LauncherDestructorView launcherDestructorView = launcherDestructorToView.getOrDefault(launcherDestructor.getId(), null);
            LauncherView launcherView = launcherToView.getOrDefault(launcher.getId(), null);
            if(launcherView != null && launcherDestructorView != null) {
                fxController.destructingLauncherAnimation(launcherDestructorView, launcherView, time);
            }else {
                new Alert(Alert.AlertType.ERROR, "Launcher Client.View: " + launcherView + "Launcher Destructor Client.View" + launcherDestructorView)
                .showAndWait();
            }
        });
        return null;
    }

    @Override
    public Void launcherDestructed(LauncherDestructor launcherDestructor, Launcher launcher) {
        Platform.runLater(() -> {
            MissileView missileView;

            LauncherDestructorView launcherDestructorView = launcherDestructorToView.getOrDefault(launcherDestructor.getId(), null);
            LauncherView launcherView = launcherToView.getOrDefault(launcher.getId(), null);

            if (launcherDestructorView.getWeapon().getType().equals(LauncherDestructor.DestructorType.PLANE)){
                missileView = new MissileView("Client/View/images/Ice_Cream_Cone_PNG_Clip_Art-2033.png");
            }
            else {
                missileView = new MissileView("Client/View/images/Ferrero-Rocher.png");
            }

            fxController.launcherDestructorDroppingABomb(launcherDestructorView, launcherView, missileView, 1);
        });
        return null;
    }

    @Override
    public Void launcherWasHidden(LauncherDestructor launcherDestructor, Launcher launcher) {
        LauncherDestructorView launcherDestructorView = launcherDestructorToView.getOrDefault(launcherDestructor.getId(), null);
        Platform.runLater(() ->
            fxController.goingBack(launcherDestructorView, 1)
        );

        return null;
    }

    @Override
    public Void endOfWar() {
        Platform.runLater(() -> {});
        return null;
    }

    private void setFxController(MainWindowController fxController) {
        this.fxController = fxController;
    }


}
