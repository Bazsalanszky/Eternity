package eu.toldi.infinityforlemmy.events;

public class ChangeVoteButtonsPositionEvent {
    public boolean voteButtonsOnTheRight;

    public ChangeVoteButtonsPositionEvent(boolean voteButtonsOnTheRight) {
        this.voteButtonsOnTheRight = voteButtonsOnTheRight;
    }
}
