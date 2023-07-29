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
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.activities.BaseActivity;
import eu.toldi.infinityforlemmy.customviews.LandscapeExpandedRoundedBottomSheetDialogFragment;
import eu.toldi.infinityforlemmy.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostTypeBottomSheetFragment extends LandscapeExpandedRoundedBottomSheetDialogFragment {

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_LINK = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_GALLERY = 4;
    public static final int TYPE_POLL = 5;
    @BindView(R.id.text_type_linear_layout_post_type_bottom_sheet_fragment)
    TextView textTypeTextView;
    @BindView(R.id.link_type_linear_layout_post_type_bottom_sheet_fragment)
    TextView linkTypeTextView;
    @BindView(R.id.image_type_linear_layout_post_type_bottom_sheet_fragment)
    TextView imageTypeTextView;
    private BaseActivity activity;

    public PostTypeBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_post_type_bottom_sheet, container, false);
        ButterKnife.bind(this, rootView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                && (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) != Configuration.UI_MODE_NIGHT_YES) {
            rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        textTypeTextView.setOnClickListener(view -> {
            ((PostTypeSelectionCallback) activity).postTypeSelected(TYPE_TEXT);
            dismiss();
        });

        linkTypeTextView.setOnClickListener(view -> {
            ((PostTypeSelectionCallback) activity).postTypeSelected(TYPE_LINK);
            dismiss();
        });

        imageTypeTextView.setOnClickListener(view -> {
            ((PostTypeSelectionCallback) activity).postTypeSelected(TYPE_IMAGE);
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
        this.activity = (BaseActivity) context;
    }

    public interface PostTypeSelectionCallback {
        void postTypeSelected(int postType);
    }

}
