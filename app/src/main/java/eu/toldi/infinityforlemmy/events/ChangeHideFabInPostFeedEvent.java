package eu.toldi.infinityforlemmy.events;

public class ChangeHideFabInPostFeedEvent {
    public boolean hideFabInPostFeed;

    public ChangeHideFabInPostFeedEvent(boolean hideFabInPostFeed) {
        this.hideFabInPostFeed = hideFabInPostFeed;
    }
}
