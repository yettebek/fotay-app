<?php

require_once('db.php');

//$usu_nombre = $_POST['usu_nombre'];

$select_photos_query = "SELECT foto_id, usu_nombre, foto_fecha, foto_coment, foto_ruta FROM fotos where usu_nombre = 'Yetté'";
$result_select = $mysql->query($select_photos_query);

if ($result_select) {
    $u_posts = array();

    while ($row = $result_select->fetch_array()) {
        array_push($u_posts, array(
            'foto_id' => $row['foto_id'],
            'usu_nombre' => $row['usu_nombre'],
            'foto_fecha' => $row['foto_fecha'],
            'foto_coment' => $row['foto_coment'],
            'foto_ruta' => $row['foto_ruta']
        ));
    }

    echo json_encode($u_posts, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
} else {
    echo json_encode(array('error' => 'No se pudo obtener los datos de la BBDD'), JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
}