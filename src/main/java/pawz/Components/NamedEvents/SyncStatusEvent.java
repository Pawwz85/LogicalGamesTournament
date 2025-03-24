package pawz.Components.NamedEvents;

import pawz.Components.Internals.TournamentEvent;

public class SyncStatusEvent extends TournamentEvent {

    public final boolean synchronised;

    public SyncStatusEvent(boolean synchronised) {
        super(NamedTournamentEvents.SyncStatusEventCode);
        this.synchronised = synchronised;
    }
}
