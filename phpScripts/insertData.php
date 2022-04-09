<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    require_once("db.php");
    
    //real_escape_string: para prevenir la inyección por SQL
    $usu_nombre = mysqli_real_escape_string($mysql, $_POST['usu_nombre']);
    $usu_contrasena = mysqli_real_escape_string($mysql, $_POST['usu_contrasena']);

    $check_duplicate_username = "SELECT usu_nombre FROM usuarios WHERE usu_nombre = '$usu_nombre' ";

    $result = $mysql->query($check_duplicate_username);

    $count = mysqli_num_rows($result);

    if ($count > 0) {
        echo "Username already exists.";
        return false;
        $mysql->close();
    }else {
        $query_insert = "INSERT INTO usuarios (usu_nombre, usu_contrasena) VALUES ('$usu_nombre','$usu_contrasena')";

        $result = $mysql->query($query_insert);

        if ($result === true) {
            echo "User has been created.";
        }else {
            echo "Error. User has not been created.";
            return false;
        }
    }
    $mysql->close();
}
?>
