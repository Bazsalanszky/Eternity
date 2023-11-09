package eu.toldi.infinityforlemmy.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;

public class CustomArrayAdapter extends ArrayAdapter<String> {

    CustomThemeWrapper customThemeWrapper;

    public CustomArrayAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<String> objects, CustomThemeWrapper customThemeWrapper) {
        super(context, textViewResourceId, objects);
        this.customThemeWrapper = customThemeWrapper;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView itemView = (TextView) super.getView(position, convertView, parent);
        itemView.setTextColor(customThemeWrapper.getPrimaryTextColor()); // Set the text color
        itemView.setBackgroundColor(customThemeWrapper.getBackgroundColor()); // Set the background color
        // Apply any other styling as needed
        return itemView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView itemView = (TextView) super.getDropDownView(position, convertView, parent);
        itemView.setTextColor(customThemeWrapper.getPrimaryTextColor()); // Set the text color
        itemView.setBackgroundColor(customThemeWrapper.getBackgroundColor()); // Set the background color
        // Apply any other styling as needed
        return itemView;
    }
}
