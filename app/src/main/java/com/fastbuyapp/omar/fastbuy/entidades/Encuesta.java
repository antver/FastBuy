package com.fastbuyapp.omar.fastbuy.entidades;

import java.util.ArrayList;

public class Encuesta {
    private int codigo;
    private String pregunta;
    private String descripcion;
    private ItemEncuesta listItems;

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public ItemEncuesta getListItems() {
        return listItems;
    }

    public void setListItems(ItemEncuesta listItems) {
        this.listItems = listItems;
    }
}
