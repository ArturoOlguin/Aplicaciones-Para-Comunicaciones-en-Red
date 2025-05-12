import java.net.*;
import java.io.*;
import java.util.*;

public class CHolaD {
    // Tamaño máximo de cada fragmento (debe coincidir con el del servidor)
    private static final int FRAGMENT_SIZE = 10;
    
    // Para acumular los fragmentos recibidos del servidor
    private static StringBuilder messageBuilder = new StringBuilder();

    public static void main(String[] args) {
        try {
            // Crear socket UDP (el sistema asignará un puerto disponible)
            DatagramSocket cl = new DatagramSocket();
            System.out.println("Cliente iniciado en el puerto " + cl.getLocalPort());
            System.out.print("Escriba un mensaje para enviar al servidor: ");
            
            // Leer mensaje del usuario desde la consola
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String mensaje = br.readLine();
            
            // Convertir el mensaje a bytes para enviarlo
            byte[] b = mensaje.getBytes();
            
            // Enviar el mensaje al servidor (se fragmentará si es necesario)
            System.out.println("\nEnviando mensaje al servidor...");
            sendFragmentedMessage(cl, "127.0.0.1", 2000, b);
            System.out.println("Mensaje enviado. Esperando respuesta...");
            
            // Recibir la respuesta fragmentada del servidor
            while(true) {
                // Preparar buffer para recibir datos
                byte[] buffer = new byte[FRAGMENT_SIZE];
                DatagramPacket p = new DatagramPacket(buffer, buffer.length);
                
                // Establecer timeout de 3 segundos para evitar bloqueo infinito
                cl.setSoTimeout(3000);
                
                try {
                    // Esperar y recibir un paquete
                    cl.receive(p);
                    
                    // Convertir los bytes recibidos a String
                    String fragment = new String(p.getData(), 0, p.getLength());
                    
                    // Acumular el fragmento recibido
                    messageBuilder.append(fragment);
                    
                    // Mostrar información de depuración
                    System.out.println("\n--- Fragmento Recibido ---");
                    System.out.println("De: " + p.getAddress() + ":" + p.getPort());
                    System.out.println("Contenido: \"" + fragment + "\"");
                    System.out.println("Longitud: " + fragment.length() + " bytes");
                    System.out.println("Respuesta acumulada: \"" + messageBuilder.toString() + "\"");
                    System.out.println("Tamaño acumulado: " + messageBuilder.length() + " caracteres");
                    
                    // Detectar si es el último fragmento (cuando llega menos de FRAGMENT_SIZE bytes)
                    if (p.getLength() < FRAGMENT_SIZE) {
                        System.out.println("\n>>> RESPUESTA COMPLETA <<<");
                        System.out.println("Contenido: \"" + messageBuilder.toString() + "\"");
                        System.out.println("Longitud total: " + messageBuilder.length() + " caracteres");
                        
                        // Limpiar el builder para futuras comunicaciones
                        messageBuilder.setLength(0);
                        break;
                    }
                } catch (SocketTimeoutException e) {
                    // Si no llegan más fragmentos en el tiempo esperado
                    System.out.println("\nNo se recibieron más fragmentos. Mensaje final recibido:");
                    System.out.println("\"" + messageBuilder.toString() + "\"");
                    break;
                }
            }
            
            // Cerrar el socket al terminar
            cl.close();
            System.out.println("\nConexión cerrada. Cliente terminado.");
        } catch (Exception e) {
            // Manejar cualquier error que pueda ocurrir
            System.err.println("Error en el cliente:");
            e.printStackTrace();
        }
    }

    private static void sendFragmentedMessage(DatagramSocket socket, String host, int port, byte[] message) 
            throws IOException {
        int offset = 0; // Posición actual en el array de bytes
        int fragmentCount = 0; // Contador de fragmentos
        
        // Dividir el mensaje en fragmentos y enviarlos
        while (offset < message.length) {
            fragmentCount++;
            
            // Calcular tamaño de este fragmento
            int length = Math.min(FRAGMENT_SIZE, message.length - offset);
            
            // Copiar la porción correspondiente del mensaje
            byte[] fragment = Arrays.copyOfRange(message, offset, offset + length);
            
            // Crear paquete UDP con este fragmento
            DatagramPacket p = new DatagramPacket(
                fragment, 
                fragment.length, 
                InetAddress.getByName(host), 
                port
            );
            
            // Enviar el paquete
            socket.send(p);
            
            // Mostrar información de depuración
            String fragmentStr = new String(fragment);
            System.out.println("Fragmento [" + fragmentCount + "] enviado: \"" + fragmentStr + "\" (" + length + " bytes)");
            
            // Avanzar la posición de offset
            offset += length;
        }
        
        System.out.println("Total fragmentos enviados: " + fragmentCount);
    }
}