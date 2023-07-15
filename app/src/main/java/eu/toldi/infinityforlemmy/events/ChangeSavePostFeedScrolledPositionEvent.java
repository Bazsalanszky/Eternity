package eu.toldi.infinityforlemmy.events;

public class ChangeSavePostFeedScrolledPositionEvent {
    public boolean savePostFeedScrolledPosition;

    public ChangeSavePostFeedScrolledPositionEvent(boolean savePostFeedScrolledPosition) {
        this.savePostFeedScrolledPosition = savePostFeedScrolledPosition;
    }
}
