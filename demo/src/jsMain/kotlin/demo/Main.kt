package demo

import io.github.shijianjs.ktjs.logicflow.LogicFlow
import io.github.shijianjs.ktjs.logicflow.createLogicFlow
import io.github.shijianjs.ktjs.logicflow.core.LogicFlowEvents
import io.github.shijianjs.ktjs.logicflow.core.LogicFlowGrid
import io.github.shijianjs.ktjs.logicflow.core.edgeConfig
import io.github.shijianjs.ktjs.logicflow.core.graphConfigData
import io.github.shijianjs.ktjs.logicflow.core.gridConfig
import io.github.shijianjs.ktjs.logicflow.core.logicFlowOptions
import io.github.shijianjs.ktjs.logicflow.core.nodeConfig
import kotlin.js.JsAny
import web.cssom.ClassName
import web.dom.document
import web.events.EventHandler
import web.html.HTMLElement
import web.html.HtmlTagName

private const val PROJECT_NAME = "ktjs-logic-flow"

private val texts = Texts(
    mapOf(
        "h1" to ("$PROJECT_NAME demo" to "$PROJECT_NAME 演示"),
        "intro" to (
            "A flow-chart editor built with the real LogicFlow engine: drag the nodes around, connect " +
                "them by dragging an anchor, then export the graph as JSON." to
                "用真实 LogicFlow 引擎搭的流程图编辑器：拖动节点、从锚点拖出连线，最后把图数据导出成 JSON。"
        ),
        "add" to ("Add node" to "新增节点"),
        "delete" to ("Delete selected" to "删除选中"),
        "undo" to ("Undo" to "撤销"),
        "redo" to ("Redo" to "重做"),
        "zoomIn" to ("Zoom in" to "放大"),
        "zoomOut" to ("Zoom out" to "缩小"),
        "fit" to ("Fit view" to "自适应"),
        "export" to ("Export JSON" to "导出 JSON"),
        "status" to ("Nodes / edges" to "节点 / 连线"),
        "zoom" to ("Zoom" to "缩放"),
        "empty" to (
            "Export the graph and its JSON shows up here." to
                "导出后，图数据的 JSON 会显示在这里。"
        ),
        "hint" to (
            "Every interaction goes through the wrapper: `createLogicFlow`, `render`, `addNode`, " +
                "`deleteNode`, `zoom`, `fitView` and the `LogicFlowEvents` constants." to
                "页面上的操作全走 wrapper：`createLogicFlow`、`render`、`addNode`、`deleteNode`、" +
                "`zoom`、`fitView`，以及 `LogicFlowEvents` 常量。"
        ),
        "event" to ("Last event" to "最近事件"),
        "none" to ("none yet" to "暂无"),
    ),
)

/** The counter the page shows, so a click is visible even when the canvas looks the same. */
private var added = 0

private val jsonStringify: (JsAny) -> String = js("(value) => JSON.stringify(value, null, 2)")

private fun stringify(value: JsAny): String = jsonStringify(value)

private fun sample() = graphConfigData {
    nodes = arrayOf(
        nodeConfig {
            id = "n1"
            type = "rect"
            x = 180.0
            y = 120.0
            text = "Order received"
        },
        nodeConfig {
            id = "n2"
            type = "rect"
            x = 420.0
            y = 120.0
            text = "Check stock"
        },
        nodeConfig {
            id = "n3"
            type = "diamond"
            x = 420.0
            y = 260.0
            text = "In stock?"
        },
        nodeConfig {
            id = "n4"
            type = "circle"
            x = 420.0
            y = 380.0
            text = "Done"
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

fun main() {
    document.body?.apply {
        className = ClassName("page")
    }

    val page = document.body ?: return
    val header = document.createElement(HtmlTagName.div).apply { className = ClassName("header") }
    header.appendChild(
        document.createElement(HtmlTagName.img).apply {
            className = ClassName("logo")
            setAttribute("src", "img/$PROJECT_NAME-logo.png")
            setAttribute("alt", "$PROJECT_NAME logo")
        },
    )
    val heading = document.createElement(HtmlTagName.h1)
    header.appendChild(heading)
    header.appendChild(languageSwitch(texts))
    page.appendChild(header)

    val intro = document.createElement(HtmlTagName.p).apply { className = ClassName("hint") }
    page.appendChild(intro)

    val row = document.createElement(HtmlTagName.div).apply { className = ClassName("row") }
    page.appendChild(row)

    val view = document.createElement(HtmlTagName.div).apply { className = ClassName("view") }
    page.appendChild(view)

    val canvas = document.createElement(HtmlTagName.div).apply {
        className = ClassName("canvas")
        setAttribute("id", "canvas")
    }
    page.appendChild(canvas)

    val json = document.createElement(HtmlTagName.pre).apply {
        className = ClassName("json")
        setAttribute("data-role", "json")
    }
    page.appendChild(json)

    val hint = document.createElement(HtmlTagName.p).apply { className = ClassName("hint small") }
    page.appendChild(hint)

    val editor: LogicFlow = createLogicFlow(
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

    fun status() {
        val data = editor.getGraphRawData()
        val scale = editor.getTransform().SCALE_X
        view.textContent = "${texts["status"]}: ${data.nodes.size} / ${data.edges.size}" +
            " · ${texts["zoom"]}: ${(scale * 100).toInt()}%"
        view.setAttribute("data-nodes", data.nodes.size.toString())
        view.setAttribute("data-edges", data.edges.size.toString())
    }

    fun exportJson() {
        val data = editor.getGraphRawData()
        json.textContent = stringify(data)
    }

    fun action(label: String, handler: () -> Unit): HTMLElement =
        document.createElement(HtmlTagName.button).apply {
            className = ClassName("action")
            setAttribute("type", "button")
            setAttribute("data-action", label)
            setAttribute("data-label", label)
            textContent = texts[label]
            texts.onChange { textContent = texts[label] }
            onclick = EventHandler { handler() }
            row.appendChild(this)
        }

    action("add") {
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
        status()
    }
    action("delete") {
        val selected = editor.getSelectElements()
        selected.nodes.forEach { editor.deleteNode(it.id) }
        selected.edges.forEach { editor.deleteEdge(it.id) }
        view.setAttribute("data-select", selected.nodes.size.toString())
        status()
    }
    action("undo") {
        editor.undo()
        status()
    }
    action("redo") {
        editor.redo()
        status()
    }
    action("zoomIn") {
        editor.zoom(true)
        status()
    }
    action("zoomOut") {
        editor.zoom(false)
        status()
    }
    action("fit") {
        editor.fitView()
        status()
    }
    action("export") {
        exportJson()
    }

    texts.onChange {
        heading.textContent = texts["h1"]
        intro.textContent = texts["intro"]
        hint.textContent = texts["hint"]
        status()
    }
    heading.textContent = texts["h1"]
    intro.textContent = texts["intro"]
    hint.textContent = texts["hint"]

    editor.on(LogicFlowEvents.NodeClick) { event ->
        val label = event.data?.let { stringify(it).take(60) } ?: texts["none"]
        view.setAttribute("data-event", label)
        status()
    }
    editor.on(LogicFlowEvents.EdgeAdd) { status() }
    editor.on(LogicFlowEvents.NodeAdd) { status() }
    editor.on(LogicFlowEvents.GraphRendered) { status() }

    editor.render(sample())
    status()
    json.textContent = texts["empty"]
}
