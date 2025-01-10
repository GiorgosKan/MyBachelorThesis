<?php

global $connection;
require 'db1.php';
$name = $_POST['name'];
$country = $_POST['country'];
$city = $_POST['city'];
$latitude = $_POST['latitude'];
$longitude  = $_POST['longitude'];


$query = "INSERT INTO `museums` (name,country,city,latitude,longitude) VALUES('$name','$country','$city','$latitude','$longitude')";


$result = mysqli_query($connection,$query);

if ($result){
    echo "Data Inserted";
} else{
    "Data Not Inserted";
}
?>