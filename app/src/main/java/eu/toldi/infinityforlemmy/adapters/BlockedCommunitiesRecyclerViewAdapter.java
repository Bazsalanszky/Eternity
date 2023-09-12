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
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.activities.BaseActivity;
import eu.toldi.infinityforlemmy.activities.ViewSubredditDetailActivity;
import eu.toldi.infinityforlemmy.blockedcommunity.BlockedCommunityData;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import me.zhanghai.android.fastscroll.PopupTextProvider;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Retrofit;

public class BlockedCommunitiesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PopupTextProvider {
    private static final int VIEW_TYPE_FAVORITE_SUBREDDIT_DIVIDER = 0;
    private static final int VIEW_TYPE_FAVORITE_SUBREDDIT = 1;
    private static final int VIEW_TYPE_SUBREDDIT_DIVIDER = 2;
    private static final int VIEW_TYPE_SUBREDDIT = 3;

    private BaseActivity mActivity;
    private Executor mExecutor;
    private Retrofit mOauthRetrofit;
    private RedditDataRoomDatabase mRedditDataRoomDatabase;
    private List<BlockedCommunityData> mBlockedCommunityData;
    private List<BlockedCommunityData> mFavoriteBlockedCommunityData;
    private RequestManager glide;
    private ItemClickListener itemClickListener;

    private String accessToken;
    private String username;
    private String userIconUrl;
    private boolean hasClearSelectionRow;

    private int primaryTextColor;
    private int secondaryTextColor;

    public BlockedCommunitiesRecyclerViewAdapter(BaseActivity activity, Executor executor, Retrofit oauthRetrofit,
                                                 RedditDataRoomDatabase redditDataRoomDatabase,
                                                 CustomThemeWrapper customThemeWrapper,
                                                 String accessToken) {
        mActivity = activity;
        mExecutor = executor;
        glide = Glide.with(activity);
        mOauthRetrofit = oauthRetrofit;
        mRedditDataRoomDatabase = redditDataRoomDatabase;
        this.accessToken = accessToken;
        primaryTextColor = customThemeWrapper.getPrimaryTextColor();
        secondaryTextColor = customThemeWrapper.getSecondaryTextColor();
    }

