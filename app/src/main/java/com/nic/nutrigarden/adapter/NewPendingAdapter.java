package com.nic.nutrigarden.adapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.util.Util;
import com.nic.nutrigarden.R;
import com.nic.nutrigarden.activity.FullImageActivity;
import com.nic.nutrigarden.activity.NewPendingScreen;
import com.nic.nutrigarden.constant.AppConstant;
import com.nic.nutrigarden.dataBase.DBHelper;
import com.nic.nutrigarden.dataBase.dbData;
import com.nic.nutrigarden.databinding.NewPendingAdapterBinding;
import com.nic.nutrigarden.model.PMAYSurvey;
import com.nic.nutrigarden.session.PrefManager;
import com.nic.nutrigarden.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.nic.nutrigarden.activity.HomePage.db;

public class NewPendingAdapter extends RecyclerView.Adapter<NewPendingAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private List<PMAYSurvey> pendingListValues;
    JSONObject dataset = new JSONObject();
    dbData dbData;
    private LayoutInflater layoutInflater;

    public NewPendingAdapter(Activity context, List<PMAYSurvey> pendingListValues,dbData dbData) {

        this.context = context;
        prefManager = new PrefManager(context);

        this.dbData=dbData;
        this.pendingListValues = pendingListValues;
    }

    @Override
    public NewPendingAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        NewPendingAdapterBinding pendingAdapterBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.new_pending_adapter, viewGroup, false);
        return new MyViewHolder(pendingAdapterBinding);

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private NewPendingAdapterBinding pendingAdapterBinding;

        public MyViewHolder(NewPendingAdapterBinding Binding) {
            super(Binding.getRoot());
            pendingAdapterBinding = Binding;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final NewPendingAdapter.MyViewHolder holder, final int position) {
        holder.pendingAdapterBinding.finYear.setText(pendingListValues.get(position).getFin_year());
        holder.pendingAdapterBinding.selfHelpGroup.setText(pendingListValues.get(position).getShg_name());
        holder.pendingAdapterBinding.memberName.setText(pendingListValues.get(position).getMember_name());
        holder.pendingAdapterBinding.typeOfTree.setText(pendingListValues.get(position).getWork_name());




        holder.pendingAdapterBinding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadPending(position);
            }
        });

        holder.pendingAdapterBinding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePending(position);
            }
        });



        holder.pendingAdapterBinding.viewOfflineImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewImages(position);
            }
        });


    }


    public void deletePending(int position) {
        String shg_code = String.valueOf(pendingListValues.get(position).getShg_code());
        String shg_member_code = String.valueOf(pendingListValues.get(position).getShg_member_code());

        int sdsm = db.delete(DBHelper.SAVE_BEFORE_TREE_IMAGE_TABLE, "shg_code = ? and shg_member_code", new String[]{shg_code,shg_member_code});
        int sdsm1 = db.delete(DBHelper.SAVE_AFTER_TREE_IMAGE_TABLE, "shg_code = ? and shg_member_code", new String[]{shg_code,shg_member_code});
        pendingListValues.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position, pendingListValues.size());
        Log.d("sdsm", String.valueOf(sdsm));
    }

    public void viewImages(int position){
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, FullImageActivity.class);
        intent.putExtra("shg_code", pendingListValues.get(position).getShg_code());
        intent.putExtra("shg_member_code", pendingListValues.get(position).getShg_member_code());
        intent.putExtra("On_Off_Type", "Offline");
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }

    public void uploadPending(int position) {
        dataset = new JSONObject();
        String fin_year= pendingListValues.get(position).getFin_year();
        int shg_code = pendingListValues.get(position).getShg_code();
        int shg_member_code = pendingListValues.get(position).getShg_member_code();
        int work_code = pendingListValues.get(position).getWork_code();

        ArrayList<PMAYSurvey> getBeforeImageDetail = dbData.getParticular_Before_Save_Tree_Image_Table(shg_code,shg_member_code);
        ArrayList<PMAYSurvey> getAfterImageDetail = dbData.getParticular_After_Save_Tree_Image_Table(shg_code,shg_member_code);


        try {
           dataset.put(AppConstant.KEY_SERVICE_ID,"details_of_nutri_garden_save");
           dataset.put("fin_year",fin_year);
           dataset.put("work_type_id",work_code);
           dataset.put("self_help_group_code",shg_code);
           dataset.put("self_help_group_member_code",shg_member_code);

           if(getBeforeImageDetail.size()>0&&getAfterImageDetail.size()>0){
               String before_photo_lat= getBeforeImageDetail.get(0).getBefore_photo_lat();
               String before_photo_long= getBeforeImageDetail.get(0).getBefore_photo_long();
               String after_photo_lat= getAfterImageDetail.get(0).getAfter_photo_lat();
               String after_photo_long= getAfterImageDetail.get(0).getAfter_photo_long();
               String before_plant_image= BitMapToString(getBeforeImageDetail.get(0).getBefore_photo());
               String after_plant_image=BitMapToString(getAfterImageDetail.get(0).getAfter_photo());

               dataset.put("before_plant_latitude",before_photo_lat);
               dataset.put("before_plant_longitude",before_photo_long);
               dataset.put("after_plant_latitude",after_photo_lat);
               dataset.put("after_plant_longitude",after_photo_long);
               dataset.put("before_plant_image",before_plant_image);
               dataset.put("after_plant_image",after_plant_image);
           }
           else {
               Utils.showAlert(context,"Please Insert All Images");
           }


        } catch (JSONException e) {
            e.printStackTrace();
        }




        if (Utils.isOnline()) {
            ((NewPendingScreen)context).saveTreeJson(dataset,String.valueOf(pendingListValues.get(position).getShg_code()),String.valueOf(pendingListValues.get(position).getShg_member_code()));
        } else {
            Utils.showAlert(context, "Turn On Mobile Data To Upload");
        }

    }

    @Override
    public int getItemCount() {
        return pendingListValues.size();
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

}
