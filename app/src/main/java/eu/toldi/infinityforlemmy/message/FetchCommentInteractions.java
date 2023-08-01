package eu.toldi.infinityforlemmy.message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.comment.Comment;
import eu.toldi.infinityforlemmy.comment.ParseComment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FetchCommentInteractions {
    static void fetchReplies(Retrofit retrofit, Integer page, boolean unreadOnly, String auth, FetchCommentInteractionsListener fetchMessagesListener) {
        LemmyAPI api = retrofit.create(LemmyAPI.class);

        api.userReplies("New", page, 25, unreadOnly, auth).enqueue(
                new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                JSONArray jsonArray = jsonObject.getJSONArray("replies");
                                List<CommentInteraction> commentInteractions = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject commentInteractionObject = jsonArray.getJSONObject(i);
                                    Comment comment = ParseComment.parseSingleComment(commentInteractionObject);
                                    boolean isRead = !commentInteractionObject.getJSONObject("comment_reply").getBoolean("read");
                                    int id = commentInteractionObject.getJSONObject("comment_reply").getInt("id");
                                    commentInteractions.add(new CommentInteraction(id, comment, isRead));
                                }
                                fetchMessagesListener.fetchSuccess(commentInteractions);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                fetchMessagesListener.fetchFailed();
                            }
                        } else {
                            fetchMessagesListener.fetchFailed();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        fetchMessagesListener.fetchFailed();
                    }
                }
        );
    }

    static void fetchMentions(Retrofit retrofit, Integer page, boolean unreadOnly, String auth, FetchCommentInteractionsListener fetchMessagesListener) {
        LemmyAPI api = retrofit.create(LemmyAPI.class);

        api.userMentions("New", page, 25, unreadOnly, auth).enqueue(
                new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                JSONArray jsonArray = jsonObject.getJSONArray("mentions");
                                List<CommentInteraction> commentInteractions = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject commentInteractionObject = jsonArray.getJSONObject(i);
                                    Comment comment = ParseComment.parseSingleComment(commentInteractionObject);
                                    boolean isRead = commentInteractionObject.getJSONObject("person_mention").getBoolean("read");
                                    int id = commentInteractionObject.getJSONObject("person_mention").getInt("id");
                                    commentInteractions.add(new CommentInteraction(id, comment, isRead));
                                }
                                fetchMessagesListener.fetchSuccess(commentInteractions);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                fetchMessagesListener.fetchFailed();
                            }
                        } else {
                            fetchMessagesListener.fetchFailed();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        fetchMessagesListener.fetchFailed();
                    }
                }
        );
    }

    public interface FetchCommentInteractionsListener {
        void fetchSuccess(List<CommentInteraction> commentInteractions);

        void fetchFailed();
    }
}
