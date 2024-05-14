package modelo;

public class ItemProductoFarmacia extends ItemProducto {
    private String presentacion;

    public ItemProductoFarmacia(int codigo, String nombre, double cantidad, double precio, String presentacion) {
        super(codigo, nombre, codigo, cantidad);
        this.presentacion = presentacion;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    @Override
    public double calcularValorTotal() {
        return cantidad * precio;
    }

    @Override
    public String toString() {
        return super.toString() + ", presentacion='" + presentacion + "'";
    }
}
