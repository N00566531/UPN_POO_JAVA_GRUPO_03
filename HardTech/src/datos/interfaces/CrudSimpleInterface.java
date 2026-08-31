
package datos.interfaces;

import java.util.List;

/*
La interfaace establece un esquema para clases que realizarán operaciones CRUD
*/

public interface CrudSimpleInterface<T> {//Cada vez que se implemente se le envía un objeto
   
   /*
   Devuelve una lista de objetos, filtrada según el parámetro texto
   para obtener registros de la base de datos que coincidan con el criterio de búsqueda
   */
   public List<T> listar(String texto);
   
   //Devuelve un booleano que indica si la inserción fue exitosa o no
   public boolean insertar(T obj);
   
   //Devolverá 1 si actualiza y 0 si no lo hace, al igual que insertar
   public boolean actualizar(T obj);
   
   //Para activar o desactivar (eliminación lógica)
   public boolean desactivar(int id);  
   public boolean activar(int id);
   
   //Devuelve el total de registros de la tabla
   public int total(); 
   
   //Determina si existe un registro en la tabla según el texto recibido
   public boolean existe(String texto);

}