package eu.toldi.infinityforlemmy.dto;

public class PostVoteDTO {

    private final int post_id;
    private final int score;
    private final String auth;

    public PostVoteDTO(int post_id, int vote, String auth) {
        this.post_id = post_id;
        this.score = vote;
        this.auth = auth;
    }

    public int getPost_id() {
        return post_id;
    }

    public int getScore() {
        return score;
    }

    public String getAccess_token() {
        return auth;
    }
}
