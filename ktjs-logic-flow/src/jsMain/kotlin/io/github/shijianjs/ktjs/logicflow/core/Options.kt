package io.github.shijianjs.ktjs.logicflow.core

import js.objects.unsafeJso
import kotlin.js.JsAny
import kotlin.js.unsafeCast
import web.html.HTMLElement

/**
 * The options an editor accepts, upstream `Options.Common`.
 *
 * Built with [logicFlowOptions]; what a builder does not set stays absent, and LogicFlow reads
 * “absent” as “use the default”, which is how `grid`, `background` and `keyboard` are turned on.
 */
external interface LogicFlowOptions {
    /** The element the editor draws into. Required. */
    var container: HTMLElement

    /** Canvas width in pixels; defaults to the container's width. */
    var width: Double?

    /** Canvas height in pixels; defaults to the container's height. */
    var height: Double?

    /** Canvas background: a colour/image configuration, or `false` for a transparent canvas. */
    var background: LogicFlowBackground?

    /** The dotted grid: on, off, a cell size, or a whole configuration. */
    var grid: LogicFlowGrid?

    /** `true` draws every element, `false` starts empty and renders incrementally. */
    var partial: Boolean?

    /** Keyboard shortcuts: which ones, and whether they are global to the page. */
    var keyboard: LogicFlowKeyboard?

    /** Theme overrides, merged over the default theme. */
    var style: JsAny?

    /** The edge type a drag produces: `"line"`, `"polyline"`, `"bezier"`. */
    var edgeType: String?

    /** Whether dragging an edge's endpoints is allowed. */
    var adjustEdge: Boolean?

    /** Whether elements can be rotated. */
    var allowRotate: Boolean?

    /** Whether elements can be resized. */
    var allowResize: Boolean?

    /** `true` makes the canvas read-only: no dragging, resizing, connecting or text editing. */
    var isSilentMode: Boolean?

    /** Keeps the page from scrolling while the canvas is dragged. */
    var stopScrollGraph: Boolean?

    /** Keeps the canvas from zooming. */
    var stopZoomGraph: Boolean?

    /** Restricts panning to one axis, a padding box, or turns it off entirely. */
    var stopMoveGraph: LogicFlowMoveLock?

    /** Enables the dash animations of nodes and edges. */
    var animation: LogicFlowAnimation?

    /** Keeps an undo/redo history. */
    var history: Boolean?

    /** Draws an outline around hovered elements. */
    var outline: Boolean?

    /** Snaps elements to each other while dragging. */
    var snapline: Boolean?

    /** The distance at which snapping kicks in, in pixels. */
    var snaplineEpsilon: Double?

    /** Enables double-click text editing. */
    var textEdit: Boolean?

    /** Hooks that can veto a clone or a delete. */
    var guards: JsAny?

    /** Whether elements overlap each other, or keep to the grid. */
    var overlapMode: Double?

    /** Plugins to install on creation, each an `ExtensionConstructor` or an `ExtensionDefinition`. */
    var plugins: Array<kotlin.js.JsAny>?

    /** Per-plugin options, keyed by plugin name. */
    var pluginsOptions: JsAny?

    /** Plugin names to skip on install. */
    var disabledPlugins: Array<String>?

    /** Tool names to skip: `"menu"`, `"control"`, `"selection-select"`, … */
    var disabledTools: Array<String>?

    /** Generates ids for elements that arrive without one. */
    var idGenerator: ((String?) -> String)?

    /** The theme mode: `"default"`, `"retro"`, `"dark"`, `"colorful"`, or a registered one. */
    var themeMode: String?
}

/** Builds [LogicFlowOptions], setting only the fields the block assigns. */
inline fun logicFlowOptions(block: LogicFlowOptions.() -> Unit): LogicFlowOptions =
    unsafeJso(block)

/** Canvas background: a configuration object, or `false` for the theme's own background. */
external interface LogicFlowBackground

/** The background described by a configuration object. */
@Suppress("FunctionName")
fun LogicFlowBackground(config: BackgroundConfig): LogicFlowBackground =
    config.unsafeCast<LogicFlowBackground>()

/** No canvas background of its own; the theme decides. */
@Suppress("FunctionName")
fun LogicFlowBackground(enabled: Boolean): LogicFlowBackground = enabled.unsafeCast<LogicFlowBackground>()

/** The background image and its repeat mode, upstream `Options.BackgroundConfig`. */
external interface BackgroundConfig {
    /** URL of the background image. */
    var backgroundImage: String?

    /** `repeat`, `repeat-x`, `repeat-y` or `no-repeat`. */
    var backgroundRepeat: String?
}

