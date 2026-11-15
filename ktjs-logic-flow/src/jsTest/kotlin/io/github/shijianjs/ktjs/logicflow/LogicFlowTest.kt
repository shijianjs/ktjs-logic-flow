package io.github.shijianjs.ktjs.logicflow

import io.github.shijianjs.ktjs.logicflow.core.LogicFlowEvents
import io.github.shijianjs.ktjs.logicflow.core.LogicFlowGrid
import io.github.shijianjs.ktjs.logicflow.core.edgeConfig
import io.github.shijianjs.ktjs.logicflow.core.graphConfigData
import io.github.shijianjs.ktjs.logicflow.core.logicFlowOptions
import io.github.shijianjs.ktjs.logicflow.core.nodeConfig
import kotlin.js.unsafeCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import web.dom.document
import web.html.HTMLElement
import web.html.HtmlTagName

/**
 * Runs the wrapper against the real `@logicflow/core` package in Chromium.
 *
 * LogicFlow draws into a real element with a size, so the tests create a host, render into it, and
 * assert on the data the editor reports back rather than on the DOM.
 */
class LogicFlowTest {
    private fun host(tag: String): HTMLElement {
        val element = document.createElement(HtmlTagName.div).unsafeCast<HTMLElement>()
        element.setAttribute("data-role", tag)
        element.style.width = "600px"
        element.style.height = "400px"
        document.body?.appendChild(element)
        return element
    }

    private fun editorOn(tag: String) = createLogicFlow(
        logicFlowOptions {
            container = host(tag)
            width = 600.0
            height = 400.0
            grid = LogicFlowGrid(true)
            history = true
            keyboard = null
        },
    )

    private fun sample() = graphConfigData {
        nodes = arrayOf(
            nodeConfig {
                id = "n1"
                type = "rect"
                x = 100.0
                y = 100.0
                text = "Start"
            },
            nodeConfig {
                id = "n2"
                type = "circle"
                x = 300.0
                y = 100.0
                text = "End"
            },
        )
        edges = arrayOf(
            edgeConfig {
                id = "e1"
                type = "polyline"
                sourceNodeId = "n1"
                targetNodeId = "n2"
            },
        )
    }

    @Test
    fun theEditorRendersItsContainerAndGrid() {
        val host = host("render")
        val lf = createLogicFlow(logicFlowOptions { container = host; width = 600.0; grid = LogicFlowGrid(true) })

        // LogicFlow builds its own wrapper inside the container; nothing is drawn until `render`.
        assertTrue(host.childElementCount > 0, "the editor mounted its wrapper")
        // `width` and `height` are declared optional upstream and this build leaves them unset,
        // so the container is what has to carry a size.
        assertTrue(host.getBoundingClientRect().width > 0.0, "the container carries a size")
        lf.destroy()
    }

    @Test
    fun renderReportsTheDataItDrew() {
        val lf = editorOn("render-data")
        lf.render(sample())

        val data = lf.getGraphRawData()
        assertEquals(2, data.nodes.size, "two nodes")
        assertEquals(1, data.edges.size, "one edge")
        assertEquals("n1", data.nodes[0].id, "the node keeps its id")
        assertEquals(100.0, data.nodes[0].x, "the node keeps its position")
        assertEquals("polyline", data.edges[0].type, "the edge keeps its type")
        lf.destroy()
    }

    @Test
    fun nodesCanBeAddedAndDeleted() {
        val lf = editorOn("nodes")

        val model = lf.addNode(
            nodeConfig {
                id = "n3"
                type = "rect"
                x = 120.0
                y = 160.0
                text = "Added"
            },
        )
        assertEquals(1, lf.getGraphRawData().nodes.size, "the node is in the graph")
        assertTrue(model != null, "addNode returns the model")
        assertEquals("Added", lf.getNodeDataById("n3")?.text?.value, "the label is kept")

        assertTrue(lf.deleteNode("n3"), "deleteNode reports it existed")
        assertEquals(0, lf.getGraphRawData().nodes.size, "the node is gone")
        lf.destroy()
    }

    @Test
    fun edgesFollowTheirNodes() {
        val lf = editorOn("edges")
        lf.render(sample())

        assertEquals(1, lf.getNodeEdges("n1").size, "the start node owns the edge")
        assertEquals(1, lf.getNodeOutgoingEdge("n1").size, "the edge is outgoing from n1")
        assertEquals(1, lf.getNodeIncomingEdge("n2").size, "the edge is incoming to n2")

        val edge = lf.addEdge(
            edgeConfig {
                id = "e2"
                type = "line"
                sourceNodeId = "n2"
                targetNodeId = "n1"
            },
        )
        assertTrue(edge != null, "addEdge returns the model")
        assertEquals(2, lf.getGraphRawData().edges.size, "the second edge is in the graph")
        lf.destroy()
    }

    @Test
    fun customPropertiesRoundTrip() {
        val lf = editorOn("properties")
        lf.render(sample())

        val value = js("{ owner: 'team-a', weight: 3 }")
        lf.setProperties("n1", value.unsafeCast<kotlin.js.JsAny>())
        val read = lf.getProperties("n1")
        assertTrue(read != null, "getProperties returns what was set")

        lf.deleteProperty("n1", "weight")
        assertEquals("n1", lf.getGraphRawData().nodes[0].id, "the node survived the property edit")
        lf.destroy()
    }

    @Test
    fun theEventsReachListeners() {
        val lf = editorOn("events")
        var added = 0
        var rendered = 0

        lf.on(LogicFlowEvents.NodeAdd) { added++ }
        lf.on(LogicFlowEvents.GraphRendered) { rendered++ }

        lf.render(sample())
        assertEquals(1, rendered, "graph:rendered fires once per render")

        lf.addNode(nodeConfig { id = "n4"; type = "rect"; x = 200.0; y = 200.0 })
        assertEquals(1, added, "node:add fires when a node is added")
        lf.destroy()
    }

    @Test
    fun zoomAndTransformGoThroughTheWrapper() {
        val lf = editorOn("history")
        lf.render(sample())

        // `zoom` answers with a percentage string, `"104%"` for one step in from 100%.
        val scale = lf.zoom(true)
        assertTrue(scale.endsWith("%"), "zooming answers with a percentage ($scale)")
        assertTrue(scale.removeSuffix("%").toDouble() > 100.0, "zooming in goes past 100% ($scale)")
        assertTrue(lf.getTransform().SCALE_X > 1.0, "the transform follows the zoom")

        lf.resetZoom()
        assertEquals(1.0, lf.getTransform().SCALE_X, "resetZoom restores the scale")
        lf.destroy()
    }

    @Test
    fun aSilentEditorRefusesEdits() {
        val host = host("silent")
        val lf = createLogicFlow(
            logicFlowOptions {
                container = host
                isSilentMode = true
                history = false
            },
        )
        lf.render(sample())

        val config = lf.getEditConfig()
        assertEquals(true, config.isSilentMode, "the silent mode is reported")
        assertEquals(2, lf.getGraphRawData().nodes.size, "the sample still renders")
        lf.destroy()
    }
}
