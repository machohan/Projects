<?php
	
	include_once "dbConnect.php";
	
	error_reporting(0);
	
	if(!isset($_SESSION['isLoggedIn']))
		$_SESSION['isLoggedIn']=False;
	
	$operation= addslashes($_REQUEST['operation']);
	$g_debug="";
	$g_errors="";
	
	if($operation == "login"){
		$user= addslashes($_REQUEST['user']);
		$password= addslashes($_REQUEST['password']);
		
		$dbconn = pg_connect_db();
		$result = pg_prepare($dbconn, "login", 'SELECT id, username, firstName, lastName, passwd FROM account WHERE username=$1 AND passwd=$2');
		$result = pg_execute($dbconn, "login", array($user,$password));
		
		if($row = pg_fetch_row($result)) {
			$_SESSION['accountId']=$row[0];
			$_SESSION['user']=$row[1];
			$_SESSION['firstName']=$row[2];
			$_SESSION['lastName']=$row[3];
			$_SESSION['isLoggedIn']=True;
		} else {
			$g_debug = "Not logged in";
			$_SESSION['isLoggedIn']=False;
		}
		
	} elseif($operation == "deleteExpression"){
		$expressionId = addslashes($_REQUEST['expressionId']);
		$accountId= addslashes($_REQUEST['accountId']); 
		
		$dbconn = pg_connect_db();
		$result = pg_prepare($dbconn, "delete", 'DELETE FROM solution WHERE id=$1 AND accountId=$2');
		$result = pg_execute($dbconn, "delete", array($expressionId,$accountId));
		
	} elseif($operation == "addExpression"){
		$expression = addslashes($_REQUEST['expression']);
		$value= addslashes($_REQUEST['value']);
		$accountId= addslashes($_REQUEST['accountId']);

		$dbconn = pg_connect_db();
		$result = pg_prepare($dbconn, "add", 'SELECT * FROM solution WHERE expression=$1');
		$result = pg_execute($dbconn, "add", array($expression));
		
		if(!($row = pg_fetch_row($result))) {
			$result = pg_prepare($dbconn, "", 'insert into solution (value, expression, accountId) values ($1, $2, $3)');
			$result = pg_execute($dbconn, "", array($value,$expression,$accountId));
			
		} else {
			$g_errors="$expression is already in our database";
		}
		
	} elseif($operation == "logout"){
		unset($_SESSION);
		$_SESSION['isLoggedIn']=False;
		session_destroy();
	}
	
	$g_isLoggedIn=$_SESSION['isLoggedIn']; 
	$g_index="";
	for($i=0;$i<=100;$i+=10){ $g_index=$g_index . "<a href=#$i>$i</a> "; } 
	$g_userFullName=$_SESSION['firstName'] . " " . $_SESSION['lastName'];
	$g_userFirstName=$_SESSION['firstName'];
	$g_accountId=$_SESSION['accountId'];
?>