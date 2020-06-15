package com.nitkkr.nitkkr_admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class FeeUpdateActivity extends AppCompatActivity {


    private static String UPDATE_FEE_DETAIL = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.UPDATE_FEE_DETAIL;

    EditText studEnoET, feeReceiptNoET, feePaidDateET, feeAmountPaidET, feeAmountDueET;
    Button feeSubmitBtn;

    String studEnoStr, feeReceiptNoStr, feePaidDateStr, feeAmountPaidStr, feeAmountDueStr;

    AlertDialog.Builder feeUpdateDoneAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fee_update);
        findAllIds();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            feeUpdateDoneAlertDialog = new AlertDialog.Builder(FeeUpdateActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        else
            feeUpdateDoneAlertDialog = new AlertDialog.Builder(FeeUpdateActivity.this);
        feeUpdateDoneAlertDialog.setIcon(R.drawable.ic_success_24dp);
        feeUpdateDoneAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(FeeUpdateActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        feeSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEmptyFields()){
                    feeUpdate();
                }
            }
        });
    }

    private void findAllIds(){
        studEnoET = findViewById(R.id.et_stud_enroll_no_fee);
        feeReceiptNoET = findViewById(R.id.et_fee_receipt_no);
        feePaidDateET = findViewById(R.id.et_fee_paid_date);
        feeAmountPaidET = findViewById(R.id.et_fee_paid_amount);
        feeAmountDueET = findViewById(R.id.et_fee_due_amount);
        feeSubmitBtn = findViewById(R.id.btn_submit_fee_update);

    }

    private Boolean checkEmptyFields(){
        if (studEnoET.getText().toString().trim().isEmpty()) {
            studEnoET.setError("Field can't be empty");
            return false;
        } else {
            studEnoStr = studEnoET.getText().toString().trim();
        }
        if (feeReceiptNoET.getText().toString().trim().isEmpty()) {
            feeReceiptNoET.setError("Field can't be empty");
            return false;
        } else {
            feeReceiptNoStr = feeReceiptNoET.getText().toString().trim();
        }
        if (feePaidDateET.getText().toString().trim().isEmpty()) {
            feePaidDateET.setError("Field can't be empty");
            return false;
        } else {
            feePaidDateStr = feePaidDateET.getText().toString().trim();
        }
        if (feeAmountPaidET.getText().toString().trim().isEmpty()) {
            feeAmountPaidET.setError("Field can't be empty");
            return false;
        } else {
            feeAmountPaidStr = feeAmountPaidET.getText().toString().trim();
        }
        if (feeAmountDueET.getText().toString().trim().isEmpty()) {
            feeAmountDueET.setError("Field can't be empty");
            return false;
        } else {
            feeAmountDueStr = feeAmountDueET.getText().toString().trim();
        }
        return true;
    }

    private void feeUpdate(){
        MyHelperClass.showProgress(FeeUpdateActivity.this, "Fee Update", "Please wait a moment");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_FEE_DETAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            MyHelperClass.hideProgress();
                            JSONObject root = new JSONObject(response);
                            String success = root.getString("success");
                            if (success.equals("1")) {
                                feeUpdateDoneAlertDialog.setMessage(root.getString("message"));
                                feeUpdateDoneAlertDialog.setTitle("Fee Update Successful");
                                feeUpdateDoneAlertDialog.show();
                            } else {
                                MyHelperClass.showAlerter(FeeUpdateActivity.this,"Retry", root.getString("message"), R.drawable.ic_error_red_24dp);
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
                        MyHelperClass.showAlerter(FeeUpdateActivity.this, "Error", "Unknown error occurred!", R.drawable.ic_error_red_24dp);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("eno", studEnoStr);
                params.put("receipt_no", feeReceiptNoStr);
                params.put("paid_date", feePaidDateStr);
                params.put("amount_paid", feeAmountPaidStr);
                params.put("amount_due", feeAmountDueStr);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
