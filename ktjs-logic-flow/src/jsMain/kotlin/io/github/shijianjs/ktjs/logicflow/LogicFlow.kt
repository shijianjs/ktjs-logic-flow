/**
 * Kotlin/JS bindings for [LogicFlow](https://github.com/didi/LogicFlow), the flow-chart editor.
 *
 * The entry point is [LogicFlow]; the types it uses live in
 * [io.github.shijianjs.ktjs.logicflow.core].
 */
package io.github.shijianjs.ktjs.logicflow

import io.github.shijianjs.ktjs.logicflow.core.EditConfig
import io.github.shijianjs.ktjs.logicflow.core.EdgeConfig
import io.github.shijianjs.ktjs.logicflow.core.EdgeData
import io.github.shijianjs.ktjs.logicflow.core.GraphConfigData
import io.github.shijianjs.ktjs.logicflow.core.GraphData
import io.github.shijianjs.ktjs.logicflow.core.LogicFlowEvent
import io.github.shijianjs.ktjs.logicflow.core.LogicFlowOptions
import io.github.shijianjs.ktjs.logicflow.core.NodeConfig
import io.github.shijianjs.ktjs.logicflow.core.NodeData
import io.github.shijianjs.ktjs.logicflow.core.Position
import io.github.shijianjs.ktjs.logicflow.core.TransformData
import kotlin.js.JsAny
import kotlin.js.definedExternally
import kotlin.js.JsName
import kotlin.js.unsafeCast
import web.html.HTMLElement

/**
 * The `@logicflow/core` module.
 *
 * The package is CommonJS with both `exports.default` and `exports.LogicFlow` pointing at the class,
 * so the module is bound as a namespace and the class taken from it by name.
 */
@JsModule("@logicflow/core")
external object LogicFlowModule {
    /** The `LogicFlow` class. */
    @JsName("LogicFlow")
    val LogicFlowClass: JsAny
}

private val constructLogicFlow: (JsAny, LogicFlowOptions) -> LogicFlow =
    js("(cls, options) => new cls(options)")

/** Creates an editor on `options.container`, which is what `new LogicFlow({ container })` does. */
fun createLogicFlow(options: LogicFlowOptions): LogicFlow =
    constructLogicFlow(LogicFlowModule.LogicFlowClass, options)

/**
 * A flow-chart editor instance, upstream `LogicFlow`.
 *
 * ```kotlin
 * val lf = createLogicFlow(logicFlowOptions { container = host })
 * lf.render(graphConfigData { … })
 * ```
 *
 * The instance carries an index signature upstream (`[propName: string]: any`), which the wrapper
 * does not model: use the typed members below, and reach anything else through a `js(…)` shim.
 */
