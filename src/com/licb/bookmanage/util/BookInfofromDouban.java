package com.licb.bookmanage.util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.licb.bookmanage.domain.Book;

public class BookInfofromDouban {
	private static final HttpClient client;
	private static XmlPullParserFactory factory;
	private static XmlPullParser parser;
	private static final String doubanAPI = "http://api.douban.com/book/subject/isbn/";
	static {
		client = new DefaultHttpClient();
		try {
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			parser = factory.newPullParser();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}

	private BookInfofromDouban() {
	}

	public static Book getBookInfofromDouban(String isbn) {
		Book book = null;
		Pattern p = Pattern.compile("([\\d]+)[^\\d]*");
		try {
			String url = doubanAPI + isbn;
			System.out.println("请求豆瓣接口：" + url);
			HttpGet getReq = new HttpGet(url);
			// 豆瓣好贱啊，对请求的User-Agent做限制。这里需要模拟一下浏览器的请求头
			getReq.setHeader("User-Agent",
					"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:17.0) Gecko/17.0 Firefox/17.0");
			HttpResponse response = client.execute(getReq);
			System.out.println("豆瓣返回状态码: "
					+ response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				parser.setInput(response.getEntity().getContent(), "UTF-8");
				book = new Book();
				for (int eventType = parser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = parser
						.next()) {
					switch (eventType) {
					case XmlPullParser.START_TAG:
						String tagName = parser.getName();
						if (tagName != null && "attribute".equals(tagName)) {
							String attrName = parser.getAttributeValue(null,
									"name");
							if (attrName.equals("title")) {
								book.setTitle(parser.nextText());
							} else if (attrName.equals("subtitle")) {
								book.setSubtitle(parser.nextText());
							} else if (attrName.equals("author")) {
								book.setAuthor(parser.nextText());
							} else if (attrName.equals("translator")) {
								book.setTranslator(parser.nextText());
							} else if (attrName.equals("price")) {
								book.setPrice(parser.nextText());
							} else if (attrName.equals("publisher")) {
								book.setPublisher(parser.nextText());
							} else if (attrName.equals("isbn13")) {
								book.setIsbn(parser.nextText());
							} else if (attrName.equals("pages")) {
								// 页码有的是 “428 页”的形式，有的是“428“的形式
								String pages = parser.nextText();
								Matcher m = p.matcher(pages);
								while (m.find()) {
									String num = m.group(1);
									book.setPages(StringUtils.isNotBlank(num) ? Integer
											.parseInt(num) : -1);
								}
							}
						}
						break;
					case XmlPullParser.END_TAG:
						tagName = parser.getName();
						// System.out.println("===遇到结束标签 " + tagName);
						break;
					default:
						tagName = parser.getName();
						// System.out.println("===遇到既不是开始也表示结束的标签 " + tagName);
						break;
					}
				}
			}

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		return book;
	}
}
