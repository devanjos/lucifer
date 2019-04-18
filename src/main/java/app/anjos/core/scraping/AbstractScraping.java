package app.anjos.core.scraping;

import java.io.Closeable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public abstract class AbstractScraping<O extends Object> implements WebDriver, Closeable {

	private static final String DEFAULT_CACHE = "default";

	private static Class<? extends RemoteWebDriver> DRIVER_CLASS;

	public static void setChromeDriver(String driverPath) {
		System.setProperty("webdriver.chrome.driver", driverPath);
		DRIVER_CLASS = ChromeDriver.class;
	}

	protected WebDriver driver;

	protected Map<String, WebElement> elementMap;
	protected Map<String, List<WebElement>> elementsMap;

	public AbstractScraping() {
		if (DRIVER_CLASS.equals(ChromeDriver.class)) {
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--headless");
			driver = new ChromeDriver(options);
		}

		elementMap = new HashMap<>();
		elementsMap = new HashMap<>();
	}

	public Map<String, Object> GET(String url, Map<String, String> params) {
		return null;
	}

	public Map<String, Object> POST(String url, Map<String, String> params) {
		return null;
	}

	/**
	 * 
	 * @param url
	 * @param delay
	 *            Time to wait to visit the url (<b>in seconds</b>)
	 */
	public void visit(String url, long delay) {
		try {
			Thread.sleep(delay * 1000);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		visit(url);
	}

	public void visit(String url) {
		Logger.getGlobal().log(Level.INFO, "Visit: " + url);
		driver.get(url);
	}

	protected WebElement getElement() {
		return getElement(DEFAULT_CACHE);
	}

	protected WebElement getElement(String cache) {
		return existsElement(cache) ? elementMap.get(cache) : null;
	}

	protected void setElement(String cache, WebElement element) {
		elementMap.put(cache, element);
	}

	public WebElement find(By by) {
		return find(by, true);
	}

	public WebElement find(By by, boolean cache) {
		return find(false, by, cache, DEFAULT_CACHE);
	}

	public WebElement find(By by, String cache) {
		return find(false, by, true, cache);
	}

	public WebElement findInElement(By by) {
		return findInElement(by, true);
	}

	public WebElement findInElement(By by, boolean cache) {
		return find(true, by, cache, DEFAULT_CACHE);
	}

	public WebElement findInElement(By by, String cache) {
		return find(true, by, true, cache);
	}

	protected WebElement find(boolean useElement, By by, boolean cache, String cacheName) {
		WebElement element = null;

		if (useElement && (!elementMap.containsKey(cacheName) || elementMap.get(cacheName) == null)) {
			if (cache)
				setElement(cacheName, element);
			return element;
		}

		try {
			element = ((useElement) ? elementMap.get(cacheName) : driver).findElement(by);
		} catch (Exception ex) {}

		if (cache)
			elementMap.put(cacheName, element);

		return element;
	}

	public String getText() {
		return getText(DEFAULT_CACHE);
	}

	public String getText(String cache) {
		return (existsElement(cache)) ? elementMap.get(cache).getText() : null;
	}

	public boolean existsElement() {
		return existsElement(DEFAULT_CACHE);
	}

	public boolean existsElement(String cache) {
		return elementMap.containsKey(cache) && elementMap.get(cache) != null;
	}

	protected List<WebElement> getElements() {
		return getElements(DEFAULT_CACHE);
	}

	protected List<WebElement> getElements(String cache) {
		return existsElements(cache) ? elementsMap.get(cache) : new LinkedList<>();
	}

	protected void setElements(String cache, List<WebElement> elements) {
		elementsMap.put(cache, elements);
	}

	public List<WebElement> findAll(By by) {
		return findAll(by, true);
	}

	public List<WebElement> findAll(By by, boolean cache) {
		return findAll(by, false, cache, DEFAULT_CACHE);
	}

	public List<WebElement> findAll(By by, String cache) {
		return findAll(by, false, true, cache);
	}

	public List<WebElement> findAllInElement(By by) {
		return findAllInElement(by, true);
	}

	public List<WebElement> findAllInElement(By by, boolean cache) {
		return findAll(by, true, cache, DEFAULT_CACHE);
	}

	public List<WebElement> findAllInElement(By by, String cache) {
		return findAll(by, true, true, cache);
	}

	protected List<WebElement> findAll(By by, boolean useElement, boolean cache, String cacheName) {
		List<WebElement> elements = new LinkedList<>();

		if (useElement && (!elementMap.containsKey(cacheName) || elementMap.get(cacheName) == null)) {
			if (cache)
				setElements(cacheName, elements);
			return elements;
		}

		elements = ((useElement) ? elementMap.get(cacheName) : driver).findElements(by);

		if (cache)
			elementsMap.put(cacheName, elements);

		return elements;
	}

	public boolean existsElements() {
		return existsElements(DEFAULT_CACHE);
	}

	public boolean existsElements(String cache) {
		return elementsMap.containsKey(cache) && !elementsMap.get(cache).isEmpty();
	}

	public void forEach(Consumer<? super WebElement> action) {
		forEach(action, DEFAULT_CACHE);
	}

	public void forEach(Consumer<? super WebElement> action, String cache) {
		if (existsElements(cache))
			elementsMap.get(cache).forEach(action);
	}

	public List<String> findUrls() {
		return findUrls(null, false, DEFAULT_CACHE);
	}

	public List<String> findUrls(boolean useElement) {
		return findUrls(null, useElement, DEFAULT_CACHE);
	}

	public List<String> findUrls(String regex) {
		return findUrls(regex, false, DEFAULT_CACHE);
	}

	public List<String> findUrls(String regex, boolean useElement) {
		return findUrls(regex, useElement, DEFAULT_CACHE);
	}

	public List<String> findUrls(boolean useElement, String cache) {
		return findUrls(null, useElement, cache);
	}

	public List<String> findUrlsInElement() {
		return findUrls(null, true, DEFAULT_CACHE);
	}

	public List<String> findUrlsInElement(String regex) {
		return findUrls(regex, true, DEFAULT_CACHE);
	}

	public List<String> findUrls(String regex, boolean useElement, String cache) {
		List<String> urls = new LinkedList<>();

		if (useElement && (!elementMap.containsKey(cache) || elementMap.get(cache) == null))
			return urls;

		List<WebElement> elements = ((useElement) ? elementMap.get(cache) : driver).findElements(By.xpath(".//*"));

		elements.forEach((e) -> {
			String href = e.getAttribute("href");
			if (href != null && !href.trim().isEmpty()) {
				href = href.trim();
				if (regex == null || href.matches(regex))
					urls.add(href);
			}
		});

		return urls;
	}

	// ###################################################################################
	// ##################################### WRAPING #####################################
	// ###################################################################################
	@Override
	public void get(String url) {
		driver.get(url);
	}

	@Override
	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}

	@Override
	public String getTitle() {
		return driver.getTitle();
	}

	@Override
	public List<WebElement> findElements(By by) {
		return driver.findElements(by);
	}

	@Override
	public WebElement findElement(By by) {
		return driver.findElement(by);
	}

	@Override
	public String getPageSource() {
		return driver.getPageSource();
	}

	@Override
	public void close() {
		elementMap = new HashMap<>();
		elementsMap = new HashMap<>();

		if (driver != null) {
			driver.close();
			driver.quit();
		}
	}

	@Override
	public void quit() {
		driver.quit();
	}

	@Override
	public Set<String> getWindowHandles() {
		return driver.getWindowHandles();
	}

	@Override
	public String getWindowHandle() {
		return driver.getWindowHandle();
	}

	@Override
	public TargetLocator switchTo() {
		return driver.switchTo();
	}

	@Override
	public Navigation navigate() {
		return driver.navigate();
	}

	@Override
	public Options manage() {
		return driver.manage();
	}

	public abstract List<O> execute() throws Exception;
}
