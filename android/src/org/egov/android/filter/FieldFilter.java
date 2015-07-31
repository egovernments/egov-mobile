package org.egov.android.filter;

import java.lang.reflect.Field;
import java.util.List;

import org.egov.android.annotation.Column;
import org.egov.android.common.Purpose;

public class FieldFilter implements IFilter<Field, Column> {

    private Purpose purpose = Purpose.ALL;
    private List<String> fieldNames = null;

    public FieldFilter(Purpose purpose) {
        this.purpose = purpose;
    }

    public Purpose getPurpose() {
        return purpose;
    }

    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }

    public List<String> getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(List<String> fieldNames) {
        this.fieldNames = fieldNames;
    }

    @Override
    public boolean filter(Field field, Column column) {
        if (purpose.equals(Purpose.INSERT) && column.isAutoIncrement()) {
            return false;
        }
        if (this.fieldNames != null && !this.fieldNames.contains(field.getName())) {
            return false;
        }
        return true;
    }
}
