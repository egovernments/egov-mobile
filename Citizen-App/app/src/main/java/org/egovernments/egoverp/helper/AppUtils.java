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

package org.egovernments.egoverp.helper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedHashTreeMap;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.config.Config;
import org.egovernments.egoverp.config.SessionManager;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Apputil class
 */
public class AppUtils {

    private static final String PATTERN_PAN = "[A-Z]{5}[0-9]{4}[A-Z]{1}";

    static ConfigManager configManager;
    static SessionManager sessionManager;

    public static ConfigManager getConfigManager(Context appContext) throws IOException {
        if (configManager == null) {
            InputStream inputStream = appContext.getAssets().open("egov.conf");
            configManager = new ConfigManager(inputStream, appContext);
            inputStream.close();
        }
        return configManager;
    }

    public static SessionManager getSessionManger(Context applicationContext) {
        if (sessionManager == null) {
            sessionManager = new SessionManager(applicationContext);
        }
        return sessionManager;
    }

    public static String getNullAsEmptyString(JsonElement jsonElement) {
        return jsonElement.isJsonNull() ? "" : jsonElement.getAsString();
    }

    public static boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public static boolean isValidPANNo(String panNo) {
        Pattern pattern = Pattern.compile(PATTERN_PAN);
        return pattern.matcher(panNo).matches();
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value).setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    /* PASSWORD LEVEL NONE VALIDATION */
    public static boolean checkPasswordNoneLevel(String password) {
        return password.length() >= 6;
    }

    /* PASSWORD LEVEL LOW VALIDATION */
    public static boolean checkPasswordLowLevel(String password)
    {
        String numExp = ".*[0-9].*";
        String alphaCapExp=".*[A-Z].*";
        String alphaSmallExp=".*[a-z].*";
        return (password.matches(numExp) && password.matches(alphaCapExp) && password.matches(alphaSmallExp) && (password.length() >= 6));
    }

    /* PASSWORD LEVEL MEDIUM VALIDATION */
    public static boolean checkPasswordMediumLevel(String password)
    {
        String numExp=".*[0-9].*";
        String alphaCapExp=".*[A-Z].*";
        String alphaSmallExp=".*[a-z].*";
        return (password.matches(numExp) && password.matches(alphaCapExp) && password.matches(alphaSmallExp) && (password.length()>=8));
    }

    /* PASSWORD LEVEL HIGH VALIDATION */
    public static boolean checkPasswordHighLevel(String password)
    {
        String expression="(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@$%^+=_?();:])(?=\\S+$).{8,32}$";
        String exceptCharExp=".*[&<>#%\"'].*";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches() && !password.matches(exceptCharExp);
    }


    public static boolean isValidPassword(String password, ConfigManager configManager) {
        String pwdLevel=configManager.getString(Config.APP_PASSWORD_LEVEL);
        if(pwdLevel.equals(PasswordLevel.HIGH))
        {
            return AppUtils.checkPasswordHighLevel(password);
        }
        else if(pwdLevel.equals(PasswordLevel.MEDIUM))
        {
            return AppUtils.checkPasswordMediumLevel(password);
        } else if (pwdLevel.equals(PasswordLevel.LOW))
        {
            return AppUtils.checkPasswordLowLevel(password);
        } else {
            return AppUtils.checkPasswordNoneLevel(password);
        }
    }

    public static void showImageDialog(Context context, String title, String content, int imgResId, String negativeButtonText)
    {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final ViewGroup nullParent = null;
        final View dialogView = inflater.inflate(R.layout.dialog_receipt_info, nullParent);

        TextView tvDesc=(TextView)dialogView.findViewById(R.id.tvContent);
        ImageView imgView=(ImageView)dialogView.findViewById(R.id.imgContent);

        tvDesc.setText(content);
        imgView.setImageResource(imgResId);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                context);
        builder.setTitle(title);
        builder.setView(dialogView);
        builder.setNegativeButton(negativeButtonText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {
                    }
                });
        builder.show();
    }

    public static Integer getAppVersionCode(Context context) {
        Integer versionCode = 0;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }


    //check internet connection available method
    public static boolean checkInternetConnectivity(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            return activeNetwork != null;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Bundle getTransitionBundle(Activity activity) {
        final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, true);
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs);
        return transitionActivityOptions.toBundle();
    }

    public static String getPasswordConstraintInformation(ConfigManager configManager, Context context) {
        String pwdLevel = configManager.getString(Config.APP_PASSWORD_LEVEL);
        if (pwdLevel.equals(PasswordLevel.HIGH)) {
            return context.getString(R.string.password_level_high);
        } else if (pwdLevel.equals(PasswordLevel.MEDIUM)) {
            return context.getString(R.string.password_level_medium);
        } else if (pwdLevel.equals(PasswordLevel.LOW)) {
            return context.getString(R.string.password_level_low);
        } else {
            return context.getString(R.string.password_level_none);
        }
    }

    public static boolean checkPermission(Activity activity, String permission) {
        int result = ContextCompat.checkSelfPermission(activity,
                permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    public static void showLanguageChangePrompt(Context context, ConfigManager configManager, String title, String[] btnText,
                                                final LanguageChangeListener listener, String selectedLocaleCode) throws IOException {

        final String[] languageCodes = getSupportedLocalesCode(configManager);

        final CharSequence[] languagesName = getLanguagesListByLocaleCodes(languageCodes);
        if (languagesName == null) return;

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setCancelable(false);

        final ArrayList<Integer> selectedLanguageIdx = new ArrayList<>();
        selectedLanguageIdx.add(0, 0);

        int selectedIdx = Arrays.asList(languageCodes).indexOf(selectedLocaleCode);

        DialogInterface.OnClickListener buttonListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface,
                                int paramInt) {
                Integer idx = selectedLanguageIdx.get(0);
                listener.languageChangeListener(languageCodes[idx].trim(), languagesName[idx].toString());
                paramDialogInterface.dismiss();
            }
        };

        builder.setSingleChoiceItems(languagesName, selectedIdx, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedLanguageIdx.add(0, i);
            }
        });

        builder.setPositiveButton(btnText[0], buttonListener);

        if (btnText.length != 1) {
            builder.setNegativeButton(btnText[1], null);
        }
        builder.show();
    }

    @Nullable
    private static CharSequence[] getLanguagesListByLocaleCodes(String[] localeCodes) {
        CharSequence[] languageNames = new CharSequence[localeCodes.length];
        int idx = 0;
        for (String languageCode : localeCodes) {
            languageNames[idx] = getLanguageDisplayNameByLocaleCode(languageCode);
            idx++;
        }
        return languageNames;
    }

    @NonNull
    public static String[] getSupportedLocalesCode(ConfigManager configManager) throws IOException {
        //retrieve supported languages list from config
        return configManager.getString(Config.APP_LOCALES).replaceAll("\\s*", "").split(",");
    }

    public static String getLanguageDisplayNameByLocaleCode(String localeCode) {
        Locale locale = new Locale(localeCode.trim());
        return locale.getDisplayLanguage(locale);
    }

    //return Map<language name : language code>
    public static Map<String, String> getSupportedLanguagesList(ConfigManager configManager) throws IOException {
        Map<String, String> languagesList = new LinkedHashTreeMap<>();
        String[] supportedLanguageCodes = getSupportedLocalesCode(configManager);
        for (String languageCode : supportedLanguageCodes) {
            languagesList.put(getLanguageDisplayNameByLocaleCode(languageCode), languageCode);
        }
        return languagesList;
    }

}
