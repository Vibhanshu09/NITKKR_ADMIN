<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
	$eno = $_POST['eno'];
	$name = $_POST['name'];
	$email = $_POST['email'];
	$stud_mob = $_POST['stud_mob'];
	$parent_mob = $_POST['parent_mob'];
	$father_name = $_POST['father_name'];
	$mother_name = $_POST['mother_name'];
	$category = $_POST['category'];
	$gender = $_POST['gender'];
	$dob = $_POST['dob'];
	$date_of_admission = $_POST['date_of_admission'];
	$p_address = $_POST['p_address'];
	$c_address = $_POST['c_address'];
	$qualification_10 = $_POST['qualification_10'];
	$stream_10 = $_POST['stream_10'];
	$pass_year_10 = $_POST['pass_year_10'];
	$collage_10 = $_POST['collage_10'];
	$percentage_10 = $_POST['percentage_10'];
	$qualification_12 = $_POST['qualification_12'];
	$stream_12 = $_POST['stream_12'];
	$pass_year_12 = $_POST['pass_year_12'];
	$collage_12 = $_POST['collage_12'];
	$percentage_12 = $_POST['percentage_12'];
	$course = $_POST['course'];
	$branch = $_POST['branch'];
	$curr_sem = $_POST['curr_sem'];
	$curr_sec = $_POST['curr_sec'];
	$stud_img = $_POST['stud_img'];

	

	$curr_semester = intval($curr_sem);
	$curr_section = intval($curr_sec);

    require_once "connect.php";

    $stud_img_upload_dir = "../images/stud_images";
    if (!file_exists($stud_img_upload_dir)) {
    	mkdir($stud_img_upload_dir, 0777, true);
    }
    $stud_img_url = $stud_img_upload_dir . "/" . $eno . ".jpeg";
	$result = array();
	$select_query = "SELECT * FROM stud_bsk_dtl WHERE e_no = '$eno'";
	$check_student = mysqli_query($conn,$select_query);
	if (mysqli_num_rows($check_student) === 1) {
		$result['success'] = "0";
		$result['message'] = "This student is already registered.";
	}else{
		file_put_contents($stud_img_url, base64_decode($stud_img));
		$insert_query_bsk_dtl = "INSERT INTO stud_bsk_dtl VALUES ('$eno', '$name', '$email', '$stud_mob', '$parent_mob', '$father_name', '$mother_name', '$category', '$gender', '$dob', '$date_of_admission', '$p_address', '$c_address', '$stud_img_url')";
		if(mysqli_query($conn, $insert_query_bsk_dtl)){
			$insert_query_qualification = "INSERT INTO stud_qualification_rec VALUES ('$eno', '$qualification_10', '$stream_10', '$pass_year_10', '$collage_10', '$percentage_10'), ('$eno', '$qualification_12', '$stream_12', '$pass_year_12', '$collage_12', '$percentage_12')";
			$insert_query_acadmic = "INSERT INTO stud_acdmic_dtl VALUES ('$eno', '$course', '$branch', '$curr_semester', '$curr_section')";
			if(mysqli_query($conn, $insert_query_qualification) && mysqli_query($conn, $insert_query_acadmic)){
				$result['success'] = "1";
				$result['message'] = $name." Registered Successfully.";	
			}
		}
		else{
			unlink($stud_img_url);
			$result['success'] = "0";
			$result['message'] = "Unknown Error occurred.";
		}
	}
	echo json_encode($result);
	mysqli_close($conn);
}
?>