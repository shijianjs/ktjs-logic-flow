package demo.page.overview

import io.github.shijianjs.ktjs.demo.page.DemoPage
import demo.API_URL
import demo.DOCS_URL
import demo.SOURCE_URL
import io.github.shijianjs.ktjs.demo.i18n.Binder
import io.github.shijianjs.ktjs.demo.i18n.Lang
import io.github.shijianjs.ktjs.demo.i18n.Langs
import io.github.shijianjs.ktjs.demo.i18n.Translations
import io.github.shijianjs.ktjs.demo.i18n.texts
import io.github.shijianjs.ktjs.demo.page.Blocks.anchor
import io.github.shijianjs.ktjs.demo.page.Blocks.codeText
import io.github.shijianjs.ktjs.demo.page.Blocks.paragraph
import io.github.shijianjs.ktjs.demo.page.Blocks.row
import io.github.shijianjs.ktjs.demo.page.SectionBlock
import io.github.shijianjs.ktjs.dom.element.invoke
import io.github.shijianjs.ktjs.dom.element.unaryPlus
import io.github.shijianjs.ktjs.dom.webcomponent.CustomElementReferenceAdapter
import web.html.HtmlTagName

private val overviewEnglish: OverviewTexts = OverviewTexts(
    title = "Overview",
    summary = "A Kotlin/JS wrapper for the LogicFlow flow-chart editor: typed options, typed graph " +
        "data, and every interaction reachable from Kotlin.",
    docsLink = "Documentation",
    sourceLink = "Source repository",
    apiLink = "API reference",
    purposeHeading = "What it wraps",
    body = "LogicFlow keeps the graph and draws it with SVG; the wrapper makes its options and its " +
        "graph data typed objects. Instead of an untyped literal, `logicFlowOptions { container = …; " +
        "grid = LogicFlowGrid(gridConfig { size = 20.0 }) }` is checked, `graphConfigData { nodes = …; " +
        "edges = … }` describes the document, and `LogicFlowEvents` is a list of constants rather " +
        "than string literals.",
    bullets = listOf(
        "createLogicFlow(logicFlowOptions { … }) — one editor over one container.",
        "graphConfigData { nodes = arrayOf(nodeConfig { … }); edges = … } — the graph, as data.",
        "editor.on(LogicFlowEvents.NodeClick) { … } — events as constants, not strings.",
    ),
    dataHeading = "The graph is plain data",
    dataBody = "Nodes and edges are values, not widgets: `getGraphRawData()` hands back the same " +
        "structure the editor was given, so storing, diffing and reloading a page is the consumer's " +
        "decision — the demo exports it as JSON as an example.",
    nextHeading = "Reading order",
    nextBody = "The overview, then the editor: drag the nodes, connect them from an anchor, and " +
        "export the graph.",
)

private val overviewChinese: OverviewTexts = OverviewTexts(
    title = "概览",
    summary = "LogicFlow 流程图编辑器的 Kotlin/JS 封装：选项有类型、图数据有类型，每次交互都能从 Kotlin 发起。",
    docsLink = "使用文档",
    sourceLink = "源码仓库",
    apiLink = "API 文档",
    purposeHeading = "它封装了什么",
    body = "LogicFlow 持有图数据并用 SVG 画出来；封装把选项与图数据都变成有类型的对象：不再是字面量，" +
        "`logicFlowOptions { container = …; grid = LogicFlowGrid(gridConfig { size = 20.0 }) }` 会被检查，" +
        "`graphConfigData { nodes = …; edges = … }` 描述整张图，`LogicFlowEvents` 是一组常量而不是字符串字面量。",
    bullets = listOf(
        "createLogicFlow(logicFlowOptions { … }) —— 一个容器上的一个编辑器。",
        "graphConfigData { nodes = arrayOf(nodeConfig { … }); edges = … } —— 图数据本身。",
        "editor.on(LogicFlowEvents.NodeClick) { … } —— 事件是常量，不是字符串。",
    ),
    dataHeading = "图就是数据",
    dataBody = "节点与连线都是值，不是控件：`getGraphRawData()` 交回来的结构与给编辑器的是同一个，" +
        "存盘、比对、重载都归调用方决定——demo 里导出成 JSON 只是一个例子。",
    nextHeading = "建议顺序",
    nextBody = "先看概览，再看编辑器：拖动节点、从锚点连出线，然后把图导出。",
)

private val overviewTexts: Translations<OverviewTexts> = texts(
    Langs.EN to overviewEnglish,
    Langs.ZH_HANS to overviewChinese,
)

/**
 * Landing page: what the library wraps, how it is consumed, where the reference lives.
 */
internal class OverviewPage : DemoPage() {

    private val texts = Binder(overviewTexts)

    init {
        addLink(texts.bind(anchor("", DOCS_URL, external = true), { it.docsLink }))
        addLink(texts.bind(anchor("", API_URL, external = true), { it.apiLink }))
        addLink(texts.bind(anchor("", SOURCE_URL, external = true), { it.sourceLink }))

        val purpose = SectionBlock.NAME { show() }
        texts.bind(purpose.heading, { it.purposeHeading })
        val bullets = HtmlTagName.ul {
            List(3) { index -> texts.bind(+HtmlTagName.li()) { it.bullets.getOrNull(index).orEmpty() } }
        }
        purpose.body.append(texts.bind(paragraph(), { it.body }), bullets)

        val data = SectionBlock.NAME { show() }
        texts.bind(data.heading, { it.dataHeading })
        data.body.append(
            texts.bind(paragraph(), { it.dataBody }),
            row(codeText("graphConfigData { nodes = arrayOf(nodeConfig { … }); edges = arrayOf(edgeConfig { … }) }")),
        )

        val next = SectionBlock.NAME { show() }
        texts.bind(next.heading, { it.nextHeading })
        next.body.append(texts.bind(paragraph(), { it.nextBody }))

        body.append(purpose.root, data.root, next.root)
    }

    override fun applyTranslation(lang: Lang) {
        val current = overviewTexts[lang]
        setTitle(current.title)
        setSummary(current.summary)
        texts.apply(lang)
    }

    companion object : CustomElementReferenceAdapter<OverviewPage>(OverviewPage::class.js, "logic-flow-overview-page")
}
