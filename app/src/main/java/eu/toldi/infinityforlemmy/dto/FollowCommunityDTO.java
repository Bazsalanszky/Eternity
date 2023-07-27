package eu.toldi.infinityforlemmy.dto;

public class FollowCommunityDTO {

    private int community_id;
    private boolean follow;
    private String auth;

    public FollowCommunityDTO(int community_id, boolean follow, String auth) {
        this.community_id = community_id;
        this.follow = follow;
        this.auth = auth;
    }

    public int getCommunity_id() {
        return community_id;
    }

    public boolean isFollow() {
        return follow;
    }

    public String getAuth() {
        return auth;
    }
}
