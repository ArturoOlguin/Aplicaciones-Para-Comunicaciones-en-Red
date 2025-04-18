import javax.swing.JFileChooser;
import java.net.*;
import java.io.*;

public class ClienteArchivo {

    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.printf("Escriba la dirección del servidor");
            String host = br.readLine();
            System.out.printf("\n\nEscriba el puerto:");
            int pto = Integer.parseInt(br.readLine());

            /*Definicion del socket */
            Socket cl = new Socket(host, pto);
            /*Elección del archivo */
            JFileChooser jf = new JFileChooser();
            int r = jf.showOpenDialog(null);

            /*Obtención de datos */
            if (r==JFileChooser.APPROVE_OPTION) {
                File f = jf.getSelectedFile();
                String archivo = f.getAbsolutePath();
                String nombre = f.getName();
                long tam = f.length();

                
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                DataInputStream dis = new DataInputStream(new FileInputStream(archivo));

                dos.writeUTF(nombre);
                dos.flush();
                dos.writeLong(tam);
                dos.flush();

                byte[] b = new byte[1024];
                long enviados = 0;
                int porcentaje , n;

                while (enviados<tam) {
                    n = dis.read(b);
                    dos.write(b, 0, n);
                    dos.flush();
                    enviados = enviados+n;
                    porcentaje = (int)(enviados*100/tam);
                    System.out.printf("Enviado: "+porcentaje+"%\r");
                }

                System.out.printf("\n\nArchivo enviado");
                dos.close();
                dis.close();
                cl.close();
            }     
        } catch (Exception e) {
            e.printStackTrace();
        }
    }    
}