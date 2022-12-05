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

public class Dashboard extends AppCompatActivity implements View.OnClickListener, MyDialog.myOnClickListener {
    private DashboardBinding dashboardBinding;
    private PrefManager prefManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        dashboardBinding = DataBindingUtil.setContentView(this, R.layout.dashboard);
        dashboardBinding.setActivity(this);
        prefManager = new PrefManager(this);

        dashboardBinding.name.setText(prefManager.getName());

        if(prefManager.getKeyRoleCode().equals("2")){
            dashboardBinding.detailsVerify.setVisibility(View.GONE);
            dashboardBinding.detailsEntry.setVisibility(View.VISIBLE);
            dashboardBinding.detailsView.setVisibility(View.VISIBLE);
        }
        else {
            dashboardBinding.detailsVerify.setVisibility(View.VISIBLE);
            dashboardBinding.detailsEntry.setVisibility(View.GONE);
            dashboardBinding.detailsView.setVisibility(View.VISIBLE);
        }

        dashboardBinding.detailsEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this,DetailsEnterScreen.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });
        dashboardBinding.detailsVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this,QRVerifivation.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });
        dashboardBinding.detailsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this,ViewDetailsScreen.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });
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
    public void logout() {
        closeApplication();
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
