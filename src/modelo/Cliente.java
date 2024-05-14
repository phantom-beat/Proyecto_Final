package modelo;


public class Cliente {
    private String identificacion;
    private String nombre;
    private String direccion;

    public Cliente(String identificacion, String nombre, String direccion) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return "Cliente{" +
               "identificacion='" + identificacion + '\'' +
               ", nombre='" + nombre + '\'' +
               ", direccion='" + direccion + '\'' +
               '}';
    }
}

