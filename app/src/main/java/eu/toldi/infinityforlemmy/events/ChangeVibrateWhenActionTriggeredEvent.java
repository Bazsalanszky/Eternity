package eu.toldi.infinityforlemmy.events;

public class ChangeVibrateWhenActionTriggeredEvent {
    public boolean vibrateWhenActionTriggered;

    public ChangeVibrateWhenActionTriggeredEvent(boolean vibrateWhenActionTriggered) {
        this.vibrateWhenActionTriggered = vibrateWhenActionTriggered;
    }
}
