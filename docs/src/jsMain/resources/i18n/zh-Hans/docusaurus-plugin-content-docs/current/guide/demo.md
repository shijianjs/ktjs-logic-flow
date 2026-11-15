---
sidebar_position: 4
title: 演示页
---

# 演示页

演示页：[https://shijianjs.github.io/ktjs-logic-flow/demo/](https://shijianjs.github.io/ktjs-logic-flow/demo/)

演示页在一个四节点样例流程上挂编辑器：可以拖动节点、从锚点拖出连线，画布由库自己渲染。所有按钮
都走 wrapper：**新增节点** 调 `addNode`，**删除选中** 先读 `getSelectElements` 再调
`deleteNode` / `deleteEdge`，**撤销** / **重做** 走历史栈，**放大** / **缩小** / **自适应** 改视口，
**导出 JSON** 把 `getGraphRawData` 打印到下方面板。状态行显示节点与连线数量以及当前缩放，语言开关会
改写每个标签。

页面右上角的语言按钮在英文与简体中文之间切换，切换的是页面上全部可见文案（不只是页面标题）。

API 文档在 [https://shijianjs.github.io/ktjs-logic-flow/api/](https://shijianjs.github.io/ktjs-logic-flow/api/)，由 Dokka 从已发布模块生成。
