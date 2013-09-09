package com.licb.bookmanage.domain;

import java.io.Serializable;

/**
 * 图书实体
 * 
 * @author licb
 * 
 */
public class Book implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 415665083660632882L;
	private long id;
	private String name;// 根据图书的标题和副标题ｇｅｔ方法做了改动
	private String isbn;
	private String author;// 原作者
	private String translator;// 译者
	private String price;// 价格
	private String publisher;// 出版社
	private String title;// 标题
	private String subtitle;// 副标题
	private long insertTime;
	private int pages;// 总页码
	private int readed;// 已读页码
	private String bookType;
	private String bookBuyStatus;
	private String bookReadStatus;
	// 格式化好的图书录入时间
	private String formatInsertTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		if (subtitle != null) {
			name = title + " " + subtitle;
		} else {
			name = title;
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public long getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(long insertTime) {
		this.insertTime = insertTime;
	}

	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public int getReaded() {
		return readed;
	}

	public void setReaded(int readed) {
		this.readed = readed;
	}

	public String getTranslator() {
		return translator;
	}

	public void setTranslator(String translator) {
		this.translator = translator;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getBookType() {
		return bookType;
	}

	public void setBookType(String bookType) {
		this.bookType = bookType;
	}

	public String getBookBuyStatus() {
		return bookBuyStatus;
	}

	public void setBookBuyStatus(String bookBuyStatus) {
		this.bookBuyStatus = bookBuyStatus;
	}

	public String getBookReadStatus() {
		return bookReadStatus;
	}

	public void setBookReadStatus(String bookReadStatus) {
		this.bookReadStatus = bookReadStatus;
	}

	public String getFormatInsertTime() {
		return formatInsertTime;
	}

	public void setFormatInsertTime(String formatInsertTime) {
		this.formatInsertTime = formatInsertTime;
	}

}
