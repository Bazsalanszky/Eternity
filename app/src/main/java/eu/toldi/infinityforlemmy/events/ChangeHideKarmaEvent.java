package eu.toldi.infinityforlemmy.events;

public class ChangeHideKarmaEvent {
    public boolean hideKarma;

    public ChangeHideKarmaEvent(boolean showKarma) {
        this.hideKarma = showKarma;
    }
}
