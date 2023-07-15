package eu.toldi.infinityforlemmy.events;

import eu.toldi.infinityforlemmy.Flair;

public class FlairSelectedEvent {
    public long viewPostDetailFragmentId;
    public Flair flair;

    public FlairSelectedEvent(long viewPostDetailFragmentId, Flair flair) {
        this.viewPostDetailFragmentId = viewPostDetailFragmentId;
        this.flair = flair;
    }
}
