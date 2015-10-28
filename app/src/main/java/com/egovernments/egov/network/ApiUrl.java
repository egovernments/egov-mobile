package com.egovernments.egov.network;


public class ApiUrl {

    public final static String api_baseUrl = "https://phoenix-qa.egovernments.org/api/v1.0";
    public final static String login_baseUrl = "https://phoenix-qa.egovernments.org/api";

    //Calls to picasso do not inherit entire URLs from here, will need to be changed manually
    //Redirect URL parameter of calls to CITIZEN_PASSWORD_RECOVER must be changed manually

    /**
     * Grievance
     */
    public final static String COMPLAINT_GET_TYPES = "/complaint/getAllTypes";

    public final static String COMPLAINT_GET_FREQUENTLY_FILED_TYPES = "/complaint/getFrequentlyFiledTypes";

    public final static String COMPLAINT_CREATE = "/complaint/create";

    public final static String COMPLAINT_UPLOAD_SUPPORT_DOCUMENT = "/complaint/{complaintNo}/uploadSupportDocument";

    public final static String COMPLAINT_DOWNLOAD_SUPPORT_DOCUMENT = "/complaint/{complaintNo}/downloadSupportDocument";

    public final static String COMPLAINT_GET_LOCATION_BY_NAME = "/complaint/getLocation";

    public final static String COMPLAINT_LATEST = "/complaint/latest/{page}/{pageSize}";

    public final static String COMPLAINT_NEARBY = "/complaint/nearby/{page}/{pageSize}";

    public final static String COMPLAINT_SEARCH = "/complaint/search";

    public final static String COMPLAINT_DETAIL = "/complaint/{complaintNo}/detail";

    public final static String COMPLAINT_STATUS = "/complaint/{complaintNo}/status";

    public final static String COMPLAINT_UPDATE_STATUS = "/complaint/{complaintNo}/updateStatus";

    /**
     * Citizen
     */
    public final static String CITIZEN_REGISTER = "/createCitizen";

    public final static String CITIZEN_ACTIVATE = "/activateCitizen";

    public final static String CITIZEN_LOGIN = "/oauth/token";

    public final static String CITIZEN_PASSWORD_RECOVER = "/recoverPassword";

    public final static String CITIZEN_GET_PROFILE = "/citizen/getProfile";

    public final static String CITIZEN_UPDATE_PROFILE = "/citizen/updateProfile";

    public final static String CITIZEN_LOGOUT = "/citizen/logout";

    public final static String CITIZEN_GET_MY_COMPLAINT = "/citizen/getMyComplaint/{page}/{pageSize}";

    public final static String CITIZEN_SEND_OTP = "/sendOTP";
}
