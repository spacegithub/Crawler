package com.hcq.util;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author zhuxiongxian
 * @version 1.0
 * @created at 2017��3��17�� ����6:06:32
 */
public class JsoupHelper {

    /**
     * ��ȡ����
     * 
     * @param url
     *            ����url
     * @param params
     *            ����
     * @param charset
     *            �������뷽ʽ
     * @param headers
     *            ͷ����Ϣ
     * @return
     */
    public static Connection getConnection(String url, Map<String, String> params, String charset,
            Map<String, String> headers) {
        if (params != null) {
            List<NameValuePair> paramList = new ArrayList<NameValuePair>();
            Iterator<Entry<String, String>> iter = params.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, String> entry = iter.next();
                String key = entry.getKey();
                String value = entry.getValue();
                paramList.add(new BasicNameValuePair(key, value));
            }
            try {
                String paramStr = EntityUtils.toString(new UrlEncodedFormEntity(paramList, charset));
                StringBuffer sb = new StringBuffer();
                sb.append(url);
                if (url.indexOf("?") > 0) {
                    sb.append("&");
                } else {
                    sb.append("?");
                }
                sb.append(paramStr);
                url = sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Connection conn = Jsoup.connect(url);
        conn.timeout(10000); // 10�볬ʱ
        conn.ignoreContentType(true);

        if (headers != null) {
            Iterator<Entry<String, String>> iter = headers.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, String> entry = iter.next();
                String key = entry.getKey();
                String value = entry.getValue();
                conn.header(key, value);
            }
        }

        return conn;
    }

    public static String get(String url, Map<String, String> params, String charset,
            Map<String, String> headers) {
        String result = "";
        try {
            Connection conn = getConnection(url, params, charset, headers);
            Response response = conn.execute();
            result = response.body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /*
     * ��ü�����ϵ�ҳ����Ϣ
     */
    public Document getDoc(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		//������־��Ϣ
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
		        "org.apache.commons.logging.impl.NoOpLog");
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		//֧��JavaScript
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setTimeout(5000);
		HtmlPage rootPage = webClient.getPage(url);
		//����һ������JavaScript��ʱ��
		webClient.waitForBackgroundJavaScript(5000);
		String html = rootPage.asXml();
		Document document = Jsoup.parse(html);
		return document;
	}
    
    // ����ƥ��HTML
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