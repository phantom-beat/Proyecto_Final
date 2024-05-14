package Vista;

import Modelo.ItemProductoOtro;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.FileReader;
import javax.swing.JOptionPane;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import modelo.Cliente;
import modelo.ItemProducto;
import modelo.Pedido;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import static javax.swing.UIManager.get;
import modelo.ItemProductoCanastaFamiliar;
import modelo.ItemProductoFarmacia;

public class UsaPedido {
    private static final LinkedList<Pedido> losPedidos = new LinkedList<>();
    private static final String ARCHIVO_PEDIDOS = "pedidos.txt";
    
    public static void main(String[] args) {
        // Lista de opciones para el menú
        String[] opciones = {
            "Crear Pedido",
            "Actualizar Estado de Pedido",
            "Consultar Todos los Pedidos",
            "Consultar Pedido por Número",
            "Consultar Último Pedido",
            "Eliminar Todos los Pedidos",
            "Eliminar un Pedido",
            "Eliminar Último Pedido",
            "Generar archivo de texto de los pedidos",
            "Recuperar pedidos desde archivo de texto",
            "Salir"
        };

        int opcion = -1; // Valor predeterminado para evitar errores

        do {
            // Construye un mensaje con saltos de línea para mostrar las opciones verticalmente
            StringBuilder menuMensaje = new StringBuilder("Seleccione una opción:\n");
            for (int i = 0; i < opciones.length; i++) {
                menuMensaje.append(i).append(". ").append(opciones[i]).append("\n");
            }

            // Obtener la entrada del usuario
            String input = JOptionPane.showInputDialog(
                null,
                menuMensaje.toString(),
                "Gestión de Pedidos",
                JOptionPane.PLAIN_MESSAGE
            );

            if (input != null) { // Si el usuario no canceló
                try {
                    opcion = Integer.parseInt(input); // Convertir a entero
                } catch (NumberFormatException e) {
                    System.err.println("Entrada no válida. Ingrese un número.");
                    continue; // Volver a pedir la entrada
                }

                // Switch para las diferentes opciones
                switch (opcion) {
                    case 0 -> crearPedido();
                    case 1 -> {
                        int numeroPedido = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el número del pedido a actualizar:"));
                        char estadoPedido = JOptionPane.showInputDialog("Ingrese el nuevo estado del pedido (A, D, C):").charAt(0);
                        actualizarEstadoPedido(numeroPedido, estadoPedido);
                    }
                    case 2 -> consultarTodosPedidos();
                    case 3 -> {
                        int numero = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el número del pedido a consultar:"));
                        consultarPedidoNumero(numero);
                    }
                    case 4 -> consultarUltimoPedido();
                    case 5 -> eliminarTodosPedidos();
                    case 6 -> eliminarUnPedido();
                    case 7 -> eliminarUltimoPedido();
                    case 8 -> generarArchivoTextoDePedidos();
                    case 9 -> recuperarPedidosDesdeArchivoTexto();
                    case 10 -> {
                        JOptionPane.showMessageDialog(null, "Saliendo del programa.");
                        System.exit(0);
                    }
                    default -> System.err.println("Opción no válida.");
                }
            } else {
                System.out.println("Operación cancelada por el usuario.");
                break; // Salir del bucle si el usuario cancela
            }

        } while (opcion != 10); // Continuar hasta que se seleccione "Salir"
    }
//se crea el pedido para que el usuario digite los datos correspondientes 
private static void crearPedido() {
  
       String idCliente = JOptionPane.showInputDialog("Ingrese la identificación del cliente:");
       String nombreCliente = JOptionPane.showInputDialog("Ingrese el nombre del cliente:");
       String direccionCliente = JOptionPane.showInputDialog("Ingrese la dirección del cliente:");
       String estadoPedido = JOptionPane.showInputDialog("Ingrese el estado del pedido,A: abierto, D: despachado, C: cancelado");
       String observacion = JOptionPane.showInputDialog("ingrese la observacion de el pedido : ");
       int valorUrgencia = Integer.parseInt(JOptionPane.showInputDialog("ingrese el valor de urgencia de el producto : "));

            

        boolean formatoValido = false; // si no hay fecha y hora da error porq el formato no es valido 
        LocalDateTime fechaHora = LocalDateTime.parse(JOptionPane.showInputDialog("Ingrese la fecha y hora (formato: yyyy-MM-dd T HH:mm):"));
        while (!formatoValido) {
            try {


                formatoValido = true;
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(null, "Formato de fecha y hora inválido. Por favor, ingrese en el formato correcto (yyyy-MM-dd HH:mm).");
            }
        }

            //cambiamos el estadoPedido de Str a Char 
       char estado = estadoPedido.charAt(0);

       if (estado == 'A' || estado == 'D' || estado == 'C') {
           Cliente cliente = new Cliente(idCliente, nombreCliente, direccionCliente);

            //creamos la lista de el producto 
           LinkedList<ItemProducto> productos = new LinkedList<>();
           boolean agregarMas = true;
           
           //se crea un bucle para el boton agregar mas productos 
           //pedimos los datos de los productos para crear el objeto en el cual se almacenen los datos 

while (agregarMas) {
    String codigoProducto = JOptionPane.showInputDialog("Ingrese el código del producto:");
    String nombreProducto = JOptionPane.showInputDialog("Ingrese el nombre del producto:");
    String cantidadProductoStr = JOptionPane.showInputDialog("Ingrese la cantidad del producto:");
    double cantidadProducto;
    char categoriaItemProducto;
    String tipoCanastaFamiliar = "";
    String presentacionFarmacia = "";
    double porcentajeIva = 0;

    // Solicitamos al usuario ingresar la categoría del producto hasta que sea válida
    do {
        String inputCategoria = JOptionPane.showInputDialog("Ingrese la categoría del producto (C - Canasta Familiar, F - Farmacia, O - Otro): ");
        categoriaItemProducto = inputCategoria.toUpperCase().charAt(0);
    } while (categoriaItemProducto != 'C' && categoriaItemProducto != 'F' && categoriaItemProducto != 'O');

    try {
        cantidadProducto = Double.parseDouble(cantidadProductoStr);

        // Si la categoría es Canasta Familiar
       switch (categoriaItemProducto) {
    case 'C':
        tipoCanastaFamiliar = JOptionPane.showInputDialog("Ingrese el tipo de canasta familiar (Grano, Carne, Aseo personal, Lácteo, Fruta, Verdura, Legumbre, Papa): ");
        double precioProductoCanastaFamiliar = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el precio del producto:"));
        ItemProducto objItemProductoCanastaFamiliar = new ItemProductoCanastaFamiliar(Integer.parseInt(codigoProducto), nombreProducto, cantidadProducto, precioProductoCanastaFamiliar, tipoCanastaFamiliar);
        productos.add(objItemProductoCanastaFamiliar);
        break;
    case 'F':
        presentacionFarmacia = JOptionPane.showInputDialog("Ingrese la presentación del producto en Farmacia: ");
        double precioProductoFarmacia = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el precio del producto:"));
        ItemProducto objItemProductoFarmacia = new ItemProductoFarmacia(Integer.parseInt(codigoProducto), nombreProducto, cantidadProducto, precioProductoFarmacia, presentacionFarmacia);
        productos.add(objItemProductoFarmacia);
        break;
    default:
        porcentajeIva = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el porcentaje de IVA del producto: "));
        double precioProductoOtro = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el precio del producto:"));
        ItemProducto objItemProductoOtro = new ItemProductoOtro(Integer.parseInt(codigoProducto), nombreProducto, cantidadProducto, precioProductoOtro, porcentajeIva);
        productos.add(objItemProductoOtro);
        break;
}
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Entrada inválida. Por favor, ingrese valores numéricos válidos.");
        continue; // Salta a la siguiente iteración del bucle
    }

    int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea agregar otro producto?", "Agregar Producto", JOptionPane.YES_NO_OPTION);
    agregarMas = respuesta == JOptionPane.YES_OPTION;
}


Pedido pedido = new Pedido(losPedidos.size() + 1, fechaHora, cliente, productos, observacion, true, estado);
losPedidos.add(pedido);
guardarPedidoEnArchivo(pedido);
JOptionPane.showMessageDialog(null, "Pedido creado y guardado con éxito!");
       }
}
    private static void guardarPedidoEnArchivo(Pedido pedido) {
    try (PrintWriter out = new PrintWriter(new FileWriter(ARCHIVO_PEDIDOS))) {
        for (Pedido p : losPedidos) {
            out.println("Pedido #" + p.getNumero());
            out.println("Cliente: " + p.getSuCliente().getNombre() + " - ID: " + p.getSuCliente().getIdentificacion() + " - Dirección: " + p.getSuCliente().getDireccion());
            out.println("Estado: " + p.getEstado());
            out.println("Productos:");
            for (ItemProducto item : p.getSusItemsProductos()) {
                out.println("   - " + item.getNombre() + ", Código: " + item.getCodigo() + ", Cantidad: " + item.getCantidad() + ", Precio: $" + item.getPrecio());
            }
            out.println("Observaciones: " + p.getObservacion());
            out.println("--------------------------------------------------");
        }
        out.flush();
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al guardar los pedidos en el archivo: " + e.getMessage());
    }
}


private static void actualizarEstadoPedido(int numeroPedido, char estadoPedido) {
    // Verificar si el estado ingresado es válido (C o D)
    if (estadoPedido != 'C' && estadoPedido != 'D') {
        JOptionPane.showMessageDialog(null, "Estado no válido. Ingrese C (cancelado/anulado) o D (despachado).");
        return;
    }

    // Buscar el pedido correspondiente en la lista
    Pedido pedidoEncontrado = null;
    for (Pedido pedido : losPedidos) {
        if (pedido.getNumero() == numeroPedido) {
            pedidoEncontrado = pedido;
            break;
        }
    }

    // Si no se encontró el pedido, mostrar un mensaje de error
    if (pedidoEncontrado == null) {
        JOptionPane.showMessageDialog(null, "No se encontró el pedido con número " + numeroPedido);
        return;
    }

    // Actualizar el estado del pedido
    pedidoEncontrado.setEstado(estadoPedido);
    JOptionPane.showMessageDialog(null, "El estado del pedido ha sido actualizado.");

    // Guardar los cambios en el archivo de pedidos
    actualizarArchivoPedido(pedidoEncontrado);
}

