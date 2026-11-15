# ktjs-logic-flow

LogicFlow 的 Kotlin/JS 绑定 —— 流程图编辑器

封装覆盖 `@logicflow/core` 2.2：`LogicFlow` 类及其选项、节点与连线编辑、元素操作、文本、编辑配置、主题、图操作、历史、视口、连线动画和事件总线，另外还有数据的类型化 builder（`nodeConfig`、`edgeConfig`、`graphConfigData`、`textConfig`）与 `LogicFlowEvents` 事件名。该包是 CommonJS，因此模块绑成命名空间、由 `createLogicFlow` 经 shim 构造；联合类型（`background`、`grid`、`stopMoveGraph`）做成不透明类型 + 工厂。上游是 Apache-2.0，本仓库是 MIT，文档在每个该说明的地方都写清了。

- 上游包：[`@logicflow/core`](https://github.com/didi/LogicFlow) 2.2.5
- 文档：https://shijianjs.github.io/ktjs-logic-flow/
- 演示：https://shijianjs.github.io/ktjs-logic-flow/demo/
- API：https://shijianjs.github.io/ktjs-logic-flow/api/
- 英文说明：[README.md](README.md)

## 用法

```kotlin
implementation("io.github.shijianjs:ktjs-logic-flow:2.2.5-kt.1")
```

## 构建

```bash
./gradlew build
./gradlew :ktjs-logic-flow:jsBrowserTest
./gradlew :demo:e2eTest
./gradlew pagesSite
```

## 许可证

wrapper 是 MIT；上游 `@logicflow/core` 的许可证不同，见文档的「许可证」页。
