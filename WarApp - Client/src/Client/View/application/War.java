package Client.View.application;

import Client.Messaging.MessageController;
import Client.View.AbstractWarView;
import Client.View.ConsoleView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class War {


    public static void main(String[] args) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.execute(() -> WarApplication.main(args));
        AbstractWarView abstractWarView = new ConsoleView(MessageController.getInstance());
        abstractWarView.start();
        es.shutdown();
    }

    protected static void loadFromJson(){
        MessageController.getInstance().readFromJson();
    }


}
