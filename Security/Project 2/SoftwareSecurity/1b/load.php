<?php
	
	include_once "dbConnect.php";
	
	error_reporting(0);
	
	$dbconn = pg_connect_db();
	
	$result = pg_prepare($dbconn, "", "SELECT firstName, lastName, value, expression, s.accountId, s.id FROM account a, solution s WHERE a.id=s.accountId AND value=$1 ORDER BY firstName, lastName, expression");
	
	$result = pg_execute($dbconn, "", array($i));
	
	while ($row = pg_fetch_row($result)) {
		
		$count=0;
		$firstName=$row[$count++];
		$lastName=$row[$count++];
		$value=$row[$count++];
		$expression=$row[$count++];
		$expressionAccountId=$row[$count++];
		$expressionId=$row[$count++];
		
		if($expressionAccountId==$g_accountId){
			$deleteLink="
			<a href=\"\" onclick=\"

			javascript: 
			
			window.location.href='?operation=deleteExpression&expressionId=$expressionId&accountId=$g_accountId';
			
			window.location.href='index.php';\">
			
			<img src=\"delete.png\" width=\"20\" border=\"0\" />
			
			</a>";
		} 
		else {
			
			$deleteLink="";
		}
		
		echo("<tr> <td>$expression</td><td>$deleteLink</td><td>$firstName $lastName</td></tr>");
	}
?>