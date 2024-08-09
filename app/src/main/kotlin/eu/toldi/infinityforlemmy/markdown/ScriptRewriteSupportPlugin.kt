package eu.toldi.infinityforlemmy.markdown

import io.noties.markwon.AbstractMarkwonPlugin

// Source copied from https://github.com/LemmyNet/jerboa/blob/main/app/src/main/java/com/jerboa/util/markwon/ScriptRewriteSupportPlugin.kt

class ScriptRewriteSupportPlugin : AbstractMarkwonPlugin() {
    override fun processMarkdown(markdown: String): String =
        super.processMarkdown(
            if (markdown.contains("^") || markdown.contains("~")) {
                rewriteLemmyScriptToMarkwonScript(markdown)
            } else { // Fast path: if there are no markdown characters, we don't need to do anything
                markdown
            },
        )

    companion object {
        val SUPERSCRIPT_RGX = Regex("""\^([^\n^]+)\^""")
        val SUBSCRIPT_RGX = Regex("""(?<!~)~([^\n~]+)~""")

        fun rewriteLemmyScriptToMarkwonScript(text: String): String =
            text
                .replace(SUPERSCRIPT_RGX, "<sup>$1</sup>")
                .replace(SUBSCRIPT_RGX, "<sub>$1</sub>")
    }
}