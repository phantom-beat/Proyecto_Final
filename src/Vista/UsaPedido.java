package Vista;

import modelo.*;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class UsaPedido {
    private static final LinkedList<Pedido> losPedidos = new LinkedList<>();
    private static final String ARCHIVO_PEDIDOS = "pedidos.txt";

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
                handleMenuOption(opcion);
            } else {
                break;
            }
        } while (true);
    }

    private static void handleMenuOption(int opcion) {
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
                    String resultado = consultarPedidoNumero(numero);
                    JOptionPane.showMessageDialog(null, resultado);
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
                default -> JOptionPane.showMessageDialog(null, "Opción no válida.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese un valor numérico donde se requiera.");
        }
    }

    private static void crearPedido() {
        try {
            String idCliente = JOptionPane.showInputDialog("Ingrese la identificación del cliente:");
            String nombreCliente = JOptionPane.showInputDialog("Ingrese el nombre del cliente:");
            String direccionCliente = JOptionPane.showInputDialog("Ingrese la dirección del cliente:");
            String estadoPedido = JOptionPane.showInputDialog("Ingrese el estado del pedido (A: abierto, D: despachado, C: cancelado):");
            String observaciones = JOptionPane.showInputDialog("Ingrese las observaciones del pedido:");

            LocalDateTime fechaHora = null;
            boolean formatoValido = false;
            while (!formatoValido) {
                try {
                    String fechaHoraStr = JOptionPane.showInputDialog("Ingrese la fecha y hora (formato: yyyy-MM-dd'T'HH:mm):");
                    fechaHora = LocalDateTime.parse(fechaHoraStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
                    formatoValido = true;
                } catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(null, "Formato de fecha y hora inválido. Por favor, ingrese en el formato correcto (yyyy-MM-dd'T'HH:mm).");
                }
            }

            char estado = estadoPedido.charAt(0);
            Cliente cliente = new Cliente(idCliente, nombreCliente, direccionCliente);
            LinkedList<ItemProducto> productos = new LinkedList<>();

            boolean agregarMas = true;
            while (agregarMas) {
                String codigoProducto = JOptionPane.showInputDialog("Ingrese el código del producto:");
                String nombreProducto = JOptionPane.showInputDialog("Ingrese el nombre del producto:");
                double cantidadProducto = Double.parseDouble(JOptionPane.showInputDialog("Ingrese la cantidad del producto:"));
                double precioProducto = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el precio del producto:"));
                String tipoProducto = JOptionPane.showInputDialog("Ingrese el tipo de producto (Farmacia/Canasta/Otro):");

                ItemProducto producto = null;
                switch (tipoProducto.toLowerCase()) {
                    case "farmacia" -> {
                        String presentacion = JOptionPane.showInputDialog("Ingrese la presentación del producto farmacéutico:");
                        producto = new ItemProductoFarmacia(Integer.parseInt(codigoProducto), nombreProducto, cantidadProducto, precioProducto, presentacion);
                    }
                    case "canasta" -> {
                        String tipo = JOptionPane.showInputDialog("Ingrese el tipo de canasta (Grano, Carne, etc.):");
                        producto = new ItemProductoCanastaFamiliar(Integer.parseInt(codigoProducto), nombreProducto, cantidadProducto, precioProducto, tipo);
                    }
                    case "otro" -> {
                        double iva = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el porcentaje de IVA del producto:"));
                        producto = new ItemProductoOtro(Integer.parseInt(codigoProducto), nombreProducto, cantidadProducto, precioProducto, iva);
                    }
                }

                if (producto != null) {
                    productos.add(producto);
                }

                int respuesta = JOptionPane.showConfirmDialog(null, "¿Desea agregar otro producto?", "Agregar Producto", JOptionPane.YES_NO_OPTION);
                agregarMas = (respuesta == JOptionPane.YES_OPTION);
            }

                Pedido nuevoPedido = new Pedido(losPedidos.size() + 1, fechaHora, cliente, productos, observaciones, estado, false);
                losPedidos.add(nuevoPedido);

                boolean normal = Boolean.parseBoolean(JOptionPane.showInputDialog("¿El pedido es normal? (true/false):"));
                double valorTotal;

                if (normal) {
                    // Si el pedido es normal, se calcula el valor total sin valor de urgencia
                    valorTotal = nuevoPedido.calcularValorTotalPagar();
                    JOptionPane.showMessageDialog(null, "El valor total del pedido normal es: " + valorTotal);
                } else {
                    // Si el pedido es urgente, se solicita el valor de urgencia y se calcula el valor total con este
                    int valorUrgencia = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el valor de urgencia:"));
                    valorTotal = nuevoPedido.calcularValorTotalPagar(valorUrgencia);
                    JOptionPane.showMessageDialog(null, "El valor total del pedido urgente es: " + valorTotal);
                }

            guardarPedidoEnArchivo();
            JOptionPane.showMessageDialog(null, "Pedido creado y guardado con éxito.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error al ingresar datos numéricos: " + e.getMessage());
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(null, "Error al crear el pedido: " + e.getMessage());
        }
    }

    private static void guardarPedidoEnArchivo() {
        try (PrintWriter out = new PrintWriter(new FileWriter(ARCHIVO_PEDIDOS))) {
            for (Pedido p : losPedidos) {
                out.println("Pedido #" + p.getNumero());
                out.println("Fecha/Hora: " + p.getFechaHora());
                out.println("Cliente: " + p.getSuCliente().getIdentificacion() + " - " + p.getSuCliente().getNombre() + " - " + p.getSuCliente().getDireccion());
                out.println("Estado: " + p.getEstado());
                out.println("Observaciones: " + p.getObservacion());
                out.println("Productos:");
                for (ItemProducto item : p.getSusItemsProductos()) {
                    out.println("   - " + item.getClass().getSimpleName() + " - " + item.getNombre() + ", Código: " + item.getCodigo() + ", Cantidad: " + item.getCantidad() + ", Precio: $" + item.getPrecio());
                    if (item instanceof ItemProductoFarmacia) {
                        out.println("       Presentación: " + ((ItemProductoFarmacia) item).getPresentacion());
                    } else if (item instanceof ItemProductoCanastaFamiliar) {
                        out.println("       Tipo: " + ((ItemProductoCanastaFamiliar) item).getTipo());
                    } else if (item instanceof ItemProductoOtro) {
                        out.println("       IVA: " + ((ItemProductoOtro) item).getPorcentajeIva() + "%");
                    }
                }
                out.println("--------------------------------------------------");
            }
            out.flush();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar los pedidos en el archivo: " + e.getMessage());
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
            actualizarArchivoPedido(pedidoEncontrado);
        } else {
            JOptionPane.showMessageDialog(null, "No se encontró un pedido con el número " + numeroPedido);
        }
    }

    private static void actualizarArchivoPedido(Pedido pedido) {
        try (PrintWriter out = new PrintWriter(new FileWriter(ARCHIVO_PEDIDOS))) {
            for (Pedido p : losPedidos) {
                out.println("Pedido #" + p.getNumero());
                out.println("Fecha/Hora: " + p.getFechaHora());
                out.println("Cliente: " + p.getSuCliente().getNombre() + " - ID: " + p.getSuCliente().getIdentificacion() + " - Dirección: " + p.getSuCliente().getDireccion());
                out.println("Estado: " + p.getEstado());
                for (ItemProducto item : p.getSusItemsProductos()) {
                    out.println("   - " + item.getNombre() + ", Código: " + item.getCodigo() + ", Cantidad: " + item.getCantidad() + ", Precio: $" + item.getPrecio());
                }
                out.println("Observaciones: " + p.getObservacion());
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

    private static String consultarPedidoNumero(int numeroPedido) {
        Pedido pedidoEncontrado = losPedidos.stream()
            .filter(p -> p.getNumero() == numeroPedido)
            .findFirst()
            .orElse(null);

        if (pedidoEncontrado != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Pedido #").append(pedidoEncontrado.getNumero()).append("\n");
            sb.append("Fecha/Hora: ").append(pedidoEncontrado.getFechaHora()).append("\n");
            sb.append("Cliente: ").append(pedidoEncontrado.getSuCliente().getNombre())
              .append(" - ID: ").append(pedidoEncontrado.getSuCliente().getIdentificacion())
              .append(" - Dirección: ").append(pedidoEncontrado.getSuCliente().getDireccion()).append("\n");
            sb.append("Estado: ").append(pedidoEncontrado.getEstado()).append("\n");
            sb.append("Observaciones: ").append(pedidoEncontrado.getObservacion()).append("\n");
            sb.append("Productos:\n");

            double valorTotalItems = 0;
            for (ItemProducto item : pedidoEncontrado.getSusItemsProductos()) {
                sb.append(" - ").append(item.getNombre()).append(", Código: ").append(item.getCodigo())
                  .append(", Cantidad: ").append(item.getCantidad()).append(", Precio: $").append(item.getPrecio()).append("\n");

                if (item instanceof ItemProductoFarmacia) {
                    ItemProductoFarmacia farmacia = (ItemProductoFarmacia) item;
                    sb.append("   Presentación: ").append(farmacia.getPresentacion()).append("\n");
                } else if (item instanceof ItemProductoCanastaFamiliar) {
                    ItemProductoCanastaFamiliar canasta = (ItemProductoCanastaFamiliar) item;
                    sb.append("   Tipo: ").append(canasta.getTipo()).append("\n");
                } else if (item instanceof ItemProductoOtro) {
                    ItemProductoOtro otro = (ItemProductoOtro) item;
                    sb.append("   Porcentaje IVA: ").append(otro.getPorcentajeIva()).append("%\n");
                }

                valorTotalItems += item.getPrecio() * item.getCantidad();
            }

            sb.append("Cantidad de ítems de pedido: ").append(pedidoEncontrado.getSusItemsProductos().size()).append("\n");
            sb.append("Valor total de los ítems: $").append(valorTotalItems).append("\n");
            return sb.toString();
        } else {
            return "No se encontró un pedido con el número " + numeroPedido;
        }
    }

    private static void consultarUltimoPedido() {
        if (losPedidos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay pedidos para mostrar.");
            return;
        }

        Pedido ultimoPedido = losPedidos.getLast();

        String mensaje;
        mensaje = """
                  Último Pedido:
                  Pedido #""" + ultimoPedido.getNumero() + "\n"
                + "Fecha/Hora: " + ultimoPedido.getFechaHora() + "\n"
                + "Cliente: " + ultimoPedido.getSuCliente().getNombre()
                + " - ID: " + ultimoPedido.getSuCliente().getIdentificacion()
                + " - Dirección: " + ultimoPedido.getSuCliente().getDireccion() + "\n"
                + "Estado: " + ultimoPedido.getEstado() + "\n"
                + "Observaciones: " + ultimoPedido.getObservacion() + "\n"
                + "Productos:\n";

        double valorTotalItems = 0;
        for (ItemProducto item : ultimoPedido.getSusItemsProductos()) {
            mensaje += " - " + item.getNombre() + ", Código: " + item.getCodigo()
                    + ", Cantidad: " + item.getCantidad() + ", Precio: $" + item.getPrecio() + "\n";

            double valorTotal = item.getCantidad() * item.getPrecio();
            valorTotalItems += valorTotal;
        }

        int valorUrgencia = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el valor de urgencia del pedido:"));
        valorTotalItems += valorUrgencia;

        mensaje += "Cantidad de ítems de pedido: " + ultimoPedido.getSusItemsProductos().size() + "\n";
        mensaje += "Valor total de los ítems: $" + valorTotalItems + "\n";

        JTextArea textArea = new JTextArea(mensaje);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        JOptionPane.showMessageDialog(null, scrollPane, "Consulta del Último Pedido", JOptionPane.INFORMATION_MESSAGE);
    }

    private static void eliminarTodosPedidos() {
        int confirm = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar todos los pedidos?",
                                                    "Eliminar Todos los Pedidos", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            losPedidos.clear();
            JOptionPane.showMessageDialog(null, "Todos los pedidos han sido eliminados.");
        } else {
            JOptionPane.showMessageDialog(null, "Operación cancelada.");
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

        try (PrintWriter out = new PrintWriter(new FileWriter(ARCHIVO_PEDIDOS, false))) {
            for (Pedido p : losPedidos) {
                out.println(p.getNumero() +
                    "--" + p.getFechaHora() +
                    "--" + p.getSuCliente().getNombre() +
                    "--" + p.getSuCliente().getIdentificacion() +
                    "--" + p.getSuCliente().getDireccion() +
                    "--" + p.isNormal() +
                    "--" + p.getEstado() +
                    "--" + p.getObservacion() +
                    "--" + p.calcularCantidadItemsPedidos() +
                    "--" + p.calcularValorTotalItems());

                for (ItemProducto item : p.getSusItemsProductos()) {
                    out.print(item.getNombre());
                    out.print(item.getCodigo());
                    out.print(item.getCantidad());
                    out.print(item.getPrecio());
                   
                    if (item instanceof ItemProductoCanastaFamiliar) {
                        ItemProductoCanastaFamiliar canasta = (ItemProductoCanastaFamiliar) item;
                        out.print(canasta.getTipo());
                    } else if (item instanceof ItemProductoFarmacia) {
                        ItemProductoFarmacia farmacia = (ItemProductoFarmacia) item;
                        out.print(farmacia.getPresentacion());
                    } else if (item instanceof ItemProductoOtro) {
                        ItemProductoOtro otro = (ItemProductoOtro) item;
                        out.print(otro.getPorcentajeIva() + "%");
                    }
                    out.println(); 
                }

            }

            JOptionPane.showMessageDialog(null, "Archivo de pedidos generado con éxito.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al escribir el archivo de pedidos: " + e.getMessage());
        }
    }


    private static void recuperarPedidosDesdeArchivoTexto() {
        losPedidos.clear(); 

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(ARCHIVO_PEDIDOS), StandardCharsets.UTF_8))) {
            String line;
            Pedido pedidoActual = null;
            LinkedList<ItemProducto> productos = new LinkedList<>();

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Número del pedido:")) {
                    if (pedidoActual != null) { 
                        pedidoActual.setSusItemsProductos(productos);
                        losPedidos.add(pedidoActual);
                    }
                    int numero = Integer.parseInt(line.split(":")[1].trim());
                    pedidoActual = new Pedido();
                    pedidoActual.setNumero(numero);
                    productos = new LinkedList<>();
                } else if (line.startsWith("Fecha-hora del pedido:")) {
                    String dateTimePart = line.split(":")[1].trim();
                    try {
                        LocalDateTime fechaHora = LocalDateTime.parse(dateTimePart, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
                        pedidoActual.setFechaHora(fechaHora);
                    } catch (DateTimeParseException e) {
                        try {
                            LocalDateTime fechaHora = LocalDateTime.parse(dateTimePart, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH"));
                            pedidoActual.setFechaHora(fechaHora);
                        } catch (DateTimeParseException ex) {
                            System.out.println("Fecha y hora completamente inválidas: " + dateTimePart);
                            pedidoActual.setFechaHora(null);
                        }
                    }
                } else if (line.startsWith("Datos del cliente:")) {
                    String[] clienteParts = line.split(" - ");
                    if (clienteParts.length >= 3) {
                        String clienteNombre = clienteParts[0].split(":")[1].trim();
                        String clienteID = clienteParts[1].split(":")[1].trim();
                        String clienteDireccion = clienteParts[2].split(":")[1].trim();
                        Cliente cliente = new Cliente(clienteID, clienteNombre, clienteDireccion);
                        pedidoActual.setSuCliente(cliente);
                    }
                } else if (line.startsWith("Estado del pedido:")) {
                    char estado = line.split(":")[1].trim().charAt(0);
                    pedidoActual.setEstado(estado);
                } else if (line.startsWith("Observación del pedido:")) {
                    String observacion = line.split(":")[1].trim();
                    pedidoActual.setObservacion(observacion);
                } else if (line.startsWith("   -")) {
                    try {
                        String productDetails = line.trim().substring(2).trim();
                        String[] parts = productDetails.split(", ");
                        if (parts.length >= 5) {
                            String nombreProducto = parts[0].split(": ")[1].trim();
                            int codigo = Integer.parseInt(parts[1].split(": ")[1].trim());
                            double cantidad = Double.parseDouble(parts[2].split(": ")[1].trim());
                            double precio = Double.parseDouble(parts[3].split(": $")[1].trim());
                            String presentacion = parts[4].split(": ")[1].trim();
                            ItemProducto producto = new ItemProductoFarmacia(codigo, nombreProducto, cantidad, precio, presentacion);
                            productos.add(producto);
                        } else {
                            System.out.println("Información del producto incompleta o mal formada en la línea: " + line);
                        }
                    } catch (Exception e) {
                        System.out.println("Error parsing product line: " + line);
                    }
                }
            }
            if (pedidoActual != null) {
                pedidoActual.setSusItemsProductos(productos);
                losPedidos.add(pedidoActual);
            }
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error al recuperar los pedidos desde el archivo: " + e.getMessage());
        }
    }


}


