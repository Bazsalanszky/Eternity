package eu.toldi.infinityforlemmy.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.snackbar.Snackbar;
import com.libRG.CustomTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.Flair;
import eu.toldi.infinityforlemmy.Infinity;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.RedditDataRoomDatabase;
import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.UploadImageEnabledActivity;
import eu.toldi.infinityforlemmy.UploadedImage;
import eu.toldi.infinityforlemmy.account.Account;
import eu.toldi.infinityforlemmy.adapters.MarkdownBottomBarRecyclerViewAdapter;
import eu.toldi.infinityforlemmy.asynctasks.LoadSubredditIcon;
import eu.toldi.infinityforlemmy.bottomsheetfragments.AccountChooserBottomSheetFragment;
import eu.toldi.infinityforlemmy.bottomsheetfragments.FlairBottomSheetFragment;
import eu.toldi.infinityforlemmy.bottomsheetfragments.UploadedImagesBottomSheetFragment;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.customviews.LinearLayoutManagerBugFixed;
import eu.toldi.infinityforlemmy.events.SubmitTextOrLinkPostEvent;
import eu.toldi.infinityforlemmy.events.SwitchAccountEvent;
import eu.toldi.infinityforlemmy.services.SubmitPostService;
import eu.toldi.infinityforlemmy.subreddit.FetchSubredditData;
import eu.toldi.infinityforlemmy.subreddit.SubredditData;
import eu.toldi.infinityforlemmy.subscribedsubreddit.SubscribedSubredditData;
import eu.toldi.infinityforlemmy.utils.APIUtils;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;
import eu.toldi.infinityforlemmy.utils.Utils;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Retrofit;

