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
import eu.toldi.infinityforlemmy.activities.SelectedSubredditsAndUsersActivity;
import eu.toldi.infinityforlemmy.customviews.LandscapeExpandedRoundedBottomSheetDialogFragment;
import eu.toldi.infinityforlemmy.utils.Utils;

public class SelectSubredditsOrUsersOptionsBottomSheetFragment extends LandscapeExpandedRoundedBottomSheetDialogFragment {

    @BindView(R.id.select_subreddits_text_view_search_user_and_subreddit_sort_type_bottom_sheet_fragment)
    TextView selectSubredditsTextView;
    @BindView(R.id.select_users_text_view_search_user_and_subreddit_sort_type_bottom_sheet_fragment)
    TextView selectUsersTextView;
    private SelectedSubredditsAndUsersActivity activity;

    public SelectSubredditsOrUsersOptionsBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_select_subreddits_or_users_options_bottom_sheet, container, false);

        ButterKnife.bind(this, rootView);

        selectSubredditsTextView.setOnClickListener(view -> {
            activity.selectSubreddits();
            dismiss();
        });

        selectUsersTextView.setOnClickListener(view -> {
            activity.selectUsers();
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
        activity = (SelectedSubredditsAndUsersActivity) context;
    }
}