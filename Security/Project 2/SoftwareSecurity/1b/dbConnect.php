<?php
	
	error_reporting(0);
	
	// overridden database functions
	function pg_connect_db(){
		
		$dbconn = pg_connect("dbname=fourfours user=ff host=localhost password=adg135sfh246");
		
		pg_set_client_encoding($dbconn, 'UTF8');
		return $dbconn;
	}
	
?>