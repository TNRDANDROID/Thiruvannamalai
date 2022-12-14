package com.nic.Thiruvannamalai.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nic.Thiruvannamalai.R;
import com.nic.Thiruvannamalai.api.Api;
import com.nic.Thiruvannamalai.api.ApiService;
import com.nic.Thiruvannamalai.api.ServerResponse;
import com.nic.Thiruvannamalai.constant.AppConstant;
import com.nic.Thiruvannamalai.databinding.RegisteredScreenBinding;
import com.nic.Thiruvannamalai.session.PrefManager;
import com.nic.Thiruvannamalai.utils.CameraUtils;
import com.nic.Thiruvannamalai.utils.UrlGenerator;
import com.nic.Thiruvannamalai.utils.Utils;
import com.nic.Thiruvannamalai.utils.VerhoeffAlgorithm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;
import in.mayanknagwanshi.imagepicker.ImageSelectActivity;

import static com.nic.Thiruvannamalai.utils.CameraUtils.MEDIA_TYPE_IMAGE;
import static com.nic.Thiruvannamalai.utils.Utils.showAlert;

public class DetailsEnterScreen extends AppCompatActivity implements Api.ServerResponseListener  {
    RegisteredScreenBinding registeredScreenBinding;
    private PrefManager prefManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        registeredScreenBinding = DataBindingUtil.setContentView(this, R.layout.registered_screen);

        registeredScreenBinding.setActivity(this);
        prefManager = new PrefManager(this);
        registeredScreenBinding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (CameraUtils.checkPermissions(DetailsEnterScreen.this)) {
                        captureImage();
                    } else {
                        requestCameraPermission(MEDIA_TYPE_IMAGE);
                    }
//                            checkPermissionForCamera();
                } else {
                    captureImage();
                }
            }
        });
        registeredScreenBinding.imageViewPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (CameraUtils.checkPermissions(DetailsEnterScreen.this)) {
                        captureImage();
                    } else {
                        requestCameraPermission(MEDIA_TYPE_IMAGE);
                    }
//                            checkPermissionForCamera();
                } else {
                    captureImage();
                }
            }
        });

        registeredScreenBinding.backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
    public void validate() {

        if(!registeredScreenBinding.userName.getText().toString().equalsIgnoreCase("")){
            if(!registeredScreenBinding.age.getText().toString().equalsIgnoreCase("")){
                if(!registeredScreenBinding.location.getText().toString().equalsIgnoreCase("")){
                    if(mobilValidation()){
                        if(aadharValidation()){
                            if (registeredScreenBinding.imageView.getDrawable() != null) {
                                save_and_delete_alert("save");
                            }
                            else {
                                showAlert(this,"Please Capture Image");
                            }
                        }else {
                            registeredScreenBinding.adharNumber.requestFocus();
                            registeredScreenBinding.adharNumber.setError("Enter valid Aadhar Number");
                        }
                    }
                    else {
                        registeredScreenBinding.phoneNumber.requestFocus();
                        registeredScreenBinding.phoneNumber.setError("Enter Valid Phone Number");
                        //showAlert(this,"Enter Phone Number");
                    }
                }else {
                    registeredScreenBinding.location.requestFocus();
                    registeredScreenBinding.location.setError("Enter Location");
                    //showAlert(this,"Enter Location");
                }
            }else {
                registeredScreenBinding.age.requestFocus();
                registeredScreenBinding.age.setError("Enter Age");
                //showAlert(this,"Enter Age");
            }
        }else {
            registeredScreenBinding.userName.requestFocus();
            registeredScreenBinding.userName.setError("Enter User Name");
            //showAlert(this,"Enter User Name");
        }

    }
