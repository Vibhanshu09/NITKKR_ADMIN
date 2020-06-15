<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
	$course_code = $_POST['course_code'];
	$branch_code = $_POST['branch_code'];
	$branch_name = $_POST['branch_name'];
	

    require_once "connect.php";

	$result = array();
	$select_query = "SELECT * FROM branch WHERE branch_code = '$branch_code'";
	$check_branch = mysqli_query($conn,$select_query);
	if (mysqli_num_rows($check_branch) === 1) {
		$result['success'] = "0";
		$result['message'] = "This branch already exist.";
	}else{
		$insert_query = "INSERT INTO branch VALUES ('$course_code', '$branch_code', '$branch_name')";
		if(mysqli_query($conn, $insert_query)){
			$result['success'] = "1";
			$result['message'] = $branch_name." registered Successfully.";
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