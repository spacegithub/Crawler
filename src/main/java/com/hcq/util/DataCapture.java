package com.hcq.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class DataCapture {
	private ThreadLocal<WebClient> clientThreadLocal;
	public DataCapture(){
		clientThreadLocal = new ThreadLocal<WebClient>();
	}
	public Document getDoc(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		
		//构造一个webClient 模拟Chrome 浏览器
		WebClient webClient = null;
		
		if((webClient = clientThreadLocal.get())==null){
			webClient = new WebClient(BrowserVersion.CHROME);
			//屏蔽日志信息
			LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
			        "org.apache.commons.logging.impl.NoOpLog");
			java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
			//支持JavaScript
			webClient.getOptions().setJavaScriptEnabled(false);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setActiveXNative(false);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
//			webClient.getOptions().setTimeout(10000);
			clientThreadLocal.set(webClient);
		}
		HtmlPage rootPage;
			rootPage = webClient.getPage(url);
		//设置一个运行JavaScript的时间
		webClient.waitForBackgroundJavaScript(5000);
		String html = rootPage.asXml();
		Document document = Jsoup.parse(html);
		return document;
	}
	
	// 正则匹配HTML
		public String reg(String text, String reg) {
			Pattern pat = Pattern.compile(reg);
			Matcher mat = pat.matcher(text);
			boolean rs = mat.find();
			if (rs) {
				return mat.group();
			}
			return null;
		}
}
