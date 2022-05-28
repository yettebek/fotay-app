<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    require_once("db.php");

    //real_escape_string: para prevenir la inyección por SQL
    $foto_id = mysqli_real_escape_string($mysql, $_POST['foto_id']);
    $usu_id = mysqli_real_escape_string($mysql, $_POST['usu_id']);
    $usu_nombre = mysqli_real_escape_string($mysql, $_POST['usu_nombre']);
    $fecha_coment = mysqli_real_escape_string($mysql, $_POST['fecha_coment']);
    $txt_coment = mysqli_real_escape_string($mysql, $_POST['txt_coment']);


    $insert_coment_query = "INSERT INTO comentarios (foto_id, usu_id, usu_nombre, fecha_coment, txt_coment) VALUES ('$foto_id', '$usu_id', '$usu_nombre', '$fecha_coment', '$txt_coment')";

    $insert_coment_result = mysqli_query($mysql, $insert_coment_query);

    if ($insert_coment_result === true) {
        echo "Exito";
    } else {
        echo "Error";
    }
}
