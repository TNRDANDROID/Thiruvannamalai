package com.nic.publictax.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.nic.publictax.R;
import com.nic.publictax.api.Api;
import com.nic.publictax.api.ApiService;
import com.nic.publictax.api.ServerResponse;
import com.nic.publictax.constant.AppConstant;
import com.nic.publictax.dataBase.DBHelper;
import com.nic.publictax.dataBase.dbData;
import com.nic.publictax.databinding.DashboardBinding;
import com.nic.publictax.dialog.MyDialog;
import com.nic.publictax.session.PrefManager;
import com.nic.publictax.support.ProgressHUD;
import com.nic.publictax.utils.UrlGenerator;
import com.nic.publictax.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.ertugrul.lib.OnItemReselectedListener;


public class Dashboard extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener, MyDialog.myOnClickListener {
    private DashboardBinding dashboardBinding;
    private PrefManager prefManager;
    public com.nic.publictax.dataBase.dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    Handler myHandler = new Handler();
    private ProgressHUD progressHUD;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dashboardBinding = DataBindingUtil.setContentView(this, R.layout.dashboard);
        dashboardBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }

        dashboardBinding.menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuDrawer();
            }
        });
        getTaxCollection();
        dashboardBinding.bottomBar.setOnItemReselectListener(new OnItemReselectedListener() {
            @Override
            public void onItemReselect(int pos) {
                Toast.makeText(Dashboard.this, "selected"+pos, Toast
                        .LENGTH_SHORT).show();
            }
        });
    }

    public void openMenuDrawer(){
        if(dashboardBinding.drawerLayout.isDrawerOpen(Gravity.LEFT)){
            dashboardBinding.drawerLayout.closeDrawer(Gravity.LEFT);
        }else{
            dashboardBinding.drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
    @Override
    protected void onResume() {
        super.onResume();

    }
    public  void getTaxCollection() {
        try {
            //new ApiService(this).makeJSONObjectRequest("TaxCollection", Api.Method.POST, UrlGenerator.getAppMainServiceUrl(), taxCollectionJsonParams(), "not cache", this);
            new ApiService(this).makeJSONObjectRequest("TaxCollection", Api.Method.POST, UrlGenerator.getServicesListUrl(), taxCollectionJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject taxCollectionJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), taxCollectionParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("TaxCollection", "" + dataSet);
        return dataSet;
    }
    public JSONObject taxCollectionParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "TaxTypeList");
        dataSet.put("language_name", "en");
        Log.d("TaxCollection", "" + dataSet);
        return dataSet;
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject response = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();

            if ("TaxCollection".equals(urlType) && response != null) {
                String key = response.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);

                if (jsonObject.getString("STATUS").equalsIgnoreCase("SUCCESS") && jsonObject.getString("RESPONSE").equalsIgnoreCase("SUCCESS")) {
//                    new Insert_TaxCollection().execute(jsonObject);
                }
                Log.d("TaxCollection", "" + jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError error) {

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
