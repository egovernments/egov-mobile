package org.egov.android.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.egov.android.data.ColumnType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface Column {

    String name() default "";

    ColumnType type() default ColumnType.TEXT;

    boolean allowNull() default true;

    boolean isAutoIncrement() default false;

    boolean isPrimaryKey() default false;

    String version() default "1.0";

    String defaultValue() default "";
}
