package eu.toldi.infinityforlemmy.events;

public class ChangeLongPressToHideToolbarInCompactLayoutEvent {
    public boolean longPressToHideToolbarInCompactLayout;

    public ChangeLongPressToHideToolbarInCompactLayoutEvent(boolean longPressToHideToolbarInCompactLayout) {
        this.longPressToHideToolbarInCompactLayout = longPressToHideToolbarInCompactLayout;
    }
}
