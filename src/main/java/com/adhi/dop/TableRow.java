package com.adhi.dop;

public class TableRow {

	private String propertyName, material1Val, material2Val;

	public TableRow(String propertyName, String material1Val, String material2Val) {
		this.propertyName = propertyName;
		this.material1Val = material1Val;
		this.material2Val = material2Val;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public String getMaterial1Val() {
		return material1Val;
	}

	public String getMaterial2Val() {
		return material2Val;
	}

}
