
package entidades;

import java.util.Objects;

public class Rol {
    
    //Variables de tabla rol
    private int id;
    private String nombre;
    private String descripcion;
    
    
    //Const vacio
    public Rol() {
    }
    
    
    //Const con patametros para las 3 variables
    public Rol(int id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    
    //Const con parametros solo para el id y nombre
    public Rol(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    
    //Met Set y Get
    public int getId() {
        return id;
    }

    public void setId(int id) {
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
    
    
    //Método toString para mostrar los roles en un combo al registrar un usuario 
    @Override
    public String toString() {
        return nombre; //que muestre solo nombre
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + this.id;
        hash = 17 * hash + Objects.hashCode(this.nombre);
        hash = 17 * hash + Objects.hashCode(this.descripcion);
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
        final Rol other = (Rol) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.nombre, other.nombre)) {
            return false;
        }
        return Objects.equals(this.descripcion, other.descripcion);
    }

    
}
