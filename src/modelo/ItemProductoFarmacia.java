

package modelo;

/**
 *
 * @author gabag
 */
public class ItemProductoFarmacia extends ItemProducto{
    String presentacion;

    public ItemProductoFarmacia(int codigo, String nombreP, double cantidad, String presentacion, String tipo, double precio) {
        super(codigo, nombreP, cantidad, presentacion, tipo, precio);
    }
    
    

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String presentacion) {
        this.presentacion = presentacion;
    }

    @Override
    public String toString() {
        return "ItemProductoFarmacia{" + "presentacion=" + presentacion + '}';
    }
    
    
}
