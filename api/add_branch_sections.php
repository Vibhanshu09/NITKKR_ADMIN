<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
	$course_code = $_POST['course_code'];
	$branch_code = $_POST['branch_code'];
	$semester = intval($_POST['semester']);
	$no_of_sec = intval($_POST['no_of_sec']);


    require_once "connect.php";

	$result = array();
	$insert_query = "INSERT INTO sections VALUES ('$course_code', '$branch_code', '$semester', '$no_of_sec')";
	if (mysqli_query($conn,$insert_query)) {
		$result['success'] = "1";
		$result['message'] = "Section added successfully";
	} else {
		$result['success'] = "0";
		$result['message'] = "Duplicate Entry not allowed";
	}
	echo json_encode($result);
	mysqli_close($conn);
}
?>