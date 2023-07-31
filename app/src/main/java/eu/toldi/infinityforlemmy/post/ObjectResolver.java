package eu.toldi.infinityforlemmy.post;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.comment.Comment;
import eu.toldi.infinityforlemmy.comment.ParseComment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ObjectResolver {

    RetrofitHolder retrofitHolder;

    public ObjectResolver(RetrofitHolder retrofitHolder) {
        this.retrofitHolder = retrofitHolder;
    }

    public void resolvePost(String query, String auth, ObjectResolverListener objectResolverListener) {
        LemmyAPI lemmyAPI = retrofitHolder.getRetrofit().create(LemmyAPI.class);

        Call<String> response = lemmyAPI.resolveObject(query, auth);
        response.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()
                        && response.body() != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body())).getJSONObject("post");
                        Post p = ParsePost.parseBasicData(jsonObject);
                        objectResolverListener.onResolveObjectSuccess(p);
                    } catch (JSONException e) {
                        objectResolverListener.onResolveObjectFailed();
                    }

                } else {
                    objectResolverListener.onResolveObjectFailed();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                objectResolverListener.onResolveObjectFailed();
            }
        });

    }

    public void resolveComment(String query, String auth, ObjectResolverListener objectResolverListener) {
        LemmyAPI lemmyAPI = retrofitHolder.getRetrofit().create(LemmyAPI.class);

        Call<String> response = lemmyAPI.resolveObject(query, auth);
        response.enqueue(
                new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()
                                && response.body() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(response.body())).getJSONObject("comment");
                                Comment c = ParseComment.parseSingleComment(jsonObject);
                                objectResolverListener.onResolveObjectSuccess(c);
                            } catch (JSONException e) {
                                objectResolverListener.onResolveObjectFailed();
                            }

                        } else {
                            objectResolverListener.onResolveObjectFailed();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        objectResolverListener.onResolveObjectFailed();
                    }
                }
        );
    }

    public interface ObjectResolverListener {
        void onResolveObjectSuccess(Object resolvedObject);

        void onResolveObjectFailed();
    }
}
