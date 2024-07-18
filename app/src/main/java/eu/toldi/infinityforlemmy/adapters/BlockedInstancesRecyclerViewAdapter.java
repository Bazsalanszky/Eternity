package eu.toldi.infinityforlemmy.adapters;

import android.app.Activity;
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
import eu.toldi.infinityforlemmy.activities.InstanceInfoActivity;
import eu.toldi.infinityforlemmy.blockedinstances.BlockedInstanceData;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import me.zhanghai.android.fastscroll.PopupTextProvider;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Retrofit;

public class BlockedInstancesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PopupTextProvider {
    private static final int VIEW_TYPE_FAVORITE_SUBREDDIT_DIVIDER = 0;
    private static final int VIEW_TYPE_FAVORITE_SUBREDDIT = 1;
    private static final int VIEW_TYPE_SUBREDDIT_DIVIDER = 2;
    private static final int VIEW_TYPE_SUBREDDIT = 3;

    private BaseActivity mActivity;
    private Executor mExecutor;
    private Retrofit mOauthRetrofit;
    private RedditDataRoomDatabase mRedditDataRoomDatabase;
    private List<BlockedInstanceData> mBlockedInstanceData;
    private List<BlockedInstanceData> mFavoriteBlockedInstanceData;
    private RequestManager glide;
    private ItemClickListener itemClickListener;

    private String accessToken;
    private String instancename;
    private String instanceIconUrl;
    private boolean hasClearSelectionRow;

    private int primaryTextColor;
    private int secondaryTextColor;

