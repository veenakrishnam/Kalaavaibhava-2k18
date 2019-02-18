<?php
	require_once 'db_config.php';

	$sql = "SELECT * FROM sponsor";
	$result = mysqli_query($con, $sql);

	if ($result) {
		$response['error'] = false;
		$response['sponsors'] = array();
		while ($data = mysqli_fetch_array($result)) {
			$tmp = array();
			$tmp['id'] = $data['id'];
			$tmp['name'] = $data['name'];
			$tmp['imageUrl'] = $data['imageUrl'];

			array_push($response['sponsors'], $tmp);
		}

		echo json_encode($response['sponsors']);
	}
	else{
		$response['error'] = true ;
		$response['message'] = "There is no sponsors info in database";
		echo json_encode($response);
	}
?>