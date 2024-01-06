package eu.toldi.infinityforlemmy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.activities.BaseActivity;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.subreddit.SubredditWithSelection;

public class SelectedSubredditsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private BaseActivity activity;
    private CustomThemeWrapper customThemeWrapper;
    private ArrayList<SubredditWithSelection> subreddits;

    public SelectedSubredditsRecyclerViewAdapter(BaseActivity activity, CustomThemeWrapper customThemeWrapper, ArrayList<SubredditWithSelection> subreddits) {
        this.activity = activity;
        this.customThemeWrapper = customThemeWrapper;
        if (subreddits == null) {
            this.subreddits = new ArrayList<>();
        } else {
            this.subreddits = subreddits;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SubredditViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_subreddit, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SubredditViewHolder) {
            ((SubredditViewHolder) holder).subredditNameTextView.setText(subreddits.get(holder.getBindingAdapterPosition()).getQualifiedName());
            ((SubredditViewHolder) holder).deleteButton.setOnClickListener(view -> {
                subreddits.remove(holder.getBindingAdapterPosition());
                notifyItemRemoved(holder.getBindingAdapterPosition());
            });
        }
    }

    @Override
    public int getItemCount() {
        return subreddits.size();
    }

    public void addSubreddits(ArrayList<SubredditWithSelection> newSubreddits) {
        int oldSize = subreddits.size();
        for (SubredditWithSelection subreddit : newSubreddits) {
            if (!subreddits.contains(subreddit)) {
                subreddits.add(subreddit);
            }
        }
        notifyItemRangeInserted(oldSize, newSubreddits.size());
    }

    public void addUserInSubredditType(String username) {
        /*subreddits.add(username);
        notifyItemInserted(subreddits.size());*/
    }

    public ArrayList<SubredditWithSelection> getSubreddits() {
        return subreddits;
    }

    class SubredditViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.subreddit_name_item_selected_subreddit)
        TextView subredditNameTextView;
        @BindView(R.id.delete_image_view_item_selected_subreddit)
        ImageView deleteButton;

        public SubredditViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            subredditNameTextView.setTextColor(customThemeWrapper.getPrimaryIconColor());
            deleteButton.setColorFilter(customThemeWrapper.getPrimaryIconColor(), android.graphics.PorterDuff.Mode.SRC_IN);

            if (activity.typeface != null) {
                subredditNameTextView.setTypeface(activity.typeface);
            }
        }
    }
}
