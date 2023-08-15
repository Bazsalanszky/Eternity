package eu.toldi.infinityforlemmy.events;

import eu.toldi.infinityforlemmy.privatemessage.PrivateMessage;

public class RepliedToPrivateMessageEvent {
    public PrivateMessage newReply;
    public int messagePosition;

    public RepliedToPrivateMessageEvent(PrivateMessage newReply, int messagePosition) {
        this.newReply = newReply;
        this.messagePosition = messagePosition;
    }
}
