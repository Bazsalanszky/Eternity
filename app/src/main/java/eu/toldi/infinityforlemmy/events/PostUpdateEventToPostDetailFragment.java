package eu.toldi.infinityforlemmy.events;

import eu.toldi.infinityforlemmy.post.Post;

public class PostUpdateEventToPostDetailFragment {
    public final Post post;

    public PostUpdateEventToPostDetailFragment(Post post) {
        this.post = post;
    }
}