    private static void actualizarArchivoPedido(Pedido pedido) {
    // Sobrescribe el archivo completo para actualizar el estado de un pedido específico
    try (PrintWriter out = new PrintWriter(new FileWriter(ARCHIVO_PEDIDOS))) {
        for (Pedido p : losPedidos) {
            out.println("Pedido #" + p.getNumero() + " - Fecha: " + p.getFechaHora());
            out.println("Cliente: " + p.getSuCliente().getNombre() + " - ID: " + p.getSuCliente
        ().getIdentificacion() + " - Dirección: " + p.getSuCliente().getDireccion());
            out.println("Estado: " + p.getEstado());
            for (ItemProducto item : p.getSusItemsProductos()) {
                out.println("Producto: " + item.getNombre() + " - Código: " +
                        item.getCodigo() + " - Cantidad: " + item.getCantidad() + " - Precio: $" + item.getPrecio());
            }
            out.println("--------------------------------------------------");
        }
        out.flush();
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al actualizar el archivo de pedidos: " + e.getMessage());
    }
}


public static void consultarTodosPedidos() {
    StringBuilder resultadoBuilder = new StringBuilder("REPORTE DE TODOS LOS PEDIDOS \n\n");
    for (Pedido objPedido : losPedidos) {
        resultadoBuilder.append("Número del pedido: ").append(objPedido.getNumero()).append("\n")
                        .append("Fecha-hora del pedido: ").append(objPedido.getFechaHora()).append("\n")
                        .append("Datos del cliente: ").append(objPedido.getSuCliente()).append("\n")
                        .append("El pedido está normal: ").append(objPedido.isNormal()).append("\n")
                        .append("Estado del pedido: ").append(objPedido.getEstado()).append("\n")
                        .append("Observación del pedido: ").append(objPedido.getObservacion()).append("\n")
                        .append("Cantidad de items del pedido: ").append(objPedido.calcularCantidadItemsPedidos()).append("\n")
                        .append("Valor total de los items pedidos: ").append(objPedido.calcularValorTotalItems()).append("\n")
                        .append("Datos de sus items productos:").append(objPedido.getSusItemsProductos()).append("\n\n");
    }
    
    JTextArea textArea = new JTextArea(resultadoBuilder.toString());
    textArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(500, 400));
    JOptionPane.showMessageDialog(null, scrollPane, "Todos los Pedidos", JOptionPane.INFORMATION_MESSAGE);
}



