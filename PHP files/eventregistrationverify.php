<?php
	require_once 'db_config.php';

	$event_id = $_GET['event_id'];
	$user_email = $_GET['email'];

	$sql = "SELECT * FROM eventregistration WHERE event_id = '$event_id' AND user_email = '$user_email'";
	$result = mysqli_query($con, $sql);

	$tmp['user_email'] = "";
	if ($result) {
		while ($data = mysqli_fetch_array($result)) {
			$tmp['user_email'] = $data['user_email'];
		}

		echo json_encode($tmp);
	}
	else{
		$tmp['user_email'] = "";

		echo json_encode($tmp);
	}
?>