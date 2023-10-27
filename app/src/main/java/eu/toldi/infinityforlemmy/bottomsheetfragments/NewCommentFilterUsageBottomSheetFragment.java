package eu.toldi.infinityforlemmy.bottomsheetfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import eu.toldi.infinityforlemmy.activities.CommentFilterUsageListingActivity;
import eu.toldi.infinityforlemmy.commentfilter.CommentFilterUsage;
import eu.toldi.infinityforlemmy.customviews.LandscapeExpandedRoundedBottomSheetDialogFragment;
import eu.toldi.infinityforlemmy.databinding.FragmentNewCommentFilterUsageBottomSheetBinding;
import eu.toldi.infinityforlemmy.utils.Utils;


public class NewCommentFilterUsageBottomSheetFragment extends LandscapeExpandedRoundedBottomSheetDialogFragment {
    private CommentFilterUsageListingActivity activity;

    public NewCommentFilterUsageBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentNewCommentFilterUsageBottomSheetBinding binding = FragmentNewCommentFilterUsageBottomSheetBinding.inflate(inflater, container, false);

        binding.subredditTextViewNewCommentFilterUsageBottomSheetFragment.setOnClickListener(view -> {
            activity.newCommentFilterUsage(CommentFilterUsage.SUBREDDIT_TYPE);
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
        activity = (CommentFilterUsageListingActivity) context;
    }
}