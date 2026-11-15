package io.github.shijianjs.ktjs.logicflow.core

import kotlin.js.JsAny

/**
 * The events an editor emits, as named constants.
 *
 * Pass one of these to `on` / `once` / `off` instead of typing the string, so a typo is a compile
 * error. Names come from the package's own `EventType` enum.
 */
object LogicFlowEvents {
    /** A click on a node or an edge: the union of [NodeClick] and [EdgeClick]. */
    inline val ElementClick: String get() = "element:click"

    /** A node was added. */
    inline val NodeAdd: String get() = "node:add"

    /** A node was deleted. */
    inline val NodeDelete: String get() = "node:delete"

    /** A click on a node. */
    inline val NodeClick: String get() = "node:click"

    /** A double click on a node. */
    inline val NodeDbClick: String get() = "node:dbclick"

    /** A right click on a node. */
    inline val NodeContextMenu: String get() = "node:contextmenu"

    /** A node started being dragged. */
    inline val NodeDragStart: String get() = "node:dragstart"

    /** A node is being dragged. */
    inline val NodeDrag: String get() = "node:drag"

    /** A dragged node was dropped. */
    inline val NodeDrop: String get() = "node:drop"

    /** The mouse entered a node. */
    inline val NodeMouseEnter: String get() = "node:mouseenter"

    /** The mouse left a node. */
    inline val NodeMouseLeave: String get() = "node:mouseleave"

    /** A node was rotated. */
    inline val NodeRotate: String get() = "node:rotate"

    /** A node was resized. */
    inline val NodeResize: String get() = "node:resize"

    /** A custom property of a node changed. */
    inline val NodePropertiesChange: String get() = "node:properties-change"

    /** An edge was added. */
    inline val EdgeAdd: String get() = "edge:add"

    /** An edge was deleted. */
    inline val EdgeDelete: String get() = "edge:delete"

    /** A click on an edge. */
    inline val EdgeClick: String get() = "edge:click"

    /** A double click on an edge. */
    inline val EdgeDbClick: String get() = "edge:dbclick"

    /** An edge was adjusted: an endpoint moved. */
    inline val EdgeAdjust: String get() = "edge:adjust"

    /** A click on an anchor, the connect handle of a node. */
    inline val AnchorClick: String get() = "anchor:click"

    /** An anchor drag started, the beginning of a manual connection. */
    inline val AnchorDragStart: String get() = "anchor:dragstart"

    /** An anchor drag ended, either on a node or on the blank canvas. */
    inline val AnchorDragEnd: String get() = "anchor:dragend"

    /** A click on the blank canvas. */
    inline val BlankClick: String get() = "blank:click"

    /** The mouse moved over the blank canvas. */
    inline val BlankMouseMove: String get() = "blank:mousemove"

    /** A right click on the blank canvas. */
    inline val BlankContextMenu: String get() = "blank:contextmenu"

    /** The history changed, after an undo or a redo. */
    inline val HistoryChange: String get() = "history:change"

    /** The canvas was zoomed or panned. */
    inline val GraphTransform: String get() = "graph:transform"

    /** A render finished. */
    inline val GraphRendered: String get() = "graph:rendered"

    /** The graph data changed. */
    inline val GraphUpdated: String get() = "graph:updated"

    /** The editing configuration changed. */
    inline val EditConfigChanged: String get() = "editConfig:changed"
}

/**
 * The object a listener receives.
 *
 * LogicFlow hands the data of the element the event is about, plus a `data` field carrying it for
 * the generic element events. The payload differs per event, so it stays [JsAny]; the fields below
 * are the ones every event carries.
 */
external interface LogicFlowEvent {
    /** The event name, for example `node:click`. */
    val type: String?

    /** The data of the element the event is about, for the element events. */
    val data: JsAny?
}
