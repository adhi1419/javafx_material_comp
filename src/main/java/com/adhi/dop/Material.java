package com.adhi.dop;

import java.util.HashMap;

public class Material {

	private String name;
	private HashMap<String, String> properties = new HashMap<>();

	public Material(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addProperty(String propertyName, String value) {
		properties.put(propertyName, value);
	}

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public String getPropertyValue(String propertyName) {
		return properties.get(propertyName);
	}

	public String toString() {
		return name;
	}
}
