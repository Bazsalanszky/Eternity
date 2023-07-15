package eu.toldi.infinityforlemmy.bottomsheetfragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.activities.BaseActivity;
import eu.toldi.infinityforlemmy.activities.CustomizeThemeActivity;
import eu.toldi.infinityforlemmy.customviews.LandscapeExpandedRoundedBottomSheetDialogFragment;
import eu.toldi.infinityforlemmy.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateThemeBottomSheetFragment extends LandscapeExpandedRoundedBottomSheetDialogFragment {

    @BindView(R.id.import_theme_text_view_create_theme_bottom_sheet_fragment)
    TextView importTextView;
    @BindView(R.id.light_theme_text_view_create_theme_bottom_sheet_fragment)
    TextView lightThemeTextView;
    @BindView(R.id.dark_theme_text_view_create_theme_bottom_sheet_fragment)
    TextView darkThemeTextView;
    @BindView(R.id.amoled_theme_text_view_create_theme_bottom_sheet_fragment)
    TextView amoledThemeTextView;
    private BaseActivity activity;

    public interface SelectBaseThemeBottomSheetFragmentListener {
        void importTheme();
    }

    public CreateThemeBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_theme_bottom_sheet, container, false);
        ButterKnife.bind(this, rootView);

        importTextView.setOnClickListener(view -> {
            ((SelectBaseThemeBottomSheetFragmentListener) activity).importTheme();
            dismiss();
        });

        lightThemeTextView.setOnClickListener(view -> {
            Intent intent = new Intent(activity, CustomizeThemeActivity.class);
            intent.putExtra(CustomizeThemeActivity.EXTRA_CREATE_THEME, true);
            intent.putExtra(CustomizeThemeActivity.EXTRA_IS_PREDEFIINED_THEME, true);
            intent.putExtra(CustomizeThemeActivity.EXTRA_THEME_NAME, getString(R.string.theme_name_indigo));
            startActivity(intent);
            dismiss();
        });

        darkThemeTextView.setOnClickListener(view -> {
            Intent intent = new Intent(activity, CustomizeThemeActivity.class);
            intent.putExtra(CustomizeThemeActivity.EXTRA_CREATE_THEME, true);
            intent.putExtra(CustomizeThemeActivity.EXTRA_IS_PREDEFIINED_THEME, true);
            intent.putExtra(CustomizeThemeActivity.EXTRA_THEME_NAME, getString(R.string.theme_name_indigo_dark));
            startActivity(intent);
            dismiss();
        });

        amoledThemeTextView.setOnClickListener(view -> {
            Intent intent = new Intent(activity, CustomizeThemeActivity.class);
            intent.putExtra(CustomizeThemeActivity.EXTRA_CREATE_THEME, true);
            intent.putExtra(CustomizeThemeActivity.EXTRA_IS_PREDEFIINED_THEME, true);
            intent.putExtra(CustomizeThemeActivity.EXTRA_THEME_NAME, getString(R.string.theme_name_indigo_amoled));
            startActivity(intent);
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
