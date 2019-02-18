<?php
	require_once 'db_config.php';

	$email = $_GET['email'];

	$sql = "SELECT * FROM user WHERE email = '$email'";
	$result = mysqli_query($con, $sql);

	if ($result) {
		$response['error'] = false;
		while ($data = mysqli_fetch_array($result)) {
			$tmp = array();
			$tmp['name'] = $data['name'];
			$tmp['email'] = $data['email'];
			$tmp['phoneNumber'] = $data['phone_number'];
			$tmp['college'] = $data['college'];
		}

		echo json_encode($tmp);
	}
	else{
		$response['error'] = true ;
		$response['message'] = "There is no user info in database";
		echo json_encode($response);
	}
?>