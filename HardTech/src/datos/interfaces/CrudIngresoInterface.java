

package datos.Interfaces;

import java.util.List;

//Cuando se implementa Interface se envia dos parametros; t y d
public interface CrudIngresoInterface<T,D> {
   //Met para listar ingresos (T)
   public List<T> listar(String texto,int totalPorPagina,int numPagina);
   
   /*Met listar detalles, espera id ingreso; se basa en DetalleIngreso (D)*/
   public List<D> listarDetalle(int id);
   public boolean insertar(T obj);
   public boolean anular(int id);
   public int total();
   public boolean existe(String texto1, String texto2); //busqueda por dos valores, num y serie
}
