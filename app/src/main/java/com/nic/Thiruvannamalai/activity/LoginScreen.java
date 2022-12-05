package com.nic.Thiruvannamalai.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.VolleyError;
import com.nic.Thiruvannamalai.R;
import com.nic.Thiruvannamalai.api.Api;
import com.nic.Thiruvannamalai.api.ApiService;
import com.nic.Thiruvannamalai.api.ServerResponse;
import com.nic.Thiruvannamalai.constant.AppConstant;
import com.nic.Thiruvannamalai.dataBase.DBHelper;
import com.nic.Thiruvannamalai.dataBase.dbData;

import com.nic.Thiruvannamalai.databinding.NewLoginScreenBinding;
import com.nic.Thiruvannamalai.model.PublicTax;
import com.nic.Thiruvannamalai.session.PrefManager;
import com.nic.Thiruvannamalai.support.ProgressHUD;
import com.nic.Thiruvannamalai.utils.FontCache;
import com.nic.Thiruvannamalai.utils.UrlGenerator;
import com.nic.Thiruvannamalai.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by AchanthiSundar on 28-12-2018.
 */

public class LoginScreen extends AppCompatActivity implements View.OnClickListener, Api.ServerResponseListener {

    private String randString;

    public static DBHelper dbHelper;
    public static SQLiteDatabase db;
    JSONObject jsonObject;

    private PrefManager prefManager;
    private ProgressHUD progressHUD;
    private int setPType;

    //public LoginScreenBinding loginScreenBinding;
    public NewLoginScreenBinding loginScreenBinding;
    public dbData dbData = new dbData(this);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        loginScreenBinding = DataBindingUtil.setContentView(this, R.layout.new_login_screen);

