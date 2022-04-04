package com.nic.nutrigarden.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.nic.nutrigarden.Fragment.SlideshowDialogFragment;
import com.nic.nutrigarden.R;
import com.nic.nutrigarden.adapter.FullImageAdapter;
import com.nic.nutrigarden.api.Api;
import com.nic.nutrigarden.api.ApiService;
import com.nic.nutrigarden.api.ServerResponse;
import com.nic.nutrigarden.constant.AppConstant;
import com.nic.nutrigarden.dataBase.dbData;
import com.nic.nutrigarden.databinding.FullImageRecyclerBinding;
import com.nic.nutrigarden.model.PMAYSurvey;
import com.nic.nutrigarden.session.PrefManager;
import com.nic.nutrigarden.utils.UrlGenerator;
import com.nic.nutrigarden.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FullImageActivity extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener {
    private FullImageRecyclerBinding fullImageRecyclerBinding;
    private FullImageAdapter fullImageAdapter;
    private PrefManager prefManager;
    private static  ArrayList<PMAYSurvey> activityImage = new ArrayList<>();
    private com.nic.nutrigarden.dataBase.dbData dbData = new dbData(this);
    ArrayList<PMAYSurvey> imageListTree;

    String fin_year="";
    int shg_code=0;
    int shg_member_code=0;
    int work_type_id=0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullImageRecyclerBinding = DataBindingUtil.setContentView(this, R.layout.full_image_recycler);
        fullImageRecyclerBinding.setActivity(this);
        prefManager = new PrefManager(this);
//        work_id = ;

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        fullImageRecyclerBinding.imagePreviewRecyclerview.setLayoutManager(mLayoutManager);
        fullImageRecyclerBinding.imagePreviewRecyclerview.setItemAnimator(new DefaultItemAnimator());
        fullImageRecyclerBinding.imagePreviewRecyclerview.setHasFixedSize(true);
        fullImageRecyclerBinding.imagePreviewRecyclerview.setNestedScrollingEnabled(false);
        fullImageRecyclerBinding.imagePreviewRecyclerview.setFocusable(false);
        fullImageRecyclerBinding.imagePreviewRecyclerview.setAdapter(fullImageAdapter);
        if(getIntent().getStringExtra("On_Off_Type").equals("Online")){
            fin_year = getIntent().getStringExtra("fin_year");
            shg_code = getIntent().getIntExtra("shg_code",0);
            shg_member_code = getIntent().getIntExtra("shg_member_code",0);
            work_type_id = getIntent().getIntExtra("work_type_id",0);
            if(Utils.isOnline()){
                 getOnlineImage();
            }
        }
        else {
            shg_code = getIntent().getIntExtra("shg_code",0);
            shg_member_code = getIntent().getIntExtra("shg_member_code",0);
            new fetchImagetask().execute();
        }



    }
    public class fetchImagetask extends AsyncTask<Void, Void,
            ArrayList<PMAYSurvey>> {
        @Override
        protected ArrayList<PMAYSurvey> doInBackground(Void... params) {

            dbData.open();
            activityImage = new ArrayList<>();
            ArrayList<PMAYSurvey> getBeforeImageDetail = dbData.getParticular_Before_Save_Tree_Image_Table(shg_code,shg_member_code);
            ArrayList<PMAYSurvey> getAfterImageDetail = dbData.getParticular_After_Save_Tree_Image_Table(shg_code,shg_member_code);

            imageListTree = new ArrayList<>();
            PMAYSurvey imageTree = new PMAYSurvey();
            PMAYSurvey imageTree1 = new PMAYSurvey();
            imageTree.setImage(getBeforeImageDetail.get(0).getBefore_photo());
            imageTree1.setImage(getAfterImageDetail.get(0).getAfter_photo());
            imageListTree.add(imageTree);
            imageListTree.add(imageTree1);

            Log.d("IMAGE_COUNT", String.valueOf(imageListTree.size()));
            return imageListTree;
        }

        @Override
        protected void onPostExecute(final ArrayList<PMAYSurvey> imageList) {
            super.onPostExecute(imageList);
            setAdapter();
        }
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

    @Override
    public void onClick(View view) {

    }

    public void getOnlineImage() {
        try {
            new ApiService(this).makeJSONObjectRequest("OnlineImage", Api.Method.POST, UrlGenerator.getPMAYListUrl(), ImagesJsonParams(), "not cache", this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONObject ImagesJsonParams() throws JSONException {
        String authKey = Utils.encrypt(prefManager.getUserPassKey(), getResources().getString(R.string.init_vector), ImagesListJsonParams().toString());
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        dataSet.put(AppConstant.DATA_CONTENT, authKey);
        Log.d("utils_ImageEncrydataSet", "" + authKey);
        return dataSet;
    }

    public JSONObject ImagesListJsonParams() throws JSONException {
        JSONObject dataSet = new JSONObject();
        dataSet.put(AppConstant.KEY_SERVICE_ID, "details_of_nutri_garden_photos_view");
        dataSet.put("fin_year", fin_year);
        dataSet.put("self_help_group_code", shg_code);
        dataSet.put("self_help_group_member_code", shg_member_code);
        dataSet.put("work_type_id", work_type_id);
        Log.d("utils_imageDataset", "" + dataSet);
        return dataSet;
    }

    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            String urlType = serverResponse.getApi();
            JSONObject responseObj = serverResponse.getJsonResponse();

            if ("OnlineImage".equals(urlType) && responseObj != null) {
                String key = responseObj.getString(AppConstant.ENCODE_DATA);
                String responseDecryptedBlockKey = Utils.decrypt(prefManager.getUserPassKey(), key);
                JSONObject jsonObject = new JSONObject(responseDecryptedBlockKey);
                if (jsonObject.getString("STATUS").equalsIgnoreCase("OK") && jsonObject.getString("RESPONSE").equalsIgnoreCase("OK")) {
                    generateImageArrayList(jsonObject.getJSONArray(AppConstant.JSON_DATA));
                }
                Log.d("resp_OnlineImage", "" + responseDecryptedBlockKey);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void generateImageArrayList(JSONArray jsonArray){
        if(jsonArray.length() > 0){
            imageListTree = new ArrayList<>();
            for(int i = 0; i < jsonArray.length(); i++ ) {
                try {
                    PMAYSurvey imageOnline = new PMAYSurvey();
                    PMAYSurvey imageOnline1 = new PMAYSurvey();

                    byte[] before_decodedString = Base64.decode(jsonArray.getJSONObject(i).getString("before_plant_image"), Base64.DEFAULT);
                    Bitmap before_decodedByte = BitmapFactory.decodeByteArray(before_decodedString, 0, before_decodedString.length);
                    imageOnline.setImage(before_decodedByte);
                    byte[] after_decodedString = Base64.decode(jsonArray.getJSONObject(i).getString("after_plant_image"), Base64.DEFAULT);
                    Bitmap after_decodedByte = BitmapFactory.decodeByteArray(after_decodedString, 0, after_decodedString.length);
                    imageOnline1.setImage(after_decodedByte);
                    imageListTree.add(imageOnline);
                    imageListTree.add(imageOnline1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            setAdapter();
        }
    }
    public void setAdapter(){
        fullImageAdapter = new FullImageAdapter(FullImageActivity.this,
                imageListTree, dbData);
        fullImageRecyclerBinding.imagePreviewRecyclerview.addOnItemTouchListener(new FullImageAdapter.RecyclerTouchListener(getApplicationContext(), fullImageRecyclerBinding.imagePreviewRecyclerview, new FullImageAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", imageListTree);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        fullImageRecyclerBinding.imagePreviewRecyclerview.setAdapter(fullImageAdapter);
    }



    @Override
    public void OnError(VolleyError volleyError) {

    }
}
