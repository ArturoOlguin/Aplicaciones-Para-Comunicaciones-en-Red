import javax.swing.JFileChooser;
import java.net.*;
import java.io.*;

public class ClienteArchivos {

    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Escriba la dirección del servidor: ");
            String host = br.readLine();
            System.out.print("\nEscriba el puerto: ");
            int pto = Integer.parseInt(br.readLine());

            // Definición del socket
            Socket cl = new Socket(host, pto);
            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
            
            // Preguntar cuántos archivos se enviarán
            System.out.print("\n¿Cuántos archivos desea enviar? ");
            int numArchivos = Integer.parseInt(br.readLine());
            dos.writeInt(numArchivos);
            dos.flush();
            
            for (int i = 0; i < numArchivos; i++) {
                System.out.println("\nSeleccione el archivo #" + (i+1));
                
                // Elección del archivo
                JFileChooser jf = new JFileChooser();
                int r = jf.showOpenDialog(null);

                if (r == JFileChooser.APPROVE_OPTION) {
                    File f = jf.getSelectedFile();
                    String archivo = f.getAbsolutePath();
                    String nombre = f.getName();
                    long tam = f.length();

                    DataInputStream dis = new DataInputStream(new FileInputStream(archivo));

                    // Enviar metadatos del archivo
                    dos.writeUTF(nombre);
                    dos.flush();
                    dos.writeLong(tam);
                    dos.flush();

                    byte[] b = new byte[1024];
                    long enviados = 0;
                    int porcentaje, n;

                    while (enviados < tam) {
                        n = dis.read(b);
                        dos.write(b, 0, n);
                        dos.flush();
                        enviados += n;
                        porcentaje = (int)(enviados * 100 / tam);
                        System.out.print("Enviando archivo " + (i+1) + ": " + porcentaje + "%\r");
                    }

                    dis.close();
                    System.out.println("\nArchivo " + nombre + " enviado correctamente.");
                }
            }
            
            System.out.println("\nTodos los archivos han sido enviados.");
            dos.close();
            cl.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}