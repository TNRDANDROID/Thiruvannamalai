package com.nic.nutrigarden.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nic.nutrigarden.R;
import com.nic.nutrigarden.adapter.CommonAdapter;
import com.nic.nutrigarden.dataBase.DBHelper;
import com.nic.nutrigarden.dataBase.dbData;
import com.nic.nutrigarden.databinding.ActivityNewHomePageBinding;
import com.nic.nutrigarden.model.PMAYSurvey;
import com.nic.nutrigarden.session.PrefManager;
import com.nic.nutrigarden.support.MyLocationListener;
import com.nic.nutrigarden.utils.CameraUtils;
import com.nic.nutrigarden.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;

public class NewHomePage extends AppCompatActivity {

    ActivityNewHomePageBinding newHomePageBinding;
    private PrefManager prefManager;
    public com.nic.nutrigarden.dataBase.dbData dbData = new dbData(this);
    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    ArrayList<PMAYSurvey> self_GroupList;
    ArrayList<PMAYSurvey> self_Group_Member_List;
    ArrayList<PMAYSurvey> fin_Year_List;
    ArrayList<PMAYSurvey> type_tree_List;

    String fin_year="";
    String shg_name="";
    String member_name="";
    String tree_name="";
    int self_Group_code=0;
    int self_Group_member_code=0;
    int tree_type_code=0;

    ///Capture Image Part
    Uri mImageUri;
    private static String imageStoragePath;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int BITMAP_SAMPLE_SIZE = 8;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 2500;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    private static final int PERMISSION_REQUEST_CODE = 200;
    LocationManager mlocManager = null;
    LocationListener mlocListener;
    Double beforeofflatTextValue=0.0, beforeofflongTextValue=0.0;
    Double afterofflatTextValue=0.0, afterofflongTextValue=0.0;

    String image_flag="";

