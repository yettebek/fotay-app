<?php
$mysql = new mysqli(
    "localhost", 
    "id16979451_pma",
    "p]NhWhIkcSK{E0P5",
    "id16979451_fotay_db"
);

if ($mysql -> connect_error) {
    die("Failed to connect" .$mysql->connect_error);
}
?>