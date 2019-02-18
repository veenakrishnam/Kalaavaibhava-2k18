<?php
	require_once 'db_config.php';

	//$content   = file_get_contents("php://input");
	//$data  = json_decode($content, TRUE);

	$name = $_GET['name'];
	$email = $_GET['email'];
	$phone_number = $_GET['phoneNumber'];
	$college = $_GET['college'];

	$response = array();

	if ($email != "") {
		$sql = "INSERT INTO user (name, email, phone_number, college) VALUES ('$name', '$email', '$phone_number', '$college')";
		echo $sql;

		$result = mysqli_query($con, $sql);

		if ($result) {
			$response['error'] = false;
			$response['message'] = "Successfully created the user";

			echo json_encode($response);
		}
		else{
			$response['error'] = true;
			$response['message'] = "Failed to create new user";

			echo json_encode($response);
		}	
	}
?>