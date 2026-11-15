# Technical Decisions

`@logicflow/core` 2.2.5 的声明是一份 CommonJS 构建 + TypeScript 类型，落到 Kotlin/JS external 声明
时,下面这些地方需要做选择。每条都记录了决定、理由，以及被否决的做法。

## 1. 模块当命名空间绑，类按名字取

`lib/index.js` 同时有 `exports.default` 与 `exports.LogicFlow`（都指向同一个类），而包的 `main`
指向这份 CJS 构建。Kotlin 的 `@JsModule` 标在**类**上会生成 default import，在 CJS 互操作下不一定成立；
标在**对象**上是 namespace import，任何 interop 模式都能拿到成员。因此：

```kotlin
@JsModule("@logicflow/core")
external object LogicFlowModule {
    @JsName("LogicFlow") val LogicFlowClass: JsAny
}

private val constructLogicFlow: (JsAny, LogicFlowOptions) -> LogicFlow =
    js("(cls, options) => new cls(options)")
```

`@JsName` 在 `new cls(options)` 里也会存在（`new cls` 不依赖名字，名字只是取成员的键），因此别再
把类名写死进 shim。

**否决**：`@JsModule("...") external class LogicFlow(...)` — 依赖 default export 在不同构建产物上的
表现，换一次打包方式就会 `export 'default' ... was not found`。

## 2. 构造参数必填 `container` 声明为不可空

上游 `Options.Common.container: HTMLElement` 是必填。选项接口里写成 `var container: HTMLElement`，
builder 里漏写就是编译期拿不到值（Kotlin 会要求赋值才能编译）—— 把「必须给容器」这件事前移到编译期。

## 3. 对象字面量一律 `unsafeJso` builder，不用构造函数

`logicFlowOptions`、`graphConfigData`、`nodeConfig`、`edgeConfig`、`textConfig`、`Position`、
`BackgroundConfig`、`GridConfig`、`LogicFlowKeyboard`、`LogicFlowAnimation`、`registerConfig` 都是
`inline fun xxx(block: T.() -> Unit): T = unsafeJso(block)`。

原因：LogicFlow 用 `Object.assign` 把选项合并到默认值，**只有「不存在的键」才会取到默认值**。
自己拼 `js("({})")` 很容易在可选字段上写入 `null`，那会让 LogicFlow 拿到一个不是 `undefined` 的值。

## 4. 三处联合类型做成不透明类型 + 工厂

| 上游 | Kotlin |
|---|---|
| `background?: false \| BackgroundConfig` | `LogicFlowBackground(enabled: Boolean)` / `LogicFlowBackground(config: BackgroundConfig)` |
| `grid?: number \| boolean \| GridOptions` | `LogicFlowGrid(enabled: Boolean)` / `LogicFlowGrid(size: Double)` / `LogicFlowGrid(config: GridConfig)` |
| `stopMoveGraph?: boolean \| 'vertical' \| 'horizontal' \| [number,number,number,number]` | `LogicFlowMoveLock(enabled: Boolean)` / `LogicFlowMoveLock(axis: String)` / `LogicFlowMoveLock(top, right, bottom, left)` |

四元组参数化（`LogicFlowMoveLock(10.0, 10.0, 20.0, 20.0)`）比让调用方写数组更安全：上游要求的正是
四个数字的 padding box。

## 5. `PointTuple = [number, number]` 落到 `Array<Double>`

Kotlin/JS 没有元组类型。`zoom(point)` 这类参数声明为 `Array<Double>`，KDoc 里写明上游要求
`[x, y]` 两个元素。

## 6. 可选参数只能是 `definedExternally`

external 函数/成员的默认值不能写 `= null`，编译器会报
`Wrong default value for parameter of external function`。全部改成 `= definedExternally`，这样
「没传」才等价于 JS 的 `undefined`。

**注意**：`zoom(zoomSize: JsAny? = definedExternally)` 若显式传 `null`，到达库的是 `null` 而不是
`undefined` —— 调用方应省略参数，而不是传 `null`。

## 7. 事件回调统一 `(LogicFlowEvent) -> Unit`

上游每个事件都有自己的负载类型（`NodeClickEvent`、`EdgeAdjustEvent` …），泛型 `EventCallback<T>`
在 Kotlin/JS 里无法按事件名推导。声明成一个 `LogicFlowEvent`（`type` + `data`），并用
`LogicFlowEvents` 常量收住事件名 —— 名字拼错是编译错误，负载差异留在 `data` 上。

