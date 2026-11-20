package demo.page.editor

import io.github.shijianjs.ktjs.demo.page.DemoPage
import io.github.shijianjs.ktjs.dom.webcomponent.defineIfMissing
import web.components.CustomElementReference
import web.dom.TagName
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.exampleBox
import io.github.shijianjs.ktjs.demo.page.grid
import io.github.shijianjs.ktjs.demo.page.sectionBlock
import io.github.shijianjs.ktjs.dom.element.inlineStyle
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.logicflow.LogicFlow
import io.github.shijianjs.ktjs.logicflow.createLogicFlow
import io.github.shijianjs.ktjs.logicflow.core.LogicFlowEvents
import io.github.shijianjs.ktjs.logicflow.core.LogicFlowGrid
import io.github.shijianjs.ktjs.logicflow.core.edgeConfig
import io.github.shijianjs.ktjs.logicflow.core.graphConfigData
import io.github.shijianjs.ktjs.logicflow.core.gridConfig
import io.github.shijianjs.ktjs.logicflow.core.logicFlowOptions
import io.github.shijianjs.ktjs.logicflow.core.nodeConfig
import io.github.shijianjs.ktjs.vaadin.button.ButtonElement
import io.github.shijianjs.ktjs.vaadin.button.createButton
import kotlin.js.JsAny
import web.animations.requestAnimationFrame
import web.cssom.FontFamily
import web.cssom.FontSize
import web.cssom.Margin
import web.cssom.WhiteSpace
import web.cssom.px
import web.dom.clickEvent
import web.events.addHandler
import web.html.HTMLElement
import web.html.HtmlTagName

