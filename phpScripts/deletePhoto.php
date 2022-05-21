<?php

require_once('db.php');

$foto_id = mysqli_real_escape_string($mysql, $_REQUEST["foto_id"]);

$delete_photo_query = "DELETE FROM fotos WHERE foto_id = '$foto_id'";

$result_select = $mysql->query($delete_photo_query);

if ($result_select) {
    echo "Foto eliminada.";

} else {
    echo "Error: " . $delete_photo_query . "<br>" . $mysql->error;
}
