<?php
require "db2.php";

function haversineGreatCircleDistance($latitudeFrom, $longitudeFrom, $latitudeTo, $longitudeTo, $earthRadius = 6371000) {
    // convert from degrees to radians
    $latFrom = deg2rad($latitudeFrom);
    $lonFrom = deg2rad($longitudeFrom);
    $latTo = deg2rad($latitudeTo);
    $lonTo = deg2rad($longitudeTo);

    $latDelta = $latTo - $latFrom;
    $lonDelta = $lonTo - $lonFrom;

    $angle = 2 * asin(sqrt(pow(sin($latDelta / 2), 2) +
            cos($latFrom) * cos($latTo) * pow(sin($lonDelta / 2), 2)));
    return $angle * $earthRadius;
}

$db = new DataBase();
if (isset($_POST['Longitude']) && isset($_POST['Latitude'])) {
    if ($db->dbConnect()) {
        $longitude = $_POST['Longitude'];
        $latitude = $_POST['Latitude'];
        $sql = "SELECT name, latitude, longitude FROM museums";
        $result = $db->query($sql);

        if ($result) {
            $found = false;
            while ($row = $result->fetch_assoc()) {
                $museumLatitude = $row['latitude'];
                $museumLongitude = $row['longitude'];
                $distance = haversineGreatCircleDistance($latitude, $longitude, $museumLatitude, $museumLongitude);

                if ($distance <= 5) { // 5 meters
                    $found = true;
                    $name = $row['name'];
                    echo "Museum Found;$name";
                    break;
                }
            }
            if (!$found) {
                echo "Error: Can't find a museum";
            }
        } else {
            echo "Error: SQL query failed: " . $db->connection->error;
        }
    } else {
        echo "Error: Database connection failed";
    }
} else {
    echo "All fields are required";
}
?>
