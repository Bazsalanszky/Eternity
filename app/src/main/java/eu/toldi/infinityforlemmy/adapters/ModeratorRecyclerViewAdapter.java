package eu.toldi.infinityforlemmy.adapters;

import eu.toldi.infinityforlemmy.activities.BaseActivity;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.user.BasicUserRecyclerViewAdapter;

public class ModeratorRecyclerViewAdapter extends BasicUserRecyclerViewAdapter {

    private final int mModeratorColor;

    public ModeratorRecyclerViewAdapter(BaseActivity activity, CustomThemeWrapper customThemeWrapper) {
        super(activity, customThemeWrapper);
        mModeratorColor = customThemeWrapper.getModerator();
    }

    @Override
    protected int getUserNameTextColor() {
        return mModeratorColor;
    }
}
