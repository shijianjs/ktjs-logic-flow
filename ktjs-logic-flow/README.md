# ktjs-logic-flow (module)

Kotlin/JS wrapper for [`@logicflow/core`](https://www.npmjs.com/package/@logicflow/core) 2.2.5 — the
LogicFlow flow-chart editor.

One npm package, one Gradle module, one Kotlin package (`io.github.shijianjs.ktjs.logicflow`) with a
`core` sub-package for the options, the data types and the event names.

## Installation

```kotlin
dependencies {
    implementation("io.github.shijianjs:ktjs-logic-flow:2.2.5-kt.1")
}
```

The page also needs the stylesheet that ships inside the npm package
(`@logicflow/core/dist/index.css`). The demo copies it out of `node_modules` with a Gradle `Copy` task
rather than loading it from a CDN.

## Usage

```kotlin
import io.github.shijianjs.ktjs.logicflow.createLogicFlow
import io.github.shijianjs.ktjs.logicflow.core.LogicFlowEvents
import io.github.shijianjs.ktjs.logicflow.core.LogicFlowGrid
import io.github.shijianjs.ktjs.logicflow.core.edgeConfig
import io.github.shijianjs.ktjs.logicflow.core.graphConfigData
import io.github.shijianjs.ktjs.logicflow.core.logicFlowOptions
import io.github.shijianjs.ktjs.logicflow.core.nodeConfig

val lf = createLogicFlow(logicFlowOptions {
    container = host          // required: the element the editor draws into
    grid = LogicFlowGrid(true)
    history = true
    edgeType = "polyline"
})

lf.on(LogicFlowEvents.NodeAdd) { event -> println("added: ${event.type}") }

lf.render(graphConfigData {
    nodes = arrayOf(
        nodeConfig { id = "n1"; type = "rect"; x = 120.0; y = 80.0; text = "Start" },
        nodeConfig { id = "n2"; type = "circle"; x = 320.0; y = 80.0; text = "End" },
    )
    edges = arrayOf(
        edgeConfig { id = "e1"; type = "polyline"; sourceNodeId = "n1"; targetNodeId = "n2" },
    )
})
```

- Options, graph data and element configs are built with `unsafeJso` builders (`logicFlowOptions`,
  `graphConfigData`, `nodeConfig`, `edgeConfig`, `textConfig`). A field the block does not assign stays
  absent, which LogicFlow reads as “use the default”.
- `background`, `grid` and `stopMoveGraph` are unions upstream; each is an opaque Kotlin type with
  factories — `LogicFlowBackground(...)`, `LogicFlowGrid(true)` / `LogicFlowGrid(20.0)` /
  `LogicFlowGrid(gridConfig { … })`, `LogicFlowMoveLock(…)`.
- `LogicFlowEvents` holds the event names, so a typo is a compile error. Payloads stay `LogicFlowEvent`
  (`type` + `data`), because every event carries a different shape.
- `getNodeModelById` / `getEdgeModelById` / `getModelById` answer with the library's model classes.
  They are returned as `JsAny?`; cast with `logicFlowModel<T>(model)` when you need a member.
- `zoom()` answers with a percentage string (`"104%"`); `getTransform().SCALE_X` is the scale as a number.
- API calls such as `addNode` do not enter the undo stack — LogicFlow records history for user
  interactions only.

## Build and test

```bash
./gradlew :ktjs-logic-flow:jsBrowserTest   # Chromium: renders real flows, adds/deletes, events
./gradlew :demo:e2eTest                    # Playwright against the demo page
```

After adding or upgrading an npm dependency, refresh the shared lock file with the official registry
(the lock file is committed, and CI has no mirror):

```bash
./gradlew --stop
$env:npm_config_registry='https://registry.npmjs.org'
./gradlew kotlinUpgradePackageLock
```

## License

This repository is MIT. The wrapped package is **Apache-2.0**, which is a different license:

> Copyright 2019-2020 didi
>
> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
>
>     http://www.apache.org/licenses/LICENSE-2.0

`@logicflow/core` is npm-installed unchanged, so you receive it under its own terms; the notice above
is reproduced here to keep it alongside this wrapper. See
[docs/guide/license](https://shijianjs.github.io/ktjs-logic-flow/guide/license) for what that means in
practice.
