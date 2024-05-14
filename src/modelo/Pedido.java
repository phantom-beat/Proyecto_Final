package modelo;

import java.time.LocalDateTime;
import java.util.LinkedList;

public class Pedido {
    private int numeroPedido;
    private LocalDateTime fechaHora;
    private Cliente cliente;
    private char estado;
    private String observacion;
    private LinkedList<ItemProducto> productos;

    public Pedido(int numeroPedido, LocalDateTime fechaHora, Cliente cliente, LinkedList<ItemProducto> productos, String observacion, boolean par1, char estado) {
        this.numeroPedido = numeroPedido;
        this.fechaHora = fechaHora;
        this.cliente = cliente;
        this.estado = estado;
        this.observacion = observacion;
        this.productos = productos;
    }

    public int getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(int numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Cliente getSuCliente() {
        return cliente;
    }

    public void setSuCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public char getEstado() {
        return estado;
    }

    public void setEstado(char estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public LinkedList<ItemProducto> getSusItemsProductos() {
        return productos;
    }

    public void setSusItemsProductos(LinkedList<ItemProducto> productos) {
        this.productos = productos;
    }

    @Override
    public String toString() {
        return "Pedido{" + "numeroPedido=" + numeroPedido + ", fechaHora=" + fechaHora + ", cliente=" + cliente + ", estado=" + estado + ", observacion=" + observacion + ", productos=" + productos + '}';
    }
    
    
}
