package eu.toldi.infinityforlemmy.events;

public class ChangePostLayoutEvent {
    public int postLayout;

    public ChangePostLayoutEvent(int postLayout) {
        this.postLayout = postLayout;
    }
}
