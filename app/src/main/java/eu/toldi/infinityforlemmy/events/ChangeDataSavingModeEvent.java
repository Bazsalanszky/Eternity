package eu.toldi.infinityforlemmy.events;

public class ChangeDataSavingModeEvent {
    public String dataSavingMode;

    public ChangeDataSavingModeEvent(String dataSavingMode) {
        this.dataSavingMode = dataSavingMode;
    }
}
