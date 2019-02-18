<?php
	require_once 'db_config.php';

	$sql = "SELECT * FROM event";
	$result = mysqli_query($con, $sql);

	if ($result) {
		$response['error'] = false;
		$response['events'] = array();
		while ($data = mysqli_fetch_array($result)) {
			$tmp = array();
			$tmp['id'] = $data['id'];
			$tmp['title'] = $data['title'];
			$tmp['date'] = $data['date'];
			$tmp['time'] = $data['time'];
			$tmp['imageUrl'] = $data['imageUrl'];

			array_push($response['events'], $tmp);
		}

		echo json_encode($response['events']);
	}
	else{
		$response['error'] = true ;
		$response['message'] = "There is no events info in database";
		echo json_encode($response);
	}
?>