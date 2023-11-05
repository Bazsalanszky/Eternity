package eu.toldi.infinityforlemmy.bottomsheetfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import eu.toldi.infinityforlemmy.activities.CommentFilterPreferenceActivity;
import eu.toldi.infinityforlemmy.commentfilter.CommentFilter;
import eu.toldi.infinityforlemmy.customviews.LandscapeExpandedRoundedBottomSheetDialogFragment;
import eu.toldi.infinityforlemmy.databinding.FragmentCommentFilterOptionsBottomSheetBinding;
import eu.toldi.infinityforlemmy.utils.Utils;


public class CommentFilterOptionsBottomSheetFragment extends LandscapeExpandedRoundedBottomSheetDialogFragment {

    public static final String EXTRA_POST_FILTER = "EPF";
    private CommentFilterPreferenceActivity activity;

    public CommentFilterOptionsBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentCommentFilterOptionsBottomSheetBinding binding = FragmentCommentFilterOptionsBottomSheetBinding.inflate(inflater, container, false);

        CommentFilter commentFilter = getArguments().getParcelable(EXTRA_POST_FILTER);

        binding.editTextViewCommentFilterOptionsBottomSheetFragment.setOnClickListener(view -> {
            activity.editCommentFilter(commentFilter);
            dismiss();
        });

        binding.applyToTextViewCommentFilterOptionsBottomSheetFragment.setOnClickListener(view -> {
            activity.applyCommentFilterTo(commentFilter);
            dismiss();
        });

        binding.deleteTextViewCommentFilterOptionsBottomSheetFragment.setOnClickListener(view -> {
            activity.deleteCommentFilter(commentFilter);
            dismiss();
        });

        if (activity.typeface != null) {
            Utils.setFontToAllTextViews(binding.getRoot(), activity.typeface);
        }

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (CommentFilterPreferenceActivity) context;
    }
}