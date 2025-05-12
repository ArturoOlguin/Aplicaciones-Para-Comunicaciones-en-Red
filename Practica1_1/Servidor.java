import java.io.*;
import java.net.*;
import java.util.*;

public class Servidor {
    // Constantes de configuración
    private static final int PUERTO = 5000;               // Puerto de escucha
    private static final String ARCHIVO_CATALOGO = "catalogo.dat"; // Archivo de persistencia
    private static List<Producto> catalogo;               // Catálogo de productos en memoria

    public static void main(String[] args) throws Exception {
        cargarCatalogo(); // Carga el catálogo al iniciar el servidor
        
        // Crea el socket del servidor
        ServerSocket servidor = new ServerSocket(PUERTO);
        System.out.println("Servidor escuchando en el puerto " + PUERTO);

        // Bucle infinito para aceptar clientes
        while (true) {
            Socket cliente = servidor.accept(); // Espera una conexión
            System.out.println("Cliente conectado.");
            manejarCliente(cliente); // Atiende al cliente
        }
    }

    // Método para manejar la comunicación con un cliente
    private static void manejarCliente(Socket socket) throws Exception {
        // Crea streams de entrada/salida
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

        // Envía el catálogo completo al cliente
        oos.writeObject(catalogo);
        enviarImagenes(socket); // Envía todas las imágenes

        // Recibe el carrito de compras del cliente
        Map<Integer, Integer> carrito = (Map<Integer, Integer>) ois.readObject();

        // Actualiza el stock según la compra recibida
        for (Map.Entry<Integer, Integer> entry : carrito.entrySet()) {
            for (Producto p : catalogo) {
                if (p.getId() == entry.getKey()) {
                    p.setStock(p.getStock() - entry.getValue()); // Reduce el stock
                }
            }
        }

        guardarCatalogo(); // Guarda los cambios en disco

        // Envía el catálogo actualizado de vuelta al cliente
        oos.writeObject(catalogo);

        socket.close(); // Cierra la conexión
        System.out.println("Cliente desconectado.");
    }

    // Método para enviar todas las imágenes al cliente
    private static void enviarImagenes(Socket socket) throws IOException {
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        
        // Envía cada imagen del catálogo
        for (Producto p : catalogo) {
            File img = new File("img/" + p.getImagen()); // Ruta de la imagen
            dos.writeUTF(p.getImagen()); // Envía el nombre del archivo
            dos.writeLong(img.length()); // Envía el tamaño

            // Transfiere el contenido del archivo
            FileInputStream fis = new FileInputStream(img);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, count);
            }
            fis.close();
        }
        dos.writeUTF("FIN"); // Marca el final del envío
    }

    // Carga el catálogo desde archivo o crea uno inicial
    private static void cargarCatalogo() throws Exception {
        File archivo = new File(ARCHIVO_CATALOGO);
        if (!archivo.exists()) { // Si no existe el archivo
            catalogo = new ArrayList<>();
            // Crea productos de ejemplo
            catalogo.add(new Producto(1, "Mouse", "Mouse óptico USB", 100.0, 10, "mouse.jpg"));
            catalogo.add(new Producto(2, "Teclado", "Teclado mecánico", 350.0, 5, "teclado.jpg"));
            guardarCatalogo(); // Guarda el catálogo inicial
        } else {
            // Lee el catálogo desde el archivo
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo));
            catalogo = (List<Producto>) ois.readObject();
            ois.close();
        }
    }

    // Guarda el catálogo actual en disco
    private static void guardarCatalogo() throws Exception {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_CATALOGO));
        oos.writeObject(catalogo);
        oos.close();
    }
}