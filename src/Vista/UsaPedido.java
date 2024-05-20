package Vista;

import modelo.*;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class UsaPedido {
    private static final LinkedList<Pedido> losPedidos = new LinkedList<>();
    private static final String ARCHIVO_PEDIDOS = "ARCHIVO_PEDIDOS.txt";

    public static void main(String[] args) {
        String[] opciones = {
            "Crear Pedido", "Actualizar Estado de Pedido", "Consultar Todos los Pedidos",
            "Consultar Pedido por Número", "Consultar Último Pedido", "Eliminar Todos los Pedidos",
            "Eliminar un Pedido", "Eliminar Último Pedido", "Generar archivo de texto de los pedidos",
            "Recuperar pedidos desde archivo de texto", "Salir"
        };

        int opcion;
        do {
            StringBuilder menuMensaje = new StringBuilder("Seleccione una opción:\n");
            for (int i = 0; i < opciones.length; i++) {
                menuMensaje.append(i).append(". ").append(opciones[i]).append("\n");
            }

            String input = JOptionPane.showInputDialog(null, menuMensaje.toString(), "Gestión de Pedidos", JOptionPane.PLAIN_MESSAGE);
            if (input != null && !input.isEmpty()) {
                opcion = Integer.parseInt(input);
                MenuOption(opcion);
            } else {
                break;
            }
        } while (true);
    }

    private static void MenuOption(int opcion) {
        try {
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
                case 6 -> {
                    int numeroPedido = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el número del pedido que desea eliminar:"));
                    eliminarUnPedido(numeroPedido);
                }
                case 7 -> eliminarUltimoPedido();
                case 8 -> generarArchivoTextoDePedidos();
                case 9 -> recuperarDesdeArchivoTextoDePedidos();
                case 10 -> {
                    JOptionPane.showMessageDialog(null, "Saliendo del programa.");
                    System.exit(0);
                }
                default -> JOptionPane.showMessageDialog(null, "Opción no válida.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese un valor numérico donde se requiera.");
        }
    }

public static void crearPedido() {
    try {
        // Solicitar los datos del pedido
        int numero = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el número del pedido:"));

        LocalDateTime fechaHora = null;
        boolean formatoValido = false;

        while (!formatoValido) {
            try {
                String fechaHoraStr = JOptionPane.showInputDialog("Ingrese la fecha y hora (formato: yyyy-MM-dd'T'HH:mm):");
                if (fechaHoraStr == null) return;
                fechaHora = LocalDateTime.parse(fechaHoraStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
                formatoValido = true;
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(null, "Formato de fecha y hora inválido. Por favor, ingrese en el formato correcto (yyyy-MM-dd'T'HH:mm).");
            }
        }
        String identificacion = JOptionPane.showInputDialog("Ingrese la identificación del cliente:");
        String nombre = JOptionPane.showInputDialog("Ingrese el nombre del cliente:");
        String direccion = JOptionPane.showInputDialog("Ingrese la dirección del cliente:");
        Cliente cliente = new Cliente(identificacion, nombre, direccion);

        LinkedList<ItemProducto> itemsProductos = new LinkedList<>();
        String respuesta = JOptionPane.showInputDialog("¿Desea agregar ítems de producto? (s/n):");
        while (respuesta.equalsIgnoreCase("s")) {
            int codigo = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el código del producto:"));
            String nombreProducto = JOptionPane.showInputDialog("Ingrese el nombre del producto:");
            double cantidad = Double.parseDouble(JOptionPane.showInputDialog("Ingrese la cantidad:"));
            double precio = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el precio:"));
            String categoria = JOptionPane.showInputDialog("Ingrese la categoría del producto (O: Otro, C: Canasta Familiar, F: Farmacia):");

            ItemProducto itemProducto;
            switch (categoria.toUpperCase()) {
                case "O" -> {
                    double porcentajeIva = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el porcentaje de IVA:"));
                    itemProducto = new ItemProductoOtro(codigo, nombreProducto, cantidad, precio, porcentajeIva);
                }
                case "C" -> {
                    String tipo = JOptionPane.showInputDialog("Ingrese el tipo (Grano, Carne, Aseo hogar, Aseo personal, Lácteo, Fruta, Verdura, Legumbre, Papa):");
                    itemProducto = new ItemProductoCanastaFamiliar(codigo, nombreProducto, cantidad, precio, tipo);
                }
                case "F" -> {
                    String presentacion = JOptionPane.showInputDialog("Ingrese la presentación (Caja, Frasco, etc.):");
                    itemProducto = new ItemProductoFarmacia(codigo, nombreProducto, cantidad, precio, presentacion);
                }
                default -> {
                    JOptionPane.showMessageDialog(null, "Categoría de producto inválida. Se creará un producto de categoría 'Otro'.");
                    itemProducto = new ItemProductoOtro(codigo, nombreProducto, cantidad, precio, 0.19); // IVA del 19% por defecto
                }
            }
            itemsProductos.add(itemProducto);
            respuesta = JOptionPane.showInputDialog("¿Desea agregar otro ítem de producto? (s/n):");
        }

        String observacion = JOptionPane.showInputDialog("Ingrese la observación del pedido:");
        char estado = JOptionPane.showInputDialog("Ingrese el estado del pedido (A: abierto, D: despachado, C: cancelado):").charAt(0);
        boolean normal = JOptionPane.showInputDialog("¿Es un pedido normal? (s/n):").equalsIgnoreCase("s");

        int valorUrgencia = 0;
        if (!normal) {
            valorUrgencia = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el valor de urgencia:"));
        }

        Pedido pedido = new Pedido(numero, fechaHora, cliente, itemsProductos, observacion, estado, normal);
        if (normal) {
            pedido.calcularValorTotalPagar(); // Llamada al método sin valorUrgencia
        } else {
            pedido.calcularValorTotalPagar(valorUrgencia); // Llamada al método con valorUrgencia
        }

        losPedidos.add(pedido);
        JOptionPane.showMessageDialog(null, "Pedido creado correctamente.");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error al crear el pedido: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private static void actualizarEstadoPedido(int numeroPedido, char estadoPedido) {
        if (estadoPedido != 'A' && estadoPedido != 'D' && estadoPedido != 'C') {
            JOptionPane.showMessageDialog(null, "Estado no válido. Ingrese A (abierto), D (despachado) o C (cancelado).");
            return;
        }

        Pedido pedidoEncontrado = null;
        for (Pedido pedido : losPedidos) {
            if (pedido.getNumero() == numeroPedido) {
                pedidoEncontrado = pedido;
                break;
            }
        }

        if (pedidoEncontrado != null) {
            pedidoEncontrado.setEstado(estadoPedido);
            JOptionPane.showMessageDialog(null, "El estado del pedido #" + numeroPedido + " ha sido actualizado a " + estadoPedido);
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró un pedido con el número " + numeroPedido);
        }
    }

   public static void consultarTodosPedidos() {
    StringBuilder resultadoBuilder = new StringBuilder("REPORTE DE TODOS LOS PEDIDOS \n\n");
    for (Pedido objPedido : losPedidos) {
        resultadoBuilder.append("Número del pedido: ").append(objPedido.getNumero()).append("\n")
            .append("Fecha-hora del pedido: ").append(objPedido.getFechaHora()).append("\n")
            .append("Cliente: ").append(objPedido.getSuCliente().getNombre())
            .append(" - ID: ").append(objPedido.getSuCliente().getIdentificacion())
            .append(" - Dirección: ").append(objPedido.getSuCliente().getDireccion()).append("\n")
            .append("El pedido está normal: ").append(objPedido.isNormal() ? "Sí" : "No").append("\n")
            .append("Estado: ").append(objPedido.getEstado()).append("\n")
            .append("Observaciones: ").append(objPedido.getObservacion()).append("\n")
            .append("Cantidad de items del pedido: ").append(objPedido.calcularCantidadItemsPedidos()).append("\n")
            .append("Valor total de los items pedidos: ").append(String.format("%.2f", objPedido.calcularValorTotalItems())).append("\n");

        if (objPedido.isNormal()) {
            resultadoBuilder.append("Valor total a pagar (pedido normal): ").append(String.format("%.2f", objPedido.calcularValorTotalPagar())).append("\n");
        } else {
            resultadoBuilder.append("Valor total a pagar (pedido urgente): ").append(String.format("%.2f", objPedido.calcularValorTotalPagar(5000))).append("\n");  // Aquí debes pasar el valor de urgencia real
        }
        
        resultadoBuilder.append("Productos:\n");
        for (ItemProducto item : objPedido.getSusItemsProductos()) {
            resultadoBuilder.append(" - ").append(item.getNombre()).append(", Código: ").append(item.getCodigo())
                .append(", Cantidad: ").append(item.getCantidad()).append(", Precio: $").append(item.getPrecio()).append("\n");

            switch (item) {
                case ItemProductoFarmacia itemProductoFarmacia -> resultadoBuilder.append("   Presentación: ").append(itemProductoFarmacia.getPresentacion()).append("\n");
                case ItemProductoCanastaFamiliar itemProductoCanastaFamiliar -> resultadoBuilder.append("   Tipo: ").append(itemProductoCanastaFamiliar.getTipo()).append("\n");
                case ItemProductoOtro itemProductoOtro -> resultadoBuilder.append("   Porcentaje IVA: ").append(itemProductoOtro.getPorcentajeIva()).append("%\n");
                default -> {
                }
            }
        }
        resultadoBuilder.append("\n");
    }

    JTextArea textArea = new JTextArea(resultadoBuilder.toString());
    textArea.setEditable(false);
    JScrollPane scrollPane = new JScrollPane(textArea);
    scrollPane.setPreferredSize(new Dimension(500, 400));
    JOptionPane.showMessageDialog(null, scrollPane, "Todos los Pedidos", JOptionPane.INFORMATION_MESSAGE);
}
    private static String consultarPedidoNumero(int numeroPedido) {
        Pedido pedidoEncontrado = losPedidos.stream()
            .filter(p -> p.getNumero() == numeroPedido)
            .findFirst()
            .orElse(null);

        if (pedidoEncontrado != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Pedido #").append(pedidoEncontrado.getNumero()).append("\n")
              .append("Fecha/Hora: ").append(pedidoEncontrado.getFechaHora()).append("\n")
              .append("Cliente: ").append(pedidoEncontrado.getSuCliente().getNombre())
              .append(" - ID: ").append(pedidoEncontrado.getSuCliente().getIdentificacion())
              .append(" - Dirección: ").append(pedidoEncontrado.getSuCliente().getDireccion()).append("\n")
              .append("Estado: ").append(pedidoEncontrado.getEstado()).append("\n")
              .append("Observaciones: ").append(pedidoEncontrado.getObservacion()).append("\n")
              .append("El pedido está normal: ").append(pedidoEncontrado.isNormal() ? "Sí" : "No").append("\n")
              .append("Productos:\n");

            double valorTotalItems = 0;
            for (ItemProducto item : pedidoEncontrado.getSusItemsProductos()) {
                sb.append(" - ").append(item.getNombre()).append(", Código: ").append(item.getCodigo())
                  .append(", Cantidad: ").append(item.getCantidad()).append(", Precio: $").append(item.getPrecio()).append("\n");

                switch (item) {
                    case ItemProductoFarmacia itemProductoFarmacia -> sb.append("   Presentación: ").append(itemProductoFarmacia.getPresentacion()).append("\n");
                    case ItemProductoCanastaFamiliar itemProductoCanastaFamiliar -> sb.append("   Tipo: ").append(itemProductoCanastaFamiliar.getTipo()).append("\n");
                    case ItemProductoOtro itemProductoOtro -> sb.append("   Porcentaje IVA: ").append(itemProductoOtro.getPorcentajeIva()).append("%\n");
                    default -> {
                    }
                }

                valorTotalItems += item.getPrecio() * item.getCantidad();
            }

            sb.append("Cantidad de ítems de pedido: ").append(pedidoEncontrado.getSusItemsProductos().size()).append("\n")
              .append("Valor total de los ítems: $").append(valorTotalItems).append("\n");

            String resultado = sb.toString();
            JTextArea textArea = new JTextArea(resultado);
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            JOptionPane.showMessageDialog(null, scrollPane, "Consulta de Pedido por Número", JOptionPane.INFORMATION_MESSAGE);

            return resultado;
        } else {
            String mensajeError = "No se encontró un pedido con el número " + numeroPedido;
            JOptionPane.showMessageDialog(null, mensajeError);
            return mensajeError;
        }
    }

    private static void consultarUltimoPedido() {
        if (losPedidos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay pedidos para mostrar.");
            return;
        }

        Pedido ultimoPedido = losPedidos.getLast();

        StringBuilder sb = new StringBuilder();
        sb.append("Último Pedido:\n")
          .append("Pedido #").append(ultimoPedido.getNumero()).append("\n")
          .append("Fecha/Hora: ").append(ultimoPedido.getFechaHora()).append("\n")
          .append("Cliente: ").append(ultimoPedido.getSuCliente().getNombre())
          .append(" - ID: ").append(ultimoPedido.getSuCliente().getIdentificacion())
          .append(" - Dirección: ").append(ultimoPedido.getSuCliente().getDireccion()).append("\n")
          .append("Estado: ").append(ultimoPedido.getEstado()).append("\n")
          .append("Observaciones: ").append(ultimoPedido.getObservacion()).append("\n")
          .append("El pedido está normal: ").append(ultimoPedido.isNormal() ? "Sí" : "No").append("\n")
          .append("Productos:\n");

        double valorTotalItems = 0;
        for (ItemProducto item : ultimoPedido.getSusItemsProductos()) {
            sb.append(" - ").append(item.getNombre()).append(", Código: ").append(item.getCodigo())
              .append(", Cantidad: ").append(item.getCantidad()).append(", Precio: $").append(item.getPrecio()).append("\n");

            switch (item) {
                case ItemProductoFarmacia itemProductoFarmacia -> sb.append("   Presentación: ").append(itemProductoFarmacia.getPresentacion()).append("\n");
                case ItemProductoCanastaFamiliar itemProductoCanastaFamiliar -> sb.append("   Tipo: ").append(itemProductoCanastaFamiliar.getTipo()).append("\n");
                case ItemProductoOtro itemProductoOtro -> sb.append("   Porcentaje IVA: ").append(itemProductoOtro.getPorcentajeIva()).append("%\n");
                default -> {
                }
            }

            valorTotalItems += item.getPrecio() * item.getCantidad();
        }

        sb.append("Cantidad de ítems de pedido: ").append(ultimoPedido.getSusItemsProductos().size()).append("\n")
          .append("Valor total de los ítems: $").append(valorTotalItems).append("\n");

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(null, scrollPane, "Consulta del Último Pedido", JOptionPane.INFORMATION_MESSAGE);
    }


    private static void eliminarTodosPedidos() {
        int confirmacion = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar todos los pedidos?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            losPedidos.clear();
            JOptionPane.showMessageDialog(null, "Todos los pedidos han sido eliminados.");
        }
    }

    public static void eliminarUnPedido(int numeroPedido) {
        boolean encontrado = false;

        for (int i = 0; i < losPedidos.size(); i++) {
            if (losPedidos.get(i).getNumero() == numeroPedido) {
                int confirm = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar el pedido número " + numeroPedido + "?",
                                                            "Eliminar Pedido", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    losPedidos.remove(i);
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
    }

    private static void eliminarUltimoPedido() {
        if (losPedidos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay pedidos para eliminar.");
            return;
        }

        Pedido ultimoPedido = losPedidos.getLast();
        int confirm = JOptionPane.showConfirmDialog(null,
            "¿Está seguro de que desea eliminar el último pedido realizado (Pedido #" + ultimoPedido.getNumero() + ")?",
            "Eliminar Último Pedido", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            losPedidos.removeLast();
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

        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(ARCHIVO_PEDIDOS)))) {
            for (Pedido p : losPedidos) {
                out.print(p.getNumero() + "--" + p.getFechaHora().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")) +
                        "--" + p.getSuCliente().getNombre() +
                        "--" + p.getSuCliente().getIdentificacion() +
                        "--" + p.getSuCliente().getDireccion() +
                        "--" + (p.isNormal() ? 'N' : 'E') +
                        "--" + p.getEstado() +
                        "--" + p.getObservacion() +
                        "--" + p.calcularCantidadItemsPedidos() +
                        "--" + p.calcularValorTotalItems());

                for (ItemProducto item : p.getSusItemsProductos()) {
                    out.print("--" + item.getNombre() + "--" + item.getCodigo() + "--" + item.getCantidad() + "--" + item.getPrecio());

                    switch (item) {
                        case ItemProductoCanastaFamiliar itemProductoCanastaFamiliar -> out.print("--" + itemProductoCanastaFamiliar.getTipo());
                        case ItemProductoFarmacia itemProductoFarmacia -> out.print("--" + itemProductoFarmacia.getPresentacion());
                        case ItemProductoOtro itemProductoOtro -> out.print("--" + itemProductoOtro.getPorcentajeIva());
                        default -> {
                        }
                    }

                    out.print("\n");
                }

                out.print("\n");  // Separar pedidos
            }

            JOptionPane.showMessageDialog(null, "Archivo de pedidos generado con éxito.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al escribir el archivo de pedidos: " + e.getMessage());
        }
    }

    private static void recuperarDesdeArchivoTextoDePedidos() {
        try (BufferedReader entrada = new BufferedReader(new FileReader(ARCHIVO_PEDIDOS))) {
            String linea;
            while ((linea = entrada.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;  // Saltar líneas vacías
                }

                String[] datos = linea.split("--");
                if (datos.length > 9) {  // Asegurarse de que hay suficientes datos antes de intentar parsear
                    try {
                        int numeroPedido = Integer.parseInt(datos[0]);
                        LocalDateTime fechaHora = LocalDateTime.parse(datos[1], DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
                        String nombreCliente = datos[2];
                        String identificacionCliente = datos[3];
                        String direccionCliente = datos[4];
                        boolean esNormal = datos[5].equals("N");
                        char estado = datos[6].charAt(0);
                        String observacion = datos[7];

                        Cliente cliente = new Cliente(identificacionCliente, nombreCliente, direccionCliente);
                        LinkedList<ItemProducto> productos = new LinkedList<>();

                        for (int i = 10; i < datos.length; i++) {
                            String[] datosItem = datos[i].split("--");
                            if (datosItem.length > 4) {
                                try {
                                    String nombreProducto = datosItem[0];
                                    int codigoProducto = Integer.parseInt(datosItem[1]);
                                    double cantidadProducto = Double.parseDouble(datosItem[2]);
                                    double precioProducto = Double.parseDouble(datosItem[3]);
                                    String tipoProducto = datosItem[4];

                                    ItemProducto producto = null;
                                    switch (tipoProducto.toLowerCase()) {
                                        case "tipo:" -> {
                                            String tipo = datosItem[5];
                                            producto = new ItemProductoCanastaFamiliar(codigoProducto, nombreProducto, cantidadProducto, precioProducto, tipo);
                                        }
                                        case "presentación:" -> {
                                            String presentacion = datosItem[5];
                                            producto = new ItemProductoFarmacia(codigoProducto, nombreProducto, cantidadProducto, precioProducto, presentacion);
                                        }
                                        case "iva:" -> {
                                            double porcentajeIva = Double.parseDouble(datosItem[5]);
                                            producto = new ItemProductoOtro(codigoProducto, nombreProducto, cantidadProducto, precioProducto, porcentajeIva);
                                        }
                                    }

                                    if (producto != null) {
                                        productos.add(producto);
                                    }
                                } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                                    JOptionPane.showMessageDialog(null, "Error al parsear los datos del producto: " + e.getMessage());
                                }
                            }
                        }

                        Pedido pedido = new Pedido(numeroPedido, fechaHora, cliente, productos, observacion, estado, esNormal);
                        losPedidos.add(pedido);
                    } catch (NumberFormatException | DateTimeParseException | StringIndexOutOfBoundsException e) {
                        JOptionPane.showMessageDialog(null, "Error al parsear los datos del pedido: " + e.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Línea mal formateada o incompleta en el archivo: " + linea);
                }
            }

            JOptionPane.showMessageDialog(null, "Pedidos recuperados desde el archivo de texto con éxito.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al recuperar pedidos desde el archivo de texto: " + e.getMessage());
        }
    }
}
