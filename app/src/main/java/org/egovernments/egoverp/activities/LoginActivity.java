/*
 *    eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (c) 2016  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egovernments.egoverp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.egovernments.egoverp.R;
import org.egovernments.egoverp.helper.AppUtils;
import org.egovernments.egoverp.helper.ConfigManager;
import org.egovernments.egoverp.helper.CustomAutoCompleteTextView;
import org.egovernments.egoverp.helper.NothingSelectedSpinnerAdapter;
import org.egovernments.egoverp.models.City;
import org.egovernments.egoverp.models.District;
import org.egovernments.egoverp.network.ApiController;
import org.egovernments.egoverp.network.ApiUrl;
import org.egovernments.egoverp.network.SessionManager;
import org.egovernments.egoverp.network.UpdateService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * The login screen activity
 **/

@SuppressWarnings("ALL")
public class LoginActivity extends Activity {

    private String username;
    private String password;
   /* private String url;
    private String cityName;*/

    private ProgressBar progressBar;

    private FloatingActionButton loginButton;
    private com.melnykov.fab.FloatingActionButton loginButtonCompat;
    private TextView forgotLabel;
    private Button signupButton;

    private EditText username_edittext;
    private EditText password_edittext;

    private Handler handler;

    private SessionManager sessionManager;

    private ConfigManager configManager;

    private CustomAutoCompleteTextView cityAutocompleteTextBox;
    private CustomAutoCompleteTextView districtAutocompleteTextBox;

    private Spinner spinnerCity;
    private Spinner spinnerDistrict;

    List<District> districtsList;
    List<City> citiesList;


/*
    private int code;
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(getApplicationContext());

        //Checks if session manager believes that the user is logged in.
        if (sessionManager.isLoggedIn()) {
            //If user is logged in and has a stored access token, immediately login user
            if (sessionManager.getAccessToken() != (null)) {
                //Start fetching data from server
                startService(new Intent(LoginActivity.this, UpdateService.class).putExtra(UpdateService.KEY_METHOD, UpdateService.UPDATE_PROFILE));
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            } else {
                showToastMsg("Session expired!");
            }
        }

        setContentView(R.layout.activity_login);

        /*url = sessionManager.getBaseURL();
        cityName = sessionManager.getUrlLocation();
        code = sessionManager.getUrlLocationCode();*/

        spinnerCity = (Spinner) findViewById(R.id.signin_city);
        spinnerDistrict = (Spinner) findViewById(R.id.spinner_district);

        loginButton = (FloatingActionButton) findViewById(R.id.signin_submit);
        loginButtonCompat = (com.melnykov.fab.FloatingActionButton) findViewById(R.id.signin_submit_compat);

        forgotLabel = (TextView) findViewById(R.id.signin_forgot);
        signupButton = (Button) findViewById(R.id.signin_register);

        progressBar = (ProgressBar) findViewById(R.id.loginprogressBar);

        username_edittext = (EditText) findViewById(R.id.signin_username);
        password_edittext = (EditText) findViewById(R.id.signin_password);

        cityAutocompleteTextBox = (CustomAutoCompleteTextView) findViewById(R.id.login_spinner_autocomplete);
        districtAutocompleteTextBox = (CustomAutoCompleteTextView) findViewById(R.id.autocomplete_district);

        cityAutocompleteTextBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToastMsg("Fetching municipality list, please wait");
            }
        });

        districtAutocompleteTextBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToastMsg("Fetching district list, please wait");
            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                username = username_edittext.getText().toString().trim();
                password = password_edittext.getText().toString().trim();

                submit(username, password);
            }
        };

        ImageView imgLogo=(ImageView)findViewById(R.id.applogoBig);
        imgLogo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                sessionManager.setDemoMode(!sessionManager.isDemoMode());
                showToastMsg("Demo Mode is "+(sessionManager.isDemoMode()?"Enabled":"Disabled"));

                return false;

            }
        });

