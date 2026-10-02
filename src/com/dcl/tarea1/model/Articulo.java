package com.dcl.tarea1.model;

/**
 * Representa un artículo gestionado por la App.
 * Almacena toda la información asociada a él, como su código,
 * denominación, marca, gama, tipo y demás características.
 * 
 * @author David Cuadra Lara
 * @version 1.1
 */
public class Articulo {

    private final String codigo;
    private final String denominacion;
    private final String marca;
    private final String gama;
    private final String tipo;
    private final String eficienciaEnergetica;
    private final String precioUnd;
    private final String stock;
    private final String fecha;
    private final String garantia;
    private final String estado;
    private final String servicios;
    private final String descripcion;

    public Articulo(
            String codigo, 
            String denominacion, 
            String marca, 
            String gama, 
            String tipo, 
            String eficienciaEnergetica, 
            String precioUnd, 
            String stock, 
            String fecha, 
            String garantia,
            String estado,
            String servicios, 
            String descripcion) {
        this.codigo = codigo;
        this.denominacion = denominacion;
        this.marca = marca;
        this.gama = gama;
        this.tipo = tipo;
        this.eficienciaEnergetica = eficienciaEnergetica;
        this.precioUnd = precioUnd;
        this.stock = stock;
        this.fecha = fecha;
        this.garantia = garantia;
        this.estado = estado;
        this.servicios = servicios;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public String getMarca() {
        return marca;
    }

    public String getGama() {
        return gama;
    }

    public String getTipo() {
        return tipo;
    }

    public String getEficienciaEnergetica() {
        return eficienciaEnergetica;
    }

    public String getPrecioUnd() {
        return precioUnd;
    }

    public String getStock() {
        return stock;
    }

    public String getFecha() {
        return fecha;
    }

    public String getGarantia() {
        return garantia;
    }

    public String getEstado() {
        return estado;
    }

    public String getServicios() {
        return servicios;
    }

    public String getDescripcion() {
        return descripcion;
    }

}
