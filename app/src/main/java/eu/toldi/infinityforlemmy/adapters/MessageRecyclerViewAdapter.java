package eu.toldi.infinityforlemmy.adapters;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.NetworkState;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.activities.BaseActivity;
import eu.toldi.infinityforlemmy.activities.LinkResolverActivity;
import eu.toldi.infinityforlemmy.activities.ViewPostDetailActivity;
import eu.toldi.infinityforlemmy.activities.ViewPrivateMessagesActivity;
import eu.toldi.infinityforlemmy.activities.ViewUserDetailActivity;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.events.ChangeInboxCountEvent;
import eu.toldi.infinityforlemmy.markdown.RedditHeadingPlugin;
import eu.toldi.infinityforlemmy.markdown.SpoilerAwareMovementMethod;
import eu.toldi.infinityforlemmy.markdown.SpoilerParserPlugin;
import eu.toldi.infinityforlemmy.markdown.SuperscriptPlugin;
import eu.toldi.infinityforlemmy.message.CommentInteraction;
import eu.toldi.infinityforlemmy.message.FetchMessage;
import eu.toldi.infinityforlemmy.message.ReadMessage;
import io.noties.markwon.AbstractMarkwonPlugin;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonConfiguration;
import io.noties.markwon.core.MarkwonTheme;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import io.noties.markwon.inlineparser.HtmlInlineProcessor;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;
import io.noties.markwon.movement.MovementMethodPlugin;
import retrofit2.Retrofit;

