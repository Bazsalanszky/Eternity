package eu.toldi.infinityforlemmy.dto;

public class DeleteCommentDTO {

    private int comment_id;
    boolean deleted;
    String auth;

    public DeleteCommentDTO(int comment_id, boolean deleted, String auth) {
        this.comment_id = comment_id;
        this.deleted = deleted;
        this.auth = auth;
    }

    public int getComment_id() {
        return comment_id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getAuth() {
        return auth;
    }
}
