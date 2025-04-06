import java.net.*;
import java.io.*;

package Original;

public class Servidor {

    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(7000);
            
            for(;;){
                Socket cl = s.accept();
                System.out.println("Conexión establecida desde: "+cl.getInputStream()+":"+cl.getPort());
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                byte[] b = new byte[1024];
                String nombre = dis.readUTF();
                System.out.println("Recibimos el archivo: "+nombre);
                long tam = dis.readLong();
                DataOutputStream dos = new DataOutputStream(new FileOutputStream(nombre));

                long recibidos = 0;
                int n, porcentaje;

                while (recibidos<tam) {
                    n = dis.read(b);
                    dos.write(b,0,n);
                    dos.flush();
                    recibidos = recibidos +n;
                    porcentaje = (int)(recibidos*100/tam);
                    System.out.print("\n\nArchivo recibido...");

                }

                dos.close();
                dis.close();
                cl.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}