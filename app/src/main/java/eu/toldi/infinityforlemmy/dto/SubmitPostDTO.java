package eu.toldi.infinityforlemmy.dto;

public class SubmitPostDTO {

    private String name;
    private int community_id;
    private String url;
    private String body;
    private String honeypot;
    private boolean nsfw;
    private Integer language_id;
    private String auth;

    public SubmitPostDTO(String name, int community_id, String url, String body, String honeypot, boolean nsfw, Integer language_id, String auth) {
        this.name = name;
        this.community_id = community_id;
        this.url = url;
        this.body = body;
        this.honeypot = honeypot;
        this.nsfw = nsfw;
        this.language_id = language_id;
        this.auth = auth;
    }

    public String getName() {
        return name;
    }

    public int getCommunity_id() {
        return community_id;
    }

    public String getUrl() {
        return url;
    }

    public String getBody() {
        return body;
    }

    public String getHoneypot() {
        return honeypot;
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
