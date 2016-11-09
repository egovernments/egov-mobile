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

package org.egov.employee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.egov.employee.api.ApiController;
import org.egov.employee.api.ApiUrl;
import org.egov.employee.application.EgovApp;
import org.egov.employee.controls.CustomAutoCompleteTextView;
import org.egov.employee.data.MultiDistrictsAPIResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import offices.org.egov.egovemployees.R;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class LoginActivity extends BaseActivity {

    FloatingActionButton fablogin;
    ProgressBar pblogin;
    EditText editTextUsernmae;
    EditText editTextPwd;
    CustomAutoCompleteTextView autocompleteDistrict;
    CustomAutoCompleteTextView autocompleteCity;
    boolean isDistrictSelectedFromList;

    ArrayList<MultiDistrictsAPIResponse> districts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editTextUsernmae=(EditText) findViewById(R.id.edusername);
        editTextPwd=(EditText) findViewById(R.id.edpwd);

        fablogin = (FloatingActionButton) findViewById(R.id.fablogin);
        pblogin = (ProgressBar)findViewById(R.id.pblogin);
        pblogin.setVisibility(View.INVISIBLE);

        autocompleteDistrict=(CustomAutoCompleteTextView)findViewById(R.id.autocomplete_district);
        autocompleteCity=(CustomAutoCompleteTextView)findViewById(R.id.autocomplete_city);

        if(EgovApp.getInstance().isMultiCitySupport())
        {
            List<String> options = new ArrayList<>();

            Type listOfTestObject = new TypeToken<List<MultiDistrictsAPIResponse>>(){}.getType();
            districts=new Gson().fromJson(preference.getCitiesList(), listOfTestObject);

            setAdapterForAutoCompleteTextView(districts, autocompleteDistrict);

            isDistrictSelectedFromList =false;

            autocompleteDistrict.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    isDistrictSelectedFromList=true;
                    String selection = (String) parent.getItemAtPosition(position);
                    setAdapterForAutoCompleteTextView(getCitiesByDistrictName(selection), autocompleteCity);
                }
            });

            autocompleteDistrict.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus && !isDistrictSelectedFromList)
                    {
                        List<MultiDistrictsAPIResponse.City> cities=getCitiesByDistrictName(autocompleteDistrict.getText().toString().trim());
                        if(cities!=null)
                        setAdapterForAutoCompleteTextView(cities, autocompleteCity);
                    }
                }
            });

            autocompleteDistrict.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    isDistrictSelectedFromList =false;
                    autocompleteCity.setText("");
                    autocompleteCity.setAdapter(null);
                }
            });


        }
        else {
            autocompleteDistrict.setVisibility(View.GONE);
            autocompleteCity.setVisibility(View.GONE);
        }

        setFieldValuesFromPreference();

        if(getIntent().getBooleanExtra("isFromSessionTimeOut", false))
        {
            showSnackBar("Session Timeout!");
        }

        fablogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInputFields()) {
                    performLoginAction();
                }
            }
        });
    }

    public void setAdapterForAutoCompleteTextView(List<?> items, final CustomAutoCompleteTextView autoCompleteTextView)
    {
        List<String> options = new ArrayList<>();

        for(Object object:items)
        {
            if(object instanceof MultiDistrictsAPIResponse)
            {
                options.add(((MultiDistrictsAPIResponse) object).getDistrictName());
            }
            else if(object instanceof MultiDistrictsAPIResponse.City){
                options.add(((MultiDistrictsAPIResponse.City) object).getCityName());
            }
        }

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,options);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setDrawableClickListener(new CustomAutoCompleteTextView.DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                if (target == DrawablePosition.RIGHT) {
                    autoCompleteTextView.showDropDown();
                }
            }
        });

        autoCompleteTextView.setText("");

    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_login;
    }

    public void setFieldValuesFromPreference()
    {
        if(!TextUtils.isEmpty(preference.getUserName()))
        {
            editTextUsernmae.setText(preference.getUserName());
            editTextPwd.setText(preference.getPwd());
        }

        if(!TextUtils.isEmpty(preference.getDistrict()) && !TextUtils.isEmpty(preference.getActiveCityName()))
        {
            autocompleteDistrict.setText(preference.getDistrict());
            autocompleteCity.setText(preference.getActiveCityName());
            autocompleteDistrict.dismissDropDown();
            editTextPwd.requestFocus();
        }

    }

    //perform login action
    public void performLoginAction()
    {

        //get current method name for retry functionality in internet connection method
        String currentMethodName=Thread.currentThread().getStackTrace()[2].getMethodName();

        //check internet connection is available or not with retry function
        if(checkInternetConnectivity(LoginActivity.this, currentMethodName)) {

            final String username = editTextUsernmae.getText().toString().trim();
            final String pwd = editTextPwd.getText().toString();

            hideLoginControls();

            preference.setDistrict(autocompleteDistrict.getText().toString());
            preference.setActiveCityName(autocompleteCity.getText().toString());
            preference.setUserName("");
            preference.setPwd("");

            Call<JsonObject> jsonLogin = ApiController.getAPI(getApplicationContext(), LoginActivity.this).login(ApiUrl.AUTHORIZATION,
                    username, "read write", pwd, "password");

            final Callback<JsonObject> login = new Callback<JsonObject>() {
                @Override
                public void onResponse(Response<JsonObject> response, Retrofit retrofit) {

                    JsonObject respJson=response.body();
                    try {
                        if (respJson.has("access_token")) {

                            if(respJson.get("userType").getAsString().equals(ApiUrl.USER_TYPE_EMPLOYEE)) {

                                preference.setApiAccessToken(respJson.get("access_token").getAsString());
                                preference.setUserName(username);
                                preference.setPwd(pwd);
                                preference.setName(respJson.get("name").getAsString());
                                preference.setDistrict(autocompleteDistrict.getText().toString());
                                preference.setActiveCityName(autocompleteCity.getText().toString());
                                preference.setActiveCityLat(respJson.get("cityLat").getAsDouble());
                                preference.setActiveCityLng(respJson.get("cityLng").getAsDouble());

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pblogin.setVisibility(View.INVISIBLE);
                                        startActivity(new Intent(LoginActivity.this, Homepage.class));
                                        finish();
                                    }
                                }, 1000);

                                recordEmployeeLog();
                           }
                           else
                           {
                                showSnackBar("You're not a employee!");
                                showLoginControls();
                           }

                        } else {
                            showSnackBar("Invalid response from server!");
                            showLoginControls();
                        }
                    }
                    catch (Exception ex)
                    {
                        showSnackBar("Invalid response from server!");
                        showLoginControls();
                    }

                }

                @Override
                public void onFailure(Throwable t) {
                    showLoginControls();
                }
            };

            jsonLogin.enqueue(login);
        }
    }

    public void showLoginControls()
    {
        fablogin.setVisibility(View.VISIBLE);
        pblogin.setVisibility(View.INVISIBLE);
    }

    public void hideLoginControls()
    {
        fablogin.setVisibility(View.INVISIBLE);
        pblogin.setVisibility(View.VISIBLE);
    }

    public boolean validateInputFields()
    {
        //validate city dropdown if app has multicity support
        if(EgovApp.getInstance().isMultiCitySupport())
        {

            if(TextUtils.isEmpty(autocompleteDistrict.getText().toString()) || TextUtils.isEmpty(autocompleteCity.getText().toString()))
            {
                showSnackBar("Please enter your district and municipality!");
                return false;
            }

            MultiDistrictsAPIResponse.City city=getCityByCityName(autocompleteCity.getText().toString().trim());
            if(city==null)
            {
                showSnackBar("Please enter valid district and municipality!");
                return false;
            }
            preference.setActiveCityUrl(city.getUrl());
            preference.setActiveCityName(city.getCityName());
            preference.setActiveCityCode(city.getCityCode());
        }

        if(TextUtils.isEmpty(editTextUsernmae.getText().toString()))
        {
            showSnackBar("Please enter your username!");
            return false;
        }

        if(TextUtils.isEmpty(editTextPwd.getText().toString()))
        {
            showSnackBar("Please enter your password!");
            return false;
        }

        return true;
    }

    public List<MultiDistrictsAPIResponse.City> getCitiesByDistrictName(String districtName)
    {
        for(MultiDistrictsAPIResponse district:districts)
        {
            if(district.getDistrictName().toUpperCase().equals(districtName.trim().toUpperCase()))
            {
                if(!isDistrictSelectedFromList)
                {
                    autocompleteDistrict.setText(district.getDistrictName());
                }
                return district.getCities();
            }
        }
        return null;
    }

    public MultiDistrictsAPIResponse.City getCityByCityName(String cityName)
    {
        for(MultiDistrictsAPIResponse district:districts)
        {
            for(MultiDistrictsAPIResponse.City city:district.getCities())
            {
                if(city.getCityName().toUpperCase().equals(cityName.trim().toUpperCase()))
                {
                    return city;
                }
            }
        }
        return null;
    }

}