    byte[] before_image_byte;
    byte[] after_image_byte;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newHomePageBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_home_page);
        newHomePageBinding.setActivity(this);
        prefManager = new PrefManager(this);
        try {
            dbHelper = new DBHelper(this);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setAllAdapter();
        newHomePageBinding.finYearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    fin_year = fin_Year_List.get(newHomePageBinding.finYearSpinner.getSelectedItemPosition()).getFin_year();
                }
                else {
                    fin_year="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        newHomePageBinding.selfGroupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    self_Group_code = self_GroupList.get(newHomePageBinding.selfGroupSpinner.getSelectedItemPosition()).getShg_code();
                    shg_name = self_GroupList.get(newHomePageBinding.selfGroupSpinner.getSelectedItemPosition()).getShg_name();
                    loadSelfGroupMemberTable();
                }
                else {
                    self_Group_code=0;
                    self_Group_member_code=0;
                    shg_name="";
                    newHomePageBinding.groupMemberSpinner.setAdapter(null);

                    self_Group_member_code=0;
                    member_name="";
                    newHomePageBinding.beforeCaptureIcon.setVisibility(View.VISIBLE);
                    newHomePageBinding.beforeImage.setVisibility(View.GONE);
                    before_image_byte=new byte[0];
                    newHomePageBinding.beforeImage.setImageBitmap(null);
                    newHomePageBinding.beforeTakenIcon.setVisibility(View.GONE);
                    beforeofflatTextValue = 0.0;
                    beforeofflongTextValue = 0.0;
                    newHomePageBinding.typeTreeSpinner.setSelection(0);

                    newHomePageBinding.afterCaptureIcon.setVisibility(View.VISIBLE);
                    newHomePageBinding.afterCaptureImage.setVisibility(View.GONE);
                    after_image_byte=new byte[0];
                    newHomePageBinding.afterCaptureImage.setImageBitmap(null);
                    newHomePageBinding.afterTakenIcon.setVisibility(View.GONE);
                    afterofflatTextValue = 0.0;
                    afterofflongTextValue = 0.0;
                    newHomePageBinding.typeTreeSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        newHomePageBinding.groupMemberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    self_Group_member_code = self_Group_Member_List.get(newHomePageBinding.groupMemberSpinner.getSelectedItemPosition()).getShg_member_code();
                    member_name = self_Group_Member_List.get(newHomePageBinding.groupMemberSpinner.getSelectedItemPosition()).getMember_name();
                    ArrayList<PMAYSurvey> getBeforeImageDetail = dbData.getParticular_Before_Save_Tree_Image_Table(self_Group_code,self_Group_member_code);
                    ArrayList<PMAYSurvey> getAfterImageDetail = dbData.getParticular_After_Save_Tree_Image_Table(self_Group_code,self_Group_member_code);
                    if(getBeforeImageDetail.size()>0){
                        newHomePageBinding.beforeCaptureIcon.setVisibility(View.GONE);
                        newHomePageBinding.beforeImage.setVisibility(View.VISIBLE);
                        before_image_byte=get_image_byte(getBeforeImageDetail.get(0).getBefore_photo());
                        newHomePageBinding.beforeImage.setImageBitmap(getBeforeImageDetail.get(0).getBefore_photo());
                        newHomePageBinding.beforeTakenIcon.setVisibility(View.VISIBLE);

                        beforeofflatTextValue = Double.valueOf(getBeforeImageDetail.get(0).getBefore_photo_lat());
                        beforeofflongTextValue = Double.valueOf(getBeforeImageDetail.get(0).getBefore_photo_long());

                        newHomePageBinding.typeTreeSpinner.setSelection(getBeforeImageDetail.get(0).getWork_code());
                    }
                    else {
                        newHomePageBinding.beforeCaptureIcon.setVisibility(View.VISIBLE);
                        newHomePageBinding.beforeImage.setVisibility(View.GONE);
                        before_image_byte=new byte[0];
                        newHomePageBinding.beforeImage.setImageBitmap(null);
                        newHomePageBinding.beforeTakenIcon.setVisibility(View.GONE);
                        beforeofflatTextValue = 0.0;
                        beforeofflongTextValue = 0.0;
                        newHomePageBinding.typeTreeSpinner.setSelection(0);
                    }
                    if(getAfterImageDetail.size()>0){
                        newHomePageBinding.afterCaptureIcon.setVisibility(View.GONE);
                        newHomePageBinding.afterCaptureImage.setVisibility(View.VISIBLE);
                        after_image_byte=get_image_byte(getAfterImageDetail.get(0).getAfter_photo());
                        newHomePageBinding.afterCaptureImage.setImageBitmap(getAfterImageDetail.get(0).getAfter_photo());
                        newHomePageBinding.afterTakenIcon.setVisibility(View.VISIBLE);

                        afterofflatTextValue = Double.valueOf(getAfterImageDetail.get(0).getAfter_photo_lat());
                        afterofflongTextValue = Double.valueOf(getAfterImageDetail.get(0).getAfter_photo_long());

                        newHomePageBinding.typeTreeSpinner.setSelection(getAfterImageDetail.get(0).getWork_code());
                    }
                    else {
                        newHomePageBinding.afterCaptureIcon.setVisibility(View.VISIBLE);
                        newHomePageBinding.afterCaptureImage.setVisibility(View.GONE);
                        after_image_byte=new byte[0];
                        newHomePageBinding.afterCaptureImage.setImageBitmap(null);
                        newHomePageBinding.afterTakenIcon.setVisibility(View.GONE);
                        afterofflatTextValue = 0.0;
                        afterofflongTextValue = 0.0;
                        newHomePageBinding.typeTreeSpinner.setSelection(0);
                    }

                }
                else {
                    self_Group_member_code=0;
                    member_name="";
                    newHomePageBinding.beforeCaptureIcon.setVisibility(View.VISIBLE);
                    newHomePageBinding.beforeImage.setVisibility(View.GONE);
                    before_image_byte=new byte[0];
                    newHomePageBinding.beforeImage.setImageBitmap(null);
                    newHomePageBinding.beforeTakenIcon.setVisibility(View.GONE);
                    beforeofflatTextValue = 0.0;
                    beforeofflongTextValue = 0.0;
                    newHomePageBinding.typeTreeSpinner.setSelection(0);

                    newHomePageBinding.afterCaptureIcon.setVisibility(View.VISIBLE);
                    newHomePageBinding.afterCaptureImage.setVisibility(View.GONE);
                    after_image_byte=new byte[0];
                    newHomePageBinding.afterCaptureImage.setImageBitmap(null);
                    newHomePageBinding.afterTakenIcon.setVisibility(View.GONE);
                    afterofflatTextValue = 0.0;
                    afterofflongTextValue = 0.0;
                    newHomePageBinding.typeTreeSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        newHomePageBinding.typeTreeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    tree_type_code = type_tree_List.get(newHomePageBinding.typeTreeSpinner.getSelectedItemPosition()).getWork_code();
                    tree_name = type_tree_List.get(newHomePageBinding.typeTreeSpinner.getSelectedItemPosition()).getWork_name();
                }
                else {
                    tree_type_code=0;
                    tree_name="";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        newHomePageBinding.beforeCaptureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beforeCaptureValidation();
            }
        });
        newHomePageBinding.afterCaptureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                afterCaptureValidation();
            }
        });
        newHomePageBinding.saveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveValidation();
            }
        });

        newHomePageBinding.beforeTakenIcon.setVisibility(View.GONE);
        newHomePageBinding.afterTakenIcon.setVisibility(View.GONE);

        newHomePageBinding.viewImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNutriGardenDetails();
            }
        });

    }

    public void setAllAdapter(){
        loadFinYearTable();
        loadSelfGroupTable();
        loadTypeOfTreeTable();
    }
    public void loadSelfGroupTable(){
        self_GroupList = new ArrayList<>();
        dbData.open();
        PMAYSurvey self_group_list_item = new PMAYSurvey();
        self_group_list_item.setShg_code(0);
        self_group_list_item.setShg_name(getResources().getString(R.string.select_self_help_group));
        self_GroupList.add(self_group_list_item);
        self_GroupList.addAll(dbData.getAll_Master_Self_Help_Group());

        newHomePageBinding.selfGroupSpinner.setAdapter(new CommonAdapter(this,self_GroupList,"self_GroupList"));

    }
    public void loadFinYearTable(){
        fin_Year_List = new ArrayList<>();
        dbData.open();
        PMAYSurvey fin_Year_List_item = new PMAYSurvey();
        fin_Year_List_item.setFin_year(getResources().getString(R.string.select_fin_year));
        fin_Year_List.add(fin_Year_List_item);
        fin_Year_List.addAll(dbData.getAll_Master_Fin_Year());

        newHomePageBinding.finYearSpinner.setAdapter(new CommonAdapter(this,fin_Year_List,"fin_Year_List"));

    }
    public void loadTypeOfTreeTable(){
        type_tree_List = new ArrayList<>();
        dbData.open();
        PMAYSurvey type_tree_List_item = new PMAYSurvey();
        type_tree_List_item.setWork_code(0);
        type_tree_List_item.setWork_name(getResources().getString(R.string.select_type_of_tree));
        type_tree_List.add(type_tree_List_item);
        type_tree_List.addAll(dbData.getAll_Master_Work_Type());

        newHomePageBinding.typeTreeSpinner.setAdapter(new CommonAdapter(this,type_tree_List,"type_tree_List"));

    }
    public void loadSelfGroupMemberTable(){
        self_Group_Member_List = new ArrayList<>();
        dbData.open();
        PMAYSurvey self_Group_Member_List_item = new PMAYSurvey();
        self_Group_Member_List_item.setShg_code(0);
        self_Group_Member_List_item.setShg_member_code(0);
        self_Group_Member_List_item.setMember_name(getResources().getString(R.string.select_self_help_group_member));
        self_Group_Member_List.add(self_Group_Member_List_item);
        self_Group_Member_List.addAll(dbData.getAll_Master_Self_Help_Group_Member(self_Group_code));

        newHomePageBinding.groupMemberSpinner.setAdapter(new CommonAdapter(this,self_Group_Member_List,"self_Group_Member_List"));

    }


    public void getLatLong() {
        mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);

        //API level 9 and up
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        Integer gpsFreqInMillis = 1000;
        Integer gpsFreqInDistance = 1;


        // permission was granted, yay! Do the
        // location-related task you need to do.
        if (ContextCompat.checkSelfPermission(NewHomePage.this,
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            //Request location updates:
            //mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
            mlocManager.requestLocationUpdates(gpsFreqInMillis, gpsFreqInDistance, criteria, mlocListener, null);

        }

        if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(NewHomePage.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(NewHomePage.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    requestPermissions(new String[]{CAMERA, ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            } else {
                if (ActivityCompat.checkSelfPermission(NewHomePage.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(NewHomePage.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(NewHomePage.this, new String[]{ACCESS_FINE_LOCATION}, 1);

                }
            }
            if (MyLocationListener.latitude > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (CameraUtils.checkPermissions(NewHomePage.this)) {
                        captureImage();
                    } else {
                        requestCameraPermission(MEDIA_TYPE_IMAGE);
                    }
//                            checkPermissionForCamera();
                } else {
                    captureImage();
                }
            } else {
                Utils.showAlert(NewHomePage.this, getResources().getString(R.string.satellite));
            }
        } else {
            Utils.showAlert(NewHomePage.this, getResources().getString(R.string.gps_is_not_turned_on));
        }
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
        builder.setTitle(getResources().getString(R.string.permission_required))
                .setMessage(getResources().getString(R.string.camera_need_permission))
                .setPositiveButton(getResources().getString(R.string.goto_settings), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        CameraUtils.openSettings(NewHomePage.this);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }



    public byte[] get_image_byte(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
       // bmp.recycle();
        return  byteArray;
    }

    public void beforeCaptureValidation(){
        if(!fin_year.equals("")){
            if(self_Group_code!=0){
                if(self_Group_member_code!=0){
                    if(tree_type_code!=0){
                        image_flag="before";
                        getLatLong();
                    }
                    else {
                        Utils.showAlert(this,getResources().getString(R.string.select_type_of_tree));
                    }
                }
                else {
                    Utils.showAlert(this,getResources().getString(R.string.select_self_help_group_member));
                }
            }
            else {
                Utils.showAlert(this,getResources().getString(R.string.select_self_help_group));
            }
        }
        else {
            Utils.showAlert(this,getResources().getString(R.string.select_fin_year));
        }
    }
    public void afterCaptureValidation(){
        if(!fin_year.equals("")){
            if(self_Group_code!=0){
                if(self_Group_member_code!=0){
                    if(tree_type_code!=0){
                       if(newHomePageBinding.beforeImage.getDrawable()!=null){
                           image_flag="after";
                           getLatLong();
                       }
                       else {
                           Utils.showAlert(this,"Please Capture Before Image");
                       }
                    }
                    else {
                        Utils.showAlert(this,getResources().getString(R.string.select_type_of_tree));
                    }
                }
                else {
                    Utils.showAlert(this,getResources().getString(R.string.select_self_help_group_member));
                }
            }
            else {
                Utils.showAlert(this,getResources().getString(R.string.select_self_help_group));
            }
        }
        else {
            Utils.showAlert(this,getResources().getString(R.string.select_fin_year));
        }
    }
    public void saveValidation(){
        if(!fin_year.equals("")){
            if(self_Group_code!=0){
                if(self_Group_member_code!=0){
                    if(tree_type_code!=0){
                        if(beforeofflatTextValue!=0.0&&beforeofflongTextValue!=0.0){
                            if(afterofflatTextValue!=0.0&&afterofflongTextValue!=0.0){
                                saveLocally("after_tree");
                            }
                            else {
                                Utils.showAlert(this,"Please Capture After Image");
                            }
                        }
                        else {
                            Utils.showAlert(this,"Please Capture Before Image");
                        }
                    }
                    else {
                        Utils.showAlert(this,getResources().getString(R.string.select_type_of_tree));
                    }
                }
                else {
                    Utils.showAlert(this,getResources().getString(R.string.select_self_help_group_member));
                }
            }
            else {
                Utils.showAlert(this,getResources().getString(R.string.select_self_help_group));
            }
        }
        else {
            Utils.showAlert(this,getResources().getString(R.string.select_fin_year));
        }
    }

    public void saveLocally(String type){
        dbData.open();
        String whereClause = "";String[] whereArgs = null;
        try {
            ContentValues beforevalues = new ContentValues();
            ContentValues aftervalues = new ContentValues();
            beforevalues.put("fin_year",fin_year);
            beforevalues.put("shg_code",self_Group_code);
            beforevalues.put("shg_member_code",self_Group_member_code);
            beforevalues.put("work_code",tree_type_code);
            beforevalues.put("shg_name",shg_name);
            beforevalues.put("member_name",member_name);
            beforevalues.put("work_name",tree_name);
            beforevalues.put("before_photo",before_image_byte);
            beforevalues.put("before_photo_lat",beforeofflatTextValue);
            beforevalues.put("before_photo_long",beforeofflongTextValue);

            aftervalues.put("fin_year",fin_year);
            aftervalues.put("shg_code",self_Group_code);
            aftervalues.put("shg_member_code",self_Group_member_code);
            aftervalues.put("work_code",tree_type_code);
            aftervalues.put("shg_name",shg_name);
            aftervalues.put("member_name",member_name);
            aftervalues.put("work_name",tree_name);
            aftervalues.put("after_photo",after_image_byte);
            aftervalues.put("after_photo_lat",afterofflatTextValue);
            aftervalues.put("after_photo_long",afterofflongTextValue);
            if(type.equals("before_tree")){

                ArrayList<PMAYSurvey> imagCount = dbData.getParticular_Before_Save_Tree_Image_Table(self_Group_code,self_Group_member_code);
                if(imagCount.size()>0){
                    whereClause = "shg_code = ? and shg_member_code = ?";
                    whereArgs = new String[]{String.valueOf(self_Group_code), String.valueOf(self_Group_member_code)};
                    //  long insert_id = db.insert(DBHelper.SAVE_TREE_IMAGE_TABLE,null,values);
                    long update_id = db.update(DBHelper.SAVE_BEFORE_TREE_IMAGE_TABLE,beforevalues,whereClause,whereArgs);
                    if(update_id>0){
                        Toasty.success(this, "Updated Success", Toast.LENGTH_LONG, true).show();
                    }
                }
                else {
                    long insert_id = db.insert(DBHelper.SAVE_BEFORE_TREE_IMAGE_TABLE,null,beforevalues);
                    if(insert_id>0){
                        Toasty.success(this, "Inserted Success", Toast.LENGTH_LONG, true).show();
                    }
                }
            }
            else if(type.equals("after_tree")){

                ArrayList<PMAYSurvey> imagCount1 = dbData.getParticular_Before_Save_Tree_Image_Table(self_Group_code,self_Group_member_code);
                if(imagCount1.size()>0){
                    whereClause = "shg_code = ? and shg_member_code = ?";
                    whereArgs = new String[]{String.valueOf(self_Group_code), String.valueOf(self_Group_member_code)};
                    //  long insert_id = db.insert(DBHelper.SAVE_TREE_IMAGE_TABLE,null,values);
                    long update_id = db.update(DBHelper.SAVE_BEFORE_TREE_IMAGE_TABLE,beforevalues,whereClause,whereArgs);
                    if(update_id>0){
                        Toasty.success(this, "Updated Success", Toast.LENGTH_LONG, true).show();
                    }
                }
                else {
                    long insert_id = db.insert(DBHelper.SAVE_BEFORE_TREE_IMAGE_TABLE,null,beforevalues);
                    if(insert_id>0){
                        Toasty.success(this, "Inserted Success", Toast.LENGTH_LONG, true).show();
                    }
                }

                ArrayList<PMAYSurvey> imagCount = dbData.getParticular_After_Save_Tree_Image_Table(self_Group_code,self_Group_member_code);
                if(imagCount.size()>0){
                    whereClause = "shg_code = ? and shg_member_code = ?";
                    whereArgs = new String[]{String.valueOf(self_Group_code), String.valueOf(self_Group_member_code)};
                    //  long insert_id = db.insert(DBHelper.SAVE_TREE_IMAGE_TABLE,null,values);
                    long update_id = db.update(DBHelper.SAVE_AFTER_TREE_IMAGE_TABLE,aftervalues,whereClause,whereArgs);
                    if(update_id>0){
                        Toasty.success(this, "Updated Success", Toast.LENGTH_LONG, true).show();
                        onBackPressed();
                    }
                }
                else {
                    long insert_id = db.insert(DBHelper.SAVE_AFTER_TREE_IMAGE_TABLE,null,aftervalues);
                    if(insert_id>0){
                        Toasty.success(this, "Inserted Success", Toast.LENGTH_LONG, true).show();
                        onBackPressed();
                    }
                }
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    private void captureImage() {
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

        }
        else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            File file = CameraUtils.getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (file != null) {
                imageStoragePath = file.getAbsolutePath();
            }

            Uri fileUri = CameraUtils.getOutputMediaFileUri(this, file);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }
        if (MyLocationListener.latitude > 0) {
            if(image_flag.equals("before")){
                beforeofflatTextValue = MyLocationListener.latitude;
                beforeofflongTextValue = MyLocationListener.longitude;
            }
            else if(image_flag.equals("after")){
                afterofflatTextValue = MyLocationListener.latitude;
                afterofflongTextValue = MyLocationListener.longitude;
            }

        }
    }

    public void previewCapturedImage() {
        try {
            // hide video preview
            Bitmap bitmap = CameraUtils.optimizeBitmap(BITMAP_SAMPLE_SIZE, imageStoragePath);

            ExifInterface ei = null;
            try {
                ei = new ExifInterface(imageStoragePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            Bitmap rotatedBitmap = null;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = bitmap;
            }
            if(image_flag.equals("before")){
                newHomePageBinding.beforeCaptureIcon.setVisibility(View.GONE);
                newHomePageBinding.beforeImage.setVisibility(View.VISIBLE);
                before_image_byte=get_image_byte(rotatedBitmap);
                newHomePageBinding.beforeImage.setImageBitmap(rotatedBitmap);
                newHomePageBinding.beforeTakenIcon.setVisibility(View.VISIBLE);
                saveLocally("before_tree");
            }
            else if(image_flag.equals("after")){
                newHomePageBinding.afterCaptureIcon.setVisibility(View.GONE);
                newHomePageBinding.afterCaptureImage.setVisibility(View.VISIBLE);
                newHomePageBinding.afterTakenIcon.setVisibility(View.VISIBLE);
                newHomePageBinding.afterCaptureImage.setImageBitmap(rotatedBitmap);
                after_image_byte=get_image_byte(rotatedBitmap);
            }
//            cameraScreenBinding.imageView.showImage((getImageUri(rotatedBitmap)));
        } catch (NullPointerException e) {
            e.printStackTrace();

        }
    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Bitmap photo= (Bitmap) data.getExtras().get("data");
                    if(image_flag.equals("before")){
                        newHomePageBinding.beforeCaptureIcon.setVisibility(View.GONE);
                        newHomePageBinding.beforeImage.setVisibility(View.VISIBLE);
                        before_image_byte=get_image_byte(photo);
                        newHomePageBinding.beforeImage.setImageBitmap(photo);
                        newHomePageBinding.beforeTakenIcon.setVisibility(View.VISIBLE);
                        saveLocally("before_tree");
                    }
                    else if(image_flag.equals("after")){
                        newHomePageBinding.afterCaptureIcon.setVisibility(View.GONE);
                        newHomePageBinding.afterCaptureImage.setVisibility(View.VISIBLE);
                        newHomePageBinding.afterTakenIcon.setVisibility(View.VISIBLE);
                        newHomePageBinding.afterCaptureImage.setImageBitmap(photo);
                        after_image_byte=get_image_byte(photo);
                    }
                }
                else {
                    // Refreshing the gallery
                    CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                    // successfully captured the image
                    // display it in image view
                    previewCapturedImage();
                }
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.user_canceled_image_capture), Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.sorry_failed_to_capture), Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Refreshing the gallery
                CameraUtils.refreshGallery(getApplicationContext(), imageStoragePath);

                // video successfully recorded
                // preview the recorded video
//                previewVideo();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.user_canceled_video), Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.sorry_failed_capture_video), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    public void showNutriGardenDetails(){
        Intent intent = new Intent(NewHomePage.this,ViewNutriGarden.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
