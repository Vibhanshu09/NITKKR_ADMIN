<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
	$course_code = $_POST['course_code'];
	$course_name = $_POST['course_name'];
	$no_of_sem = $_POST['no_of_sem'];
	$no_of_semester = intval($no_of_sem);

	
    require_once "connect.php";
	$select_query = "SELECT * FROM course WHERE course_code = '$course_code'";
	$check_course = mysqli_query($conn,$select_query);
	$result = array();
	if (mysqli_num_rows($check_course) === 1) {
		$result['success'] = "0";
		$result['message'] = "This course already exist.";

	}else{
		$insert_query = "INSERT INTO course (course_code, course_name, no_of_semester) VALUES ('$course_code', '$course_name', '$no_of_semester')";
		if(mysqli_query($conn, $insert_query)){
			$result['success'] = "1";
			$result['message'] = $course_name." registered Successfully.";
		}
		else{
			$result['success'] = "0";
			$result['message'] = "Unknown Error occurred.";
		}

	}
	echo json_encode($result);
	mysqli_close($conn);
}
?>