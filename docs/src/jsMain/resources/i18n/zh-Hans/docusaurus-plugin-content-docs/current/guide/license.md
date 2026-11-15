---
sidebar_position: 5
title: 许可证
---

# 许可证

## 本仓库

wrapper 与文档是 MIT，见仓库根目录的 `LICENSE`。

## 上游项目

`LogicFlow` 使用 **Apache-2.0** 许可，与本仓库的 MIT **并不相同**：

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

对本 wrapper 的使用者意味着：

- 本仓库（Kotlin 声明、构建、演示、文档）是 MIT。
- npm 依赖 `@logicflow/core` 仍是 Apache-2.0，原样提供，并携带它自己的许可文本与版权声明。
  Apache-2.0 声明已在此处和模块 README 中复述。
- Apache-2.0 要求保留声明并说明重要改动；本 wrapper 没有修改库源码，只是为它声明类型。
- LogicFlow 本身没有付费档：核心编辑器、节点类型，以及 `@logicflow/extension` 里的插件（菜单、
  控制栏、小地图、快照、框选等）都出自同一个 Apache-2.0 仓库。没有任何部分因许可原因被排除。
- 随包提供的样式 `@logicflow/core/dist/index.css` 与该包同许可。

上游源码：[https://github.com/didi/LogicFlow](https://github.com/didi/LogicFlow)

使用前请自行核对上游许可证原文；本页只是提示，不构成法律意见。
