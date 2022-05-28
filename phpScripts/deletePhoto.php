<?php

require_once('db.php');

$foto_id = mysqli_real_escape_string($mysql, $_REQUEST["foto_id"]);

$delete_comments_query = "DELETE FROM comentarios WHERE foto_id = '$foto_id'";
$delete_photo_query = "DELETE FROM fotos WHERE foto_id = '$foto_id'";

$delete_comments_result = mysqli_query($mysql, $delete_comments_query);
$delete_photo_result = mysqli_query($mysql, $delete_photo_query);

if ($delete_comments_result || $delete_photo_result) {
    echo "Foto eliminada.";

} else {
    echo "Error: " . $delete_photo_query . "<br>" . $mysql->error;
}
