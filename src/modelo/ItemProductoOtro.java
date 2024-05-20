package modelo;

public final class ItemProductoOtro extends ItemProducto {

    private double porcentajeIva;

    public ItemProductoOtro(int codigo, String nombre, double cantidad, double precio, double porcentajeIva) {
        super(codigo, nombre, cantidad, precio);
        this.porcentajeIva = porcentajeIva;
    }

    public double getPorcentajeIva() {
        return porcentajeIva;
    }

    public void setPorcentajeIva(double porcentajeIva) {
        this.porcentajeIva = porcentajeIva;
    }

    @Override
    public double calcularValorTotal() {
        double precioConIva = this.precio * (1 + this.porcentajeIva);
        return this.cantidad * precioConIva;
    }

}
