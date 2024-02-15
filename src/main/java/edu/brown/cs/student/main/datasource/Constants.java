package edu.brown.cs.student.main.datasource;

import java.util.concurrent.TimeUnit;

/**
 * This is a Constants class that determines the erasure policy for the Cache. Developers can
 * access this and change the time it takes before a query entry is deleted.
 */
public class Constants {
    public static final Integer EXPIRE_TIME_IN_MINUTES = 5;
    public static final Integer EXPIRE_TIME_IN_SECONDS = 300;
    public static final TimeUnit TIME_MINUTE = TimeUnit.MINUTES;

    public static final TimeUnit TIME_SECOND = TimeUnit.SECONDS;
}
