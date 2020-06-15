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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddStudent extends AppCompatActivity {

    private static String ADD_NEW_STUDENT = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.ADD_NEW_STUDENT;
    private static String FETCH_AVAILABLE_COURSE = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.FETCH_AVAILABLE_COURSE;
    private static String FETCH_AVAILABLE_BRANCH = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.FETCH_AVAILABLE_BRANCH;
    private static final int EXTRNL_STRG_REQ_CODE = RemoteServiceUrl.REQUEST_CODE.EXTRNL_STRG_REQ_CODE;

    EditText studEnoET, studNameET, studEmailET, studMobileET, studParentMobileET, studFatherNameET, studMotherNameET, studDobET, studDoaET, studPAddressET, studCAddressET,
            stud10thSchoolET, stud10thPassYearET, stud10thPercET, stud12thSchoolET, stud12thPassYearET, stud12thPercentageET, stud12thStreamET;
    Spinner studCategorySpinner, studGenderSpinner, studCourseSpinner, studBranchSpinner, studSemesterSpinner, studSectionSpinner;
    ArrayList<String> studCourseCodeArrayList, studCourseArrayList, studBranchArrayList, studSemesterArrayList, studCourseSemesterArrayList, studSectionArrayList, studBranchSectionArrayList;
    Button studImagePickerBtn, studRegisterBtn;
    ImageView studImageIV;
    private Bitmap studImageBitmap;
    private Uri studImageUri = null;
    AlertDialog.Builder studRegDoneAlertDialog;

    String studEnoStr, studNameStr, studEmailStr, studMobileStr, studParentMobileStr, studFatherNameStr, studMotherNameStr, studCategoryStr, studGenderStr, studDobStr,
            studDoaStr, studPAddressEStr, studCAddressStr, stud10thSchoolStr, stud10thPassYearStr, stud10thPercentageStr, stud12thSchoolStr, stud12thPassYearStr,
            stud12thPercentageStr, stud12thStreamStr, studCourseCodeStr, studCourseStr, studBranchStr, studSemesterStr, studSectionStr, studImageStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        findAllIds();
        addWidgetListeners();
        fetchCourseFromServer();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EXTRNL_STRG_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startPictureChooser();
            } else
                MyHelperClass.showAlerter(AddStudent.this,"Permission Required", "Please grant permission to choose an image", R.drawable.ic_error_red_24dp);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXTRNL_STRG_REQ_CODE && resultCode == RESULT_OK && data != null) {
            studImageUri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(studImageUri);
                studImageBitmap = BitmapFactory.decodeStream(inputStream);
                studImageIV.setImageBitmap(studImageBitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void findAllIds(){
        studEnoET = findViewById(R.id.et_stud_enroll_no);
        studNameET = findViewById(R.id.et_stud_name);
        studEmailET = findViewById(R.id.et_stud_email);
        studMobileET = findViewById(R.id.et_stud_mobile);
        studParentMobileET = findViewById(R.id.et_stud_parent_mobile);
        studFatherNameET = findViewById(R.id.et_stud_father_name);
        studMotherNameET = findViewById(R.id.et_stud_mother_name);
        studDobET = findViewById(R.id.et_stud_dob);
        studDoaET = findViewById(R.id.et_stud_doa);
        studPAddressET = findViewById(R.id.et_stud_p_address);
        studCAddressET = findViewById(R.id.et_stud_c_address);
        stud10thSchoolET = findViewById(R.id.et_stud_10th_school_name);
        stud10thPassYearET = findViewById(R.id.et_stud_10th_pass_year);
        stud10thPercET = findViewById(R.id.et_stud_10th_percentage);
        stud12thSchoolET = findViewById(R.id.et_stud_12th_school_name);
        stud12thPassYearET = findViewById(R.id.et_stud_12th_pass_year);
        stud12thPercentageET = findViewById(R.id.et_stud_12th_percentage);
        stud12thStreamET = findViewById(R.id.et_stud_12th_stream);

        studCategorySpinner = findViewById(R.id.spnr_stud_category);
        studGenderSpinner = findViewById(R.id.spnr_stud_gender);
        studCourseSpinner = findViewById(R.id.spnr_stud_course);
        studBranchSpinner = findViewById(R.id.spnr_stud_branch);
        studSemesterSpinner = findViewById(R.id.spnr_stud_semester);
        MyHelperClass.setSpinnerHeight(studSemesterSpinner);
        studSectionSpinner = findViewById(R.id.spnr_stud_section);
        MyHelperClass.setSpinnerHeight(studSectionSpinner);

        studImagePickerBtn = findViewById(R.id.btn_stud_image_pick);
        studRegisterBtn = findViewById(R.id.btn_submit_stud_details);

        studImageIV = findViewById(R.id.iv_stud_image_preview);

        studCourseCodeArrayList = new ArrayList<>();
        studCourseArrayList = new ArrayList<>();
        studBranchArrayList = new ArrayList<>();
        studCourseSemesterArrayList = new ArrayList<>();
        studSemesterArrayList = new ArrayList<>();
        studBranchSectionArrayList = new ArrayList<>();
        studSectionArrayList = new ArrayList<>();

    }

    private void addWidgetListeners(){
        studCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                studCategoryStr = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        studGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                studGenderStr = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        studCourseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                studCourseStr = parent.getItemAtPosition(position).toString();
                int j = Integer.parseInt(studCourseSemesterArrayList.get(position));
                Log.v("val of j", studCourseSemesterArrayList.get(position));
                studSemesterArrayList.clear();
                for (int i=0; i < j; i++){
                    studSemesterArrayList.add(String.valueOf(i+1));
                }
                studSemesterSpinner.setAdapter(new ArrayAdapter<String>(AddStudent.this,android.R.layout.simple_spinner_dropdown_item,studSemesterArrayList));
                studCourseCodeStr = studCourseCodeArrayList.get(position);
                fetchBranchFromServer();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        studBranchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                studBranchStr = parent.getItemAtPosition(position).toString();
                int j = Integer.parseInt(studBranchSectionArrayList.get(position));
                studSectionArrayList.clear();
                for (int i=0; i < j; i++){
                    studSectionArrayList.add(String.valueOf(i+1));
                }
                studSectionSpinner.setAdapter(new ArrayAdapter<String>(AddStudent.this,android.R.layout.simple_spinner_dropdown_item,studSectionArrayList));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        studSemesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                studSemesterStr = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        studSectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                studSectionStr = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        studImagePickerBtn.setOnClickListener(new View.OnClickListener() {
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
        studRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEmptyFields()){
                    addNewStudent();
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            studRegDoneAlertDialog = new AlertDialog.Builder(AddStudent.this, android.R.style.Theme_Material_Dialog_Alert);
        else
            studRegDoneAlertDialog = new AlertDialog.Builder(AddStudent.this);
        studRegDoneAlertDialog.setIcon(R.drawable.ic_success_24dp);
        studRegDoneAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(AddStudent.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private Boolean checkEmptyFields(){
        if (studEnoET.getText().toString().trim().isEmpty()){
            studEnoET.setError("Field can't be empty");
            return false;
        }else{
            studEnoStr = studEnoET.getText().toString().trim();
        }
        if (studNameET.getText().toString().trim().isEmpty()){
            studNameET.setError("Field can't be empty");
            return false;
        }else{
            studNameStr = studNameET.getText().toString().trim();
        }
        if (studEmailET.getText().toString().trim().isEmpty()){
            studEmailET.setError("Field can't be empty");
            return false;
        }else{
            studEmailStr = studEmailET.getText().toString().trim();
        }
        if (studMobileET.getText().toString().trim().isEmpty()){
            studMobileET.setError("Field can't be empty");
            return false;
        }else{
            studMobileStr = studMobileET.getText().toString().trim();
        }
        if (studParentMobileET.getText().toString().trim().isEmpty()){
            studParentMobileET.setError("Field can't be empty");
            return false;
        }else{
            studParentMobileStr = studParentMobileET.getText().toString().trim();
        }
        if (studFatherNameET.getText().toString().trim().isEmpty()){
            studFatherNameET.setError("Field can't be empty");
            return false;
        }else{
            studFatherNameStr = studFatherNameET.getText().toString().trim();
        }
        if (studMotherNameET.getText().toString().trim().isEmpty()){
            studMotherNameET.setError("Field can't be empty");
            return false;
        }else{
            studMotherNameStr = studMotherNameET.getText().toString().trim();
        }
        if (studDobET.getText().toString().trim().isEmpty()){
            studDobET.setError("Field can't be empty");
            return false;
        }else{
            studDobStr = studDobET.getText().toString().trim();
        }
        if (studDoaET.getText().toString().trim().isEmpty()){
            studDoaET.setError("Field can't be empty");
            return false;
        }else{
            studDoaStr = studDoaET.getText().toString().trim();
        }
        if (studPAddressET.getText().toString().trim().isEmpty()){
            studPAddressET.setError("Field can't be empty");
            return false;
        }else{
            studPAddressEStr = studPAddressET.getText().toString().trim();
        }
        if (studCAddressET.getText().toString().trim().isEmpty()){
            studCAddressET.setError("Field can't be empty");
            return false;
        }else{
            studCAddressStr = studCAddressET.getText().toString().trim();
        }
        if (stud10thSchoolET.getText().toString().trim().isEmpty()){
            stud10thSchoolET.setError("Field can't be empty");
            return false;
        }else{
            stud10thSchoolStr = stud10thSchoolET.getText().toString().trim();
        }
        if (stud10thPassYearET.getText().toString().trim().isEmpty()){
            stud10thPassYearET.setError("Field can't be empty");
            return false;
        }else{
            stud10thPassYearStr = stud10thPassYearET.getText().toString().trim();
        }
        if (stud10thPercET.getText().toString().trim().isEmpty()){
            stud10thPercET.setError("Field can't be empty");
            return false;
        }else{
            stud10thPercentageStr = stud10thPercET.getText().toString().trim();
        }
        if (stud12thSchoolET.getText().toString().trim().isEmpty()){
            stud12thSchoolET.setError("Field can't be empty");
            return false;
        }else{
            stud12thSchoolStr = stud12thSchoolET.getText().toString().trim();
        }
        if (stud12thPassYearET.getText().toString().trim().isEmpty()){
            stud12thPassYearET.setError("Field can't be empty");
            return false;
        }else{
            stud12thPassYearStr = stud12thPassYearET.getText().toString().trim();
        }
        if (stud12thPercentageET.getText().toString().trim().isEmpty()){
            stud12thPercentageET.setError("Field can't be empty");
            return false;
        }else{
            stud12thPercentageStr = stud12thPercentageET.getText().toString().trim();
        }
        if (stud12thStreamET.getText().toString().trim().isEmpty()){
            stud12thStreamET.setError("Field can't be empty");
            return false;
        }else{
            stud12thStreamStr = stud12thStreamET.getText().toString().trim();
        }
        if (studImageUri == null){
            MyHelperClass.showAlerter(AddStudent.this, "Error", "Please select Student image", R.drawable.ic_error_red_24dp);
            return false;
        }else{
            studImageStr = MyHelperClass.imageBitmapToStringConverter(studImageBitmap);
        }
        return true;

    }

    private void fetchCourseFromServer(){
        MyHelperClass.showProgress(AddStudent.this, "Getting form ready", "Please wait a moment");
        studCourseCodeArrayList.clear();
        studCourseArrayList.clear();
        studCourseSemesterArrayList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_AVAILABLE_COURSE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            MyHelperClass.hideProgress();
                            JSONObject root = new JSONObject(response);
                            String success = root.getString("success");
                            if (success.equals("1")) {
                                JSONArray courseArray = root.getJSONArray("courses");
                                for (int i=0; i < courseArray.length(); i++){
                                    JSONObject course = courseArray.getJSONObject(i);
                                    studCourseCodeArrayList.add(course.getString("course_code"));
                                    studCourseArrayList.add(course.getString("course_name"));
                                    studCourseSemesterArrayList.add(course.getString("no_of_semester"));
                                }
                                studCourseSpinner.setAdapter(new ArrayAdapter<String>(AddStudent.this,android.R.layout.simple_spinner_dropdown_item,studCourseArrayList));
                                //studSemesterSpinner.setAdapter(new ArrayAdapter<String>(AddStudent.this,android.R.layout.simple_spinner_dropdown_item,studSemesterArrayList));
                            } else {
                                MyHelperClass.showAlerter(AddStudent.this, "Error-->Try Again", root.getString("message"), R.drawable.ic_error_red_24dp);
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
                        MyHelperClass.showAlerter(AddStudent.this, "Error", "Unknown error occurred!", R.drawable.ic_error_red_24dp);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("fetch_code", "1212");
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
    private void fetchBranchFromServer(){
        MyHelperClass.showProgress(AddStudent.this, "Fetching Branch", "Please wait a moment");
        studBranchArrayList.clear();
        studBranchSectionArrayList.clear();
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
                                    studBranchArrayList.add(course.getString("branch_name"));
                                    studBranchSectionArrayList.add(course.getString("sections"));//TODO:: Update the section as of new....
                                }
                                studBranchSpinner.setAdapter(new ArrayAdapter<String>(AddStudent.this,android.R.layout.simple_spinner_dropdown_item,studBranchArrayList));
                            } else {
                                MyHelperClass.showAlerter(AddStudent.this, "Error-->Try Again", root.getString("message"), R.drawable.ic_error_red_24dp);
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
                        MyHelperClass.showAlerter(AddStudent.this, "Error", "Unknown error occurred!", R.drawable.ic_error_red_24dp);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("course_code", studCourseCodeStr);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void addNewStudent(){
        MyHelperClass.showProgress(AddStudent.this, "Student Registration", "Please wait a moment");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_NEW_STUDENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            MyHelperClass.hideProgress();
                            JSONObject root = new JSONObject(response);
                            String success = root.getString("success");
                            if (success.equals("1")) {
                                studRegDoneAlertDialog.setMessage(root.getString("message"));
                                studRegDoneAlertDialog.setTitle("Registration Successful");
                                studRegDoneAlertDialog.show();
                            } else {
                                MyHelperClass.showAlerter(AddStudent.this,"Retry", root.getString("message"), R.drawable.ic_error_red_24dp);

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
                        MyHelperClass.showAlerter(AddStudent.this, "Error", "Unknown error occurred!", R.drawable.ic_error_red_24dp);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("eno", studEnoStr);
                params.put("name", studNameStr);
                params.put("email", studEmailStr);
                params.put("stud_mob", studMobileStr);
                params.put("parent_mob", studParentMobileStr);
                params.put("father_name", studFatherNameStr);
                params.put("mother_name", studMotherNameStr);
                params.put("category", studCategoryStr);
                params.put("gender", studGenderStr);
                params.put("dob", studDobStr);
                params.put("date_of_admission", studDoaStr);
                params.put("p_address", studPAddressEStr);
                params.put("c_address", studCAddressStr);
                params.put("qualification_10", "10th");
                params.put("stream_10", "Common");
                params.put("pass_year_10", stud10thPassYearStr);
                params.put("collage_10", stud10thSchoolStr);
                params.put("percentage_10", stud10thPercentageStr);
                params.put("qualification_12", "12th");
                params.put("stream_12", stud12thStreamStr);
                params.put("pass_year_12", stud12thPassYearStr);
                params.put("collage_12", stud12thSchoolStr);
                params.put("percentage_12", stud12thPercentageStr);
                params.put("course", studCourseStr);
                params.put("branch", studBranchStr);
                params.put("curr_sem", studSemesterStr);
                params.put("curr_sec", studSectionStr);
                params.put("stud_img", studImageStr);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void startPictureChooser(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Select Image"),EXTRNL_STRG_REQ_CODE);
    }

}
