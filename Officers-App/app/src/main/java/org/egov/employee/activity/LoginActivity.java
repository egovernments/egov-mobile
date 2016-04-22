package org.egov.employee.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.egov.employee.api.ApiUrl;
import org.egov.employee.api.ApiController;
import org.egov.employee.application.EgovApp;
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
    Spinner spinnerCity;
    JsonArray citiesArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        editTextUsernmae=(EditText) findViewById(R.id.edusername);
        editTextPwd=(EditText) findViewById(R.id.edpwd);

        fablogin = (FloatingActionButton) findViewById(R.id.fablogin);
        pblogin = (ProgressBar)findViewById(R.id.pblogin);
        pblogin.setVisibility(View.INVISIBLE);
        spinnerCity=(Spinner)findViewById(R.id.spinnercity);

        if(EgovApp.getInstance().isMultiCitySupport())
        {
            List<String> cities = new ArrayList<>();
            cities.add("Select Your City");
            citiesArray=new JsonParser().parse(preference.getCitiesList()).getAsJsonArray();
            int activeCityCode=preference.getActiveCityCode();
            int loopIdx=0, activeCityPosition=0;
            for(JsonElement jsonObj:citiesArray) {
                //add city to list for load dropdown
                JsonObject jsonCityObj = jsonObj.getAsJsonObject();
                cities.add(jsonCityObj.get("city_name").getAsString());
                //check if app preference having any active city code or not
                if(activeCityCode>-1 && activeCityCode==jsonCityObj.get("city_code").getAsInt())
                {
                    activeCityPosition=loopIdx;
                }
                loopIdx++;
            }

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_item_large, cities);
            dataAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_large);
            spinnerCity.setAdapter(dataAdapter);
            spinnerCity.setVisibility(View.VISIBLE);
            spinnerCity.setSelection((activeCityPosition+1));
        }
        else {
            spinnerCity.setVisibility(View.GONE);
        }

        setFieldValuesFromPreference();

        if(getIntent().getBooleanExtra("isFromSessionTimeOut", false))
        {
            showErrorMessage("Session Timeout!");
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

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pblogin.setVisibility(View.INVISIBLE);
                                        startActivity(new Intent(LoginActivity.this, Homepage.class));
                                        finish();
                                    }
                                }, 1000);
                            }
                            else
                            {
                                showErrorMessage("You're not a employee!");
                                showLoginControls();
                            }

                        } else {
                            showErrorMessage("Invalid response from server!");
                            showLoginControls();
                        }
                    }
                    catch (Exception ex)
                    {
                        showErrorMessage("Invalid response from server!");
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
            if(spinnerCity.getSelectedItemPosition() == 0)
            {
                showErrorMessage("Please select your city!");
                return false;
            }
            else
            {
                //get current city json object based on cities dropdown selection
                JsonObject currentCityObj = citiesArray.get((spinnerCity.getSelectedItemPosition()-1)).getAsJsonObject();
                preference.setActiveCityUrl(currentCityObj.get("url").getAsString());
                preference.setActiveCityName(currentCityObj.get("city_name").getAsString());
                preference.setActiveCityCode(currentCityObj.get("city_code").getAsInt());
            }
        }

        if(TextUtils.isEmpty(editTextUsernmae.getText().toString()))
        {
            showErrorMessage("Please enter your username!");
            return false;
        }

        if(TextUtils.isEmpty(editTextPwd.getText().toString()))
        {
            showErrorMessage("Please enter your password!");
            return false;
        }

        return true;
    }

}
