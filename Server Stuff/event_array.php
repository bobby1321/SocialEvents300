<?php
$mysqli = new mysqli("localhost","","","test");
$query = "SELECT * FROM table1";

if ($dblink->connect_errno) {
     echo "Failed to connect to database";
     exit();
}
$result = $mysqli -> query($query);
$dbdata = array();
while ( $row = $result->fetch_assoc())  {
        $dbdata[]=$row;
}
$myJson = json_encode($dbdata);
echo $myJson;
?>
