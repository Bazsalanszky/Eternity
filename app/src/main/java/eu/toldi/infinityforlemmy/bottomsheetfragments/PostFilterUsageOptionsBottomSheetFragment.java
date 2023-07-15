package eu.toldi.infinityforlemmy.bottomsheetfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.activities.PostFilterUsageListingActivity;
import eu.toldi.infinityforlemmy.customviews.LandscapeExpandedRoundedBottomSheetDialogFragment;
import eu.toldi.infinityforlemmy.postfilter.PostFilterUsage;
import eu.toldi.infinityforlemmy.utils.Utils;

public class PostFilterUsageOptionsBottomSheetFragment extends LandscapeExpandedRoundedBottomSheetDialogFragment {

    public static final String EXTRA_POST_FILTER_USAGE = "EPFU";

    @BindView(R.id.edit_text_view_post_filter_usage_options_bottom_sheet_fragment)
    TextView editTextView;
    @BindView(R.id.delete_text_view_post_filter_usage_options_bottom_sheet_fragment)
    TextView deleteTextView;
    private PostFilterUsageListingActivity activity;

    public PostFilterUsageOptionsBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_post_filter_usage_options_bottom_sheet, container, false);

        ButterKnife.bind(this, rootView);

        PostFilterUsage postFilterUsage = getArguments().getParcelable(EXTRA_POST_FILTER_USAGE);

        if (postFilterUsage.usage == PostFilterUsage.HOME_TYPE || postFilterUsage.usage == PostFilterUsage.SEARCH_TYPE) {
            editTextView.setVisibility(View.GONE);
        } else {
            editTextView.setOnClickListener(view -> {
                activity.editPostFilterUsage(postFilterUsage);
                dismiss();
            });
        }

        deleteTextView.setOnClickListener(view -> {
            activity.deletePostFilterUsage(postFilterUsage);
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
        activity = (PostFilterUsageListingActivity) context;
    }
}