package modelo;


import java.time.LocalDateTime;
import java.util.LinkedList;
import modelo.Cliente;
import modelo.ItemProducto;

public class Pedido {
    private int numero;
    private LocalDateTime fechaHora;
    private Cliente suCliente;
    private LinkedList<ItemProducto> susItemsProductos;
    private String observacion;
    private boolean normal;
    private char estado;

    public Pedido(int numero, LocalDateTime fechaHora, Cliente suCliente, LinkedList<ItemProducto> susItemsProductos,
                  String observacion, boolean normal, char estado) {
        this.numero = numero;
        this.fechaHora = fechaHora;
        this.suCliente = suCliente;
        this.susItemsProductos = susItemsProductos;
        this.observacion = observacion;
        this.normal = normal;
        this.estado = estado;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Cliente getSuCliente() {
        return suCliente;
    }

    public void setSuCliente(Cliente suCliente) {
        this.suCliente = suCliente;
    }

    public LinkedList<ItemProducto> getSusItemsProductos() {
        return susItemsProductos;
    }

    public void setSusItemsProductos(LinkedList<ItemProducto> susItemsProductos) {
        this.susItemsProductos = susItemsProductos;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public boolean isNormal() {
        return normal;
    }

    public void setNormal(boolean normal) {
        this.normal = normal;
    }

    public char getEstado() {
        return estado;
    }

    public void setEstado(char estado) {
        this.estado = estado;
    }
    // calcula la cantidad total de los item
    public int calcularCantidadItemsPedidos() {
    int cantidadTotal = 0;
    for (ItemProducto itemProducto : susItemsProductos) {
        cantidadTotal += itemProducto.getCantidad();
    }
    return cantidadTotal;
}

    // Calcula el total de los ítems en el pedido
    public double calcularValorTotalItems() {
    double valorTotal = 0;
    for (ItemProducto itemProducto : susItemsProductos) {
        valorTotal += itemProducto.calcularValorTotal();
    }
    return valorTotal;
}
    

    // Calcula el valor total a pagar, incluyendo un valor adicional en caso de urgencia
    public double calcularValorTotalPagar() {
    double valorTotalPagar = 0;
    for (ItemProducto itemProducto : susItemsProductos) {
        valorTotalPagar += itemProducto.calcularValorTotal();
    }
    return valorTotalPagar;
    
    
}

    @Override
    public String toString() {
        return "Pedido{" +
               "numero=" + numero +
               ", fechaHora=" + fechaHora +
               ", suCliente=" + suCliente +
               ", susItemsProductos=" + susItemsProductos +
               ", observacion='" + observacion + '\'' +
               ", normal=" + normal +
               ", estado=" + estado +
               '}';
    }
    
    
    public double calcularValorTotalPagar(double valorUrgencia) {
    double valorTotalPagar = calcularValorTotalItems() + valorUrgencia;
    return valorTotalPagar;
    
}
}
