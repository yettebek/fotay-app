<?php
if ($_SERVER['REQUEST_METHOD'] == 'POST') {
    require_once("db.php");
    $usu_nombre = $_POST['usu_nombre'];

    $delete_user_query = "DELETE FROM usuarios WHERE usu_nombre = '$usu_nombre'";
     $delete_fotos_query = "DELETE FROM fotos WHERE usu_nombre = '$usu_nombre'";

     $result1 = $mysql->query($delete_user_query);
     $result2 = $mysql->query($delete_fotos_query);

    //Comprobamos si se ha eliminado el usuario o las fotos del usuario en caso de que las hubiera
     if($result1 || $result2){
        echo "Usuario eliminado";
     }else{
        echo "Error al eliminar usuario";
     }
 }


