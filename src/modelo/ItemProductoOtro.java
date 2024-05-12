/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author gabag
 */
public class ItemProductoOtro extends ItemProducto {
    double porcentajeIva;

    public ItemProductoOtro(int codigo, String nombreP, double cantidad, String presentacion, String tipo, double precio) {
        super(codigo, nombreP, cantidad, presentacion, tipo, precio);
    }



    public double getPorcentajeIva() {
        return porcentajeIva;
    }

    public void setPorcentajeIva(double porcentajeIva) {
        this.porcentajeIva = porcentajeIva;
    }

    @Override
    public String toString() {
        return "ItemProductoOtro{" + "porcentajeIva=" + porcentajeIva + '}';
    }
    
    
}
