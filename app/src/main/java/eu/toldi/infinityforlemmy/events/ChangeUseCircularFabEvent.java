package eu.toldi.infinityforlemmy.events;

public class ChangeUseCircularFabEvent {

    private boolean useCircularFab;

    public ChangeUseCircularFabEvent(boolean useCircularFab) {
        this.useCircularFab = useCircularFab;
    }

    public boolean isUseCircularFab() {
        return useCircularFab;
    }
}
