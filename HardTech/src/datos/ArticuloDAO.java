
package datos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.Conexion;
import datos.interfaces.CrudPaginadoInterface; 
import entidades.Articulo; 
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/*
Implementa esta interface obligatoriamente y le enviamos 
como parametro la entidad Articulo
*/
public class ArticuloDAO implements CrudPaginadoInterface<Articulo> { 
    private final Conexion CON;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;
    
    public ArticuloDAO(){
        //Obtener conexión
        CON=Conexion.getInstancia();
    }
    
    //Implementamos los metodos abstractos de interface
    @Override
    
    //Método devuelve una lista de artículos, objetos por página y el num de pag
    public List<Articulo> listar(String texto,int totalPorPagina,int numPagina) {
        /*
        inicializa lista llamada registros que almacena objetos 
        de tipo Artículo; instancia a la clase ArrayList
         */

        List<Articulo> registros=new ArrayList();
        try {
            //Conexión y envío de la consulta
            ps=CON.conectar().prepareStatement("SELECT a.id,a.categoria_id, c.nombre as categoria_nombre, a.codigo, a.nombre, a.precio_venta, a.stock, a.descripcion, a.imagen, a.activo FROM articulo a inner join categoria c ON a.categoria_id=c.id WHERE a.nombre LIKE ? ORDER BY a.id ASC LIMIT ?,?");
            
            //PreparedStatement envia 3 parametros
            ps.setString(1,"%" + texto +"%"); // Filtro de búsqueda por nombre        
            ps.setInt(2, (numPagina-1)*totalPorPagina); //Calcula registro inicial de página actual
            ps.setInt(3, totalPorPagina);// Cantidad máxima de registros que se mostrarán en la página
            
            //Ejecutar y devolver el resultado
            rs=ps.executeQuery();
            
            while(rs.next()){ //recorro cada fila del rs
                /*
                Para cada fila se crea un nuevo Articulo con los valores 
                de rs y se agrega a la lista registros
                */
                registros.add(new Articulo(rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getDouble(6),rs.getInt(7),rs.getString(8),rs.getString(9),rs.getBoolean(10)));
            }
            ps.close();
            rs.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            
        } finally{
            ps=null;
            rs=null;
            CON.desconectar();
        }
        //devuelve la lista registros con objetos Articulo
        return registros;
    }
    
