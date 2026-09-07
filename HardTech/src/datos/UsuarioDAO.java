
package datos;

//Importacion de clases y librerias
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.Conexion;
import datos.interfaces.CrudPaginadoInterface;

import entidades.Usuario;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

//Implementa CrudPaginadoInterface y le envia como parametro la entidad Usuario
public class UsuarioDAO implements CrudPaginadoInterface<Usuario> { 
    //Creo obj para las clases
    private final Conexion CON;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;
    
    public UsuarioDAO(){
        CON=Conexion.getInstancia();
    }
    
    
    @Override
    //Método devuelve una lista de objetos de tipo Usuario
    public List<Usuario> listar(String texto,int totalPorPagina,int numPagina) {//par esperados
        /*
        inicializa lista llamada registros que almacena objetos 
        de tipo Usuario; instancia a la clase ArrayList
         */

        List<Usuario> registros=new ArrayList();
        try {
            ps=CON.conectar().prepareStatement("SELECT u.id, u.rol_id, r.nombre as rol_nombre, u.nombre, u.tipo_documento, u.num_documento, u.direccion, u.telefono, u.email, u.clave, u.activo FROM usuario u inner join rol r ON u.rol_id=r.id WHERE u.nombre LIKE ? ORDER BY u.id ASC LIMIT ?,?");
            
            //PreparedStatement envia 3 parametros
            ps.setString(1,"%" + texto +"%"); //parametro like         
            ps.setInt(2, (numPagina-1)*totalPorPagina); //limitar cantidad de registros, desde que registro empezar a mostrar
            ps.setInt(3, totalPorPagina);//cuantos registros mostrar a partir del registro anterior 
            
            //Ejecutar y devolver el resultado
            rs=ps.executeQuery();
            while(rs.next()){//recorro cada fila del rs
                /*
                Para cada fila se crea un nuevo Usuario con los valores 
                de rs y se agrega a la lista registros
                */
                registros.add(new Usuario(rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getBoolean(11)));
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
        //devuelve la lista registros con los objetos Usuarios
        return registros;
    }
    
    
    //Met que devuelve un objeto de tipo Usuario
    public Usuario login(String email, String clave){ 
        
        Usuario usu=null; //objeto de tipo Usuario inicializado en nulo
        
        try {
             ps=CON.conectar().prepareStatement("SELECT u.id, u.rol_id, r.nombre as rol_nombre, u.nombre, u.tipo_documento, u.num_documento, u.direccion, u.telefono, u.email, u.activo FROM usuario u inner join rol r ON u.rol_id=r.id WHERE u.email=? AND clave=?");
            //PreparedStatement envia 2 parametros
            ps.setString(1,email); //parametro email        
            ps.setString(2,clave); //parametro clave
            
            //ResulSet los registros obtenidos de esa sentencia
            rs=ps.executeQuery();
            
            if(rs.first()){//si al menos obtengo 1 registro del resulset
                //instancio al objeto Usuario y le envio los valores al Constructor que no recibe clave
               usu=new Usuario(rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getBoolean(10)); 
                
            }
            ps.close();
            rs.close();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            ps=null;
            rs=null;
            CON.desconectar();
            
        }
        return usu; //retorno objeto
    }
    
  
    @Override
    public boolean insertar(Usuario obj) {
        resp=false;// hasta aqui no inserta nada
        try {
            ps=CON.conectar().prepareStatement("INSERT INTO usuario (rol_id,nombre,tipo_documento,num_documento,direccion,telefono,email,clave,activo) VALUES (?,?,?,?,?,?,?,?,1)");
            //enviamos al ps los valores, obtenidos de los atributos del obj a Articulo
            ps.setInt(1,obj.getRolId());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getTipoDocumento());
            ps.setString(4, obj.getNumDocumento());
            ps.setString(5, obj.getDireccion());
            ps.setString(6, obj.getTelefono());
            ps.setString(7, obj.getEmail());
            ps.setString(8, obj.getClave());
            
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
    public boolean actualizar(Usuario obj) {
        resp=false; //hasta aqui no edita nada
        try {
            ps=CON.conectar().prepareStatement("UPDATE usuario SET rol_id=?, nombre=?, tipo_documento=?, num_documento=?, direccion=?, telefono=?, email=?, clave=? WHERE id=?");
            ps.setInt(1,obj.getRolId());
            ps.setString(2, obj.getNombre());
            ps.setString(3, obj.getTipoDocumento());
            ps.setString(4, obj.getNumDocumento());
            ps.setString(5, obj.getDireccion());
            ps.setString(6, obj.getTelefono());
            ps.setString(7, obj.getEmail());
            ps.setString(8, obj.getClave());
            ps.setInt(9,obj.getId());
            
             //si al ejecutar obtengo 1 fila mínimo
            if (ps.executeUpdate()>0){
                resp=true; //logró editar
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
    public boolean desactivar(int id) {
        resp=false;
        try {
            ps=CON.conectar().prepareStatement("UPDATE usuario SET activo=0 WHERE id=?");
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
            ps=CON.conectar().prepareStatement("UPDATE usuario SET activo=1 WHERE id=?");
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
            ps=CON.conectar().prepareStatement("SELECT COUNT(id) FROM usuario");            
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
    public boolean existe(String texto) {
        resp=false;
        try {
            ps=CON.conectar().prepareStatement("SELECT email FROM usuario WHERE email=?");
            ps.setString(1, texto);
            rs=ps.executeQuery();
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
