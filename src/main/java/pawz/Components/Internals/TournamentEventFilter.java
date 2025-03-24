package pawz.Components.Internals;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class TournamentEventFilter {

    private final ConcurrentHashMap<Integer, Function<TournamentEvent, Object>> eventHandlers;

    public TournamentEventFilter() {
        eventHandlers = new ConcurrentHashMap<>();
    }


    public @Nullable Function<TournamentEvent, Object> registerEvent(int eventCode, Function<TournamentEvent, Object> handler){
        return eventHandlers.put(eventCode, handler);
    }

    public Object handleEvent(TournamentEvent event){
       Function<TournamentEvent, Object> handler = eventHandlers.get(event.eventTypeCode);
       return (handler != null) ? handler.apply(event) : null;
    }
}
