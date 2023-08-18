package eu.toldi.infinityforlemmy.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.Infinity;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.RetrofitHolder;
import eu.toldi.infinityforlemmy.UploadImageEnabledActivity;
import eu.toldi.infinityforlemmy.UploadedImage;
import eu.toldi.infinityforlemmy.adapters.MarkdownBottomBarRecyclerViewAdapter;
import eu.toldi.infinityforlemmy.bottomsheetfragments.UploadedImagesBottomSheetFragment;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.customviews.LinearLayoutManagerBugFixed;
import eu.toldi.infinityforlemmy.privatemessage.LemmyPrivateMessageAPI;
import eu.toldi.infinityforlemmy.privatemessage.PrivateMessage;
import eu.toldi.infinityforlemmy.user.BasicUserInfo;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;
import eu.toldi.infinityforlemmy.utils.Utils;

public class SendPrivateMessageActivity extends BaseActivity implements UploadImageEnabledActivity {
    public static final String EXTRA_RECIPIENT_USER_INFO = "ERUI";

    private static final int PICK_IMAGE_REQUEST_CODE = 100;
    private static final int CAPTURE_IMAGE_REQUEST_CODE = 200;
    @BindView(R.id.coordinator_layout_send_private_message_activity)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_layout_send_private_message_activity)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_send_private_message_activity)
    Toolbar toolbar;
    @BindView(R.id.username_edit_text_send_private_message_activity)
    EditText usernameEditText;
    @BindView(R.id.divider_1_send_private_message_activity)
    View divider1;
    @BindView(R.id.divider_2_send_private_message_activity)
    View divider2;
    @BindView(R.id.content_edit_text_send_private_message_activity)
    EditText messageEditText;

    @BindView(R.id.markdown_bottom_bar_recycler_view_send_private_message_activity)
    RecyclerView markdownBottomBarRecyclerView;
    @Inject
    @Named("no_oauth")
    RetrofitHolder mRetrofit;

    @Inject
    Executor mExecutor;

    @Inject
    @Named("default")
    SharedPreferences mSharedPreferences;
    @Inject
    @Named("current_account")
    SharedPreferences mCurrentAccountSharedPreferences;
    @Inject
    CustomThemeWrapper mCustomThemeWrapper;

    @Inject
    LemmyPrivateMessageAPI mLemmyPrivateMessageAPI;
    private String mAccessToken;

    private BasicUserInfo mRecipientBasicUserInfo;
    private boolean isSubmitting = false;

    private ArrayList<UploadedImage> uploadedImages = new ArrayList<>();

    private Uri capturedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);

        setImmersiveModeNotApplicable();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_private_message);

        ButterKnife.bind(this);

        applyCustomTheme();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isChangeStatusBarIconColor()) {
            addOnOffsetChangedListener(appBarLayout);
        }

        MarkdownBottomBarRecyclerViewAdapter adapter = new MarkdownBottomBarRecyclerViewAdapter(
                mCustomThemeWrapper, new MarkdownBottomBarRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onClick(int item) {
                MarkdownBottomBarRecyclerViewAdapter.bindEditTextWithItemClickListener(
                        SendPrivateMessageActivity.this, messageEditText, item);
            }

            @Override
            public void onUploadImage() {
                Utils.hideKeyboard(SendPrivateMessageActivity.this);
                UploadedImagesBottomSheetFragment fragment = new UploadedImagesBottomSheetFragment();
                Bundle arguments = new Bundle();
                arguments.putParcelableArrayList(UploadedImagesBottomSheetFragment.EXTRA_UPLOADED_IMAGES,
                        uploadedImages);
                fragment.setArguments(arguments);
                fragment.show(getSupportFragmentManager(), fragment.getTag());
            }
        });

        markdownBottomBarRecyclerView.setLayoutManager(new LinearLayoutManagerBugFixed(this,
                LinearLayoutManagerBugFixed.HORIZONTAL, false));
        markdownBottomBarRecyclerView.setAdapter(adapter);

        mAccessToken = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCESS_TOKEN, null);

        setSupportActionBar(toolbar);
        if (savedInstanceState != null) {
            mRecipientBasicUserInfo = savedInstanceState.getParcelable(EXTRA_RECIPIENT_USER_INFO);
        } else {
            mRecipientBasicUserInfo = getIntent().getParcelableExtra(EXTRA_RECIPIENT_USER_INFO);
        }

        if (mRecipientBasicUserInfo != null) {
            usernameEditText.setText(mRecipientBasicUserInfo.getQualifiedName());
            usernameEditText.setEnabled(false);
        } else {
            finish();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST_CODE) {
                if (data == null) {
                    Toast.makeText(SendPrivateMessageActivity.this, R.string.error_getting_image, Toast.LENGTH_LONG).show();
                    return;
                }
                Utils.uploadImageToReddit(this, mExecutor, mRetrofit,
                        mAccessToken, messageEditText, coordinatorLayout, data.getData(), uploadedImages);
            } else if (requestCode == CAPTURE_IMAGE_REQUEST_CODE) {
                Utils.uploadImageToReddit(this, mExecutor, mRetrofit,
                        mAccessToken, messageEditText, coordinatorLayout, capturedImageUri, uploadedImages);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_private_message_activity, menu);
        applyMenuItemTheme(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_send_send_private_message_activity) {
            if (!isSubmitting) {
                isSubmitting = true;
                if (usernameEditText.getText() == null || usernameEditText.getText().toString().equals("")) {
                    isSubmitting = false;
                    Snackbar.make(coordinatorLayout, R.string.message_username_required, Snackbar.LENGTH_LONG).show();
                    return true;
                }

                if (messageEditText.getText() == null || messageEditText.getText().toString().equals("")) {
                    isSubmitting = false;
                    Snackbar.make(coordinatorLayout, R.string.message_content_required, Snackbar.LENGTH_LONG).show();
                    return true;
                }

                item.setEnabled(false);
                item.getIcon().setAlpha(130);
                Snackbar sendingSnackbar = Snackbar.make(coordinatorLayout, R.string.sending_message, Snackbar.LENGTH_INDEFINITE);
                sendingSnackbar.show();

                mLemmyPrivateMessageAPI.sendPrivateMessage(mAccessToken, mRecipientBasicUserInfo.getId(), messageEditText.getText().toString(), new LemmyPrivateMessageAPI.PrivateMessageSentListener() {
                    @Override
                    public void onPrivateMessageSentSuccess(@NonNull PrivateMessage privateMessage) {
                        isSubmitting = false;
                        item.setEnabled(true);
                        item.getIcon().setAlpha(255);
                        Toast.makeText(SendPrivateMessageActivity.this, R.string.send_message_success, Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onPrivateMessageSentError() {
                        isSubmitting = false;
                        sendingSnackbar.dismiss();
                        item.setEnabled(true);
                        item.getIcon().setAlpha(255);

                        Snackbar.make(coordinatorLayout, R.string.send_message_failed, Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        }
        return false;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_RECIPIENT_USER_INFO, mRecipientBasicUserInfo);
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
        coordinatorLayout.setBackgroundColor(mCustomThemeWrapper.getBackgroundColor());
        applyAppBarLayoutAndCollapsingToolbarLayoutAndToolbarTheme(appBarLayout, null, toolbar);
        int primaryTextColor = mCustomThemeWrapper.getPrimaryTextColor();
        usernameEditText.setTextColor(primaryTextColor);
        messageEditText.setTextColor(primaryTextColor);
        int secondaryTextColor = mCustomThemeWrapper.getSecondaryTextColor();
        usernameEditText.setHintTextColor(secondaryTextColor);
        messageEditText.setHintTextColor(secondaryTextColor);
        int dividerColor = mCustomThemeWrapper.getDividerColor();
        divider1.setBackgroundColor(dividerColor);
        divider2.setBackgroundColor(dividerColor);
        if (typeface != null) {
            usernameEditText.setTypeface(typeface);
            messageEditText.setTypeface(typeface);
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
        int start = Math.max(messageEditText.getSelectionStart(), 0);
        int end = Math.max(messageEditText.getSelectionEnd(), 0);
        messageEditText.getText().replace(Math.min(start, end), Math.max(start, end),
                "![" + uploadedImage.imageName + "](" + uploadedImage.imageUrl + ")",
                0, "![]()".length() + uploadedImage.imageName.length() + uploadedImage.imageUrl.length());
    }
}