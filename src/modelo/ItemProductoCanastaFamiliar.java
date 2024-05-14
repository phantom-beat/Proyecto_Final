/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author gabag
 */
public class ItemProductoCanastaFamiliar extends ItemProducto {
    String tipo;

    public ItemProductoCanastaFamiliar(int codigo, String nombreP, double cantidad, String presentacion, String tipo, double precio) {
        super(codigo, nombreP, cantidad, presentacion, tipo, precio);
    }
    


    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "ItemProductoCanastaFamiliar{" + "tipo=" + tipo + '}';
    }


    
}

}



