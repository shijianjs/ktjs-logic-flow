---
sidebar_position: 5
title: License
---

# License

## This repository

The wrapper and its documentation are MIT — see `LICENSE` at the repository root.

## The upstream project

`LogicFlow` is **Apache-2.0** licensed, which is *not* the same as this repository's MIT:

> Copyright 2019-2020 didi
>
> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at
>
>     http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.

What that means for you as a user of this wrapper:

- This repository (the Kotlin declarations, the build, the demo, the documentation) is MIT.
- The npm dependency `@logicflow/core` stays Apache-2.0, and you receive it unchanged, with its own
  license text and copyright notice. The Apache-2.0 notice is reproduced here and in the module README.
- Apache-2.0 asks you to keep the notices and to state significant changes; the wrapper does not
  modify the library's source, it declares types for it.
- LogicFlow itself has no paid tier: the core editor, the node types, the plugins in
  `@logicflow/extension` (menu, control, mini-map, snapshot, selection, …) are all in the same
  Apache-2.0 repository. Nothing is excluded for licensing reasons.
- The bundled stylesheet (`@logicflow/core/dist/index.css`) ships with the package and carries the
  same license.

Upstream source: [https://github.com/didi/LogicFlow](https://github.com/didi/LogicFlow)

Check the upstream license text yourself before relying on it; this page is a pointer, not legal
advice.
