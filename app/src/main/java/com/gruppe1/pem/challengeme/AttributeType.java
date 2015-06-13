package com.gruppe1.pem.challengeme;

/**
 * Created by Simon on 13.06.2015.
 */
public class AttributeType {
    private static final String DB_TABLE = Constants.DB_TABLE_PREFIX + "attribute_types";

    private int m_Id;
    private String m_name;
    private int m_valueType;
    private int is_unique;

    /*
     * --------------------------------------------------------------------
     * ------------------------- Getter and setter ------------------------
     * --------------------------------------------------------------------
     */

    public int getId() {
        return m_Id;
    }

    public void setId(int m_Id) {
        this.m_Id = m_Id;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String m_name) {
        this.m_name = m_name;
    }

    public int getValueType() {
        return m_valueType;
    }

    public void setValueType(int m_valueType) {
        this.m_valueType = m_valueType;
    }

    public int getIsUnique() {
        return is_unique;
    }

    public void setIsUnique(int is_unique) {
        this.is_unique = is_unique;
    }
}
