<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
	$eno = $_POST['eno'];
	$receipt_no = $_POST['receipt_no'];
	$paid_date = $_POST['paid_date'];
	$amount_paid = intval($_POST['amount_paid']);
	$amount_due = intval($_POST['amount_due']);

    require_once "connect.php";

	$result = array();
	$select_query = "SELECT * FROM stud_bsk_dtl WHERE e_no = '$eno'";
	$check_eno = mysqli_query($conn,$select_query);
	if (mysqli_num_rows($check_eno) === 1) {
		$insert_fee_details = "INSERT INTO fee_dtl VALUES ('$eno', '$receipt_no', '$paid_date', '$amount_paid', '$amount_due')";
		if (mysqli_query($conn, $insert_fee_details)) {
			$result['success'] = "1";
			$result['message'] = "Fee details updated for roll no : " . $eno;
		}else{
			$result['success'] = "0";
			$result['message'] = "Unknown Error occurred.";
		}
		
	}else{
		$result['success'] = "0";
		$result['message'] = $eno." is not found in Database";	
	}
	echo json_encode($result);
	mysqli_close($conn);
}
?>