public class PostTextActivity extends BaseActivity implements FlairBottomSheetFragment.FlairSelectionCallback,
        UploadImageEnabledActivity, AccountChooserBottomSheetFragment.AccountChooserListener {
    static final String EXTRA_SUBREDDIT_NAME = "ESN";
    static final String EXTRA_CONTENT = "EC";

    private static final String SELECTED_ACCOUNT_STATE = "SAS";
    private static final String SUBREDDIT_NAME_STATE = "SNS";
    private static final String SUBREDDIT_ICON_STATE = "SIS";
    private static final String SUBREDDIT_SELECTED_STATE = "SSS";
    private static final String SUBREDDIT_IS_USER_STATE = "SIUS";
    private static final String LOAD_SUBREDDIT_ICON_STATE = "LSIS";
    private static final String IS_POSTING_STATE = "IPS";
    private static final String FLAIR_STATE = "FS";
    private static final String IS_SPOILER_STATE = "ISS";
    private static final String IS_NSFW_STATE = "INS";
    private static final String UPLOADED_IMAGES_STATE = "UIS";

    private static final int SUBREDDIT_SELECTION_REQUEST_CODE = 0;
    private static final int PICK_IMAGE_REQUEST_CODE = 100;
    private static final int CAPTURE_IMAGE_REQUEST_CODE = 200;
    private static final int MARKDOWN_PREVIEW_REQUEST_CODE = 300;
    private static final String COMMUNITY_DATA_STATE = "CDS";

    @BindView(R.id.coordinator_layout_post_text_activity)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_layout_post_text_activity)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_post_text_activity)
    Toolbar toolbar;
    @BindView(R.id.account_linear_layout_post_text_activity)
    LinearLayout accountLinearLayout;
    @BindView(R.id.account_icon_gif_image_view_post_text_activity)
    GifImageView accountIconImageView;
    @BindView(R.id.account_name_text_view_post_text_activity)
    TextView accountNameTextView;
    @BindView(R.id.subreddit_icon_gif_image_view_post_text_activity)
    GifImageView iconGifImageView;
    @BindView(R.id.subreddit_name_text_view_post_text_activity)
    TextView subredditNameTextView;
    @BindView(R.id.rules_button_post_text_activity)
    MaterialButton rulesButton;
    @BindView(R.id.divider_1_post_text_activity)
    MaterialDivider divider1;
    @BindView(R.id.nsfw_custom_text_view_post_text_activity)
    CustomTextView nsfwTextView;
    @BindView(R.id.receive_post_reply_notifications_linear_layout_post_text_activity)
    LinearLayout receivePostReplyNotificationsLinearLayout;
    @BindView(R.id.receive_post_reply_notifications_text_view_post_text_activity)
    TextView receivePostReplyNotificationsTextView;
    @BindView(R.id.receive_post_reply_notifications_switch_material_post_text_activity)
    MaterialSwitch receivePostReplyNotificationsSwitchMaterial;
    @BindView(R.id.divider_2_post_text_activity)
    MaterialDivider divider2;
    @BindView(R.id.post_title_edit_text_post_text_activity)
    EditText titleEditText;
    @BindView(R.id.post_text_content_edit_text_post_text_activity)
    EditText contentEditText;
    @BindView(R.id.markdown_bottom_bar_recycler_view_post_text_activity)
    RecyclerView markdownBottomBarRecyclerView;
    @Inject
    @Named("no_oauth")
    RetrofitHolder mRetrofit;
    @Inject
    @Named("oauth")
    Retrofit mOauthRetrofit;
    @Inject
    @Named("upload_media")
    Retrofit mUploadMediaRetrofit;
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
    private Account selectedAccount;
    private String mAccessToken;
    private String iconUrl;
    private String subredditName;
    private SubscribedSubredditData communityData;
    private boolean subredditSelected = false;
    private boolean subredditIsUser;
    private boolean loadSubredditIconSuccessful = true;
    private boolean isPosting;
    private int primaryTextColor;
    private int flairBackgroundColor;
    private int flairTextColor;
    private int spoilerBackgroundColor;
    private int spoilerTextColor;
    private int nsfwBackgroundColor;
    private int nsfwTextColor;
    private Flair flair;
    private boolean isSpoiler = false;
    private boolean isNSFW = false;
    private Resources resources;
    private Menu mMenu;
    private RequestManager mGlide;
    private FlairBottomSheetFragment flairSelectionBottomSheetFragment;
    private Snackbar mPostingSnackbar;
    private Uri capturedImageUri;
    private ArrayList<UploadedImage> uploadedImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);

        setImmersiveModeNotApplicable();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post_text);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        applyCustomTheme();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isChangeStatusBarIconColor()) {
            addOnOffsetChangedListener(appBarLayout);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mGlide = Glide.with(getApplication());

        mPostingSnackbar = Snackbar.make(coordinatorLayout, R.string.posting, Snackbar.LENGTH_INDEFINITE);

        resources = getResources();

        mAccessToken = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCESS_TOKEN, null);

        if (savedInstanceState != null) {
            selectedAccount = savedInstanceState.getParcelable(SELECTED_ACCOUNT_STATE);
            communityData = savedInstanceState.getParcelable(COMMUNITY_DATA_STATE);
            subredditName = savedInstanceState.getString(SUBREDDIT_NAME_STATE);
            iconUrl = savedInstanceState.getString(SUBREDDIT_ICON_STATE);
            subredditSelected = savedInstanceState.getBoolean(SUBREDDIT_SELECTED_STATE);
            subredditIsUser = savedInstanceState.getBoolean(SUBREDDIT_IS_USER_STATE);
            loadSubredditIconSuccessful = savedInstanceState.getBoolean(LOAD_SUBREDDIT_ICON_STATE);
            isPosting = savedInstanceState.getBoolean(IS_POSTING_STATE);
            flair = savedInstanceState.getParcelable(FLAIR_STATE);
            isSpoiler = savedInstanceState.getBoolean(IS_SPOILER_STATE);
            isNSFW = savedInstanceState.getBoolean(IS_NSFW_STATE);
            uploadedImages = savedInstanceState.getParcelableArrayList(UPLOADED_IMAGES_STATE);

            if (selectedAccount != null) {
                mGlide.load(selectedAccount.getProfileImageUrl())
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .error(mGlide.load(R.drawable.subreddit_default_icon)
                                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                        .into(accountIconImageView);

                accountNameTextView.setText(selectedAccount.getAccountName());
            } else {
                loadCurrentAccount();
            }

            if (subredditName != null) {
                subredditNameTextView.setTextColor(primaryTextColor);
                subredditNameTextView.setText(subredditName);
                if (!loadSubredditIconSuccessful) {
                    loadSubredditIcon();
                }
            }
            displaySubredditIcon();

            if (isPosting) {
                mPostingSnackbar.show();
            }

            if (isNSFW) {
                nsfwTextView.setBackgroundColor(nsfwBackgroundColor);
                nsfwTextView.setBorderColor(nsfwBackgroundColor);
                nsfwTextView.setTextColor(nsfwTextColor);
            }
        } else {
            isPosting = false;

            loadCurrentAccount();

            if (getIntent().hasExtra(EXTRA_SUBREDDIT_NAME)) {
                loadSubredditIconSuccessful = false;
                subredditName = getIntent().getStringExtra(EXTRA_SUBREDDIT_NAME);
                FetchSubredditData.fetchSubredditData(mRetrofit.getRetrofit(), subredditName, mAccessToken, new FetchSubredditData.FetchSubredditDataListener() {
                    @Override
                    public void onFetchSubredditDataSuccess(SubredditData subredditData, int nCurrentOnlineSubscribers) {
                        {
                            communityData = new SubscribedSubredditData(subredditData);
                            subredditName = communityData.getName();
                            subredditIsUser = false;
                            subredditSelected = true;
                            subredditNameTextView.setTextColor(primaryTextColor);
                            subredditNameTextView.setText(subredditName);
                            loadSubredditIcon();
                        }
                    }

                    @Override
                    public void onFetchSubredditDataFail(boolean isQuarantined) {
                        finish();
                    }
                });
            } else {
                mGlide.load(R.drawable.subreddit_default_icon)
                        .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                        .into(iconGifImageView);
            }

            String text = getIntent().getStringExtra(EXTRA_CONTENT);
            if (text != null) {
                contentEditText.setText(text);
            }
        }

        accountLinearLayout.setOnClickListener(view -> {
            AccountChooserBottomSheetFragment fragment = new AccountChooserBottomSheetFragment();
            fragment.show(getSupportFragmentManager(), fragment.getTag());
        });

        iconGifImageView.setOnClickListener(view -> subredditNameTextView.performClick());

        subredditNameTextView.setOnClickListener(view -> {
            Intent intent = new Intent(this, SubredditSelectionActivity.class);
            intent.putExtra(SubredditSelectionActivity.EXTRA_SPECIFIED_ACCOUNT, selectedAccount);
            startActivityForResult(intent, SUBREDDIT_SELECTION_REQUEST_CODE);
        });

        rulesButton.setOnClickListener(view -> {
            if (subredditName == null) {
                Snackbar.make(coordinatorLayout, R.string.select_a_community, Snackbar.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, RulesActivity.class);
                if (subredditIsUser) {
                    intent.putExtra(RulesActivity.EXTRA_SUBREDDIT_NAME, "u_" + subredditName);
                } else {
                    intent.putExtra(RulesActivity.EXTRA_SUBREDDIT_NAME, communityData.getQualified_name());
                }
                startActivity(intent);
            }
        });


        nsfwTextView.setOnClickListener(view -> {
            if (!isNSFW) {
                nsfwTextView.setBackgroundColor(nsfwBackgroundColor);
                nsfwTextView.setBorderColor(nsfwBackgroundColor);
                nsfwTextView.setTextColor(nsfwTextColor);
                isNSFW = true;
            } else {
                nsfwTextView.setBackgroundColor(resources.getColor(android.R.color.transparent));
                nsfwTextView.setTextColor(primaryTextColor);
                isNSFW = false;
            }
        });

        receivePostReplyNotificationsLinearLayout.setOnClickListener(view -> {
            receivePostReplyNotificationsSwitchMaterial.performClick();
        });

        MarkdownBottomBarRecyclerViewAdapter adapter = new MarkdownBottomBarRecyclerViewAdapter(
                mCustomThemeWrapper, new MarkdownBottomBarRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onClick(int item) {
                MarkdownBottomBarRecyclerViewAdapter.bindEditTextWithItemClickListener(
                        PostTextActivity.this, contentEditText, item);
            }

            @Override
            public void onUploadImage() {
                Utils.hideKeyboard(PostTextActivity.this);
                UploadedImagesBottomSheetFragment fragment = new UploadedImagesBottomSheetFragment();
                Bundle arguments = new Bundle();
                arguments.putParcelableArrayList(UploadedImagesBottomSheetFragment.EXTRA_UPLOADED_IMAGES,
                        uploadedImages);
                fragment.setArguments(arguments);
                fragment.show(getSupportFragmentManager(), fragment.getTag());
            }
        });

        markdownBottomBarRecyclerView.setLayoutManager(new LinearLayoutManagerBugFixed(this,
                LinearLayoutManager.HORIZONTAL, false));
        markdownBottomBarRecyclerView.setAdapter(adapter);
    }

    private void loadCurrentAccount() {
        Handler handler = new Handler();
        mExecutor.execute(() -> {
            Account account = mRedditDataRoomDatabase.accountDao().getCurrentAccount();
            selectedAccount = account;
            handler.post(() -> {
                if (!isFinishing() && !isDestroyed() && account != null) {
                    mGlide.load(account.getProfileImageUrl())
                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                            .error(mGlide.load(R.drawable.subreddit_default_icon)
                                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                            .into(accountIconImageView);

                    accountNameTextView.setText(account.getAccountName());
                }
            });
        });
    }

    @Override
    public SharedPreferences getDefaultSharedPreferences() {
        return mSharedPreferences;
    }

    @Override
    protected CustomThemeWrapper getCustomThemeWrapper() {
        return mCustomThemeWrapper;
    }

    @Override
    protected void applyCustomTheme() {
        coordinatorLayout.setBackgroundColor(mCustomThemeWrapper.getBackgroundColor());
        applyAppBarLayoutAndCollapsingToolbarLayoutAndToolbarTheme(appBarLayout, null, toolbar);
        primaryTextColor = mCustomThemeWrapper.getPrimaryTextColor();
        accountNameTextView.setTextColor(primaryTextColor);
        int secondaryTextColor = mCustomThemeWrapper.getSecondaryTextColor();
        subredditNameTextView.setTextColor(secondaryTextColor);
        rulesButton.setTextColor(mCustomThemeWrapper.getButtonTextColor());
        rulesButton.setBackgroundColor(mCustomThemeWrapper.getColorPrimaryLightTheme());
        receivePostReplyNotificationsTextView.setTextColor(primaryTextColor);
        int dividerColor = mCustomThemeWrapper.getDividerColor();
        divider1.setDividerColor(dividerColor);
        divider2.setDividerColor(dividerColor);
        flairBackgroundColor = mCustomThemeWrapper.getFlairBackgroundColor();
        flairTextColor = mCustomThemeWrapper.getFlairTextColor();
        spoilerBackgroundColor = mCustomThemeWrapper.getSpoilerBackgroundColor();
        spoilerTextColor = mCustomThemeWrapper.getSpoilerTextColor();
        nsfwBackgroundColor = mCustomThemeWrapper.getNsfwBackgroundColor();
        nsfwTextColor = mCustomThemeWrapper.getNsfwTextColor();
        nsfwTextView.setTextColor(primaryTextColor);
        titleEditText.setTextColor(primaryTextColor);
        titleEditText.setHintTextColor(secondaryTextColor);
        contentEditText.setTextColor(primaryTextColor);
        contentEditText.setHintTextColor(secondaryTextColor);
        if (typeface != null) {
            subredditNameTextView.setTypeface(typeface);
            rulesButton.setTypeface(typeface);
            receivePostReplyNotificationsTextView.setTypeface(typeface);
            nsfwTextView.setTypeface(typeface);
            titleEditText.setTypeface(typeface);
        }
        if (contentTypeface != null) {
            contentEditText.setTypeface(typeface);
        }
    }

    private void displaySubredditIcon() {
        if (iconUrl != null && !iconUrl.equals("")) {
            mGlide.load(iconUrl)
                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                    .error(mGlide.load(R.drawable.subreddit_default_icon)
                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                    .into(iconGifImageView);
        } else {
            mGlide.load(R.drawable.subreddit_default_icon)
                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                    .into(iconGifImageView);
        }
    }

    private void loadSubredditIcon() {
        LoadSubredditIcon.loadSubredditIcon(mExecutor, new Handler(), mRedditDataRoomDatabase, communityData.getQualified_name(),
                mAccessToken, mRetrofit.getRetrofit(), iconImageUrl -> {
                    iconUrl = iconImageUrl;
                    displaySubredditIcon();
                    loadSubredditIconSuccessful = true;
                });
    }

    private void promptAlertDialog(int titleResId, int messageResId) {
        new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogTheme)
                .setTitle(titleResId)
                .setMessage(messageResId)
                .setPositiveButton(R.string.discard_dialog_button, (dialogInterface, i)
                        -> finish())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_text_activity, menu);
        applyMenuItemTheme(menu);
        mMenu = menu;
        if (isPosting) {
            mMenu.findItem(R.id.action_send_post_text_activity).setEnabled(false);
            mMenu.findItem(R.id.action_send_post_text_activity).getIcon().setAlpha(130);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            if (isPosting) {
                promptAlertDialog(R.string.exit_when_submit, R.string.exit_when_submit_post_detail);
                return true;
            } else {
                if (!titleEditText.getText().toString().equals("") || !contentEditText.getText().toString().equals("")) {
                    promptAlertDialog(R.string.discard, R.string.discard_detail);
                    return true;
                }
            }
            finish();
            return true;
        } else if (itemId == R.id.action_preview_post_text_activity) {
            Intent intent = new Intent(this, FullMarkdownActivity.class);
            intent.putExtra(FullMarkdownActivity.EXTRA_COMMENT_MARKDOWN, contentEditText.getText().toString());
            intent.putExtra(FullMarkdownActivity.EXTRA_SUBMIT_POST, true);
            startActivityForResult(intent, MARKDOWN_PREVIEW_REQUEST_CODE);
        } else if (itemId == R.id.action_send_post_text_activity) {
            submitPost(item);
            return true;
        }

        return false;
    }

    private void submitPost(MenuItem item) {
        if (!subredditSelected) {
            Snackbar.make(coordinatorLayout, R.string.select_a_community, Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (titleEditText.getText() == null || titleEditText.getText().toString().equals("")) {
            Snackbar.make(coordinatorLayout, R.string.title_required, Snackbar.LENGTH_SHORT).show();
            return;
        }

        isPosting = true;

        if (item != null) {
            item.setEnabled(false);
            item.getIcon().setAlpha(130);
        }

        mPostingSnackbar.show();


        Intent intent = new Intent(this, SubmitPostService.class);
        intent.putExtra(SubmitPostService.EXTRA_ACCOUNT, selectedAccount);
        intent.putExtra(SubmitPostService.EXTRA_SUBREDDIT_NAME, communityData.getId());
        intent.putExtra(SubmitPostService.EXTRA_TITLE, titleEditText.getText().toString());
        intent.putExtra(SubmitPostService.EXTRA_BODY, contentEditText.getText().toString());
        intent.putExtra(SubmitPostService.EXTRA_KIND, APIUtils.KIND_SELF);
        intent.putExtra(SubmitPostService.EXTRA_FLAIR, flair);
        intent.putExtra(SubmitPostService.EXTRA_IS_SPOILER, isSpoiler);
        intent.putExtra(SubmitPostService.EXTRA_IS_NSFW, isNSFW);
        intent.putExtra(SubmitPostService.EXTRA_RECEIVE_POST_REPLY_NOTIFICATIONS, receivePostReplyNotificationsSwitchMaterial.isChecked());
        intent.putExtra(SubmitPostService.EXTRA_POST_TYPE, SubmitPostService.EXTRA_POST_TEXT_OR_LINK);
        ContextCompat.startForegroundService(this, intent);
    }

    @Override
    public void onBackPressed() {
        if (isPosting) {
            promptAlertDialog(R.string.exit_when_submit, R.string.exit_when_submit_post_detail);
        } else {
            if (!titleEditText.getText().toString().equals("") || !contentEditText.getText().toString().equals("")) {
                promptAlertDialog(R.string.discard, R.string.discard_detail);
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(COMMUNITY_DATA_STATE, communityData);
        outState.putParcelable(SELECTED_ACCOUNT_STATE, selectedAccount);
        outState.putString(SUBREDDIT_NAME_STATE, subredditName);
        outState.putString(SUBREDDIT_ICON_STATE, iconUrl);
        outState.putBoolean(SUBREDDIT_SELECTED_STATE, subredditSelected);
        outState.putBoolean(SUBREDDIT_IS_USER_STATE, subredditIsUser);
        outState.putBoolean(LOAD_SUBREDDIT_ICON_STATE, loadSubredditIconSuccessful);
        outState.putBoolean(IS_POSTING_STATE, isPosting);
        outState.putParcelable(FLAIR_STATE, flair);
        outState.putBoolean(IS_SPOILER_STATE, isSpoiler);
        outState.putBoolean(IS_NSFW_STATE, isNSFW);
        outState.putParcelableArrayList(UPLOADED_IMAGES_STATE, uploadedImages);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SUBREDDIT_SELECTION_REQUEST_CODE) {
                communityData = data.getParcelableExtra(SubredditSelectionActivity.EXTRA_RETURN_COMMUNITY_DATA);
                subredditName = communityData.getName();
                iconUrl = communityData.getIconUrl();
                subredditSelected = true;
                subredditIsUser = false;

                subredditNameTextView.setTextColor(primaryTextColor);
                subredditNameTextView.setText(subredditName);
                displaySubredditIcon();

                flair = null;

            } else if (requestCode == PICK_IMAGE_REQUEST_CODE) {
                if (data == null) {
                    Toast.makeText(PostTextActivity.this, R.string.error_getting_image, Toast.LENGTH_LONG).show();
                    return;
                }
                Utils.uploadImageToReddit(this, mExecutor, mRetrofit,
                        mAccessToken, contentEditText, coordinatorLayout, data.getData(), uploadedImages);
            } else if (requestCode == CAPTURE_IMAGE_REQUEST_CODE) {
                Utils.uploadImageToReddit(this, mExecutor, mRetrofit,
                        mAccessToken, contentEditText, coordinatorLayout, capturedImageUri, uploadedImages);
            } else if (requestCode == MARKDOWN_PREVIEW_REQUEST_CODE) {
                submitPost(mMenu.findItem(R.id.action_send_post_text_activity));
            }
        }

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void flairSelected(Flair flair) {
        this.flair = flair;
    }

    @Subscribe
    public void onAccountSwitchEvent(SwitchAccountEvent event) {
        finish();
    }

    @Subscribe
    public void onSubmitTextPostEvent(SubmitTextOrLinkPostEvent submitTextOrLinkPostEvent) {
        isPosting = false;
        mPostingSnackbar.dismiss();
        if (submitTextOrLinkPostEvent.postSuccess) {
            Intent intent = new Intent(PostTextActivity.this, ViewPostDetailActivity.class);
            intent.putExtra(ViewPostDetailActivity.EXTRA_POST_DATA, submitTextOrLinkPostEvent.post);
            startActivity(intent);
            finish();
        } else {
            mMenu.findItem(R.id.action_send_post_text_activity).setEnabled(true);
            mMenu.findItem(R.id.action_send_post_text_activity).getIcon().setAlpha(255);
            if (submitTextOrLinkPostEvent.errorMessage == null || submitTextOrLinkPostEvent.errorMessage.equals("")) {
                Snackbar.make(coordinatorLayout, R.string.post_failed, Snackbar.LENGTH_SHORT).show();
            } else {
                Snackbar.make(coordinatorLayout, submitTextOrLinkPostEvent.errorMessage.substring(0, 1).toUpperCase()
                        + submitTextOrLinkPostEvent.errorMessage.substring(1), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void uploadImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getResources().getString(R.string.select_from_gallery)), PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    public void captureImage() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            capturedImageUri = FileProvider.getUriForFile(this, "eu.toldi.infinityforlemmy.provider",
                    File.createTempFile("captured_image", ".jpg", getExternalFilesDir(Environment.DIRECTORY_PICTURES)));
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
            startActivityForResult(pictureIntent, CAPTURE_IMAGE_REQUEST_CODE);
        } catch (IOException ex) {
            Toast.makeText(this, R.string.error_creating_temp_file, Toast.LENGTH_SHORT).show();
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.no_camera_available, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void insertImageUrl(UploadedImage uploadedImage) {
        int start = Math.max(contentEditText.getSelectionStart(), 0);
        int end = Math.max(contentEditText.getSelectionEnd(), 0);
        contentEditText.getText().replace(Math.min(start, end), Math.max(start, end),
                "![" + uploadedImage.imageName + "](" + uploadedImage.imageUrl + ")",
                0, "![]()".length() + uploadedImage.imageName.length() + uploadedImage.imageUrl.length());
    }

    @Override
    public void onAccountSelected(Account account) {
        if (account != null) {
            selectedAccount = account;

            mGlide.load(selectedAccount.getProfileImageUrl())
                    .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0)))
                    .error(mGlide.load(R.drawable.subreddit_default_icon)
                            .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(72, 0))))
                    .into(accountIconImageView);

            accountNameTextView.setText(selectedAccount.getAccountName());
        }
    }
}
