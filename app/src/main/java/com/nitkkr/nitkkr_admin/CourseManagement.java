package com.nitkkr.nitkkr_admin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CourseManagement extends AppCompatActivity {

    private static String REGISTER_NEW_COURSE = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.REGISTER_NEW_COURSE;
    private static String FETCH_AVAILABLE_COURSE = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.FETCH_AVAILABLE_COURSE;
    private static String REGISTER_NEW_BRANCH = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.REGISTER_NEW_BRANCH;
    private static String FETCH_AVAILABLE_BRANCH = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.FETCH_AVAILABLE_BRANCH;
    private static String ADD_BRANCH_SECTIONS = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.ADD_BRANCH_SECTIONS;
    private static String ADD_NEW_SUBJECT = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.ADD_NEW_SUBJECT;

    Button addNewCourseBtnStart, addNewBranchBtnStart, addBranchSectionBtnStart, addNewSubjectBtnStart, registerCourseBtnSubmit,
            registerBranchBtnSubmit, addBranchSectionBtnSubmit, addNewSubjectBtnSubmit;
    LinearLayout courseMngBtnLL, addNewCourseLL, addNewBranchLL, addBranchSectionLL, addNewSubjectLL;
    EditText newCourseCodeET, newCourseNameET, noOfSemET, newBranchCodeET, newBranchNameET, newBranchSectionsET, newSubjectCodeET, newSubjectNameET;
    Spinner allCourseSpinner1, allCourseSpinner2, allBranchSpinner, allSemesterSpinner, allBranchSubSpinner;
    RadioGroup newSubjectTypeRG;
    ArrayList<String> allCourseSpinnerArrayList, allBranchSpinnerArrayList;
    ArrayList<String> courseCodeArrayList, branchCodeArrayList, allSemesterArrayList, semesterArrayList, branchCodeNameArrayList;

    private String newCourseCodeStr, newCourseNameStr, noOfSemStr, newBranchCodeStr, newBranchNameStr,
            newBranchSectionStr, newBranchSemesterStr, newSubjectBranchStr, newSubjectCodeStr, newSubjectNameStr, newSubjectTypeStr;
    private int selectedCourseSpinner1Item =- 1, selectedCourseSpinner2Item = -1, selectedBranchSpinnerItem = -1, selectedBranchSubSpinnerItem = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_management);
        findAllIds();
        addSpinnerListeners();

        newSubjectTypeRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId == R.id.rb_sub_type_theory){
                    newSubjectTypeStr = "Theory";
                }else{
                    newSubjectTypeStr = "Lab";
                }
            }
        });

        addNewCourseBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseMngBtnLL.setVisibility(View.GONE);
                addNewCourseLL.setVisibility(View.VISIBLE);
            }
        });

        addNewBranchBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCourseFromServer();
                courseMngBtnLL.setVisibility(View.GONE);
                addNewBranchLL.setVisibility(View.VISIBLE);
            }
        });

        addBranchSectionBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchCourseFromServer();
                courseMngBtnLL.setVisibility(View.GONE);
                addBranchSectionLL.setVisibility(View.VISIBLE);
            }
        });

        addNewSubjectBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchAllBranchForSubject();
                courseMngBtnLL.setVisibility(View.GONE);
                addNewSubjectLL.setVisibility(View.VISIBLE);
            }
        });

        registerCourseBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCourseCodeStr = newCourseCodeET.getText().toString().trim();
                newCourseNameStr = newCourseNameET.getText().toString().trim();
                noOfSemStr = noOfSemET.getText().toString().trim();
                registerNewCourse();
                addNewCourseLL.setVisibility(View.GONE);
                courseMngBtnLL.setVisibility(View.VISIBLE);
            }
        });
        registerBranchBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newBranchCodeStr = newBranchCodeET.getText().toString().trim();
                newBranchNameStr = newBranchNameET.getText().toString().trim();
                registerNewBranch();
                addNewBranchLL.setVisibility(View.GONE);
                courseMngBtnLL.setVisibility(View.VISIBLE);
            }
        });
        addBranchSectionBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newBranchSectionStr = newBranchSectionsET.getText().toString().trim();
                addBranchSection();
                /*Log.v("course_code", courseCodeArrayList.get(selectedCourseSpinner2Item));
                Log.v("branch_code", branchCodeArrayList.get(selectedBranchSpinnerItem));
                Log.v("semester", newBranchSemesterStr);
                Log.v("no_of_sec", newBranchSectionStr);*/
                addBranchSectionLL.setVisibility(View.GONE);
                courseMngBtnLL.setVisibility(View.VISIBLE);
            }
        });
        addNewSubjectBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newSubjectCodeStr = newSubjectCodeET.getText().toString().trim();
                newSubjectNameStr = newSubjectNameET.getText().toString().trim();
                addNewSubject();
                addNewSubjectLL.setVisibility(View.GONE);
                courseMngBtnLL.setVisibility(View.VISIBLE);
            }
        });

    }

    private void findAllIds(){
        addNewCourseBtnStart = findViewById(R.id.btn_add_new_course_start);
        addNewBranchBtnStart = findViewById(R.id.btn_add_new_branch_start);
        addBranchSectionBtnStart = findViewById(R.id.btn_update_branch_section_start);
        addNewSubjectBtnStart = findViewById(R.id.btn_add_new_subject_start);
        registerCourseBtnSubmit = findViewById(R.id.btn_add_new_course_submit);
        registerBranchBtnSubmit = findViewById(R.id.btn_add_new_branch_submit);
        addBranchSectionBtnSubmit = findViewById(R.id.btn_add_branch_section_submit);
        addNewSubjectBtnSubmit = findViewById(R.id.btn_add_new_subject_submit);

        courseMngBtnLL = findViewById(R.id.course_mng_btn_ll);
        addNewCourseLL = findViewById(R.id.add_new_course_ll);
        addNewBranchLL = findViewById(R.id.add_new_branch_ll);
        addBranchSectionLL = findViewById(R.id.add_branch_section_ll);
        addNewSubjectLL = findViewById(R.id.add_new_subject_ll);

        newCourseCodeET = findViewById(R.id.et_new_course_code);
        newCourseNameET = findViewById(R.id.et_new_course_name);
        noOfSemET = findViewById(R.id.et_no_of_sem);
        newBranchCodeET = findViewById(R.id.et_new_branch_code);
        newBranchNameET = findViewById(R.id.et_new_branch_name);
        newBranchSectionsET = findViewById(R.id.et_add_branch_sections);
        newSubjectCodeET = findViewById(R.id.et_new_subject_code);
        newSubjectNameET = findViewById(R.id.et_new_subject_name);

        newSubjectTypeRG = findViewById(R.id.rg_new_subject_type);

        allCourseSpinner1 = findViewById(R.id.spinner_all_course_1);
        MyHelperClass.setSpinnerHeight(allCourseSpinner1);
        allCourseSpinner2 = findViewById(R.id.spinner_all_course_2);
        MyHelperClass.setSpinnerHeight(allCourseSpinner2);
        allBranchSpinner = findViewById(R.id.spinner_all_branch);
        MyHelperClass.setSpinnerHeight(allBranchSpinner);
        allSemesterSpinner = findViewById(R.id.spinner_semester);
        MyHelperClass.setSpinnerHeight(allSemesterSpinner);
        allBranchSubSpinner = findViewById(R.id.spinner_all_branch_sub);
        MyHelperClass.setSpinnerHeight(allBranchSubSpinner);

        allCourseSpinnerArrayList = new ArrayList<>();
        allBranchSpinnerArrayList = new ArrayList<>();
        courseCodeArrayList = new ArrayList<>();
        branchCodeArrayList = new ArrayList<>();
        allSemesterArrayList = new ArrayList<>();
        semesterArrayList = new ArrayList<>();
        branchCodeNameArrayList = new ArrayList<>();
    }

    private void addSpinnerListeners(){
        allCourseSpinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != selectedCourseSpinner1Item){
                    selectedCourseSpinner1Item = position;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        allCourseSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != selectedCourseSpinner2Item){
                    selectedCourseSpinner2Item = position;
                    fetchBranchFromServer();
                }
                int j = Integer.parseInt(allSemesterArrayList.get(position));
                semesterArrayList.clear();
                for (int i=0; i < j; i++){
                    semesterArrayList.add(String.valueOf(i+1));
                }
                allSemesterSpinner.setAdapter(new ArrayAdapter<String>(CourseManagement.this,android.R.layout.simple_spinner_dropdown_item,semesterArrayList));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        allBranchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selectedBranchSpinnerItem != position){
                    selectedBranchSpinnerItem = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        allSemesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newBranchSemesterStr = semesterArrayList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        allBranchSubSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selectedBranchSubSpinnerItem != position){
                    selectedBranchSubSpinnerItem = position;
                    newSubjectBranchStr = branchCodeArrayList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void registerNewCourse() {
        MyHelperClass.showProgress(CourseManagement.this, "Course Registration", "Please wait a moment");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_NEW_COURSE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            MyHelperClass.hideProgress();
                            JSONObject root = new JSONObject(response);
                            String success = root.getString("success");
                            if (success.equals("1")) {
                                MyHelperClass.showAlerter(CourseManagement.this, "Success", root.getString("message"), R.drawable.ic_success_24dp);
                            } else {
                                MyHelperClass.showAlerter(CourseManagement.this, "Error", root.getString("message"), R.drawable.ic_error_red_24dp);
                            }
                            newCourseCodeET.setText("");
                            newCourseNameET.setText("");
                            noOfSemET.setText("");
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
                        MyHelperClass.showAlerter(CourseManagement.this, "Error", "Unknown error occurred!", R.drawable.ic_error_red_24dp);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("course_code", newCourseCodeStr);
                params.put("course_name", newCourseNameStr);
                params.put("no_of_sem", noOfSemStr);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void fetchCourseFromServer(){
        MyHelperClass.showProgress(CourseManagement.this, "Fetching Course", "Please wait a moment");
        courseCodeArrayList.clear();
        allCourseSpinnerArrayList.clear();
        allSemesterArrayList.clear();
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
                                    courseCodeArrayList.add(course.getString("course_code"));
                                    allCourseSpinnerArrayList.add(course.getString("course_name"));
                                    allSemesterArrayList.add(course.getString("no_of_semester"));
                                }
                                allCourseSpinner1.setAdapter(new ArrayAdapter<String>(CourseManagement.this,android.R.layout.simple_spinner_dropdown_item,allCourseSpinnerArrayList));
                                allCourseSpinner2.setAdapter(new ArrayAdapter<String>(CourseManagement.this,android.R.layout.simple_spinner_dropdown_item,allCourseSpinnerArrayList));
                            } else {
                                MyHelperClass.showAlerter(CourseManagement.this, "Error", root.getString("message"), R.drawable.ic_error_red_24dp);
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
                        MyHelperClass.showAlerter(CourseManagement.this, "Error", "Unknown error occurred!", R.drawable.ic_error_red_24dp);
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

    private void registerNewBranch(){
        MyHelperClass.showProgress(CourseManagement.this, "Branch Registration", "Please wait a moment");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REGISTER_NEW_BRANCH,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            MyHelperClass.hideProgress();
                            JSONObject root = new JSONObject(response);
                            String success = root.getString("success");
                            if (success.equals("1")) {
                                MyHelperClass.showAlerter(CourseManagement.this, "Success", root.getString("message"), R.drawable.ic_success_24dp);
                            } else {
                                MyHelperClass.showAlerter(CourseManagement.this, "Error", root.getString("message"), R.drawable.ic_error_red_24dp);
                            }
                            newBranchCodeET.setText("");
                            newBranchNameET.setText("");
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
                        MyHelperClass.showAlerter(CourseManagement.this, "Error", "Unknown error occurred!", R.drawable.ic_error_red_24dp);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("course_code", courseCodeArrayList.get(selectedCourseSpinner1Item));
                params.put("branch_code", newBranchCodeStr);
                params.put("branch_name", newBranchNameStr);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void fetchBranchFromServer(){
        MyHelperClass.showProgress(CourseManagement.this, "Fetching Branch", "Please wait a moment");
        branchCodeArrayList.clear();
        allBranchSpinnerArrayList.clear();
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
                                    branchCodeArrayList.add(course.getString("branch_code"));
                                    allBranchSpinnerArrayList.add(course.getString("branch_name"));
                                }
                                allBranchSpinner.setAdapter(new ArrayAdapter<String>(CourseManagement.this,android.R.layout.simple_spinner_dropdown_item,allBranchSpinnerArrayList));
                            } else {
                                MyHelperClass.showAlerter(CourseManagement.this, "Error", root.getString("message"), R.drawable.ic_error_red_24dp);
                                addNewBranchLL.setVisibility(View.GONE);
                                courseMngBtnLL.setVisibility(View.VISIBLE);
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
                        MyHelperClass.showAlerter(CourseManagement.this, "Error", "Unknown error occurred!", R.drawable.ic_error_red_24dp);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("course_code", courseCodeArrayList.get(selectedCourseSpinner2Item));
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void fetchAllBranchForSubject(){
        MyHelperClass.showProgress(CourseManagement.this, "Fetching Branch", "Please wait a moment");
        branchCodeArrayList.clear();
        branchCodeNameArrayList.clear();
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
                                    branchCodeArrayList.add(course.getString("branch_code"));
                                    branchCodeNameArrayList.add(course.getString("branch_code") + " - " +course.getString("branch_name"));
                                }
                                allBranchSubSpinner.setAdapter(new ArrayAdapter<String>(CourseManagement.this,android.R.layout.simple_spinner_dropdown_item,branchCodeNameArrayList));
                            } else {
                                MyHelperClass.showAlerter(CourseManagement.this, "Error", root.getString("message"), R.drawable.ic_error_red_24dp);
                                addNewSubjectLL.setVisibility(View.GONE);
                                courseMngBtnLL.setVisibility(View.VISIBLE);
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
                        MyHelperClass.showAlerter(CourseManagement.this, "Error", "Unknown error occurred!", R.drawable.ic_error_red_24dp);
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

    private void addBranchSection(){
        MyHelperClass.showProgress(CourseManagement.this, "Section Update", "Please wait a moment");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_BRANCH_SECTIONS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            MyHelperClass.hideProgress();
                            JSONObject root = new JSONObject(response);
                            String success = root.getString("success");
                            if (success.equals("1")) {
                                MyHelperClass.showAlerter(CourseManagement.this, "Success", root.getString("message"), R.drawable.ic_success_24dp);
                            } else {
                                MyHelperClass.showAlerter(CourseManagement.this, "Error", root.getString("message"), R.drawable.ic_error_red_24dp);
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
                        MyHelperClass.showAlerter(CourseManagement.this, "Error", "Unknown error occurred!", R.drawable.ic_error_red_24dp);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("course_code", courseCodeArrayList.get(selectedCourseSpinner2Item));
                params.put("branch_code", branchCodeArrayList.get(selectedBranchSpinnerItem));
                params.put("semester", newBranchSemesterStr);
                params.put("no_of_sec", newBranchSectionStr);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void addNewSubject(){
        MyHelperClass.showProgress(CourseManagement.this, "Adding Subject", "Please wait a moment");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_NEW_SUBJECT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            MyHelperClass.hideProgress();
                            JSONObject root = new JSONObject(response);
                            String success = root.getString("success");
                            if (success.equals("1")) {
                                MyHelperClass.showAlerter(CourseManagement.this, "Success", root.getString("message"), R.drawable.ic_success_24dp);
                            } else {
                                MyHelperClass.showAlerter(CourseManagement.this, "Error", root.getString("message"), R.drawable.ic_error_red_24dp);
                            }
                            newSubjectCodeET.setText("");
                            newSubjectNameET.setText("");
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
                        MyHelperClass.showAlerter(CourseManagement.this, "Error", "Unknown error occurred!", R.drawable.ic_error_red_24dp);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("branch_code", newSubjectBranchStr);
                params.put("subject_code", newSubjectCodeStr);
                params.put("subject_name", newSubjectNameStr);
                params.put("subject_type", newSubjectTypeStr);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

}
