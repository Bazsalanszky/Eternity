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
import eu.toldi.infinityforlemmy.adapters.navigationdrawer.PostFilterUsageEmbeddedRecyclerViewAdapter;
import eu.toldi.infinityforlemmy.customtheme.CustomThemeWrapper;
import eu.toldi.infinityforlemmy.customviews.LinearLayoutManagerBugFixed;
import eu.toldi.infinityforlemmy.databinding.ItemPostFilterWithUsageBinding;
import eu.toldi.infinityforlemmy.postfilter.PostFilter;
import eu.toldi.infinityforlemmy.postfilter.PostFilterWithUsage;
import eu.toldi.infinityforlemmy.utils.Utils;

public class PostFilterWithUsageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 1;
    private static final int VIEW_TYPE_POST_FILTER = 2;

    private BaseActivity activity;
    private CustomThemeWrapper customThemeWrapper;
    private final OnItemClickListener onItemClickListener;
    private List<PostFilterWithUsage> postFilterWithUsageList;
    private RecyclerView.RecycledViewPool recycledViewPool;

    public interface OnItemClickListener {
        void onItemClick(PostFilter postFilter);
    }

    public PostFilterWithUsageRecyclerViewAdapter(BaseActivity activity, CustomThemeWrapper customThemeWrapper,
                                                  OnItemClickListener onItemClickListener) {
        this.activity = activity;
        this.customThemeWrapper = customThemeWrapper;
        this.recycledViewPool = new RecyclerView.RecycledViewPool();
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        }
        return VIEW_TYPE_POST_FILTER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_fragment_header, parent, false));
        } else {
            return new PostFilterViewHolder(ItemPostFilterWithUsageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PostFilterViewHolder) {
            ((PostFilterViewHolder) holder).binding.postFilterNameTextViewItemPostFilter.setText(postFilterWithUsageList.get(position - 1).postFilter.name);
            ((PostFilterViewHolder) holder).adapter.setPostFilterUsageList(postFilterWithUsageList.get(position - 1).postFilterUsages);
        }
    }

    @Override
    public int getItemCount() {
        return postFilterWithUsageList == null ? 1 : 1 + postFilterWithUsageList.size();
    }

    public void setPostFilterWithUsageList(List<PostFilterWithUsage> postFilterWithUsageList) {
        this.postFilterWithUsageList = postFilterWithUsageList;
        notifyDataSetChanged();
    }

    private class PostFilterViewHolder extends RecyclerView.ViewHolder {
        ItemPostFilterWithUsageBinding binding;
        PostFilterUsageEmbeddedRecyclerViewAdapter adapter;

        public PostFilterViewHolder(@NonNull ItemPostFilterWithUsageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.postFilterNameTextViewItemPostFilter.setTextColor(customThemeWrapper.getPrimaryTextColor());

            if (activity.typeface != null) {
                binding.postFilterNameTextViewItemPostFilter.setTypeface(activity.typeface);
            }

            binding.getRoot().setOnClickListener(view -> {
                onItemClickListener.onItemClick(postFilterWithUsageList.get(getBindingAdapterPosition() - 1).postFilter);
            });

            binding.postFilterUsageRecyclerViewItemPostFilter.setRecycledViewPool(recycledViewPool);
            binding.postFilterUsageRecyclerViewItemPostFilter.setLayoutManager(new LinearLayoutManagerBugFixed(activity));
            adapter = new PostFilterUsageEmbeddedRecyclerViewAdapter(activity);
            binding.postFilterUsageRecyclerViewItemPostFilter.setAdapter(adapter);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            TextView infoTextView = itemView.findViewById(R.id.info_text_view_item_filter_fragment_header);
            infoTextView.setTextColor(customThemeWrapper.getSecondaryTextColor());
            infoTextView.setCompoundDrawablesWithIntrinsicBounds(Utils.getTintedDrawable(activity, R.drawable.ic_info_preference_24dp, activity.customThemeWrapper.getPrimaryIconColor()), null, null, null);
        }
    }
}