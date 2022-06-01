<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    require_once("db.php");

    //real_escape_string: para prevenir la inyección por SQL
    $usu_id = mysqli_real_escape_string($mysql, $_POST['usu_id']);
    $emisor = mysqli_real_escape_string($mysql, $_POST['emisor']);
    $receptor = mysqli_real_escape_string($mysql, $_POST['receptor']);
    $mensaje = mysqli_real_escape_string($mysql, $_POST['mensaje']);

    $insert_message_query = "INSERT INTO chat (usu_id, emisor, receptor, fecha_mensaje, mensaje) VALUES ('$usu_id', '$emisor', '$receptor', NOW() + INTERVAL 2 HOUR, '$mensaje')";

    $insert_message_result = mysqli_query($mysql, $insert_message_query);

    if ($insert_message_result === true) {
        echo "Exito";
    } else {
        echo "Error";
    }
}
