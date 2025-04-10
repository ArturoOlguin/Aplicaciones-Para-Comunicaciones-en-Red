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
            
            // Configurar JFileChooser para selección múltiple
            JFileChooser jf = new JFileChooser();
            jf.setMultiSelectionEnabled(true); // Habilitar selección múltiple
            int r = jf.showOpenDialog(null);

            if (r == JFileChooser.APPROVE_OPTION) {
                File[] files = jf.getSelectedFiles(); // Obtener array de archivos seleccionados
                
                // Enviar cantidad de archivos primero
                dos.writeInt(files.length);
                dos.flush();
                
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
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
                        System.out.print("Enviando archivo " + (i+1) + "/" + files.length + 
                                       " (" + nombre + "): " + porcentaje + "%\r");
                    }

                    dis.close();
                    System.out.println("\nArchivo " + nombre + " enviado correctamente.");
                }
                
                System.out.println("\nTodos los archivos (" + files.length + ") han sido enviados.");
            }
            
            dos.close();
            cl.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}