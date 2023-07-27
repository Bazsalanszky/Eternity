package eu.toldi.infinityforlemmy.dto;

public class SaveCommentDTO {

    private int comment_id;
    private boolean save;
    private String auth;

    public SaveCommentDTO(int comment_id, boolean save, String auth) {
        this.comment_id = comment_id;
        this.save = save;
        this.auth = auth;
    }

    public int getComment_id() {
        return comment_id;
    }

    public boolean isSave() {
        return save;
    }

    public String getAuth() {
        return auth;
    }
}
