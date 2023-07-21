package eu.toldi.infinityforlemmy.comment;

import android.os.Handler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executor;

import eu.toldi.infinityforlemmy.apis.RevedditAPI;
import eu.toldi.infinityforlemmy.utils.APIUtils;
import eu.toldi.infinityforlemmy.utils.JSONUtils;
import eu.toldi.infinityforlemmy.utils.Utils;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FetchRemovedCommentReveddit {

    public static void fetchRemovedComment(Executor executor, Handler handler, Retrofit retrofit, Comment comment,
                                           long postCreatedUtc, int nComments, FetchRemovedCommentListener listener) {
        executor.execute(() -> {
            String parentIdWithoutPrefix = " comment.getParentId().substring(3)";
            String rootCommentId = parentIdWithoutPrefix.equals(comment.getLinkId()) ? String.valueOf(comment.getId()) : parentIdWithoutPrefix;
            try {
                Response<String> response = retrofit.create(RevedditAPI.class).getRemovedComments(
                        APIUtils.getRevedditHeader(),
                        comment.getLinkId(),
                        (comment.getCommentTimeMillis() / 1000) - 1,
                        rootCommentId,
                        String.valueOf(comment.getId()),
                        nComments,
                        postCreatedUtc / 1000,
                        true).execute();
                if (response.isSuccessful()) {
                    Comment removedComment = parseRemovedComment(new JSONObject(response.body()).getJSONObject(String.valueOf(comment.getId())), comment);
                    handler.post(() -> {
                        if (removedComment != null) {
                            listener.fetchSuccess(removedComment, comment);
                        } else {
                            listener.fetchFailed();
                        }
                    });
                } else {
                    handler.post(listener::fetchFailed);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                handler.post(listener::fetchFailed);
            }
        });
    }

    private static Comment parseRemovedComment(JSONObject result, Comment comment) throws JSONException {
        String id = result.getString(JSONUtils.ID_KEY);
        String author = result.getString(JSONUtils.AUTHOR_KEY);
        String body = Utils.modifyMarkdown(Utils.trimTrailingWhitespace(result.optString(JSONUtils.BODY_KEY)));

        if (id.equals(comment.getId()) && (!author.equals(comment.getAuthor()) || !body.equals(comment.getCommentRawText()))) {
            comment.setAuthor(author);
            comment.setCommentMarkdown(body);
            comment.setCommentRawText(body);
            if (result.has(JSONUtils.IS_SUBMITTER_KEY)) {
                // This doesn't seem to be present for the Reveddit API anymore...
                comment.setSubmittedByAuthor(result.getBoolean(JSONUtils.IS_SUBMITTER_KEY));
            }
            return comment;
        } else {
            return null;
        }
    }

    public interface FetchRemovedCommentListener {
        void fetchSuccess(Comment fetchedComment, Comment originalComment);

        void fetchFailed();
    }
}
