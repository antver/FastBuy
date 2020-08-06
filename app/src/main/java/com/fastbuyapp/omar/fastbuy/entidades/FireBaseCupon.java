package com.fastbuyapp.omar.fastbuy.entidades;

import java.io.Serializable;

public class FireBaseCupon implements Serializable {
    private String cantidad;
    private String codcupon;
    private String telefonousuario;

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getCodcupon() {
        return codcupon;
    }

    public void setCodcupon(String codcupon) {
        this.codcupon = codcupon;
    }

    public String getTelefonousuario() {
        return telefonousuario;
    }

    public void setTelefonousuario(String telefonousuario) {
        this.telefonousuario = telefonousuario;
    }
}
