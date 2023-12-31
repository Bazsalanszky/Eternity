package eu.toldi.infinityforlemmy.adapters;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import eu.toldi.infinityforlemmy.asynctasks.InsertMultireddit;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.multireddit.MultiReddit;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import me.zhanghai.android.fastscroll.PopupTextProvider;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Retrofit;

public class MultiRedditListingRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PopupTextProvider {

    private static final int VIEW_TYPE_FAVORITE_MULTI_REDDIT_DIVIDER = 0;
    private static final int VIEW_TYPE_FAVORITE_MULTI_REDDIT = 1;
    private static final int VIEW_TYPE_MULTI_REDDIT_DIVIDER = 2;
    private static final int VIEW_TYPE_MULTI_REDDIT = 3;

    private BaseActivity mActivity;
    private Executor mExecutor;
    private Retrofit mOauthRetrofit;
    private RedditDataRoomDatabase mRedditDataRoomDatabase;
    private RequestManager mGlide;

    private String mAccessToken;
    private List<MultiReddit> mMultiReddits;
    private List<MultiReddit> mFavoriteMultiReddits;
    private int mPrimaryTextColor;
    private int mSecondaryTextColor;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onClick(MultiReddit multiReddit);

        void onLongClick(MultiReddit multiReddit);
    }

    public MultiRedditListingRecyclerViewAdapter(BaseActivity activity, Executor executor, Retrofit oauthRetrofit,
                                                 RedditDataRoomDatabase redditDataRoomDatabase,
                                                 CustomThemeWrapper customThemeWrapper,
                                                 String accessToken, OnItemClickListener onItemClickListener) {
        mActivity = activity;
        mExecutor = executor;
        mGlide = Glide.with(activity);
        mOauthRetrofit = oauthRetrofit;
        mRedditDataRoomDatabase = redditDataRoomDatabase;
        mAccessToken = accessToken;
        mPrimaryTextColor = customThemeWrapper.getPrimaryTextColor();
        mSecondaryTextColor = customThemeWrapper.getSecondaryTextColor();
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mFavoriteMultiReddits != null && mFavoriteMultiReddits.size() > 0) {
            if (position == 0) {
                return VIEW_TYPE_FAVORITE_MULTI_REDDIT_DIVIDER;
            } else if (position == mFavoriteMultiReddits.size() + 1) {
                return VIEW_TYPE_MULTI_REDDIT_DIVIDER;
            } else if (position <= mFavoriteMultiReddits.size()) {
                return VIEW_TYPE_FAVORITE_MULTI_REDDIT;
            } else {
                return VIEW_TYPE_MULTI_REDDIT;
            }
        } else {
            return VIEW_TYPE_MULTI_REDDIT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_FAVORITE_MULTI_REDDIT_DIVIDER:
                return new FavoriteMultiRedditsDividerViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_favorite_thing_divider, parent, false));
            case VIEW_TYPE_FAVORITE_MULTI_REDDIT:
                return new FavoriteMultiRedditViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_multi_reddit, parent, false));
            case VIEW_TYPE_MULTI_REDDIT_DIVIDER:
                return new AllMultiRedditsDividerViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_favorite_thing_divider, parent, false));
            default:
                return new MultiRedditViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_multi_reddit, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MultiRedditViewHolder) {
            String name;
            String iconUrl;

            int offset = (mFavoriteMultiReddits != null && mFavoriteMultiReddits.size() > 0) ?
                    mFavoriteMultiReddits.size() + 2 : 0;

            MultiReddit multiReddit = mMultiReddits.get(holder.getBindingAdapterPosition() - offset);
            name = multiReddit.getDisplayName();
            iconUrl = multiReddit.getIconUrl();
            if (multiReddit.isFavorite()) {
                ((MultiRedditViewHolder) holder).favoriteImageView.setImageResource(R.drawable.ic_favorite_24dp);
            } else {
                ((MultiRedditViewHolder) holder).favoriteImageView.setImageResource(R.drawable.ic_favorite_border_24dp);
            }

            ((MultiRedditViewHolder) holder).favoriteImageView.setOnClickListener(view -> {
                if (multiReddit.isFavorite()) {
                    ((MultiRedditViewHolder) holder).favoriteImageView.setImageResource(R.drawable.ic_favorite_border_24dp);
                    multiReddit.setFavorite(false);

                    InsertMultireddit.insertMultireddit(mExecutor, new Handler(), mRedditDataRoomDatabase, multiReddit,
                            () -> {
                                //Do nothing
                            });
                } else {
                    ((MultiRedditViewHolder) holder).favoriteImageView.setImageResource(R.drawable.ic_favorite_24dp);
                    multiReddit.setFavorite(true);

                    InsertMultireddit.insertMultireddit(mExecutor, new Handler(), mRedditDataRoomDatabase, multiReddit,
                            () -> {
                                //Do nothing
                            });
                }

            });
            holder.itemView.setOnClickListener(view -> {
                mOnItemClickListener.onClick(multiReddit);
            });

            holder.itemView.setOnLongClickListener(view -> {
                mOnItemClickListener.onLongClick(multiReddit);
                return true;
            });

            if (iconUrl != null && !iconUrl.equals("")) {
                mGlide.load(iconUrl)
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .error(mGlide.load(R.drawable.subreddit_default_icon)
                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                        .into(((MultiRedditViewHolder) holder).iconImageView);
            } else {
                mGlide.load(R.drawable.subreddit_default_icon)
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .into(((MultiRedditViewHolder) holder).iconImageView);
            }
            ((MultiRedditViewHolder) holder).multiRedditNameTextView.setText(name);
        } else if (holder instanceof FavoriteMultiRedditViewHolder) {
            MultiReddit multiReddit = mFavoriteMultiReddits.get(holder.getBindingAdapterPosition() - 1);
            String name = multiReddit.getDisplayName();
            String iconUrl = multiReddit.getIconUrl();
            if (multiReddit.isFavorite()) {
                ((FavoriteMultiRedditViewHolder) holder).favoriteImageView.setImageResource(R.drawable.ic_favorite_24dp);
            } else {
                ((FavoriteMultiRedditViewHolder) holder).favoriteImageView.setImageResource(R.drawable.ic_favorite_border_24dp);
            }

            ((FavoriteMultiRedditViewHolder) holder).favoriteImageView.setOnClickListener(view -> {
                if (multiReddit.isFavorite()) {
                    ((FavoriteMultiRedditViewHolder) holder).favoriteImageView.setImageResource(R.drawable.ic_favorite_border_24dp);
                    multiReddit.setFavorite(false);
                    InsertMultireddit.insertMultireddit(mExecutor, new Handler(), mRedditDataRoomDatabase, multiReddit,
                            () -> {
                                //Do nothing
                            });

                } else {
                    ((FavoriteMultiRedditViewHolder) holder).favoriteImageView.setImageResource(R.drawable.ic_favorite_24dp);
                    multiReddit.setFavorite(true);

                    InsertMultireddit.insertMultireddit(mExecutor, new Handler(), mRedditDataRoomDatabase, multiReddit,
                            () -> {
                                //Do nothing
                            });
                }
            });
            holder.itemView.setOnClickListener(view -> {
                mOnItemClickListener.onClick(multiReddit);
            });

            holder.itemView.setOnLongClickListener(view -> {
                mOnItemClickListener.onLongClick(multiReddit);
                return true;
            });

            if (iconUrl != null && !iconUrl.equals("")) {
                mGlide.load(iconUrl)
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .error(mGlide.load(R.drawable.subreddit_default_icon)
                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                        .into(((FavoriteMultiRedditViewHolder) holder).iconImageView);
            } else {
                mGlide.load(R.drawable.subreddit_default_icon)
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .into(((FavoriteMultiRedditViewHolder) holder).iconImageView);
            }
            ((FavoriteMultiRedditViewHolder) holder).multiRedditNameTextView.setText(name);
        }
    }

    @Override
    public int getItemCount() {
        if (mMultiReddits != null) {
            if (mFavoriteMultiReddits != null && mFavoriteMultiReddits.size() > 0) {
                return mMultiReddits.size() > 0 ?
                        mFavoriteMultiReddits.size() + mMultiReddits.size() + 2 : 0;
            }

            return mMultiReddits.size();
        }
        return 0;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof MultiRedditViewHolder) {
            mGlide.clear(((MultiRedditViewHolder) holder).iconImageView);
        } else if (holder instanceof FavoriteMultiRedditViewHolder) {
            mGlide.clear(((FavoriteMultiRedditViewHolder) holder).iconImageView);
        }
    }

    public void setMultiReddits(List<MultiReddit> multiReddits) {
        mMultiReddits = multiReddits;
        notifyDataSetChanged();
    }

    public void setFavoriteMultiReddits(List<MultiReddit> favoriteMultiReddits) {
        mFavoriteMultiReddits = favoriteMultiReddits;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public String getPopupText(int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_MULTI_REDDIT:
                int offset = (mFavoriteMultiReddits != null && mFavoriteMultiReddits.size() > 0) ?
                        mFavoriteMultiReddits.size() + 2 : 0;
                return mMultiReddits.get(position - offset).getDisplayName().substring(0, 1).toUpperCase();
            case VIEW_TYPE_FAVORITE_MULTI_REDDIT:
                return mFavoriteMultiReddits.get(position - 1).getDisplayName().substring(0, 1).toUpperCase();
            default:
                return "";
        }
    }

    class MultiRedditViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.multi_reddit_icon_gif_image_view_item_multi_reddit)
        GifImageView iconImageView;
        @BindView(R.id.multi_reddit_name_text_view_item_multi_reddit)
        TextView multiRedditNameTextView;
        @BindView(R.id.favorite_image_view_item_multi_reddit)
        ImageView favoriteImageView;

        MultiRedditViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mActivity.typeface != null) {
                multiRedditNameTextView.setTypeface(mActivity.typeface);
            }
            multiRedditNameTextView.setTextColor(mPrimaryTextColor);
        }
    }

    class FavoriteMultiRedditViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.multi_reddit_icon_gif_image_view_item_multi_reddit)
        GifImageView iconImageView;
        @BindView(R.id.multi_reddit_name_text_view_item_multi_reddit)
        TextView multiRedditNameTextView;
        @BindView(R.id.favorite_image_view_item_multi_reddit)
        ImageView favoriteImageView;

        FavoriteMultiRedditViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mActivity.typeface != null) {
                multiRedditNameTextView.setTypeface(mActivity.typeface);
            }
            multiRedditNameTextView.setTextColor(mPrimaryTextColor);
        }
    }

    class FavoriteMultiRedditsDividerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.divider_text_view_item_favorite_thing_divider)
        TextView dividerTextView;

        FavoriteMultiRedditsDividerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mActivity.typeface != null) {
                dividerTextView.setTypeface(mActivity.typeface);
            }
            dividerTextView.setText(R.string.favorites);
            dividerTextView.setTextColor(mSecondaryTextColor);
        }
    }

    class AllMultiRedditsDividerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.divider_text_view_item_favorite_thing_divider)
        TextView dividerTextView;

        AllMultiRedditsDividerViewHolder(@NonNull View itemView) {
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