/** Builds [BackgroundConfig]. */
inline fun backgroundConfig(block: BackgroundConfig.() -> Unit): BackgroundConfig =
    unsafeJso(block)

/** The dotted grid: a switch, a cell size, or a configuration object. */
external interface LogicFlowGrid

/** Turns the grid on or off. */
@Suppress("FunctionName")
fun LogicFlowGrid(enabled: Boolean): LogicFlowGrid = enabled.unsafeCast<LogicFlowGrid>()

/** A grid with square cells of the given size, in pixels. */
@Suppress("FunctionName")
fun LogicFlowGrid(size: Double): LogicFlowGrid = size.unsafeCast<LogicFlowGrid>()

/** A grid described by a configuration object. */
@Suppress("FunctionName")
fun LogicFlowGrid(config: GridConfig): LogicFlowGrid = config.unsafeCast<LogicFlowGrid>()

/** The grid configuration, upstream `Grid.GridOptions`. */
external interface GridConfig {
    /** Cell size: one number, or `[width, height]`. */
    var size: Double?

    /** The grid's line colour. */
    var color: String?

    /** Whether the grid is drawn at all. */
    var visible: Boolean?

    /** Grid line style: `"dot"` or `"mesh"`. */
    var type: String?
}

/** Builds [GridConfig]. */
inline fun gridConfig(block: GridConfig.() -> Unit): GridConfig = unsafeJso(block)

/** Panning restrictions: a switch, one axis, or a padding box. */
external interface LogicFlowMoveLock

/** Locks or unlocks panning entirely. */
@Suppress("FunctionName")
fun LogicFlowMoveLock(enabled: Boolean): LogicFlowMoveLock = enabled.unsafeCast<LogicFlowMoveLock>()

/** Locks panning to one axis (`"vertical"` or `"horizontal"`). */
@Suppress("FunctionName")
fun LogicFlowMoveLock(axis: String): LogicFlowMoveLock = axis.unsafeCast<LogicFlowMoveLock>()

/** Locks panning to a padding box: how far each side may be dragged. */
@Suppress("FunctionName")
fun LogicFlowMoveLock(top: Double, right: Double, bottom: Double, left: Double): LogicFlowMoveLock =
    arrayOf(top, right, bottom, left).unsafeCast<LogicFlowMoveLock>()

/** Keyboard shortcut options, upstream `KeyboardDef`. */
external interface LogicFlowKeyboard {
    /** Whether shortcuts work at all. */
    var enabled: Boolean?

    /** Whether shortcuts fire while the focus is outside the canvas. */
    var onlyMode: Boolean?

    /** Shortcuts to disable, by key combination. */
    var disabled: Array<String>?
}

/** Builds [LogicFlowKeyboard]. */
inline fun logicFlowKeyboard(block: LogicFlowKeyboard.() -> Unit): LogicFlowKeyboard =
    unsafeJso(block)

/** Animation switches for nodes and edges. */
external interface LogicFlowAnimation {
    /** Animates edges that are marked animated. */
    var edge: Boolean?

    /** Animates nodes that are marked animated. */
    var node: Boolean?
}

/** Builds [LogicFlowAnimation]. */
inline fun logicFlowAnimation(block: LogicFlowAnimation.() -> Unit): LogicFlowAnimation =
    unsafeJso(block)

/** The editing configuration an editor reports, upstream `IEditConfigType`. */
external interface EditConfig {
    /** Whether dragging an edge's endpoints is allowed. */
    var adjustEdge: Boolean?

    /** Whether an edge may be adjusted at all. */
    var adjustEdgeStartAndEnd: Boolean?

    /** Whether nodes can be resized. */
    var allowResize: Boolean?

    /** Whether nodes can be rotated. */
    var allowRotate: Boolean?

    /** Whether the canvas is read-only. */
    var isSilentMode: Boolean?

    /** Whether a node's text can be edited by double-clicking. */
    var nodeTextEdit: Boolean?

    /** Whether an edge's text can be edited by double-clicking. */
    var edgeTextEdit: Boolean?

    /** The node types that may not be moved. */
    var nodeDraggable: Array<String>?

    /** The zoom range, `[min, max]`. */
    var zoomRange: Array<Double>?
}

/** The scale and translation of the canvas, upstream `LogicFlow.getTransform()`. */
external interface TransformData {
    /** The horizontal scale. */
    val SCALE_X: Double

    /** The vertical scale. */
    val SCALE_Y: Double

    /** The horizontal translation. */
    val TRANSLATE_X: Double

    /** The vertical translation. */
    val TRANSLATE_Y: Double
}
