package com.nic.publictax.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.nic.publictax.R;
import com.nic.publictax.activity.FullImageActivity;
import com.nic.publictax.activity.NewPendingScreen;
import com.nic.publictax.constant.AppConstant;
import com.nic.publictax.dataBase.DBHelper;
import com.nic.publictax.dataBase.dbData;
import com.nic.publictax.databinding.NewPendingAdapterBinding;
import com.nic.publictax.model.PublicTax;
import com.nic.publictax.session.PrefManager;
import com.nic.publictax.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class NewPendingAdapter extends RecyclerView.Adapter<NewPendingAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private List<PublicTax> pendingListValues;
    JSONObject dataset = new JSONObject();
    dbData dbData;
    private LayoutInflater layoutInflater;
    public  DBHelper dbHelper;
    public  SQLiteDatabase db;
    public NewPendingAdapter(Activity context, List<PublicTax> pendingListValues, dbData dbData) {

        this.context = context;
        prefManager = new PrefManager(context);

        this.dbData=dbData;
        this.pendingListValues = pendingListValues;

        try {
            dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                //uploadPending(position);
                save_and_delete_alert(position,"save");
            }
        });

        holder.pendingAdapterBinding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deletePending(position);
                save_and_delete_alert(position,"delete");
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

        int sdsm = db.delete(DBHelper.SAVE_BEFORE_TREE_IMAGE_TABLE, "shg_code = ? and shg_member_code = ?", new String[]{shg_code,shg_member_code});
        int sdsm1 = db.delete(DBHelper.SAVE_AFTER_TREE_IMAGE_TABLE, "shg_code = ? and shg_member_code = ?", new String[]{shg_code,shg_member_code});
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

        ArrayList<PublicTax> getBeforeImageDetail = dbData.getParticular_Before_Save_Tree_Image_Table(shg_code,shg_member_code);
        ArrayList<PublicTax> getAfterImageDetail = dbData.getParticular_After_Save_Tree_Image_Table(shg_code,shg_member_code);


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

    public void save_and_delete_alert(int position,String save_delete){
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.alert_dialog);

            TextView text = (TextView) dialog.findViewById(R.id.tv_message);
            if(save_delete.equals("save")) {
                text.setText(context.getResources().getString(R.string.do_u_want_to_upload));
            }
            else if(save_delete.equals("delete")){
                text.setText(context.getResources().getString(R.string.do_u_want_to_delete));
            }

            Button yesButton = (Button) dialog.findViewById(R.id.btn_ok);
            Button noButton = (Button) dialog.findViewById(R.id.btn_cancel);
            noButton.setVisibility(View.VISIBLE);
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(save_delete.equals("save")) {
                        uploadPending(position);
                        dialog.dismiss();
                    }
                    else if(save_delete.equals("delete")) {
                        deletePending(position);
                        dialog.dismiss();
                    }
                }
            });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
