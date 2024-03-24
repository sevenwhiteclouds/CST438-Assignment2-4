package com.cst438.test.utils;

public enum Constants {
    CHROME_DRIVER_FILE_LOCATION("<PATH HERE>/chromedriver-win64/chromedriver-win64/chromedriver.exe", 0),
    URL("http://localhost:3000", 0),
    SLEEP_DURATION("", 1000);

    private final String value;
    private final int intValue;

    Constants(String value, int intValue) {
        this.value = value;
        this.intValue = intValue;
    }

    public String getValue() {
        return value;
    }

    public int getIntValue() {
        return intValue;
    }
}

