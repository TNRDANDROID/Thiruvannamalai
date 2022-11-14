package com.nic.Thiruvannamalai.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.VolleyError;
import com.nic.Thiruvannamalai.R;
import com.nic.Thiruvannamalai.adapter.TaxTypeListAdapter;
import com.nic.Thiruvannamalai.api.Api;
import com.nic.Thiruvannamalai.api.ApiService;
import com.nic.Thiruvannamalai.api.ServerResponse;
import com.nic.Thiruvannamalai.constant.AppConstant;
import com.nic.Thiruvannamalai.databinding.DashboardBinding;
import com.nic.Thiruvannamalai.dialog.MyDialog;
import com.nic.Thiruvannamalai.model.PublicTax;
import com.nic.Thiruvannamalai.session.PrefManager;
import com.nic.Thiruvannamalai.utils.UrlGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity implements Api.ServerResponseListener, View.OnClickListener, MyDialog.myOnClickListener {
    private DashboardBinding dashboardBinding;
    private PrefManager prefManager;

    TaxTypeListAdapter taxListAdapter;
    ArrayList<PublicTax> taxTypeList;
    ArrayList<PublicTax> transactionList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dashboardBinding = DataBindingUtil.setContentView(this, R.layout.dashboard);
        dashboardBinding.setActivity(this);
        prefManager = new PrefManager(this);

        dashboardBinding.menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuDrawer();
            }
        });
        getTaxTypeList();

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
    public void getTaxTypeList() {
        try {
            new ApiService(this).makeJSONObjectRequest("TaxTypeList", Api.Method.POST, UrlGenerator.getServicesListUrl(), taxTypeListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject taxTypeListJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "TaxTypeList");
        Log.d("taxTypeList", "" + dataSet);
        return dataSet;
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject responseObj = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();
            String status;
            String response;

            if ("TaxTypeList".equals(urlType) && responseObj != null) {
                if (responseObj.getString("STATUS").equalsIgnoreCase("SUCCESS") && responseObj.getString("RESPONSE").equalsIgnoreCase("SUCCESS")) {
                    new Insert_TaxCollection().execute(responseObj);
                }
                Log.d("TaxCollection", "" + responseObj);
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

    @SuppressLint("StaticFieldLeak")
    public class Insert_TaxCollection extends AsyncTask<JSONObject, Void, ArrayList<PublicTax>> {

        @Override
        protected ArrayList<PublicTax> doInBackground(JSONObject... params) {

            if (params.length > 0) {
                taxTypeList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray();

                try {
                    jsonArray = params[0].getJSONArray("DATA");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        PublicTax tax = new PublicTax();
                        tax.setTaxtypeid(jsonArray.getJSONObject(i).getString("taxtypeid"));
                        tax.setTaxtypedesc_en(jsonArray.getJSONObject(i).getString("taxtypedesc_en"));

                        taxTypeList.add(tax);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }



            }


            return taxTypeList;


        }

        @Override
        protected void onPostExecute(ArrayList<PublicTax> list) {
            super.onPostExecute(list);

            ArrayList<Integer> taxImageList = new ArrayList<>();
            taxImageList.add(R.drawable.property_tax);
            taxImageList.add(R.drawable.water_tap);
            taxImageList.add(R.drawable.professional_tax);
            taxImageList.add(R.drawable.non_tax);
            taxImageList.add(R.drawable.trade_licence_tax);
//            dashboardBinding.recycler.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
            dashboardBinding.recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

            if(list.size()>0){
                dashboardBinding.noDataFound.setVisibility(View.GONE);
                dashboardBinding.recycler.setVisibility(View.VISIBLE);
                taxListAdapter = new TaxTypeListAdapter(Dashboard.this,list,taxImageList);
                dashboardBinding.recycler.setAdapter(taxListAdapter);
            }else {
                dashboardBinding.noDataFound.setVisibility(View.VISIBLE);
                dashboardBinding.recycler.setVisibility(View.GONE);
            }


        }
    }

}
