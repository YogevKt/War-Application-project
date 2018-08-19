package Client.WarObserver;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

public class WarObservable {
    private final int MAX_NUM_OF_THREADS = 2;

    private List<WarObserver> subscribers = Collections.synchronizedList(new ArrayList<>());
    private ExecutorService executor = Executors.newFixedThreadPool(MAX_NUM_OF_THREADS);
    private Socket publishSocket;

    public WarObservable() {

    }

    protected void publish(Function<WarObserver, Void> function){
        for(WarObserver subscriber : subscribers){
            executor.execute(() -> function.apply(subscriber));
        }
    }

    public void subscribe(WarObserver subscriber){
        //open connection with the client
        subscribers.add(subscriber);
    }

    public void unsubscribe(WarObserver subscriber){
        subscribers.remove(subscriber);
    }

}
