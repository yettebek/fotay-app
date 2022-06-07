<?php

    require_once("db.php");

    //real_escape_string: para prevenir la inyección por SQL
    $foto_id = mysqli_real_escape_string($mysql, $_REQUEST["foto_id"]);

    $select_comments = "SELECT u.foto_perfil, coment_id, c.foto_id, c.usu_id, c.usu_nombre, c.fecha_coment, c.txt_coment FROM comentarios c INNER JOIN usuarios u ON c.usu_id = u.usu_id WHERE c.foto_id = '$foto_id' ORDER BY c.coment_id ASC";


    $result_select_comments = mysqli_query($mysql, $select_comments);

    if ($result_select_comments) {
        $u_coment = array();
        while ($row = $result_select_comments->fetch_assoc()) {
            array_push($u_coment, array(
                'coment_id' => $row['coment_id'],
                'foto_id' => $row['foto_id'],
                'usu_id' => $row['usu_id'],
                'foto_perfil' => $row['foto_perfil'],
                'usu_nombre' => $row['usu_nombre'],
                'fecha_coment' => $row['fecha_coment'],
                'txt_coment' => $row['txt_coment']
            ));
        }
        echo json_encode(array('comments' => $u_coment), JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    } else {
     echo json_encode(array('error' => 'No se pudo obtener los datos de la BBDD'), JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    }

