package eu.toldi.infinityforlemmy.markdown;

import android.app.Activity;
import android.content.Context;
import android.text.util.Linkify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.RequestManager;

import org.commonmark.ext.gfm.tables.TableBlock;

import java.util.regex.Pattern;

import eu.toldi.infinityforlemmy.R;
import eu.toldi.infinityforlemmy.customviews.CustomMarkwonAdapter;
import io.noties.markwon.Markwon;
import io.noties.markwon.MarkwonPlugin;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.glide.GlideImagesPlugin;
import io.noties.markwon.inlineparser.BangInlineProcessor;
import io.noties.markwon.inlineparser.HtmlInlineProcessor;
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin;
import io.noties.markwon.linkify.LinkifyPlugin;
import io.noties.markwon.movement.MovementMethodPlugin;
import io.noties.markwon.recycler.MarkwonAdapter;
import io.noties.markwon.recycler.table.TableEntry;
import io.noties.markwon.recycler.table.TableEntryPlugin;
import me.saket.bettermovementmethod.BetterLinkMovementMethod;

public class MarkdownUtils {
    /**
     * Creates a Markwon instance with all the plugins required for processing Reddit's markdown.
     *
     * @return configured Markwon instance
     */
    @NonNull
    public static Markwon createFullRedditMarkwon(@NonNull Activity context,
                                                  @NonNull MarkwonPlugin miscPlugin,
                                                  int markdownColor,
                                                  int spoilerBackgroundColor, RequestManager mGlide,
                                                  @Nullable BetterLinkMovementMethod.OnLinkLongClickListener onLinkLongClickListener, boolean dataSaverEnabled) {
        Markwon result;
        if (dataSaverEnabled) {
            result = Markwon.builder(context)
                    .usePlugin(miscPlugin)
                    .usePlugin(new ScriptRewriteSupportPlugin())
                    .usePlugin(new MarkwonSpoilerPlugin(true))
                    .usePlugin(HtmlPlugin.create())
                    .usePlugin(StrikethroughPlugin.create())
                    .usePlugin(TablePlugin.create(context))
                    .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
                    .usePlugin(new MarkwonLemmyLinkPlugin())
                    .build();
        } else {
            result = Markwon.builder(context)
                    .usePlugin(GlideImagesPlugin.create(new GlideMarkdownLoader(mGlide)))
                    .usePlugin(miscPlugin)
                    .usePlugin(new ScriptRewriteSupportPlugin())
                    .usePlugin(new MarkwonSpoilerPlugin(true))
                    .usePlugin(HtmlPlugin.create())
                    .usePlugin(StrikethroughPlugin.create())
                    .usePlugin(MovementMethodPlugin.create(new SpoilerAwareMovementMethod()
                            .setOnLinkLongClickListener(onLinkLongClickListener)))
                    .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
                    .usePlugin(TablePlugin.create(context))
                    .usePlugin(ClickableGlideImagesPlugin.create(context))
                    .usePlugin(new MarkwonLemmyLinkPlugin())
                    .build();
        }

        return result;
    }

    @NonNull
    public static Markwon createDescriptionMarkwon(Activity context, MarkwonPlugin miscPlugin,
                                                   BetterLinkMovementMethod.OnLinkLongClickListener onLinkLongClickListener, boolean dataSaverEnabled) {
        Markwon result;
        if (dataSaverEnabled) {
            result = Markwon.builder(context)
                    .usePlugin(miscPlugin)
                    .usePlugin(new ScriptRewriteSupportPlugin())
                    .usePlugin(new MarkwonSpoilerPlugin(true))
                    .usePlugin(HtmlPlugin.create())
                    .usePlugin(StrikethroughPlugin.create())
                    .usePlugin(MovementMethodPlugin.create(new SpoilerAwareMovementMethod()
                            .setOnLinkLongClickListener(onLinkLongClickListener)))
                    .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
                    .usePlugin(TablePlugin.create(context))
                    .usePlugin(new MarkwonLemmyLinkPlugin())
                    .build();
        } else {
            result = Markwon.builder(context)
                    .usePlugin(miscPlugin)
                    .usePlugin(new ScriptRewriteSupportPlugin())
                    .usePlugin(new MarkwonSpoilerPlugin(true))
                    .usePlugin(HtmlPlugin.create())
                    .usePlugin(MovementMethodPlugin.create(new SpoilerAwareMovementMethod()
                            .setOnLinkLongClickListener(onLinkLongClickListener)))
                    .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
                    .usePlugin(TablePlugin.create(context))
                    .usePlugin(GlideImagesPlugin.create(context.getApplicationContext()))
                    .usePlugin(new MarkwonLemmyLinkPlugin())
                    .build();
        }
        return result;
    }

    /**
     * Creates a Markwon instance that processes only the links.
     * @return configured Markwon instance
     */
    @NonNull
    public static Markwon createLinksOnlyMarkwon(@NonNull Context context,
                                                  @NonNull MarkwonPlugin miscPlugin,
                                                  @Nullable BetterLinkMovementMethod.OnLinkLongClickListener onLinkLongClickListener) {
        return Markwon.builder(context)
                .usePlugin(MarkwonInlineParserPlugin.create(plugin -> {
                    plugin.excludeInlineProcessor(HtmlInlineProcessor.class);
                    plugin.excludeInlineProcessor(BangInlineProcessor.class);
                }))
                .usePlugin(miscPlugin)
                .usePlugin(MovementMethodPlugin.create(BetterLinkMovementMethod.newInstance().setOnLinkLongClickListener(onLinkLongClickListener)))
                .usePlugin(LinkifyPlugin.create(Linkify.WEB_URLS))
                .build();
    }

    /**
     * Creates a MarkwonAdapter configured with support for tables.
     */
    @NonNull
    public static MarkwonAdapter createTablesAdapter() {
        return MarkwonAdapter.builder(R.layout.adapter_default_entry, R.id.text)
                .include(TableBlock.class, TableEntry.create(builder -> builder
                        .tableLayout(R.layout.adapter_table_block, R.id.table_layout)
                        .textLayoutIsRoot(R.layout.view_table_entry_cell)))
                .build();
    }

    @NonNull
    public static MarkwonAdapter createTablesAndImagesAdapter(@NonNull Context context) {
        return MarkwonAdapter.builder(R.layout.adapter_default_entry, R.id.text)
                .include(TableBlock.class, TableEntry.create(builder -> builder
                        .tableLayout(R.layout.adapter_table_block, R.id.table_layout)
                        .textLayoutIsRoot(R.layout.view_table_entry_cell)))
                .build();
    }

    /**
     * Creates a CustomMarkwonAdapter configured with support for tables.
     */
    @NonNull
    public static CustomMarkwonAdapter createCustomTablesAdapter() {
        return CustomMarkwonAdapter.builder(R.layout.adapter_default_entry, R.id.text)
                .include(TableBlock.class, TableEntry.create(builder -> builder
                        .tableLayout(R.layout.adapter_table_block, R.id.table_layout)
                        .textLayoutIsRoot(R.layout.view_table_entry_cell)))
                .build();
    }

    private static final Pattern emptyPattern = Pattern.compile("!\\[\\]\\((.*?)\\)");
    private static final Pattern nonEmptyPattern = Pattern.compile("!\\[(.*?)\\]\\((.*?)\\)");

}
