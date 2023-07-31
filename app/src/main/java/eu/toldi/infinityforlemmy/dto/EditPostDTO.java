package eu.toldi.infinityforlemmy.dto;

public class EditPostDTO {

    private int post_id;
    private String name;
    private String url;
    private String body;
    private boolean nsfw;
    private Integer language_id;
    private String auth;

    public EditPostDTO(int post_id, String name, String url, String body, boolean nsfw, Integer language_id, String auth) {
        this.post_id = post_id;
        this.name = name;
        this.url = url;
        this.body = body;
        this.nsfw = nsfw;
        this.language_id = language_id;
        this.auth = auth;
    }

    public int getPost_id() {
        return post_id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public Integer getLanguage_id() {
        return language_id;
    }

    public String getAuth() {
        return auth;
    }

}