/** Every string this page shows, in one language. */
private class EditorTexts(
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

private val editorEnglish: EditorTexts = EditorTexts(
    title = "Editor",
    summary = "The real LogicFlow engine with typed options and typed graph data: drag the nodes " +
        "around, connect them from an anchor, then export the graph as JSON.",
    canvasHeading = "Canvas",
    jsonHeading = "The graph as JSON",
    hint = "Every interaction goes through the wrapper: `createLogicFlow`, `render`, `addNode`, " +
        "`deleteNode`, `zoom`, `fitView` and the `LogicFlowEvents` constants.",
    emptyJson = "Export the graph and its JSON shows up here.",
    labels = listOf("Add node", "Delete selected", "Undo", "Redo", "Zoom in", "Zoom out", "Fit view", "Export JSON"),
    counts = "Nodes / edges",
    zoom = "Zoom",
    lastEvent = "Last event",
    noEvent = "none yet",
    sampleTexts = listOf("Order received", "Check stock", "In stock?", "Done"),
)

private val editorChinese: EditorTexts = EditorTexts(
    title = "编辑器",
    summary = "真实 LogicFlow 引擎，选项与图数据都有类型：拖动节点、从锚点连出线，最后把图导出成 JSON。",
    canvasHeading = "画布",
    jsonHeading = "图数据的 JSON",
    hint = "页面上的操作全走封装：`createLogicFlow`、`render`、`addNode`、`deleteNode`、`zoom`、" +
        "`fitView`，以及 `LogicFlowEvents` 常量。",
    emptyJson = "导出后，图数据的 JSON 会显示在这里。",
    labels = listOf("新增节点", "删除选中", "撤销", "重做", "放大", "缩小", "自适应", "导出 JSON"),
    counts = "节点 / 连线",
    zoom = "缩放",
    lastEvent = "最近事件",
    noEvent = "暂无",
    sampleTexts = listOf("收到订单", "检查库存", "有库存吗", "完成"),
)

private val editorTexts: Translations<EditorTexts> = texts(
    Langs.EN to editorEnglish,
    Langs.ZH_HANS to editorChinese,
)

/** One toolbar button: the command it runs on the editor. */
private class Action(val id: String, val run: (EditorPage, LogicFlow) -> Unit)

private val ACTIONS: List<Action> = listOf(
    Action("add") { page, editor -> page.addNode(editor) },
    Action("delete") { page, editor -> page.deleteSelected(editor) },
    Action("undo") { page, editor -> editor.undo(); page.refresh(editor) },
    Action("redo") { page, editor -> editor.redo(); page.refresh(editor) },
    Action("zoomIn") { page, editor -> editor.zoom(true); page.refresh(editor) },
    Action("zoomOut") { page, editor -> editor.zoom(false); page.refresh(editor) },
    Action("fit") { page, editor -> editor.fitView(); page.refresh(editor) },
    Action("export") { page, editor -> page.exportJson(editor) },
)

/**
 * The editor page: a toolbar, the canvas, the document read back as HTML — here, the graph as JSON.
 *
 * LogicFlow needs its own stylesheet. `index.html` links `lf/index.css` (that is how the file ships),
 * but the canvas lives in this page's shadow tree and document styles do not cross that boundary,
 * so the page re-hosts the sheet through [io.github.shijianjs.ktjs.demo.page.DemoPage.addLinkedSheet].
 */
internal class EditorPage : DemoPage() {

    private val texts = Binder(editorTexts)

    /** The strings the readouts and the sample graph use; kept in step with the switch. */
    private var current: EditorTexts = editorEnglish

    private var editor: LogicFlow? = null

    /** Counts the nodes the toolbar added, so a click is visible even when the canvas looks alike. */
    private var added = 0

    private val canvas: HTMLElement = HtmlTagName.div {
        setAttribute("data-canvas", "")
        inlineStyle.minHeight = 460.px
    }

    /** Word counts, zoom and last event: one line under the toolbar, marked for the e2e suite. */
    private val status: HTMLElement = HtmlTagName.p { setAttribute("data-status", "") }

    private val json: HTMLElement = HtmlTagName.pre {
        setAttribute("data-json", "")
        inlineStyle.apply {
            margin = Margin(0.px, 0.px)
            fontFamily = FontFamily.monospace
            fontSize = 13.px
            whiteSpace = WhiteSpace.preWrap
        }
    }

    /** One button per action, each tagged so the end-to-end suite can address it by name. */
    private val buttons: List<ButtonElement> = ACTIONS.map { action ->
        createButton { setAttribute("data-action", action.id) }
    }


    init {
        addLinkedSheet("lf/index.css")

        val section = sectionBlock()
        texts.bind(section.heading, { it.canvasHeading })
        texts.bind(section.hint, { it.hint })
        section.body.append(
            grid(minColumnWidth = 150, *buttons.toTypedArray()),
            status,
            exampleBox(null, canvas),
        )

        val output = sectionBlock()
        texts.bind(output.heading, { it.jsonHeading })
        output.body.append(exampleBox(null, json))

        body.append(section.root, output.root)
        json.textContent = current.emptyJson

        buttons.forEachIndexed { index, button ->
            val action = ACTIONS[index]
            button.clickEvent.addHandler {
                val target = editor ?: return@addHandler
                action.run(this, target)
            }
        }

        createOnceMounted()
    }

    /** Creates the editor as soon as the canvas is in the document. */
    private fun createOnceMounted() {
        if (editor != null) return
        if (!canvas.isConnected) {
            requestAnimationFrame { _ -> createOnceMounted() }
            return
        }
        val created = createLogicFlow(
            logicFlowOptions {
                container = canvas
                width = 1060.0
                height = 460.0
                grid = LogicFlowGrid(gridConfig { size = 20.0 })
                history = true
                edgeType = "polyline"
                snapline = true
            },
        )
        editor = created

        created.on(LogicFlowEvents.NodeClick) { event ->
            status.setAttribute("data-event", event.data?.let { stringify(it).take(60) } ?: current.noEvent)
            refresh(created)
        }
        created.on(LogicFlowEvents.EdgeAdd) { refresh(created) }
        created.on(LogicFlowEvents.NodeAdd) { refresh(created) }
        created.on(LogicFlowEvents.GraphRendered) { refresh(created) }

        created.render(sample(current))
        refresh(created)
    }

    /** Re-reads the graph: node and edge counts, the zoom level, and the event readout. */
    fun refresh(editor: LogicFlow) {
        val data = editor.getGraphRawData()
        val scale = editor.getTransform().SCALE_X
        status.textContent = "${current.counts}: ${data.nodes.size} / ${data.edges.size}" +
            " · ${current.zoom}: ${(scale * 100).toInt()}%"
        status.setAttribute("data-nodes", "${data.nodes.size}")
        status.setAttribute("data-edges", "${data.edges.size}")
    }

    /** Adds one node to the right of the sample graph; the id is unique per click. */
    fun addNode(editor: LogicFlow) {
        added += 1
        editor.addNode(
            nodeConfig {
                id = "added-$added"
                type = "rect"
                x = 620.0
                y = 160.0 + added * 40.0
                text = "New task $added"
            },
        )
        refresh(editor)
    }

    /** Deletes whatever the user has selected, nodes and edges alike. */
    fun deleteSelected(editor: LogicFlow) {
        val selected = editor.getSelectElements()
        selected.nodes.forEach { editor.deleteNode(it.id) }
        selected.edges.forEach { editor.deleteEdge(it.id) }
        status.setAttribute("data-select", "${selected.nodes.size}")
        refresh(editor)
    }

    /** Writes the whole graph into the JSON panel. */
    fun exportJson(editor: LogicFlow) {
        json.textContent = stringify(editor.getGraphRawData())
    }

    override fun applyTranslation(lang: Lang) {
        current = editorTexts[lang]
        setTitle(current.title)
        setSummary(current.summary)
        texts.apply(lang)
        buttons.forEachIndexed { index, button ->
            button.textContent = current.labels.getOrElse(index) { "" }
        }
        json.textContent = current.emptyJson
        editor?.let { refresh(it) }
    }

    companion object : CustomElementReference<EditorPage>(EditorPage::class.js) {
        val NAME: TagName<EditorPage> = TagName("logic-flow-editor-page")

        fun define() {
            defineIfMissing(NAME, this)
        }
    }
}

/** `JSON.stringify(value, null, 2)`: the standard formatter, so the panel shows the graph as JSON. */
private val stringifyJson: (JsAny) -> String = js("(value) => JSON.stringify(value, null, 2)")

private fun stringify(value: JsAny): String = stringifyJson(value)

/** The sample graph: a four-node order flow, labelled in the language the page is showing. */
private fun sample(texts: EditorTexts) = graphConfigData {
    nodes = arrayOf(
        nodeConfig {
            id = "n1"
            type = "rect"
            x = 180.0
            y = 120.0
            text = texts.sampleTexts[0]
        },
        nodeConfig {
            id = "n2"
            type = "rect"
            x = 420.0
            y = 120.0
            text = texts.sampleTexts[1]
        },
        nodeConfig {
            id = "n3"
            type = "diamond"
            x = 420.0
            y = 260.0
            text = texts.sampleTexts[2]
        },
        nodeConfig {
            id = "n4"
            type = "circle"
            x = 420.0
            y = 380.0
            text = texts.sampleTexts[3]
        },
    )
    edges = arrayOf(
        edgeConfig {
            id = "e1"
            type = "polyline"
            sourceNodeId = "n1"
            targetNodeId = "n2"
        },
        edgeConfig {
            id = "e2"
            type = "polyline"
            sourceNodeId = "n2"
            targetNodeId = "n3"
        },
        edgeConfig {
            id = "e3"
            type = "polyline"
            sourceNodeId = "n3"
            targetNodeId = "n4"
        },
    )
}

/** Creates a defined `<logic-flow-editor-page>`. */
internal fun newEditorPage(): EditorPage {
    EditorPage.define()
    return EditorPage.NAME()
}
