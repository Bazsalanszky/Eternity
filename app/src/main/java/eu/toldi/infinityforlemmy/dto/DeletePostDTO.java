package eu.toldi.infinityforlemmy.dto;

public class DeletePostDTO {

    int post_id;
    boolean deleted;
    String auth;

    public DeletePostDTO(int post_id, boolean deleted, String auth) {
        this.post_id = post_id;
        this.deleted = deleted;
        this.auth = auth;
    }

    public int getPost_id() {
        return post_id;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getAuth() {
        return auth;
    }
}
