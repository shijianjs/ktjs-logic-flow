---
sidebar_position: 2
title: Getting started
---

# Getting started

## Dependency

```kotlin
kotlin {
    js {
        binaries.executable()
        browser()
    }
    sourceSets {
        jsMain.dependencies {
            implementation("io.github.shijianjs:ktjs-logic-flow:2.2.5-kt.1")
            // The public API exposes `kotlin-browser`'s `web.*` types, so a consumer must be able to
            // compile against them.
            implementation("org.jetbrains.kotlin-wrappers:kotlin-browser:2026.11.1")
        }
    }
}
```

Kotlin 2.4.21 or newer; the module emits ESM and targets `es2015` or later.

## Register the plugins

LogicFlow calls its optional integrations *plugins*, and they arrive through the `plugins` option rather than through imports:

```kotlin
val lf = createLogicFlow(logicFlowOptions {
    container = host
    plugins = arrayOf(menuPlugin, controlPlugin)
    pluginsOptions = menuOptions
})
```

The core package is one import, `@logicflow/core`, and the wrapper binds it once:

```kotlin
import io.github.shijianjs.ktjs.logicflow.createLogicFlow
```

The plugins themselves (`@logicflow/extension`) are not part of this repository, so `plugins` and
`pluginsOptions` are declared as `JsAny`: pass the constructor or definition you imported from
the extension package, unchanged.

## First call

```kotlin
import io.github.shijianjs.ktjs.logicflow.core.LogicFlowEvents
import io.github.shijianjs.ktjs.logicflow.core.LogicFlowGrid
import io.github.shijianjs.ktjs.logicflow.core.edgeConfig
import io.github.shijianjs.ktjs.logicflow.core.graphConfigData
import io.github.shijianjs.ktjs.logicflow.core.logicFlowOptions
import io.github.shijianjs.ktjs.logicflow.core.nodeConfig
import io.github.shijianjs.ktjs.logicflow.createLogicFlow

fun mount(host: web.html.HTMLElement) {
    val lf = createLogicFlow(logicFlowOptions {
        container = host
        grid = LogicFlowGrid(true)
        history = true
        edgeType = "polyline"
    })

    lf.on(LogicFlowEvents.NodeAdd) { event -> println("added: ${'$'}{event.type}") }

    lf.render(graphConfigData {
        nodes = arrayOf(
            nodeConfig { id = "n1"; type = "rect"; x = 120.0; y = 80.0; text = "Start" },
            nodeConfig { id = "n2"; type = "circle"; x = 320.0; y = 80.0; text = "End" },
        )
        edges = arrayOf(
            edgeConfig { id = "e1"; type = "polyline"; sourceNodeId = "n1"; targetNodeId = "n2" },
        )
    })
}
```

The page needs the library's stylesheet (`@logicflow/core/dist/index.css`), or the canvas draws
without colours and the text editor loses its layout.

## Build and test

```bash
./gradlew build                     # compile, Detekt, browser tests
./gradlew :ktjs-logic-flow:jsBrowserTest     # only the wrapper's browser tests
./gradlew :docs:docusaurusStart     # documentation dev server
./gradlew pagesSite                 # docs at /, demo at /demo/, Dokka at /api/
```
