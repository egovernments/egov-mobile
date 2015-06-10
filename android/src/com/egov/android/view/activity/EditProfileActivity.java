package com.egov.android.view.activity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.egov.android.R;
import com.egov.android.controller.ApiController;
import com.egov.android.library.api.ApiResponse;
import com.egov.android.library.http.IHttpClientListener;
import com.egov.android.library.http.Uploader;
import com.egov.android.library.listener.Event;
import com.egov.android.view.component.EGovRoundedImageView;

public class EditProfileActivity extends BaseActivity implements IHttpClientListener {

    private Dialog dialog;
    private boolean toastShown = false;
    private static final int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ((Button) findViewById(R.id.editprofile_doEditprofile)).setOnClickListener(this);
        ((Button) findViewById(R.id.changepicture)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.edit_profile_calendar)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.editprofile_doEditprofile:
                // editProfile();
                break;
            case R.id.changepicture:
                openDialog();
                break;
            case R.id.from_gallery:
                uploadImageFromSDCard();
                dialog.cancel();
                break;
            case R.id.from_camera:
                captureImage();
                dialog.cancel();
                break;
            case R.id.edit_profile_calendar:
                showDatePicker();
                break;
        }
    }

    private void openDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_upload_dialog);
        dialog.show();

        ((LinearLayout) dialog.findViewById(R.id.from_gallery)).setOnClickListener(this);
        ((LinearLayout) dialog.findViewById(R.id.from_camera)).setOnClickListener(this);
    }

    private void uploadImageFromSDCard() {
        Intent photo_picker = new Intent(Intent.ACTION_PICK);
        photo_picker.setType("image/*,video/*");
        startActivityForResult(photo_picker, RESULT_LOAD_IMAGE);
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DAY_OF_MONTH);
        new DatePickerDialog(this, datepicker, year, month, date).show();
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null,
                    null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();

            if (!checkFileExtension(imagePath)) {
                showMsg(_setMessage(R.string.file_type));
                return;
            }
            File file = new File(imagePath);
            long bytes = file.length();
            long fileSize = getConfig().getInt("upload.file.size") * 1024 * 1024;

            if (bytes > Long.valueOf(fileSize)) {
                showMsg(_setMessage(R.string.file_size));
            } else {
                _setImageResource(imagePath);
            }
        }
    }

    private boolean checkFileExtension(String filePath) {
        String fileType = (String) getConfig().get("upload.file.type", "");
        Pattern fileExtnPtrn = Pattern.compile("([^\\s]+(\\.(?i)(" + fileType + "))$)");
        Matcher mtch = fileExtnPtrn.matcher(filePath);
        if (mtch.matches()) {
            return true;
        }
        return false;
    }

    private void _setImageResource(String imagePath) {

        EGovRoundedImageView image = (EGovRoundedImageView) findViewById(R.id.profile_image);

        image.setImageBitmap(BitmapFactory.decodeFile(imagePath));

        Uploader upload = new Uploader();
        upload.setUrl("http://192.168.1.91/charles/egovernance/upload.php");
        upload.setInputFile(imagePath);
        upload.setListener(this);
        upload.upload();
    }

    private void editProfile() {

        _clearStatus(new int[] { R.id.edit_profile_name_status, R.id.edit_profile_mobile_status,
                R.id.edit_profile_email_status, R.id.edit_profile_alt_contact_status,
                R.id.edit_profile_dob_status });

        EditText name = (EditText) findViewById(R.id.edit_profile_name);
        EditText phone = (EditText) findViewById(R.id.edit_profile_mobile);
        EditText email = (EditText) findViewById(R.id.edit_profile_email);
        EditText alt_conatct_no = (EditText) findViewById(R.id.edit_profile_alt_contact);
        EditText date_of_birth = (EditText) findViewById(R.id.edit_profile_dob);

        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.gender);
        int selected = radiogroup.getCheckedRadioButtonId();
        RadioButton b = (RadioButton) findViewById(selected);
        b.getText();

        if (isEmpty(name.getText().toString())) {
            _changeStatus("error", R.id.edit_profile_name_status, _setMessage(R.string.name_empty));
        }
        if (isEmpty(phone.getText().toString())) {
            _changeStatus("error", R.id.edit_profile_mobile_status,
                    _setMessage(R.string.phone_empty));
        }
        if (isEmpty(email.getText().toString())) {
            _changeStatus("error", R.id.edit_profile_email_status,
                    _setMessage(R.string.email_empty));
        }
        if (isEmpty(alt_conatct_no.getText().toString())) {
            _changeStatus("error", R.id.edit_profile_alt_contact_status,
                    _setMessage(R.string.alt_phone_empty));
        }
        if (isEmpty(date_of_birth.getText().toString())) {
            _changeStatus("error", R.id.edit_profile_dob_status, _setMessage(R.string.birth_empty));
        }
        if (!isEmpty(name.getText().toString()) && !isEmpty(phone.getText().toString())
                && !isEmpty(email.getText().toString())
                && !isEmpty(alt_conatct_no.getText().toString())
                && !isEmpty(date_of_birth.getText().toString())) {
            ApiController.getInstance().updateProfile(this);
        }
    }

    private void _clearStatus(int[] ids) {
        toastShown = false;
        for (int id : ids) {
            setImageBackground(id, "success");
        }
    }

    private void _changeStatus(String type, int id, String message) {
        setImageBackground(id, type);
        showMsg(message);
    }

    private String _setMessage(int id) {
        return getResources().getString(id);
    }

    private void showMsg(String message) {
        if (toastShown == false && message != null && !message.equals("")) {
            Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 120);
            toast.show();
            toastShown = true;
        }
    }

    private boolean isEmpty(String data) {
        return (data == null || (data != null && data.trim().equals("")));
    }

    private void setImageBackground(int id, String type) {
        int image = (type == "success") ? R.drawable.success_icon : R.drawable.error_icon;
        ((ImageView) findViewById(id)).setVisibility(View.VISIBLE);
        ((ImageView) findViewById(id)).setBackgroundResource(image);
    }

    private DatePickerDialog.OnDateSetListener datepicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int date) {

            try {
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date d = dateFormatter.parse(String.valueOf(year + "-" + (month + 1) + "-" + date));
                String formatedDate = dateFormatter.format(d);
                ((EditText) findViewById(R.id.edit_profile_dob)).setText(formatedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onResponse(Event<ApiResponse> event) {
        super.onResponse(event);
        String status = event.getData().getApiStatus().getStatus();
        String msg = event.getData().getApiStatus().getMessage();
        showMsg(msg);
        if (status.equalsIgnoreCase("success")) {
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }

    @Override
    public void onProgress(int percent) {

    }

    @Override
    public void onComplete(byte[] data) {

    }

    @Override
    public void onError(byte[] data) {

    }

}
