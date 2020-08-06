package com.fastbuyapp.omar.fastbuy.entidades;

import java.io.Serializable;

public class FireBaseEntregado implements Serializable {
    private String estado;
    private String pedido;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPedido() {
        return pedido;
    }

    public void setPedido(String pedido) {
        this.pedido = pedido;
    }
}
