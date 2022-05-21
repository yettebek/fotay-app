<?php

require_once('db.php');

$usu_nombre = mysqli_real_escape_string($mysql, $_REQUEST["usu_nombre"]);

$select_photos_query = "SELECT f.foto_id, f.usu_nombre, f.foto_fecha, f.foto_coment, f.foto_ruta, u.foto_perfil FROM fotos as f JOIN usuarios u ON f.usu_id = u.usu_id WHERE u.usu_nombre = '$usu_nombre' ORDER BY foto_fecha DESC
";

$result_select = $mysql->query($select_photos_query);

if ($result_select) {
    $u_posts = array();
    while ($row = $result_select->fetch_assoc()) { //OR $select_photos_query

        array_push($u_posts, array(
            'foto_id' => $row['foto_id'],
            'usu_nombre' => $row['usu_nombre'],
            'foto_fecha' => $row['foto_fecha'],
            'foto_coment' => $row['foto_coment'],
            'foto_ruta' => $row['foto_ruta'],
            'foto_perfil' => $row['foto_perfil']

        ));
    }

    echo json_encode(array('profile_posts' => $u_posts), JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
} else if ($result_select == 0) {
    echo json_encode(array('error' => 'Registro vacío.'), JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
} else {
    echo json_encode(array('error' => 'No se pudo obtener los datos de la BBDD'), JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
}
