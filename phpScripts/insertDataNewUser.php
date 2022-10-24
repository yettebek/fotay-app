<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    require_once("db.php");
    session_start();

    //real_escape_string: para prevenir la inyección por SQL
    $usu_nombre = mysqli_real_escape_string($mysql, $_POST['usu_nombre']);
    $usu_contrasena = mysqli_real_escape_string($mysql, $_POST['usu_contrasena']);

    //crear una sessión para el usuario
    $_SESSION['usu_nombre'] = $usu_nombre;

    $check_duplicate_username = "SELECT usu_id, usu_nombre FROM usuarios WHERE usu_nombre = '$usu_nombre'";

    $response = $mysql->query($check_duplicate_username);

    $count = mysqli_num_rows($response);

    $result = array();
    $result['register'] = array();

    if ($result['register'] == null) {

        $query_insert = "INSERT INTO usuarios (usu_nombre, usu_contrasena) VALUES ('$usu_nombre','$usu_contrasena')";

        $query_select = "SELECT usu_id, usu_nombre FROM usuarios WHERE usu_nombre = '$usu_nombre' ";

        $response = $mysql->query($query_insert);
        $response2 = $mysql->query($query_select);

        if ($response && $response2) {
            while ($row = $response2->fetch_assoc()) {
                $column['usu_id'] = $row['usu_id'];
                $column['usu_nombre'] = $row['usu_nombre'];

                $result['success'] = "1";
                $result['message'] = "User created";

                array_push($result['register'], $column);
                echo json_encode($result, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
            }
        } else {
            $result['success'] = "3";
            $result['message'] = "Error creating user";

            echo json_encode($result, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

            return false;
            $mysql->close();
        }
    } else {
        $result['success'] = "0";
        $result['message'] = "User already exists";

        echo json_encode($result, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

        return false;
        $mysql->close();
    }
    $mysql->close();
}