//        To make fab compatible in older android versions
        if (Build.VERSION.SDK_INT >= 21) {
            loginButton.setOnClickListener(onClickListener);
        } else {
            loginButton.setVisibility(View.GONE);
            loginButtonCompat.setVisibility(View.VISIBLE);
            loginButtonCompat.setOnClickListener(onClickListener);
        }

        forgotLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        password_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    username = username_edittext.getText().toString().trim();
                    password = password_edittext.getText().toString().trim();
                    submit(username, password);
                    if(imm != null){
                        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                    }
                    return true;
                }
                return false;
            }
        });

        handler = new Handler();

        try {
            configManager = AppUtils.getConfigManager(getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new GetAllCitiesTask().execute();
    }

    private void showToastMsg(String msg)
    {
        Toast toast = Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    private void showToastMsg(Integer msg)
    {
        Toast toast = Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    public void showLoginProgress()
    {
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.GONE);
        loginButtonCompat.setVisibility(View.GONE);
        forgotLabel.setVisibility(View.INVISIBLE);
        signupButton.setVisibility(View.INVISIBLE);
    }

    public void hideLoginProgress()
    {
        progressBar.setVisibility(View.GONE);
        forgotLabel.setVisibility(View.VISIBLE);
        signupButton.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 21) {
            loginButton.setVisibility(View.VISIBLE);
        } else
            loginButtonCompat.setVisibility(View.VISIBLE);
    }

    //Invokes call to API
    private void submit(final String username, final String password) {

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                showToastMsg(R.string.login_field_empty_prompt);
                progressBar.setVisibility(View.GONE);
                forgotLabel.setVisibility(View.VISIBLE);
                signupButton.setVisibility(View.VISIBLE);

                if (Build.VERSION.SDK_INT >= 21) {
                    loginButton.setVisibility(View.VISIBLE);
                } else
                    loginButtonCompat.setVisibility(View.VISIBLE);

            } else {

                City selectedCity = getCityByName(cityAutocompleteTextBox.getText().toString());

                if (selectedCity == null && configManager.getString("api.multicities").equals("true")) {

                    String errorMsg = (TextUtils.isEmpty(cityAutocompleteTextBox.getText().toString()) ? "Please select your district and municipality!" : "Selected municipality is not found!");
                    showToastMsg(errorMsg);
                    CustomAutoCompleteTextView controlToFocus = (TextUtils.isEmpty(districtAutocompleteTextBox.getText().toString()) ? districtAutocompleteTextBox : cityAutocompleteTextBox);
                    controlToFocus.requestFocus();
                    return;
                }

                if (configManager.getString("api.multicities").equals("true"))
                    sessionManager.setBaseURL(selectedCity.getUrl(), selectedCity.getCityName(), selectedCity.getCityCode());

                showLoginProgress();

                ApiController.resetAndGetAPI(LoginActivity.this).login(ApiUrl.AUTHORIZATION, username, "read write", password, "password", new Callback<JsonObject>() {
                    @Override
                    public void success(JsonObject jsonObject, Response response) {

                        //Stores access token in session manager
                        sessionManager.loginUser(password, username, jsonObject.get("access_token").getAsString(), jsonObject.get("cityLat").getAsDouble(), jsonObject.get("cityLng").getAsDouble());
                        startService(new Intent(LoginActivity.this, UpdateService.class).putExtra(UpdateService.KEY_METHOD, UpdateService.UPDATE_ALL));
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        JsonObject jsonObject = null;
                        if (error != null) {
                            if (error.getLocalizedMessage() != null && !error.getLocalizedMessage().contains("400")) {
                                showToastMsg(error.getLocalizedMessage());
                            } else {
                                try {
                                    jsonObject = (JsonObject) error.getBody();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (jsonObject != null) {
                                    String errorDescription = jsonObject.get("error_description").getAsString().trim();
                                    //If user has attempted to log into a yet to be activated account,
                                    // automatically redirect the user to account activation screen
                                    if (errorDescription.contains("Please activate your account!")) {
                                        Intent intent = new Intent(LoginActivity.this, AccountActivationActivity.class);
                                        intent.putExtra("username", username);
                                        intent.putExtra("password", password);
                                        startActivity(intent);
                                    } else {
                                        showToastMsg(errorDescription);
                                    }
                                } else {
                                    showToastMsg("An unexpected error occurred while accessing the network!");
                                }
                            }
                        }

                        hideLoginProgress();

                    }
                });


            }

    }

    class GetAllCitiesTask extends AsyncTask<String, Integer, Object> {
        @Override
        protected Object doInBackground(String... params) {
            loadDropdowns();
            return null;
        }
    }

    public void hideMultiCityComponents()
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                username_edittext.setBackgroundResource(R.drawable.top_edittext);
                spinnerDistrict.setVisibility(View.GONE);
                districtAutocompleteTextBox.setVisibility(View.GONE);
                spinnerCity.setVisibility(View.GONE);
                cityAutocompleteTextBox.setVisibility(View.GONE);
            }
        });
    }

    public void loadDistrictDropdown() throws IOException
    {
        districtsList = ApiController.getAllCitiesURLs(configManager.getString("api.multipleCitiesUrl"));

        if (districtsList != null) {

            final List<String> districts = new ArrayList<>();

            for (int i = 0; i < districtsList.size(); i++) {
                districts.add(districtsList.get(i).getDistrictName());
            }

            loadDropdownsWithData(districts, spinnerDistrict, districtAutocompleteTextBox, true);

        } else {
            resetAndRefreshDropdownValues();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadCityDropdown()
    {
        if(districtsList==null){
            return;
        }
        if(citiesList!=null){ citiesList.clear(); }

        cityAutocompleteTextBox.setHint("Loading");
        cityAutocompleteTextBox.setOnClickListener(null);
        cityAutocompleteTextBox.setAdapter(null);

        spinnerCity.setAdapter(null);

        String s = districtAutocompleteTextBox.getText().toString().toUpperCase();
        List<String> cities=new ArrayList<>();

        for (District district : districtsList) {
            if (s.equals(district.getDistrictName().toUpperCase())) {
                districtAutocompleteTextBox.setText(s.toUpperCase());
                cityAutocompleteTextBox.requestFocus();
                //noinspection unchecked
                @SuppressWarnings("unchecked") ArrayList<City> citiesArrayList=(ArrayList)district.getCities();
                citiesList= (ArrayList)citiesArrayList.clone();
                for(City city: citiesList)
                {
                    cities.add(city.getCityName());
                }
                break;
            }
        }

        loadDropdownsWithData(cities, spinnerCity, cityAutocompleteTextBox, false);
    }

    public void loadDropdownsWithData(final List<String> autocompleteList, final Spinner autoCompleteSpinner, final CustomAutoCompleteTextView autocompleteTextBox, final Boolean isDistrict)
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ArrayAdapter<String> dropdownAdapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_spinner_dropdown_item, autocompleteList);
                dropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                autoCompleteSpinner.setAdapter(new NothingSelectedSpinnerAdapter(dropdownAdapter, android.R.layout.simple_spinner_dropdown_item, LoginActivity.this));
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
                            }
                        }
                        autocompleteTextBox.dismissDropDown();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_spinner_dropdown_item, autocompleteList);
                //String hintText = (autocompleteList.size() > 0 ? (isDistrict ? "District" : "Municipality") : (isDistrict ? "Districts Not Found!" : "Municipalities Not Found!"));

                autocompleteTextBox.setHint((isDistrict ? "District" : "Municipality"));
                autocompleteTextBox.setCompoundDrawablesWithIntrinsicBounds((isDistrict ? R.drawable.ic_place_black_24dp : R.drawable.ic_location_city_black_24dp), 0, (autocompleteList.size() > 0 ? R.drawable.ic_keyboard_arrow_down_black_24dp : 0), 0);
                autocompleteTextBox.setOnClickListener(null);
                autocompleteTextBox.setAdapter(autoCompleteAdapter);
                autocompleteTextBox.setThreshold(1);
                autocompleteTextBox.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                    @Override
                    public void onClick(DrawablePosition target) {
                        if (target == DrawablePosition.RIGHT) {
                            if (!isDistrict && !cityAutocompleteTextBox.hasFocus()) {
                                return;
                            }
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
                            cityAutocompleteTextBox.setText("");
                            cityAutocompleteTextBox.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, 0, 0);
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
                                    if (isDistrict && !TextUtils.isEmpty(districtAutocompleteTextBox.getText().toString())) {
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

    public void resetAndRefreshDropdownValues()
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                districtAutocompleteTextBox.setOnClickListener(null);
                districtAutocompleteTextBox.setHint("Loading failed");
                districtAutocompleteTextBox.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_place_black_24dp, 0, R.drawable.ic_refresh_black_24dp, 0);
                districtAutocompleteTextBox.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                    @Override
                    public void onClick(DrawablePosition target) {
                        if (target == DrawablePosition.RIGHT) {
                            districtAutocompleteTextBox.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_place_black_24dp, 0, 0, 0);
                            districtAutocompleteTextBox.setDrawableClickListener(null);
                            districtAutocompleteTextBox.setHint(getString(R.string.loading_label));
                            new GetAllCitiesTask().execute();
                        }
                    }
                });

                cityAutocompleteTextBox.setOnClickListener(null);
                cityAutocompleteTextBox.setHint("Loading failed");
                cityAutocompleteTextBox.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location_city_black_24dp, 0, R.drawable.ic_refresh_black_24dp, 0);
                cityAutocompleteTextBox.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
                    @Override
                    public void onClick(DrawablePosition target) {
                        if (target == DrawablePosition.RIGHT) {
                            loadCityDropdown();
                        }
                    }
                });
            }
        });
    }

    public void loadDropdowns()
    {
        try {

            if (configManager.getString("api.multicities").equals("false")) {
                hideMultiCityComponents();
            } else {
                loadDistrictDropdown();
            }
        } catch (IOException e) {
            e.printStackTrace();
            resetAndRefreshDropdownValues();
        }
    }

}


