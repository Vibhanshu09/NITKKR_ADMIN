<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    $result = array();
	$result['courses'] = array();
	
	require_once "connect.php";
	$sql_marks = "SELECT * FROM COURSE";
	
	$responce = mysqli_query($conn,$sql_marks);
	$course_count = 0;
	while($row = mysqli_fetch_assoc($responce)){
		$index['course_code'] = $row['course_code'];
		$index['course_name'] = $row['course_name'];
		$index['no_of_semester'] = $row['no_of_semester'];
		$result['courses'][] = $index;
		$course_count++;
	}
	if($course_count !== 0){
		$result['success'] = "1";
		$result['message'] = "Record Found";
		echo json_encode($result);
		mysqli_close($conn);
	} else{
		$result['success'] = "0";
		$result['message'] = "No any registered course found.";
		echo json_encode($result);
		mysqli_close($conn);
	}
}
?>