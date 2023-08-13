package eu.toldi.infinityforlemmy.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
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
import eu.toldi.infinityforlemmy.apis.LemmyAPI;
import eu.toldi.infinityforlemmy.bottomsheetfragments.UploadedImagesBottomSheetFragment;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.customviews.LinearLayoutManagerBugFixed;
import eu.toldi.infinityforlemmy.customviews.slidr.Slidr;
import eu.toldi.infinityforlemmy.dto.EditPostDTO;
import eu.toldi.infinityforlemmy.events.SwitchAccountEvent;
import eu.toldi.infinityforlemmy.post.Post;
import eu.toldi.infinityforlemmy.utils.SharedPreferencesUtils;
import eu.toldi.infinityforlemmy.utils.UploadImageUtils;
import eu.toldi.infinityforlemmy.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class EditPostActivity extends BaseActivity implements UploadImageEnabledActivity {

    public static final String EXTRA_DATA = "ED";


    private static final int UPLOAD_IMAGE_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQUEST_CODE = 100;
    private static final int CAPTURE_IMAGE_REQUEST_CODE = 200;
    private static final int MARKDOWN_PREVIEW_REQUEST_CODE = 300;

    private static final String UPLOADED_IMAGES_STATE = "UIS";

    private static final String picturePattern = "https:\\/\\/[^\\/]+\\/pictrs\\/image\\/([a-f\\d-]+\\.jpeg)";


    @BindView(R.id.coordinator_layout_edit_post_activity)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.appbar_layout_edit_post_activity)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar_edit_post_activity)
    Toolbar toolbar;
    @BindView(R.id.post_title_text_view_edit_post_activity)
    EditText titleEditText;
    @BindView(R.id.divider_edit_post_activity)
    View divider;
    @BindView(R.id.post_text_content_edit_text_edit_post_activity)
    EditText contentEditText;
    @BindView(R.id.markdown_bottom_bar_recycler_view_edit_post_activity)
    RecyclerView markdownBottomBarRecyclerView;
    @BindView(R.id.post_link_edit_text_post_edit_activity)
    EditText linkEditText;

    @BindView(R.id.upload_image_button_post_edit_activity)
    MaterialButton uploadImageButton;

    @BindView(R.id.image_view_post_edit_activity)
    ImageView imageView;

    @Inject
    @Named("no_oauth")
    RetrofitHolder mRetrofit;
    @Inject
    @Named("upload_media")
    Retrofit mUploadMediaRetrofit;
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
    private Post mPost;

    private String mAccessToken;
    private boolean isSubmitting = false;
    private Uri capturedImageUri;
    private ArrayList<UploadedImage> uploadedImages = new ArrayList<>();


    private RequestManager mGlide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ((Infinity) getApplication()).getAppComponent().inject(this);

        setImmersiveModeNotApplicable();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_post);

        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        applyCustomTheme();

        if (mSharedPreferences.getBoolean(SharedPreferencesUtils.SWIPE_RIGHT_TO_GO_BACK, true)) {
            Slidr.attach(this);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isChangeStatusBarIconColor()) {
            addOnOffsetChangedListener(appBarLayout);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPost = getIntent().getParcelableExtra(EXTRA_DATA);
        if (mPost == null) {
            finish();
        }

        mAccessToken = mCurrentAccountSharedPreferences.getString(SharedPreferencesUtils.ACCESS_TOKEN, null);
        titleEditText.setText(mPost.getTitle());
        contentEditText.setText(mPost.getSelfText());
        linkEditText.setText(mPost.getUrl());

        mGlide = Glide.with(this);

        if (mPost.getUrl() != null && mPost.getUrl().matches(picturePattern)) {
            loadImage();
        }

        linkEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().matches(picturePattern)) {
                    loadImage();
                } else {
                    uploadImageButton.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        uploadImageButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_from_gallery)), UPLOAD_IMAGE_REQUEST_CODE);
        });


        if (savedInstanceState != null) {
            uploadedImages = savedInstanceState.getParcelableArrayList(UPLOADED_IMAGES_STATE);
        }

        MarkdownBottomBarRecyclerViewAdapter adapter = new MarkdownBottomBarRecyclerViewAdapter(
                mCustomThemeWrapper, new MarkdownBottomBarRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onClick(int item) {
                MarkdownBottomBarRecyclerViewAdapter.bindEditTextWithItemClickListener(
                        EditPostActivity.this, contentEditText, item);
            }

            @Override
            public void onUploadImage() {
                Utils.hideKeyboard(EditPostActivity.this);
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

        contentEditText.requestFocus();
        Utils.showKeyboard(this, new Handler(), contentEditText);
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
        titleEditText.setTextColor(mCustomThemeWrapper.getPostTitleColor());
        divider.setBackgroundColor(mCustomThemeWrapper.getPostTitleColor());
        contentEditText.setTextColor(mCustomThemeWrapper.getPostContentColor());
        linkEditText.setTextColor(mCustomThemeWrapper.getPostContentColor());

        uploadImageButton.setTextColor(mCustomThemeWrapper.getButtonTextColor());
        uploadImageButton.setBackgroundColor(mCustomThemeWrapper.getColorPrimaryLightTheme());
        if (titleTypeface != null) {
            titleEditText.setTypeface(titleTypeface);
        }
        if (contentTypeface != null) {
            contentEditText.setTypeface(contentTypeface);
        }
    }

    private void loadImage() {
        uploadImageButton.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        mGlide.load(mPost.getUrl()).into(imageView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.hideKeyboard(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_post_activity, menu);
        applyMenuItemTheme(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_preview_edit_post_activity) {
            Intent intent = new Intent(this, FullMarkdownActivity.class);
            intent.putExtra(FullMarkdownActivity.EXTRA_COMMENT_MARKDOWN, contentEditText.getText().toString());
            intent.putExtra(FullMarkdownActivity.EXTRA_SUBMIT_POST, true);
            startActivityForResult(intent, MARKDOWN_PREVIEW_REQUEST_CODE);
        } else if (item.getItemId() == R.id.action_send_edit_post_activity) {
            editPost();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void editPost() {
        if (!isSubmitting) {
            isSubmitting = true;

            Snackbar.make(coordinatorLayout, R.string.posting, Snackbar.LENGTH_SHORT).show();
            mRetrofit.getRetrofit().create(LemmyAPI.class).postUpdate(new EditPostDTO(mPost.getId(), titleEditText.getText().toString(), (linkEditText.getText().toString().isEmpty()) ? null : linkEditText.getText().toString(), contentEditText.getText().toString(), mPost.isNSFW(), null, mAccessToken))
                    .enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            isSubmitting = false;
                            if (response.isSuccessful()) {
                                Toast.makeText(EditPostActivity.this, R.string.edit_success, Toast.LENGTH_SHORT).show();
                                Intent returnIntent = new Intent();
                                setResult(RESULT_OK, returnIntent);
                                finish();
                            } else {
                                Snackbar.make(coordinatorLayout, R.string.post_failed, Snackbar.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            isSubmitting = false;
                            Snackbar.make(coordinatorLayout, R.string.post_failed, Snackbar.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST_CODE) {
                if (data == null) {
                    Toast.makeText(EditPostActivity.this, R.string.error_getting_image, Toast.LENGTH_LONG).show();
                    return;
                }
                Utils.uploadImageToReddit(this, mExecutor, mRetrofit,
                        mAccessToken, contentEditText, coordinatorLayout, data.getData(), uploadedImages);
            } else if (requestCode == UPLOAD_IMAGE_REQUEST_CODE) {
                if (data == null) {
                    Snackbar.make(coordinatorLayout, R.string.error_getting_image, Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(this, R.string.uploading_image, Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                Uri imageUri = data.getData();
                mExecutor.execute(() -> {
                    try {
                        Bitmap bitmap = Glide.with(this).asBitmap().load(imageUri).submit().get();
                        String imageUrlOrError = UploadImageUtils.uploadImage(mRetrofit, mAccessToken, bitmap);
                        handler.post(() -> {
                            if (imageUrlOrError != null && !imageUrlOrError.startsWith("Error: ")) {
                                String fileName = Utils.getFileName(this, imageUri);
                                if (fileName == null) {
                                    fileName = imageUrlOrError;
                                }
                                mPost.setUrl(imageUrlOrError);
                                linkEditText.setText(imageUrlOrError);
                                Snackbar.make(coordinatorLayout, R.string.upload_image_success, Snackbar.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(this, R.string.upload_image_failed, Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                        handler.post(() -> Toast.makeText(this, R.string.get_image_bitmap_failed, Toast.LENGTH_LONG).show());
                    } catch (XmlPullParserException | JSONException | IOException e) {
                        e.printStackTrace();
                        handler.post(() -> Toast.makeText(this, R.string.error_processing_image, Toast.LENGTH_LONG).show());
                    }
                });
            } else if (requestCode == CAPTURE_IMAGE_REQUEST_CODE) {
                Utils.uploadImageToReddit(this, mExecutor, mRetrofit,
                        mAccessToken, contentEditText, coordinatorLayout, capturedImageUri, uploadedImages);
            } else if (requestCode == MARKDOWN_PREVIEW_REQUEST_CODE) {
                editPost();
            }
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(UPLOADED_IMAGES_STATE, uploadedImages);
    }

    private void promptAlertDialog(int titleResId, int messageResId) {
        new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogTheme)
                .setTitle(titleResId)
                .setMessage(messageResId)
                .setPositiveButton(R.string.discard_dialog_button, (dialogInterface, i)
                        -> finish())
                .setNegativeButton(R.string.no, null)
                .show();
    }

    @Override
    public void onBackPressed() {
        if (isSubmitting) {
            promptAlertDialog(R.string.exit_when_submit, R.string.exit_when_edit_post_detail);
        } else {
            if (contentEditText.getText().toString().equals(mPost.getSelfText())) {
                finish();
            } else {
                promptAlertDialog(R.string.discard, R.string.discard_detail);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onAccountSwitchEvent(SwitchAccountEvent event) {
        finish();
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
}
