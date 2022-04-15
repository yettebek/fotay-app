<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    require_once('db.php');

    //real_escape_string: para prevenir la inyección por SQL
    $usu_nombre = $_POST['usu_nombre'];
    $photo_path =  $_POST['foto_ruta'];
    $photo_comment =  $_POST['foto_coment'];

    $timestamp = time();
    $photo_name = "fotay_" . $timestamp . ".jpg";
    $WEBHOST_URL = "https://fotay.000webhostapp.com/files/php/$photo_name";

    #insercción de la imágen en la BBDD
    $insert_image_query = "INSERT INTO fotos (usu_nombre, foto_fecha, foto_coment, foto_ruta) VALUES('$usu_nombre',NOW(),'$photo_comment','$WEBHOST_URL')";

    #ver si se han insertado los datos en la BBDD
    $result_insert = $mysql->query($insert_image_query);

    if ($result_insert === true) {
        file_put_contents($photo_name, base64_decode($photo_path));
    }

    $mysql->close();

}
?>