package com.fastbuyapp.omar.fastbuy.entidades;

import android.content.ContentValues;

import com.fastbuyapp.omar.fastbuy.HelperEsquema.UsuarioContract;

/**
 * Created by OMAR on 25/03/2019.
 */

public class Usuario {
    private int codigo;
    private String nombres;
    private String numeroTelefono;
    private String fechaNacimiento;
    private String email;
    private String dni;
    private String ruc;

    public Usuario(){
        this.codigo = 0;
        this.nombres = "Usuario";
        this.numeroTelefono = "";
        this.fechaNacimiento = "";
        this.email = "";
        this.dni = "";
        this.ruc = "";
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(UsuarioContract.UsuarioEntry.CODIGO, 1);
        values.put(UsuarioContract.UsuarioEntry.NOMBRES, nombres);
        values.put(UsuarioContract.UsuarioEntry.TELEFONO, numeroTelefono);
        values.put(UsuarioContract.UsuarioEntry.FECHANACIMIENTO, fechaNacimiento);
        values.put(UsuarioContract.UsuarioEntry.EMAIL, email);
        values.put(UsuarioContract.UsuarioEntry.DNI, dni);
        values.put(UsuarioContract.UsuarioEntry.RUC, ruc);
        return values;
    }
}
