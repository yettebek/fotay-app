<?php
$mysql = new mysqli(
    "localhost", //nombre host del servidor
    "id16979451_pma", //nombre usuario de la base de datos
    "p]NhWhIkcSK{E0P5", //contraseña de la base de datos
    "id16979451_fotay_db" //nombre de la base de datos
);

if ($mysql -> connect_error) {
    die("Error de conexión: " . $mysql -> connect_error);
}
?>