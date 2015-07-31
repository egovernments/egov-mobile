package org.egov.android.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.util.Log;

public class ReflectionUtil {

    private final static String TAG = ReflectionUtil.class.getName();

    /**
     * 
     * @param instance
     * @param methodName
     * @return
     */
    public static Object invokeMethod(Object instance, String methodName) {
        return ReflectionUtil.invokeMethod(instance, methodName, new Class[] {}, new Object[] {});
    }

    /**
     * 
     * @param instance
     * @param methodName
     * @param argsType
     * @param args
     * @return
     */
    public static Object invokeMethod(Object instance,
                                      String methodName,
                                      Class<?>[] argsType,
                                      Object[] args) {
        Method method;
        try {
            method = instance.getClass().getDeclaredMethod(methodName, argsType);
            method.setAccessible(true);
            return method.invoke(instance, args);
        } catch (Exception e) {
            Log.d(TAG, "********** errro ***********");
            Log.d(TAG, e.getMessage());
        }

        return null;
    }

    /**
     * 
     * @param instance
     * @param fields
     * @param data
     */
    public static void setFieldData(Object instance, String fieldName, Object data) {
        try {
            Class<?> clazz = instance.getClass();
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, data);
        } catch (Exception e) {
            Log.d(TAG, "set field error " + e.getMessage());
        }

        //ReflectionUtil.setFieldData(instance, new String[] { fieldName }, new Object[] { data });
    }

    /**
     * 
     * @param instance
     * @param fields
     * @param data
     */
    public static void setFieldData(Object instance, String[] fields, Object[] data) {
        int i = 0;
        for (String fieldName : fields) {
            ReflectionUtil.setFieldData(instance, fieldName, data[i++]);
        }
    }
}
