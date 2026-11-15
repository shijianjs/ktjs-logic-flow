package demo.e2e

import com.microsoft.playwright.Page
import com.microsoft.playwright.junit.UsePlaywright
import com.microsoft.playwright.options.WaitUntilState
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junitpioneer.jupiter.RetryingTest

/**
 * Smoke checks for the demo page: the bundle loads, the editor draws the sample flow, its buttons go
 * through the wrapper, and the language switch swaps every visible string.
 */
@UsePlaywright(DemoOptions::class)
class DemoPageTest {
    @RetryingTest(maxAttempts = 2)
    fun `page renders heading, logo and styles`(page: Page) {
        openPage(page)

        assertTrue(page.title().contains("ktjs-logic-flow"), "title: ${page.title()}")
        assertEquals("ktjs-logic-flow demo", page.locator("h1").textContent())
        assertTrue(page.locator("img.logo").isVisible(), "the demo serves the project logo")

        val height = (
            page.locator(".canvas").evaluate("(node) => node.getBoundingClientRect().height") as Number
            ).toDouble()
        assertTrue(height > 300, "the stylesheet gives the canvas a height ($height)")
    }

    @RetryingTest(maxAttempts = 2)
    fun `the editor draws the sample flow`(page: Page) {
        openPage(page)

        waitForFlow(page)
        assertEquals("4", page.locator(".view").getAttribute("data-nodes"), "four sample nodes")
        assertEquals("3", page.locator(".view").getAttribute("data-edges"), "three sample edges")
        assertTrue(
            page.locator(".canvas svg").count() > 0,
            "LogicFlow draws its SVG inside the host",
        )
    }

    @RetryingTest(maxAttempts = 2)
    fun `adding a node goes through the wrapper`(page: Page) {
        openPage(page)

        waitForFlow(page)
        page.locator("[data-action=add]").click()

        assertEquals("5", page.locator(".view").getAttribute("data-nodes"), "the node reached the graph")
        page.locator("[data-action=export]").click()

        val json = page.locator("[data-role=json]").textContent()
        assertTrue(json.contains("added-1"), "the export holds the added node: ${json.take(120)}")
    }

    @RetryingTest(maxAttempts = 2)
    fun `zoom and fit change the scale`(page: Page) {
        openPage(page)

        waitForFlow(page)
        page.locator("[data-action=zoomIn]").click()

        val zoomed = page.locator(".view").textContent()
        assertTrue(zoomed.contains("%"), "the status reports the scale: $zoomed")
        assertTrue(!zoomed.contains(": 100%"), "zooming moved the scale: $zoomed")

        page.locator("[data-action=fit]").click()
        assertTrue(page.locator(".view").textContent().contains("%"), "the scale is still reported")
    }

    @RetryingTest(maxAttempts = 2)
    fun `selecting a node lets the delete button remove it`(page: Page) {
        openPage(page)

        waitForFlow(page)
        // LogicFlow selects on click; the first node sits at (180, 120) in canvas space.
        page.locator(".canvas").click(
            com.microsoft.playwright.Locator.ClickOptions().setPosition(180.0, 120.0),
        )
        page.waitForFunction("() => document.querySelector('.canvas .lf-node') !== null || true")
        page.locator("[data-action=delete]").click()

        assertEquals("3", page.locator(".view").getAttribute("data-nodes"), "the clicked node is gone")
    }

    @RetryingTest(maxAttempts = 2)
    fun `the language switch swaps every visible string`(page: Page) {
        openPage(page)

        page.locator("button.lang").click()

        assertEquals("ktjs-logic-flow 演示", page.locator("h1").textContent())
        assertEquals("zh-Hans", page.locator("html").getAttribute("lang"))
        assertEquals("新增节点", page.locator(".row button").first().textContent())

        page.locator("button.lang").click()

        assertEquals("ktjs-logic-flow demo", page.locator("h1").textContent())
        assertEquals("en", page.locator("html").getAttribute("lang"))
    }

    @RetryingTest(maxAttempts = 2)
    fun `page logs no console errors or failed requests`(page: Page) {
        val consoleErrors = mutableListOf<String>()
        val failedRequests = mutableListOf<String>()
        page.onConsoleMessage { message -> if (message.type() == "error") consoleErrors += message.text() }
        page.onRequestFailed { request -> failedRequests += "${request.url()} ${request.failure()}" }

        openPage(page)
        waitForFlow(page)
        page.locator("[data-action=export]").click()

        assertTrue(consoleErrors.isEmpty(), "console errors: $consoleErrors")
        assertTrue(failedRequests.isEmpty(), "failed requests: $failedRequests")
    }

    private fun openPage(page: Page) {
        page.navigate("/", Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE))
    }

    /** The editor draws asynchronously; wait until the sample is on the canvas. */
    private fun waitForFlow(page: Page) {
        page.waitForFunction(
            "() => { const shape = document.querySelector('.canvas svg');" +
                " const view = document.querySelector('.view');" +
                " return shape !== null && view !== null && view.getAttribute('data-nodes') === '4'; }",
        )
    }
}
