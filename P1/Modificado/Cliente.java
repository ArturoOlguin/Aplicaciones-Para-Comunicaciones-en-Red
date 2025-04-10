/*Elaborado por: Olguín Martínez José Arturo
 * Grupo 6CM3
 * Aplicaciones para comunicaciones en red
 */

import java.net.*;
import java.io.*;

public class Cliente {
    public static void main(String[] args) {
        try {
            BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Escriba la dirección del servidor: ");
            String host = br1.readLine();
            System.out.println("\n\nEscriba el puerto: ");
            int pto = Integer.parseInt(br1.readLine());
            
            Socket cl = new Socket(host, pto);
            System.out.println("Conexión establecida con el servidor.");

            // Recibir mensaje del servidor
            BufferedReader br2 = new BufferedReader(new InputStreamReader(cl.getInputStream()));
            String mensaje = br2.readLine();
            System.out.println("Mensaje recibido del servidor: " + mensaje);

            // Enviar el mismo mensaje de vuelta al servidor (eco)
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(cl.getOutputStream()));
            pw.println(mensaje);
            pw.flush();
            System.out.println("Mensaje enviado de vuelta al servidor: " + mensaje);

            br1.close();
            br2.close();
            pw.close();
            cl.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
}