<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
	$branch_code = $_POST['branch_code'];
	$subject_code = $_POST['subject_code'];
	$subject_name = $_POST['subject_name'];
	$subject_type = $_POST['subject_type'];

    require_once "connect.php";

	$result = array();
	$select_query = "SELECT * FROM subject WHERE sub_code = '$subject_code'";
	$check_eno = mysqli_query($conn,$select_query);
	if (mysqli_num_rows($check_eno) === 1) {
		$result['success'] = "0";
		$result['message'] = $subject_code." is already registered";		
	}else{
		$insert_subject = "INSERT INTO subject VALUES ('$branch_code', '$subject_code', '$subject_name', '$subject_type')";
		if (mysqli_query($conn, $insert_subject)) {
			$result['success'] = "1";
			$result['message'] = "New subject updated : " . $subject_name;
		}
		else{
			$result['success'] = "0";
			$result['message'] = "Unknon error occured";
		}
	}
	echo json_encode($result);
	mysqli_close($conn);
}
?>