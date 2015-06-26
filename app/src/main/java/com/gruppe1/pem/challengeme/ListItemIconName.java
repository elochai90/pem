package com.gruppe1.pem.challengeme;

import android.util.Log;

import java.util.Date;

/**
 * Created by bianka on 05.06.2015.
 */
public class ListItemIconName {

    // TODO: not public but  getters and setters
    public int elementId;
    public int icon;
    public String name;
    public Date date;

    public ListItemIconName(int elementId, int icon, String name)
    {
        this.elementId = elementId;
        this.icon = icon;
        this.name = name;

//        Log.e("###LIST", this.name);
    }

    public ListItemIconName(int elementId, int icon, String name, Date date)
    {
        this.elementId = elementId;
        this.icon = icon;
        this.name = name;
        this.date = date;
    }
}