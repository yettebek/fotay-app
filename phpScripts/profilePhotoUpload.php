<?php
if($_SERVER['REQUEST_METHOD'] == 'POST'){
    require_once('db.php');
    $usu_id = $_POST['usu_id'];
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
        
        #Si el usuario no tiene ningun post, se le añade la foto de perfil seleccionada
        $update_profile_usuarios_query = "UPDATE usuarios SET foto_perfil = '$PROFILE_URL' WHERE usu_id = '$usu_id'";

        $result_usuarios = $mysql->query($update_profile_usuarios_query);

        if ($result_usuarios === true) {
            file_put_contents($photo_profile_name, base64_decode($profile_path));
        }else{
            echo "Error al insertar la foto de perfil";
        }
}else {
    echo "Error al recibir los datos";
    $mysql->close();
}
