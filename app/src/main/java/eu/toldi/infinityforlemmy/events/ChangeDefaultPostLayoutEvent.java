package eu.toldi.infinityforlemmy.events;

public class ChangeDefaultPostLayoutEvent {
    public int defaultPostLayout;

    public ChangeDefaultPostLayoutEvent(int defaultPostLayout) {
        this.defaultPostLayout = defaultPostLayout;
    }
}
