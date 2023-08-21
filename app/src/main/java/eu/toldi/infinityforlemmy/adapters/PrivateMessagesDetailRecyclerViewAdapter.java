package eu.toldi.infinityforlemmy.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spanned;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.activities.LinkResolverActivity;
import eu.toldi.infinityforlemmy.activities.ViewPrivateMessagesActivity;
import eu.toldi.infinityforlemmy.activities.ViewUserDetailActivity;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.markdown.ClickableGlideImagesPlugin;
import eu.toldi.infinityforlemmy.markdown.RedditHeadingPlugin;
import eu.toldi.infinityforlemmy.markdown.SpoilerAwareMovementMethod;
import eu.toldi.infinityforlemmy.markdown.SpoilerParserPlugin;
import eu.toldi.infinityforlemmy.markdown.SuperscriptPlugin;
import eu.toldi.infinityforlemmy.privatemessage.PrivateMessage;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;
import eu.toldi.infinityforlemmy.utils.Utils;
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
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PrivateMessagesDetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 0;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 1;
    private PrivateMessage mMessage;
    private ViewPrivateMessagesActivity mViewPrivateMessagesActivity;
    private RequestManager mGlide;
    private Locale mLocale;
    private String mAccountName;
    private Markwon mMarkwon;
    private boolean mShowElapsedTime;
    private String mTimeFormatPattern;
    private int mSecondaryTextColor;
    private int mReceivedMessageTextColor;
    private int mSentMessageTextColor;
    private int mReceivedMessageBackgroundColor;
    private int mSentMessageBackgroundColor;

    public PrivateMessagesDetailRecyclerViewAdapter(ViewPrivateMessagesActivity viewPrivateMessagesActivity,
                                                    SharedPreferences sharedPreferences, Locale locale,
                                                    PrivateMessage message, String accountName,
                                                    CustomThemeWrapper customThemeWrapper) {
        mMessage = message;
        mViewPrivateMessagesActivity = viewPrivateMessagesActivity;
        mGlide = Glide.with(viewPrivateMessagesActivity);
        mLocale = locale;
        mAccountName = accountName;
        int commentColor = customThemeWrapper.getCommentColor();
        // todo:https://github.com/Docile-Alligator/Infinity-For-Reddit/issues/1027
        //  add tables support and replace with MarkdownUtils#commonPostMarkwonBuilder
        mMarkwon = Markwon.builder(viewPrivateMessagesActivity)
                .usePlugin(MarkwonInlineParserPlugin.create(plugin -> {
                    plugin.excludeInlineProcessor(HtmlInlineProcessor.class);
                }))
                .usePlugin(new AbstractMarkwonPlugin() {
                    @Override
                    public void beforeSetText(@NonNull TextView textView, @NonNull Spanned markdown) {
                        if (mViewPrivateMessagesActivity.contentTypeface != null) {
                            textView.setTypeface(mViewPrivateMessagesActivity.contentTypeface);
                        }
                    }

                    @Override
                    public void configureConfiguration(@NonNull MarkwonConfiguration.Builder builder) {
                        builder.linkResolver((view, link) -> {
                            Intent intent = new Intent(viewPrivateMessagesActivity, LinkResolverActivity.class);
                            Uri uri = Uri.parse(link);
                            intent.setData(uri);
                            viewPrivateMessagesActivity.startActivity(intent);
                        });
                    }

                    @Override
                    public void configureTheme(@NonNull MarkwonTheme.Builder builder) {
                        builder.linkColor(customThemeWrapper.getLinkColor());
                    }
                })
                .usePlugin(GlideImagesPlugin.create(viewPrivateMessagesActivity))
                .usePlugin(ClickableGlideImagesPlugin.create(viewPrivateMessagesActivity))
                .usePlugin(SuperscriptPlugin.create())
                .usePlugin(StrikethroughPlugin.create())
                .usePlugin(SpoilerParserPlugin.create(commentColor, commentColor | 0xFF000000))
                .usePlugin(RedditHeadingPlugin.create())
                .usePlugin(MovementMethodPlugin.create(new SpoilerAwareMovementMethod()))
                .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
                .build();
        mShowElapsedTime = sharedPreferences.getBoolean(SharedPreferencesUtils.SHOW_ELAPSED_TIME_KEY, false);
        mTimeFormatPattern = sharedPreferences.getString(SharedPreferencesUtils.TIME_FORMAT_KEY, SharedPreferencesUtils.TIME_FORMAT_DEFAULT_VALUE);
        mSecondaryTextColor = customThemeWrapper.getSecondaryTextColor();
        mReceivedMessageTextColor = customThemeWrapper.getReceivedMessageTextColor();
        mSentMessageTextColor = customThemeWrapper.getSentMessageTextColor();
        mReceivedMessageBackgroundColor = customThemeWrapper.getReceivedMessageBackgroundColor();
        mSentMessageBackgroundColor = customThemeWrapper.getSentMessageBackgroundColor();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return mMessage.getCreatorQualifiedName().equals(mAccountName) ? VIEW_TYPE_MESSAGE_SENT : VIEW_TYPE_MESSAGE_RECEIVED;
        } else {
            return mMessage.getReplies().get(position - 1).getCreatorQualifiedName().equals(mAccountName) ? VIEW_TYPE_MESSAGE_SENT : VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            return new SentMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_private_message_sent, parent, false));
        } else {
            return new ReceivedMessageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_private_message_received, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PrivateMessage message;
        if (holder.getBindingAdapterPosition() == 0) {
            message = mMessage;
        } else {
            message = mMessage.getReplies().get(holder.getBindingAdapterPosition() - 1);
        }
        if (message != null) {
            if (holder instanceof MessageViewHolder) {
                mMarkwon.setMarkdown(((MessageViewHolder) holder).messageTextView, message.getContent());

                if (mShowElapsedTime) {
                    ((MessageViewHolder) holder).timeTextView.setText(Utils.getElapsedTime(mViewPrivateMessagesActivity, message.getPublished()));
                } else {
                    ((MessageViewHolder) holder).timeTextView.setText(Utils.getFormattedTime(mLocale, message.getPublished(), mTimeFormatPattern));
                }
            }

            if (holder instanceof SentMessageViewHolder) {
                ((SentMessageViewHolder) holder).messageTextView.setBackground(Utils.getTintedDrawable(mViewPrivateMessagesActivity,
                        R.drawable.private_message_ballon, mSentMessageBackgroundColor));
            } else if (holder instanceof ReceivedMessageViewHolder) {
                if (!message.getCreatorAvatar().equals("")) {
                    mGlide.load(message.getCreatorAvatar())
                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                            .error(mGlide.load(R.drawable.subreddit_default_icon)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                            .into(((ReceivedMessageViewHolder) holder).userAvatarImageView);
                } else {
                    mViewPrivateMessagesActivity.fetchUserAvatar(message.getCreatorQualifiedName(), userAvatarUrl -> {
                        if (userAvatarUrl == null || userAvatarUrl.equals("")) {
                            mGlide.load(R.drawable.subreddit_default_icon)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                    .into(((ReceivedMessageViewHolder) holder).userAvatarImageView);
                        } else {
                            mGlide.load(userAvatarUrl)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                                    .error(mGlide.load(R.drawable.subreddit_default_icon)
                                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                                    .into(((ReceivedMessageViewHolder) holder).userAvatarImageView);
                        }
                    });
                }

                ((ReceivedMessageViewHolder) holder).userAvatarImageView.setOnClickListener(view -> {

                    Intent intent = new Intent(mViewPrivateMessagesActivity, ViewUserDetailActivity.class);
                    intent.putExtra(ViewUserDetailActivity.EXTRA_USER_NAME_KEY, message.getCreatorName());
                    intent.putExtra(ViewUserDetailActivity.EXTRA_QUALIFIED_USER_NAME_KEY, message.getCreatorQualifiedName());
                    mViewPrivateMessagesActivity.startActivity(intent);
                });

                ((ReceivedMessageViewHolder) holder).messageTextView.setBackground(
                        Utils.getTintedDrawable(mViewPrivateMessagesActivity,
                                R.drawable.private_message_ballon, mReceivedMessageBackgroundColor));
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mMessage == null) {
            return 0;
        } else if (mMessage.getReplies() == null) {
            return 1;
        } else {
            return 1 + mMessage.getReplies().size();
        }
    }

    public void setMessage(PrivateMessage message) {
        mMessage = message;
        notifyDataSetChanged();
    }

    public void addReply(PrivateMessage reply) {
        int currentSize = getItemCount();

        if (mMessage != null) {
            mMessage.addReply(reply);
        } else {
            mMessage = reply;
        }

        notifyItemInserted(currentSize);
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof MessageViewHolder) {
            ((MessageViewHolder) holder).messageTextView.setBackground(null);
            ((MessageViewHolder) holder).timeTextView.setVisibility(View.GONE);
        }
        if (holder instanceof ReceivedMessageViewHolder) {
            mGlide.clear(((ReceivedMessageViewHolder) holder).userAvatarImageView);
        }
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timeTextView;
        ImageView copyImageView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setBaseView(TextView messageTextView, TextView timeTextView, ImageView copyImageView) {
            this.messageTextView = messageTextView;
            this.timeTextView = timeTextView;
            this.copyImageView = copyImageView;

            messageTextView.setTextColor(Color.WHITE);
            timeTextView.setTextColor(mSecondaryTextColor);

            itemView.setOnClickListener(view -> {
                if (timeTextView.getVisibility() != View.VISIBLE) {
                    timeTextView.setVisibility(View.VISIBLE);
                    copyImageView.setVisibility(View.VISIBLE);
                } else {
                    timeTextView.setVisibility(View.GONE);
                    copyImageView.setVisibility(View.GONE);
                }
                mViewPrivateMessagesActivity.delayTransition();
            });

            messageTextView.setOnClickListener(view -> {
                if (messageTextView.getSelectionStart() == -1 && messageTextView.getSelectionEnd() == -1) {
                    itemView.performClick();
                }
            });

            copyImageView.setColorFilter(mSecondaryTextColor, android.graphics.PorterDuff.Mode.SRC_IN);

            copyImageView.setOnClickListener(view -> {
                PrivateMessage message;
                if (getBindingAdapterPosition() == 0) {
                    message = mMessage;
                } else {
                    message = mMessage.getReplies().get(getBindingAdapterPosition() - 1);
                }
                if (message != null) {
                    ClipboardManager clipboard = (ClipboardManager) mViewPrivateMessagesActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard != null) {
                        ClipData clip = ClipData.newPlainText("simple text", message.getContent());
                        clipboard.setPrimaryClip(clip);
                        if (android.os.Build.VERSION.SDK_INT < 33) {
                            Toast.makeText(mViewPrivateMessagesActivity, R.string.copy_success, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mViewPrivateMessagesActivity, R.string.copy_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    class SentMessageViewHolder extends MessageViewHolder {
        @BindView(R.id.message_text_view_item_private_message_sent)
        TextView messageTextView;
        @BindView(R.id.time_text_view_item_private_message_sent)
        TextView timeTextView;
        @BindView(R.id.copy_image_view_item_private_message_sent)
        ImageView copyImageView;

        SentMessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setBaseView(messageTextView, timeTextView, copyImageView);

            messageTextView.setTextColor(mSentMessageTextColor);
        }
    }

    class ReceivedMessageViewHolder extends MessageViewHolder {
        @BindView(R.id.avatar_image_view_item_private_message_received)
        ImageView userAvatarImageView;
        @BindView(R.id.message_text_view_item_private_message_received)
        TextView messageTextView;
        @BindView(R.id.time_text_view_item_private_message_received)
        TextView timeTextView;
        @BindView(R.id.copy_image_view_item_private_message_received)
        ImageView copyImageView;

        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setBaseView(messageTextView, timeTextView, copyImageView);

            messageTextView.setTextColor(mReceivedMessageTextColor);
        }
    }
}
