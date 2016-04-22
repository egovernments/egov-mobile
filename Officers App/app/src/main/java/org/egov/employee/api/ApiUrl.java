package org.egov.employee.api;

/**
 * Created by egov on 11/1/16.
 */
public class ApiUrl {

    public final static String AUTHORIZATION = "Basic ZWdvdi1hcGk6ZWdvd i1hcGk=";
    public final static String USER_TYPE_EMPLOYEE="EMPLOYEE";

    public final static String EMPLOYEE_LOGIN = "api/oauth/token";

    public final static String EMPLOYEE_LOGOUT = "api/v1.0/employee/logout";

    public final static String EMPLOYEE_WORKLIST_TYPES = "api/v1.0/employee/inbox";

    public final static String EMPLOYEE_WORKLIST = "api/v1.0/employee/inbox/{worklisttype}/{from}/{to}";

    public final static String PGR_COMPLAINT_DETAILS = "api/v1.0/complaint/detail/{complaintNo}";

    public final static String PGR_DOWNLOAD_IMAGE = "api/v1.0/complaint/downloadfile/"; //Need to append file id with this url for download particular image

    public final static String PGR_COMPLAINT_HISTORY = "/api/v1.0/complaint/{complaintNo}/complaintHistory";




}
