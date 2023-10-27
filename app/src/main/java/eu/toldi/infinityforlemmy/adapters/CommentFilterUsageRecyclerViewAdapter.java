package eu.toldi.infinityforlemmy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.activities.BaseActivity;
import eu.toldi.infinityforlemmy.commentfilter.CommentFilterUsage;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;


public class CommentFilterUsageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CommentFilterUsage> commentFilterUsages;
    private BaseActivity activity;
    private CustomThemeWrapper customThemeWrapper;
    private CommentFilterUsageRecyclerViewAdapter.OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onClick(CommentFilterUsage commentFilterUsage);
    }

    public CommentFilterUsageRecyclerViewAdapter(BaseActivity activity, CustomThemeWrapper customThemeWrapper,
                                                 CommentFilterUsageRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        this.activity = activity;
        this.customThemeWrapper = customThemeWrapper;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentFilterUsageRecyclerViewAdapter.CommentFilterUsageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_filter_usage, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CommentFilterUsage commentFilterUsage = commentFilterUsages.get(position);
        switch (commentFilterUsage.usage) {
            case CommentFilterUsage.SUBREDDIT_TYPE:
                ((CommentFilterUsageRecyclerViewAdapter.CommentFilterUsageViewHolder) holder).usageTextView.setText(activity.getString(R.string.post_filter_usage_community, commentFilterUsage.nameOfUsage));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return commentFilterUsages == null ? 0 : commentFilterUsages.size();
    }

    public void setCommentFilterUsages(List<CommentFilterUsage> commentFilterUsages) {
        this.commentFilterUsages = commentFilterUsages;
        notifyDataSetChanged();
    }

    private class CommentFilterUsageViewHolder extends RecyclerView.ViewHolder {
        TextView usageTextView;

        public CommentFilterUsageViewHolder(@NonNull View itemView) {
            super(itemView);
            usageTextView = (TextView) itemView;

            usageTextView.setTextColor(customThemeWrapper.getPrimaryTextColor());

            if (activity.typeface != null) {
                usageTextView.setTypeface(activity.typeface);
            }

            usageTextView.setOnClickListener(view -> {
                onItemClickListener.onClick(commentFilterUsages.get(getBindingAdapterPosition()));
            });
        }
    }
}