package com.nic.nutrigarden.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.nic.nutrigarden.R;
import com.nic.nutrigarden.adapter.NewPendingAdapter;
import com.nic.nutrigarden.adapter.PendingAdapter;
import com.nic.nutrigarden.api.Api;
import com.nic.nutrigarden.api.ApiService;
import com.nic.nutrigarden.api.ServerResponse;
import com.nic.nutrigarden.constant.AppConstant;
import com.nic.nutrigarden.dataBase.DBHelper;
import com.nic.nutrigarden.dataBase.dbData;
import com.nic.nutrigarden.databinding.ActivityNewPendingScreenBinding;
import com.nic.nutrigarden.databinding.PendingScreenBinding;
import com.nic.nutrigarden.model.PMAYSurvey;
import com.nic.nutrigarden.session.PrefManager;
import com.nic.nutrigarden.utils.UrlGenerator;
import com.nic.nutrigarden.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static com.nic.nutrigarden.activity.HomePage.db;

public class NewPendingScreen extends AppCompatActivity implements Api.ServerResponseListener {
    public ActivityNewPendingScreenBinding pendingScreenBinding;
    private RecyclerView recyclerView;
    private NewPendingAdapter pendingAdapter;
    private PrefManager prefManager;
    public com.nic.nutrigarden.dataBase.dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    private Activity context;

    String shg_code="";
    String shg_member_code="";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pendingScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_pending_screen);
        pendingScreenBinding.setActivity(this);
        context = this;
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        recyclerView = pendingScreenBinding.pendingList;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        new fetchPendingtask().execute();
    }



    public class fetchPendingtask extends AsyncTask<Void, Void,
            ArrayList<PMAYSurvey>> {
        @Override
        protected ArrayList<PMAYSurvey> doInBackground(Void... params) {
            dbData.open();
            ArrayList<PMAYSurvey> pmaySurveys = new ArrayList<>();
            pmaySurveys = dbData.getAllTreeImages();
            Log.d("Tree_COUNT", String.valueOf(pmaySurveys.size()));
            return pmaySurveys;
        }

        @Override
        protected void onPostExecute(ArrayList<PMAYSurvey> treeList) {
            super.onPostExecute(treeList);
            recyclerView.setVisibility(View.VISIBLE);
            pendingAdapter = new NewPendingAdapter(NewPendingScreen.this, treeList,dbData);
            recyclerView.setAdapter(pendingAdapter);
        }
    }



    public JSONObject saveTreeJson(JSONObject savePMAYDataSet, String shg_code_, String shg_member_code_) {
        shg_code = shg_code_;
        shg_member_code=shg_member_code_;
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), savePMAYDataSet.toString());
        JSONObject dataSet = new JSONObject();
        try {
            dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
            dataSet.put(AppConstant.DATA_CONTENT, authKey);

            new ApiService(this).makeJSONObjectRequest("saveTreeImages", Api.Method.POST, UrlGenerator.getPMAYListUrl(), dataSet, "not cache", this);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("saveTreeImages", "" + authKey);
        return dataSet;
    }
    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("saveTreeImages".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    Utils.showAlert(this, "Uploaded");
                    String whereClause = "shg_code = ? and shg_member_code = ?";
                    String[] whereArgs = new String[]{shg_code, shg_member_code};
                    int sdsm = db.delete(DBHelper.SAVE_BEFORE_TREE_IMAGE_TABLE, whereClause, whereArgs);
                    int sdsm1 = db.delete(DBHelper.SAVE_AFTER_TREE_IMAGE_TABLE, whereClause, whereArgs);

                    new fetchPendingtask().execute();
                    pendingAdapter.notifyDataSetChanged();
                }
                else if(jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("FAIL")){
                    Toasty.error(this, jsonObject.getString("MESSAGE"), Toast.LENGTH_LONG, true).show();
                   /* db.delete(DBHelper.SAVE_PMAY_DETAILS,"id = ?",new String[] {prefManager.getKeyDeleteId()});
                    db.delete(DBHelper.SAVE_PMAY_IMAGES, "pmay_id = ? ", new String[]{prefManager.getKeyDeleteId()});
                    new fetchPendingtask().execute();
                    pendingAdapter.notifyDataSetChanged();*/
                }
                Log.d("saved_response", "" + responseDecryptedBlockKey);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void OnError(VolleyError volleyError) {

    }

    public void homePage() {
        Intent intent = new Intent(this, MainPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Home", "Home");
        startActivity(intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    public void onBackPress() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }
}
