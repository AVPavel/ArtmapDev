package com.example.demo.Globals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GlobalLogger {

    /**
     * Gets a logger instance for the provided class.
     *
     * @param clazz The class for which the logger is required.
     * @return Logger instance for the class.
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