/*
    public void validateFields() {
        if(mobilValidation()){
            if(aadharValidation()){
                if (registeredScreenBinding.imageView.getDrawable() != null) {
                    save_and_delete_alert("save");
                }
                else {
                    showAlert(this,"Please Capture Image");
                }
            }else {
                registeredScreenBinding.adharNumber.requestFocus();
                registeredScreenBinding.adharNumber.setError("Enter valid Aadhar Number");
            }
        }else {
            registeredScreenBinding.phoneNumber.requestFocus();
            registeredScreenBinding.phoneNumber.setError("Enter Valid Phone Number");
        }

    }
*/
    public boolean mobilValidation() {
        boolean flag;
            if(!registeredScreenBinding.phoneNumber.getText().toString().equalsIgnoreCase("")&&(Utils.isValidMobile1(registeredScreenBinding.phoneNumber.getText().toString()))){
                flag=true;
            }else {
                flag=false;
            }

        return flag;
    }
    public boolean aadharValidation() {
        boolean flag;
        if(!registeredScreenBinding.adharNumber.getText().toString().isEmpty()){
            if(!registeredScreenBinding.adharNumber.getText().toString().equalsIgnoreCase("")&&(registeredScreenBinding.adharNumber.getText().toString().length()==12)&&(validateAadhar(registeredScreenBinding.adharNumber.getText().toString()))){
                flag=true;
            }else {
                flag=false;
            }

        }else {
            flag=true;
        }
        return flag;
    }

    private boolean validateAadhar(String aadharNumber) {
        Pattern aadharPattern = Pattern.compile("\\d{12}");
        boolean isValidAadhar = aadharPattern.matcher(aadharNumber).matches();
        if(isValidAadhar){
            isValidAadhar = VerhoeffAlgorithm.validateVerhoeff(aadharNumber);
        }
        return isValidAadhar;

    }
    private void saveData() {
        try {
            new ApiService(this).makeJSONObjectRequest("save", Api.Method.POST, UrlGenerator.getAppMainService(), saveJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public JSONObject saveJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), saveParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("sav_params", "" + dataSet);
        return dataSet;
    }
    public  JSONObject saveParams() throws JSONException {
        JSONObject dataset = new JSONObject();
        JSONObject dataset1 = new JSONObject();
        JSONArray jsonArray = new JSONArray();

        String image_str = "";
        Bitmap bitmap = ((BitmapDrawable) registeredScreenBinding.imageView.getDrawable()).getBitmap();
        image_str = BitMapToString(bitmap);
        try{
            dataset.put(AppConstant.KEY_SERVICE_ID,"t_tvm_deepa_festival_details_save");
            dataset1.put("tvm_deepa_festival_id","");
            dataset1.put("name",registeredScreenBinding.userName.getText().toString());
            dataset1.put("age", registeredScreenBinding.age.getText().toString());
            dataset1.put("location",registeredScreenBinding.location.getText().toString());
            dataset1.put("pincode","");
            dataset1.put("mobile_no", registeredScreenBinding.phoneNumber.getText().toString());
            dataset1.put("aadhar_no", registeredScreenBinding.adharNumber.getText().toString());
            dataset1.put("image", image_str);
            jsonArray.put(dataset1);
            dataset.put("tvm_deepa_festival",jsonArray);

        }
        catch (JSONException e){
            e.printStackTrace();
        }

        Log.d("save_params", "" + dataset);
        return dataset;
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    private void requestCameraPermission(final int type) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {

                            if (type == MEDIA_TYPE_IMAGE) {
                                // capture picture
                                captureImage();
                            } else {
//                                captureVideo();
                            }

                        } else if (report.isAnyPermissionPermanentlyDenied()) {
                            showPermissionsAlert();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
    private void showPermissionsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Required")
                .setMessage("Camera Need Few Permissions")
                .setPositiveButton(getResources().getString(R.string.goto_settings), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(DetailsEnterScreen.this);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void captureImage() {

        Intent intent = new Intent(this, ImageSelectActivity.class);
        intent.putExtra(ImageSelectActivity.FLAG_COMPRESS, true);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_CAMERA, true);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_GALLERY, false);//default is true
        intent.putExtra(ImageSelectActivity.FLAG_CROP, false);//default is false
        startActivityForResult(intent, 1213);
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
         if(requestCode == 1213){
             if(resultCode==RESULT_OK){
                 String filePath = data.getStringExtra(ImageSelectActivity.RESULT_FILE_PATH);
                 Bitmap rotatedBitmap = BitmapFactory.decodeFile(filePath);

                 registeredScreenBinding.imageViewPreview.setVisibility(View.GONE);
                 registeredScreenBinding.imageView.setVisibility(View.VISIBLE);
                 registeredScreenBinding.imageView.setImageBitmap(rotatedBitmap);
             }
             else {
                 Toasty.error(DetailsEnterScreen.this,"User Cancelled Image capture",Toasty.LENGTH_SHORT,true).show();
             }


         }

    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            Log.d("response", String.valueOf(serverResponse));
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();


            if ("save".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    tokenNumberShowAlert(jsonObject.getString("MESSAGE"),jsonObject.getString("TOKEN"));
                    registeredScreenBinding.userName.setText("");
                    registeredScreenBinding.age.setText("");
                    registeredScreenBinding.location.setText("");
                    registeredScreenBinding.pinCode.setText("");
                    registeredScreenBinding.phoneNumber.setText("");
                    registeredScreenBinding.adharNumber.setText("");
                    registeredScreenBinding.imageView.setImageDrawable(null);
                    registeredScreenBinding.imageViewPreview.setVisibility(View.VISIBLE);
                    registeredScreenBinding.imageView.setVisibility(View.GONE);

                }
                else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("FAIL")) {
                    showAlert(this, jsonObject.getString("MESSAGE"));
                }
                Log.d("savedImage", "" + responseObj.toString());
                Log.d("savedImage", "" + responseDecryptedBlockKey);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    private void save_and_delete_alert(String save_delete){
        try {
            final Dialog dialog = new Dialog(DetailsEnterScreen.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog);

            TextView text = (TextView) dialog.findViewById(R.id.tv_message);
            if(save_delete.equals("save")) {
                text.setText(getResources().getString(R.string.do_u_want_to_upload));
            }
            else if(save_delete.equals("delete")){
                text.setText(getResources().getString(R.string.do_u_want_to_delete));
            }
            else if(save_delete.equals("dead_sapling_save")){
                text.setText(getResources().getString(R.string.do_u_want_to_upload));
            }

            Button yesButton = (Button) dialog.findViewById(R.id.btn_ok);
            Button noButton = (Button) dialog.findViewById(R.id.btn_cancel);
            noButton.setVisibility(View.VISIBLE);
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(save_delete.equals("save")) {
                        saveData();
                        dialog.dismiss();
                    }
                    else if(save_delete.equals("delete")) {
                        dialog.dismiss();
                    }
                    else if(save_delete.equals("dead_sapling_save")){
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public  void tokenNumberShowAlert(String msg,String token_number){
        try {
            final Dialog dialog = new Dialog(DetailsEnterScreen.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.token_number_show_alert);

            TextView text = (TextView) dialog.findViewById(R.id.tv_message);
            TextView token_number_text = (TextView) dialog.findViewById(R.id.token_number);
            text.setText(msg);
            token_number_text.setText(token_number);

            Button dialogButton = (Button) dialog.findViewById(R.id.btn_ok);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
