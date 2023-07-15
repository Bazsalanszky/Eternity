package eu.toldi.infinityforlemmy.events;

import eu.toldi.infinityforlemmy.message.Message;

public class PassPrivateMessageEvent {
    public Message message;

    public PassPrivateMessageEvent(Message message) {
        this.message = message;
    }
}
