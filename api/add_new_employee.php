<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
	$emp_code = $_POST['emp_code'];
	$emp_name = $_POST['emp_name'];
	$emp_email = $_POST['emp_email'];
	$emp_phone = $_POST['emp_phone'];
	$emp_type = $_POST['emp_type'];
	$department = $_POST['department'];
	$date_of_joining = $_POST['date_of_joining'];
	$emp_img = $_POST['emp_img'];

    require_once "connect.php";

    $emp_img_upload_dir = "../images/emp_images";
    if (!file_exists($emp_img_upload_dir)) {
    	mkdir($emp_img_upload_dir, 0777, true);
    }
    $emp_img_url = $emp_img_upload_dir . "/" . $emp_code . ".jpeg";
	$result = array();
	$select_query = "SELECT * FROM emp_details WHERE emp_code = '$emp_code'";
	$check_emp = mysqli_query($conn,$select_query);
	if (mysqli_num_rows($check_emp) === 1) {
		$result['success'] = "0";
		$result['message'] = "This Employee is already registered.";
	}else{
		file_put_contents($emp_img_url, base64_decode($emp_img));
		$insert_query_emp_dtl = "INSERT INTO emp_details (emp_code, emp_name, emp_email, emp_phone, emp_type, department, date_of_joining, emp_img_url) VALUES ('$emp_code', '$emp_name', '$emp_email', '$emp_phone', '$emp_type', '$department', '$date_of_joining', '$emp_img_url')";
		if(mysqli_query($conn, $insert_query_emp_dtl)){
			$result['success'] = "1";
			$result['message'] = $emp_name." Registered Successfully.";	
		}
		else{
			unlink($emp_img_url);
			$result['success'] = "0";
			$result['message'] = "Unknown Error occurred.";
		}
	}
	echo json_encode($result);
	mysqli_close($conn);
}
?>