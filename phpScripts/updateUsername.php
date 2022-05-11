<?php
if($_SERVER['REQUEST_METHOD'] == 'POST'){
    require_once('db.php');

    $usu_id = mysqli_real_escape_string($mysql, $_POST['usu_id']);
    $usu_nombre = mysqli_real_escape_string($mysql, $_POST['usu_nombre']);

    $update_query_usuarios = "UPDATE usuarios SET usu_nombre = '$usu_nombre' WHERE usu_id = '$usu_id'";

    $update_query_fotos = "UPDATE fotos f INNER JOIN usuarios u ON f.foto_id = u.usu_id SET f.usu_nombre = u.usu_nombre";

    $result_update = mysqli_query($mysql, $update_query_usuarios);
    $result_update_fotos = mysqli_query($mysql, $update_query_fotos);

    if($result_update && $result_update_fotos){
        echo 'Nombre actualizado';
    }else{
        echo 'Error al cambiar el nombre';
    }
}else{
    echo "Error. Not a POST request.";
    $mysql->close();
    exit();
}