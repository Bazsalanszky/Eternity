package eu.toldi.infinityforlemmy;

public interface ActivityToolbarInterface {
    void onLongPress();
    default void displaySortType() {}
}
