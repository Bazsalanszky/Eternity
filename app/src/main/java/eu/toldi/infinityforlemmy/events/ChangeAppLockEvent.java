package eu.toldi.infinityforlemmy.events;

public class ChangeAppLockEvent {
    public boolean appLock;
    public long appLockTimeout;

    public ChangeAppLockEvent(boolean appLock, long appLockTimeout) {
        this.appLock = appLock;
        this.appLockTimeout = appLockTimeout;
    }
}
