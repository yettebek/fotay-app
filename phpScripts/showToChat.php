<?php
    require_once("db.php");
    session_start();
    //real_escape_string: para prevenir la inyección por SQL
    $emisor = mysqli_real_escape_string($mysql, $_REQUEST["emisor"]);
    $receptor = mysqli_real_escape_string($mysql, $_REQUEST["receptor"]);

$select_message_query = "SELECT id_mensaje, usu_id, emisor, receptor,fecha_mensaje, mensaje FROM chat WHERE emisor = '$emisor' AND receptor = '$receptor' ORDER BY fecha_mensaje ASC";

    $select_message_query_result = mysqli_query($mysql, $select_message_query);

    if($select_message_query_result) {
        $u_chat = array();
        while ($row = $select_message_query_result->fetch_assoc()) { //OR $select_photos_query
            array_push($u_chat, array(
                'id_mensaje' => $row['id_mensaje'],
                'usu_id' => $row['usu_id'],
                'emisor' => $row['emisor'],
                'receptor' => $row['receptor'],
                'fecha_mensaje' => $row['fecha_mensaje'],
                'mensaje' => $row['mensaje']
            ));
        }

        echo json_encode(array('chat_messages' => $u_chat), JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    }  else {
        echo json_encode(array('error' => 'No se pudo obtener los datos de la BBDD'), JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    }

