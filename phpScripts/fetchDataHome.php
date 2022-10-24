<?php

require_once('db.php');

$select_photos_query = "SELECT f.foto_id, f.usu_nombre,f.foto_fecha, f.foto_coment, f.foto_ruta, f.total_comentarios, u.foto_perfil FROM fotos as f JOIN usuarios u ON f.usu_id = u.usu_id ORDER BY foto_id ASC;";

$result_select = $mysql->query($select_photos_query);

if ($result_select) {
    $u_posts = array();
    while ($row = $result_select->fetch_assoc()) {
        array_push($u_posts, array(
            'foto_id' => $row['foto_id'],
            'usu_nombre' => $row['usu_nombre'],
            'foto_fecha' => $row['foto_fecha'],
            'foto_coment' => $row['foto_coment'],
            'foto_ruta' => $row['foto_ruta'],
            'foto_perfil' => $row['foto_perfil'],
            'total_comentarios' => $row['total_comentarios']
        ));
    }

    echo json_encode(array('home_posts' => $u_posts), JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);


} else {
    echo json_encode(array('error' => 'No se pudo obtener los datos de la BBDD'), JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
}

