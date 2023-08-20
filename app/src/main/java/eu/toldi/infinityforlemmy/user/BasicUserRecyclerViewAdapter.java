package eu.toldi.infinityforlemmy.user;


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

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.activities.BaseActivity;
import eu.toldi.infinityforlemmy.activities.ViewUserDetailActivity;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import me.zhanghai.android.fastscroll.PopupTextProvider;
import pl.droidsonroids.gif.GifImageView;

public class BasicUserRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PopupTextProvider {
    private static final int VIEW_TYPE_FAVORITE_USER_DIVIDER = 0;
    private static final int VIEW_TYPE_FAVORITE_USER = 1;
    private static final int VIEW_TYPE_USER_DIVIDER = 2;
    private static final int VIEW_TYPE_USER = 3;

    private List<BasicUserInfo> basicUserInfo;
    private BaseActivity mActivity;
    private RequestManager glide;
    private int mPrimaryTextColor;
    private int mSecondaryTextColor;

    public BasicUserRecyclerViewAdapter(BaseActivity activity,
                                        CustomThemeWrapper customThemeWrapper) {
        mActivity = activity;
        glide = Glide.with(activity);
        mPrimaryTextColor = customThemeWrapper.getPrimaryTextColor();
        mSecondaryTextColor = customThemeWrapper.getSecondaryTextColor();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_USER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new UserViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_subscribed_thing, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof UserViewHolder) {

            if (!basicUserInfo.get(viewHolder.getBindingAdapterPosition()).getAvatar().equals("")) {
                glide.load(basicUserInfo.get(viewHolder.getBindingAdapterPosition()).getAvatar())
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .error(glide.load(R.drawable.subreddit_default_icon)
                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                        .into(((UserViewHolder) viewHolder).iconGifImageView);
            } else {
                glide.load(R.drawable.subreddit_default_icon)
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .into(((UserViewHolder) viewHolder).iconGifImageView);
            }
            ((UserViewHolder) viewHolder).userNameTextView.setText(basicUserInfo.get(viewHolder.getBindingAdapterPosition()).getDisplayName());

        }
    }

    @Override
    public int getItemCount() {
        if (basicUserInfo != null && basicUserInfo.size() > 0) {
            return basicUserInfo.size();
        }
        return 0;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof UserViewHolder) {
            glide.clear(((UserViewHolder) holder).iconGifImageView);
        }
    }

    public void setUsers(List<BasicUserInfo> subscribedUsers) {
        basicUserInfo = subscribedUsers;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public String getPopupText(int position) {
        return basicUserInfo.get(position).getQualifiedName().substring(0, 1).toUpperCase();
    }

    protected int getUserNameTextColor() {
        return mPrimaryTextColor;
    }


    protected class UserViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thing_icon_gif_image_view_item_subscribed_thing)
        GifImageView iconGifImageView;
        @BindView(R.id.thing_name_text_view_item_subscribed_thing)
        TextView userNameTextView;

        protected UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mActivity.typeface != null) {
                userNameTextView.setTypeface(mActivity.typeface);
            }
            userNameTextView.setTextColor(getUserNameTextColor());

            itemView.setOnClickListener(view -> {
                int position = getBindingAdapterPosition();
                if (position >= 0 && basicUserInfo.size() > position) {
                    Intent intent = new Intent(mActivity, ViewUserDetailActivity.class);
                    intent.putExtra(ViewUserDetailActivity.EXTRA_USER_NAME_KEY, basicUserInfo.get(position).getDisplayName());
                    intent.putExtra(ViewUserDetailActivity.EXTRA_QUALIFIED_USER_NAME_KEY, basicUserInfo.get(position).getQualifiedName());
                    mActivity.startActivity(intent);
                }
            });

        }
    }
}