    public BlockedCommunitiesRecyclerViewAdapter(BaseActivity activity, Executor executor, Retrofit oauthRetrofit,
                                                 RedditDataRoomDatabase redditDataRoomDatabase,
                                                 CustomThemeWrapper customThemeWrapper,
                                                 String accessToken, boolean hasClearSelectionRow,
                                                 ItemClickListener itemClickListener) {
        this(activity, executor, oauthRetrofit, redditDataRoomDatabase, customThemeWrapper, accessToken);
        this.hasClearSelectionRow = hasClearSelectionRow;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (mFavoriteBlockedCommunityData != null && mFavoriteBlockedCommunityData.size() > 0) {
            if (itemClickListener != null && !hasClearSelectionRow) {
                if (position == 0) {
                    return VIEW_TYPE_SUBREDDIT;
                } else if (position == 1) {
                    return VIEW_TYPE_FAVORITE_SUBREDDIT_DIVIDER;
                } else if (position == mFavoriteBlockedCommunityData.size() + 2) {
                    return VIEW_TYPE_SUBREDDIT_DIVIDER;
                } else if (position <= mFavoriteBlockedCommunityData.size() + 1) {
                    return VIEW_TYPE_FAVORITE_SUBREDDIT;
                } else {
                    return VIEW_TYPE_SUBREDDIT;
                }
            } else if (hasClearSelectionRow) {
                if (position == 0) {
                    return VIEW_TYPE_SUBREDDIT;
                } else if (position == 1) {
                    return VIEW_TYPE_SUBREDDIT;
                } else if (position == 2) {
                    return VIEW_TYPE_FAVORITE_SUBREDDIT_DIVIDER;
                } else if (position == mFavoriteBlockedCommunityData.size() + 3) {
                    return VIEW_TYPE_SUBREDDIT_DIVIDER;
                } else if (position <= mFavoriteBlockedCommunityData.size() + 2) {
                    return VIEW_TYPE_FAVORITE_SUBREDDIT;
                } else {
                    return VIEW_TYPE_SUBREDDIT;
                }
            } else {
                if (position == 0) {
                    return VIEW_TYPE_FAVORITE_SUBREDDIT_DIVIDER;
                } else if (position == mFavoriteBlockedCommunityData.size() + 1) {
                    return VIEW_TYPE_SUBREDDIT_DIVIDER;
                } else if (position <= mFavoriteBlockedCommunityData.size()) {
                    return VIEW_TYPE_FAVORITE_SUBREDDIT;
                } else {
                    return VIEW_TYPE_SUBREDDIT;
                }
            }
        } else {
            return VIEW_TYPE_SUBREDDIT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i) {
            case VIEW_TYPE_FAVORITE_SUBREDDIT_DIVIDER:
                return new FavoriteSubredditsDividerViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_favorite_thing_divider, viewGroup, false));
            case VIEW_TYPE_FAVORITE_SUBREDDIT:
                return new FavoriteSubredditViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_subscribed_thing, viewGroup, false));
            case VIEW_TYPE_SUBREDDIT_DIVIDER:
                return new AllSubredditsDividerViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_favorite_thing_divider, viewGroup, false));
            default:
                return new SubredditViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.item_subscribed_thing, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof SubredditViewHolder) {
            String name;
            String fullname = "";
            String iconUrl;

            if (hasClearSelectionRow && viewHolder.getBindingAdapterPosition() == 0) {
                ((SubredditViewHolder) viewHolder).subredditNameTextView.setText(R.string.all_communities);
                viewHolder.itemView.setOnClickListener(view -> itemClickListener.onClick(null));
                return;
            } else {
                int offset = hasClearSelectionRow ? 1 : 0;
                BlockedCommunityData communityData = mBlockedCommunityData.get(viewHolder.getBindingAdapterPosition() - offset);
                name = mBlockedCommunityData.get(viewHolder.getBindingAdapterPosition() - offset).getName();
                fullname = mBlockedCommunityData.get(viewHolder.getBindingAdapterPosition() - offset).getQualified_name();
                iconUrl = mBlockedCommunityData.get(viewHolder.getBindingAdapterPosition() - offset).getIconUrl();

                if (itemClickListener != null) {
                    viewHolder.itemView.setOnClickListener(view -> itemClickListener.onClick(communityData));
                }
            }

            if (itemClickListener == null) {
                String finalFullname = fullname;
                viewHolder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(mActivity, ViewSubredditDetailActivity.class);
                    intent.putExtra(ViewSubredditDetailActivity.EXTRA_SUBREDDIT_NAME_KEY, name);
                    intent.putExtra(ViewSubredditDetailActivity.EXTRA_COMMUNITY_FULL_NAME_KEY,
                            finalFullname);
                    mActivity.startActivity(intent);
                });
            }

            if (iconUrl != null && !iconUrl.equals("")) {
                glide.load(iconUrl)
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .error(glide.load(R.drawable.subreddit_default_icon)
                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                        .into(((SubredditViewHolder) viewHolder).iconGifImageView);
            } else {
                glide.load(R.drawable.subreddit_default_icon)
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .into(((SubredditViewHolder) viewHolder).iconGifImageView);
            }
            ((SubredditViewHolder) viewHolder).subredditNameTextView.setText(name);
            if (fullname.contains("@")) {
                ((SubredditViewHolder) viewHolder).communityInstanceTextView.setText("@" + fullname.split(Pattern.quote("@"), 2)[1]);
                ((SubredditViewHolder) viewHolder).communityInstanceTextView.setTextColor(CustomThemeWrapper.darkenColor(primaryTextColor, 0.7f));
            }
        } else if (viewHolder instanceof FavoriteSubredditViewHolder) {
            int offset;
            if (itemClickListener != null) {
                if (hasClearSelectionRow) {
                    offset = 3;
                } else {
                    offset = 2;
                }
            } else {
                offset = 1;
            }
            BlockedCommunityData communityData = mFavoriteBlockedCommunityData.get(viewHolder.getBindingAdapterPosition() - offset);
            String name = mFavoriteBlockedCommunityData.get(viewHolder.getBindingAdapterPosition() - offset).getName();
            String iconUrl = mFavoriteBlockedCommunityData.get(viewHolder.getBindingAdapterPosition() - offset).getIconUrl();

            if (itemClickListener != null) {
                viewHolder.itemView.setOnClickListener(view -> itemClickListener.onClick(communityData));
            } else {
                viewHolder.itemView.setOnClickListener(view -> {
                    Intent intent = new Intent(mActivity, ViewSubredditDetailActivity.class);
                    intent.putExtra(ViewSubredditDetailActivity.EXTRA_SUBREDDIT_NAME_KEY, name);
                    mActivity.startActivity(intent);
                });
            }

            if (iconUrl != null && !iconUrl.equals("")) {
                glide.load(iconUrl)
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .error(glide.load(R.drawable.subreddit_default_icon)
                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                        .into(((FavoriteSubredditViewHolder) viewHolder).iconGifImageView);
            } else {
                glide.load(R.drawable.subreddit_default_icon)
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .into(((FavoriteSubredditViewHolder) viewHolder).iconGifImageView);
            }
            ((FavoriteSubredditViewHolder) viewHolder).subredditNameTextView.setText(name);
        }
    }

    @Override
    public int getItemCount() {
        if (mBlockedCommunityData != null) {

            if (itemClickListener != null) {
                return mBlockedCommunityData.size() > 0 ? mBlockedCommunityData.size() + ((hasClearSelectionRow) ? 1 : 0) : 0;
            }

            return mBlockedCommunityData.size();
        }
        return 0;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof SubredditViewHolder) {
            glide.clear(((SubredditViewHolder) holder).iconGifImageView);
        } else if (holder instanceof FavoriteSubredditViewHolder) {
            glide.clear(((FavoriteSubredditViewHolder) holder).iconGifImageView);
        }
    }

    public void setSubscribedSubreddits(List<BlockedCommunityData> subscribedSubreddits) {
        mBlockedCommunityData = subscribedSubreddits;
        notifyDataSetChanged();
    }

    public void setFavoriteSubscribedSubreddits(List<BlockedCommunityData> favoriteBlockedCommunityData) {
        mFavoriteBlockedCommunityData = favoriteBlockedCommunityData;
        notifyDataSetChanged();
    }

    public void addUser(String username, String userIconUrl) {
        this.username = username;
        this.userIconUrl = userIconUrl;
    }

    @NonNull
    @Override
    public String getPopupText(int position) {
        switch (getItemViewType(position)) {
            case VIEW_TYPE_SUBREDDIT:
                if (hasClearSelectionRow && position == 0) {
                    return "";
                } else if (itemClickListener != null && !hasClearSelectionRow && position == 0) {
                    return "";
                } else if (hasClearSelectionRow && position == 1) {
                    return "";
                } else {
                    int offset;
                    if (itemClickListener != null) {
                        if (hasClearSelectionRow) {
                            offset = (mFavoriteBlockedCommunityData != null && mFavoriteBlockedCommunityData.size() > 0) ?
                                    mFavoriteBlockedCommunityData.size() + 4 : 0;
                        } else {
                            offset = (mFavoriteBlockedCommunityData != null && mFavoriteBlockedCommunityData.size() > 0) ?
                                    mFavoriteBlockedCommunityData.size() + 3 : 0;
                        }
                    } else {
                        offset = (mFavoriteBlockedCommunityData != null && mFavoriteBlockedCommunityData.size() > 0) ?
                                mFavoriteBlockedCommunityData.size() + 2 : 0;
                    }

                    return mBlockedCommunityData.get(position - offset).getName().substring(0, 1).toUpperCase();
                }
            case VIEW_TYPE_FAVORITE_SUBREDDIT:
                int offset;
                if (itemClickListener != null) {
                    if (hasClearSelectionRow) {
                        offset = 3;
                    } else {
                        offset = 2;
                    }
                } else {
                    offset = 1;
                }
                return mFavoriteBlockedCommunityData.get(position - offset).getName().substring(0, 1).toUpperCase();
            default:
                return "";
        }
    }

    public interface ItemClickListener {
        void onClick(BlockedCommunityData subredditData);
    }

    class SubredditViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thing_icon_gif_image_view_item_subscribed_thing)
        GifImageView iconGifImageView;
        @BindView(R.id.thing_name_text_view_item_subscribed_thing)
        TextView subredditNameTextView;

        @BindView(R.id.thing_instance_text_view_item_subscribed_thing)
        TextView communityInstanceTextView;

        SubredditViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mActivity.typeface != null) {
                subredditNameTextView.setTypeface(mActivity.typeface);
            }
            subredditNameTextView.setTextColor(primaryTextColor);
        }
    }

    class FavoriteSubredditViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thing_icon_gif_image_view_item_subscribed_thing)
        GifImageView iconGifImageView;
        @BindView(R.id.thing_name_text_view_item_subscribed_thing)
        TextView subredditNameTextView;

        FavoriteSubredditViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mActivity.typeface != null) {
                subredditNameTextView.setTypeface(mActivity.typeface);
            }
            subredditNameTextView.setTextColor(primaryTextColor);
        }
    }

    class FavoriteSubredditsDividerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.divider_text_view_item_favorite_thing_divider)
        TextView dividerTextView;

        FavoriteSubredditsDividerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mActivity.typeface != null) {
                dividerTextView.setTypeface(mActivity.typeface);
            }
            dividerTextView.setText(R.string.favorites);
            dividerTextView.setTextColor(secondaryTextColor);
        }
    }

    class AllSubredditsDividerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.divider_text_view_item_favorite_thing_divider)
        TextView dividerTextView;

        AllSubredditsDividerViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mActivity.typeface != null) {
                dividerTextView.setTypeface(mActivity.typeface);
            }
            dividerTextView.setText(R.string.all);
            dividerTextView.setTextColor(secondaryTextColor);
        }
    }
}

