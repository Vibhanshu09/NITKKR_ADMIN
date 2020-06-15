package com.nitkkr.nitkkr_admin;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nitkkr.helperClasses.MyHelperClass;
import com.nitkkr.helperClasses.RemoteServiceUrl;
import com.nitkkr.helperClasses.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddEmployee extends AppCompatActivity {

    private static String FETCH_AVAILABLE_BRANCH = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.FETCH_AVAILABLE_BRANCH;
    private static String ADD_NEW_EMPLOYEE = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.ADD_NEW_EMPLOYEE;
    private static final int EXTRNL_STRG_REQ_CODE = RemoteServiceUrl.REQUEST_CODE.EXTRNL_STRG_REQ_CODE;

    EditText empIdET, empNameET, empEmailET, empMobileET, empDojET;
    RadioGroup empRoleRG;
    Spinner empDepartmentSpinner;
    ImageView empImagePreviewIV;
    Button empChooseImageBtn, empSubmitDtlBtn;

    ArrayList<String> empDepartmentArrayList, empDepartmentSpinnerArrayList;
    private Bitmap empImageBitmap;
    private Uri empImageUri = null;
    AlertDialog.Builder empRegDoneAlertDialog;

    String empIdStr, empNameStr, empEmailStr, empMobileStr, empDojStr, empRoleStr, empDepartmentStr, empImageStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);
        findAllIds();
        fetchBranchFromServer();
        addWidgetListeners();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTRNL_STRG_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startPictureChooser();
            } else
                MyHelperClass.showAlerter(AddEmployee.this,"Permission Required", "Please grant permission to choose an image", R.drawable.ic_error_red_24dp);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXTRNL_STRG_REQ_CODE && resultCode == RESULT_OK && data != null) {
            empImageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(empImageUri);
                empImageBitmap = BitmapFactory.decodeStream(inputStream);
                empImagePreviewIV.setImageBitmap(empImageBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void findAllIds(){
        empIdET = findViewById(R.id.et_emp_id);
        empNameET = findViewById(R.id.et_emp_name);
        empEmailET = findViewById(R.id.et_emp_email);
        empMobileET = findViewById(R.id.et_emp_mobile);
        empRoleRG = findViewById(R.id.rg_emp_role);
        empDepartmentSpinner = findViewById(R.id.spnr_emp_department);
        empDojET = findViewById(R.id.et_emp_doj);
        empImagePreviewIV = findViewById(R.id.iv_emp_image_preview);
        empChooseImageBtn = findViewById(R.id.btn_emp_image_pick);
        empSubmitDtlBtn = findViewById(R.id.btn_submit_emp_details);

        empDepartmentArrayList = new ArrayList<>();
        empDepartmentSpinnerArrayList = new ArrayList<>();
    }

    private void addWidgetListeners(){
        empRoleRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rb_emp_admin){
                    empRoleStr = "Admin";
                    empDepartmentStr = "Admin";
                    empDepartmentSpinner.setEnabled(false);
                }else{
                    empRoleStr = "Faculty";
                    empDepartmentSpinner.setEnabled(true);
                }
            }
        });

        empDepartmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                empDepartmentStr = empDepartmentArrayList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        empChooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTRNL_STRG_REQ_CODE);
                    } else
                        startPictureChooser();
                } else {
                    startPictureChooser();
                }
            }
        });

        empSubmitDtlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEmptyFields()){
                    addNewEmployee();
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            empRegDoneAlertDialog = new AlertDialog.Builder(AddEmployee.this, android.R.style.Theme_Material_Dialog_Alert);
        else
            empRegDoneAlertDialog = new AlertDialog.Builder(AddEmployee.this);
        empRegDoneAlertDialog.setIcon(R.drawable.ic_success_24dp);
        empRegDoneAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(AddEmployee.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });


    }

    private Boolean checkEmptyFields() {
        if (empIdET.getText().toString().trim().isEmpty()) {
            empIdET.setError("Field can't be empty");
            return false;
        } else {
            empIdStr = empIdET.getText().toString().trim();
        }
        if (empNameET.getText().toString().trim().isEmpty()) {
            empNameET.setError("Field can't be empty");
            return false;
        } else {
            empNameStr = empNameET.getText().toString().trim();
        }
        if (empEmailET.getText().toString().trim().isEmpty()) {
            empEmailET.setError("Field can't be empty");
            return false;
        } else {
            empEmailStr = empEmailET.getText().toString().trim();
        }
        if (empMobileET.getText().toString().trim().isEmpty()) {
            empMobileET.setError("Field can't be empty");
            return false;
        } else {
            empMobileStr = empMobileET.getText().toString().trim();
        }
        if (empDojET.getText().toString().trim().isEmpty()) {
            empDojET.setError("Field can't be empty");
            return false;
        } else {
            empDojStr = empDojET.getText().toString().trim();
        }

        if (empImageUri == null){
            MyHelperClass.showAlerter(AddEmployee.this, "Error", "Please select Student image", R.drawable.ic_error_red_24dp);
            return false;
        }else{
            empImageStr = MyHelperClass.imageBitmapToStringConverter(empImageBitmap);
        }
        return true;
    }

    private void startPictureChooser(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Select Image"),EXTRNL_STRG_REQ_CODE);
    }

    private void fetchBranchFromServer(){
        MyHelperClass.showProgress(AddEmployee.this, "Fetching Branch", "Please wait a moment");
        empDepartmentArrayList.clear();
        empDepartmentSpinnerArrayList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_AVAILABLE_BRANCH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            MyHelperClass.hideProgress();
                            JSONObject root = new JSONObject(response);
                            String success = root.getString("success");
                            if (success.equals("1")) {
                                JSONArray courseArray = root.getJSONArray("branches");
                                for (int i=0; i < courseArray.length(); i++){
                                    JSONObject course = courseArray.getJSONObject(i);
                                    empDepartmentArrayList.add(course.getString("branch_name"));
                                    empDepartmentSpinnerArrayList.add(course.getString("branch_code") + " - " +course.getString("branch_name"));
                                }
                                empDepartmentSpinner.setAdapter(new ArrayAdapter<String>(AddEmployee.this,android.R.layout.simple_spinner_dropdown_item,empDepartmentSpinnerArrayList));
                            } else {
                                MyHelperClass.showAlerter(AddEmployee.this, "Error-->Try Again", root.getString("message"), R.drawable.ic_error_red_24dp);
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
                        MyHelperClass.showAlerter(AddEmployee.this, "Error", "Unknown error occurred!", R.drawable.ic_error_red_24dp);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("course_code", "fetch_all");
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void addNewEmployee(){
        MyHelperClass.showProgress(AddEmployee.this, "Employee Registration", "Please wait a moment");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_NEW_EMPLOYEE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            MyHelperClass.hideProgress();
                            JSONObject root = new JSONObject(response);
                            String success = root.getString("success");
                            if (success.equals("1")) {
                                empRegDoneAlertDialog.setMessage(root.getString("message"));
                                empRegDoneAlertDialog.setTitle("Registration Successful");
                                empRegDoneAlertDialog.show();
                            } else {
                                MyHelperClass.showAlerter(AddEmployee.this,"Retry", root.getString("message"), R.drawable.ic_error_red_24dp);
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
                        MyHelperClass.showAlerter(AddEmployee.this, "Error", "Unknown error occurred!", R.drawable.ic_error_red_24dp);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("emp_code", empIdStr);
                params.put("emp_name", empNameStr);
                params.put("emp_email", empEmailStr);
                params.put("emp_phone", empMobileStr);
                params.put("emp_type", empRoleStr);
                params.put("department", empDepartmentStr);
                params.put("date_of_joining", empDojStr);
                params.put("emp_img", empImageStr);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
