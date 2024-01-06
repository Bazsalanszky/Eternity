package eu.toldi.infinityforlemmy.bottomsheetfragments;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.SortType;
import eu.toldi.infinityforlemmy.SortTypeSelectionCallback;
import eu.toldi.infinityforlemmy.activities.BaseActivity;
import eu.toldi.infinityforlemmy.customviews.LandscapeExpandedRoundedBottomSheetDialogFragment;
import eu.toldi.infinityforlemmy.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class SortTypeBottomSheetFragment extends LandscapeExpandedRoundedBottomSheetDialogFragment {

    public static final String EXTRA_CURRENT_SORT_TYPE = "ECST";

    public static final String EXTRA_PAGE_TYPE = "EPT";

    public static final int PAGE_TYPE_FRONT_PAGE = 0;
    public static final int PAGE_TYPE_COMMUNITY = 1;
    public static final int PAGE_TYPE_USER = 2;
    public static final int PAGE_TYPE_SEARCH = 3;
    public static final int PAGE_TYPE_MULTICOMMUNITY = 4;
    public static final int PAGE_TYPE_ANONYMOUS_FRONT_PAGE = 5;


    @BindView(R.id.best_type_text_view_sort_type_bottom_sheet_fragment)
    TextView activeTypeTextView;
    @BindView(R.id.hot_type_text_view_sort_type_bottom_sheet_fragment)
    TextView hotTypeTextView;
    @BindView(R.id.new_type_text_view_sort_type_bottom_sheet_fragment)
    TextView newTypeTextView;
    @BindView(R.id.old_type_text_view_sort_type_bottom_sheet_fragment)
    TextView oldTypeTextView;
    @BindView(R.id.top_type_text_view_sort_type_bottom_sheet_fragment)
    TextView topTypeTextView;

    @BindView(R.id.scaled_type_text_view_sort_type_bottom_sheet_fragment)
    TextView scaledTypeTextView;

    @BindView(R.id.controversial_type_text_view_sort_type_bottom_sheet_fragment)
    TextView controversialTypeTextView;

    private BaseActivity activity;

    public SortTypeBottomSheetFragment() {
        // Required empty public constructor
    }

    public static SortTypeBottomSheetFragment getNewInstance(int pageType, SortType currentSortType) {
        SortTypeBottomSheetFragment fragment = new SortTypeBottomSheetFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_PAGE_TYPE, pageType);
        bundle.putString(EXTRA_CURRENT_SORT_TYPE, currentSortType.getType().fullName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sort_type_bottom_sheet, container, false);
        ButterKnife.bind(this, rootView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) != Configuration.UI_MODE_NIGHT_YES) {
            rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        int pageType = getArguments().getInt(EXTRA_PAGE_TYPE, PAGE_TYPE_USER);

        switch (pageType) {

            case PAGE_TYPE_MULTICOMMUNITY:
            case PAGE_TYPE_USER:
            case PAGE_TYPE_ANONYMOUS_FRONT_PAGE:
                activeTypeTextView.setVisibility(View.GONE);
                hotTypeTextView.setVisibility(View.GONE);
                break;

            default:
            case PAGE_TYPE_COMMUNITY:
            case PAGE_TYPE_FRONT_PAGE:

                break;
        }

        String currentSortType = getArguments().getString(EXTRA_CURRENT_SORT_TYPE);
        if (currentSortType.equals(SortType.Type.ACTIVE.fullName)) {
            activeTypeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(activeTypeTextView.getCompoundDrawablesRelative()[0], null, AppCompatResources.getDrawable(activity, R.drawable.ic_round_check_circle_day_night_24dp), null);
        } else if (currentSortType.equals(SortType.Type.HOT.fullName)) {
            hotTypeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(hotTypeTextView.getCompoundDrawablesRelative()[0], null, AppCompatResources.getDrawable(activity, R.drawable.ic_round_check_circle_day_night_24dp), null);
        } else if (currentSortType.equals(SortType.Type.NEW.fullName)) {
            newTypeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(newTypeTextView.getCompoundDrawablesRelative()[0], null, AppCompatResources.getDrawable(activity, R.drawable.ic_round_check_circle_day_night_24dp), null);
        } else if (currentSortType.equals(SortType.Type.OLD.fullName)) {
            oldTypeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(oldTypeTextView.getCompoundDrawablesRelative()[0], null, AppCompatResources.getDrawable(activity, R.drawable.ic_round_check_circle_day_night_24dp), null);
        } else if (currentSortType.equals(SortType.Type.TOP.fullName)) {
            topTypeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(topTypeTextView.getCompoundDrawablesRelative()[0], null, AppCompatResources.getDrawable(activity, R.drawable.ic_round_check_circle_day_night_24dp), null);
        } else if (currentSortType.equals(SortType.Type.SCALED.fullName)) {
            scaledTypeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(scaledTypeTextView.getCompoundDrawablesRelative()[0], null, AppCompatResources.getDrawable(activity, R.drawable.ic_round_check_circle_day_night_24dp), null);
        } else if (currentSortType.equals(SortType.Type.CONTROVERSIAL.fullName)) {
            controversialTypeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(controversialTypeTextView.getCompoundDrawablesRelative()[0], null, AppCompatResources.getDrawable(activity, R.drawable.ic_round_check_circle_day_night_24dp), null);
        }

        activeTypeTextView.setOnClickListener(view -> {
            ((SortTypeSelectionCallback) activity).sortTypeSelected(new SortType(SortType.Type.ACTIVE));
            dismiss();
        });

        hotTypeTextView.setOnClickListener(view -> {
            ((SortTypeSelectionCallback) activity).sortTypeSelected(new SortType(SortType.Type.HOT));
            dismiss();
        });

        newTypeTextView.setOnClickListener(view -> {
            ((SortTypeSelectionCallback) activity).sortTypeSelected(new SortType(SortType.Type.NEW));
            dismiss();
        });

        oldTypeTextView.setOnClickListener(view -> {
            ((SortTypeSelectionCallback) activity).sortTypeSelected(new SortType(SortType.Type.OLD));
            dismiss();
        });

        topTypeTextView.setOnClickListener(view -> {
            ((SortTypeSelectionCallback) activity).sortTypeSelected(SortType.Type.TOP.name());
            dismiss();
        });

        scaledTypeTextView.setOnClickListener(view -> {
            ((SortTypeSelectionCallback) activity).sortTypeSelected(new SortType(SortType.Type.SCALED));
            dismiss();
        });

        controversialTypeTextView.setOnClickListener(view -> {
            ((SortTypeSelectionCallback) activity).sortTypeSelected(new SortType(SortType.Type.CONTROVERSIAL));
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
