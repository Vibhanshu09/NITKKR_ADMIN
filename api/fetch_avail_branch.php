<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
	$course_code = $_POST['course_code'];
    $result = array();
	$result['branches'] = array();
	
	require_once "connect.php";
	if($course_code === "fetch_all"){
		$sql_marks = "SELECT branch_code, branch_name FROM BRANCH";	
	}else{
		$sql_marks = "SELECT branch_code, branch_name FROM BRANCH WHERE course_code = '$course_code'";
	}
	
	$responce = mysqli_query($conn,$sql_marks);
	$branch_count = 0;
	while($row = mysqli_fetch_assoc($responce)){
		$index['branch_code'] = $row['branch_code'];
		$index['branch_name'] = $row['branch_name'];
		$result['branches'][] = $index;
		$branch_count++;
	}
	if($branch_count !== 0){
		$result['success'] = "1";
		$result['message'] = "Record Found";
		echo json_encode($result);
		mysqli_close($conn);
	}else{
		$result['success'] = "0";
		$result['message'] = "No any registered branch found.";
		echo json_encode($result);
		mysqli_close($conn);
	}
}
?>