package com.gruppe1.pem.challengeme;

import java.util.Date;

/**
 * Created by bianka on 05.06.2015.
 * ListItemIconName class
 */
public class ListItemIconName {

    private int elementId;
    private int icon;
    private String name;
    private Date date;
    private String itemFile;

    public ListItemIconName(int elementId, int icon, String name, String itemBitmap) {
        this.elementId = elementId;
        this.icon = icon;
        this.name = name;
        this.itemFile = itemBitmap;
    }

    public ListItemIconName(int elementId, int icon, String name, String itemFile, Date date) {
        this.elementId = elementId;
        this.icon = icon;
        this.name = name;
        this.itemFile = itemFile;
        this.date = date;
    }

    public int getElementId() {
        return elementId;
    }


    public int getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }
    public String getItemFile() {
        return itemFile;
    }
}