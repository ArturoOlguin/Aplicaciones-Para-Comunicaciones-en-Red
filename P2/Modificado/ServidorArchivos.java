import java.net.*;
import java.io.*;

public class ServidorArchivos {
    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(7000);
            for(;;) {
                Socket cl = s.accept();
                System.out.println("Conexión establecida desde " + cl.getInetAddress() + ":" + cl.getPort());
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                
                // Recibir cantidad de archivos
                int numArchivos = dis.readInt();
                System.out.println("Recibiendo " + numArchivos + " archivos...");
                
                for (int i = 0; i < numArchivos; i++) {
                    byte[] b = new byte[1024];
                    String nombre = dis.readUTF();
                    System.out.println("Recibiendo archivo #" + (i+1) + ": " + nombre);
                    long tam = dis.readLong();
                    
                    // Usar nombre único para evitar sobrescritura
                    String nombreArchivo = "recibido_" + (i+1) + "_" + nombre;
                    DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombreArchivo));
                    long recibidos = 0;
                    int n, porcentaje;

                    while (recibidos < tam) {
                        n = dis.read(b);
                        dos.write(b, 0, n);
                        dos.flush();
                        recibidos += n;
                        porcentaje = (int)(recibidos * 100 / tam);
                        System.out.print("Progreso archivo " + (i+1) + ": " + porcentaje + "%\r");
                    }

                    dos.close();
                    System.out.println("\nArchivo " + nombreArchivo + " recibido completamente.");
                }

                dis.close();
                cl.close();
                System.out.println("Todos los archivos recibidos. Esperando nueva conexión...\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}