package eu.toldi.infinityforlemmy.markdown


import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.URLSpan
import android.text.util.Linkify
import eu.toldi.infinityforlemmy.utils.LemmyUtils
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonPlugin
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.SpannableBuilder
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.core.CoreProps
import org.commonmark.node.Link
import java.util.regex.Pattern

// Source : https://github.com/dessalines/jerboa/blob/main/app/src/main/java/com/jerboa/util/markwon/MarkwonLemmyLinkPlugin.kt
class MarkwonLemmyLinkPlugin : AbstractMarkwonPlugin() {

    companion object {
        /**
         * pattern that matches all valid communities; intended to be loose
         */
        const val communityPatternFragment: String = """[a-zA-Z0-9_]{3,}"""

        /**
         * pattern to match all valid instances
         */
        const val instancePatternFragment: String =
                """([a-zA-Z0-9][a-zA-Z0-9-]{0,61}[a-zA-Z0-9]\.)+[a-zA-Z]{2,}"""

        /**
         * pattern to match all valid usernames
         */
        const val userPatternFragment: String = """[a-zA-Z0-9_]{3,}"""

        /**
         * Pattern to match lemmy's unique community pattern, e.g. !commmunity[@instance]
         */
        val lemmyCommunityPattern: Pattern =
            Pattern.compile("(?<!\\S)!($communityPatternFragment)(?:@($instancePatternFragment))\\b")

        /**
         * Pattern to match lemmy's unique user pattern, e.g. @user[@instance]
         */
        val lemmyUserPattern: Pattern =
            Pattern.compile("(?<!\\S)@($userPatternFragment)(?:@($instancePatternFragment))\\b")

    }
    override fun configure(registry: MarkwonPlugin.Registry) {
        registry.require(CorePlugin::class.java) { it.addOnTextAddedListener(LemmyTextAddedListener()) }
    }

    private class LemmyTextAddedListener : CorePlugin.OnTextAddedListener {
        override fun onTextAdded(visitor: MarkwonVisitor, text: String, start: Int) {
            // we will be using the link that is used by markdown (instead of directly applying URLSpan)
            val spanFactory = visitor.configuration().spansFactory().get(
                    Link::class.java,
            ) ?: return

            // don't re-use builder (thread safety achieved for
            // render calls from different threads and ... better performance)
            val builder = SpannableStringBuilder(text)
            if (addLinks(builder)) {
                // target URL span specifically
                val spans = builder.getSpans(0, builder.length, URLSpan::class.java)
                if (!spans.isNullOrEmpty()) {
                    val renderProps = visitor.renderProps()
                    val spannableBuilder = visitor.builder()
                    for (span in spans) {
                        CoreProps.LINK_DESTINATION[renderProps] = if (span.url.startsWith("!")) { LemmyUtils.qualifiedCommunityName2ActorId(span.url.drop(1)) } else { LemmyUtils.qualifiedUserName2ActorId(span.url.drop(1)) }
                        SpannableBuilder.setSpans(
                                spannableBuilder,
                                spanFactory.getSpans(visitor.configuration(), renderProps),
                                start + builder.getSpanStart(span),
                                start + builder.getSpanEnd(span),
                        )
                    }
                }
            }
        }

        fun addLinks(text: Spannable): Boolean {
            val communityLinkAdded = Linkify.addLinks(text, lemmyCommunityPattern, null)
            val userLinkAdded = Linkify.addLinks(text, lemmyUserPattern, null)

            return communityLinkAdded || userLinkAdded
        }
    }
}