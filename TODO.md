# TODO

## ktjs-logic-flow 上游 API 对齐

把上游 `@logicflow/core` 2.2.5-kt.1 的类型声明逐个落成 Kotlin/JS 声明，并让 demo、文档、
测试与之一致。

- 总计：103
- 已完成：76
- 明确不做：9
- 待完成：18

详细清单：[coverage.md](docs/development/coverage.md)
技术决策：[technical-decisions.md](docs/development/technical-decisions.md)

### 当前待办

- [ ] 连线侧的 `getEdgeModels`、`changeEdgeId`、`changeEdgeType`、`deleteEdgeByNodeId`
- [ ] `getAreaElement`（按矩形区域取元素，四个可选参数）
- [ ] `setOverlapMode`
- [ ] `focusOn` 的 `FocusOnArgsType` 对象形式（当前只有按 id 与按坐标两个重载）
- [ ] `getPointByClient` 的 `Position` 重载（当前只有 `(x, y)`）
- [ ] `createFakeNode` / `removeFakeNode` / `setNodeSnapLine` / `removeNodeSnapLine`（拖拽预览的吸附辅助）
- [ ] 静态成员：`LogicFlow.extensions`、`use`、`addThemeMode`、`removeThemeMode`、`clearThemeMode`
- [ ] `adapterIn` / `adapterOut` 数据适配器字段
- [ ] `LogicFlow.Theme` 的完整主题结构（目前 `style` / `setTheme` 走 `JsAny`）
- [ ] `@logicflow/extension` 包（菜单、控制栏、小地图、快照、框选等）单独成模块

### 注意事项

- 上游 `@logicflow/core` 是 **Apache-2.0**，本仓库是 MIT：文档与模块 README 都保留了上游许可与版权声明，并说明这是两套不同条款。
- 页面必须加载 `@logicflow/core/dist/index.css`，否则画布无颜色、文本编辑器失去布局。
- `LogicFlow` 类带索引签名（`[propName: string]: any`），wrapper 不建模它：用类型化成员，其余走 `js(...)` shim。
- `width` / `height` 在上游声明为可选，2.2.5 的实现并不赋值，实际尺寸由容器决定；测试断言的是容器尺寸。
- `zoom()` 返回百分比字符串（如 `104%`），不是数字；`getTransform().SCALE_X` 才是比例。
- LogicFlow 只在用户交互（拖动、连线、删除）时记录历史，API 直接调用 `addNode` 不入栈，因此 `undo` 不撤销它。
