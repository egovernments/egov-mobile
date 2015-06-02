package com.egov.android.view.activity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.egov.android.R;
import com.egov.android.controller.ApiController;
import com.egov.android.library.api.ApiResponse;
import com.egov.android.library.http.IHttpClientListener;
import com.egov.android.library.http.Uploader;
import com.egov.android.library.listener.Event;

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
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.editprofile_doEditprofile:
                editProfile();
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
        }
    }

    private void openDialog() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.show();

        ((TextView) dialog.findViewById(R.id.from_gallery)).setOnClickListener(this);
        ((TextView) dialog.findViewById(R.id.from_camera)).setOnClickListener(this);
    }

    private void uploadImageFromSDCard() {
        Intent photo_picker = new Intent(Intent.ACTION_PICK);
        photo_picker.setType("image/*,video/*");
        startActivityForResult(photo_picker, RESULT_LOAD_IMAGE);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null,
                    null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            _setImageResource(imagePath);
        }
    }

    private void _setImageResource(String imagePath) {

        ImageView image = (ImageView) findViewById(R.id.profile_image);
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
            _changeStatus("error", R.id.edit_profile_name_status, "Please enter name");
        }
        if (isEmpty(phone.getText().toString())) {
            _changeStatus("error", R.id.edit_profile_mobile_status, "Please enter mobile number");
        }
        if (isEmpty(email.getText().toString())) {
            _changeStatus("error", R.id.edit_profile_email_status, "Please enter email");
        }
        if (isEmpty(alt_conatct_no.getText().toString())) {
            _changeStatus("error", R.id.edit_profile_alt_contact_status,
                    "Please enter alternative contact number");
        }
        if (isEmpty(date_of_birth.getText().toString())) {
            _changeStatus("error", R.id.edit_profile_dob_status,
                    "Please enter alternative contact number");
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
