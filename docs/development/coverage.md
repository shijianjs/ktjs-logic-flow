# API Coverage

## 来源与范围

- 来源：https://github.com/didi/LogicFlow
- 包：`@logicflow/core` 2.2.5（npm 上发布的版本；本地 `packages/core` 目录是 2.2.4，以发布版为准）
- 参考声明：`npm pack @logicflow/core@2.2.5` 解包后的 `lib/LogicFlow.d.ts`、`lib/options.d.ts`、
  `lib/constant/index.d.ts`
- 核对日期：2026-11-16
- 范围：`LogicFlow` 类的公开实例 API（方法按重载分别计数）、静态成员、公开的可写字段（适配器），
  以及构造选项和数据类型。内部协作者字段（控制器、模型表、视图表）不计入统计，单独列为排除项。

## 统计

| 状态 | 数量 |
|---|---:|
| 总计 | 103 |
| 已完成 | 76 |
| 明确不做 | 9 |
| 待完成 | 18 |

## 明确不做

| 名称 | 排除理由 |
|---|---|
| `viewMap` | 内部视图表（`Map<string, ComponentType>`），元素视图由编辑器自己管理，包装它等于暴露实现细节 |
| `history` | 内部命令栈控制器（`History` 类实例）；撤销/重做已经通过 `undo` / `redo` 暴露 |
| `keyboard` | 内部快捷键控制器；快捷键通过构造选项 `keyboard` 打开、通过 `updateEditConfig` 调整 |
| `dnd` | 内部拖放控制器，供 `@logicflow/extension` 的拖拽面板使用，不在核心 API 之内 |
| `tool` | 内部工具控制器，管理当前激活的交互工具 |
| `snaplineModel` | 内部吸附模型；吸附通过构造选项 `snapline` / `snaplineEpsilon` 控制 |
| `components` | 内部扩展渲染列表（`ExtensionRenderFunc[]`），由 `plugins` 选项填充 |
| `plugins`（实例字段） | 内部的插件实例表；安装插件走构造选项 `plugins`，本仓库不管理实例 |
| `extension` | 内部扩展注册表；注册/查询扩展由 `@logicflow/extension` 包自身负责 |

## 待完成

- [ ] 连线侧的 `getEdgeModels`、`changeEdgeId`、`changeEdgeType`、`deleteEdgeByNodeId`
- [ ] `getAreaElement`（按矩形区域取元素，四个可选参数）
- [ ] `setOverlapMode`
- [ ] `focusOn` 的 `FocusOnArgsType` 对象形式（当前只有按 id 与按坐标两个重载）
- [ ] `getPointByClient` 的 `Position` 重载（当前只有 `(x, y)`)
- [ ] `createFakeNode` / `removeFakeNode` / `setNodeSnapLine` / `removeNodeSnapLine`（拖拽预览的吸附辅助）
- [ ] 静态成员：`LogicFlow.extensions`、`use`、`addThemeMode`、`removeThemeMode`、`clearThemeMode`
- [ ] `adapterIn` / `adapterOut` 数据适配器字段
- [ ] `LogicFlow.Theme` 的完整主题结构（目前 `style` / `setTheme` 走 `JsAny`）
- [ ] `@logicflow/extension` 包（菜单、控制栏、小地图、快照、框选等）单独成模块

## 已完成的部分（计数口径）

已完成 76 项，均可在模块源码中找到对应声明并通过浏览器测试：

- 构造与选项（`LogicFlowOptions` 及 `logicFlowOptions` builder，含 `background` / `grid` /
  `stopMoveGraph` 三个联合类型的工厂）；
- `register` 两个重载、`batchRegister`；
- 节点：`addNode` 两个重载、`deleteNode`、`cloneNode`、`changeNodeId`、`changeNodeType`、
  `getNodeModelById`、`getNodeDataById`、`getNodeIncomingEdge`、`getNodeOutgoingEdge`、
  `getNodeIncomingNode`、`getNodeOutgoingNode`；
- 连线：`setDefaultEdgeType`、`addEdge`、`getEdgeDataById`、`getEdgeModelById`、`deleteEdge`、
  `getNodeEdges`；
- 元素：`addElements`、`selectElementById`、`deselectElementById`、`getSelectElements`、
  `clearSelectElements`、`getModelById`、`getDataById`、`deleteElement`、`setElementZIndex`、
  `setProperties`、`getProperties`、`deleteProperty`、`updateAttributes`；
- 文本：`editText`、`updateText`；
- 编辑配置：`updateEditConfig`、`getEditConfig`；
- 主题：`setTheme`、`getTheme`；
- 图操作与视口：`focusOn`（id / 坐标两个重载）、`resize`、`toFront`、`getPointByClient`、
  `getGraphData`、`getGraphRawData`、`clearData`、`renderRawData`、`render`；
- 历史与缩放：`undo`、`redo`、`zoom`、`resetZoom`、`setZoomMiniSize`、`setZoomMaxSize`、
  `getTransform`、`translate`、`resetTranslate`、`translateCenter`、`fitView`；
- 连线动画：`openEdgeAnimation`、`closeEdgeAnimation`；
- 事件：`on`、`off`、`once`、`emit`；
- `destroy`。

数据类型（`NodeConfig`、`NodeData`、`EdgeConfig`、`EdgeData`、`GraphConfigData`、`GraphData`、
`TextConfig`、`Position`、`RegisterConfig`、`EditConfig`、`TransformData`）与构造选项
（`BackgroundConfig`、`GridConfig`、`LogicFlowKeyboard`、`LogicFlowAnimation`）一并声明，
它们是选项与数据的一部分，不单独计入上面的 103 项。
