---
sidebar_position: 1
title: 简介
---

# 简介

`ktjs-logic-flow` 是 [LogicFlow](https://github.com/didi/LogicFlow) 生态里 `@logicflow/core` 2.2.5 的 Kotlin/JS wrapper：上游
`.d.ts` 里的每个类型和成员都落成可被编译器检查的 `external` 声明，不靠 `dynamic` 兜底。

封装覆盖 `@logicflow/core` 2.2 的 `LogicFlow` 类：构造函数与选项（`container`、`width`、`height`、`background`、`grid`、`keyboard`、`style`、`edgeType`、`history`、`snapline`、`partial`、`stopMoveGraph`、`isSilentMode`、`themeMode`、`plugins` 等）、节点编辑（`addNode`、`deleteNode`、`cloneNode`、`changeNodeId`、`changeNodeType`、一组 `getNode…` 查询）、连线编辑（`addEdge`、`deleteEdge`、`getEdgeDataById`、`getEdgeModelById`、`getNodeEdges`、`setDefaultEdgeType`）、元素操作（`addElements`、选中相关方法、`setProperties` / `getProperties` / `deleteProperty`、`updateAttributes`、`setElementZIndex`）、文本（`editText`、`updateText`）、编辑配置、主题、图操作（`render`、`renderRawData`、`getGraphData`、`getGraphRawData`、`clearData`）、历史与视口（`undo`、`redo`、`zoom`、`fitView`、`translate`、`getTransform` 等）、连线动画，以及事件总线。同时声明了数据类型（`NodeConfig`、`NodeData`、`EdgeConfig`、`EdgeData`、`GraphConfigData`、`GraphData`、`TextConfig`、`RegisterConfig`）与 `LogicFlowEvents` 事件名。

## 封装了哪些包

| 模块 | Gradle 坐标 | 上游 |
| --- | --- | --- |
| `ktjs-logic-flow` | `io.github.shijianjs:ktjs-logic-flow` | `@logicflow/core` 2.2.5 |

一个 npm 包、一个 Gradle 模块、一个 Kotlin 包（`io.github.shijianjs.ktjs.logicflow`），选项、数据类型与事件名放在 `core` 子包。

## 许可证

wrapper 自身是 MIT。上游 `@logicflow/core` 的许可证与 wrapper 不同，商用前请读
[许可证说明](guide/license.md)。

## 接下来

- [快速开始](guide/getting-started.md) — 加依赖、写第一个调用、跑构建。
- [API 概览](guide/api-overview.md) — 包结构、类型映射约定、常用写法。
- [演示页](guide/demo.md) — 浏览器里直接看效果。
