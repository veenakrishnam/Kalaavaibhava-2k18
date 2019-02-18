<?php
	require_once 'db_config.php';

	$eid = $_GET['id'];

	$sql = "SELECT * FROM event WHERE id = '$eid'";
	$result = mysqli_query($con, $sql);

	if ($result) {
		$response['error'] = false;
		while ($data = mysqli_fetch_array($result)) {
			$tmp = array();
			$tmp['id'] = $data['id'];
			$tmp['title'] = $data['title'];
			$tmp['date'] = $data['date'];
			$tmp['time'] = $data['time'];
			$tmp['quote'] = $data['quote'];
			$tmp['rules'] = $data['rules'];
			$tmp['student_co_ordinators'] = $data['student_co_ordinators'];
			$tmp['faculty_co_ordinators'] = $data['faculty_co_ordinators'];
			$tmp['imageUrl'] = $data['imageUrl'];
		}

		echo json_encode($tmp);
	}
	else{
		$response['error'] = true ;
		$response['message'] = "There is no event info in database";
		echo json_encode($response);
	}
?>