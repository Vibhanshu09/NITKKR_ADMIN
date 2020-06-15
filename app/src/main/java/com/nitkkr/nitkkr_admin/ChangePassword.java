package com.nitkkr.nitkkr_admin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nitkkr.helperClasses.MyHelperClass;
import com.nitkkr.helperClasses.RemoteServiceUrl;
import com.nitkkr.helperClasses.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends AppCompatActivity {

    private static String CHANGE_PASS_URL = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.CHANGE_PASS;
    private static String PASSWORD_PREF_KEY = RemoteServiceUrl.SHARED_PREF.PASSWORD_PREF_KEY;
    private static String IS_LOGIN_PREF_KEY = RemoteServiceUrl.SHARED_PREF.IS_LOGIN_PREF_KEY;
    private static String EMP_ID_PREF_KEY = RemoteServiceUrl.SHARED_PREF.EMP_ID_PREF_KEY;
    String sharedPrefUserFileName = RemoteServiceUrl.SHARED_PREF.USER_FILE_NAME;
    String sharedPrefLoginFileName = RemoteServiceUrl.SHARED_PREF.LOGIN_STATUS_FILE_NAME;
    SharedPreferences sharedPrefUser, sharedPrefLogin;
    EditText con_passEditText, old_passEditText, new_passEditText;
    Button changePasswordBtn;
    private String emp_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        con_passEditText = findViewById(R.id.con_pass);
        old_passEditText = findViewById(R.id.old_pass);
        new_passEditText = findViewById(R.id.new_pass);
        changePasswordBtn = findViewById(R.id.change_pass_btn);

        sharedPrefUser = getSharedPreferences(sharedPrefUserFileName, Context.MODE_PRIVATE);
        sharedPrefLogin = getSharedPreferences(sharedPrefLoginFileName, Context.MODE_PRIVATE);

        emp_id = sharedPrefLogin.getString(EMP_ID_PREF_KEY, "");

        old_passEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 4 && con_passEditText.getText().toString().trim().length() > 4
                        && old_passEditText.getText().toString().trim().length() > 4
                        && new_passEditText.getText().toString().trim().length() > 4
                        && con_passEditText.getText().toString().trim().equals(new_passEditText.getText().toString().trim())) {
                    activateBtn();
                } else {
                    deactivateBtn();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        new_passEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 4 && con_passEditText.getText().toString().trim().length() > 4
                        && old_passEditText.getText().toString().trim().length() > 4
                        && new_passEditText.getText().toString().trim().length() > 4
                        && con_passEditText.getText().toString().trim().equals(new_passEditText.getText().toString().trim())) {
                    activateBtn();
                } else {
                    deactivateBtn();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        con_passEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 4 && con_passEditText.getText().toString().trim().length() > 4
                        && old_passEditText.getText().toString().trim().length() > 4
                        && new_passEditText.getText().toString().trim().length() > 4
                        && con_passEditText.getText().toString().trim().equals(new_passEditText.getText().toString().trim())) {
                    activateBtn();
                } else {
                    deactivateBtn();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void activateBtn() {
        changePasswordBtn.setBackground(getResources().getDrawable(R.drawable.button_enabled));
        changePasswordBtn.setEnabled(true);

    }

    private void deactivateBtn() {
        changePasswordBtn.setBackground(getResources().getDrawable(R.drawable.button_disabled));
        changePasswordBtn.setEnabled(false);

    }

    private void changePassword() {
        final String old_password = old_passEditText.getText().toString().trim();
        final String new_password = new_passEditText.getText().toString().trim();
        String c_new_password = con_passEditText.getText().toString().trim();
        if (new_password.equals(c_new_password)) {
            MyHelperClass.showProgress(ChangePassword.this, "Requesting", "Please wait a moment");
            StringRequest stringRequest = new StringRequest(Request.Method.POST, CHANGE_PASS_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject root = new JSONObject(response);
                                String success = root.getString("success");
                                if (success.equals("1")) {
                                    updateSharedPref();
                                    MyHelperClass.hideProgress();
                                    MyHelperClass.showAlerter(ChangePassword.this, "Success", root.getString("message"), R.drawable.ic_error_red_24dp);
                                    Intent intent = new Intent(ChangePassword.this, WelcomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else {
                                    MyHelperClass.hideProgress();
                                    MyHelperClass.showAlerter(ChangePassword.this, "Error", root.getString("message"), R.drawable.ic_error_red_24dp);
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
                            MyHelperClass.hideProgress();
                            MyHelperClass.showAlerter(ChangePassword.this, "Error", error.getMessage(), R.drawable.ic_error_red_24dp);
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("emp_id", emp_id);
                    params.put("old_password", old_password);
                    params.put("new_password", new_password);
                    return params;
                }
            };
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        } else {
            con_passEditText.setError("Password Not Match");
        }

    }

    private void updateSharedPref() {
        SharedPreferences.Editor userEditor = sharedPrefUser.edit();
        SharedPreferences.Editor loginEditor = sharedPrefLogin.edit();
        userEditor.putString(PASSWORD_PREF_KEY, "");
        userEditor.apply();
        loginEditor.putBoolean(IS_LOGIN_PREF_KEY, false);
        loginEditor.apply();
    }
}
