package eu.toldi.infinityforlemmy.adapters;

import eu.toldi.infinityforlemmy.activities.BaseActivity;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.user.BasicUserRecyclerViewAdapter;

public class AdminRecyclerViewAdapter extends BasicUserRecyclerViewAdapter {

    CustomThemeWrapper mCustomThemeWrapper;

    public AdminRecyclerViewAdapter(BaseActivity activity, CustomThemeWrapper customThemeWrapper) {
        super(activity, customThemeWrapper);
        mCustomThemeWrapper = customThemeWrapper;
    }

    @Override
    protected int getUserNameTextColor() {
        return mCustomThemeWrapper.getAdmin();
    }


}
