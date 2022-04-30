<?php
if($_SERVER['REQUEST_METHOD'] == 'POST'){
    require_once('db.php');

    $usu_nombre = $_POST['usu_nombre'];
    $profile_path =  $_POST['foto_perfil'];

    $timestamp = time();

     #consulta para recoger el id de la foto
        $select_image_query = "SELECT usu_id FROM usuarios ORDER BY usu_id ASC";
        $result_select = $mysql->query($select_image_query);

        while ($row = $result_select->fetch_array()) {
            $default_photo_id = $row['usu_id'];
        }

        $photo_profile_name = "images/profile_" . $default_photo_id . $timestamp . ".jpg";
        $PROFILE_URL = "https://fotay.000webhostapp.com/$photo_profile_name";

        #consulta para insertar la foto de perfil
        $insert_profile_query = "UPDATE usuarios SET foto_perfil = '$PROFILE_URL' WHERE usu_nombre = '$usu_nombre'";
        $result_insert_profile = $mysql->query($insert_profile_query);

        if ($result_insert_profile === true) {
            file_put_contents($photo_profile_name, base64_decode($profile_path));
        }else{
            echo "Error al insertar la foto de perfil";
        }
}else {
    echo "Error al recibir los datos";
    $mysql->close();
}
?>