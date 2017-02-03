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

package org.egovernments.egoverp.activities;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.Slide;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.api.ApiController;
import org.egovernments.egoverp.api.ApiUrl;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.helper.CustomAutoCompleteTextView;
import org.egovernments.egoverp.helper.NothingSelectedSpinnerAdapter;
import org.egovernments.egoverp.listeners.SMSListener;
import org.egovernments.egoverp.models.City;
import org.egovernments.egoverp.models.District;
import org.egovernments.egoverp.models.RegisterRequest;
import org.egovernments.egoverp.services.UpdateService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;

import static org.egovernments.egoverp.config.Config.API_MULTICITIES;
import static org.egovernments.egoverp.config.Config.API_MULTIPLE_CITIES_URL;

@SuppressWarnings("unchecked")
public class RegisterActivity extends BaseActivity {

    public static Boolean isRunning = false;
    public static Boolean isBroadcastRunning = false;

    List<District> districtsList;
    List<City> citiesList;
    boolean isMultiCity = true;
    EditText name_edittext;
    EditText email_edittext;
    EditText phoneno_edittext;
    EditText password_edittext;
    EditText confirmpassword_edittext;
    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;
    private String deviceID;
    private String deviceOS;
    private String deviceType;
    BroadcastReceiver otpReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setOTPCode(intent);
        }
    };
    private TextView municipalityInfo;
    private TextInputLayout nameInputLayout;
    private Handler handler;
    private Spinner spinnerCity;
    private Spinner spinnerDistrict;
    private CustomAutoCompleteTextView cityAutoCompleteTextView;
    private CustomAutoCompleteTextView districtAutoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.createaccount_label);
        setContentViewWithNavBar(R.layout.activity_register, false);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setupWindowAnimations();
        }

        spinnerCity = (Spinner) findViewById(R.id.signup_city);
        spinnerDistrict =(Spinner) findViewById(R.id.spinner_district);

        municipalityInfo = (TextView) findViewById(R.id.municipality_change_info);
        nameInputLayout = (TextInputLayout) findViewById(R.id.signup_name_inputlayout);

        name_edittext = (EditText) findViewById(R.id.signup_name);
        email_edittext = (EditText) findViewById(R.id.signup_email);
        phoneno_edittext = (EditText) findViewById(R.id.signup_phoneno);
        password_edittext = (EditText) findViewById(R.id.signup_password);
        confirmpassword_edittext = (EditText) findViewById(R.id.signup_confirmpassword);

        cityAutoCompleteTextView = (CustomAutoCompleteTextView) findViewById(R.id.register_spinner_autocomplete);
        districtAutoCompleteTextView =(CustomAutoCompleteTextView)findViewById(R.id.autocomplete_district);

        cityAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSnackBar(getString(R.string.fetch_list_municipality));
            }
        });

        districtAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSnackBar(getString(R.string.fetch_district_list));
            }
        });

        deviceID = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        deviceOS = Integer.toString(Build.VERSION.SDK_INT);
        deviceType = "mobile";


        Button register = (Button) findViewById(R.id.signup_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndCreateAnAccount(name_edittext.getText().toString().trim(), email_edittext.getText().toString().trim()
                        , phoneno_edittext.getText().toString().trim(), password_edittext.getText().toString().trim(), confirmpassword_edittext.getText().toString().trim());
            }
        });

        confirmpassword_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) RegisterActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(confirmpassword_edittext.getWindowToken(), 0);

                    validateAndCreateAnAccount(name_edittext.getText().toString().trim(), email_edittext.getText().toString().trim()
                            , phoneno_edittext.getText().toString().trim(), password_edittext.getText().toString().trim(), confirmpassword_edittext.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });


        password_edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextInputLayout til = (TextInputLayout) findViewById(R.id.textInputLayPwd);
                if (!hasFocus && !TextUtils.isEmpty(password_edittext.getText())) {

                    if (!AppUtils.isValidPassword(password_edittext.getText().toString(), configManager)) {
                        til.setError(AppUtils.getPasswordConstraintInformation(configManager, getApplicationContext()));
                        password_edittext.setError(null);
                        password_edittext.setText("");
                    } else {
                        til.setErrorEnabled(false);
                        til.setError("");
                    }
                }
            }
        });

        handler = new Handler();

        new GetAllCitiesTask().execute();

    }

    private void setOTPCode(Intent intent) {
        if (alertDialog != null && alertDialog.isShowing()) {
            EditText etOTP = (EditText) alertDialog.findViewById(R.id.etOTP);
            if (etOTP != null) {
                etOTP.setText(intent.getStringExtra(SMSListener.PARAM_OTP_CODE));
                etOTP.setSelection(etOTP.getText().length());
            }
            registerAccount(intent.getStringExtra(SMSListener.PARAM_OTP_CODE));
        }
    }

    private boolean isValidEmail(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void validateAndCreateAnAccount(final String name, final String email, final String phoneno,
                                            final String password, final String confirmPassword) {

        final City selectedCity=getCityByName(cityAutoCompleteTextView.getText().toString());

        if ((selectedCity == null && isMultiCity)) {
            showSnackBar(getString(R.string.please_select_your_district_municipality));
        } else if(TextUtils.isEmpty(name)){
            showSnackBar(getString(R.string.please_enter_your_name));
        } else if(TextUtils.isEmpty(phoneno)){
            showSnackBar(getString(R.string.please_enter_your_phone_no));
        } else if (phoneno.length() != 10) {
            showSnackBar(getString(R.string.phone_no_10_digits));
        } else if(!TextUtils.isEmpty(email) && !isValidEmail(email)){
            showSnackBar(getString(R.string.please_enter_valid_email));
        } else if(TextUtils.isEmpty(password)){
            showSnackBar(getString(R.string.please_enter_password));
        } else if (!AppUtils.isValidPassword(password, configManager)) {
            showSnackBar(AppUtils.getPasswordConstraintInformation(configManager, getApplicationContext()));
        } else if (!password.equals(confirmPassword)) {
            showSnackBar(getString(R.string.password_do_not_match));
        } else {

            if(isMultiCity) {
                sessionManager.setBaseURL(selectedCity.getUrl(), selectedCity.getCityName(),
                        selectedCity.getCityCode(), selectedCity.getModules()!=null?selectedCity.getModules().toString():null);
            }

            if (validateInternetConnection())
                sendOTPCode();
        }
    }

    public void resetAndRefreshDropdown()
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                cityAutoCompleteTextView.setOnClickListener(null);
                cityAutoCompleteTextView.setHint(getString(R.string.loading_failed));
                cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, R.drawable.ic_refresh_black_24dp, 0);
                cityAutoCompleteTextView.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                    @Override
                    public void onClick(DrawablePosition target) {
                        if (target == DrawablePosition.RIGHT) {
                            cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, 0, 0);
                            cityAutoCompleteTextView.setDrawableClickListener(null);
                            cityAutoCompleteTextView.setHint(getString(R.string.loading_label));
                            new GetAllCitiesTask().execute();
                        }
                    }
                });

                cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, R.drawable.ic_refresh_black_24dp, 0);
                cityAutoCompleteTextView.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                    @Override
                    public void onClick(DrawablePosition target) {
                        if (target == DrawablePosition.RIGHT) {
                            cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, 0, 0);
                            cityAutoCompleteTextView.setDrawableClickListener(null);
                            cityAutoCompleteTextView.setHint(getString(R.string.loading_label));
                            new GetAllCitiesTask().execute();
                        }
                    }
                });
            }
        });
    }

    public void loadDropdowns()
    {
        try {
            if (configManager.getString(API_MULTICITIES).equals("false")) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.multicityoptions).setVisibility(View.GONE);
                        municipalityInfo.setVisibility(View.GONE);
                        nameInputLayout.setBackgroundResource(R.drawable.top_edittext);
                        isMultiCity=false;
                    }
                });
            } else {
                loadDistrictDropdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
            resetAndRefreshDropdown();
        }
    }

    public void loadDistrictDropdown() throws IOException
    {
        districtsList = ApiController.getAllCitiesURLs(null, configManager.getString(API_MULTIPLE_CITIES_URL));

        if (districtsList != null) {

            final List<String> districts = new ArrayList<>();

            for (int i = 0; i < districtsList.size(); i++) {
                districts.add(districtsList.get(i).getDistrictName());
            }

            loadDropdownsWithData(districts, spinnerDistrict, districtAutoCompleteTextView, true);

        } else {
            resetAndRefreshDropdown();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadCityDropdown()
    {
        if(citiesList!=null){ citiesList.clear(); }

        cityAutoCompleteTextView.setHint(getString(R.string.loading_label));
        cityAutoCompleteTextView.setOnClickListener(null);
        cityAutoCompleteTextView.setAdapter(null);

        spinnerCity.setAdapter(null);

        String s = districtAutoCompleteTextView.getText().toString().toUpperCase();
        List<String> cities=new ArrayList<>();

        for (District district : districtsList) {
            if (s.equals(district.getDistrictName().toUpperCase())) {
                districtAutoCompleteTextView.setText(s.toUpperCase());
                cityAutoCompleteTextView.requestFocus();
                //noinspection unchecked
                citiesList= (List<City>) ((ArrayList<City>)district.getCities()).clone();
                for(City city: citiesList)
                {
                    cities.add(city.getCityName());
                }
                break;
            }
        }

        loadDropdownsWithData(cities, spinnerCity, cityAutoCompleteTextView, false);
    }

    public void loadDropdownsWithData(final List<String> autocompleteList, final Spinner autoCompleteSpinner, final CustomAutoCompleteTextView autocompleteTextBox, final Boolean isDistrict)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, autocompleteList);
                dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                autoCompleteSpinner.setAdapter(new NothingSelectedSpinnerAdapter(dropdownAdapter, android.R.layout.simple_spinner_dropdown_item, RegisterActivity.this));
                autoCompleteSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        if ((position - 1) > -1) {
                            if (isDistrict) {
                                autocompleteTextBox.setText(districtsList.get(position - 1).getDistrictName());
                                loadCityDropdown();
                            } else {
                                City selectedCity = citiesList.get(position - 1);
                                autocompleteTextBox.setText(selectedCity.getCityName());
                                name_edittext.requestFocus();
                            }
                        }
                        autocompleteTextBox.dismissDropDown();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_spinner_dropdown_item, autocompleteList);
                //String hintText = (autocompleteList.size() > 0 ? (isDistrict ? "District" : "Municipality") : (isDistrict ? "Districts Not Found!" : "Municipalities Not Found!"));

                autocompleteTextBox.setHint((isDistrict ? getString(R.string.district) : getString(R.string.municipality)));
                autocompleteTextBox.setCompoundDrawablesWithIntrinsicBounds((isDistrict ? R.drawable.ic_place_black_24dp : R.drawable.ic_location_city_black_24dp), 0, (autocompleteList.size() > 0 ? R.drawable.ic_keyboard_arrow_down_black_24dp : 0), 0);
                autocompleteTextBox.setOnClickListener(null);
                autocompleteTextBox.setAdapter(autoCompleteAdapter);
                autocompleteTextBox.setThreshold(1);
                autocompleteTextBox.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                    @Override
                    public void onClick(DrawablePosition target) {
                        if (target == DrawablePosition.RIGHT) {
                            autoCompleteSpinner.performClick();
                        }
                    }
                });

                autocompleteTextBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (isDistrict) {
                            if (citiesList != null) {
                                citiesList.clear();
                            }
                            cityAutoCompleteTextView.setText("");
                            cityAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, 0, 0);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                autocompleteTextBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(final View v, boolean hasFocus) {

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (!v.hasFocus()) {
                                    if (isDistrict && !TextUtils.isEmpty(districtAutoCompleteTextView.getText().toString())) {
                                        loadCityDropdown();
                                    }
                                }
                            }
                        });

                    }
                });

                autocompleteTextBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (isDistrict) {
                            loadCityDropdown();
                        }
                    }
                });

            }
        });
    }

    public City getCityByName(String cityName)
    {
        if(citiesList!=null) {
            for (City city : citiesList) {
                if (cityName.equals(city.getCityName())) {
                    return city;
                }
            }
        }
        return null;
    }

    public void showOTPVerificationDialog()
    {
        if(alertDialog!=null && alertDialog.isShowing())
        {
            alertDialog.dismiss();
        }

        dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_verify_otp, null);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setView(dialogView);
        final EditText etOTP=(EditText)dialogView.findViewById(R.id.etOTP);

        dialogBuilder.setPositiveButton(R.string.sing_up, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!TextUtils.isEmpty(etOTP.getText()))
                    registerAccount(etOTP.getText().toString());
                else
                    showSnackBar(getString(R.string.please_enter_otp_code));
            }
        });

        dialogBuilder.setNeutralButton(R.string.generate_otp, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendOTPCode();
            }
        });

        alertDialog=dialogBuilder.create();
        alertDialog.show();
    }

    public void sendOTPCode()
    {

        progressDialog.show();

        Call<JsonObject> sendOTP = ApiController.getRetrofit2API(getApplicationContext(), sessionManager.getBaseURL())
                .sendOTPToVerifyBeforeAccountCreate(phoneno_edittext.getText().toString());

        sendOTP.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                progressDialog.dismiss();
                showOTPVerificationDialog();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isRunning = true;
        LocalBroadcastManager.getInstance(RegisterActivity.this).registerReceiver(otpReceiver,
                new IntentFilter(SMSListener.OTP_LISTENER));
        isBroadcastRunning = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setOTPCode(getIntent());
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(RegisterActivity.this).unregisterReceiver(otpReceiver);
        isBroadcastRunning = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isRunning = false;
    }

    public void registerAccount(final String otpCode)
    {
        progressDialog.show();

        RegisterRequest registerRequest = new RegisterRequest(email_edittext.getText().toString(), phoneno_edittext.getText().toString(),
                name_edittext.getText().toString(), password_edittext.getText().toString(), deviceID, deviceType, deviceOS, otpCode);

        Call<JsonObject> createAccount = ApiController.getRetrofit2API(getApplicationContext(), sessionManager.getBaseURL())
                .registerUser(registerRequest);

        createAccount.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                progressDialog.dismiss();
                alertDialog.dismiss();
                citizenLogin(phoneno_edittext.getText().toString(), password_edittext.getText().toString());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                if (alertDialog.isShowing())
                    alertDialog.dismiss();
            }
        });

    }

    public void citizenLogin(final String username, final String password)
    {

        progressDialog.show();

        Call<JsonObject> login = ApiController.getRetrofit2API(getApplicationContext())
                .login(ApiUrl.AUTHORIZATION, username, "read write", password, "password");

        login.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {

                if (response.isSuccessful()) {

                    JsonObject jsonObject = response.body();
                    sessionManager.loginUser(username, password, AppUtils.getNullAsEmptyString(jsonObject.get("name")),
                            AppUtils.getNullAsEmptyString(jsonObject.get("mobileNumber")), AppUtils.getNullAsEmptyString(jsonObject.get("emailId")),
                            jsonObject.get("access_token").getAsString(), jsonObject.get("cityLat").getAsDouble(), jsonObject.get("cityLng").getAsDouble());

                    startService(new Intent(RegisterActivity.this, UpdateService.class)
                            .putExtra(UpdateService.KEY_METHOD, UpdateService.UPDATE_ALL));

                    Intent openHomeScreen = new Intent(RegisterActivity.this, HomeActivity.class);
                    openHomeScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(openHomeScreen);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                showSnackBar(t.getLocalizedMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        Slide enterTransition = new Slide();
        enterTransition.setDuration(getResources().getInteger(R.integer.anim_duration_long));
        enterTransition.setSlideEdge(Gravity.BOTTOM);
        getWindow().setEnterTransition(enterTransition);
    }

    @Override
    public void errorOccurred(String errorMsg, int errorCode) {
        //super.errorOccurred(errorMsg, errorCode);
        showAlertDialogWithMessage(getString(R.string.error), errorMsg);
    }

    class GetAllCitiesTask extends AsyncTask<String, Integer, Object> {

        @Override
        protected Object doInBackground(String... params) {
            loadDropdowns();
            return null;
        }

    }
}


