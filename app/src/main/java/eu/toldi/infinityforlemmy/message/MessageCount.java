package eu.toldi.infinityforlemmy.message;

public class MessageCount {

    /*
    Example:
    {
  "replies": 1,
  "mentions": 0,
  "private_messages": 0
}
     */
    public int replies;
    public int mentions;
    public int private_messages;

    public MessageCount(int replies, int mentions, int private_messages) {
        this.replies = replies;
        this.mentions = mentions;
        this.private_messages = private_messages;
    }

    public int getReplies() {
        return replies;
    }

    public int getMentions() {
        return mentions;
    }

    public int getPrivate_messages() {
        return private_messages;
    }

    public int getSum() {
        return replies + mentions + private_messages;
    }
}
