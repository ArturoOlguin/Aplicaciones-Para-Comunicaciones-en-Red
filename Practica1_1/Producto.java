import java.io.Serializable;

// Clase que representa un producto en el sistema de comercio electrónico
public class Producto implements Serializable {
    // Atributos de la clase
    private int id;                 // Identificador único del producto
    private String nombre;          // Nombre descriptivo del producto
    private String caracteristicas; // Descripción detallada del producto
    private double precio;          // Precio unitario del producto
    private int stock;              // Cantidad disponible en inventario
    private String imagen;          // Nombre del archivo de imagen asociado

    // Constructor para inicializar un producto con todos sus atributos
    public Producto(int id, String nombre, String caracteristicas, double precio, int stock, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.caracteristicas = caracteristicas;
        this.precio = precio;
        this.stock = stock;
        this.imagen = imagen;
    }

    // Métodos getters para acceder a los atributos privados
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCaracteristicas() { return caracteristicas; }
    public double getPrecio() { return precio; }
    public int getStock() { return stock; }
    public String getImagen() { return imagen; }

    // Setter para modificar el stock (único atributo modificable)
    public void setStock(int stock) { this.stock = stock; }

    // Representación textual del producto para mostrarlo en listados
    @Override
    public String toString() {
        return "ID: " + id + ", Nombre: " + nombre + ", Precio: $" + precio + ", Stock: " + stock;
    }
}