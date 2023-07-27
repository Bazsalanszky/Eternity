package eu.toldi.infinityforlemmy.dto;

public class EditCommentDTO {

    private int comment_id;
    private String content;
    private Integer language_id;
    private String form_id;
    private String auth;

    public EditCommentDTO(int comment_id, String content, Integer language_id, String form_id, String auth) {
        this.comment_id = comment_id;
        this.content = content;
        this.language_id = language_id;
        this.form_id = form_id;
        this.auth = auth;
    }

    public int getComment_id() {
        return comment_id;
    }

    public String getContent() {
        return content;
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
