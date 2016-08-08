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

package org.egovernments.egoverp.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import org.egovernments.egoverp.activities.SplashScreenActivity;
import org.egovernments.egoverp.network.SessionManager;

/**
 * Created by egov on 8/8/16.
 */
public class SMSListener extends BroadcastReceiver {

    private SmsMessage currentSMS;
    private String message;
    SessionManager sessionManager;

    public static String OTP_LISTENER="OTP_Listener";
    public static String PARAM_LAUCNH_FROM_SMS ="isLaunchFromSMS";
    public static String PARAM_OTP_CODE ="OTP_code";

    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                Object[] pdu_Objects = (Object[]) bundle.get("pdus");
                if (pdu_Objects != null) {

                    for (Object aObject : pdu_Objects) {

                        currentSMS = getIncomingMessage(aObject, bundle);

                        String senderNo = currentSMS.getDisplayOriginatingAddress();

                        message = currentSMS.getDisplayMessageBody();

                        sessionManager=new SessionManager(context);

                        if((message.contains("Your OTP for recovering password is") || message.contains("Your Portal Activation Code is"))
                                && TextUtils.isEmpty(sessionManager.getAccessToken()))
                        {

                            long lastOtpSentTime=0l;

                            if(message.contains("Your OTP for recovering password is"))
                            {
                                lastOtpSentTime=sessionManager.getForgotPasswordTime();
                            }
                            else
                            {
                                lastOtpSentTime=sessionManager.getRegisteredUserLastOTPTime();
                            }

                            long expiryOTPTime=lastOtpSentTime+(5*60*1000); //CALCULATE OTP Expiry Time (+5 mins)

                            if(lastOtpSentTime<expiryOTPTime)
                            {
                                boolean isRunning =sessionManager.isOTPLocalBroadCastRunning();

                                String otpCode=getOTPCode(message);

                                if(isRunning)
                                {
                                    Intent otpIntent = new Intent(OTP_LISTENER);
                                    otpIntent.putExtra(PARAM_OTP_CODE, otpCode);
                                    LocalBroadcastManager.getInstance(context).sendBroadcast(otpIntent);
                                }
                                else
                                {
                                    Intent appLaunchIntent = new Intent(context, SplashScreenActivity.class);
                                    appLaunchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    appLaunchIntent.putExtra(PARAM_LAUCNH_FROM_SMS, true);
                                    appLaunchIntent.putExtra(PARAM_OTP_CODE, otpCode);
                                    context.startActivity(appLaunchIntent);
                                }

                            }

                        }


                    }

                }


            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }

        return currentSMS;
    }

    private String getOTPCode(String message)
    {
        message=message.trim();
        String otpCode=message.substring(message.length()-5, message.length());
        return (TextUtils.isEmpty(otpCode)?"":otpCode);
    }

}
