import java.io.*;
import java.net.Socket;

public class Cliente {
    public static void main(String[] args) {
        final String SERVIDOR_IP = "localhost";
        final int PUERTO = 1234;
                
        /* Hacer una instancia de la clase (objeto)*/
        Persona persona = new Persona("Juan Pérez", 30, 1.75, 70.5f, true, "Calle Falsa 123");
        
        // Imprime los valores de los atributos en consola
        System.out.println("Objeto original:");
        System.out.println(persona.toString());
        
        // Serializa el objeto, a excepción de 3 atributos (usamos transient en Persona.java)
        
        try (Socket socket = new Socket(SERVIDOR_IP, PUERTO);
             OutputStream outputStream = socket.getOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)) {
            
            //Imprimir en consola los valores de los atributos del objeto ya serializado
            System.out.println("\nObjeto con atributos transient (antes de serializar):");
            System.out.println("Nombre: " + persona.getNombre());
            System.out.println("Edad: " + persona.getEdad());
            System.out.println("Altura: " + persona.getAltura());
            System.out.println("Peso: " + persona.getPeso());
            System.out.println("Es estudiante: " + persona.isEsEstudiante());
            System.out.println("Dirección: " + persona.getDireccion());
            
            //Guarda el objeto serializado en un archivo de texto plano
            try (FileOutputStream fileOut = new FileOutputStream("persona.txt");
                 ObjectOutputStream fileObjectOut = new ObjectOutputStream(fileOut)) {
                fileObjectOut.writeObject(persona);
                System.out.println("\nObjeto serializado guardado en persona.txt");
            } catch (IOException e) {
                System.err.println("Error al guardar el archivo: " + e.getMessage());
            }
            
            // Envía el objeto serializado por medio de sockets a la entidad SERVIDOR
            objectOutputStream.writeObject(persona);
            System.out.println("\nObjeto enviado al servidor.");
            
        } catch (IOException e) {
            System.err.println("Error en el cliente: " + e.getMessage());
        }
    }
}