package com.nitkkr.nitkkr_admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

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

public class SubjectAllotmentActivity extends AppCompatActivity {

    private static String FETCH_AVAILABLE_COURSE = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.FETCH_AVAILABLE_COURSE;
    private static String FETCH_AVAILABLE_BRANCH = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.FETCH_AVAILABLE_BRANCH;
    private static String FETCH_AVAILABLE_SECTION = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.FETCH_AVAILABLE_SECTION;
    private static String FETCH_SUBJECT_AND_FACULTY = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.FETCH_SUBJECT_AND_FACULTY;
    private static String ASSIGN_SUBJECT_TO_FACULTY = RemoteServiceUrl.SERVER_URL + RemoteServiceUrl.METHOD_NAME.ASSIGN_SUBJECT_TO_FACULTY;

    Spinner courseSASpinner, branchSASpinner, semesterSASpinner, sectionSASpinner, subjectSASpinner, facultySASpinner;
    EditText nolAssignedET;
    Button assignSubjectBtn, nextPrevSABtn;
    LinearLayout secSubFacLL;
    AlertDialog.Builder empRegDoneAlertDialog;

    ArrayList<String> courseCodeArrayList, courseArrayList, branchCodeArrayList, branchArrayList, totalSemesterArrayList , semesterArrayList,  sectionArrayList,
            subjectCodeArrayList, subjectArrayList, facultyCodeArrayList, facultyArrayList;
    String courseStr = "", branchStr = "", semesterStr = "", sectionStr, subjectStr, facultyStr, nolAssignedStr;
    private int flag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_allotment);
        findAllIds();
        addSpinnerListeners();
        fetchCourseFromServer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            empRegDoneAlertDialog = new AlertDialog.Builder(SubjectAllotmentActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        else
            empRegDoneAlertDialog = new AlertDialog.Builder(SubjectAllotmentActivity.this);
        empRegDoneAlertDialog.setIcon(R.drawable.ic_success_24dp);
        empRegDoneAlertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(SubjectAllotmentActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        assignSubjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nolAssignedStr = nolAssignedET.getText().toString().trim();
                assignSubject();
            }
        });
        nextPrevSABtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag==0){
                    secSubFacLL.setVisibility(View.VISIBLE);
                    courseSASpinner.setEnabled(false);
                    branchSASpinner.setEnabled(false);
                    semesterSASpinner.setEnabled(false);
                    fetchSection();
                    fetchSubjectAndFaculty();
                    nextPrevSABtn.setText("Previous");
                    Log.v("CO:" , courseStr);
                    Log.v("BR:" , branchStr);
                    Log.v("Sem:" , semesterStr);
                    flag = 1;
                }else {
                    secSubFacLL.setVisibility(View.GONE);
                    courseSASpinner.setEnabled(true);
                    branchSASpinner.setEnabled(true);
                    semesterSASpinner.setEnabled(true);
                    nextPrevSABtn.setText("Next");
                    flag = 0;

                }
            }
        });
    }

    private void findAllIds(){
        courseSASpinner = findViewById(R.id.spinner_all_course_sa);
        branchSASpinner = findViewById(R.id.spinner_all_branch_sa);
        semesterSASpinner = findViewById(R.id.spinner_semester_sa);
        sectionSASpinner = findViewById(R.id.spinner_section_sa);
        subjectSASpinner = findViewById(R.id.spinner_subject_sa);
        facultySASpinner = findViewById(R.id.spinner_faculty_sa);
        nolAssignedET = findViewById(R.id.et_nol_assigned_sa);
        assignSubjectBtn = findViewById(R.id.btn_assign_subject);
        nextPrevSABtn = findViewById(R.id.btn_next_prev_sa);
        secSubFacLL = findViewById(R.id.ll_sec_sub_fac_nol_sa);

        courseCodeArrayList = new ArrayList<>();
        courseArrayList = new ArrayList<>();
        branchCodeArrayList = new ArrayList<>();
        branchArrayList = new ArrayList<>();
        totalSemesterArrayList = new ArrayList<>();
        semesterArrayList = new ArrayList<>();
        sectionArrayList = new ArrayList<>();
        subjectCodeArrayList = new ArrayList<>();
        subjectArrayList = new ArrayList<>();
        facultyCodeArrayList = new ArrayList<>();
        facultyArrayList = new ArrayList<>();

    }

    private void addSpinnerListeners(){
        courseSASpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                courseStr = courseCodeArrayList.get(position);
                int j = Integer.parseInt(totalSemesterArrayList.get(position));
                semesterArrayList.clear();
                for (int i=0; i < j; i++){
                    semesterArrayList.add(String.valueOf(i+1));
                }
                semesterSASpinner.setAdapter(new ArrayAdapter<String>(SubjectAllotmentActivity.this,android.R.layout.simple_spinner_dropdown_item,semesterArrayList));
                fetchBranchFromServer();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        branchSASpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                branchStr = branchCodeArrayList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        semesterSASpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                semesterStr = semesterArrayList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sectionSASpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sectionStr = sectionArrayList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        subjectSASpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subjectStr = subjectCodeArrayList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        facultySASpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                facultyStr = facultyCodeArrayList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void fetchCourseFromServer(){
        MyHelperClass.showProgress(SubjectAllotmentActivity.this, "Getting form ready", "Please wait a moment");
        courseCodeArrayList.clear();
        courseArrayList.clear();
        totalSemesterArrayList.clear();
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
                                    courseArrayList.add(course.getString("course_name"));
                                    totalSemesterArrayList.add(course.getString("no_of_semester"));
                                }
                                courseSASpinner.setAdapter(new ArrayAdapter<String>(SubjectAllotmentActivity.this,android.R.layout.simple_spinner_dropdown_item,courseArrayList));
                            } else {
                                MyHelperClass.showAlerter(SubjectAllotmentActivity.this, "Error-->Try Again", root.getString("message"), R.drawable.ic_error_red_24dp);
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
                        MyHelperClass.showAlerter(SubjectAllotmentActivity.this, "Error", "Unknown error occurred!", R.drawable.ic_error_red_24dp);
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
        MyHelperClass.showProgress(SubjectAllotmentActivity.this, "Fetching Branch", "Please wait a moment");
        branchCodeArrayList.clear();
        branchArrayList.clear();
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
                                    branchArrayList.add(course.getString("branch_name"));
                                }
                                branchSASpinner.setAdapter(new ArrayAdapter<String>(SubjectAllotmentActivity.this,android.R.layout.simple_spinner_dropdown_item,branchArrayList));
                            } else {
                                MyHelperClass.showAlerter(SubjectAllotmentActivity.this, "Error-->Try Again", root.getString("message"), R.drawable.ic_error_red_24dp);
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
                        MyHelperClass.showAlerter(SubjectAllotmentActivity.this, "Error", "Unknown error occurred!", R.drawable.ic_error_red_24dp);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("course_code", courseStr);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void fetchSection(){
        MyHelperClass.showProgress(SubjectAllotmentActivity.this, "Fetching Sections", "Please wait a moment");
        sectionArrayList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_AVAILABLE_SECTION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            MyHelperClass.hideProgress();
                            JSONObject root = new JSONObject(response);
                            String success = root.getString("success");
                            if (success.equals("1")) {
                                int j = Integer.parseInt(root.getString("section"));
                                for (int i = 0; i<j; i++){
                                    sectionArrayList.add(String.valueOf(i+1));
                                }
                                sectionSASpinner.setAdapter(new ArrayAdapter<String>(SubjectAllotmentActivity.this,android.R.layout.simple_spinner_dropdown_item,sectionArrayList));
                            } else {
                                MyHelperClass.showAlerter(SubjectAllotmentActivity.this, "Error-->Try Again", root.getString("message"), R.drawable.ic_error_red_24dp);
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
                        MyHelperClass.showAlerter(SubjectAllotmentActivity.this, "Error", "Unknown error occurred!", R.drawable.ic_error_red_24dp);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("course_code", courseStr);
                params.put("branch_code", branchStr);
                params.put("semester", semesterStr);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void fetchSubjectAndFaculty(){
        MyHelperClass.hideProgress();
        MyHelperClass.showProgress(SubjectAllotmentActivity.this, "Fetching Data", "Please wait a moment");
        subjectArrayList.clear();
        subjectCodeArrayList.clear();
        facultyArrayList.clear();
        facultyCodeArrayList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FETCH_SUBJECT_AND_FACULTY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            MyHelperClass.hideProgress();
                            JSONObject root = new JSONObject(response);
                            String success = root.getString("success");
                            if (success.equals("1")) {
                                JSONArray subjectArray = root.getJSONArray("subjects");
                                for (int i=0; i < subjectArray.length(); i++){
                                    JSONObject subject = subjectArray.getJSONObject(i);
                                    subjectArrayList.add(subject.getString("subject_name"));
                                    subjectCodeArrayList.add(subject.getString("subject_code"));
                                }
                                JSONArray facultyArray = root.getJSONArray("faculties");
                                for (int i=0; i < facultyArray.length(); i++){
                                    JSONObject faculty = facultyArray.getJSONObject(i);
                                    facultyArrayList.add(faculty.getString("faculty_name"));
                                    facultyCodeArrayList.add(faculty.getString("faculty_code"));
                                }
                                subjectSASpinner.setAdapter(new ArrayAdapter<String>(SubjectAllotmentActivity.this,android.R.layout.simple_spinner_dropdown_item,subjectArrayList));
                                facultySASpinner.setAdapter(new ArrayAdapter<String>(SubjectAllotmentActivity.this,android.R.layout.simple_spinner_dropdown_item,facultyArrayList));
                            } else {
                                MyHelperClass.showAlerter(SubjectAllotmentActivity.this, "Error-->Try Again", "Unknown error occurred", R.drawable.ic_error_red_24dp);
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
                        MyHelperClass.showAlerter(SubjectAllotmentActivity.this, "Error", "Unknown error occurred!", R.drawable.ic_error_red_24dp);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("branch_code", branchStr);
                params.put("branch_name", branchSASpinner.getSelectedItem().toString());
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void assignSubject(){
        MyHelperClass.showProgress(SubjectAllotmentActivity.this, "Assigning Subjects", "Please wait a moment");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ASSIGN_SUBJECT_TO_FACULTY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            MyHelperClass.hideProgress();
                            JSONObject root = new JSONObject(response);
                            String success = root.getString("success");
                            if (success.equals("1")) {
                                empRegDoneAlertDialog.setTitle("Subject Assignment");
                                empRegDoneAlertDialog.setMessage("Subject Assigned successfully");
                                empRegDoneAlertDialog.show();
                            } else {
                                MyHelperClass.showAlerter(SubjectAllotmentActivity.this, "Error-->Try Again", root.getString("message"), R.drawable.ic_error_red_24dp);
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
                        MyHelperClass.showAlerter(SubjectAllotmentActivity.this, "Error", "Unknown error occurred!", R.drawable.ic_error_red_24dp);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("course_code", courseStr);
                params.put("branch_code", branchStr);
                params.put("semester", semesterStr);
                params.put("section", sectionStr);
                params.put("subject", subjectStr);
                params.put("faculty", facultyStr);
                params.put("nol_assigned", nolAssignedStr);
                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }


}
