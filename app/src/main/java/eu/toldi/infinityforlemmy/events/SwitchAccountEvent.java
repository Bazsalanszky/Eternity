package eu.toldi.infinityforlemmy.events;

public class SwitchAccountEvent {
    public String excludeActivityClassName;

    public SwitchAccountEvent() {
    }

    public SwitchAccountEvent(String excludeActivityClassName) {
        this.excludeActivityClassName = excludeActivityClassName;
    }
}
