package com.licb.bookmanage.domain;

public class Item {
	private int id;
	private String value;

	public Item(int id, String value) {
		super();
		this.id = id;
		this.value = value;
	}

	public Item() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

}
