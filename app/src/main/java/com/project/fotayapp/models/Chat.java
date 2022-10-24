package com.project.fotayapp.models;

import java.io.Serializable;

public class Chat implements Serializable {
    //Variables usu_id, emisor, receptor, mensaje

    private int usu_id;
    private String emisor;
    private String receptor;
    private String fecha;
    private String mensaje;

    //Constructor
    public Chat(int usu_id, String emisor, String receptor, String fecha, String mensaje){
        this.usu_id = usu_id;
        this.emisor = emisor;
        this.receptor = receptor;
        this.fecha = fecha;
        this.mensaje = mensaje;
    }

    //Getters
    public int getUsu_id(){
        return usu_id;
    }
    public String getEmisor(){
        return emisor;
    }
    public String getReceptor(){
        return receptor;
    }
    public String getFecha(){
        return fecha;
    }
    public String getMensaje(){
        return mensaje;
    }
}
