package com.nic.nutrigarden.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.nic.nutrigarden.R;
import com.nic.nutrigarden.api.Api;
import com.nic.nutrigarden.api.ApiService;
import com.nic.nutrigarden.api.ServerResponse;
import com.nic.nutrigarden.constant.AppConstant;
import com.nic.nutrigarden.dataBase.dbData;
import com.nic.nutrigarden.databinding.MainPageBinding;
import com.nic.nutrigarden.dialog.MyDialog;
import com.nic.nutrigarden.model.PMAYSurvey;
import com.nic.nutrigarden.session.PrefManager;
import com.nic.nutrigarden.utils.UrlGenerator;
import com.nic.nutrigarden.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainPage extends AppCompatActivity implements Api.ServerResponseListener ,MyDialog.myOnClickListener{

    public MainPageBinding mainPageBinding;
    private PrefManager prefManager;
    public com.nic.nutrigarden.dataBase.dbData dbData = new dbData(this);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainPageBinding = DataBindingUtil.setContentView(this, R.layout.main_page);
        mainPageBinding.setActivity(this);

        prefManager = new PrefManager(this);

        mainPageBinding.tvName.setText(prefManager.getDistrictName());
        mainPageBinding.designation.setText(prefManager.getBlockName());

        if(Utils.isOnline()){
            getNutri_garden_master_form_list();
        }

        mainPageBinding.goWorks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHomeScreen();
            }
        });
        mainPageBinding.syncLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPendingScreen();
            }
        });
        syncButtonVisibility();
    }

    @Override
    protected void onResume() {
        super.onResume();
        syncButtonVisibility();
    }

    public void getNutri_garden_master_form_list() {
        try {
            new ApiService(this).makeJSONObjectRequest("nutri_garden_master_form_list", Api.Method.POST, UrlGenerator.getPMAYListUrl(), nutri_garden_master_form_listJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject nutri_garden_master_form_listJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), Utils.nutri_garden_master_form_listJsonParams(this).toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("master_listJsonParams", "" + authKey);
        return dataSet;
    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject loginResponse = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();

            if ("nutri_garden_master_form_list".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    new Insertnutri_garden_master_form_list().execute(jsonObject);
                }
                Log.d("garden_master_list", "" + responseDecryptedBlockKey);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public class Insertnutri_garden_master_form_list extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            dbData.open();
            dbData.deletefin_year();
            dbData.deletework_type();
            dbData.deleteself_help_group();
            dbData.deleteself_help_group_member();

                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    JSONArray jsonArray1 = new JSONArray();
                    JSONArray jsonArray2 = new JSONArray();
                    JSONArray jsonArray3 = new JSONArray();
                    JSONArray jsonArray4 = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {

                        try {
                            jsonArray1= jsonArray.getJSONObject(i).getJSONArray("fin_year");
                            jsonArray2= jsonArray.getJSONObject(i).getJSONArray("work_type");
                            jsonArray3= jsonArray.getJSONObject(i).getJSONArray("self_help_group");
                            jsonArray4= jsonArray.getJSONObject(i).getJSONArray("self_help_group_member");
                            for (int j=0;j<jsonArray1.length();j++){
                                PMAYSurvey pmaySurvey = new PMAYSurvey();
                                pmaySurvey.setFin_year(jsonArray1.getJSONObject(j).getString("fin_year"));
                                dbData.insert_Master_Fin_Year(pmaySurvey);
                            }
                            for (int j=0;j<jsonArray2.length();j++){
                                PMAYSurvey pmaySurvey = new PMAYSurvey();
                                pmaySurvey.setWork_code(jsonArray2.getJSONObject(j).getInt("work_code"));
                                pmaySurvey.setWork_name(jsonArray2.getJSONObject(j).getString("work_name"));
                                dbData.insert_Master_Work_Type(pmaySurvey);
                            }
                            for (int j=0;j<jsonArray3.length();j++){
                                PMAYSurvey pmaySurvey = new PMAYSurvey();
                                pmaySurvey.setShg_code(jsonArray3.getJSONObject(j).getInt("shg_code"));
                                pmaySurvey.setShg_name(jsonArray3.getJSONObject(j).getString("shg_name"));
                                dbData.insert_Master_Self_Help_Group(pmaySurvey);
                            }
                            for (int j=0;j<jsonArray4.length();j++){
                                PMAYSurvey pmaySurvey = new PMAYSurvey();
                                pmaySurvey.setShg_code(jsonArray4.getJSONObject(j).getInt("shg_code"));
                                pmaySurvey.setShg_member_code(jsonArray4.getJSONObject(j).getInt("shg_member_code"));
                                pmaySurvey.setMember_name(jsonArray4.getJSONObject(j).getString("member_name"));
                                dbData.insert_Master_Self_Help_Group_Member(pmaySurvey);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }


            return null;


        }
    }
    private void showHomeScreen() {
        Intent intent = new Intent(MainPage.this, NewHomePage.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void syncButtonVisibility() {
        dbData.open();
        ArrayList<PMAYSurvey> workImageCount = dbData.getAllTreeImages();

        if (workImageCount.size() > 0) {
            mainPageBinding.syncLayout.setVisibility(View.VISIBLE);
        }else {
            mainPageBinding.syncLayout.setVisibility(View.GONE);
        }
    }

    public void openPendingScreen() {
        Intent intent = new Intent(this, NewPendingScreen.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    public void logout() {
        dbData.open();
        ArrayList<PMAYSurvey> ImageCount = dbData.getAllTreeImages();
        if (!Utils.isOnline()) {
            Utils.showAlert(this, "Logging out while offline may leads to loss of data!");
        } else {
            if (!(ImageCount.size() > 0)) {
                closeApplication();
            } else {
                Utils.showAlert(this, "Sync all the data before logout!");
            }
        }
    }

    public void closeApplication() {
        new MyDialog(this).exitDialog(this, "Are you sure you want to Logout?", "Logout");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                new MyDialog(this).exitDialog(this, "Are you sure you want to exit ?", "Exit");
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onButtonClick(AlertDialog alertDialog, String type) {
        alertDialog.dismiss();
        if ("Exit".equalsIgnoreCase(type)) {
            onBackPressed();
        } else {

            Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("EXIT", false);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        }
    }
}
