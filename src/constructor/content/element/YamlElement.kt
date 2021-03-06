package constructor.content.element

import constructor.constant.YamlSymbol
import constructor.content.base.YamlContent
import constructor.content.base.YamlContentBuilder

/**
 * Base class for all yaml elements
 */
abstract class YamlElement : YamlContent {

    override fun asYaml(): String {
        val contentBuilder = YamlContentBuilder()
        onContentBuild(contentBuilder)
        return contentBuilder.build()
    }

    /**
     * Called when content build is required.
     * When realizing this function all the content should be added via [builder]
     */
    protected abstract fun onContentBuild(builder: YamlContentBuilder)
}

/**
 * Yaml element of type # [comment]
 */
class YamlComment(val comment: String) : YamlElement() {
    override fun onContentBuild(builder: YamlContentBuilder) {
        builder.commentSymbol()
        builder.indentSymbol()
        builder.content(comment)
    }
}

/**
 * Yaml element of type [header]:
 */
class YamlHeader(val header: String) : YamlElement() {
    override fun onContentBuild(builder: YamlContentBuilder) {
        builder.content(header)
        builder.colonSymbol()
    }
}

/**
 * Yaml element of type [header]: !![tag]
 * @param withColonSeparator if false element will be of type [header] !![tag]
 */
class YamlTaggedHeader(
    val header: String,
    val tag: YamlTag,
    val withColonSeparator: Boolean = true
) : YamlElement() {
    override fun onContentBuild(builder: YamlContentBuilder) {
        builder.content(header)
        if (withColonSeparator) {
            builder.colonSymbol()
        }
        builder.indentSymbol()
        builder.content(tag.asYaml())
    }
}

/**
 * Yaml element of type &[anchorName]
 */
class YamlAnchor(val anchorName: String) : YamlElement() {
    override fun onContentBuild(builder: YamlContentBuilder) {
        builder.anchorSymbol()
        builder.content(anchorName)
    }
}

/**
 * Yaml element of type - &${anchor.name}
 */
class YamlArrayAnchor(val anchor: YamlAnchor) : YamlElement() {
    override fun onContentBuild(builder: YamlContentBuilder) {
        builder.arrayPrefixSymbol()
        builder.indentSymbol()
        builder.content(anchor.asYaml())
    }
}

/**
 * Yaml element of type *${anchor.name}
 */
class YamlAnchorLink(val anchor: YamlAnchor) : YamlElement() {
    override fun onContentBuild(builder: YamlContentBuilder) {
        builder.anchorLinkSymbol()
        builder.content(anchor.anchorName)
    }
}

/**
 * Yaml element of type <<: *${anchor.name}
 */
class YamlAnchorOverride(val anchor: YamlAnchor) : YamlElement() {
    override fun onContentBuild(builder: YamlContentBuilder) {
        builder.anchorOverrideSymbol()
        builder.colonSymbol()
        builder.indentSymbol()
        builder.content(YamlAnchorLink(anchor).asYaml())
    }
}

/**
 * Yaml element of type - [itemName]
 */
class YamlArrayItem(val itemName: String) : YamlElement() {
    override fun onContentBuild(builder: YamlContentBuilder) {
        builder.arrayPrefixSymbol()
        builder.indentSymbol()
        builder.content(itemName)
    }
}

/**
 * Yaml element of type !![tagName]
 */
class YamlTag(val tagName: String) : YamlElement() {
    override fun onContentBuild(builder: YamlContentBuilder) {
        builder.tagSymbol()
        builder.content(tagName)
    }
}

/**
 * Yaml element of type [key]: [value]
 * @param withColonSeparator if false the element will be of type [key] [value]
 * @param withQuotedValue if true the element will be of type [key]: "[value]"
 */
class YamlPair(
    val key: String,
    val value: String,
    val withColonSeparator: Boolean = true,
    val withQuotedValue: Boolean = false
) : YamlElement() {
    override fun onContentBuild(builder: YamlContentBuilder) {
        builder.content(key)
        if (withColonSeparator) {
            builder.colonSymbol()
        }
        builder.indentSymbol()
        builder.content(valueContent())
    }

    private fun valueContent(): String {
        return if (withQuotedValue) "\"$value\"" else value
    }
}

/**
 * Yaml element of type
 * $element[0]
 * $element[1]
 * ...
 */
class YamlMultiLine(val lines: List<String>) : YamlElement() {
    override fun onContentBuild(builder: YamlContentBuilder) {
        lines
            .forEachIndexed { index, line ->
                builder.content(line)
                if (index != lines.lastIndex) {
                    builder.newLineSymbol()
                }
            }
    }
}

/**
 * Yaml element of type
 * $element[0]
 *   $element[1]
 *     $element[2]
 * ...
 */
class YamlNestedLines(val lines: List<String>) : YamlElement() {
    override fun onContentBuild(builder: YamlContentBuilder) {
        lines
            .forEachIndexed { index, line ->
                var indents = ""
                repeat(index) { indents += YamlSymbol.TAB }
                builder.content(line.prependIndent(indents))
                if (index != lines.lastIndex) {
                    builder.newLineSymbol()
                }
            }
    }
}