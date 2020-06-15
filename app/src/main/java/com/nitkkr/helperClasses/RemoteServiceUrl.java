package com.nitkkr.helperClasses;

public interface RemoteServiceUrl {
    public static final String SERVER_URL = "http://192.168.43.184/erp/api_admin/";

    public static interface METHOD_NAME {
        String LOGIN = "login.php";
        String APP_INFO = "app_info.php";
        String CHANGE_PASS = "change_pass.php";
        String REGISTER_NEW_COURSE = "reg_new_course.php";
        String FETCH_AVAILABLE_COURSE = "fetch_avail_course.php";
        String REGISTER_NEW_BRANCH = "reg_new_branch.php";
        String FETCH_AVAILABLE_BRANCH = "fetch_avail_branch.php";
        String ADD_BRANCH_SECTIONS = "add_branch_sections.php";
        String ADD_NEW_SUBJECT = "add_new_subject.php";
        String ADD_NEW_STUDENT = "add_new_student.php";
        String ADD_NEW_EMPLOYEE = "add_new_employee.php";
        String UPDATE_FEE_DETAIL = "fee_update.php";
        String FETCH_AVAILABLE_SECTION = "fetch_avail_section.php";
        String FETCH_SUBJECT_AND_FACULTY = "fetch_sub_and_faculty.php";
        String ASSIGN_SUBJECT_TO_FACULTY = "assign_sub_to_faculty.php";


    }

    public static interface SHARED_PREF {
        String USER_FILE_NAME = "admin";
        String LOGIN_STATUS_FILE_NAME = "login";
        String EMP_ID_PREF_KEY = "e,p_id_pref_key";
        String NAME_PREF_KEY = "name_pref_key";
        String PASSWORD_PREF_KEY = "password_pref_key";
        String IS_LOGIN_PREF_KEY = "is_login";
    }

    public static interface REQUEST_CODE {
        int EXTRNL_STRG_REQ_CODE = 101;
    }
}