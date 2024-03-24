public enum Constants {
    DRIVER_FILE_LOCATION(Constants.CHROME_DRIVER_FILE_LOCATION),
    URL(Constants.URL),
    SLEEP_DURATION(Constants.SLEEP_DURATION);

    public static final String CHROME_DRIVER_FILE_LOCATION = "<PATH HERE>/chromedriver-win64/chromedriver-win64/chromedriver.exe";
    public static final String URL = "http://localhost:3000";
    public static final int SLEEP_DURATION = 1000; // milliseconds

}

