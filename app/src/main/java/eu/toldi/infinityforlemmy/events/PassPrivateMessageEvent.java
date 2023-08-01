package eu.toldi.infinityforlemmy.events;

import eu.toldi.infinityforlemmy.message.CommentInteraction;

public class PassPrivateMessageEvent {
    public CommentInteraction message;

    public PassPrivateMessageEvent(CommentInteraction message) {
        this.message = message;
    }
}
