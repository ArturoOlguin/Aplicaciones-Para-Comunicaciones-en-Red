import java.net.*;
import java.io.*;
import java.util.*;

public class SHolaD {
    // Tamaño máximo de cada fragmento de mensaje (10 bytes para ver la fragmentacion del mensaje)
    private static final int FRAGMENT_SIZE = 10;
    
    // Mapa para mantener el estado de reconstrucción de mensajes por cada cliente
    // Clave: Dirección del cliente (IP + puerto)
    // Valor: StringBuilder que acumula los fragmentos recibidos
    private static Map<InetSocketAddress, StringBuilder> messageBuilders = new HashMap<>();

    public static void main(String[] args) {
        try {
            // Crear un socket UDP en el puerto 2000
            DatagramSocket s = new DatagramSocket(2000);
            System.out.println("Servidor iniciado en el puerto 2000, esperando clientes...");

            // Bucle infinito para atender clientes igual podria ser un for sin parametros como en los ejemplos anteriores
            while(true) {
                // Buffer para recibir datos (tamaño igual al FRAGMENT_SIZE)
                byte[] buffer = new byte[FRAGMENT_SIZE];
                
                // Preparar paquete para recibir datos
                DatagramPacket p = new DatagramPacket(buffer, buffer.length);
                
                // Esperar y recibir un paquete (esta llamada bloquea hasta recibir datos)
                s.receive(p);
                
                // Crear objeto que identifica al cliente (dirección IP + puerto)
                InetSocketAddress clientAddress = new InetSocketAddress(p.getAddress(), p.getPort());
                
                // Obtener el StringBuilder para este cliente, o crear uno nuevo si es la primera vez
                StringBuilder messageBuilder = messageBuilders.get(clientAddress);
                if (messageBuilder == null) {
                    messageBuilder = new StringBuilder();
                    messageBuilders.put(clientAddress, messageBuilder);
                    System.out.println("\nNuevo cliente conectado: " + clientAddress);
                }
                
                // Convertir los bytes recibidos a String (solo los bytes realmente recibidos)
                String fragment = new String(p.getData(), 0, p.getLength());
                
                // Agregar el fragmento al mensaje en construcción
                messageBuilder.append(fragment);
                
                // Mostrar información de depuración
                System.out.println("\n--- Fragmento Recibido ---");
                System.out.println("Cliente: " + clientAddress);
                System.out.println("Contenido: \"" + fragment + "\"");
                System.out.println("Longitud: " + fragment.length() + " bytes");
                System.out.println("Mensaje acumulado: \"" + messageBuilder.toString() + "\"");
                System.out.println("Tamaño acumulado: " + messageBuilder.length() + " caracteres");

                // Detectar si es el último fragmento (cuando llega menos de FRAGMENT_SIZE bytes)
                if (p.getLength() < FRAGMENT_SIZE) {
                    String completeMessage = messageBuilder.toString();
                    System.out.println("\n>>> Mensaje completo recibido <<<");
                    System.out.println("De: " + clientAddress);
                    System.out.println("Contenido: \"" + completeMessage + "\"");
                    System.out.println("Longitud total: " + completeMessage.length() + " caracteres");

                    // Preparar respuesta (añade "Eco: " al mensaje original)
                    String respuesta = "Eco: " + completeMessage;
                    
                    // Enviar la respuesta fragmentada
                    System.out.println("\nEnviando respuesta fragmentada...");
                    sendFragmentedMessage(s, clientAddress, respuesta.getBytes());

                    // Eliminar el builder de este cliente ya que el mensaje está completo
                    messageBuilders.remove(clientAddress);
                    System.out.println("Respuesta enviada. Esperando nuevo mensaje...");
                }
            }
        } catch (Exception e) {
            // Manejar cualquier error que pueda ocurrir
            System.err.println("Error en el servidor:");
            e.printStackTrace();
        }
    }
    /*Función para enviar los mensajes fragmentados */
    private static void sendFragmentedMessage(DatagramSocket socket, InetSocketAddress address, byte[] message) 
            throws IOException {
        int offset = 0; // Posición actual en el array de bytes
        
        // Dividir el mensaje en fragmentos
        while (offset < message.length) {
            // Calcular tamaño de este fragmento (el menor entre FRAGMENT_SIZE o lo que quede)
            int length = Math.min(FRAGMENT_SIZE, message.length - offset);
            
            // Copiar la porción correspondiente del mensaje
            byte[] fragment = Arrays.copyOfRange(message, offset, offset + length);
            
            // Crear paquete UDP con este fragmento
            DatagramPacket p = new DatagramPacket(
                fragment, 
                fragment.length, 
                address.getAddress(), 
                address.getPort()
            );
            
            // Enviar el paquete
            socket.send(p);
            
            // Mostrar información de depuración
            String fragmentStr = new String(fragment);
            System.out.println("Enviado fragmento [" + (offset/length + 1) + "]: \"" + fragmentStr + "\" (" + length + " bytes)");
            
            // Avanzar la posición de offset
            offset += length;
        }
    }
}