<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {

	$branch_code = $_POST['branch_code'];
	$branch_name = $_POST['branch_name'];
	
    $result = array();
	$result['subjects'] = array();
	$result['faculties'] = array();
	
	require_once "connect.php";
	$sql_subjects = "SELECT * FROM subject WHERE branch_code = '$branch_code'";
	$sql_faculties = "SELECT * FROM emp_details WHERE department = '$branch_name' AND currently_working = 1 AND emp_type = 'Faculty'";
	
	$subject_responce = mysqli_query($conn,$sql_subjects);
	$faculty_responce = mysqli_query($conn,$sql_faculties);
	while($row = mysqli_fetch_assoc($subject_responce)){
		$index['subject_code'] = $row['sub_code'];
		$index['subject_name'] = $row['sub_name'];
		$result['subjects'][] = $index;

	}
	while($row = mysqli_fetch_assoc($faculty_responce)){
		$index1['faculty_name'] = $row['emp_name'];
		$index1['faculty_code'] = $row['emp_code'];
		$result['faculties'][] = $index1;
	}
	$result['success'] = "1";
	$result['message'] = "Record Found";
	echo json_encode($result);
	mysqli_close($conn);

}
?>