public class MessageRecyclerViewAdapter extends PagedListAdapter<CommentInteraction, RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_DATA = 0;
    private static final int VIEW_TYPE_ERROR = 1;
    private static final int VIEW_TYPE_LOADING = 2;
    private static final DiffUtil.ItemCallback<CommentInteraction> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull CommentInteraction message, @NonNull CommentInteraction t1) {
            return message.getComment().getId() == t1.getComment().getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull CommentInteraction message, @NonNull CommentInteraction t1) {
            return message.getComment().getCommentMarkdown().equals(t1.getComment().getCommentMarkdown());
        }
    };
    private BaseActivity mActivity;
    private Retrofit retrofit;
    private Markwon mMarkwon;
    private String mAccessToken;
    private int mMessageType;
    private NetworkState networkState;
    private RetryLoadingMoreCallback mRetryLoadingMoreCallback;
    private int mColorAccent;
    private int mMessageBackgroundColor;
    private int mUsernameColor;
    private int mPrimaryTextColor;
    private int mSecondaryTextColor;
    private int mUnreadMessageBackgroundColor;
    private int mColorPrimaryLightTheme;
    private int mButtonTextColor;
    private boolean markAllMessagesAsRead = false;

    public MessageRecyclerViewAdapter(BaseActivity activity, Retrofit oauthRetrofit,
                                      CustomThemeWrapper customThemeWrapper,
                                      String accessToken, String where,
                                      RetryLoadingMoreCallback retryLoadingMoreCallback) {
        super(DIFF_CALLBACK);
        mActivity = activity;
        retrofit = oauthRetrofit;
        mRetryLoadingMoreCallback = retryLoadingMoreCallback;

        mColorAccent = customThemeWrapper.getColorAccent();
        mMessageBackgroundColor = customThemeWrapper.getCardViewBackgroundColor();
        mUsernameColor = customThemeWrapper.getUsername();
        mPrimaryTextColor = customThemeWrapper.getPrimaryTextColor();
        mSecondaryTextColor = customThemeWrapper.getSecondaryTextColor();
        int spoilerBackgroundColor = mSecondaryTextColor | 0xFF000000;
        mUnreadMessageBackgroundColor = customThemeWrapper.getUnreadMessageBackgroundColor();
        mColorPrimaryLightTheme = customThemeWrapper.getColorPrimaryLightTheme();
        mButtonTextColor = customThemeWrapper.getButtonTextColor();

        // todo:https://github.com/Docile-Alligator/Infinity-For-Reddit/issues/1027
        //  add tables support and replace with MarkdownUtils#commonPostMarkwonBuilder
        mMarkwon = Markwon.builder(mActivity)
                .usePlugin(MarkwonInlineParserPlugin.create(plugin -> {
                    plugin.excludeInlineProcessor(HtmlInlineProcessor.class);
                }))
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                        builder.linkResolver((view, link) -> {
                            Intent intent = new Intent(mActivity, LinkResolverActivity.class);
                            Uri uri = Uri.parse(link);
                            intent.setData(uri);
                            mActivity.startActivity(intent);
                        });
                    }

                    @Override
                    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                        builder.linkColor(customThemeWrapper.getLinkColor());
                    }
                })
                .usePlugin(SuperscriptPlugin.create())
                .usePlugin(SpoilerParserPlugin.create(mSecondaryTextColor, spoilerBackgroundColor))
                .usePlugin(RedditHeadingPlugin.create())
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(MovementMethodPlugin.create(new SpoilerAwareMovementMethod()))
                .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
                .usePlugin(GlideImagesPlugin.create(mActivity))
                .build();
        mAccessToken = accessToken;
        if (where.equals(FetchMessage.WHERE_MESSAGES)) {
            mMessageType = FetchMessage.MESSAGE_TYPE_PRIVATE_MESSAGE;
        } else {
            mMessageType = FetchMessage.MESSAGE_TYPE_INBOX;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DATA) {
            return new DataViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false));
        } else if (viewType == VIEW_TYPE_ERROR) {
            return new ErrorViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer_error, parent, false));
        } else {
            return new LoadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer_loading, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DataViewHolder) {
            CommentInteraction message = getItem(holder.getBindingAdapterPosition());
            if (message != null) {

                if (message.isRead()) {
                    if (markAllMessagesAsRead) {
                        message.markAsRead();
                    } else {
                        holder.itemView.setBackgroundColor(
                                mUnreadMessageBackgroundColor);
                    }
                }


                ((DataViewHolder) holder).titleTextView.setVisibility(View.GONE);


                ((DataViewHolder) holder).authorTextView.setText(message.getComment().getAuthorQualifiedName());
                String subject = message.getComment().getCommunityQualifiedName();
                ((DataViewHolder) holder).subjectTextView.setText(subject);
                mMarkwon.setMarkdown(((DataViewHolder) holder).contentCustomMarkwonView, message.getComment().getCommentMarkdown());

                holder.itemView.setOnClickListener(view -> {
                    if (mMessageType == FetchMessage.MESSAGE_TYPE_INBOX
                            && message.getComment() != null) {

                        Intent intent = new Intent(mActivity, ViewPostDetailActivity.class);

                        intent.putExtra(ViewPostDetailActivity.EXTRA_POST_ID, message.getComment().getPostId());
                        intent.putExtra(ViewPostDetailActivity.EXTRA_SINGLE_COMMENT_ID, message.getComment().getId());
                        if(message.getComment().getDepth() > 0) {
                            intent.putExtra(ViewPostDetailActivity.EXTRA_SINGLE_COMMENT_PARENT_ID, message.getComment().getParentId());
                        }
                        mActivity.startActivity(intent);
                    } else if (mMessageType == FetchMessage.MESSAGE_TYPE_PRIVATE_MESSAGE) {
                        Intent intent = new Intent(mActivity, ViewPrivateMessagesActivity.class);
                        intent.putExtra(ViewPrivateMessagesActivity.EXTRA_PRIVATE_MESSAGE_INDEX, holder.getBindingAdapterPosition());
                        intent.putExtra(ViewPrivateMessagesActivity.EXTRA_MESSAGE_POSITION, holder.getBindingAdapterPosition());
                        mActivity.startActivity(intent);
                    }

                    if (message.isRead()) {
                        holder.itemView.setBackgroundColor(mMessageBackgroundColor);


                        ReadMessage.readMessage(retrofit, mAccessToken, message.getId(),
                                new ReadMessage.ReadMessageListener() {
                                    @Override
                                    public void readSuccess() {
                                        message.markAsRead();
                                        EventBus.getDefault().post(new ChangeInboxCountEvent(-1));
                                    }

                                    @Override
                                    public void readFailed() {
                                        message.markAsUnRead();
                                        holder.itemView.setBackgroundColor(mUnreadMessageBackgroundColor);
                                    }
                                });
                    }
                });

                ((DataViewHolder) holder).authorTextView.setOnClickListener(view -> {

                    Intent intent = new Intent(mActivity, ViewUserDetailActivity.class);
                    intent.putExtra(ViewUserDetailActivity.EXTRA_USER_NAME_KEY, message.getComment().getAuthor());
                    intent.putExtra(ViewUserDetailActivity.EXTRA_QUALIFIED_USER_NAME_KEY, message.getComment().getAuthorQualifiedName());
                    mActivity.startActivity(intent);
                });
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Reached at the end
        if (hasExtraRow() && position == getItemCount() - 1) {
            if (networkState.getStatus() == NetworkState.Status.LOADING) {
                return VIEW_TYPE_LOADING;
            } else {
                return VIEW_TYPE_ERROR;
            }
        } else {
            return VIEW_TYPE_DATA;
        }
    }

    @Override
    public int getItemCount() {
        if (hasExtraRow()) {
            return super.getItemCount() + 1;
        }
        return super.getItemCount();
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof DataViewHolder) {
            ((DataViewHolder) holder).itemView.setBackgroundColor(mMessageBackgroundColor);
            ((DataViewHolder) holder).titleTextView.setVisibility(View.VISIBLE);
        }
    }

    private boolean hasExtraRow() {
        return networkState != null && networkState.getStatus() != NetworkState.Status.SUCCESS;
    }

    public void setNetworkState(NetworkState newNetworkState) {
        NetworkState previousState = this.networkState;
        boolean previousExtraRow = hasExtraRow();
        this.networkState = newNetworkState;
        boolean newExtraRow = hasExtraRow();
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(super.getItemCount());
            } else {
                notifyItemInserted(super.getItemCount());
            }
        } else if (newExtraRow && !previousState.equals(newNetworkState)) {
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public void updateMessageReply(CommentInteraction newReply, int position) {
        if (position >= 0 && position < super.getItemCount()) {
            CommentInteraction message = getItem(position);
            if (message != null) {
                notifyItemChanged(position);
            }
        }
    }

    public void setMarkAllMessagesAsRead(boolean markAllMessagesAsRead) {
        this.markAllMessagesAsRead = markAllMessagesAsRead;
    }

    public interface RetryLoadingMoreCallback {
        void retryLoadingMore();
    }

    class DataViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.author_text_view_item_message)
        TextView authorTextView;
        @BindView(R.id.subject_text_view_item_message)
        TextView subjectTextView;
        @BindView(R.id.title_text_view_item_message)
        TextView titleTextView;
        @BindView(R.id.content_custom_markwon_view_item_message)
        TextView contentCustomMarkwonView;

        DataViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mActivity.typeface != null) {
                authorTextView.setTypeface(mActivity.typeface);
                subjectTextView.setTypeface(mActivity.typeface);
                titleTextView.setTypeface(mActivity.titleTypeface);
                contentCustomMarkwonView.setTypeface(mActivity.contentTypeface);
            }
            itemView.setBackgroundColor(mMessageBackgroundColor);
            authorTextView.setTextColor(mUsernameColor);
            subjectTextView.setTextColor(mPrimaryTextColor);
            titleTextView.setTextColor(mPrimaryTextColor);
            contentCustomMarkwonView.setTextColor(mSecondaryTextColor);

            contentCustomMarkwonView.setMovementMethod(LinkMovementMethod.getInstance());

            contentCustomMarkwonView.setOnClickListener(view -> {
                if (contentCustomMarkwonView.getSelectionStart() == -1 && contentCustomMarkwonView.getSelectionEnd() == -1) {
                    itemView.performClick();
                }
            });
        }
    }

    class ErrorViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.error_text_view_item_footer_error)
        TextView errorTextView;
        @BindView(R.id.retry_button_item_footer_error)
        Button retryButton;

        ErrorViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (mActivity.typeface != null) {
                errorTextView.setTypeface(mActivity.typeface);
                retryButton.setTypeface(mActivity.typeface);
            }
            errorTextView.setText(R.string.load_comments_failed);
            errorTextView.setTextColor(mSecondaryTextColor);
            retryButton.setOnClickListener(view -> mRetryLoadingMoreCallback.retryLoadingMore());
            retryButton.setBackgroundTintList(ColorStateList.valueOf(mColorPrimaryLightTheme));
            retryButton.setTextColor(mButtonTextColor);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.progress_bar_item_footer_loading)
        ProgressBar progressBar;

        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(mColorAccent));
        }
    }
}
