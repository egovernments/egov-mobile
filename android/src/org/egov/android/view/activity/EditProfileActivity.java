/**
 * eGov suite of products aim to improve the internal efficiency,transparency, accountability and the service delivery of the
 * government organizations.
 * 
 * Copyright (C) <2015> eGovernments Foundation
 * 
 * The updated version of eGov suite of products as by eGovernments Foundation is available at http://www.egovernments.org
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * http://www.gnu.org/licenses/ or http://www.gnu.org/licenses/gpl.html .
 * 
 * In addition to the terms of the GPL license to be adhered to in using this program, the following additional terms are to be
 * complied with:
 * 
 * 1) All versions of this program, verbatim or modified must carry this Legal Notice.
 * 
 * 2) Any misrepresentation of the origin of the material is prohibited. It is required that all modified versions of this
 * material be marked in reasonable ways as different from the original version.
 * 
 * 3) This license does not grant any rights to any user of the program with regards to rights under trademark law for use of the
 * trade names or trademarks of eGovernments Foundation.
 * 
 * In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.android.view.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.egov.android.R;
import org.egov.android.controller.ApiController;
import org.egov.android.AndroidLibrary;
import org.egov.android.api.ApiResponse;
import org.egov.android.common.StorageManager;
import org.egov.android.listener.Event;
import org.egov.android.model.User;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class EditProfileActivity extends BaseActivity {

    private Dialog dialog;
    private String profPath = "";
    private String mobileNo = "";
    private String mailId = "";
    private String userName = "";
    private String gender = "";
    private String altContactNumber = "";
    private String dateOfBirth = "";
    private String panCardNumber = "";
    private String aadhaarCardNumber = "";
    private String langauge = "";
    private static final int CAPTURE_IMAGE = 1000;
    private static final int FROM_GALLERY = 2000;
    private int apiLevel = 0;

    /**
     * To initialize and set the layout for the ProfileActivity.Set click listeners to the save changes, change
     * picture and calendar icon. Here we have checked the api level to set the layout. If api level is
     * greater than 13, then call activity_edit_profile layout else call activity_lower_version_edit_profile
     * layout. activity_edit_profile layout contains EGovRoundedImageView component which is not
     * supported in lower api levels.
     * get all the user profile field values from the intent that is passed from the Profile Activity and
     * displays those values to the corresponding UI fields of EditProfile Layout
     * StorageManager is the interface to the systems' storage service. 
     * The storage manager handles storage-related items.
     * profile picture is stored in /egovernments/profile directory on device storage area.
     * profile picture is displayed using setImageBitmap method.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiLevel = AndroidLibrary.getInstance().getSession().getInt("api_level", 0);
        if (apiLevel > 13) {
            setContentView(R.layout.activity_edit_profile);
        } else {
            setContentView(R.layout.activity_lower_version_edit_profile);
        }

        ((Button) findViewById(R.id.editprofile_doEditprofile)).setOnClickListener(this);
        ((Button) findViewById(R.id.changepicture)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.edit_profile_calendar)).setOnClickListener(this);

        mobileNo = getIntent().getExtras().getString("mobileNo");
        userName = getIntent().getExtras().getString("userName");
        mailId = getIntent().getExtras().getString("mailId");
        gender = getIntent().getExtras().getString("gender");
        altContactNumber = getIntent().getExtras().getString("altContactNumber");
        dateOfBirth = getIntent().getExtras().getString("dateOfBirth");
        panCardNumber = getIntent().getExtras().getString("panCardNumber");
        aadhaarCardNumber = getIntent().getExtras().getString("aadhaarCardNumber");
        langauge = getIntent().getExtras().getString("langauge");

        ((EditText) findViewById(R.id.edit_profile_name)).setText(userName);
        ((EditText) findViewById(R.id.edit_profile_alt_contact)).setText(altContactNumber);
        ((TextView) findViewById(R.id.edit_profile_dob)).setText(dateOfBirth);
        ((EditText) findViewById(R.id.edit_profile_pan)).setText(panCardNumber);
        ((EditText) findViewById(R.id.edit_profile_aadhaar)).setText(aadhaarCardNumber);

        int selected_lang = (langauge.equalsIgnoreCase("english")) ? R.id.english : (langauge
                .equalsIgnoreCase("hindi")) ? R.id.hindi : R.id.english;

        int selected_gender = (gender.equalsIgnoreCase("male")) ? R.id.male : (gender
                .equalsIgnoreCase("female")) ? R.id.female : R.id.male;

        ((RadioGroup) findViewById(R.id.gender)).check(selected_gender);
        ((RadioGroup) findViewById(R.id.language)).check(selected_lang);

        StorageManager sm = new StorageManager();
        Object[] obj = sm.getStorageInfo();
        profPath = obj[0].toString() + "/egovernments/profile";
        String path = profPath + "/photo_" + mobileNo + ".jpg";
        File imgFile = new File(path);
        if (imgFile.exists()) {
            ((ImageView) findViewById(R.id.profile_image)).setImageBitmap(_getBitmapImage(path));
        }
    }

    /**
     * Event triggered when clicking on the item having click listener. 
     * When clicking on edit icon, redirects to EditProfileActivity and pass the user informations through intent.
     * When clicking on change picture _openDialog() will be called.
     * open dialog method has two options, add photo from gallery and camera.
     * When clicking on from_gallery, _openGalleryImages() will be called.
     * When clicking on from_camera, _openCamera() will be called.
     * when clicking on dob calendar, _showDatePicker() will be called. 
     */
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.editprofile_doEditprofile:
                _editProfile();
                break;
            case R.id.changepicture:
                _openDialog();
                break;
            case R.id.from_gallery:
                _openGalleryImages();
                dialog.cancel();
                break;
            case R.id.from_camera:
                _openCamera();
                dialog.cancel();
                break;
            case R.id.edit_profile_calendar:
                _showDatePicker();
                break;
        }
    }

    /**
     * Function called when clicking on add photo. Used to show the options where to pick the image, i.e,
     * from gallery or camera
     */
    private void _openDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_upload_dialog);
        dialog.show();
        ((LinearLayout) dialog.findViewById(R.id.from_gallery)).setOnClickListener(this);
        ((LinearLayout) dialog.findViewById(R.id.from_camera)).setOnClickListener(this);
    }

    /**
     * Function called when choosing the camera option. Start the implicit intent ACTION_IMAGE_CAPTURE
     * for result.
     */
    private void _openCamera() {
        Intent mediaCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File imageFile = new File(profPath + "/photo_temp_user.jpg");
        mediaCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
        startActivityForResult(mediaCamera, CAPTURE_IMAGE);
    }

    /**
     * Function used to choose the user dob
     */
    @SuppressLint("NewApi")
    private void _showDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = 0;
        int month = 0;
        int date = 0;
        if (dateOfBirth.equals("")) {
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            date = c.get(Calendar.DAY_OF_MONTH);
        } else {
            String[] birth = dateOfBirth.split("-");
            year = Integer.valueOf(birth[0]);
            month = Integer.valueOf(birth[1]) - 1;
            date = Integer.valueOf(birth[2]);
        }
        DatePickerDialog datePicker = new DatePickerDialog(this, datepicker, year, month, date);
        if (apiLevel > 13) {
            datePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        }
        datePicker.show();
    }

    /**
     * Function called when choosing the gallery option. Start the implicit intent ACTION_PICK for
     * result.
     */
    private void _openGalleryImages() {
        Intent photo_picker = new Intent(Intent.ACTION_PICK);
        photo_picker.setType("image/*");
        startActivityForResult(photo_picker, FROM_GALLERY);
    }

    /**
     * Event triggered when an action completed in another activity(request from this
     * activity)).
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE && resultCode == RESULT_OK) {
            _validateImageUrl();
        } else if (requestCode == FROM_GALLERY && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null,
                    null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            try {
                InputStream in = new FileInputStream(new File(imagePath));
                OutputStream out = new FileOutputStream(new File(profPath + "/photo_temp_user.jpg"));
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            _validateImageUrl();
        }
    }

    /**
     * Function used to validate whether the device has space to save the image or not.
     */
    private void _validateImageUrl() {

        StorageManager sm = new StorageManager();
        Object[] obj = sm.getStorageInfo();
        long totalSize = (Long) obj[2];

        String tempPath = profPath + "/photo_temp_user.jpg";
        File tempFile = new File(tempPath);

        if (totalSize < tempFile.length()) {
            showMessage(getMessage(R.string.sdcard_space_not_sufficient));
            tempFile.delete();
            return;
        }

        ((ImageView) findViewById(R.id.profile_image)).setImageBitmap(_getBitmapImage(tempPath));
    }

    /**
     * Function used to decode the file(for memory consumption) and return the bitmap to show it in
     * image view
     * 
     * @param path
     *            => image file path
     * @return bitmap
     */
    private Bitmap _getBitmapImage(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * Function called when click on edit profile. Check the empty fields validation and call edit
     * profile api.
     */
    @SuppressLint("DefaultLocale")
    private void _editProfile() {

        String name = ((EditText) findViewById(R.id.edit_profile_name)).getText().toString().trim();
        String alt_conatct_no = ((EditText) findViewById(R.id.edit_profile_alt_contact)).getText()
                .toString().trim();
        String date_of_birth = ((TextView) findViewById(R.id.edit_profile_dob)).getText()
                .toString();
        String panNo = ((EditText) findViewById(R.id.edit_profile_pan)).getText().toString().trim();
        String aadhaarNo = ((EditText) findViewById(R.id.edit_profile_aadhaar)).getText()
                .toString().trim();

        RadioGroup gender = (RadioGroup) findViewById(R.id.gender);
        int genderSelected = gender.getCheckedRadioButtonId();
        RadioButton genderButton = (RadioButton) findViewById(genderSelected);

        RadioGroup language = (RadioGroup) findViewById(R.id.language);
        int languageSelected = language.getCheckedRadioButtonId();
        RadioButton languageButton = (RadioButton) findViewById(languageSelected);

        if (isEmpty(name)) {
            showMessage(getMessage(R.string.name_empty));
            return;
        } else if (name.length() < 3) {
            showMessage(getMessage(R.string.name_length));
            return;
        } else if (!isEmpty(alt_conatct_no) && alt_conatct_no.length() < 10) {
            showMessage(getMessage(R.string.phone_number_length));
            return;
        } else if (isEmpty(date_of_birth)) {
            showMessage(getMessage(R.string.birth_empty));
            return;
        } else if (!isEmpty(panNo) && panNo.length() > 10) {
            showMessage(getMessage(R.string.pan_card_length));
            return;
        } else if (!isEmpty(aadhaarNo) && aadhaarNo.length() > 20) {
            showMessage(getMessage(R.string.aadhaar_length));
            return;
        }

        User user = new User();
        user.setName(name);
        user.setGender(genderButton.getText().toString().toUpperCase());
        user.setAltContactNumber(alt_conatct_no);
        user.setDateOfBirth(date_of_birth);
        user.setPanCardNumber(panNo);
        user.setAadhaarCardNumber(aadhaarNo);
        user.setLanguage(languageButton.getText().toString());
        user.setEmail(mailId);
        user.setMobileNo(mobileNo);

        ApiController.getInstance().updateProfile(this, user);
    }

    /**
     * Event triggered when setting the date in date picker and format the date like 'yyyy-MM-dd' using
     * SimpleDateFormat.
     */
    private DatePickerDialog.OnDateSetListener datepicker = new DatePickerDialog.OnDateSetListener() {
        @SuppressLint("SimpleDateFormat")
        @Override
        public void onDateSet(DatePicker view, int year, int month, int date) {

            try {
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
                Date d = dateFormatter.parse(String.valueOf(year + "-" + (month + 1) + "-" + date));
                String formatedDate = dateFormatter.format(d);
                ((TextView) findViewById(R.id.edit_profile_dob)).setText(formatedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * The onResponse method will be invoked after the API call 
     * onResponse methods will contain the response
     * If the response has a status as 'success' then
     * we have checked whether the access token is valid or not
     * If the access token is invalid, redirect to login page.
     * If the access token is valid then stores the profile picture to the device profile picture directory
     * then call finish() method.
     */
    
    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();

        if (status.equalsIgnoreCase("success")) {
            showMessage(msg);
            File tempFile = new File(profPath + "/photo_temp_user.jpg");
            String path = profPath + "/photo_" + mobileNo + ".jpg";
            File profFile = new File(path);
            tempFile.renameTo(profFile);
            ((ImageView) findViewById(R.id.profile_image)).setImageBitmap(_getBitmapImage(path));
            finish();
        } else {
            if (msg.matches(".*Invalid access token.*")) {
                showMessage("Session expired");
                startLoginActivity();
            } else {
                showMessage(msg);
            }
        }
    }
}
