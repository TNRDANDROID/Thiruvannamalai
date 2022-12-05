package com.nic.Thiruvannamalai.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.nic.Thiruvannamalai.R;
import com.nic.Thiruvannamalai.api.Api;
import com.nic.Thiruvannamalai.api.ApiService;
import com.nic.Thiruvannamalai.api.ServerResponse;
import com.nic.Thiruvannamalai.constant.AppConstant;
import com.nic.Thiruvannamalai.databinding.ActivityQRVerifivationBinding;
import com.nic.Thiruvannamalai.session.PrefManager;
import com.nic.Thiruvannamalai.utils.UrlGenerator;
import com.nic.Thiruvannamalai.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.nic.Thiruvannamalai.utils.Utils.showAlert;

public class QRVerifivation extends AppCompatActivity implements Api.ServerResponseListener {
    private final int MY_CAMERA_REQUEST_CODE = 100;
    private PrefManager prefManager;
    ActivityQRVerifivationBinding binding;
    String type="";
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_q_r_verifivation);
        binding.setActivity(QRVerifivation.this);
        prefManager = new PrefManager(this);

        binding.backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.qrScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.documentRl.setVisibility(View.GONE);
                performQrCodeReader();
            }
        });
        binding.qrOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="online";
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                }
                else {
                    binding.documentRl.setVisibility(View.GONE);
                    performQrCodeReader();
                }
            }
        });
        binding.qrOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type="offline";
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                }
                else {
                    binding.documentRl.setVisibility(View.GONE);
                    performQrCodeReader();
                }
            }
        });
    }

    public void performQrCodeReader(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setOrientationLocked(true);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                binding.documentRl.setVisibility(View.GONE);
                performQrCodeReader();
            } else {
//                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                showAlert(QRVerifivation.this,"Permission Required");

            }
        }
    }
    public  void showAlert(Activity activity, String msg){
        try {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog);

            TextView text = (TextView) dialog.findViewById(R.id.tv_message);
            text.setText(msg);

            Button dialogButton = (Button) dialog.findViewById(R.id.btn_ok);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
                    }
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
            else {
                //Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                validate(result.getContents());

            }
        } else {
            //super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void validate(String result) {
        String value  = result;
        if(value!= null && !value.isEmpty()){
            byte[] data1 = Base64.decode(value, Base64.DEFAULT);
            String text = new String(data1, StandardCharsets.UTF_8);
            if(text!= null && !text.isEmpty()){
                String[] separated = text.split("-");
                if(separated.length == 6){
                    String id=separated[0];
                    String user_name=separated[1];
                    String user_age=separated[2];
                    String user_mobile_no=separated[3];
                    String user_aadhar_no=separated[4];
                    String festival_id=separated[5];
                    if(id!= null && !id.isEmpty()){
                        if(user_name!= null && !user_name.isEmpty()){
                            if(user_age!= null && !user_age.isEmpty()){
                                if(user_mobile_no!= null && !user_mobile_no.isEmpty()){
                                    if(festival_id!= null && !festival_id.isEmpty()){
                                        if(type.equalsIgnoreCase("online")){
                                            viewPdf(festival_id);
                                        }else {
                                            details_alert(value);
                                        }
                                    }else {
                                        Utils.showAlert(this, "Invalid Argument");
                                    }
                                }else {
                                    Utils.showAlert(this, "Invalid Argument");
                                }
                            }else {
                                Utils.showAlert(this, "Invalid Argument");
                            }
                        }else {
                            Utils.showAlert(this, "Invalid Argument");
                        }
                    }else {
                        Utils.showAlert(this, "Invalid Argument");
                    }
                }else {
                    Utils.showAlert(this, "Invalid Argument");
                }

            }else {
                Utils.showAlert(this, "Invalid Argument");
            }
        }else {
            Utils.showAlert(this, "Invalid Argument");
        }

    }


    private void details_alert(String value){
        final Dialog dialog = new Dialog(QRVerifivation.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.profile_alert_dialog);

        TextView tvm_deepa_festival_id = (TextView) dialog.findViewById(R.id.tvm_deepa_festival_id);
        TextView name = (TextView) dialog.findViewById(R.id.name);
        TextView age = (TextView) dialog.findViewById(R.id.age);
        TextView mobile_no = (TextView) dialog.findViewById(R.id.mobile_no);
        TextView aadhar_no = (TextView) dialog.findViewById(R.id.aadhar_no);
        TextView ok = (TextView) dialog.findViewById(R.id.ok);
        CircleImageView profile_image = (CircleImageView) dialog.findViewById(R.id.profile_image);

        if(value!= null){
            byte[] data1 = Base64.decode(value, Base64.DEFAULT);
            String text = new String(data1, StandardCharsets.UTF_8);

            String[] separated = text.split("-");
            String id=separated[0];
            String user_name=separated[1];
            String user_age=separated[2];
            String user_mobile_no=separated[3];
            String user_aadhar_no=separated[4];
            System.out.println("Result text >>"+id+name+age+mobile_no+aadhar_no);
            tvm_deepa_festival_id.setText("Token No : "+id);
            name.setText("Name : "+user_name);
            age.setText("Age : "+user_age);
            mobile_no.setText("Mobile No : "+user_mobile_no);
            aadhar_no.setText("Aadhar No : "+user_aadhar_no);
        }


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(layoutParams);
        dialog.getWindow().setBackgroundDrawableResource(R.color.full_transparent);
        dialog.show();

    }

    public void viewPdf(String tvm_deepa_festival_id) {
        try {
            new ApiService(this).makeJSONObjectRequest("viewPdf", Api.Method.POST, UrlGenerator.getAppMainService(), viewPdfJsonParams(tvm_deepa_festival_id), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public JSONObject viewPdfJsonParams(String tvm_deepa_festival_id) throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), viewPdfParams(tvm_deepa_festival_id).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("viewPdf", "" + dataSet);
        return dataSet;
    }
    public  JSONObject viewPdfParams(String tvm_deepa_festival_id) throws JSONException {
        JSONObject dataset = new JSONObject();
        try{
            dataset.put(AppConstant.KEY_SERVICE_ID,"ScanQRCode");
            dataset.put("qr_data",tvm_deepa_festival_id);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        Log.d("viewPdf", "" + dataset);
        return dataset;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            Log.d("response", String.valueOf(serverResponse));
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();
            if ("viewPdf".equals(urlType) && responseObj != null) {
                //String key = responseObj.getString(AppConstant.ENCODE_DATA);
                //String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                //JSONObject jsonObject = new JSONObject(responseObj);
                if (responseObj.getString("STATUS").equalsIgnoreCase("OK") && responseObj.getString("RESPONSE").equalsIgnoreCase("OK")) {

                    try {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1 = responseObj.getJSONObject("JSON_DATA");
                        String pdf_string =jsonObject1.getString("pdf_string");
                        binding.documentRl.setVisibility(View.VISIBLE);
                        viewDocument(pdf_string);
                    }
                    catch (JSONException e){

                    }
                }
                else {
                    Utils.showAlert(QRVerifivation.this,responseObj.getString("MESSAGE"));
                }
                Log.d("savedImage", "" + responseObj.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void viewDocument(String documentString) {
        if (documentString != null && !documentString.equals("")) {
            binding.documentViewer.setVisibility(View.VISIBLE);
            byte[] decodedString = new byte[0];
            try {
                //byte[] name = java.util.Base64.getEncoder().encode(fileString.getBytes());
                decodedString = Base64.decode(documentString, Base64.DEFAULT);
                //System.out.println(new String(decodedString));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            binding.documentViewer.fromBytes(decodedString).load();


        }
        else {
            Utils.showAlert(this,"No Record");
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
