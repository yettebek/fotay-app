<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    require_once("db.php");

    //real_escape_string: para prevenir la inyección por SQL
    $usu_nombre = mysqli_real_escape_string($mysql, $_POST['usu_nombre']);
    $usu_contrasena = mysqli_real_escape_string($mysql, $_POST['usu_contrasena']);

    $check_credentials_query = "SELECT usu_id, usu_nombre, usu_contrasena FROM usuarios WHERE usu_nombre = '$usu_nombre' AND usu_contrasena = '$usu_contrasena' ";

    $response = $mysql->query($check_credentials_query);

    $count = mysqli_num_rows($response);

    $result = array();
    $result['login'] = array();

    if ($count > 0) {
        while ($row = $response->fetch_assoc()) {

            $column['usu_id'] = $row['usu_id'];
            $column['usu_nombre'] = $row['usu_nombre'];

            $result['success'] = "1";
            $result['message'] = "success";

            array_push($result['login'], $column);
            echo json_encode($result);
        }
    } else {
        $result['success'] = "0";
        $result['message'] = "error";

        echo json_encode($result);
    }
}
