package com.armaninyow.numericalhud;

public enum AnimationStyle {
	DECIMAL("Decimal"),
	FADE("Fade"),
	POPUP("Popup");
	
	private final String displayName;
	
	AnimationStyle(String displayName) {
		this.displayName = displayName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
}