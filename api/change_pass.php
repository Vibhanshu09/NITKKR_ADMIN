<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
	$emp_code = $_POST['emp_id'];
	$emp_old_pass = $_POST['old_password'];
	$emp_new_pass = $_POST['new_password'];

	//$conn = new mysqli("localhost", "id8772309_vibhanshu", "vibhanshu", "id8772309_student");
    require_once "connect.php";
	$sql = "SELECT * FROM USER_ADMIN WHERE emp_code = '$emp_code'";
	$responce = mysqli_query($conn,$sql);
	$result = array();
	if (mysqli_num_rows($responce) === 1) {
		$row = mysqli_fetch_assoc($responce);
		if (password_verify($emp_old_pass, $row['password'])) {
		//if ($emp_old_pass === $row['password']) {
			$enc_pass = password_hash($emp_new_pass, PASSWORD_BCRYPT);
			$update_query = "UPDATE USER_ADMIN SET password = '$enc_pass' WHERE emp_code = '$emp_code'";
			$update_responce = mysqli_query($conn,$update_query);
			if ($update_responce) {
				$result['success'] = "1";
				$result['message'] = "Password Updated Successfully";
			} else {
				$result['success'] = "0";
				$result['message'] = "Unable to update";
			}
			
		} else {
			$result['success'] = "0";
			$result['message'] = "Old password does not match";
		}
	} else{
		$result['success'] = "0";
		$result['message'] = "No Such User";
	}
	echo json_encode($result);
	mysqli_close($conn);
}
?>