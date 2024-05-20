package modelo;

public class ItemProductoCanastaFamiliar extends ItemProducto {
    private String tipo;

    public ItemProductoCanastaFamiliar(int codigo, String nombre, double cantidad, double precio, String tipo) {
        super(codigo, nombre, cantidad, precio);
        this.tipo = tipo;
    }


    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public double calcularValorTotal() {
        return cantidad * precio; // Sin IVA
    }

    @Override
    public String toString() {
        return super.toString() + ", tipo='" + tipo + "'";
    }
}
