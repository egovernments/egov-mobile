package org.egov.android.data;

public enum ColumnType {

    INTEGER("integer"),
    /**
     * 
     */
    REAL("real"),
    /**
     * 
     */
    BLOB("blob"),
    /**
     * 
     */
    DOUBLE("double"),
    /**
     * 
     */
    FLOAT("float"),
    /**
     * 
     */
    TEXT("text"),
    /**
     * 
     */
    TIMESTAMP("timestamp");

    private String type = "";

    ColumnType(String type) {
        this.type = type;
    }

    public ColumnType getBaseType() {

        if (this.type.matches("text")) {
            return ColumnType.TEXT;

        } else if (this.type.matches("integer|double|float")) {
            return ColumnType.INTEGER;

        } else if (this.type.matches("real")) {
            return ColumnType.REAL;

        } else if (this.type.matches("blob")) {
            return ColumnType.BLOB;

        } else if (this.type.matches("timestamp")) {
            return ColumnType.TIMESTAMP;
        }

        return null;
    }

    @Override
    public String toString() {
        return this.type;
    }

}
