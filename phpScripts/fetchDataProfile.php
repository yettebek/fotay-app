<?php

require_once('db.php');

$usu_nombre = mysqli_real_escape_string($mysql, $_REQUEST["usu_nombre"]);

$select_photos_query = "SELECT foto_id, usu_nombre, foto_fecha, foto_coment, foto_ruta FROM fotos where usu_nombre = '$usu_nombre'";

$result_select = $mysql->query($select_photos_query);

if ($result_select) {

    while ($row = mysqli_fetch_array($result_select, MYSQLI_ASSOC)) { //OR $select_photos_query

            $foto_id = $row["foto_id"];
            $usu_nombre = $row["usu_nombre"];
            $foto_fecha = $row["foto_fecha"];
            $foto_coment = $row["foto_coment"];
            $foto_ruta = $row["foto_ruta"];

 		 $u_posts[] = array(
 		 "foto_id" => $foto_id,
            "usu_nombre" => $usu_nombre,
            "foto_fecha" => $foto_fecha,
            "foto_coment" => $foto_coment,
            "foto_ruta" => $foto_ruta
 		 );

    }

    echo json_encode($u_posts, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);

}else if($result_select == 0){
    echo json_encode(array('error' => 'Registro vacío.'), JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
}
else {
    echo json_encode(array('error' => 'No se pudo obtener los datos de la BBDD'), JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
}