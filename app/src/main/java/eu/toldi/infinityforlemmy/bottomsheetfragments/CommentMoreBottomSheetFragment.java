package eu.toldi.infinityforlemmy.bottomsheetfragments;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.Infinity;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.activities.BaseActivity;
import eu.toldi.infinityforlemmy.activities.CommentActivity;
import eu.toldi.infinityforlemmy.activities.EditCommentActivity;
import eu.toldi.infinityforlemmy.activities.ViewPostDetailActivity;
import eu.toldi.infinityforlemmy.activities.ViewUserDetailActivity;
import eu.toldi.infinityforlemmy.comment.Comment;
import eu.toldi.infinityforlemmy.comment.LemmyCommentAPI;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.customviews.LandscapeExpandedRoundedBottomSheetDialogFragment;
import eu.toldi.infinityforlemmy.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommentMoreBottomSheetFragment extends LandscapeExpandedRoundedBottomSheetDialogFragment {

    public static final String EXTRA_COMMENT = "ECF";
    public static final String EXTRA_ACCESS_TOKEN = "EAT";
    public static final String EXTRA_EDIT_AND_DELETE_AVAILABLE = "EEADA";
    public static final String EXTRA_POSITION = "EP";
    public static final String EXTRA_SHOW_REPLY_AND_SAVE_OPTION = "ESSARO";
    public static final String EXTRA_IS_NSFW = "EIN";
    @BindView(R.id.edit_text_view_comment_more_bottom_sheet_fragment)
    TextView editTextView;
    @BindView(R.id.delete_text_view_comment_more_bottom_sheet_fragment)
    TextView deleteTextView;
    @BindView(R.id.reply_text_view_comment_more_bottom_sheet_fragment)
    TextView replyTextView;
    @BindView(R.id.save_text_view_comment_more_bottom_sheet_fragment)
    TextView saveTextView;
    @BindView(R.id.share_text_view_comment_more_bottom_sheet_fragment)
    TextView shareTextView;
    @BindView(R.id.copy_text_view_comment_more_bottom_sheet_fragment)
    TextView copyTextView;
    @BindView(R.id.report_view_comment_more_bottom_sheet_fragment)
    TextView reportTextView;

    @Inject
    LemmyCommentAPI lemmyCommentAPI;

    @Inject
    CustomThemeWrapper mCustomThemeWrapper;

    private BaseActivity activity;

    public CommentMoreBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((Infinity) activity.getApplication()).getAppComponent().inject(this);
        View rootView = inflater.inflate(R.layout.fragment_comment_more_bottom_sheet, container, false);
        ButterKnife.bind(this, rootView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) != Configuration.UI_MODE_NIGHT_YES) {
            rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        Bundle bundle = getArguments();
        if (bundle == null) {
            dismiss();
            return rootView;
        }
        Comment comment = bundle.getParcelable(EXTRA_COMMENT);
        if (comment == null) {
            dismiss();
            return rootView;
        }
        String accessToken = bundle.getString(EXTRA_ACCESS_TOKEN);
        boolean editAndDeleteAvailable = bundle.getBoolean(EXTRA_EDIT_AND_DELETE_AVAILABLE, false);
        boolean showReplyAndSaveOption = bundle.getBoolean(EXTRA_SHOW_REPLY_AND_SAVE_OPTION, false);

        if (accessToken != null && !accessToken.equals("")) {

            if (editAndDeleteAvailable) {
                editTextView.setVisibility(View.VISIBLE);
                deleteTextView.setVisibility(View.VISIBLE);

                editTextView.setOnClickListener(view -> {
                    Intent intent = new Intent(activity, EditCommentActivity.class);
                    intent.putExtra(EditCommentActivity.EXTRA_FULLNAME, comment.getId());
                    intent.putExtra(EditCommentActivity.EXTRA_CONTENT, comment.getCommentMarkdown());
                    intent.putExtra(EditCommentActivity.EXTRA_POSITION, bundle.getInt(EXTRA_POSITION));
                    if (activity instanceof ViewPostDetailActivity) {
                        activity.startActivityForResult(intent, ViewPostDetailActivity.EDIT_COMMENT_REQUEST_CODE);
                    } else {
                        activity.startActivityForResult(intent, ViewUserDetailActivity.EDIT_COMMENT_REQUEST_CODE);
                    }

                    dismiss();
                });

                deleteTextView.setOnClickListener(view -> {
                    dismiss();
                    if (activity instanceof ViewPostDetailActivity) {
                        ((ViewPostDetailActivity) activity).deleteComment(comment.getId(), bundle.getInt(EXTRA_POSITION));
                    } else if (activity instanceof ViewUserDetailActivity) {
                        ((ViewUserDetailActivity) activity).deleteComment(comment.getId());
                    }
                });
            }
        }

        if (showReplyAndSaveOption) {
            replyTextView.setVisibility(View.VISIBLE);
            saveTextView.setVisibility(View.VISIBLE);
            if (comment.isSaved()) {
                saveTextView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(activity, R.drawable.ic_bookmark_24dp), null, null, null);
                saveTextView.setText(R.string.unsave_comment);
            } else {
                saveTextView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(activity, R.drawable.ic_bookmark_border_24dp), null, null, null);
                saveTextView.setText(R.string.save_comment);
            }
            replyTextView.setOnClickListener(view -> {
                Intent intent = new Intent(activity, CommentActivity.class);
                intent.putExtra(CommentActivity.EXTRA_PARENT_DEPTH_KEY, comment.getDepth() + 1);
                intent.putExtra(CommentActivity.EXTRA_COMMENT_PARENT_BODY_MARKDOWN_KEY, comment.getCommentMarkdown());
                intent.putExtra(CommentActivity.EXTRA_COMMENT_PARENT_BODY_KEY, comment.getCommentRawText());
                intent.putExtra(CommentActivity.EXTRA_POST_ID_KEY, comment.getPostId());
                intent.putExtra(CommentActivity.EXTRA_COMMENT_PARENT_ID_KEY, comment.getId());
                intent.putExtra(CommentActivity.EXTRA_IS_REPLYING_KEY, true);

                intent.putExtra(CommentActivity.EXTRA_PARENT_POSITION_KEY, bundle.getInt(EXTRA_POSITION));
                activity.startActivityForResult(intent, CommentActivity.WRITE_COMMENT_REQUEST_CODE);

                dismiss();
            });

            saveTextView.setOnClickListener(view -> {
                if (activity instanceof ViewPostDetailActivity) {
                    ((ViewPostDetailActivity) activity).saveComment(comment, bundle.getInt(EXTRA_POSITION));
                }
                dismiss();
            });
        }

        shareTextView.setOnClickListener(view -> {
            dismiss();
            try {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, comment.getPermalink());
                activity.startActivity(Intent.createChooser(intent, getString(R.string.share)));
            } catch (ActivityNotFoundException e) {
                Toast.makeText(activity, R.string.no_activity_found_for_share, Toast.LENGTH_SHORT).show();
            }
        });

        shareTextView.setOnLongClickListener(view -> {
            dismiss();
            activity.copyLink(comment.getPermalink());
            return true;
        });

        copyTextView.setOnClickListener(view -> {
            dismiss();
            CopyTextBottomSheetFragment.show(activity.getSupportFragmentManager(),
                    comment.getCommentRawText(), comment.getCommentMarkdown());
        });

        reportTextView.setOnClickListener(view -> {
            if (accessToken == null) {
                Toast.makeText(activity, R.string.login_first, Toast.LENGTH_SHORT).show();
                dismiss();
                return;
            }
            LayoutInflater dialog_inflater = LayoutInflater.from(activity);
            View dialog_view = dialog_inflater.inflate(R.layout.dialog_report, null);
            EditText reasonEditText = dialog_view.findViewById(R.id.reasonEditText);

            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity)
                    .setTitle(R.string.report_post)
                    .setView(dialog_view)
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton(R.string.send_report, (dialogInterface, i) -> {
                        String reason = reasonEditText.getText().toString();
                        if (reason.isEmpty()) {
                            Toast.makeText(activity, "A report reason must be provided", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        lemmyCommentAPI.reportComment(comment.getId(), reason, accessToken, new LemmyCommentAPI.ReportCommentCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(activity, R.string.report_successful, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(activity, R.string.report_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
            AlertDialog dialog = builder.create();
            dialog.show();

            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            positiveButton.setTextColor(mCustomThemeWrapper.getSecondaryTextColor());
            negativeButton.setTextColor(mCustomThemeWrapper.getSecondaryTextColor());
            dismiss();
        });


        if (activity.typeface != null) {
            Utils.setFontToAllTextViews(rootView, activity.typeface);
        }

        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (BaseActivity) context;
    }
}
