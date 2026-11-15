---
sidebar_position: 3
title: API overview
---

# API overview

## Package layout

| Package | Contents |
| --- | --- |
| `io.github.shijianjs.ktjs.logicflow` | `LogicFlow`, `createLogicFlow`, `logicFlowOf`, `logicFlowModel`, `LogicFlowModule` |
| `…logicflow.core` | `LogicFlowOptions`, `logicFlowOptions`, `LogicFlowBackground`, `BackgroundConfig`, `LogicFlowGrid`, `GridConfig`, `LogicFlowMoveLock`, `LogicFlowKeyboard`, `LogicFlowAnimation`, `EditConfig`, `TransformData`, `GraphConfigData`, `GraphData`, `NodeConfig`, `NodeData`, `EdgeConfig`, `EdgeData`, `TextConfig`, `Position`, `RegisterConfig`, `LogicFlowEvent`, `LogicFlowEvents` |

## Conventions this wrapper follows

- **The module is a namespace, the class taken by name.** `@logicflow/core` is CommonJS with both
  `exports.default` and `exports.LogicFlow`; binding the module as an object and reading
  `LogicFlow` out of it keeps the import working under any interop mode. `createLogicFlow`
  then calls `new cls(options)` through a shim.
- **Object literals are built, not assembled.** `logicFlowOptions`, `graphConfigData`, `nodeConfig`,
  `edgeConfig`, `textConfig`, `BackgroundConfig`, `GridConfig`, `LogicFlowKeyboard` and
  `LogicFlowAnimation` are all `unsafeJso` builders, so a field the block does not assign stays
  absent, and LogicFlow reads *absent* as *default*.
- **Unions are opaque types with factories.** `background` is `false | BackgroundConfig`
  (`LogicFlowBackground(false)` / `LogicFlowBackground(backgroundConfig { … })`), `grid` is
  `number | boolean | GridOptions` (`LogicFlowGrid(20.0)` / `LogicFlowGrid(true)` / …), and
  `stopMoveGraph` is `boolean | 'vertical' | 'horizontal' | [number,number,number,number]`
  (`LogicFlowMoveLock(…)` with four factories).
- **Optional parameters use `definedExternally`.** An external function's default value can only be
  `definedExternally`; that is how “argument omitted” reaches the library as *not passed*.
- **Event names are typed, payloads are not.** `LogicFlowEvents` holds the names so a typo is a
  compile error; the payload of an event stays `LogicFlowEvent` with `type` and `data`.

## What is deliberately typed loosely

| Member | Kotlin type | Why |
| --- | --- | --- |
| `LogicFlowOptions.style` | `JsAny?` | a deep partial theme, spread over the default theme |
| `LogicFlowOptions.guards` | `JsAny?` | `{ beforeClone?, beforeDelete? }`, two veto hooks |
| `LogicFlowOptions.plugins` | `Array<JsAny>` | each item is an `ExtensionConstructor` or an `ExtensionDefinition` from `@logicflow/extension` |
| `LogicFlowEvent.data` | `JsAny?` | the payload differs per event |
| `getNodeModelById` / `getEdgeModelById` / `getModelById` | `JsAny?` | the model classes come from `@logicflow/core/lib/model`, one class per element type |

Every row is a real upstream shape, and each is recorded in `docs/development/technical-decisions.md`.