    //Método para listar articulos con stock disponible
    public List<Articulo> listarArticuloVenta(String texto,int totalPorPagina,int numPagina) {
        List<Articulo> registros=new ArrayList();
        try {
            ps=CON.conectar().prepareStatement("SELECT a.id,a.categoria_id, c.nombre as categoria_nombre, a.codigo, a.nombre, a.precio_venta, a.stock, a.descripcion, a.imagen, a.activo FROM articulo a inner join categoria c ON a.categoria_id=c.id WHERE a.nombre LIKE ? AND a.stock>0 AND a.activo=true ORDER BY a.id ASC LIMIT ?,?");
            ps.setString(1,"%" + texto +"%");            
            ps.setInt(2, (numPagina-1)*totalPorPagina);
            ps.setInt(3, totalPorPagina);
            rs=ps.executeQuery();
            while(rs.next()){
                registros.add(new Articulo(rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getDouble(6),rs.getInt(7),rs.getString(8),rs.getString(9),rs.getBoolean(10)));
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally{
            ps=null;
            rs=null;
            CON.desconectar();
        }
        return registros;
    }
    
    //Met que devuelve un obj de tipo Articulo para compras
    public Articulo obtenerArticuloCodigoIngreso(String codigo){//espera par codigo
        //obj de tipo Articulo
        Articulo art=null;
        try {
            //preparo el ps con la consulta SQL
            ps=CON.conectar().prepareStatement("SELECT id,codigo,nombre,precio_venta,stock FROM articulo WHERE codigo=?");
            //envio al ps el codigo
            ps.setString(1,codigo);
            //ejecuto el ps
            rs=ps.executeQuery();
            //si obtengo un resultado
            if (rs.first()){
                //envio valores al obj Articulo (creo un constructor solo para estos parametros)
                //envio al constructor estos  parametreos, por medio del metodo get del rs
                art=new Articulo(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getDouble(4),rs.getInt(5));
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally{
            ps=null;
            rs=null;
            CON.desconectar();
        }
        return art; //retorno obj articulo
    }
    
    public Articulo obtenerArticuloCodigoVenta(String codigo){
        Articulo art=null;
        try {
            ps=CON.conectar().prepareStatement("SELECT id,codigo,nombre,precio_venta,stock FROM articulo WHERE codigo=? AND stock>0 AND activo=true");
            ps.setString(1,codigo);
            rs=ps.executeQuery();
            
            if (rs.first()){
                art=new Articulo(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getDouble(4),rs.getInt(5));
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally{
            ps=null;
            rs=null;
            CON.desconectar();
        }
        return art;
    }

    @Override
    public boolean insertar(Articulo obj) {
        resp=false;// hasta aqui no inserta nada
        try {
            ps=CON.conectar().prepareStatement("INSERT INTO articulo (categoria_id,codigo,nombre,precio_venta,stock,descripcion,imagen,activo) VALUES (?,?,?,?,?,?,?,1)");
            //Enviamos al ps los valores obtenidos del obj Articulo
            ps.setInt(1,obj.getCategoriaId());
            ps.setString(2, obj.getCodigo());
            ps.setString(3, obj.getNombre());
            ps.setDouble(4, obj.getPrecioVenta());
            ps.setInt(5, obj.getStock());
            ps.setString(6, obj.getDescripcion());
            ps.setString(7, obj.getImagen());
            
            //si al ejecutar obtengo 1 fila mínimo
            if (ps.executeUpdate()>0){
                resp=true; //logró registrar
            }
            ps.close();
        }  catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally{
            ps=null;
            CON.desconectar();
        }
        return resp; //retorno la respuesta booleana
    }

    @Override
    public boolean actualizar(Articulo obj) {
        resp=false;
        try {
            ps=CON.conectar().prepareStatement("UPDATE articulo SET categoria_id=?, codigo=?, nombre=?, precio_venta=?, stock=?, descripcion=?, imagen=? WHERE id=?");
            ps.setInt(1,obj.getCategoriaId());
            ps.setString(2, obj.getCodigo());
            ps.setString(3, obj.getNombre());
            ps.setDouble(4, obj.getPrecioVenta());
            ps.setInt(5, obj.getStock());
            ps.setString(6, obj.getDescripcion());
            ps.setString(7, obj.getImagen());
            ps.setInt(8, obj.getId()); //para especificar que articulo quiero editar
            
            if (ps.executeUpdate()>0){
                resp=true; //logró actualizar
            }
            ps.close(); //cierro el ps
            
        }  catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            
        } finally{
            ps=null;
            CON.desconectar();
        }
        return resp; //retorno la respuesta booleana
    }

    @Override
    public boolean desactivar(int id) {
        resp=false;
        try {
            ps=CON.conectar().prepareStatement("UPDATE articulo SET activo=0 WHERE id=?");
            ps.setInt(1, id);
            if (ps.executeUpdate()>0){
                resp=true;
            }
            ps.close();
        }  catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally{
            ps=null;
            CON.desconectar();
        }
        return resp; //retorno la respuesta booleana
    }

    @Override
    public boolean activar(int id) {
        resp=false;
        try {
            ps=CON.conectar().prepareStatement("UPDATE articulo SET activo=1 WHERE id=?");
            ps.setInt(1, id);
            if (ps.executeUpdate()>0){
                resp=true;
            }
            ps.close();
        }  catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally{
            ps=null;
            CON.desconectar();
        }
        return resp; //retorno la respuesta booleana
    }

    @Override
    public int total() {
        int totalRegistros=0;
        try {
            ps=CON.conectar().prepareStatement("SELECT COUNT(id) FROM articulo");            
            rs=ps.executeQuery();
            
            while(rs.next()){
                totalRegistros=rs.getInt("COUNT(id)");
            }            
            ps.close();
            rs.close();
        }  catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally{
            ps=null;
            rs=null;
            CON.desconectar();
        }
        return totalRegistros;
    }

    @Override
    public boolean existe(String texto) {//texto que recibe por parametro
        resp=false;
        try {
            ps=CON.conectar().prepareStatement("SELECT nombre FROM articulo WHERE nombre=?");
            ps.setString(1, texto);
            rs=ps.executeQuery();
            //mueve cursor al último registro del rs para comprobar cuántas filas se han recuperado
            rs.last();
            if(rs.getRow()>0){
                resp=true;
            }           
            ps.close();
            rs.close();
        }  catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally{
            ps=null;
            rs=null;
            CON.desconectar();
        }
        return resp; //retorno la respuesta booleana 
    }
}
