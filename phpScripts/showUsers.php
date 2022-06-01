<?php

require_once('db.php');

$select_photos_query = "SELECT foto_perfil, usu_nombre FROM usuarios";

$result_select = $mysql->query($select_photos_query);

if ($result_select) {
    $a_users = array();
    while ($row = $result_select->fetch_assoc()) {
        array_push($a_users, array(
            'foto_perfil' => $row['foto_perfil'],
            'usu_nombre' => $row['usu_nombre'],

        ));
    }

    echo json_encode(array('users' => $a_users), JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
} else {
    echo json_encode(array('error' => 'No se pudo obtener los datos de la BBDD'), JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
}
