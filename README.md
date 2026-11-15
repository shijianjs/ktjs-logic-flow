# ktjs-logic-flow

Kotlin/JS bindings for LogicFlow — flow-chart editor

The wrapper covers `@logicflow/core` 2.2: the `LogicFlow` class together with its options, node and edge editing, element operations, text, editing configuration, theming, the graph calls, history, viewport, edge animation and the event bus, plus typed builders for the data (`nodeConfig`, `edgeConfig`, `graphConfigData`, `textConfig`) and the `LogicFlowEvents` names. The package is CommonJS, so the module is bound as a namespace and `createLogicFlow` constructs through a shim; unions (`background`, `grid`, `stopMoveGraph`) become opaque types with factories. Upstream is Apache-2.0 while this repository is MIT, and the documentation says so on every surface that matters.

- Upstream package: [`@logicflow/core`](https://github.com/didi/LogicFlow) 2.2.5
- Docs: https://shijianjs.github.io/ktjs-logic-flow/
- Demo: https://shijianjs.github.io/ktjs-logic-flow/demo/
- API: https://shijianjs.github.io/ktjs-logic-flow/api/
- 中文说明：[README.zh-CN.md](README.zh-CN.md)

## Usage

```kotlin
implementation("io.github.shijianjs:ktjs-logic-flow:2.2.5-kt.1")
```

## Build

```bash
./gradlew build
./gradlew :ktjs-logic-flow:jsBrowserTest
./gradlew :demo:e2eTest
./gradlew pagesSite
```

## License

The wrapper is MIT; the upstream `@logicflow/core` license differs — see the "License" page of the docs.
