package Vista;

import modelo.*;
import java.awt.Dimension;
import java.awt.HeadlessException;
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

     private static void crearPedido() {
    try {
        String idCliente = JOptionPane.showInputDialog("Ingrese la identificación del cliente:");
        String nombreCliente = JOptionPane.showInputDialog("Ingrese el nombre del cliente:");
        String direccionCliente = JOptionPane.showInputDialog("Ingrese la dirección del cliente:");
        
        String estadoPedido = null;
        while (estadoPedido == null || !estadoPedido.matches("[ADC]")) {
            estadoPedido = JOptionPane.showInputDialog("Ingrese el estado del pedido (A: abierto, D: despachado, C: cancelado):");
            if (estadoPedido == null || !estadoPedido.matches("[ADC]")) {
                JOptionPane.showMessageDialog(null, "Estado no válido. Ingrese A (abierto), D (despachado) o C (cancelado).");
            }
        }
        
        String observaciones = JOptionPane.showInputDialog("Ingrese las observaciones del pedido:");

        LocalDateTime fechaHora = null;
        while (fechaHora == null) {
            try {
                String fechaHoraStr = JOptionPane.showInputDialog("Ingrese la fecha y hora (formato: yyyy-MM-dd'T'HH:mm):");
                fechaHora = LocalDateTime.parse(fechaHoraStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(null, "Formato de fecha y hora inválido. Por favor, ingrese en el formato correcto (yyyy-MM-dd'T'HH:mm).");
            }
        }

        char estado = estadoPedido.charAt(0);
        Cliente cliente = new Cliente(idCliente, nombreCliente, direccionCliente);
        LinkedList<ItemProducto> productos = new LinkedList<>();

        boolean agregarMas = true;
        while (agregarMas) {
            int codigoProducto = -1;
            while (codigoProducto < 0) {
                try {
                    codigoProducto = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el código del producto:"));
                    if (codigoProducto < 0) {
                        JOptionPane.showMessageDialog(null, "Código no válido. Por favor, ingrese un número positivo.");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Código no válido. Por favor, ingrese un número.");
                }
            }
            
            String nombreProducto = JOptionPane.showInputDialog("Ingrese el nombre del producto:");
            
            double cantidadProducto = -1;
            while (cantidadProducto < 0) {
                try {
                    cantidadProducto = Double.parseDouble(JOptionPane.showInputDialog("Ingrese la cantidad del producto:"));
                    if (cantidadProducto < 0) {
                        JOptionPane.showMessageDialog(null, "Cantidad no válida. Por favor, ingrese un número positivo.");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Cantidad no válida. Por favor, ingrese un número.");
                }
            }
            
            double precioProducto = -1;
            while (precioProducto < 0) {
                try {
                    precioProducto = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el precio del producto:"));
                    if (precioProducto < 0) {
                        JOptionPane.showMessageDialog(null, "Precio no válido. Por favor, ingrese un número positivo.");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Precio no válido. Por favor, ingrese un número.");
                }
            }
            
            String tipoProducto = null;
            while (tipoProducto == null || (!tipoProducto.equalsIgnoreCase("farmacia") && !tipoProducto.equalsIgnoreCase("canasta") && !tipoProducto.equalsIgnoreCase("otro"))) {
                tipoProducto = JOptionPane.showInputDialog("Ingrese el tipo de producto (Farmacia/Canasta/Otro):");
                if (tipoProducto == null || (!tipoProducto.equalsIgnoreCase("farmacia") && !tipoProducto.equalsIgnoreCase("canasta") && !tipoProducto.equalsIgnoreCase("otro"))) {
                    JOptionPane.showMessageDialog(null, "Tipo de producto no válido. Por favor, ingrese Farmacia, Canasta u Otro.");
                }
            }

            ItemProducto producto = null;
            switch (tipoProducto.toLowerCase()) {
                case "farmacia" -> {
                    String presentacion = JOptionPane.showInputDialog("Ingrese la presentación del producto farmacéutico:");
                    producto = new ItemProductoFarmacia(codigoProducto, nombreProducto, cantidadProducto, precioProducto, presentacion);
                }
                case "canasta" -> {
                    String tipo = JOptionPane.showInputDialog("Ingrese el tipo de canasta (Grano, Carne, etc.):");
                    producto = new ItemProductoCanastaFamiliar(codigoProducto, nombreProducto, cantidadProducto, precioProducto, tipo);
                }
                case "otro" -> {
                    double iva = -1;
                    while (iva < 0) {
                        try {
                            iva = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el porcentaje de IVA del producto:"));
                            if (iva < 0) {
                                JOptionPane.showMessageDialog(null, "Porcentaje de IVA no válido. Por favor, ingrese un número positivo.");
                            }
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(null, "Porcentaje de IVA no válido. Por favor, ingrese un número.");
                        }
                    }
                    producto = new ItemProductoOtro(codigoProducto, nombreProducto, cantidadProducto, precioProducto, iva);
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

        int respuestaNormal = JOptionPane.showConfirmDialog(null, "¿El pedido es normal?", "Tipo de Pedido", JOptionPane.YES_NO_OPTION);
        boolean normal = (respuestaNormal == JOptionPane.YES_OPTION);
        double valorTotal;

        if (normal) {

            valorTotal = nuevoPedido.calcularValorTotalPagar();
            JOptionPane.showMessageDialog(null, "El valor total del pedido normal es: " + valorTotal);
        } 
        else {
            int valorUrgencia = -1;
            while (valorUrgencia < 0) {
                try {
                    valorUrgencia = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el valor de urgencia:"));
                    if (valorUrgencia < 0) {
                        JOptionPane.showMessageDialog(null, "Valor de urgencia no válido. Por favor, ingrese un número positivo.");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Valor de urgencia no válido. Por favor, ingrese un número.");
                }
            }
            valorTotal = nuevoPedido.calcularValorTotalPagar(valorUrgencia);
            JOptionPane.showMessageDialog(null, "El valor total del pedido urgente es: " + valorTotal);
        }
        JOptionPane.showMessageDialog(null, "Pedido creado y guardado con éxito.");
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Error al ingresar datos numéricos: " + e.getMessage());
    } catch (HeadlessException e) {
        JOptionPane.showMessageDialog(null, "Error al crear el pedido: " + e.getMessage());
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
            .append("El pedido está normal: ").append(objPedido.isNormal()).append("\n")
            .append("Estado: ").append(objPedido.getEstado()).append("\n")
            .append("Observaciones: ").append(objPedido.getObservacion()).append("\n")
            .append("Cantidad de items del pedido: ").append(objPedido.calcularCantidadItemsPedidos()).append("\n")
            .append("Valor total de los items pedidos: ").append(String.format("%.2f", objPedido.calcularValorTotalItems())).append("\n");

        if (objPedido.isNormal()) {
            resultadoBuilder.append("Valor total a pagar (pedido normal): ").append(String.format("%.2f", objPedido.calcularValorTotalPagar())).append("\n");
        } else {
            resultadoBuilder.append("Valor total a pagar (pedido urgente): ").append(String.format("%.2f", objPedido.calcularValorTotalPagar(0))).append("\n");
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
        int confirm = JOptionPane.showConfirmDialog(null, "¿Está seguro de que desea eliminar todos los pedidos?",
                                                    "Eliminar Todos los Pedidos", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            losPedidos.clear();
            JOptionPane.showMessageDialog(null, "Todos los pedidos han sido eliminados.");
        } else {
            JOptionPane.showMessageDialog(null, "Operación cancelada.");
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

    try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("ARCHIVO_PEDIDOS.txt")))) {
        for (Pedido p : losPedidos) {
            out.println(p.getNumero() + "--" + p.getFechaHora().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")) +
                    "--" + p.getSuCliente().getNombre() +
                    "--" + p.getSuCliente().getIdentificacion() +
                    "--" + p.getSuCliente().getDireccion() +
                    "--" + (p.isNormal() ? 'N' : 'E') +
                    "--" + p.getEstado() +
                    "--" + p.getObservacion() +
                    "--" + p.calcularCantidadItemsPedidos() +
                    "--" + p.calcularValorTotalItems());

            for (ItemProducto item : p.getSusItemsProductos()) {
                out.print("   -" + item.getNombre() + "--" + item.getCodigo() + "--" + item.getCantidad() + "--" + item.getPrecio());

                switch (item) {
                    case ItemProductoCanastaFamiliar canasta -> out.print("--" + canasta.getTipo());
                    case ItemProductoFarmacia farmacia -> out.print("--" + farmacia.getPresentacion());
                    case ItemProductoOtro otro -> out.print("--" + String.format("%.2f%%", otro.getPorcentajeIva() * 100));
                    default -> {
                    }
                }

                out.println();
            }
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
            String[] datos = linea.split("--");
            if (datos.length > 0) {
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
                        String nombreProducto = datosItem[0].substring(4);
                        int codigoProducto = Integer.parseInt(datosItem[1]);
                        double cantidadProducto = Double.parseDouble(datosItem[2]);
                        double precioProducto = Double.parseDouble(datosItem[3]);
                        String tipoProducto = datosItem[4].substring(5);

                        ItemProducto producto = null;
                        switch (tipoProducto.toLowerCase()) {
                            case "tipo:" -> {
                                String tipo = datosItem[5].substring(5);
                                producto = new ItemProductoCanastaFamiliar(codigoProducto, nombreProducto, cantidadProducto, precioProducto, tipo);
                            }
                            case "presentación:" -> {
                                String presentacion = datosItem[5].substring(14);
                                producto = new ItemProductoFarmacia(codigoProducto, nombreProducto, cantidadProducto, precioProducto, presentacion);
                            }
                            case "iva:" -> {
                                double porcentajeIva = Double.parseDouble(datosItem[5].substring(4, datosItem[5].length() - 1)) / 100;
                                producto = new ItemProductoOtro(codigoProducto, nombreProducto, cantidadProducto, precioProducto, porcentajeIva);
                            }
                        }

                        if (producto != null) {
                            productos.add(producto);
                        }
                    }
                }

                Pedido pedido = new Pedido(numeroPedido, fechaHora, cliente, productos, observacion, estado, esNormal);
                losPedidos.add(pedido);
            }
        }

        JOptionPane.showMessageDialog(null, "Pedidos recuperados desde el archivo de texto con éxito.");
    } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al recuperar pedidos desde el archivo de texto: " + e.getMessage());
    } catch (NumberFormatException | DateTimeParseException e) {
        JOptionPane.showMessageDialog(null, "Error al parsear los datos del archivo de texto: " + e.getMessage());
    }
}

}
