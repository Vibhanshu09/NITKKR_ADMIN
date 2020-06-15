<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
	$version_code = $_POST['version_code'];

    $result = array();
	
	require_once "connect.php";
	$app_dtl = "SELECT * FROM ADMIN_APP_INFO";
	
	$responce = mysqli_query($conn,$app_dtl);

	if (mysqli_num_rows($responce) === 1) {
		$row = mysqli_fetch_assoc($responce);
		if($row['version_code'] == $version_code){
			$result['success'] = "2";
			$result['app_status'] = "UPDATED";
		} else{
			$result['success'] = "1";
			$result['app_status'] = "OLD_VERSION";
			$result['app_url'] = $row['app_url'];
		}
		
		echo json_encode($result);
		mysqli_close($conn);
	} else {
		$result['success'] = "0";
		$result['message'] = "No Record Found";
		echo json_encode($result);
		mysqli_close($conn);
	}
}
?>