    public BlockedInstancesRecyclerViewAdapter(BaseActivity activity, Executor executor, Retrofit oauthRetrofit,
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

    public BlockedInstancesRecyclerViewAdapter(BaseActivity activity, Executor executor, Retrofit oauthRetrofit,
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
        if (mFavoriteBlockedInstanceData != null && mFavoriteBlockedInstanceData.size() > 0) {
            if (itemClickListener != null && !hasClearSelectionRow) {
                if (position == 0) {
                    return VIEW_TYPE_SUBREDDIT;
                } else if (position == 1) {
                    return VIEW_TYPE_FAVORITE_SUBREDDIT_DIVIDER;
                } else if (position == mFavoriteBlockedInstanceData.size() + 2) {
                    return VIEW_TYPE_SUBREDDIT_DIVIDER;
                } else if (position <= mFavoriteBlockedInstanceData.size() + 1) {
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
                } else if (position == mFavoriteBlockedInstanceData.size() + 3) {
                    return VIEW_TYPE_SUBREDDIT_DIVIDER;
                } else if (position <= mFavoriteBlockedInstanceData.size() + 2) {
                    return VIEW_TYPE_FAVORITE_SUBREDDIT;
                } else {
                    return VIEW_TYPE_SUBREDDIT;
                }
            } else {
                if (position == 0) {
                    return VIEW_TYPE_FAVORITE_SUBREDDIT_DIVIDER;
                } else if (position == mFavoriteBlockedInstanceData.size() + 1) {
                    return VIEW_TYPE_SUBREDDIT_DIVIDER;
                } else if (position <= mFavoriteBlockedInstanceData.size()) {
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
        return new InstanceViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_subscribed_thing, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {

        String name;
        String iconUrl;

        if (hasClearSelectionRow && viewHolder.getBindingAdapterPosition() == 0) {
            ((InstanceViewHolder) viewHolder).subredditNameTextView.setText(R.string.all_communities);
            viewHolder.itemView.setOnClickListener(view -> itemClickListener.onClick(null));
            return;
        } else {
            int offset = hasClearSelectionRow ? 1 : 0;
            BlockedInstanceData instanceData = mBlockedInstanceData.get(viewHolder.getBindingAdapterPosition() - offset);
            String domain = mBlockedInstanceData.get(viewHolder.getBindingAdapterPosition() - offset).getDomain();
            String instanceName = mBlockedInstanceData.get(viewHolder.getBindingAdapterPosition() - offset).getName();
            name = instanceName != null ? instanceName + " (" + domain + ")" : domain;
            iconUrl = mBlockedInstanceData.get(viewHolder.getBindingAdapterPosition() - offset).getIcon();
        }


        ((InstanceViewHolder) viewHolder).itemView.setOnClickListener(view -> {
            if (mBlockedInstanceData != null) {
                BlockedInstanceData instanceData = mBlockedInstanceData.get(viewHolder.getBindingAdapterPosition());
                Intent intent = new Intent(mActivity, InstanceInfoActivity.class);
                intent.putExtra(InstanceInfoActivity.INSTANCE_INFO_DOMAIN, instanceData.getDomain());
                mActivity.startActivity(intent);
            }
        });


        if (iconUrl == null || iconUrl.equals("")) {
            ((InstanceViewHolder) viewHolder).iconGifImageView.setVisibility(View.GONE);
        } else {
            ((InstanceViewHolder) viewHolder).iconGifImageView.setVisibility(View.VISIBLE);
            glide.load(iconUrl)
                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                    .error(glide.load(R.drawable.subreddit_default_icon)
                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                    .into(((InstanceViewHolder) viewHolder).iconGifImageView);
        }


        ((InstanceViewHolder) viewHolder).subredditNameTextView.setText(name);


    }

    @Override
    public int getItemCount() {
        if (mBlockedInstanceData != null) {

            if (itemClickListener != null) {
                return mBlockedInstanceData.size() > 0 ? mBlockedInstanceData.size() + ((hasClearSelectionRow) ? 1 : 0) : 0;
            }

            return mBlockedInstanceData.size();
        }
        return 0;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        glide.clear(((InstanceViewHolder) holder).iconGifImageView);
    }

    public void blockedInstances(List<BlockedInstanceData> subscribedSubreddits) {
        mBlockedInstanceData = subscribedSubreddits;
        notifyDataSetChanged();
    }

    public void setFavoriteSubscribedSubreddits(List<BlockedInstanceData> favoriteBlockedInstanceData) {
        mFavoriteBlockedInstanceData = favoriteBlockedInstanceData;
        notifyDataSetChanged();
    }

    public void addInstance(String instancename, String instanceIconUrl) {
        this.instancename = instancename;
        this.instanceIconUrl = instanceIconUrl;
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
                            offset = (mFavoriteBlockedInstanceData != null && mFavoriteBlockedInstanceData.size() > 0) ?
                                    mFavoriteBlockedInstanceData.size() + 4 : 0;
                        } else {
                            offset = (mFavoriteBlockedInstanceData != null && mFavoriteBlockedInstanceData.size() > 0) ?
                                    mFavoriteBlockedInstanceData.size() + 3 : 0;
                        }
                    } else {
                        offset = (mFavoriteBlockedInstanceData != null && mFavoriteBlockedInstanceData.size() > 0) ?
                                mFavoriteBlockedInstanceData.size() + 2 : 0;
                    }

                    return mBlockedInstanceData.get(position - offset).getDomain().substring(0, 1).toUpperCase();
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
                return mFavoriteBlockedInstanceData.get(position - offset).getDomain().substring(0, 1).toUpperCase();
            default:
                return "";
        }
    }

    public interface ItemClickListener {
        void onClick(BlockedInstanceData subredditData);
    }

    class InstanceViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thing_icon_gif_image_view_item_subscribed_thing)
        GifImageView iconGifImageView;
        @BindView(R.id.thing_name_text_view_item_subscribed_thing)
        TextView subredditNameTextView;

        @BindView(R.id.thing_instance_text_view_item_subscribed_thing)
        TextView instanceInstanceTextView;

        InstanceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mActivity.typeface != null) {
                subredditNameTextView.setTypeface(mActivity.typeface);
            }
            subredditNameTextView.setTextColor(primaryTextColor);
        }
    }
}