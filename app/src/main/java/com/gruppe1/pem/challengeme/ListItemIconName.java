package com.gruppe1.pem.challengeme;

import java.util.Date;

/**
 * Created by bianka on 05.06.2015.
 */
public class ListItemIconName {
    public int icon;
    public String name;
    public Date date;

    public ListItemIconName(int icon, String name)
    {
        this.icon = icon;
        this.name = name;
    }

    public ListItemIconName(int icon, String name, Date date)
    {
        this.icon = icon;
        this.name = name;
        this.date = date;
    }
}