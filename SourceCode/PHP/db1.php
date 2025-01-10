<?php

$host = "localhost";
$user="root";
$password ="";
$database = "culturalcompass";

$connection = mysqli_connect($host,$user,$password,$database);

if (!$connection) {
    echo ("Not Connected");
}

?>