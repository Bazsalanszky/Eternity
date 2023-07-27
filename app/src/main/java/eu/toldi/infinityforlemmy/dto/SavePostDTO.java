package eu.toldi.infinityforlemmy.dto;

public class SavePostDTO {


    private int post_id;
    private boolean save;
    private String auth;

    public SavePostDTO(int post_id, boolean save, String auth) {
        this.post_id = post_id;
        this.save = save;
        this.auth = auth;
    }

    public int getPost_id() {
        return post_id;
    }

    public boolean isSave() {
        return save;
    }

    public String getAuth() {
        return auth;
    }
}
