package com.nic.nutrigarden.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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

import com.nic.nutrigarden.R;
import com.nic.nutrigarden.activity.FullImageActivity;
import com.nic.nutrigarden.activity.NewPendingScreen;
import com.nic.nutrigarden.activity.ViewNutriGarden;
import com.nic.nutrigarden.constant.AppConstant;
import com.nic.nutrigarden.dataBase.DBHelper;
import com.nic.nutrigarden.dataBase.dbData;
import com.nic.nutrigarden.databinding.NewPendingAdapterBinding;
import com.nic.nutrigarden.model.PMAYSurvey;
import com.nic.nutrigarden.session.PrefManager;
import com.nic.nutrigarden.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.nic.nutrigarden.activity.HomePage.db;

public class NutriGardernDetailsServerAdapter extends RecyclerView.Adapter<NutriGardernDetailsServerAdapter.MyViewHolder> {

    private static Activity context;
    private PrefManager prefManager;
    private List<PMAYSurvey> pendingListValues;
    JSONObject dataset = new JSONObject();
    com.nic.nutrigarden.dataBase.dbData dbData;
    private LayoutInflater layoutInflater;

    public NutriGardernDetailsServerAdapter(Activity context, List<PMAYSurvey> pendingListValues,dbData dbData) {

        this.context = context;
        prefManager = new PrefManager(context);

        this.dbData=dbData;
        this.pendingListValues = pendingListValues;
    }

    @Override
    public NutriGardernDetailsServerAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
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
    public void onBindViewHolder(@NonNull final NutriGardernDetailsServerAdapter.MyViewHolder holder, final int position) {
        holder.pendingAdapterBinding.finYear.setText(pendingListValues.get(position).getFin_year());
        holder.pendingAdapterBinding.selfHelpGroup.setText(pendingListValues.get(position).getShg_name());
        holder.pendingAdapterBinding.memberName.setText(pendingListValues.get(position).getMember_name());
        holder.pendingAdapterBinding.typeOfTree.setText(pendingListValues.get(position).getWork_name());


        holder.pendingAdapterBinding.upload.setVisibility(View.GONE);
        holder.pendingAdapterBinding.viewImageText.setText("View Online Image");



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
        dataset = new JSONObject();
        String fin_year= pendingListValues.get(position).getFin_year();
        int shg_code = pendingListValues.get(position).getShg_code();
        int shg_member_code = pendingListValues.get(position).getShg_member_code();
        int work_code = pendingListValues.get(position).getWork_code();



        try {
            dataset.put(AppConstant.KEY_SERVICE_ID,"details_of_nutri_garden_delete");
            dataset.put("fin_year",fin_year);
            dataset.put("self_help_group_code",shg_code);
            dataset.put("self_help_group_member_code",shg_member_code);
            dataset.put("work_type_id",work_code);

        } catch (JSONException e) {
            e.printStackTrace();
        }




        if (Utils.isOnline()) {
            ((ViewNutriGarden)context).DeleteTreeJson(dataset);
        } else {
            Utils.showAlert(context, "Turn On Mobile Data To Upload");
        }

    }
    public void viewImages(int position){
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, FullImageActivity.class);
        intent.putExtra("shg_code", pendingListValues.get(position).getShg_code());
        intent.putExtra("shg_member_code", pendingListValues.get(position).getShg_member_code());
        intent.putExtra("work_type_id", pendingListValues.get(position).getWork_code());
        intent.putExtra("fin_year", pendingListValues.get(position).getFin_year());
        intent.putExtra("On_Off_Type", "Online");
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

    }


    @Override
    public int getItemCount() {
        return pendingListValues.size();
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

