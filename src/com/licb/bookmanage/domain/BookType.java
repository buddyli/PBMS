package com.licb.bookmanage.domain;

import java.io.Serializable;

public class BookType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3849163092493324538L;
	private int id;
	private String name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