external class LogicFlow {
    /** The element the editor draws into. */
    val container: HTMLElement

    /** The options the editor was created with. */
    val options: LogicFlowOptions

    /** The graph model: nodes, edges and their lookup tables. */
    val graphModel: JsAny

    /** The canvas width, resolved at creation time. */
    val width: Double?

    /** The canvas height, resolved at creation time. */
    val height: Double?

    // ---------------------------------------------------------------------------------------
    // Registration
    // ---------------------------------------------------------------------------------------

    /**
     * Registers custom elements: `register("my-node", { view, model })`, or a whole
     * [io.github.shijianjs.ktjs.logicflow.core.RegisterConfig].
     */
    fun register(type: String, definition: JsAny)

    /** [register] with the whole configuration object. */
    @JsName("register")
    fun registerConfig(config: JsAny)

    /** Registers every element of the given configurations at once. */
    fun batchRegister(elements: Array<JsAny>)

    // ---------------------------------------------------------------------------------------
    // Nodes
    // ---------------------------------------------------------------------------------------

    /** Adds a node; returns the model the editor created for it. */
    fun addNode(nodeConfig: NodeConfig): JsAny

    /** Adds a node and tags its creation event with the given event type. */
    @JsName("addNode")
    fun addNodeWithEvent(nodeConfig: NodeConfig, eventType: String): JsAny

    /** Deletes a node; returns whether it existed. */
    fun deleteNode(nodeId: String): Boolean

    /** Copies a node's configuration; the copy gets a new id when added. */
    fun cloneNode(nodeId: String): NodeData?

    /** Changes a node's id, returning the new one (or an empty string when the id was unknown). */
    fun changeNodeId(nodeId: String, newId: String? = definedExternally): String

    /** Changes a node's type, keeping its position and text. */
    fun changeNodeType(nodeId: String, type: String)

    /** The model behind a node. */
    fun getNodeModelById(nodeId: String): JsAny?

    /** The data behind a node. */
    fun getNodeDataById(nodeId: String): NodeData?

    /** Every edge that ends at the node. */
    fun getNodeIncomingEdge(nodeId: String): Array<JsAny>

    /** Every edge that starts at the node. */
    fun getNodeOutgoingEdge(nodeId: String): Array<JsAny>

    /** Every node that flows into the given one. */
    fun getNodeIncomingNode(nodeId: String): Array<JsAny>

    /** Every node the given one flows into. */
    fun getNodeOutgoingNode(nodeId: String): Array<JsAny>

    // ---------------------------------------------------------------------------------------
    // Edges
    // ---------------------------------------------------------------------------------------

    /** Sets the edge type a drag between two anchors produces (`"line"`, `"polyline"`, `"bezier"`). */
    fun setDefaultEdgeType(type: String)

    /** Adds an edge between two nodes. */
    fun addEdge(edgeConfig: EdgeConfig): JsAny

    /** The data behind an edge. */
    fun getEdgeDataById(edgeId: String): EdgeData?

    /** The model behind an edge. */
    fun getEdgeModelById(edgeId: String): JsAny?

    /** Deletes an edge; returns whether it existed. */
    fun deleteEdge(edgeId: String): Boolean

    /** Every edge attached to a node, incoming and outgoing. */
    fun getNodeEdges(nodeId: String): Array<JsAny>

    // ---------------------------------------------------------------------------------------
    // Elements
    // ---------------------------------------------------------------------------------------

    /** Adds nodes and edges in one call; `distance` nudges the new elements apart. */
    fun addElements(graphConfig: GraphConfigData, distance: Double? = definedExternally): JsAny

    /** Marks an element selected. */
    fun selectElementById(id: String, multiple: Boolean? = definedExternally, toFront: Boolean? = definedExternally)

    /** Unmarks a selected element. */
    fun deselectElementById(id: String)

    /** The currently selected elements, as graph data. */
    fun getSelectElements(isIgnoreCheck: Boolean? = definedExternally): GraphData

    /** Clears the selection. */
    fun clearSelectElements()

    /** The model behind any element, node or edge. */
    fun getModelById(id: String): JsAny?

    /** The data behind any element, node or edge. */
    fun getDataById(id: String): JsAny?

    /** Deletes any element, node or edge, when the id's kind is unknown. */
    fun deleteElement(id: String): Boolean

    /** Moves an element to the top (`"top"`), the bottom, or an explicit z-index. */
    fun setElementZIndex(id: String, zIndex: JsAny)

    /** Sets custom properties on an element, merging into what is there. */
    fun setProperties(id: String, properties: JsAny)

    /** The custom properties of an element. */
    fun getProperties(id: String): JsAny?

    /** Removes one custom property. */
    fun deleteProperty(id: String, key: String)

    /** Updates a model's own attributes; upstream warns about using it directly. */
    fun updateAttributes(id: String, attributes: JsAny)

    // ---------------------------------------------------------------------------------------
    // Text
    // ---------------------------------------------------------------------------------------

    /** Opens the text editor for an element. */
    fun editText(id: String)

    /** Replaces an element's text without opening the editor. */
    fun updateText(id: String, value: String)

    // ---------------------------------------------------------------------------------------
    // Edit configuration
    // ---------------------------------------------------------------------------------------

    /** Turns editing behaviours on or off: `adjustEdge`, `nodeTextEdit`, `edgeTextEdit`, … */
    fun updateEditConfig(config: JsAny)

    /** The current editing configuration. */
    fun getEditConfig(): EditConfig

    // ---------------------------------------------------------------------------------------
    // Graph
    // ---------------------------------------------------------------------------------------

    /** Applies a theme, optionally switching the theme mode first. */
    fun setTheme(style: JsAny, themeMode: String? = definedExternally)

    /** The current theme. */
    fun getTheme(): JsAny

    /** Centres the viewport on an element. */
    @JsName("focusOn")
    fun focusOnElement(id: String)

    /** Centres the viewport on a point. */
    @JsName("focusOn")
    fun focusOnPoint(coordinate: Position)

    /** Resizes the canvas; both arguments default to the container's size. */
    fun resize(width: Double? = definedExternally, height: Double? = definedExternally)

    /** Raises an element above its siblings. */
    fun toFront(id: String)

    /** Converts a client (page) coordinate into the editor's own coordinate spaces. */
    fun getPointByClient(x: Double, y: Double): JsAny

    /** The graph data, after the configured adapter. */
    fun getGraphData(): JsAny

    /** The graph data in the editor's own format, whatever adapter is configured. */
    fun getGraphRawData(): GraphData

    /** Empties the canvas. */
    fun clearData()

    /** Renders raw graph data directly. */
    fun renderRawData(graphConfig: GraphConfigData)

    /** Renders graph data: nodes and edges, replacing what is on the canvas. */
    fun render(graphConfig: GraphConfigData)

    // ---------------------------------------------------------------------------------------
    // History and viewport
    // ---------------------------------------------------------------------------------------

    /** Steps back in the history. */
    fun undo()

    /** Steps forward in the history. */
    fun redo()

    /** Zooms in (`true`), out (`false`), or by a factor; returns the resulting scale. */
    fun zoom(zoomSize: JsAny? = definedExternally, point: Array<Double>? = definedExternally): String

    /** Restores the default zoom. */
    fun resetZoom()

    /** Sets the smallest allowed zoom. */
    fun setZoomMiniSize(size: Double)

    /** Sets the largest allowed zoom. */
    fun setZoomMaxSize(size: Double)

    /** The current scale and translation. */
    fun getTransform(): TransformData

    /** Pans the canvas. */
    fun translate(x: Double, y: Double)

    /** Restores the initial pan. */
    fun resetTranslate()

    /** Centres the drawing. */
    fun translateCenter()

    /** Fits the drawing to the viewport. */
    fun fitView(verticalOffset: Double? = definedExternally, horizontalOffset: Double? = definedExternally)

    /** Starts the dash animation of an edge. */
    fun openEdgeAnimation(edgeId: String)

    /** Stops the dash animation of an edge. */
    fun closeEdgeAnimation(edgeId: String)

    // ---------------------------------------------------------------------------------------
    // Events
    // ---------------------------------------------------------------------------------------

    /** Subscribes to an event; several names may be comma-separated. */
    fun on(event: String, callback: (LogicFlowEvent) -> Unit)

    /** Removes a listener; the same function value registered with [on] is what matches. */
    fun off(event: String, callback: (LogicFlowEvent) -> Unit)

    /** Subscribes to the next occurrence of an event. */
    fun once(event: String, callback: (LogicFlowEvent) -> Unit)

    /** Fires an event on the editor's own emitter. */
    fun emit(event: String, args: JsAny)

    /** Destroys the editor and releases its DOM. */
    fun destroy()
}

/** `new LogicFlow({ container })`, spelled as a function. */
fun logicFlowOf(options: LogicFlowOptions): LogicFlow = createLogicFlow(options)

/** Casts an editor's opaque model (a node or an edge model) to the type you expect. */
inline fun <reified T : JsAny> logicFlowModel(model: JsAny?): T? = model?.unsafeCast<T>()
