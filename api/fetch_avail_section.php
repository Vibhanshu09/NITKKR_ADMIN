<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {

	$course_code = $_POST['course_code'];
	$branch_code = $_POST['branch_code'];
	$semester = intval($_POST['semester']);

    $result = array();

	require_once "connect.php";

	$sql_section = "SELECT * FROM sections WHERE course_code = '$course_code' AND branch_code = '$branch_code' AND semester = '$semester'";
	
	$responce = mysqli_query($conn,$sql_section);
	
	if($row = mysqli_fetch_assoc($responce)){
		$result['success'] = "1";
		$result['message'] = "Section found.";
		$result['section'] = $row['section'];
	}else{
		$result['success'] = "0";
		$result['message'] = "Section not found.";
	}
	echo json_encode($result);
	mysqli_close($conn);
}
?>