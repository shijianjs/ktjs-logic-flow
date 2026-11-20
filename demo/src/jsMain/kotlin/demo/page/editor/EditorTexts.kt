package demo.page.editor

import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.dom.element.invoke

/** Every string this page shows, in one language. */
internal class EditorTexts(
    val title: String,
    val summary: String,
    val canvasHeading: String,
    val jsonHeading: String,
    val hint: String,
    val emptyJson: String,
    /** The action labels, in the order of [ACTIONS]. */
    val labels: List<String>,
    /** The status line's two nouns: `Nodes / edges: 4 / 3 · Zoom: 100%`. */
    val counts: String,
    val zoom: String,
    /** The event readout's two parts: the label and the "nothing happened yet" text. */
    val lastEvent: String,
    val noEvent: String,
    /** The node texts of the sample graph. */
    val sampleTexts: List<String>,
)
