package eu.toldi.infinityforlemmy.bottomsheetfragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import eu.toldi.infinityforlemmy.activities.PostFilterPreferenceActivity;
import eu.toldi.infinityforlemmy.customviews.LandscapeExpandedRoundedBottomSheetDialogFragment;
import eu.toldi.infinityforlemmy.databinding.FragmentPostFilterOptionsBottomSheetBinding;
import eu.toldi.infinityforlemmy.postfilter.PostFilter;
import eu.toldi.infinityforlemmy.utils.Utils;

public class PostFilterOptionsBottomSheetFragment extends LandscapeExpandedRoundedBottomSheetDialogFragment {

    public static final String EXTRA_POST_FILTER = "EPF";
    private PostFilterPreferenceActivity activity;

    public PostFilterOptionsBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentPostFilterOptionsBottomSheetBinding binding = FragmentPostFilterOptionsBottomSheetBinding.inflate(inflater, container, false);

        PostFilter postFilter = getArguments().getParcelable(EXTRA_POST_FILTER);

        binding.editTextViewPostFilterOptionsBottomSheetFragment.setOnClickListener(view -> {
            activity.editPostFilter(postFilter);
            dismiss();
        });

        binding.applyToTextViewPostFilterOptionsBottomSheetFragment.setOnClickListener(view -> {
            activity.applyPostFilterTo(postFilter);
            dismiss();
        });

        binding.deleteTextViewPostFilterOptionsBottomSheetFragment.setOnClickListener(view -> {
            activity.deletePostFilter(postFilter);
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
        activity = (PostFilterPreferenceActivity) context;
    }
}