package com.egovernments.egov.network;


/**
 * The API endpoints
 **/

public class ApiUrl {
    /**
     * Grievances
     */
    public final static String COMPLAINT_GET_TYPES = "/api/v1.0/complaint/getAllTypes";

    public final static String COMPLAINT_GET_FREQUENTLY_FILED_TYPES = "/api/v1.0/complaint/getFrequentlyFiledTypes";

    public final static String COMPLAINT_CREATE = "/api/v1.0/complaint/create";

    public final static String COMPLAINT_UPLOAD_SUPPORT_DOCUMENT = "/api/v1.0/complaint/{complaintNo}/uploadSupportDocument";

    public final static String COMPLAINT_DOWNLOAD_SUPPORT_DOCUMENT = "/api/v1.0/complaint/{complaintNo}/downloadSupportDocument";

    public final static String COMPLAINT_GET_LOCATION_BY_NAME = "/api/v1.0/complaint/getLocation";

    public final static String COMPLAINT_LATEST = "/api/v1.0/complaint/latest/{page}/{pageSize}";

    public final static String COMPLAINT_NEARBY = "/api/v1.0/complaint/nearby/{page}/{pageSize}";

    public final static String COMPLAINT_SEARCH = "/api/v1.0/complaint/search";

    public final static String COMPLAINT_DETAIL = "/api/v1.0/complaint/{complaintNo}/detail";

    public final static String COMPLAINT_HISTORY = "/api/v1.0/complaint/{complaintNo}/complaintHistory";

    public final static String COMPLAINT_STATUS = "/api/v1.0/complaint/{complaintNo}/status";

    public final static String COMPLAINT_UPDATE_STATUS = "/api/v1.0/complaint/{complaintNo}/updateStatus";

    /**
     * Citizen
     */
    public final static String CITIZEN_REGISTER = "/api/v1.0/createCitizen";

    public final static String CITIZEN_ACTIVATE = "/api/v1.0/activateCitizen";

    public final static String CITIZEN_LOGIN = "/api/oauth/token";

    public final static String CITIZEN_PASSWORD_RECOVER = "/api/v1.0/recoverPassword";

    public final static String CITIZEN_GET_PROFILE = "/api/v1.0/citizen/getProfile";

    public final static String CITIZEN_UPDATE_PROFILE = "/api/v1.0/citizen/updateProfile";

    public final static String CITIZEN_LOGOUT = "/api/v1.0/citizen/logout";

    public final static String CITIZEN_GET_MY_COMPLAINT = "/api/v1.0/citizen/getMyComplaint/{page}/{pageSize}";

    public final static String CITIZEN_SEND_OTP = "/api/v1.0/sendOTP";
}
