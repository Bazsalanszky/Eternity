package eu.toldi.infinityforlemmy.bottomsheetfragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import me.saket.bettermovementmethod.BetterLinkMovementMethod;
import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.activities.LinkResolverActivity;
import eu.toldi.infinityforlemmy.activities.MainActivity;
import eu.toldi.infinityforlemmy.customviews.LandscapeExpandedRoundedBottomSheetDialogFragment;
import eu.toldi.infinityforlemmy.databinding.FragmentRedditApiInfoBottomSheetBinding;
import eu.toldi.infinityforlemmy.utils.Utils;

public class RedditAPIInfoBottomSheetFragment extends LandscapeExpandedRoundedBottomSheetDialogFragment {

    private MainActivity mainActivity;

    public RedditAPIInfoBottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentRedditApiInfoBottomSheetBinding binding = FragmentRedditApiInfoBottomSheetBinding.inflate(inflater, container, false);

        if (mainActivity != null && mainActivity.typeface != null) {
            Utils.setFontToAllTextViews(binding.getRoot(), mainActivity.typeface);
        }

        binding.getRoot().setNestedScrollingEnabled(true);

        SpannableString message = new SpannableString(getString(R.string.reddit_api_info, "https://www.reddit.com/r/reddit/comments/145bram/addressing_the_community_about_changes_to_our_api", "https://www.reddit.com/r/Infinity_For_Reddit/comments/147bhsg/the_future_of_infinity"));
        Linkify.addLinks(message, Linkify.WEB_URLS);
        binding.messageTextViewRedditApiInfoBottomSheetFragment.setText(message);
        binding.messageTextViewRedditApiInfoBottomSheetFragment.setMovementMethod(BetterLinkMovementMethod.newInstance().setOnLinkClickListener((textView, url) -> {
            Intent intent = new Intent(mainActivity, LinkResolverActivity.class);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            return true;
        }));
        binding.messageTextViewRedditApiInfoBottomSheetFragment.setLinkTextColor(getResources().getColor(R.color.colorAccent));

        binding.doNotShowThisAgainTextView.setOnClickListener(view -> {
            binding.doNotShowThisAgainCheckBox.toggle();
        });

        binding.continueButtonRedditApiInfoBottomSheetFragment.setOnClickListener(view -> {
            if (binding.doNotShowThisAgainCheckBox.isChecked()) {
                mainActivity.doNotShowRedditAPIInfoAgain();
            }
            dismiss();
        });

        return binding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }
}