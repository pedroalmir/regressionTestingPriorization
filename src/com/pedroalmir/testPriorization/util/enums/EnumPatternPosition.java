package com.pedroalmir.testPriorization.util.enums;

public enum EnumPatternPosition {
	FIRST("first"), LAST("last"), ANY_WHERE("anywhere");

	private String description;

	EnumPatternPosition(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}
}
