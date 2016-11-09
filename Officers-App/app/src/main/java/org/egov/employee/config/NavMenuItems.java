package org.egov.employee.config;

/**
 * Created by egov on 4/10/16.
 */

public enum NavMenuItems {
    WORKLIST(0),
    GRIEVANCE(1),
    LOGOUT(2);

    private final int menuCode;

    NavMenuItems(int menuCode) {
        this.menuCode = menuCode;
    }

    public int getMenuCode() {
        return menuCode;
    }
}
