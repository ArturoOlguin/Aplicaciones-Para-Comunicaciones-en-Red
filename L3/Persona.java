import java.io.Serializable;

public class Persona implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nombre;
    private int edad;
    private transient double altura; // Atributo no serializado
    private float peso;
    private transient boolean esEstudiante; // Atributo no serializado
    private transient String direccion; // Atributo no serializado
    
    public Persona(String nombre, int edad, double altura, float peso, boolean esEstudiante, String direccion) {
        this.nombre = nombre;
        this.edad = edad;
        this.altura = altura;
        this.peso = peso;
        this.esEstudiante = esEstudiante;
        this.direccion = direccion;
    }
    
    // Métodos get y set (requeridos pero no usados en este ejercicio, unicamente para compilación)
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    
    public double getAltura() { return altura; }
    public void setAltura(double altura) { this.altura = altura; }
    
    public float getPeso() { return peso; }
    public void setPeso(float peso) { this.peso = peso; }
    
    public boolean isEsEstudiante() { return esEstudiante; }
    public void setEsEstudiante(boolean esEstudiante) { this.esEstudiante = esEstudiante; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    @Override
    public String toString() {
        return "Persona{" +
                "nombre='" + nombre + '\'' +
                ", edad=" + edad +
                ", altura=" + altura +
                ", peso=" + peso +
                ", esEstudiante=" + esEstudiante +
                ", direccion='" + direccion + '\'' +
                '}';
    }
}