package demo.e2e

import com.microsoft.playwright.Locator
import com.microsoft.playwright.Page
import com.microsoft.playwright.junit.UsePlaywright
import com.microsoft.playwright.options.WaitUntilState
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junitpioneer.jupiter.RetryingTest

/**
 * Smoke checks for the demo page: the shell comes from the kit, the editor draws the sample flow, its
 * buttons go through the wrapper, and the language switch relabels the page without losing the graph.
 *
 * The shell lives in a shadow root; Playwright's CSS pierces shadow boundaries, `document.querySelector`
 * does not — so the waits use locators instead of `waitForFunction`.
 */
@UsePlaywright(DemoOptions::class)
class DemoPageTest {
    @RetryingTest(maxAttempts = 2)
    fun `page renders the shell, the logo and an overview`(page: Page) {
        openPage(page)

        assertTrue(page.title().contains("ktjs-logic-flow"), "title: ${page.title()}")
        assertTrue(page.locator("img.layout__logo").isVisible(), "the shell shows the project logo")
        assertEquals("Overview", page.locator("ktjs-demo-page h1").textContent())
        assertTrue(
            page.locator("vaadin-side-nav-item[data-route='editor']").isVisible(),
            "the sidebar lists the editor route",
        )
    }

    @RetryingTest(maxAttempts = 2)
    fun `the editor draws the sample flow`(page: Page) {
        openPage(page)
        goToEditor(page)
        waitForFlow(page)

        assertEquals("4", page.locator("[data-status]").getAttribute("data-nodes"), "four sample nodes")
        assertEquals("3", page.locator("[data-status]").getAttribute("data-edges"), "three sample edges")
        assertTrue(page.locator("[data-canvas] svg").count() > 0, "LogicFlow draws its SVG inside the host")
    }

    @RetryingTest(maxAttempts = 2)
    fun `adding a node goes through the wrapper`(page: Page) {
        openPage(page)
        goToEditor(page)
        waitForFlow(page)

        page.locator("[data-action=add]").click()
        assertEquals("5", page.locator("[data-status]").getAttribute("data-nodes"), "the node reached the graph")

        page.locator("[data-action=export]").click()
        val json = page.locator("[data-json]").textContent() ?: ""
        assertTrue(json.contains("added-1"), "the export holds the added node: ${json.take(120)}")
    }

    @RetryingTest(maxAttempts = 2)
    fun `zoom and fit change the scale`(page: Page) {
        openPage(page)
        goToEditor(page)
        waitForFlow(page)

        page.locator("[data-action=zoomIn]").click()
        val zoomed = page.locator("[data-status]").textContent() ?: ""
        assertTrue(zoomed.contains("%"), "the status reports the scale: $zoomed")
        assertTrue(!zoomed.contains(": 100%"), "zooming moved the scale: $zoomed")

        page.locator("[data-action=fit]").click()
        assertTrue((page.locator("[data-status]").textContent() ?: "").contains("%"), "the scale is still reported")
    }

    @RetryingTest(maxAttempts = 2)
    fun `selecting a node lets the delete button remove it`(page: Page) {
        openPage(page)
        goToEditor(page)
        waitForFlow(page)

        // LogicFlow selects on click; the first node sits at (180, 120) in canvas space.
        page.locator("[data-canvas]").click(Locator.ClickOptions().setPosition(180.0, 120.0))
        page.locator("[data-canvas] .lf-node").first().waitFor()
        page.locator("[data-action=delete]").click()

        assertEquals("3", page.locator("[data-status]").getAttribute("data-nodes"), "the clicked node is gone")
    }

    @RetryingTest(maxAttempts = 2)
    fun `the language switch relabels the toolbar and keeps the graph`(page: Page) {
        openPage(page)
        goToEditor(page)
        waitForFlow(page)

        page.locator("[data-menu='lang']").first().click()
        page.locator("[data-action='lang:zh-Hans']").click()

        assertEquals("zh-Hans", page.locator("html").getAttribute("lang"))
        assertEquals("编辑器", page.locator("vaadin-side-nav-item[data-route='editor']").textContent())
        assertEquals("新增节点", page.locator("[data-action=add]").textContent())
        assertEquals("4", page.locator("[data-status]").getAttribute("data-nodes"), "the graph survives the switch")
    }

    @RetryingTest(maxAttempts = 2)
    fun `page logs no console errors or failed requests`(page: Page) {
        val consoleErrors = mutableListOf<String>()
        val failedRequests = mutableListOf<String>()
        page.onConsoleMessage { message -> if (message.type() == "error") consoleErrors += message.text() }
        page.onRequestFailed { request -> failedRequests += "${request.url()} ${request.failure()}" }

        openPage(page)
        goToEditor(page)
        waitForFlow(page)
        page.locator("[data-action=export]").click()

        assertTrue(consoleErrors.isEmpty(), "console errors: $consoleErrors")
        assertTrue(failedRequests.isEmpty(), "failed requests: $failedRequests")
    }

    private fun goToEditor(page: Page) {
        page.locator("vaadin-side-nav-item[data-route='editor']").click()
        page.waitForSelector("[data-canvas]", Page.WaitForSelectorOptions().setTimeout(20_000.0))
    }

    private fun openPage(page: Page) {
        page.navigate("/", Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE))
    }

    /** The editor draws asynchronously; wait until the sample is on the canvas and counted. */
    private fun waitForFlow(page: Page) {
        page.locator("[data-canvas] svg").first().waitFor(Locator.WaitForOptions().setTimeout(20_000.0))
        page.locator("[data-status][data-nodes='4']").waitFor(Locator.WaitForOptions().setTimeout(20_000.0))
    }
}
