
package entidades;

import java.util.Objects;

//Esta clase representa a la tabla categoria
public class Categoria {
    
    //variables protegidas que representan a sus campos
    private int id;
    private String nombre;
    private String descripcion;
    private boolean activo;
    
    //Constructores
    
    //Vacio
    public Categoria() {
        
    }
    
    
    //Constructor para el combobox
    public Categoria(int id, String nombre) {
        /*
        this.id y this.nombre se refieren a los atributos de la clase
        mientras que id y nombre son los parámetros del constructor
        */  
        this.id = id;
        this.nombre = nombre;
    }
    
    //Constructor que recibe un parametro por cada var; almacenará los valores de cada parametro
    public Categoria(int id, String nombre, String descripcion, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }
    
    //como son var privadas, para acceder a ellas se necesita de los metodos 
    //Setter (para agregar valores a las variables) y Getter (para obtener los valores almacenados)

    public int getId() { //obtengo valor
        return id; //retorno id
    }

    public void setId(int id) { //para almacenar valor que se envía como parametro
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    //indica que está sobrescribiendo un método de la clase padre (object)
    //Es para proporcionar una representación en forma de texto de un objeto
    @Override
    public String toString() {
        //devolverá el valor de la variable de la instancia de la clase
        return nombre;
    }
    
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.id;
        hash = 67 * hash + Objects.hashCode(this.nombre);
        hash = 67 * hash + Objects.hashCode(this.descripcion);
        hash = 67 * hash + (this.activo ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Categoria other = (Categoria) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.activo != other.activo) {
            return false;
        }
        if (!Objects.equals(this.nombre, other.nombre)) {
            return false;
        }
        if (!Objects.equals(this.descripcion, other.descripcion)) {
            return false;
        }
        return true;
    }
    
}
