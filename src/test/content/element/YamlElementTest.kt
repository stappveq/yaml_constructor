package test.content.element

import content.element.*
import org.junit.Test

class YamlElementTest {

    @Test
    fun `yaml header test`() {
        val header = "my_header"
        assert(YamlHeader(header).asYaml() == "$header:")
    }

    @Test
    fun `yaml anchor test`() {
        val anchorName = "my_anchor"
        assert(YamlAnchor(anchorName).asYaml() == "&$anchorName")
    }

    @Test
    fun `yaml anchor link test`() {
        val anchorName = "my_anchor"
        val anchor = YamlAnchor(anchorName)
        assert(YamlAnchorLink(anchor).asYaml() == "*$anchorName")
    }

    @Test
    fun `yaml anchor override test`() {
        val anchorName = "my_anchor"
        val anchor = YamlAnchor(anchorName)
        assert(YamlAnchorOverride(anchor).asYaml() == "<<: *$anchorName")
    }

    @Test
    fun `yaml array item test`() {
        val itemName = "my_array_item"
        assert(YamlArrayItem(itemName).asYaml() == "- $itemName")
    }

    @Test
    fun `yaml tag test`() {
        val tagName = "my_tag"
        assert(YamlTag(tagName).asYaml() == "!!$tagName")
    }

    @Test
    fun `yaml pair test`() {
        val key = "my_key"
        val value = "my_value"
        assert(YamlPair(key, value).asYaml() == "$key: $value")
        assert(YamlPair(key, value, false).asYaml() == "$key $value")
    }
}