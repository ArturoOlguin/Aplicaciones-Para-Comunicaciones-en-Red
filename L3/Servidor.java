import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
    public static void main(String[] args) {
        
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            System.out.println("Servidor esperando conexiones en el puerto "+"...");
            
            while (true) {
                try (
                    /*Recibimos la conexión */
                     Socket socket = serverSocket.accept();
                     InputStream inputStream = socket.getInputStream();
                     /*Flujo de deserialización */
                     ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
                    
                    System.out.println("Cliente conectado desde: " + socket.getInetAddress());
                    
                    /*Deserializar el objeto*/
                    Persona persona = (Persona) objectInputStream.readObject();
        
                    /*Procesar el objeto */
                    System.out.println("\nObjeto recibido y deserializado:");
                    System.out.println("Nombre: " + persona.getNombre());
                    System.out.println("Edad: " + persona.getEdad());
                    System.out.println("Altura: " + persona.getAltura());
                    System.out.println("Peso: " + persona.getPeso());
                    System.out.println("Es estudiante: " + persona.isEsEstudiante());
                    System.out.println("Dirección: " + persona.getDireccion());
                    

                    /*Cierre */
                    socket.close();
                } catch (ClassNotFoundException e) {
                    System.err.println("Error al deserializar el objeto: " + e.getMessage());
                } catch (IOException e) {
                    System.err.println("Error de E/S: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo iniciar el servidor: " + e.getMessage());
        }
    }
}