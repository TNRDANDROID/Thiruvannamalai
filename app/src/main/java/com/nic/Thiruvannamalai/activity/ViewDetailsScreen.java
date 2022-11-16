package com.nic.Thiruvannamalai.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.nic.Thiruvannamalai.ImageZoom.ImageMatrixTouchHandler;
import com.nic.Thiruvannamalai.R;
import com.nic.Thiruvannamalai.adapter.SavedListViewAdapter;
import com.nic.Thiruvannamalai.api.Api;
import com.nic.Thiruvannamalai.api.ApiService;
import com.nic.Thiruvannamalai.api.ServerResponse;
import com.nic.Thiruvannamalai.constant.AppConstant;
import com.nic.Thiruvannamalai.databinding.ViewDetailsScreenBinding;
import com.nic.Thiruvannamalai.model.PublicTax;
import com.nic.Thiruvannamalai.session.PrefManager;
import com.nic.Thiruvannamalai.utils.UrlGenerator;
import com.nic.Thiruvannamalai.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.nic.Thiruvannamalai.utils.Utils.showAlert;

public class ViewDetailsScreen extends AppCompatActivity implements Api.ServerResponseListener  {
    ViewDetailsScreenBinding binding;
    private PrefManager prefManager;
    SavedListViewAdapter savedListViewAdapter;

    ArrayList<PublicTax> viewList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        binding = DataBindingUtil.setContentView(this, R.layout.view_details_screen);

        binding.setActivity(this);
        prefManager = new PrefManager(this);


        if(Utils.isOnline()){
            viewData();
        }
        else {
            Utils.showAlert(ViewDetailsScreen.this,"No Internet");
        }
    }

    private void viewData() {
        try {
            new ApiService(this).makeJSONObjectRequest("view", Api.Method.POST, UrlGenerator.getAppMainService(), viewJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public JSONObject viewJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), viewParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("view", "" + dataSet);
        return dataSet;
    }
    public  JSONObject viewParams() throws JSONException {
        JSONObject dataset = new JSONObject();
        try{
            dataset.put(AppConstant.KEY_SERVICE_ID,"tvm_details_view");
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        Log.d("view", "" + dataset);
        return dataset;
    }

    public void viewImage(int tvm_deepa_festival_id) {
        try {
            new ApiService(this).makeJSONObjectRequest("viewImage", Api.Method.POST, UrlGenerator.getAppMainService(), viewImageJsonParams(tvm_deepa_festival_id), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public JSONObject viewImageJsonParams(int tvm_deepa_festival_id) throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), viewImageParams(tvm_deepa_festival_id).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("viewImage", "" + dataSet);
        return dataSet;
    }
    public  JSONObject viewImageParams(int tvm_deepa_festival_id) throws JSONException {
        JSONObject dataset = new JSONObject();
        try{
            dataset.put(AppConstant.KEY_SERVICE_ID,"tvm_image_view");
            dataset.put("tvm_deepa_festival_id",tvm_deepa_festival_id);
        }
        catch (JSONException e){
            e.printStackTrace();
        }

        Log.d("viewImage", "" + dataset);
        return dataset;
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            Log.d("response", String.valueOf(serverResponse));
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();
            if ("view".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new InsertViewListTask().execute(jsonObject);
                }
                else if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("FAIL")) {
                    showAlert(this, jsonObject.getString("MESSAGE"));
                }
                Log.d("savedImage", "" + responseObj.toString());
            }
            if ("viewImage".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {

                    try {
                        JSONArray jsonArray = new JSONArray();
                        jsonArray = jsonObject.getJSONArray("JSON_DATA");
                        for(int i=0;i<jsonArray.length();i++){
                            String image_str = jsonArray.getJSONObject(i).getString("image");
                            ExpandedImage(image_str);
                        }
                    }
                    catch (JSONException e){

                    }
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
    public class InsertViewListTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {

                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray("JSON_DATA");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        viewList = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            PublicTax view_items = new PublicTax();
                            int tvm_deepa_festival_id = jsonArray.getJSONObject(i).getInt("tvm_deepa_festival_id");
                            int role_code = jsonArray.getJSONObject(i).getInt("role_code");
                            int dept_id = jsonArray.getJSONObject(i).getInt("dept_id");
                            String  name = jsonArray.getJSONObject(i).getString("name");
                            String  age = jsonArray.getJSONObject(i).getString("age");
                            String  location = jsonArray.getJSONObject(i).getString("location");
                            String  pincode = jsonArray.getJSONObject(i).getString("pincode");
                            String  mobile_no = jsonArray.getJSONObject(i).getString("mobile_no");
                            String  aadhar_no = jsonArray.getJSONObject(i).getString("aadhar_no");
                            String  sl_no = jsonArray.getJSONObject(i).getString("sl_no");
                            String  from_date = jsonArray.getJSONObject(i).getString("from_date");
                            String  to_date = jsonArray.getJSONObject(i).getString("to_date");

                            view_items.setTvm_deepa_festival_id(tvm_deepa_festival_id);
                            view_items.setRole_code(role_code);
                            view_items.setDept_id(dept_id);
                            view_items.setName(name);
                            view_items.setAge(age);
                            view_items.setLocation(location);
                            view_items.setPincode(pincode);
                            view_items.setMobile_no(mobile_no);
                            view_items.setAadhar_no(aadhar_no);
                            view_items.setSl_no(sl_no);
                            view_items.setFrom_date(from_date);
                            view_items.setTo_date(to_date);
                            viewList.add(view_items);
                        }
                    }
                    catch (JSONException e){

                    }
                }

            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            binding.recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            if(viewList.size()>0){
                binding.noDataFound.setVisibility(View.GONE);
                binding.recycler.setVisibility(View.VISIBLE);
                savedListViewAdapter = new SavedListViewAdapter(viewList,ViewDetailsScreen.this);
                binding.recycler.setAdapter(savedListViewAdapter);
            }
            else {
                binding.noDataFound.setVisibility(View.VISIBLE);
                binding.recycler.setVisibility(View.GONE);
                binding.recycler.setAdapter(null);
            }
        }
    }

    private void ExpandedImage(String profile) {
        try {
            String bitmap="";
            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //Inflate the view from a predefined XML layout
            View ImagePopupLayout = inflater.inflate(R.layout.image_custom_layout, null);

            ImageView zoomImage = (ImageView) ImagePopupLayout.findViewById(R.id.imgZoomProfileImage);

            zoomImage.setImageBitmap(convertStringToBitmap(profile));

            ImageMatrixTouchHandler imageMatrixTouchHandler = new ImageMatrixTouchHandler(this);
            zoomImage.setOnTouchListener(imageMatrixTouchHandler);
//            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext, R.style.MyDialogTheme);
            androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
            dialogBuilder.setView(ImagePopupLayout);

            final androidx.appcompat.app.AlertDialog alert = dialogBuilder.create();
            alert.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_zoomInOut;
            alert.show();
            alert.getWindow().setBackgroundDrawableResource(R.color.full_transparent);
            alert.setCanceledOnTouchOutside(true);

            zoomImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alert.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Bitmap convertStringToBitmap(String string) {
        byte[] byteArray1;
        byteArray1 = Base64.decode(string, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray1, 0,
                byteArray1.length);/* w  w  w.ja va 2 s  .  c om*/
        return bmp;
    }
}
