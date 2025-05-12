import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.*;

public class Cliente {
    // Configuración de conexión
    private static final String HOST = "localhost";
    private static final int PUERTO = 5000;

    public static void main(String[] args) throws Exception {
        // Establece conexión con el servidor
        Socket socket = new Socket(HOST, PUERTO);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

        // Recibe el catálogo del servidor
        @SuppressWarnings("unchecked")
        List<Producto> catalogo = (List<Producto>) ois.readObject();
        recibirImagenes(socket); // Descarga las imágenes

        // Crea un carrito de compras vacío
        Map<Integer, Integer> carrito = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        String opcion;

        // Bucle principal de interacción con el usuario
        do {
            // Muestra el catálogo
            System.out.println("\nCATÁLOGO:");
            for (Producto p : catalogo) System.out.println(p);

            // Muestra menú de opciones
            System.out.println("\nOpciones: [agregar] [eliminar] [ver] [comprar] [salir]");
            opcion = scanner.nextLine();

            // Procesa la opción seleccionada
            switch (opcion.toLowerCase()) {
                case "agregar":
                    System.out.print("ID del producto: ");
                    int id = Integer.parseInt(scanner.nextLine());
                    System.out.print("Cantidad: ");
                    int cantidad = Integer.parseInt(scanner.nextLine());
                    carrito.put(id, carrito.getOrDefault(id, 0) + cantidad);
                    break;
                case "eliminar":
                    System.out.print("ID a eliminar: ");
                    carrito.remove(Integer.parseInt(scanner.nextLine()));
                    break;
                case "ver":
                    System.out.println("CARRITO:");
                    for (Map.Entry<Integer, Integer> entry : carrito.entrySet()) {
                        Producto p = catalogo.stream()
                            .filter(prod -> prod.getId() == entry.getKey())
                            .findFirst().orElse(null);
                        if (p != null) {
                            System.out.println(p.getNombre() + " x" + entry.getValue() + 
                                " = $" + (p.getPrecio() * entry.getValue()));
                        }
                    }
                    break;
                case "comprar":
                    oos.writeObject(carrito); // Envía el carrito al servidor
                    @SuppressWarnings("unchecked")
                    List<Producto> catalogoActualizado = (List<Producto>) ois.readObject();
                    generarTicketPDF(carrito, catalogo); // Genera el ticket
                    System.out.println("¡Compra realizada!");
                    break;
            }
        } while (!opcion.equalsIgnoreCase("salir"));

        socket.close(); // Cierra la conexión
    }

    // Descarga las imágenes del servidor
    private static void recibirImagenes(Socket socket) throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        // Crea directorio para imágenes si no existe
        File dir = new File("cliente_img");
        if (!dir.exists()) dir.mkdirs();

        while (true) {
            String nombre = dis.readUTF(); // Lee nombre del archivo
            if ("FIN".equals(nombre)) break; // Finaliza al recibir marca
            long size = dis.readLong(); // Lee tamaño del archivo

            // Escribe el archivo en disco
            FileOutputStream fos = new FileOutputStream("cliente_img/" + nombre);
            byte[] buffer = new byte[1024];
            int count;
            long recibidos = 0;
            while (recibidos < size) {
                count = dis.read(buffer, 0, (int)Math.min(buffer.length, size - recibidos));
                fos.write(buffer, 0, count);
                recibidos += count;
            }
            fos.close();
        }
    }

    // Genera un PDF con el ticket de compra
    public static void generarTicketPDF(Map<Integer, Integer> carrito, List<Producto> catalogo) throws Exception {
        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream("ticket.pdf"));
        doc.open();
        doc.add(new Paragraph("Ticket de compra:\n\n"));

        double total = 0;
        // Agrega cada producto al PDF
        for (Map.Entry<Integer, Integer> entry : carrito.entrySet()) {
            Producto p = catalogo.stream()
                .filter(prod -> prod.getId() == entry.getKey())
                .findFirst().orElse(null);
            if (p != null) {
                double subtotal = p.getPrecio() * entry.getValue();
                total += subtotal;
                doc.add(new Paragraph(p.getNombre() + " x" + entry.getValue() + " = $" + subtotal));
            }
        }

        // Agrega el total al PDF
        doc.add(new Paragraph("\nTotal: $" + total));
        doc.close();
    }
}