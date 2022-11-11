package com.nic.publictax.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.android.volley.VolleyError;
import com.nic.publictax.R;
import com.nic.publictax.adapter.CommonAdapter;
import com.nic.publictax.api.Api;
import com.nic.publictax.api.ApiService;
import com.nic.publictax.api.ServerResponse;
import com.nic.publictax.constant.AppConstant;
import com.nic.publictax.databinding.RegistrationBinding;
import com.nic.publictax.model.PublicTax;
import com.nic.publictax.session.PrefManager;
import com.nic.publictax.support.ProgressHUD;
import com.nic.publictax.utils.UrlGenerator;
import com.nic.publictax.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Registration extends AppCompatActivity implements Api.ServerResponseListener{
    private PrefManager prefManager;
    private ProgressHUD progressHUD;
    RegistrationBinding demadSearchBinding;
    private List<PublicTax> District = new ArrayList<>();
    private ArrayList<PublicTax> taxTypeList = new ArrayList<>();
    private List<PublicTax> Block = new ArrayList<>();
    private List<PublicTax> Village = new ArrayList<>();
    String dcode;
    String bcode;
    String pv_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        demadSearchBinding = DataBindingUtil.setContentView(this, R.layout.registration);
        demadSearchBinding.setActivity(this);
        prefManager = new PrefManager(this);

        demadSearchBinding.districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    dcode = District.get(position).getDistictCode();
                    bcode="";
                    getBlockList();
                }
                else {
                    dcode ="";
                    bcode ="";
                    pv_code ="";
                    demadSearchBinding.blockSpinner.setAdapter(null);
                    demadSearchBinding.villageSpinner.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        demadSearchBinding.blockSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    dcode = District.get(position).getDistictCode();
                    bcode = Block.get(position).getBlockCode();
                    pv_code ="";
                    getVillageList();
                }
                else {
                    bcode ="";
                    pv_code ="";
                    demadSearchBinding.villageSpinner.setAdapter(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        demadSearchBinding.go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDashBoardScreen();
            }
        });

        if(Utils.isOnline()){
            getDistrictList();
            getTaxTypeList();
        }
    }

    public void getDistrictList() {
        try {
            new ApiService(this).makeJSONObjectRequest("DistrictList", Api.Method.POST, UrlGenerator.getServicesListUrl(), districtListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getBlockList() {
        try {
            new ApiService(this).makeJSONObjectRequest("BlockList", Api.Method.POST, UrlGenerator.getServicesListUrl(), blockListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getVillageList() {
        try {
            new ApiService(this).makeJSONObjectRequest("VillageList", Api.Method.POST, UrlGenerator.getServicesListUrl(), villageListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getTaxTypeList() {
        try {
            new ApiService(this).makeJSONObjectRequest("TaxTypeList", Api.Method.POST, UrlGenerator.getServicesListUrl(), taxTypeListJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public JSONObject districtListJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_DISTRICT_LIST_ALL);
        Log.d("districtList", "" + dataSet);
        return dataSet;
    }
    public JSONObject taxTypeListJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "TaxTypeList");
        Log.d("taxTypeList", "" + dataSet);
        return dataSet;
    }
    public JSONObject blockListJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_BLOCK_LIST_DISTRICT_WISE);
        dataSet.put(AppConstant.DISTRICT_CODE, dcode);
        Log.d("BlockList", "" + dataSet);
        return dataSet;
    }
    public JSONObject villageListJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, AppConstant.KEY_VILLAGE_LIST_BLOCK_WISE);
        dataSet.put(AppConstant.DISTRICT_CODE, dcode);
        dataSet.put(AppConstant.BLOCK_CODE,bcode);
        Log.d("VillageList", "" + dataSet);
        return dataSet;
    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject responseObj = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();


            if ("DistrictList".equals(urlType) && responseObj != null) {
                if (responseObj.getString("STATUS").equalsIgnoreCase("SUCCESS") && responseObj.getString("RESPONSE").equalsIgnoreCase("SUCCESS")) {
                    new InsertDistrictTask().execute(responseObj);
                }
                Log.d("DistrictList", "" + responseObj);
            }
            if ("BlockList".equals(urlType) && responseObj != null) {

                if (responseObj.getString("STATUS").equalsIgnoreCase("SUCCESS") && responseObj.getString("RESPONSE").equalsIgnoreCase("SUCCESS")) {
                    new InsertBlockTask().execute(responseObj);
                }
                Log.d("BlockList", "" + responseObj);
            }
            if ("VillageList".equals(urlType) && responseObj != null) {

                if (responseObj.getString("STATUS").equalsIgnoreCase("SUCCESS") && responseObj.getString("RESPONSE").equalsIgnoreCase("SUCCESS")) {
                    new InsertVillageTask().execute(responseObj);
                }
                Log.d("VillageList", "" + responseObj);
            }
            if ("TaxTypeList".equals(urlType) && responseObj != null) {
                if (responseObj.getString("STATUS").equalsIgnoreCase("SUCCESS") && responseObj.getString("RESPONSE").equalsIgnoreCase("SUCCESS")) {
                    new Insert_TaxType().execute(responseObj);
                }
                Log.d("TaxTypeList", "" + responseObj);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    @SuppressLint("StaticFieldLeak")
    public class InsertDistrictTask extends AsyncTask<JSONObject ,Void ,Void> {


        @Override
        protected Void doInBackground(JSONObject... params) {

            if (params.length > 0) {
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                District.clear();

                PublicTax district_items = new PublicTax();
                district_items.setDistictCode("0");
                district_items.setDistrictName("Select District");
                District.add(district_items);

                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        String districtCode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                        String districtName = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_NAME);

                        PublicTax district_items1 = new PublicTax();
                        district_items1.setDistictCode(districtCode);
                        district_items1.setDistrictName(districtName);
                        District.add(district_items1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressHUD = ProgressHUD.show(Registration.this, "Downloading", true, false, null);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideProgress();
            demadSearchBinding.districtSpinner.setAdapter(new CommonAdapter(Registration.this,District,"DistrictList"));
        }
    }
    @SuppressLint("StaticFieldLeak")
    public class InsertBlockTask extends AsyncTask<JSONObject ,Void ,Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {

            if (params.length > 0) {
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Block.clear();
                PublicTax block_items = new PublicTax();
                block_items.setDistictCode("0");
                block_items.setBlockCode("0");
                block_items.setBlockName("Select Block");
                Block.add(block_items);
                for (int i = 0; i < jsonArray.length(); i++) {

                    try {
                        String districtCode = jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE);
                        String blockCode = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE);
                        String blockName = jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_NAME);
                        PublicTax block_items1 = new PublicTax();
                        block_items1.setDistictCode(districtCode);
                        block_items1.setBlockCode(blockCode);
                        block_items1.setBlockName(blockName);
                        Block.add(block_items1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressHUD = ProgressHUD.show(Registration.this, "Downloading", true, false, null);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideProgress();
            demadSearchBinding.blockSpinner.setAdapter(new CommonAdapter(Registration.this,Block,"BlockList"));
        }
    }
    @SuppressLint("StaticFieldLeak")
    public class InsertVillageTask extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideProgress();
            demadSearchBinding.villageSpinner.setAdapter(new CommonAdapter(Registration.this,Village,"VillageList"));
        }

        @Override
        protected Void doInBackground(JSONObject... params) {
            if (params.length > 0) {
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = params[0].getJSONArray(AppConstant.DATA);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Village.clear();
                PublicTax village_items = new PublicTax();
                village_items.setDistictCode("0");
                village_items.setBlockCode("0");
                village_items.setPvCode("0");
                village_items.setPvName("Select Village");
                Village.add(village_items);
                for (int i = 0; i < jsonArray.length(); i++) {
                    PublicTax villageListValue = new PublicTax();
                    try {
                        villageListValue.setDistictCode(jsonArray.getJSONObject(i).getString(AppConstant.DISTRICT_CODE));
                        villageListValue.setBlockCode(jsonArray.getJSONObject(i).getString(AppConstant.BLOCK_CODE));
                        villageListValue.setPvCode(jsonArray.getJSONObject(i).getString(AppConstant.PV_CODE));
                        villageListValue.setPvName(jsonArray.getJSONObject(i).getString(AppConstant.PV_NAME));

                        Village.add(villageListValue);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
            return null;
        }

    }
    @SuppressLint("StaticFieldLeak")
    public class Insert_TaxType extends AsyncTask<JSONObject, Void, ArrayList<PublicTax>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

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
                taxTypeList.clear();
                PublicTax tax_items = new PublicTax();
                tax_items.setTaxtypeid("0");
                tax_items.setTaxtypedesc_en("Select TaxType");
                taxTypeList.add(tax_items);
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
            hideProgress();
            demadSearchBinding.taxTypeSpinner.setAdapter(new CommonAdapter(Registration.this,taxTypeList,"TaxTypeList"));

        }
    }
    public void showProgress() {
        try {
            progressHUD = ProgressHUD.show(Registration.this, "Loading...", true, false, null);
        } catch (Exception e) {
        }

    }

    void hideProgress() {
        try {
            if (progressHUD != null)
                progressHUD.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showDashBoardScreen() {
        Intent intent = new Intent(Registration.this, Dashboard.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
