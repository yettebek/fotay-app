<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    require_once('db.php');

    //real_escape_string: para prevenir la inyección por SQL
    $usu_id = $_POST['usu_id'];
    $usu_nombre = $_POST['usu_nombre'];
    $photo_path =  $_POST['foto_ruta'];
    $photo_comment =  $_POST['foto_coment'];
    $timestamp = time();

    #consulta para recoger el id de la foto y añaadirlo al nombre de la foto
    $select_image_query = "SELECT foto_id FROM fotos ORDER BY foto_id ASC";
    $result_select = $mysql->query($select_image_query);

    while ($row = $result_select->fetch_array()) {
        $default_photo_id = $row['foto_id'];
    }
    $photo_name = "images/fotay_" . $default_photo_id . $timestamp . ".jpg";

    $WEBHOST_URL = "https://fotay.000webhostapp.com/$photo_name";

    #insercción de la imágen en la BBDD
    $insert_image_query = "INSERT INTO fotos (usu_id, usu_nombre, foto_fecha, foto_coment, foto_ruta) VALUES('$usu_id','$usu_nombre',NOW() + INTERVAL 2 HOUR,'$photo_comment','$WEBHOST_URL')";

    #ver si se han insertado los datos en la BBDD
    $result_insert = $mysql->query($insert_image_query);

    if ($result_insert === true) {
        file_put_contents($photo_name, base64_decode($photo_path));
    }

    $mysql->close();

}
?>