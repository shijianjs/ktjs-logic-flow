package io.github.shijianjs.ktjs.logicflow.core

import js.objects.unsafeJso
import kotlin.js.JsAny
import kotlin.js.unsafeCast

/**
 * The graph data an editor renders, upstream `GraphConfigData`.
 *
 * Built with [graphConfigData]; an empty builder renders nothing.
 */
external interface GraphConfigData {
    /** The nodes to draw. */
    var nodes: Array<NodeConfig>?

    /** The edges to draw; each references nodes by id. */
    var edges: Array<EdgeConfig>?
}

/** Builds [GraphConfigData]. */
inline fun graphConfigData(block: GraphConfigData.() -> Unit): GraphConfigData = unsafeJso(block)

/** What an editor reports back, upstream `GraphData`: everything with ids filled in. */
external interface GraphData {
    /** Every node, with its resolved id. */
    val nodes: Array<NodeData>

    /** Every edge, with its resolved id, type and endpoints. */
    val edges: Array<EdgeData>
}

/** A node to add, upstream `NodeConfig`. Built with [nodeConfig]. */
external interface NodeConfig {
    /** The node id; the editor generates one when absent. */
    var id: String?

    /** The node type: `"rect"`, `"circle"`, `"diamond"`, `"ellipse"`, `"polygon"`, `"html"`, … */
    var type: String?

    /** The x coordinate of the node's centre, in canvas space. */
    var x: Double?

    /** The y coordinate of the node's centre, in canvas space. */
    var y: Double?

    /** The label: a plain string, or a full text configuration. */
    var text: JsAny?

    /** Stacking order within the canvas. */
    var zIndex: Double?

    /** Custom properties, kept on the node and handed back in its data. */
    var properties: JsAny?

    /** Rotation in degrees. */
    var rotate: Double?

    /** Whether the node can be rotated. */
    var rotatable: Boolean?

    /** Whether the node can be resized. */
    var resizable: Boolean?
}

/** Builds [NodeConfig]. */
inline fun nodeConfig(block: NodeConfig.() -> Unit): NodeConfig = unsafeJso(block)

/** A node as the editor reports it, upstream `NodeData`. */
external interface NodeData {
    /** The node id. */
    val id: String

    /** The node type. */
    val type: String

    /** The x coordinate of the node's centre. */
    val x: Double

    /** The y coordinate of the node's centre. */
    val y: Double

    /** The label, always the full configuration once the editor has resolved it. */
    val text: TextConfig?

    /** Custom properties. */
    val properties: JsAny?
}

/** An edge to add, upstream `EdgeConfig`. Built with [edgeConfig]. */
external interface EdgeConfig {
    /** The edge id; the editor generates one when absent. */
    var id: String?

    /** The edge type: `"line"`, `"polyline"`, `"bezier"`, … */
    var type: String?

    /** The node the edge starts at. Required. */
    var sourceNodeId: String?

    /** The anchor on the source node, when the edge should not pick one. */
    var sourceAnchorId: String?

    /** The node the edge ends at. Required. */
    var targetNodeId: String?

    /** The anchor on the target node. */
    var targetAnchorId: String?

    /** The start point, only for edges that hang loose. */
    var startPoint: Position?

    /** The end point, only for edges that hang loose. */
    var endPoint: Position?

    /** The label: a plain string, or a full text configuration. */
    var text: JsAny?

    /** The explicit polyline points, instead of letting the editor route the edge. */
    var pointsList: Array<Position>?

    /** Stacking order within the canvas. */
    var zIndex: Double?

    /** Custom properties. */
    var properties: JsAny?
}

/** Builds [EdgeConfig]. */
inline fun edgeConfig(block: EdgeConfig.() -> Unit): EdgeConfig = unsafeJso(block)

/** An edge as the editor reports it, upstream `EdgeData`. */
external interface EdgeData {
    /** The edge id. */
    val id: String

    /** The edge type. */
    val type: String

    /** The node the edge starts at. */
    val sourceNodeId: String

    /** The node the edge ends at. */
    val targetNodeId: String

    /** The resolved start point. */
    val startPoint: Position?

    /** The resolved end point. */
    val endPoint: Position?

    /** The label, always the full configuration once the editor has resolved it. */
    val text: TextConfig?

    /** Custom properties. */
    val properties: JsAny?
}

/** A label configuration, upstream `TextConfig`. Built with [textConfig]. */
external interface TextConfig {
    /** The label content. */
    var value: String?

    /** The x coordinate of the label's centre. */
    var x: Double?

    /** The y coordinate of the label's centre. */
    var y: Double?

    /** Whether the label can be edited in place. */
    var editable: Boolean?

    /** Whether the label can be dragged independently of its element. */
    var draggable: Boolean?
}

/** Builds [TextConfig]. */
inline fun textConfig(block: TextConfig.() -> Unit): TextConfig = unsafeJso(block)

/** A point in canvas space, upstream `LogicFlow.Position`. */
external interface Position {
    /** The horizontal coordinate. */
    var x: Double

    /** The vertical coordinate. */
    var y: Double
}

/** Builds [Position]. */
@Suppress("FunctionName")
fun Position(x: Double, y: Double): Position = unsafeJso {
    this.x = x
    this.y = y
}

/**
 * The configuration of a custom element, upstream `RegisterConfig`.
 *
 * `view` and `model` are preact components and model classes; the wrapper keeps them opaque, because
 * declaring them means declaring preact.
 */
external interface RegisterConfig {
    /** The element type name that `addNode` / `addEdge` will use. */
    var type: String

    /** The preact view component. */
    var view: JsAny

    /** The model class. */
    var model: JsAny
}

/** Builds [RegisterConfig]. */
inline fun registerConfig(block: RegisterConfig.() -> Unit): RegisterConfig = unsafeJso(block)
