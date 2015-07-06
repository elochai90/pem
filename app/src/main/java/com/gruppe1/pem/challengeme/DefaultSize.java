package com.gruppe1.pem.challengeme;

import java.util.Date;

/**
 * Created by bianka on 06.07.2015.
 */
public class DefaultSize {

    private String defaultSizeName;
    private String defaultSizeValue;

    public DefaultSize(String defaultSizeName, String defaultSizeValue)
    {
        this.defaultSizeName = defaultSizeName;
        this.defaultSizeValue = defaultSizeValue;
    }

    public String getDefaultSizeName() {
        return defaultSizeName;
    }

    public String getDefaultSizeValue() {
        return defaultSizeValue;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof DefaultSize && getDefaultSizeName() == ((DefaultSize) o).getDefaultSizeName());

    }
}