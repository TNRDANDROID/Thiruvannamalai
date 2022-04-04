package com.nic.nutrigarden.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.nic.nutrigarden.R;
import com.nic.nutrigarden.adapter.CommonAdapter;
import com.nic.nutrigarden.adapter.NutriGardernDetailsServerAdapter;
import com.nic.nutrigarden.api.Api;
import com.nic.nutrigarden.api.ApiService;
import com.nic.nutrigarden.api.ServerResponse;
import com.nic.nutrigarden.constant.AppConstant;
import com.nic.nutrigarden.dataBase.dbData;
import com.nic.nutrigarden.databinding.ActivityViewNutriGardenBinding;
import com.nic.nutrigarden.model.PMAYSurvey;
import com.nic.nutrigarden.session.PrefManager;
import com.nic.nutrigarden.utils.UrlGenerator;
import com.nic.nutrigarden.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class ViewNutriGarden extends AppCompatActivity implements Api.ServerResponseListener{

    ActivityViewNutriGardenBinding nutriGardenBinding;
    private PrefManager prefManager;
    public com.nic.nutrigarden.dataBase.dbData dbData = new dbData(this);
    String fin_year="";
    String shg_name="";
    String member_name="";
    String tree_name="";
    int self_Group_code=0;
    int self_Group_member_code=0;
    int tree_type_code=0;

    ArrayList<PMAYSurvey> self_GroupList;
    ArrayList<PMAYSurvey> fin_Year_List;
    ArrayList<PMAYSurvey> nutriGarednDetails;

    NutriGardernDetailsServerAdapter nutriGardernDetailsServerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nutriGardenBinding = DataBindingUtil.setContentView(this, R.layout.activity_view_nutri_garden);
        nutriGardenBinding.setActivity(this);
        prefManager = new PrefManager(this);
        setAllAdapter();

        nutriGardenBinding.finYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    fin_year = fin_Year_List.get(nutriGardenBinding.finYearSpinner.getSelectedItemPosition()).getFin_year();
                }
                else {
                    fin_year="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        nutriGardenBinding.selfGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    self_Group_code = self_GroupList.get(nutriGardenBinding.selfGroupSpinner.getSelectedItemPosition()).getShg_code();
                    shg_name = self_GroupList.get(nutriGardenBinding.selfGroupSpinner.getSelectedItemPosition()).getShg_name();
                    getdetails_of_nutri_garden_view();
                }
                else {
                    self_Group_code=0;
                    self_Group_member_code=0;
                    shg_name="";

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        nutriGardenBinding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setAllAdapter(){
        loadFinYearTable();
        loadSelfGroupTable();

    }

    public void loadSelfGroupTable(){
        self_GroupList = new ArrayList<>();
        dbData.open();
        PMAYSurvey self_group_list_item = new PMAYSurvey();
        self_group_list_item.setShg_code(0);
        self_group_list_item.setShg_name(getResources().getString(R.string.select_self_help_group));
        self_GroupList.add(self_group_list_item);
        self_GroupList.addAll(dbData.getAll_Master_Self_Help_Group());

        nutriGardenBinding.selfGroupSpinner.setAdapter(new CommonAdapter(this,self_GroupList,"self_GroupList"));

    }
    public void loadFinYearTable(){
        fin_Year_List = new ArrayList<>();
        dbData.open();
        PMAYSurvey fin_Year_List_item = new PMAYSurvey();
        fin_Year_List_item.setFin_year(getResources().getString(R.string.select_fin_year));
        fin_Year_List.add(fin_Year_List_item);
        fin_Year_List.addAll(dbData.getAll_Master_Fin_Year());

        nutriGardenBinding.finYearSpinner.setAdapter(new CommonAdapter(this,fin_Year_List,"fin_Year_List"));

    }

    public void getdetails_of_nutri_garden_view() {
        try {
            new ApiService(this).makeJSONObjectRequest("details_of_nutri_garden_view", Api.Method.POST, UrlGenerator.getPMAYListUrl(), details_of_nutri_garden_viewParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject details_of_nutri_garden_viewParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), details_of_nutri_garden_viewNormalParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("master_listJsonParams", "" + authKey);
        return dataSet;
    }
    public  JSONObject details_of_nutri_garden_viewNormalParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "details_of_nutri_garden_view");
        dataSet.put("fin_year", fin_year);
        dataSet.put("self_help_group_code", self_Group_code);
        Log.d("nutri_garden_master", "" + dataSet);
        return dataSet;
    }


    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject loginResponse = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();

            if ("details_of_nutri_garden_view".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                        new InsertNutriGartenDetails().execute(jsonObject);
                }
                Log.d("nutri_garden_view", "" + responseDecryptedBlockKey);
            }
            if ("DeleteTreeJson".equals(urlType) && loginResponse != null) {
                String key = loginResponse.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    Toasty.success(this, jsonObject.getString("MESSAGE"), Toast.LENGTH_LONG, true).show();
                    getdetails_of_nutri_garden_view();
                }if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("NO_RECORD")) {
                    Toasty.success(this, jsonObject.getString("MESSAGE"), Toast.LENGTH_LONG, true).show();
                    getdetails_of_nutri_garden_view();
                }
                Log.d("DeleteTreeJson", "" + responseDecryptedBlockKey);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public class InsertNutriGartenDetails extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {

                if (params.length > 0) {
                    JSONArray jsonArray = new JSONArray();
                    try {
                        jsonArray = params[0].getJSONArray(AppConstant.JSON_DATA);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    nutriGarednDetails = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        PMAYSurvey nutriGardenDetails = new PMAYSurvey();
                        try {
                            nutriGardenDetails.setFin_year(jsonArray.getJSONObject(i).getString("fin_year"));
                            nutriGardenDetails.setShg_code(jsonArray.getJSONObject(i).getInt("shg_code"));
                            nutriGardenDetails.setShg_name(jsonArray.getJSONObject(i).getString("shg_name"));
                            nutriGardenDetails.setShg_member_code(jsonArray.getJSONObject(i).getInt("shg_member_code"));
                            nutriGardenDetails.setMember_name(jsonArray.getJSONObject(i).getString("shg_member_name"));
                            nutriGardenDetails.setWork_code(jsonArray.getJSONObject(i).getInt("work_type_id"));
                            nutriGardenDetails.setWork_name(jsonArray.getJSONObject(i).getString("work_name"));
                            nutriGarednDetails.add(nutriGardenDetails);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(nutriGarednDetails.size()>0){
                nutriGardenBinding.pendingList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                nutriGardenBinding.pendingList.setVisibility(View.VISIBLE);
                nutriGardernDetailsServerAdapter = new NutriGardernDetailsServerAdapter(ViewNutriGarden.this,nutriGarednDetails,dbData);
                nutriGardenBinding.pendingList.setAdapter(nutriGardernDetailsServerAdapter);
            }
            else {
                nutriGardenBinding.pendingList.setVisibility(View.GONE);
            }
        }
    }

    public JSONObject DeleteTreeJson(JSONObject treeDetails) {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), treeDetails.toString());
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
            dataSet.put(AppConstant.DATA_CONTENT, authKey);

            new ApiService(this).makeJSONObjectRequest("DeleteTreeJson", Api.Method.POST, UrlGenerator.getPMAYListUrl(), dataSet, "not cache", this);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("DeleteTreeJson", "" + authKey);
        return dataSet;
    }


}
