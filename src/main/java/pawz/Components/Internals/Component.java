package pawz.Components.Internals;

import pawz.Components.NamedEvents.NamedTournamentEvents;
import pawz.Components.NamedEvents.PuzzleDashboardEvent;
import pawz.Components.NamedEvents.SubmissionDashboardEvent;
import pawz.Components.NamedEvents.SyncStatusEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class Component<ObservableData> {
    private final List<ComponentObserver<ObservableData>> observers = new ArrayList<>();

    protected final TournamentEventFilter eventFilter = new TournamentEventFilter();


    public interface ComponentObserver<ObservableData>{
        void onObservedDataUpdate(ObservableData data);
    }


    public Component(){
        eventFilter.registerEvent(NamedTournamentEvents.SyncStatusEventCode, e -> onSyncStatusEvent((SyncStatusEvent) e));
        eventFilter.registerEvent(NamedTournamentEvents.SubmissionDashboardEventCode, e -> onSubmissionDashboardEvent((SubmissionDashboardEvent<?, ?>) e));
        eventFilter.registerEvent(NamedTournamentEvents.PuzzleDashboardEventCode, e -> onPuzzleDashboardEvent((PuzzleDashboardEvent<?, ?>) e));
    }

    protected Object onSyncStatusEvent(SyncStatusEvent ignoredEvent){
        return null;
    }

    protected Object onSubmissionDashboardEvent(SubmissionDashboardEvent<?, ?> ignoredEvent){
        return null;
    }

    protected Object onPuzzleDashboardEvent(PuzzleDashboardEvent<?, ?> ignoredEvent){
        return null;
    }

    protected void notifyObservers(){
        for(var o :observers)
            o.onObservedDataUpdate(getCurrentSnapshot());
    }

    protected abstract ObservableData getCurrentSnapshot();

    public Object receiveEvent(TournamentEvent e){
        return eventFilter.handleEvent(e);
    }

    public void registerObserver(ComponentObserver<ObservableData> observer){
        this.observers.add(observer);
        observer.onObservedDataUpdate(getCurrentSnapshot());
    }

    TournamentEventFilter getEventFilter(){
        return eventFilter;
    }
}
