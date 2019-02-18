<?php
	require_once 'db_config.php';

	$sql = "SELECT * FROM crew";
	$result = mysqli_query($con, $sql);

	if ($result) {
		$response['error'] = false;
		$response['crews'] = array();
		while ($data = mysqli_fetch_array($result)) {
			$tmp = array();
			$tmp['id'] = $data['id'];
			$tmp['name'] = $data['name'];
			$tmp['semester_department'] = $data['semester_department'];
			$tmp['contactNumber'] = $data['contactNumber'];
			$tmp['email'] = $data['email'];
			$tmp['imageUrl'] = $data['imageUrl'];

			array_push($response['crews'], $tmp);
		}

		echo json_encode($response['crews']);
	}
	else{
		$response['error'] = true ;
		$response['message'] = "There is no crews info in database";
		echo json_encode($response);
	}
?>