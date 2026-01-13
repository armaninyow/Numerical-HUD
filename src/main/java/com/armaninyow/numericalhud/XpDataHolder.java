package com.armaninyow.numericalhud;

public class XpDataHolder {
    private static int realXpLevel = 0;
    private static float realXpProgress = 0.0f;
    private static boolean initialized = false;
    
    public static void setRealXpData(int level, float progress) {
        realXpLevel = level;
        realXpProgress = progress;
        initialized = true;
    }
    
    public static int getRealXpLevel() {
        return realXpLevel;
    }
    
    public static float getRealXpProgress() {
        return realXpProgress;
    }
    
    public static boolean isInitialized() {
        return initialized;
    }
    
    public static void reset() {
        realXpLevel = 0;
        realXpProgress = 0.0f;
        initialized = false;
    }
}