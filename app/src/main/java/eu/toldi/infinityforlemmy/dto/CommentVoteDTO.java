package eu.toldi.infinityforlemmy.dto;

public class CommentVoteDTO {
    int comment_id;
    int score;
    String auth;

    public CommentVoteDTO(int comment_id, int score, String auth) {
        this.comment_id = comment_id;
        this.score = score;
        this.auth = auth;
    }

    public int getComment_id() {
        return comment_id;
    }

    public int getScore() {
        return score;
    }

    public String getAuth() {
        return auth;
    }
}
