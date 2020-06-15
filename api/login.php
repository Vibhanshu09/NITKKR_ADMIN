<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
	$emp_id = $_POST['emp_id'];
	$emp_pass = $_POST['password'];
    require_once "connect.php";
	$sql = "SELECT * FROM USER_ADMIN WHERE emp_code = '$emp_id'";
	$responce = mysqli_query($conn,$sql);

	$result = array();
	$result['login'] = array();
	if (mysqli_num_rows($responce) === 1) {
		$row = mysqli_fetch_assoc($responce);
		if (password_verify($emp_pass, $row['password'])) {
		//if ($emp_pass === $row['password']) {
			$index['emp_id'] = $row['emp_code'];
			$index['name'] = $row['name'];
			$index['email'] = $row['email'];
			array_push($result['login'], $index);
			$result['success'] = "1";
			$result['message'] = "Success";
		} else {
			$result['success'] = "0";
			$result['message'] = "Password Not Matched";
		}
	} else {
		$result['success'] = "0";
		$result['message'] = "Invalid ID";
	}
	echo json_encode($result);
	mysqli_close($conn);
}
?>