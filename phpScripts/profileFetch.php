<?php

require_once('db.php');

$usu_nombre = mysqli_real_escape_string($mysql, $_REQUEST["usu_nombre"]);

    #consulta para recibir la foto de perfil del usuario
    $select_profile_query = "SELECT foto_perfil FROM fotos WHERE usu_nombre = '$usu_nombre'";
    $result_select_profile = $mysql->query($select_profile_query);

    if($result_select_profile) {
        $u_profile = array();

        while ($row = $result_select_profile->fetch_array()) {
            array_push($u_profile, array(
                'foto_perfil' => $row['foto_perfil']
            ));
        }
        echo json_encode($u_profile, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES | JSON_PRETTY_PRINT);
    }else{
        echo "No se han encontrado resultados";
    }