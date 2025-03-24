package pawz.Components.Internals;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventLoop implements Runnable {
    private final ConcurrentLinkedQueue<TournamentEvent> events = new ConcurrentLinkedQueue<>();
    public volatile boolean isRunning;

    private final ConcurrentLinkedDeque<TournamentEventFilter> filters = new ConcurrentLinkedDeque<>();

    public void addEvent(TournamentEvent event){
        events.add(event);
    }


    @Override
    public void run() {
        isRunning = true;

        while (isRunning){
            var e = events.poll();
            propagateEvent(e);
        }

    }

    public void registerComponent(Component<?> component){
        filters.add(component.getEventFilter());
    }

    private void propagateEvent(TournamentEvent e) {
        if(e != null){
            for(var filter: filters)
                filter.handleEvent(e);
        }
    }
}
