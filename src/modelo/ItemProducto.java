package modelo;

public abstract class ItemProducto {
    protected int codigo;
    protected String nombre;
    protected double cantidad;
    protected double precio;

    public ItemProducto(int codigo, String nombre, double cantidad, double precio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "ItemProducto{" +
               "codigo=" + codigo +
               ", nombre='" + nombre + '\'' +
               ", cantidad=" + cantidad +
               ", precio=" + precio +
               '}';
    }

    public abstract double calcularValorTotal();
}

