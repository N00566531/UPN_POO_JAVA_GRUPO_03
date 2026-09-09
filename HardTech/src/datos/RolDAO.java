
package datos;

import database.Conexion;

import entidades.Rol;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;


//Esta clase no implementa interfaces
public class RolDAO {
    
    //constante CON que instancia a la conexion
    private final Conexion CON;
    //Obj que compila las sentencias SQL y lo envia al Gestor
    private PreparedStatement ps;
    //Obj que instancia a la clase ResultSet, para guardar el resultado de las sentencias
    private ResultSet rs;
  
    
    //Constructor
    public RolDAO(){
        
        //llamo a la instancia de la conexion
        CON=Conexion.getInstancia();
    }
    
    
    public List<Rol> listar() {
        //obj de tipo list, e instancia a la clase ArrayList
        List<Rol> registros=new ArrayList();
        try {
            //llamo a conectar y envio la sentencia select al gestor MySQL
            ps=CON.conectar().prepareStatement("SELECT * FROM rol");
           
            rs=ps.executeQuery();//almaceno la respuesta de consulta
            //recorro el rs
            while(rs.next()){ //agrego al array los registros obtenidos del obj rol
                registros.add(new Rol(rs.getInt(1),rs.getString(2),rs.getString(3)));
            }
            ps.close(); //cierro el ps y el rs
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally{
            ps=null; //nulo
            rs=null;
            CON.desconectar(); //se desconecta
        }
        return registros; //retorno los  registros del arrays
    }
    
    //Met para seleccionar roles (es propio de la clase)
    public List<Rol> seleccionar() {
        List<Rol> registros=new ArrayList();
        try {
            ps=CON.conectar().prepareStatement("SELECT id, nombre FROM rol ORDER BY nombre asc");
            rs=ps.executeQuery();
            //recorro el rs
            while(rs.next()){
                //almaceno en el arreglo (instancio a Rol y envio valores a los parametros id y nombre
                registros.add(new Rol(rs.getInt(1),rs.getString(2)));
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
    
     public int total() {
        int totalRegistros=0; //contador en 0
        try {
            ps=CON.conectar().prepareStatement("SELECT COUNT(id) FROM rol");            
            rs=ps.executeQuery();
            
            while(rs.next()){ //recorro el rs
                //le envio el conteo obtenido de los id contados
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

}
