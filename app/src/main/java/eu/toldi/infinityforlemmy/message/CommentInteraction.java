package eu.toldi.infinityforlemmy.message;

import eu.toldi.infinityforlemmy.comment.Comment;

public class CommentInteraction {
    int id;
    private Comment comment;
    private boolean isRead;

    public CommentInteraction(int id, Comment comment, boolean isRead) {
        this.id = id;
        this.comment = comment;
        this.isRead = isRead;
    }

    public Comment getComment() {
        return this.comment;
    }

    public boolean isRead() {
        return this.isRead;
    }

    public void markAsUnRead() {
        this.isRead = false;
    }


    public void markAsRead() {
        this.isRead = true;
    }

    public int getId() {
        return id;
    }
}