        loginScreenBinding.setActivity(this);
        intializeUI();


    }

    public void intializeUI() {
        prefManager = new PrefManager(this);
        loginScreenBinding.go.setOnClickListener(this);

        loginScreenBinding.password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        //loginScreenBinding.inputLayoutEmail.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.REGULAR));
        //loginScreenBinding.inputLayoutPassword.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.REGULAR));
        loginScreenBinding.go.setTypeface(FontCache.getInstance(this).getFont(FontCache.Font.MEDIUM));
        //loginScreenBinding.inputLayoutEmail.setHintTextAppearance(R.style.InActive);
        //loginScreenBinding.inputLayoutPassword.setHintTextAppearance(R.style.InActive);
        //setTextStyle();
        loginScreenBinding.password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    checkLoginScreen();
                }
                return false;
            }
        });

        randString = Utils.randomChar();


        try {
            String versionName = getPackageManager()
                    .getPackageInfo(getPackageName(), 0).versionName;
            loginScreenBinding.tvVersion.setText("Version" + " " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        setPType = 1;
        loginScreenBinding.redEye.setOnClickListener(this);
    }

    private void setTextStyle(){
        loginScreenBinding.signInTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GoldleafBold.ttf"));
        loginScreenBinding.signInTitle1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GoldleafBold.ttf"));
        loginScreenBinding.userName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GoldleafBold.ttf"));
        loginScreenBinding.password.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GoldleafBold.ttf"));
        loginScreenBinding.go.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GoldleafBold.ttf"));
        loginScreenBinding.headerTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GoldleafBold.ttf"));
        loginScreenBinding.tvVersion.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GoldleafBold.ttf"));
        loginScreenBinding.signInTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GoldleafBold.ttf"));
        loginScreenBinding.signInTitle1.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/GoldleafBold.ttf"));
    }
    public void showPassword() {
        if (setPType == 1) {
            setPType = 0;
            loginScreenBinding.password.setTransformationMethod(null);
            if (loginScreenBinding.password.getText().length() > 0) {
                loginScreenBinding.password.setSelection(loginScreenBinding.password.getText().length());
                loginScreenBinding.redEye.setBackgroundResource(R.drawable.ic_baseline_visibility_off_24px);
            }
        } else {
            setPType = 1;
            loginScreenBinding.password.setTransformationMethod(new PasswordTransformationMethod());
            if (loginScreenBinding.password.getText().length() > 0) {
                loginScreenBinding.password.setSelection(loginScreenBinding.password.getText().length());
                loginScreenBinding.redEye.setBackgroundResource(R.drawable.ic_baseline_visibility_24px);
            }
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {


        }
    }

    public boolean validate() {
        boolean valid = true;
        String username = loginScreenBinding.userName.getText().toString().trim();
        prefManager.setUserName(username);
        String password = loginScreenBinding.password.getText().toString().trim();


        if (username.isEmpty()) {
            valid = false;
            Utils.showAlert(this, "Please enter the username");
        } else if (password.isEmpty()) {
            valid = false;
            Utils.showAlert(this, "Please enter the password");
        }
        return valid;
    }

    public void checkLoginScreen() {
        /*loginScreenBinding.userName.setText("tokenentry1");
        loginScreenBinding.password.setText("test123#$");//loc*/

        /*loginScreenBinding.userName.setText("tokenverifier1");
        loginScreenBinding.password.setText("test123#$");//loc*/

        /*loginScreenBinding.userName.setText("kpmutmrvp34u2");
        loginScreenBinding.password.setText("rdas771#$");//pro*/
        final String username = loginScreenBinding.userName.getText().toString().trim();
        final String password = loginScreenBinding.password.getText().toString().trim();
        prefManager.setUserPassword(password);

        if (Utils.isOnline()) {
            if (!validate())
                return;
            else if (prefManager.getUserName().length() > 0 && password.length() > 0) {
//                showHomeScreen();
                new ApiService(this).makeRequest("LoginScreen", Api.Method.POST, UrlGenerator.getLoginUrl(), loginParams(), "not cache", this);
            } else {
                Utils.showAlert(this, "Please enter your username and password!");
            }
        } else {
            //Utils.showAlert(this, getResources().getString(R.string.no_internet));
            AlertDialog.Builder ab = new AlertDialog.Builder(
                    LoginScreen.this);
            ab.setMessage("Internet Connection is not avaliable..Please Turn ON Network Connection OR Continue With Off-line Mode..");
            ab.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            Intent I = new Intent(
                                    android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                            startActivity(I);
                        }
                    });
            ab.setNegativeButton("Continue With Off-Line",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            //offline_mode(username, password);
                        }
                    });
            ab.show();
        }
    }


    public Map<String, String> loginParams() {
        Map<String, String> params = new HashMap<>();
        params.put(AppConstant.KEY_SERVICE_ID, "login");


        String random = Utils.randomChar();

        params.put(AppConstant.USER_LOGIN_KEY, random);
        params.put(AppConstant.KEY_APP_CODE,"VP");
        Log.d("randchar", "" + random);

        params.put(AppConstant.KEY_USER_NAME, prefManager.getUserName());
        Log.d("user", "" + loginScreenBinding.userName.getText().toString().trim());

        String encryptUserPass = Utils.md5(loginScreenBinding.password.getText().toString().trim());
        prefManager.setEncryptPass(encryptUserPass);
        Log.d("md5", "" + encryptUserPass);

        String userPass = encryptUserPass.concat(random);
        Log.d("userpass", "" + userPass);
        String sha256 = Utils.getSHA(userPass);
        Log.d("sha", "" + sha256);

        params.put(AppConstant.KEY_USER_PASSWORD, sha256);


        Log.d("user", "" + loginScreenBinding.userName.getText().toString().trim());

        Log.d("params", "" + params);
        return params;
    }






    @Override
    public void OnMyResponse(ServerResponse serverResponse) {
        try {
            JSONObject loginResponse = serverResponse.getJsonResponse();
            String urlType = serverResponse.getApi();
            String status;
            String response;

            if ("LoginScreen".equals(urlType)) {
                status = loginResponse.getString(AppConstant.KEY_STATUS);
                response = loginResponse.getString(AppConstant.KEY_RESPONSE);
                if (status.equalsIgnoreCase("OK")) {
                    if (response.equals("LOGIN_SUCCESS")) {
                        String key = loginResponse.getString(AppConstant.KEY_USER);
                        String user_data = loginResponse.getString(AppConstant.USER_DATA);
                        String decryptedKey = Utils.decrypt(prefManager.getEncryptPass(), key);
                        String userDataDecrypt = Utils.decrypt(prefManager.getEncryptPass(), user_data);
                        Log.d("userdatadecry", "" + userDataDecrypt);
                        jsonObject = new JSONObject(userDataDecrypt);
                        prefManager.setKeyLevel(jsonObject.getString("levels"));
                        prefManager.setName(jsonObject.getString("name"));
                        prefManager.setKeyDeptId(jsonObject.getString("dept_id"));
                        prefManager.setKeyRoleCode(jsonObject.getString("role_code"));
                        prefManager.setKeyRoleName(jsonObject.getString("role_name"));
                        prefManager.setUserPassKey(decryptedKey);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showHomeScreen();
                            }
                        }, 1000);

                    } else {
                        if (response.equals("LOGIN_FAILED")) {
                            Utils.showAlert(this, "Invalid UserName Or Password");
                        }
                    }
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void OnError(VolleyError volleyError) {
        Utils.showAlert(this, "Login Again");
    }



    public void showHomeScreen() {
        Intent intent = new Intent(LoginScreen.this, Dashboard.class);
        intent.putExtra("Home", "Login");
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

   /* public void offline_mode(String name, String pass) {
        String userName = prefManager.getUserName();
        String password = prefManager.getUserPassword();
        if (name.equals(userName) && pass.equals(password)) {
            showHomeScreen();
        } else {
            Utils.showAlert(this, "No data available for offline. Please Turn On Your Network");
        }
    }*/
}
