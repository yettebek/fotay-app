<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    require_once('db.php');

    $usu_id = mysqli_real_escape_string($mysql, $_POST['usu_id']);
    $usu_nombre = mysqli_real_escape_string($mysql, $_POST['usu_nombre']);

    $update_query_username_usuarios = "UPDATE usuarios SET usu_nombre = '$usu_nombre' WHERE usu_id = '$usu_id'";

    $update_query_username = "UPDATE usuarios, fotos SET usuarios.usu_nombre = '$usu_nombre', fotos.usu_nombre = '$usu_nombre' WHERE usuarios.usu_id = fotos.usu_id AND usuarios.usu_id = '$usu_id'";

    $result_one = mysqli_query($mysql, $update_query_username_usuarios);
    $result_two = mysqli_query($mysql, $update_query_username);

    if ($result_two || $result_one) {
        echo 'Nombre actualizado';
    } else {
        echo 'Error al cambiar el nombre';
    }
} else {
    echo "Error. Not a POST request.";
    $mysql->close();
    exit();
}
