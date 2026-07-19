package com.armaninyow.numericalhud;

public enum TextStyle {
	SHADOW("Shadow"),
	OUTLINE("Outline");

	private final String displayName;

	TextStyle(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}