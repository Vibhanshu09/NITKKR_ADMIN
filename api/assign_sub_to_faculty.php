<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {

	$subject = $_POST['subject'];
	$branch_code = $_POST['branch_code'];
	$course_code = $_POST['course_code'];
	$semester = intval($_POST['semester']);
	$section = intval($_POST['section']);
	$faculty = $_POST['faculty'];
	$nol_assigned = intval($_POST['nol_assigned']);
	
    $result = array();
	
	require_once "connect.php";
	$sql_subject_assign = "INSERT INTO subject_allotment VALUES ('$subject', '$branch_code', '$course_code', '$semester', '$section', '$faculty', '$nol_assigned', 0)";
	
	if(mysqli_query($conn,$sql_subject_assign)){
		$result['success'] = "1";
		$result['message'] = "Record Found";
	}else{
		$result['success'] = "0";
		$result['message'] = "Error in assigning subject";
	}

	echo json_encode($result);
	mysqli_close($conn);

}
?>