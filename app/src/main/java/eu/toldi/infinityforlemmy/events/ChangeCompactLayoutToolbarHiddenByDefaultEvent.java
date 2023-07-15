package eu.toldi.infinityforlemmy.events;

public class ChangeCompactLayoutToolbarHiddenByDefaultEvent {
    public boolean compactLayoutToolbarHiddenByDefault;

    public ChangeCompactLayoutToolbarHiddenByDefaultEvent(boolean compactLayoutToolbarHiddenByDefault) {
        this.compactLayoutToolbarHiddenByDefault = compactLayoutToolbarHiddenByDefault;
    }
}
