package com.project.fotayapp.models;

public class PostPhoto {
    /*private String usu_nombre;
    private String foto_fecha;
    private String foto_coment;*/
    private String foto_ruta;

    public PostPhoto(/*String usu_nombre, String foto_fecha, String foto_coment,*/ String foto_ruta) {
        /*this.usu_nombre = usu_nombre;
        this.foto_fecha = foto_fecha;
        this.foto_coment = foto_coment;*/
        this.foto_ruta = foto_ruta;
    }

    /*public String getUsu_nombre() {
        return usu_nombre;
    }

    public void setUsu_nombre(String usu_nombre) {
        this.usu_nombre = usu_nombre;
    }

    public String getFoto_fecha() {
        return foto_fecha;
    }

    public void setFoto_fecha(String foto_fecha) {
        this.foto_fecha = foto_fecha;
    }

    public String getFoto_coment() {
        return foto_coment;
    }

    public void setFoto_coment(String foto_coment) {
        this.foto_coment = foto_coment;
    }*/

    public String getFoto_ruta() {
        return foto_ruta;
    }

    public void setFoto_ruta(String foto_ruta) {
        this.foto_ruta = foto_ruta;
    }
}