private static  String consultarPedidoNumero(int numeroPedido) {
    // Intenta encontrar el pedido con el número dado
    Pedido pedidoEncontrado = null;

    for (Pedido p : losPedidos) {
        if (p.getNumero() == numeroPedido) {
            pedidoEncontrado = p;
            break;
        }
    }

    // Si el pedido fue encontrado, construye un mensaje con su información
    if (pedidoEncontrado != null) {
        StringBuilder sb = new StringBuilder();

        sb.append("Pedido #").append(pedidoEncontrado.getNumero()).append("\n");
        sb.append("Fecha/Hora: ").append(pedidoEncontrado.getFechaHora()).append("\n");
        sb.append("Cliente: ").append(pedidoEncontrado.getSuCliente().getNombre()).append(" - ID: ").append(pedidoEncontrado.getSuCliente().getIdentificacion()).append("\n");
        sb.append("Dirección: ").append(pedidoEncontrado.getSuCliente().getDireccion()).append("\n");
        sb.append("Estado: ").append(pedidoEncontrado.getEstado()).append("\n");
        sb.append("Observaciones: ").append(pedidoEncontrado.getObservacion()).append("\n");
        sb.append("Productos:\n");

        double valorTotalItems = 0;

        for (ItemProducto item : pedidoEncontrado.getSusItemsProductos()) {
            sb.append(" - ").append(item.getNombre()).append(", Código: ").append(item.getCodigo())
                .append(", Cantidad: ").append(item.getCantidad()).append(", Precio: $").append(item.getPrecio()).append("\n");

            // Añade información específica según el tipo de producto
            switch (item) {
                case ItemProductoFarmacia itemProductoFarmacia -> sb.append("   Presentación: ").append(itemProductoFarmacia.getPresentacion()).append("\n");
                case ItemProductoCanastaFamiliar itemProductoCanastaFamiliar -> sb.append("   Tipo: ").append(itemProductoCanastaFamiliar.getTipo()).append("\n");
                case ItemProductoOtro itemProductoOtro -> {
                    double porcentajeIva = itemProductoOtro.getPorcentajeIva();
                    if (porcentajeIva != 0) {
                        sb.append("   Porcentaje IVA: ").append(porcentajeIva).append("%\n");
                    }
                }
                default -> {
                }
            }

            valorTotalItems += item.getCantidad() * item.getPrecio();
        }

        sb.append("Cantidad de ítems de pedido: ").append(pedidoEncontrado.getSusItemsProductos().size()).append("\n");
        sb.append("Valor total de los ítems: $").append(valorTotalItems).append("\n");
        sb.append("Valor total a pagar: $").append(valorTotalItems); // Suponiendo que no hay descuentos

        return sb.toString();
    } else {
        // Si el pedido no fue encontrado, devuelve un mensaje indicando eso
        return "No se encontró un pedido con el número " + numeroPedido;
    }
}



  private static void consultarUltimoPedido() {
        // Primero, verificamos si la lista de pedidos está vacía
        if (losPedidos.isEmpty()) {
            // Si está vacía, mostramos un mensaje de advertencia y salimos del método
            JOptionPane.showMessageDialog(null, "No hay pedidos para mostrar.");
            return;
        }

        // Si hay pedidos, obtenemos el último pedido de la lista
        Pedido ultimoPedido = losPedidos.getLast();

        // Creamos un string para almacenar la información del pedido
        String mensaje = "Último Pedido:\n"
                + "Pedido #" + ultimoPedido.getNumero() + "\n"
                + "Fecha/Hora: " + ultimoPedido.getFechaHora() + "\n"
                + "Cliente: " + ultimoPedido.getSuCliente().getNombre()
                + " - ID: " + ultimoPedido.getSuCliente().getIdentificacion()
                + " - Dirección: " + ultimoPedido.getSuCliente().getDireccion() + "\n"
                + "Estado: " + ultimoPedido.getEstado() + "\n"
                + "Observaciones: " + ultimoPedido.getObservacion() + "\n"
                + "Productos:\n";

        double valorTotalItems = 0; // Variable para almacenar el valor total de los ítems

        // Iteramos sobre cada ItemProducto del pedido
        for (ItemProducto item : ultimoPedido.getSusItemsProductos()) {
            mensaje += " - " + item.getNombre() + ", Código: " + item.getCodigo()
                    + ", Cantidad: " + item.getCantidad() + ", Precio: $" + item.getPrecio() + "\n";

            // Calculamos el valor total del ItemProducto y lo sumamos al valor total de los ítems
            double valorTotal = item.getCantidad() * item.getPrecio();
            valorTotalItems += valorTotal;
        }

        // Solicitamos el valor de urgencia y lo sumamos al valor total de los ítems
        int valorUrgencia = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el valor de urgencia del pedido:"));
        valorTotalItems += valorUrgencia;

        // Agregamos la cantidad de ítems del pedido y el valor total de los ítems al mensaje
        mensaje += "Cantidad de ítems de pedido: " + ultimoPedido.getSusItemsProductos().size() + "\n";
        mensaje += "Valor total de los ítems: $" + valorTotalItems + "\n";

        // Creamos un JTextArea con el texto construido
        JTextArea textArea = new JTextArea(mensaje);
        textArea.setEditable(false); // Hacemos que el área de texto no sea editable

        // Creamos un JScrollPane para hacer que el área de texto sea desplazable
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300)); // Establecemos un tamaño preferido para el JScrollPane

        // Mostramos el JScrollPane en un cuadro de diálogo con un título y un icono de información
        JOptionPane.showMessageDialog(null, scrollPane, "Consulta del Último Pedido", JOptionPane.INFORMATION_MESSAGE);
    }



    private static void eliminarTodosPedidos() {
    int confirm = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar todos los pedidos?",
                                                "Eliminar Todos los Pedidos", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    if (confirm == JOptionPane.YES_OPTION) {
        losPedidos.clear();  // Limpia la lista de todos los pedidos
        eliminarArchivoPedidos();  // Opcional: Eliminar o limpiar el archivo de pedidos
        JOptionPane.showMessageDialog(null, "Todos los pedidos han sido eliminados.");
    } else {
        JOptionPane.showMessageDialog(null, "Operación cancelada.");
    }
}

    private static void eliminarArchivoPedidos() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_PEDIDOS))) {
            writer.print("");  // Sobrescribe el archivo con una cadena vacía, efectivamente limpiándolo
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al limpiar el archivo de pedidos: " + e.getMessage());
        }
    }


    private static void eliminarUnPedido() {
    String numeroPedidoStr = JOptionPane.showInputDialog("Ingrese el número del pedido que desea eliminar:");
    if (numeroPedidoStr == null || numeroPedidoStr.trim().isEmpty()) {
        JOptionPane.showMessageDialog(null, "No se ingresó ningún número de pedido.");
        return;
    }

    try {
        int numeroPedido = Integer.parseInt(numeroPedidoStr);
        boolean encontrado = false;

        for (int i = 0; i < losPedidos.size(); i++) {
            if (losPedidos.get(i).getNumero() == numeroPedido) {
                int confirm = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar el pedido número " + numeroPedido + "?",
                                                            "Eliminar Pedido", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    losPedidos.remove(i);
                    actualizarArchivoTrasEliminar();  // Actualiza el archivo después de eliminar un pedido
                    JOptionPane.showMessageDialog(null, "El pedido número " + numeroPedido + " ha sido eliminado.");
                } else {
                    JOptionPane.showMessageDialog(null, "Operación cancelada.");
                }
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            JOptionPane.showMessageDialog(null, "No se encontró un pedido con el número " + numeroPedido);
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Número de pedido no válido. Por favor, ingrese un número entero.");
    }
    }
    private static void actualizarArchivoTrasEliminar() {
        // Reescribir el archivo completo para actualizar los cambios
        try (PrintWriter out = new PrintWriter(new FileWriter(ARCHIVO_PEDIDOS))) {
            for (Pedido p : losPedidos) {
                out.println("Pedido #" + p.getNumero() + " - Fecha: " + p.getFechaHora());
                out.println("Cliente: " + p.getSuCliente().getNombre() + " - ID: " + p.getSuCliente().getIdentificacion() + " - Dirección: " + p.getSuCliente().getDireccion());
                out.println("Estado: " + p.getEstado());
                for (ItemProducto item : p.getSusItemsProductos()) {
                    out.println("Producto: " + item.getNombre() + " - Código: " + item.getCodigo() + " - Cantidad: " + item.getCantidad() + " - Precio: $" + item.getPrecio());
                }
                out.println("--------------------------------------------------");
            }
            out.flush();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el archivo de pedidos: " + e.getMessage());
        }
}


    private static void eliminarUltimoPedido() {
    if (losPedidos.isEmpty()) {
        JOptionPane.showMessageDialog(null, "No hay pedidos para eliminar.");
        return;
    }

    Pedido ultimoPedido = losPedidos.getLast(); // Obtener el último pedido
    int confirm = JOptionPane.showConfirmDialog(null, 
        "¿Está seguro de que desea eliminar el último pedido realizado (Pedido #" + ultimoPedido.getNumero() + ")?",
        "Eliminar Último Pedido", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

    if (confirm == JOptionPane.YES_OPTION) {
        losPedidos.removeLast(); // Elimina el último pedido de la lista
        actualizarArchivoTrasEliminar();  // Actualiza el archivo después de eliminar un pedido
        JOptionPane.showMessageDialog(null, "El último pedido ha sido eliminado.");
    } else {
        JOptionPane.showMessageDialog(null, "Operación cancelada.");
    }
}

private static void generarArchivoTextoDePedidos() {
    if (losPedidos.isEmpty()) {
        JOptionPane.showMessageDialog(null, "No hay pedidos para exportar.");
        return;
    }

    try (PrintWriter out = new PrintWriter(new FileWriter(ARCHIVO_PEDIDOS, false))) {
        for (Pedido p : losPedidos) {
            out.println("Pedido #" + p.getNumero());
            out.println("Fecha/Hora: " + p.getFechaHora());

            // Datos del cliente
            Cliente cliente = p.getSuCliente();
            out.println("Cliente: " + cliente.getNombre() + " - ID: " + " - Dirección: " + cliente.getDireccion());

            // Estado del pedido
            out.println("Estado: " + p.getEstado());

            // Listado de productos del pedido
            out.println("Productos:");
            for (ItemProducto item : p.getSusItemsProductos()) {
                out.println("   - " + item.getNombre() + ", Código: " + item.getCodigo() + ", Cantidad: " + item.getCantidad() + ", Precio: $" + item.getPrecio());
            }

            // Observaciones y separación entre pedidos
            out.println("Observaciones: " + p.getObservacion());
            out.println("--------------------------------------------------");
        }

        JOptionPane.showMessageDialog(null, "Archivo de pedidos generado con éxito.");
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al escribir el archivo de pedidos: " + e.getMessage());
    }
}
//faltan las observaciones 

//ser mas especifico con lasdirecciones 
    

//malo
private static void recuperarPedidosDesdeArchivoTexto() {
    // Limpiar la lista actual para evitar duplicados
    losPedidos.clear();

    // Manejo de recursos con try-with-resources
    try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_PEDIDOS))) {
        String line; // Variable para almacenar cada línea del archivo
        Pedido pedido = null; // Variable para el pedido actual
        LinkedList<ItemProducto> productos = new LinkedList<>(); // Lista de productos
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Formato de fecha/hora

        // Leer cada línea del archivo y procesar datos
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("Pedido #")) { // Indica el inicio de un nuevo pedido
                // Guardar el pedido anterior si existe
                if (pedido != null) {
                    pedido.setSusItemsProductos(productos); // Asignar la lista de productos al pedido
                    losPedidos.add(pedido); // Añadir a la lista de pedidos
                }

                // Crear un nuevo pedido
                int numeroPedido = Integer.parseInt(line.split("#")[1].trim());
                pedido = new Pedido(numeroPedido, null, null, null, null, false, ' '); // Nuevo pedido
                productos = new LinkedList<>(); // Resetear la lista de productos
            } else if (line.startsWith("Fecha/Hora:")) { // Información de fecha y hora
                LocalDateTime fechaHora = LocalDateTime.parse(line.split(":")[1].trim(), formatter);
                pedido.setFechaHora(fechaHora); // Asignar la fecha/hora
            } else if (line.startsWith("Cliente:")) { // Información del cliente
                String[] partes = line.split(":")[1].split("-");
                String nombreCliente = partes[0].trim();
                String numeroIdentificacion = partes[1].trim();
                String direccionCliente = partes[2].trim();
                Cliente cliente = new Cliente(numeroIdentificacion, nombreCliente, direccionCliente); // Crear cliente
                pedido.setSuCliente(cliente); // Asignar el cliente al pedido
            } else if (line.startsWith("Estado:")) { // Estado del pedido
                char estado = line.split(":")[1].trim().charAt(0); // Obtener el primer carácter
                pedido.setEstado(estado); // Asignar el estado
            } else if (line.startsWith("Observaciones:")) { // Observaciones del pedido
                String observacion = line.split(":")[1].trim();
                pedido.setObservacion(observacion); // Asignar observación
            } else if (line.startsWith("Productos:")) { // Sección de productos
                // Indica el inicio de la sección de productos, no es necesario hacer nada
            } else if (line.startsWith("   -")) { // Línea de producto
                String[] partes = line.substring(4).split(","); // Eliminar espacios iniciales
                String nombreProducto = partes[0].trim();
                String codigoProducto = partes[1].split(":")[1].trim();
                int cantidadProducto = Integer.parseInt(partes[2].split(":")[1].trim());
                double precioProducto = Double.parseDouble(partes[3].split(":")[1].trim().substring(1)); // Quitar el símbolo de dólar
               productos.add(new ItemProducto(Integer.parseInt(codigoProducto), nombreProducto, cantidadProducto, precioProducto) {
                    @Override
                    public double calcularValorTotal() {
                        throw new UnsupportedOperationException(""); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
                    }
                }); 
                   
            }
        }

        // Guardar el último pedido leído
        if (pedido != null) {
            pedido.setSusItemsProductos(productos);
            losPedidos.add(pedido); // Añadir el pedido a la lista
        }

        JOptionPane.showMessageDialog(null, "Pedidos cargados desde el archivo con éxito.");
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al leer el archivo de pedidos: " + e.getMessage());
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al procesar el archivo de pedidos: " + e.getMessage());
    }
}
}







