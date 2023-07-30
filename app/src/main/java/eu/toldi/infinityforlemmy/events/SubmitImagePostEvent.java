package eu.toldi.infinityforlemmy.events;

import eu.toldi.infinityforlemmy.post.Post;

public class SubmitImagePostEvent {
    public boolean postSuccess;
    public String errorMessage;
    public Post post;

    public SubmitImagePostEvent(boolean postSuccess, String errorMessage, Post post) {
        this.postSuccess = postSuccess;
        this.errorMessage = errorMessage;
        this.post = post;
    }
}
