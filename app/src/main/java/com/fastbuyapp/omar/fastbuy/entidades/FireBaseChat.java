package com.fastbuyapp.omar.fastbuy.entidades;

import java.io.Serializable;

public class FireBaseChat implements Serializable {
    private String codusuario;
    private  String idrepartidor;
    private String fecha;
    private String hora;
    private String id;
    private String mensaje;
    private String pedido;
    private String repartidor;

    public FireBaseChat(){

    }

    public String getCodusuario() {
        return codusuario;
    }

    public void setCodusuario(String codusuario) {
        this.codusuario = codusuario;
    }

    public String getIdrepartidor() {
        return idrepartidor;
    }

    public void setIdrepartidor(String idrepartidor) {
        this.idrepartidor = idrepartidor;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getPedido() {
        return pedido;
    }

    public void setPedido(String pedido) {
        this.pedido = pedido;
    }

    public String getRepartidor() {
        return repartidor;
    }

    public void setRepartidor(String repartidor) {
        this.repartidor = repartidor;
    }
}
