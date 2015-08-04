/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 * 
 * Copyright (C) <2015> eGovernments Foundation
 * 
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 * 
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 * 
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 * 
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 * 
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 * 
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

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
