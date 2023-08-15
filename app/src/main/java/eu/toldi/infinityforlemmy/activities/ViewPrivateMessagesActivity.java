package eu.toldi.infinityforlemmy.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.evernote.android.state.State;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.livefront.bridge.Bridge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.ActivityToolbarInterface;
import eu.toldi.infinityforlemmy.Infinity;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.adapters.PrivateMessagesDetailRecyclerViewAdapter;
import eu.toldi.infinityforlemmy.asynctasks.LoadUserData;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.customviews.LinearLayoutManagerBugFixed;
import eu.toldi.infinityforlemmy.events.PassPrivateMessageEvent;
import eu.toldi.infinityforlemmy.events.PassPrivateMessageIndexEvent;
import eu.toldi.infinityforlemmy.events.RepliedToPrivateMessageEvent;
import eu.toldi.infinityforlemmy.message.ReadMessage;
import eu.toldi.infinityforlemmy.privatemessage.LemmyPrivateMessageAPI;
import eu.toldi.infinityforlemmy.privatemessage.PrivateMessage;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;
import retrofit2.Retrofit;

public class ViewPrivateMessagesActivity extends BaseActivity implements ActivityToolbarInterface {

    public static final String EXTRA_PRIVATE_MESSAGE = "EPM";
    public static final String EXTRA_PRIVATE_MESSAGE_INDEX = "EPM";
    public static final String EXTRA_MESSAGE_POSITION = "EMP";
    private static final String USER_AVATAR_STATE = "UAS";
    @BindView(R.id.coordinator_layout_view_private_messages_activity)
    CoordinatorLayout mCoordinatorLayout;
    @BindView(R.id.appbar_layout_view_private_messages_activity)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.toolbar_view_private_messages_activity)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view_view_private_messages)
    RecyclerView mRecyclerView;
    @BindView(R.id.edit_text_divider_view_private_messages_activity)
    View mDivider;
    @BindView(R.id.edit_text_view_private_messages_activity)
    EditText mEditText;
    @BindView(R.id.send_image_view_view_private_messages_activity)
    ImageView mSendImageView;
    @BindView(R.id.edit_text_wrapper_linear_layout_view_private_messages_activity)
    LinearLayout mEditTextLinearLayout;
    @Inject
    @Named("oauth")
    Retrofit mOauthRetrofit;
    @Inject
    @Named("no_oauth")
    RetrofitHolder mRetrofit;
    @Inject
    RedditDataRoomDatabase mRedditDataRoomDatabase;
    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;
    @Inject
    @Named("current_account")
    SharedPreferences mCurrentAccountSharedPreferences;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;
    @Inject
    Executor mExecutor;

    @Inject
    LemmyPrivateMessageAPI mLemmyPrivateMessageAPI;
    private LinearLayoutManagerBugFixed mLinearLayoutManager;
    private PrivateMessagesDetailRecyclerViewAdapter mAdapter;
    @State
    PrivateMessage privateMessage;
    @State
    PrivateMessage replyTo;
    private String mAccessToken;
    private String mAccountName;

    private String mAccountQualifiedName;
    private String mUserAvatar;
    private ArrayList<ProvideUserAvatarCallback> mProvideUserAvatarCallbacks;
    private boolean isLoadingUserAvatar = false;
    private boolean isSendingMessage = false;
    private int mSecondaryTextColor;
    private int mSendMessageIconColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);

        setImmersiveModeNotApplicable();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_private_messages);

        Bridge.restoreInstanceState(this, savedInstanceState);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        applyCustomTheme();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isChangeStatusBarIconColor()) {
            addOnOffsetChangedListener(mAppBarLayout);
        }

        Intent intent = getIntent();
        privateMessage = intent.getParcelableExtra(EXTRA_PRIVATE_MESSAGE);

        Log.i("ViewPrivate", "privateMessage: " + privateMessage);

        setSupportActionBar(mToolbar);
        setToolbarGoToTop(mToolbar);

        mProvideUserAvatarCallbacks = new ArrayList<>();

        mAccessToken = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCESS_TOKEN, null);
        mAccountName = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCOUNT_NAME, null);
        mAccountQualifiedName = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCOUNT_QUALIFIED_NAME, null);

        if (savedInstanceState != null) {
            mUserAvatar = savedInstanceState.getString(USER_AVATAR_STATE);
            if (privateMessage == null) {
                EventBus.getDefault().post(new PassPrivateMessageIndexEvent(getIntent().getIntExtra(EXTRA_PRIVATE_MESSAGE_INDEX, -1)));
            } else {
                bindView();
            }
        } else {
            if (privateMessage != null) {
                bindView();
            }
            EventBus.getDefault().post(new PassPrivateMessageIndexEvent(getIntent().getIntExtra(EXTRA_PRIVATE_MESSAGE_INDEX, -1)));
        }
    }

    private void bindView() {
        if (privateMessage != null) {
            if (privateMessage.getCreatorQualifiedName().equals(mAccountQualifiedName)) {
                setTitle(privateMessage.getRecipientName());
                mToolbar.setOnClickListener(view -> {

                    Intent intent = new Intent(this, ViewUserDetailActivity.class);
                    intent.putExtra(ViewUserDetailActivity.EXTRA_USER_NAME_KEY, privateMessage.getRecipientName());
                    intent.putExtra(ViewUserDetailActivity.EXTRA_QUALIFIED_USER_NAME_KEY, privateMessage.getRecipientQualifiedName());
                    startActivity(intent);
                });
            } else {
                setTitle(privateMessage.getCreatorName());
                mToolbar.setOnClickListener(view -> {

                    Intent intent = new Intent(this, ViewUserDetailActivity.class);
                    intent.putExtra(ViewUserDetailActivity.EXTRA_USER_NAME_KEY, privateMessage.getCreatorName());
                    intent.putExtra(ViewUserDetailActivity.EXTRA_QUALIFIED_USER_NAME_KEY, privateMessage.getCreatorQualifiedName());
                    startActivity(intent);
                });
            }
        }
        mAdapter = new PrivateMessagesDetailRecyclerViewAdapter(this, mSharedPreferences,
                getResources().getConfiguration().locale, privateMessage, mAccountQualifiedName, mCustomThemeWrapper);
        mLinearLayoutManager = new LinearLayoutManagerBugFixed(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        goToBottom();
        mSendImageView.setOnClickListener(view -> {
            if (!isSendingMessage) {
                if (!mEditText.getText().toString().equals("")) {
                    //Send Message
                    if (privateMessage != null) {
                        List<PrivateMessage> replies = privateMessage.getReplies();
                        if (replyTo == null) {
                            replyTo = privateMessage;
                        }
                        isSendingMessage = true;
                        mSendImageView.setColorFilter(mSecondaryTextColor, android.graphics.PorterDuff.Mode.SRC_IN);

                        mLemmyPrivateMessageAPI.sendPrivateMessage(mAccessToken, replyTo.getCreatorId(), mEditText.getText().toString(), new LemmyPrivateMessageAPI.PrivateMessageSentListener() {

                            @Override
                            public void onPrivateMessageSentSuccess(@NonNull PrivateMessage privateMessage) {
                                if (mAdapter != null) {
                                    mAdapter.addReply(privateMessage);
                                }
                                goToBottom();
                                mEditText.setText("");
                                isSendingMessage = false;
                                EventBus.getDefault().post(new RepliedToPrivateMessageEvent(privateMessage, getIntent().getIntExtra(EXTRA_MESSAGE_POSITION, -1)));
                            }

                            @Override
                            public void onPrivateMessageSentError() {
                                Snackbar.make(mCoordinatorLayout, R.string.reply_message_failed, Snackbar.LENGTH_LONG).show();
                                mSendImageView.setColorFilter(mSendMessageIconColor, android.graphics.PorterDuff.Mode.SRC_IN);
                                isSendingMessage = false;
                            }
                        });


                        StringBuilder fullnames = new StringBuilder();

                        if (replies != null && !replies.isEmpty()) {
                            for (PrivateMessage m : replies) {
                                if (!m.getRead()) {
                                    fullnames.append(m).append(",");
                                }
                            }
                        }
                        if (fullnames.length() > 0) {
                            fullnames.deleteCharAt(fullnames.length() - 1);
                            ReadMessage.readMessage(mOauthRetrofit, mAccessToken, 0,
                                    new ReadMessage.ReadMessageListener() {
                                        @Override
                                        public void readSuccess() {
                                        }

                                        @Override
                                        public void readFailed() {
                                        }
                                    });
                        }
                    }
                }
            }
        });
    }

    public void fetchUserAvatar(String username, ProvideUserAvatarCallback provideUserAvatarCallback) {
        if (mUserAvatar == null) {
            mProvideUserAvatarCallbacks.add(provideUserAvatarCallback);
            if (!isLoadingUserAvatar) {
                LoadUserData.loadUserData(mExecutor, new Handler(), mRedditDataRoomDatabase,
                        username, mRetrofit.getRetrofit(), iconImageUrl -> {
                    isLoadingUserAvatar = false;
                    mUserAvatar = iconImageUrl == null ? "" : iconImageUrl;
                    for (ProvideUserAvatarCallback provideUserAvatarCallbackInArrayList : mProvideUserAvatarCallbacks) {
                        provideUserAvatarCallbackInArrayList.fetchAvatarSuccess(iconImageUrl);
                    }
                    mProvideUserAvatarCallbacks.clear();
                });
            }
        } else {
            provideUserAvatarCallback.fetchAvatarSuccess(mUserAvatar);
        }
    }

    public void delayTransition() {
        TransitionManager.beginDelayedTransition(mRecyclerView, new AutoTransition());
    }

    private void goToBottom() {
        if (mLinearLayoutManager != null && mAdapter != null) {
            mLinearLayoutManager.scrollToPositionWithOffset(mAdapter.getItemCount() - 1, 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(USER_AVATAR_STATE, mUserAvatar);
        Bridge.saveInstanceState(this, outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Bridge.clear(this);
    }

    @Override
    protected SharedPreferences getDefaultSharedPreferences() {
        return mSharedPreferences;
    }

    @Override
    protected CustomThemeWrapper getCustomThemeWrapper() {
        return mCustomThemeWrapper;
    }

    @Override
    protected void applyCustomTheme() {
        mCoordinatorLayout.setBackgroundColor(mCustomThemeWrapper.getBackgroundColor());
        applyAppBarLayoutAndCollapsingToolbarLayoutAndToolbarTheme(mAppBarLayout, null, mToolbar);
        mDivider.setBackgroundColor(mCustomThemeWrapper.getDividerColor());
        mEditText.setTextColor(mCustomThemeWrapper.getPrimaryTextColor());
        mSecondaryTextColor = mCustomThemeWrapper.getSecondaryTextColor();
        mEditText.setHintTextColor(mSecondaryTextColor);
        mEditTextLinearLayout.setBackgroundColor(mCustomThemeWrapper.getBackgroundColor());
        mSendMessageIconColor = mCustomThemeWrapper.getSendMessageIconColor();
        mSendImageView.setColorFilter(mSendMessageIconColor, android.graphics.PorterDuff.Mode.SRC_IN);
        if (typeface != null) {
            mEditText.setTypeface(typeface);
        }
    }

    @Override
    public void onLongPress() {
        if (mLinearLayoutManager != null) {
            mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
        }
    }

    @Subscribe
    public void onPassPrivateMessageEvent(PassPrivateMessageEvent passPrivateMessageEvent) {
       /* privateMessage = passPrivateMessageEvent.message;
        if (privateMessage != null) {
            if (privateMessage.getAuthor().equals(mAccountName)) {
                if (privateMessage.getReplies() != null) {
                    for (int i = privateMessage.getReplies().size() - 1; i >= 0; i--) {
                        if (!privateMessage.getReplies().get(i).getAuthor().equals(mAccountName)) {
                            replyTo = privateMessage.getReplies().get(i);
                            break;
                        }
                    }
                }
                if (replyTo == null) {
                    replyTo = privateMessage;
                }
            } else {
                replyTo = privateMessage;
            }

            bindView();
        }*/
    }

    public interface ProvideUserAvatarCallback {
        void fetchAvatarSuccess(String userAvatarUrl);
    }
}