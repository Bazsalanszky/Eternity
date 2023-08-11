package eu.toldi.infinityforlemmy.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.concurrent.Executor;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.activities.BaseActivity;
import eu.toldi.infinityforlemmy.activities.ViewUserDetailActivity;
import eu.toldi.infinityforlemmy.blockeduser.BlockedUserData;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import me.zhanghai.android.fastscroll.PopupTextProvider;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Retrofit;

public class BlockedUsersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PopupTextProvider {
    private static final int VIEW_TYPE_FAVORITE_USER_DIVIDER = 0;
    private static final int VIEW_TYPE_FAVORITE_USER = 1;
    private static final int VIEW_TYPE_USER_DIVIDER = 2;
    private static final int VIEW_TYPE_USER = 3;

    private List<BlockedUserData> mBlockedUserData;
    private List<BlockedUserData> mFavoriteBlockedUserData;
    private BaseActivity mActivity;
    private Executor mExecutor;
    private Retrofit mOauthRetrofit;
    private RedditDataRoomDatabase mRedditDataRoomDatabase;
    private String mAccessToken;
    private RequestManager glide;
    private int mPrimaryTextColor;
    private int mSecondaryTextColor;

    public BlockedUsersRecyclerViewAdapter(BaseActivity activity, Executor executor, Retrofit oauthRetrofit,
                                           RedditDataRoomDatabase redditDataRoomDatabase,
                                           CustomThemeWrapper customThemeWrapper,
                                           String accessToken) {
        mActivity = activity;
        mExecutor = executor;
        mOauthRetrofit = oauthRetrofit;
        mRedditDataRoomDatabase = redditDataRoomDatabase;
        mAccessToken = accessToken;
        glide = Glide.with(activity);
        mPrimaryTextColor = customThemeWrapper.getPrimaryTextColor();
        mSecondaryTextColor = customThemeWrapper.getSecondaryTextColor();
    }

    @Override
    public int getItemViewType(int position) {
        if (mFavoriteBlockedUserData != null && mFavoriteBlockedUserData.size() > 0) {
            if (position == 0) {
                return VIEW_TYPE_FAVORITE_USER_DIVIDER;
            } else if (position == mFavoriteBlockedUserData.size() + 1) {
                return VIEW_TYPE_USER_DIVIDER;
            } else if (position <= mFavoriteBlockedUserData.size()) {
                return VIEW_TYPE_FAVORITE_USER;
            } else {
                return VIEW_TYPE_USER;
            }
        } else {
            return VIEW_TYPE_USER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i) {
            case VIEW_TYPE_FAVORITE_USER_DIVIDER:
                return new FavoriteUsersDividerViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_favorite_thing_divider, viewGroup, false));
            case VIEW_TYPE_USER_DIVIDER:
                return new AllUsersDividerViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_favorite_thing_divider, viewGroup, false));
            default:
                return new UserViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_subscribed_thing, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof UserViewHolder) {
            int offset = 0;

            if (!mBlockedUserData.get(viewHolder.getBindingAdapterPosition() - offset).getAvatar().equals("")) {
                glide.load(mBlockedUserData.get(viewHolder.getBindingAdapterPosition() - offset).getAvatar())
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .error(glide.load(R.drawable.subreddit_default_icon)
                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                        .into(((UserViewHolder) viewHolder).iconGifImageView);
            } else {
                glide.load(R.drawable.subreddit_default_icon)
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .into(((UserViewHolder) viewHolder).iconGifImageView);
            }
            ((UserViewHolder) viewHolder).userNameTextView.setText(mBlockedUserData.get(viewHolder.getBindingAdapterPosition() - offset).getName());

        }
    }

    @Override
    public int getItemCount() {
        if (mBlockedUserData != null && mBlockedUserData.size() > 0) {
            return mBlockedUserData.size();
        }
        return 0;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof UserViewHolder) {
            glide.clear(((UserViewHolder) holder).iconGifImageView);
        }
    }

    public void setSubscribedUsers(List<BlockedUserData> subscribedUsers) {
        mBlockedUserData = subscribedUsers;
        notifyDataSetChanged();
    }

    public void setFavoriteSubscribedUsers(List<BlockedUserData> favoriteSubscribedUsers) {
        mFavoriteBlockedUserData = favoriteSubscribedUsers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public String getPopupText(int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_USER:
                int offset = (mFavoriteBlockedUserData != null && mFavoriteBlockedUserData.size() > 0) ?
                        mFavoriteBlockedUserData.size() + 2 : 0;
                return mBlockedUserData.get(position - offset).getName().substring(0, 1).toUpperCase();
            case VIEW_TYPE_FAVORITE_USER:
                return mFavoriteBlockedUserData.get(position - 1).getName().substring(0, 1).toUpperCase();
            default:
                return "";
        }
    }


    class UserViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thing_icon_gif_image_view_item_subscribed_thing)
        GifImageView iconGifImageView;
        @BindView(R.id.thing_name_text_view_item_subscribed_thing)
        TextView userNameTextView;

        UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mActivity.typeface != null) {
                userNameTextView.setTypeface(mActivity.typeface);
            }
            userNameTextView.setTextColor(mPrimaryTextColor);

            itemView.setOnClickListener(view -> {
                int offset = (mFavoriteBlockedUserData != null && mFavoriteBlockedUserData.size() > 0) ?
                        mFavoriteBlockedUserData.size() + 2 : 0;
                int position = getBindingAdapterPosition() - offset;
                if (position >= 0 && mBlockedUserData.size() > position) {
                    Intent intent = new Intent(mActivity, ViewUserDetailActivity.class);
                    intent.putExtra(ViewUserDetailActivity.EXTRA_USER_NAME_KEY, mBlockedUserData.get(position).getName());
                    intent.putExtra(ViewUserDetailActivity.EXTRA_QUALIFIED_USER_NAME_KEY, mBlockedUserData.get(position).getQualifiedName());
                    mActivity.startActivity(intent);
                }
            });

        }
    }

    class FavoriteUsersDividerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.divider_text_view_item_favorite_thing_divider)
        TextView dividerTextView;

        FavoriteUsersDividerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mActivity.typeface != null) {
                dividerTextView.setTypeface(mActivity.typeface);
            }
            dividerTextView.setText(R.string.favorites);
            dividerTextView.setTextColor(mSecondaryTextColor);
        }
    }

    class AllUsersDividerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.divider_text_view_item_favorite_thing_divider)
        TextView dividerTextView;

        AllUsersDividerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mActivity.typeface != null) {
                dividerTextView.setTypeface(mActivity.typeface);
            }
            dividerTextView.setText(R.string.all);
            dividerTextView.setTextColor(mSecondaryTextColor);
        }
    }
}
