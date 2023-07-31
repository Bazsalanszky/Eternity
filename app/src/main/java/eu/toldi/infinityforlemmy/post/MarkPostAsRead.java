package eu.toldi.infinityforlemmy.post;

import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.dto.ReadPostDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MarkPostAsRead {
    RetrofitHolder retrofitHolder;

    public MarkPostAsRead(RetrofitHolder retrofitHolder) {
        this.retrofitHolder = retrofitHolder;
    }

    private void setPostAsRead(int post_id, boolean markAsRead, String auth, MarkPostAsReadListener markPostAsReadListener) {
        LemmyAPI lemmyAPI = retrofitHolder.getRetrofit().create(LemmyAPI.class);

        lemmyAPI.postRead(new ReadPostDTO(post_id, markAsRead, auth)).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()
                        && response.body() != null) {
                    markPostAsReadListener.onMarkPostAsReadSuccess();
                } else {
                    markPostAsReadListener.onMarkPostAsReadFailed();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                markPostAsReadListener.onMarkPostAsReadFailed();
            }
        });
    }

    public void markPostAsRead(int post_id, String auth, MarkPostAsReadListener markPostAsReadListener) {
        setPostAsRead(post_id, true, auth, markPostAsReadListener);
    }

    public void markPostAsUnread(int post_id, String auth, MarkPostAsReadListener markPostAsReadListener) {
        setPostAsRead(post_id, false, auth, markPostAsReadListener);
    }

    public interface MarkPostAsReadListener {
        void onMarkPostAsReadSuccess();

        void onMarkPostAsReadFailed();
    }
}
