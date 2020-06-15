package com.nitkkr.nitkkr_admin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.nitkkr.helperClasses.MyHelperClass;
import com.nitkkr.helperClasses.RemoteServiceUrl;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static String IS_LOGIN_PREF_KEY = RemoteServiceUrl.SHARED_PREF.IS_LOGIN_PREF_KEY;
    private static String EMP_ID_PREF_KEY = RemoteServiceUrl.SHARED_PREF.EMP_ID_PREF_KEY;
    private static String NAME_PREF_KEY = RemoteServiceUrl.SHARED_PREF.NAME_PREF_KEY;
    String sharedPrefLoginFileName = RemoteServiceUrl.SHARED_PREF.LOGIN_STATUS_FILE_NAME;
    SharedPreferences sharedPrefLogin;
    private String emp_id = "", name = "";
    AlertDialog.Builder builder;

    RecyclerView homeCardRecyclerView;
    List<String> cardTitles;
    List<Integer> cardIcons;
    HomeCardViewAdapter homeCardViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPrefLogin = getSharedPreferences(sharedPrefLoginFileName, Context.MODE_PRIVATE);
        name = sharedPrefLogin.getString(NAME_PREF_KEY, "");
        emp_id = sharedPrefLogin.getString(EMP_ID_PREF_KEY, "");

        homeCardRecyclerView = findViewById(R.id.home_recycler_view);
        cardTitles = new ArrayList<>();
        cardIcons = new ArrayList<>();
        addTitlesAndImages();
        homeCardViewAdapter = new HomeCardViewAdapter(this, cardTitles, cardIcons);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL,false);
        homeCardRecyclerView.setLayoutManager(gridLayoutManager);
        homeCardRecyclerView.setAdapter(homeCardViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_top_right_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_Logout) {
            logout();
            return true;
        }
        if (id == R.id.action_change_password) {
            startActivity(new Intent(HomeActivity.this, ChangePassword.class));
            return true;
        }
        if (id == R.id.action_Feedback) {
            sendFeedback();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addTitlesAndImages(){
        cardTitles.add(getString(R.string.add_student));
        cardTitles.add(getString(R.string.add_employee));
        cardTitles.add(getString(R.string.fee));
        cardTitles.add(getString(R.string.subject_allocation));
        cardTitles.add(getString(R.string.course_management));
        cardTitles.add(getString(R.string.student_management));
        cardTitles.add(getString(R.string.employee_management));

        cardIcons.add(R.drawable.ic_scholer_icon);
        cardIcons.add(R.drawable.ic_employee);
        cardIcons.add(R.drawable.ic_fee);
        cardIcons.add(R.drawable.ic_subject_allocation);
        cardIcons.add(R.drawable.ic_course_management);
        cardIcons.add(R.drawable.ic_student_management);
        cardIcons.add(R.drawable.ic_emp_management);
    }

    private void logout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder = new AlertDialog.Builder(HomeActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        else
            builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setIcon(R.drawable.ic_log_out_red_24dp)
                .setTitle("Log Out?")
                .setMessage("Are you sure to want to log out and Exit?")
                .setPositiveButton("Logout & Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sharedPrefLogin.edit();
                        editor.putString(EMP_ID_PREF_KEY, "Not Available");
                        editor.putBoolean(IS_LOGIN_PREF_KEY, false);
                        editor.apply();
                        editor.commit();
                        HomeActivity.this.finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void sendFeedback() {

        String[] mail = {"reeshanrai@gmail.com"};
        String subject = "Feedback/Question about SISTec Student App";
        String body = "\n\n\n\n<-Please Do not remove below content for your better help->\n\n"
                + "Employee ID: " + emp_id
                + "\nName: " + name
                + "\n------------------------------\n"
                + "\nBrand: " + Build.BRAND
                + "\nModel: " + Build.MODEL
                + "\nAPI: " + String.valueOf(Build.VERSION.SDK_INT)
                + "\nManufacturer: " + Build.MANUFACTURER
                + "\nDevice: " + Build.DEVICE;

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, mail);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        if (emailIntent.resolveActivity(getPackageManager()) != null)
            startActivity(Intent.createChooser(emailIntent, "Send Feedback using..."));
        else
            MyHelperClass.showAlerter(HomeActivity.this, "Error", "No Mailing Application Found", R.drawable.ic_error_red_24dp);
    }
}
