package demo.page.editor

import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.logicflow.LogicFlow

/** One toolbar button: the command it runs on the editor. */
internal class Action(val id: String, val run: (EditorPage, LogicFlow) -> Unit)
