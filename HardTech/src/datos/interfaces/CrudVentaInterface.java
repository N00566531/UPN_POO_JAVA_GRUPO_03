
package datos.Interfaces;

import java.util.List;

//Cuando se implemente la Interface se envia dos parametros; t y d
public interface CrudVentaInterface<T,D> {
   public List<T> listar(String texto,int totalPorPagina,int numPagina);
   //Met para listar los detalles de ingreso; devuelve obj de tipo List
  
//Espera par int id; se basa en DetalleIngreso (D)
   public List<D> listarDetalle(int id);
   public boolean insertar(T obj);
   public boolean anular(int id);
   public int total();
   public boolean existe(String texto1, String texto2); //busqueda por dos valores, num y serie
}
