<?php
	define('DB_NAME', 'kalaavaibhava2k18');
	define('DB_USER', 'root');
	define('DB_PASSWORD', '');
	define('DB_HOST', 'localhost');

	$dblink = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD);
	if (!$dblink) {
		die('Could not connect'. mysql_error());
	}

	$db_selected = mysqli_select_db($dblink, DB_NAME);
	if (!$db_selected) {
		die('Can\'t use '. 'DB_NAME' . ':' . mysql_error());
	}
?>