**否决**：为每个事件定义独立类型和重载 —— 需要 30+ 个重载，且上游负载类型互相嵌套，收益低。

## 8. 模型类不声明，用 `JsAny` + 转型助手

`getNodeModelById` / `getEdgeModelById` / `getModelById` 返回 `BaseNodeModel` / `BaseEdgeModel` /
`GraphElement`，这些类在 `lib/model/` 下，且每个节点类型一个子类。逐个声明会把包装的复杂度推到
`model` 子包，而多数调用只需要其中的 `id`、`properties`。

决定：返回 `JsAny?`，提供 `logicFlowModel<T>(model)` 转型助手，需要时由调用方声明自己要的形状。

## 9. `register` 拆成两个 Kotlin 名字、同一个 `@JsName`

上游有两个重载：`register(RegisterConfig)` 与 `register(type, fn, isObserverView?)`。Kotlin 里两个同名
external 函数按参数区分即可，但第二个重载的第三个参数在 Kotlin 侧省掉（观察者视图极少使用），
免得出现只在 JS 里有意义的布尔常量。名字上保留 `register` 与 `registerConfig`，都映射到 `register`。

## 10. 内部协作者不进 API

`viewMap`、`history`、`keyboard`、`dnd`、`tool`、`snaplineModel`、`components`、`plugins`、
`extension` 都是上游实现的一部分，包装它们等于把实现细节写成契约。需要这些能力时，都有对应的
公开入口（`undo`/`redo`、`keyboard` 选项、`plugins` 选项）。理由逐条记在 `coverage.md`。

## 11. 静态成员暂不声明

`LogicFlow.use` / `addThemeMode` / `removeThemeMode` / `clearThemeMode` / `extensions` 是类上的静态
成员，而外部的 `LogicFlowModule` 只暴露了类本身，取静态成员需要再写一层 `@JsName` 属性访问。
当前版本的用途（注册全局扩展、注册主题模式）都能由构造选项 `plugins` 与 `themeMode` 覆盖，
因此留作待完成，而不是给一个只在一处可用的半成品入口。

## 12. `width` / `height` 声明为可空 `Double?`

上游声明是 `readonly width?: number`，但 2.2.5 的实现并不给这两个字段赋值（尺寸由容器与
`resize` 决定）。声明为可空只是因为上游这么写；测试断言的是**容器**的尺寸，不是这两个字段 ——
否则测试会写出「期望 600、实际 null」的假失败。

## 13. `zoom()` 返回百分比字符串

`zoom(zoomSize?: ZoomParamType, point?: PointTuple): string` —— 返回的是 `"104%"` 这样的字符串，
不是数字。需要比例时用 `getTransform().SCALE_X`。demo 的缩放读数因此取自 `getTransform`。

## 14. 历史只记录用户交互

`addNode` 这类 API 调用不会进入撤销栈（LogicFlow 只在交互命令里 `record`）。因此 `undo()` 不撤销
API 新增的节点；测试只验证 `undo` / `redo` 可调用、图数据仍然一致，不假装它们能回滚 API 操作。
`zIndex` 参数写成 `JsAny`（`number | 'top' | 'bottom'`），调用方传 `"top"` 或数字。

## 15. 演示页复制库自带样式，保留子目录

`@logicflow/core/dist/index.css` 是唯一需要的样式表，demo 用 Gradle `Copy`
（`logicFlowStyles`）把它原样复制到构建产物的 `lf/` 下，页面 `<link rel="stylesheet" href="lf/index.css">`。
样式表本身不引用字体或图片，但仍保留一层 `lf/` 前缀，避免以后上游加资源时路径被打平。

## 16. 浏览器测试用真实容器尺寸

LogicFlow 依赖容器的实际尺寸计算网格与视口，测试里每个容器显式给 `style.width/height`，并挂到
`document.body`。不这样做时渲染出的节点没有可点击区域，事件测试会静默通过（没有任何东西被点击）。

## 17. e2e 断言走页面自己的 `data-*` 属性

演示页把状态写在 `.view` 的 `data-nodes` / `data-edges` / `data-select` / `data-event` 上，e2e 断言
这些属性和 `[data-role=json]` 的内容，而不是去解析 LogicFlow 内部的 SVG 结构 —— 内部类名是上游
实现细节，改一次版本就会碎。
