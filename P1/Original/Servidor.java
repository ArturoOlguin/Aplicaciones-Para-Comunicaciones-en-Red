import java.net.*;
import java.io.*;

public class Servidor {

    public static void main(String[] args) {
        try {
            ServerSocket s= new ServerSocket(1234);
            System.out.println("Esperando cliente.....");

            for(;;){
                Socket cl = s.accept();
                System.out.println("Conexión establecida desde: "+cl.getInetAddress()+" : "+cl.getPort());
                String mensaje = "Hola mundo";
                PrintWriter pw = new PrintWriter(new OutputStreamWriter(cl.getOutputStream()));
                pw.println(mensaje);
                pw.flush();
                pw.close();
                cl.close();
                s.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}