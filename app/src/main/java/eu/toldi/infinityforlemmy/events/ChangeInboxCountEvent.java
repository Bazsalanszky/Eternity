package eu.toldi.infinityforlemmy.events;

public class ChangeInboxCountEvent {
    public int inboxCount;

    public ChangeInboxCountEvent(int inboxCount) {
        this.inboxCount = inboxCount;
    }
}
