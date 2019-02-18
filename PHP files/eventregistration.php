<?php
	require_once 'db_config.php';

	//$content   = file_get_contents("php://input");
	//$data  = json_decode($content, TRUE);

	$event_id = $_GET['event_id'];
	$user_email = $_GET['email'];

	$response = array();

	$sql = "SELECT * FROM eventregistration WHERE event_id = '$event_id' AND user_email = '$user_email'";
	$result = mysqli_query($con, $sql);

	$tmp['user_email'] = "";
	if ($result) {
		while ($data = mysqli_fetch_array($result)) {
			$tmp['user_email'] = $data['user_email'];
		}

		//echo json_encode($tmp);
	}
	else{
		$tmp['user_email'] = "";

		//echo json_encode($tmp);
	}

	if ($tmp['user_email'] == "") {
		if ($user_email != "") {
			$sql = "INSERT INTO eventregistration (event_id, user_email, paid_status) VALUES ('$event_id', '$user_email', 'unpaid')";
			//echo $sql;

			$result = mysqli_query($con, $sql);

			if ($result) {
				$response['error'] = false;
				$response['message'] = "Successfully registered for the event";

				echo json_encode($response);
			}
			else{
				$response['error'] = true;
				$response['message'] = "Failed to register for the event";

				echo json_encode($response);
			}	
		}
	}
	else{
		$response['error'] = false;
		$response['message'] = "User already registered for this event";

		echo json_encode($response);
	}
?>