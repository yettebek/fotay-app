<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    require_once("db.php");
    
    //real_escape_string: para prevenir la inyección por SQL
    $usu_nombre = mysqli_real_escape_string($mysql, $_POST['usu_nombre']);
    $usu_contrasena = mysqli_real_escape_string($mysql, $_POST['usu_contrasena']);

    $check_credentials_query = "SELECT usu_nombre, usu_contrasena FROM usuarios WHERE usu_nombre = '$usu_nombre' AND usu_contrasena = '$usu_contrasena' ";

    $result = $mysql->query($check_credentials_query);

    $count = mysqli_num_rows($result);

    if ($count > 0) {
        echo "Usuario encontrado";
    } else {
        echo "Error. Usuario no encontrado";
        return false;
        $mysql->close();
    }
    $mysql->close();
}
?>
