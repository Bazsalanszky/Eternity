package eu.toldi.infinityforlemmy.dto;

public class ReadCommentDTO {

    private int comment_reply_id;
    private boolean read;
    private String auth;

    public ReadCommentDTO(int comment_reply_id, boolean read, String auth) {
        this.comment_reply_id = comment_reply_id;
        this.read = read;
        this.auth = auth;
    }

    public int getComment_reply_id() {
        return comment_reply_id;
    }

    public boolean isRead() {
        return read;
    }

    public String getAuth() {
        return auth;
    }
}
