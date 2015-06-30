package com.gruppe1.pem.challengeme;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by bianka on 05.06.2015.
 */
public class CompareItem{

    // TODO: not public, but method get...
    // TODO: besser waere zwei Item Objekte zu uebergeben mit namen und createdAt
    public int iconItem1;
    public int iconItem2;
    public String name;
    public String nameItem1;
    public String nameItem2;
    public Date createdAt;

    public CompareItem(int iconItem1, int iconItem2, String name, String nameItem1, String nameItem2, Date createdAt)
    {
        this.iconItem1 = iconItem1;
        this.iconItem2 = iconItem2;
        this.name = name;
        this.nameItem1 = nameItem1;
        this.nameItem2 = nameItem2;
        this.createdAt  = createdAt;
    }

    public int getIconItem1(){
        return iconItem1;
    }
}