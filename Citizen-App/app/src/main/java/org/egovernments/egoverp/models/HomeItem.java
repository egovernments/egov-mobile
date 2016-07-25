/*
 * ******************************************************************************
 *  eGov suite of products aim to improve the internal efficiency,transparency,
 *      accountability and the service delivery of the government  organizations.
 *
 *        Copyright (C) <2016>  eGovernments Foundation
 *
 *        The updated version of eGov suite of products as by eGovernments Foundation
 *        is available at http://www.egovernments.org
 *
 *        This program is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        any later version.
 *
 *        This program is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with this program. If not, see http://www.gnu.org/licenses/ or
 *        http://www.gnu.org/licenses/gpl.html .
 *
 *        In addition to the terms of the GPL license to be adhered to in using this
 *        program, the following additional terms are to be complied with:
 *
 *    	1) All versions of this program, verbatim or modified must carry this
 *    	   Legal Notice.
 *
 *    	2) Any misrepresentation of the origin of the material is prohibited. It
 *    	   is required that all modified versions of this material be marked in
 *    	   reasonable ways as different from the original version.
 *
 *    	3) This license does not grant any rights to any user of the program
 *    	   with regards to rights under trademark law for use of the trade names
 *    	   or trademarks of eGovernments Foundation.
 *
 *      In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *  *****************************************************************************
 */

package org.egovernments.egoverp.models;


public class HomeItem {

    private String title;
    private String description;
    private int icon;
    private boolean isGrievanceItem=false;
    private boolean isNotificationItem =false;
    private NotificationItem notificationItem;
    int iconColor;

    public HomeItem(String title, int icon, String description) {
        this.title = title;
        this.description = description;
        this.icon = icon;
    }

    public HomeItem(String title, int icon, String description, int iconColor) {
        this.title = title;
        this.icon = icon;
        this.description = description;
        this.iconColor = iconColor;
    }

    public HomeItem(String title, int icon, String description, boolean isNotificationItem, NotificationItem notificationItem) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.isNotificationItem = isNotificationItem;
        this.notificationItem=notificationItem;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIcon() {
        return icon;
    }

    public boolean isGrievanceItem() {
        return isGrievanceItem;
    }

    public void setGrievanceItem(boolean grievanceItem) {
        isGrievanceItem = grievanceItem;
    }

    public boolean isNotificationItem() {
        return isNotificationItem;
    }

    public void setNotificationItem(boolean notificationItem) {
        isNotificationItem = notificationItem;
    }

    public NotificationItem getNotificationItem() {
        return notificationItem;
    }

    public void setNotificationItem(NotificationItem notificationItem) {
        this.notificationItem = notificationItem;
    }

    public int getIconColor() {
        return iconColor;
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
    }
}
