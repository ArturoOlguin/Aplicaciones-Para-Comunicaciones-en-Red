import java.net.*;
import java.io.*;

public class Servidor {
    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(1234);
            System.out.println("Servidor iniciado, esperando cliente.....");

            for(;;) {
                Socket cl = s.accept();
                System.out.println("Conexión establecida desde: " + cl.getInetAddress() + ":" + cl.getPort());
                
                // Enviar mensaje al cliente
                String mensaje = "Hola mundo";
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(cl.getOutputStream()));
                pw.println(mensaje);
                pw.flush();
                System.out.println("Mensaje enviado al cliente: " + mensaje);

                // Recibir respuesta del cliente
                BufferedReader br = new BufferedReader(new InputStreamReader(cl.getInputStream()));
                String respuesta = br.readLine();
                System.out.println("Respuesta del cliente: " + respuesta);

                pw.close();
                br.close();
                cl.close();
                s.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}