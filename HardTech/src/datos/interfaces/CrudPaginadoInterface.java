
package datos.interfaces;

import java.util.List;


public interface CrudPaginadoInterface<T> {
   /*Listar objetos de tipo Artículo según el filtro, artículos por página y num de pag que se visualiza
   */
    
   public List<T> listar(String texto,int totalPorPagina,int numPagina);
   public boolean insertar(T obj);
   public boolean actualizar(T obj);
   public boolean desactivar(int id);
   public boolean activar(int id);
   public int total();
   // Verificar si existe artículo que coincida con el texto especificado
   public boolean existe(String texto);
}
