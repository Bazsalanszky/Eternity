package eu.toldi.infinityforlemmy.events;

public class ChangeDefaultLinkPostLayoutEvent {
    public int defaultLinkPostLayout;

    public ChangeDefaultLinkPostLayoutEvent(int defaultLinkPostLayout) {
        this.defaultLinkPostLayout = defaultLinkPostLayout;
    }
}
