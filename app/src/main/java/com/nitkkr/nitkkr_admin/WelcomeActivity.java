package com.nitkkr.nitkkr_admin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nitkkr.helperClasses.AppConnectivityStatus;
import com.nitkkr.helperClasses.MyHelperClass;
import com.nitkkr.helperClasses.RemoteServiceUrl;
import com.nitkkr.helperClasses.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {

    private static String LOGIN_URL = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.LOGIN;
    private static String APP_INFO_URL = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.APP_INFO;
    private static String EMP_ID_PREF_KEY = RemoteServiceUrl.SHARED_PREF.EMP_ID_PREF_KEY;
    private static String NAME_PREF_KEY = RemoteServiceUrl.SHARED_PREF.NAME_PREF_KEY;
    private static String PASSWORD_PREF_KEY = RemoteServiceUrl.SHARED_PREF.PASSWORD_PREF_KEY;
    private static String IS_LOGIN_PREF_KEY = RemoteServiceUrl.SHARED_PREF.IS_LOGIN_PREF_KEY;
    String sharedPrefUserFileName = RemoteServiceUrl.SHARED_PREF.USER_FILE_NAME;
    String sharedPrefLoginFileName = RemoteServiceUrl.SHARED_PREF.LOGIN_STATUS_FILE_NAME;
    SharedPreferences sharedPrefUser, sharedPrefLogin;
    LinearLayout connectingLayout, loginLayout;
    ImageView startLogo;
    TextView welcomeTxt, connTxt, forgotPass;
    EditText empIdEditText, passwordEditText;
    CheckBox rememberChkbx;
    Button loginBtn;
    Animation animationBlink, animationBounce, animationZoomOut, shiftUpZoomOut;
    String emp_id = "", password = "";
    AlertDialog.Builder builder, appUpdateDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        connectingLayout = findViewById(R.id.connecting_layout);
        loginLayout = findViewById(R.id.login_layout);
        startLogo = findViewById(R.id.start_logo);
        welcomeTxt = findViewById(R.id.welcome_txt);
        connTxt = findViewById(R.id.conn);
        empIdEditText = findViewById(R.id.emp_id);
        passwordEditText = findViewById(R.id.password);
        rememberChkbx = findViewById(R.id.remember);
        forgotPass = findViewById(R.id.forgot_pass);
        loginBtn = findViewById(R.id.login);

        //Auto fetch saved preferences
        sharedPrefUser = getSharedPreferences(sharedPrefUserFileName, Context.MODE_PRIVATE);
        sharedPrefLogin = getSharedPreferences(sharedPrefLoginFileName, Context.MODE_PRIVATE);
        emp_id = sharedPrefUser.getString(EMP_ID_PREF_KEY, "");
        empIdEditText.setText(emp_id);
        password = sharedPrefUser.getString(PASSWORD_PREF_KEY, "");
        passwordEditText.setText(password);
        if (empIdEditText.getText().toString().isEmpty()) {
            empIdEditText.requestFocus();
            empIdEditText.setError(null);
        }

        //Applying animation on the views
        animationZoomOut = AnimationUtils.loadAnimation(this, R.anim.zoom_out);
        animationBounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        animationBlink = AnimationUtils.loadAnimation(this, R.anim.blink);
        shiftUpZoomOut = AnimationUtils.loadAnimation(this, R.anim.shift_up_zoom_out);

        //First animation
        startLogo.startAnimation(animationBounce);
        connTxt.startAnimation(animationBlink);

        if (AppConnectivityStatus.isOnline(getApplicationContext())) {
            if (sharedPrefLogin.getBoolean(IS_LOGIN_PREF_KEY, false))
                checkAppUpdate();
            else
                animationSwitcher(2000);
        } else {
            noInternetAlert();
        }

        empIdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (empIdEditText.getText().toString().trim().length() != 10)
                    empIdEditText.setError("Invalid Enroll Num");
                else
                    empIdEditText.setError(null);
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 4 && empIdEditText.getText().toString().trim().length() == 10) {
                    loginBtn.setBackground(getResources().getDrawable(R.drawable.button_enabled));
                    loginBtn.setEnabled(true);
                } else {
                    loginBtn.setBackground(getResources().getDrawable(R.drawable.button_disabled));
                    loginBtn.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(WelcomeActivity.this);
                builder.setTitle("Password Recovery");
                builder.setMessage("You will get a new password on your mail.");
                final EditText input = new EditText(WelcomeActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                input.setLayoutParams(lp);
                input.setHint("Employee ID");
                input.setTextColor(getResources().getColor(R.color.colorPrimary));
                builder.setView(input);
                builder.setIcon(R.drawable.ic_recover_green_24dp);
                builder.setPositiveButton("Send Link", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String userID = input.getText().toString().trim();
                        //TODO: send reset link to @userID
                        empIdEditText.setText(userID);
                        passwordEditText.setText("");
                        MyHelperClass.showAlerter(WelcomeActivity.this, "Success", "New password has been sent to your mail.", R.drawable.ic_success_24dp);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emp_id = empIdEditText.getText().toString().trim();
                password = passwordEditText.getText().toString().trim();
                if (AppConnectivityStatus.isOnline(getApplicationContext())) {
                    checkAppUpdate();
                    //login();
                } else
                    noInternetAlert();

            }
        });
    }

    private void noInternetAlert() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder = new AlertDialog.Builder(WelcomeActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        else
            builder = new AlertDialog.Builder(WelcomeActivity.this);
        builder.setTitle("Connectivity Error")
                .setMessage("No Internet Access!!!")
                .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WelcomeActivity.this.finish();
                    }
                })
                .setIcon(R.drawable.ic_error_red_24dp)
                .setCancelable(false)
                .show();
    }

    private void animationSwitcher(int delayTime) {
        //Toast.makeText(WelcomeActivity.this,"Connected",Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startLogo.startAnimation(animationZoomOut);
                welcomeTxt.setText(R.string.login_to_erp);
                welcomeTxt.startAnimation(shiftUpZoomOut);
                connTxt.clearAnimation();
                connTxt.setVisibility(View.GONE);
                connectingLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);

            }
        }, delayTime);
    }

    private void login() {
        MyHelperClass.showProgress(WelcomeActivity.this, "Login", "Please wait a moment");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject root = new JSONObject(response);
                            String success = root.getString("success");
                            JSONArray array = root.getJSONArray("login");
                            if (success.equals("1")) {
                                JSONObject jsonObject = array.getJSONObject(0);
                                updateSharedPref(true,
                                        jsonObject.getString("emp_id"),
                                        jsonObject.getString("name"));
                                MyHelperClass.hideProgress();
                                //Toast.makeText(WelcomeActivity.this, jsonObject.getString("name"), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
                                intent.putExtra("User_name", jsonObject.getString("name"));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                MyHelperClass.hideProgress();
                                MyHelperClass.showAlerter(WelcomeActivity.this, "Error", root.getString("message"), R.drawable.ic_error_red_24dp);
                                animationSwitcher(0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Response Error", error.toString());
                        updateSharedPref(false, "Not Available", "Not Available");
                        MyHelperClass.hideProgress();
                        MyHelperClass.showAlerter(WelcomeActivity.this, "Server Error", "Unable to connect to the server.", R.drawable.ic_error_red_24dp);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("emp_id", emp_id);
                params.put("password", password);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void updateSharedPref(boolean login_status, String emp, String name) {
        SharedPreferences.Editor userEditor = sharedPrefUser.edit();
        SharedPreferences.Editor loginEditor = sharedPrefLogin.edit();
        if (rememberChkbx.isChecked()) {
            //Save Login Details
            userEditor.putString(EMP_ID_PREF_KEY, emp_id);
            userEditor.putString(PASSWORD_PREF_KEY, password);
        } else {
            userEditor.putString(EMP_ID_PREF_KEY, "");
            userEditor.putString(PASSWORD_PREF_KEY, "");
        }
        userEditor.apply();
        loginEditor.putString(EMP_ID_PREF_KEY, emp);
        loginEditor.putBoolean(IS_LOGIN_PREF_KEY, login_status);
        loginEditor.putString(NAME_PREF_KEY, name);
        loginEditor.apply();
    }

    private void checkAppUpdate() {
        MyHelperClass.showProgress(WelcomeActivity.this, "Checking For Update", "Please wait a moment");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, APP_INFO_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        MyHelperClass.hideProgress();
                        try {
                            final JSONObject root = new JSONObject(response);
                            String success = root.getString("success");
                            if (success.equals("1")) {
                                appUpdateDialog = new AlertDialog.Builder(WelcomeActivity.this);
                                appUpdateDialog.setTitle("App Update Available");
                                appUpdateDialog.setMessage("Please update the app to latest version now.");
                                final String url = root.getString("app_url");
                                //url = url.replaceAll("\\","");
                                appUpdateDialog.setIcon(R.drawable.ic_update_24dp);
                                appUpdateDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(url));
                                        startActivity(i);
                                    }
                                });
                                appUpdateDialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        WelcomeActivity.this.finish();
                                    }
                                });
                                appUpdateDialog.show();
                            } else
                                login();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Response Error", error.toString());
                        MyHelperClass.hideProgress();
                        MyHelperClass.showAlerter(WelcomeActivity.this, "Auth Failure", "Invalid Credentials", R.drawable.ic_error_red_24dp);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("version_code", String.format("%d", MyHelperClass.getVersionCode(WelcomeActivity.this)));
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
