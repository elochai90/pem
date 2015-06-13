package com.gruppe1.pem.challengeme;

/**
 * Created by Simon on 13.06.2015.
 */
public class Attribute {
    private static final String DB_TABLE = Constants.DB_TABLE_PREFIX + "item_attribute_types";

    private int m_id;
    private int m_itemId;
    private AttributeType m_attributeType;
    private Object m_value;

    /*
     * --------------------------------------------------------------------
     * ------------------------- Getter and setter ------------------------
     * --------------------------------------------------------------------
     */

    public int getId() {
        return m_id;
    }

    public void setId(int m_id) {
        this.m_id = m_id;
    }

    public int getItemId() {
        return m_itemId;
    }

    public void setItemId(int m_itemId) {
        this.m_itemId = m_itemId;
    }

    public AttributeType getAttributeType() {
        return m_attributeType;
    }

    public void setAttributeType(AttributeType m_attributeType) {
        this.m_attributeType = m_attributeType;
    }

    public Object getValue() {
        return m_value;
    }

    public void setValue(Object m_value) {
        this.m_value = m_value;
    }

}
