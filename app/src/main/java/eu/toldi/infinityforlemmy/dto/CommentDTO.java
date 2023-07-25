package eu.toldi.infinityforlemmy.dto;

public class CommentDTO {

    private String content;
    private Integer post_id;
    private Integer parent_id;
    private Integer language_id;
    private String form_id;
    private String auth;

    public CommentDTO(String content, Integer post_id, Integer parent_id, Integer language_id, String form_id, String auth) {
        this.content = content;
        this.post_id = post_id;
        this.parent_id = parent_id;
        this.language_id = language_id;
        this.form_id = form_id;
        this.auth = auth;
    }

    public String getContent() {
        return content;
    }

    public Integer getPost_id() {
        return post_id;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public Integer getLanguage_id() {
        return language_id;
    }

    public String getForm_id() {
        return form_id;
    }

    public String getAuth() {
        return auth;
    }
}
