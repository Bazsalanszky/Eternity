package eu.toldi.infinityforlemmy.dto;

public class ReadPostDTO {

    private int post_id;
    private boolean read;
    private String auth;

    public ReadPostDTO(int post_id, boolean read, String auth) {
        this.post_id = post_id;
        this.read = read;
        this.auth = auth;
    }

    public int getPost_id() {
        return post_id;
    }

    public boolean isRead() {
        return read;
    }

    public String getAuth() {
        return auth;
    }
}
