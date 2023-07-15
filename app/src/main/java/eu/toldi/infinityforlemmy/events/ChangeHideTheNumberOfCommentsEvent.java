package eu.toldi.infinityforlemmy.events;

public class ChangeHideTheNumberOfCommentsEvent {
    public boolean hideTheNumberOfComments;

    public ChangeHideTheNumberOfCommentsEvent(boolean hideTheNumberOfComments) {
        this.hideTheNumberOfComments = hideTheNumberOfComments;
    }
}
