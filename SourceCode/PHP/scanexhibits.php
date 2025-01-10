<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);
header('Content-Type: application/json; charset=utf-8');

require "db2.php";
$db = new DataBase();
$response = array();

try {
    // Καταγραφή των δεδομένων που λαμβάνονται από το αίτημα GET
    file_put_contents('php://stderr', print_r($_GET, TRUE));

    if (isset($_GET['exhibitId'])) {
        $exhibitId = $_GET['exhibitId'];

        if ($db->dbConnect()) {
            $sql = "SELECT name, info, path FROM exhibits WHERE id = ?";
            $stmt = $db->connection->prepare($sql);
            if ($stmt) {
                $stmt->bind_param("i", $exhibitId);
                $stmt->execute();
                $result = $stmt->get_result();

                if ($result) {
                    if ($result->num_rows > 0) {
                        $row = $result->fetch_assoc();
                        $response = array(
                            "name" => $row['name'],
                            "information" => $row['info'],
                            "path" => "http://192.168.139.1/CulturalCompass/" . $row['path']
                        );
                    } else {
                        $response = array("error" => "Exhibit not found");
                    }
                } else {
                    $response = array("error" => "Query execution failed");
                }
                $stmt->close();
            } else {
                $response = array("error" => "Query preparation failed");
            }
        } else {
            $response = array("error" => "Database connection failed");
        }
    } else {
        $response = array("error" => "All fields are required");
    }
} catch (Exception $e) {
    $response = array("error" => "An unexpected error occurred: " . $e->getMessage());
}

echo json_encode($response, JSON_UNESCAPED_UNICODE);
?>
