package com.project.fotayapp.models;

import java.io.Serializable;

public class PostPhoto implements Serializable { //Serializable para poder pasar los datos entre las clases
    private int foto_id;
    private String usu_nombre;
    private String foto_fecha;
    private String foto_coment;
    private String foto_ruta;
    private String foto_perfil;

    public PostPhoto(int foto_id, String usu_nombre, String foto_fecha, String foto_coment, String foto_ruta, String foto_perfil) {
        this.usu_nombre = usu_nombre;
        this.foto_fecha = foto_fecha;
        this.foto_coment = foto_coment;
        this.foto_ruta = foto_ruta;
        this.foto_perfil = foto_perfil;
    }

    public int getFoto_id() {
        return foto_id;
    }

    public String getUsu_nombre() {
        return usu_nombre;
    }


    public String getFoto_fecha() {
        return foto_fecha;
    }


    public String getFoto_coment() {
        return foto_coment;
    }


    public String getFoto_ruta() {
        return foto_ruta;
    }


    public String getFoto_perfil() {
        return foto_perfil;
    }

}
