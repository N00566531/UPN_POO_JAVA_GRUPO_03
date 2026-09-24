
package datos;

import com.mysql.jdbc.Statement;
import database.Conexion;
import datos.Interfaces.CrudIngresoInterface;
import entidades.DetalleIngreso;
import entidades.Ingreso;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.sql.Date;


//Implementa la interfaz CrudIngresoInterface
public class IngresoDAO implements CrudIngresoInterface<Ingreso, DetalleIngreso>  {
    private final Conexion CON;
    private PreparedStatement ps;
    private ResultSet rs;
    private boolean resp;
    
    //Constructor
    public IngresoDAO(){
        //Instancio obj CON (clase Conexion) y a su met conexion
        CON=Conexion.getInstancia();
    }
  
    //Implementamos metodos abstractos
    @Override
    public List<Ingreso> listar(String texto, int totalPorPagina, int numPagina) {
        List<Ingreso> registros=new ArrayList();
        try {
            ps=CON.conectar().prepareStatement("SELECT i.id,i.usuario_id,u.nombre as usuario_nombre,i.persona_id,p.nombre as persona_nombre,i.tipo_comprobante,i.serie_comprobante,i.num_comprobante,i.fecha,i.impuesto,i.total,i.estado FROM ingreso i INNER JOIN persona p ON i.persona_id=p.id INNER JOIN usuario u ON i.usuario_id=u.id WHERE i.num_comprobante LIKE ? ORDER BY i.id ASC LIMIT ?,?");
            ps.setString(1,"%" + texto +"%");            
            ps.setInt(2, (numPagina-1)*totalPorPagina);
            ps.setInt(3, totalPorPagina);
            rs=ps.executeQuery();
            //Enviamos valores obtenidos al constructor sin detalles
            while(rs.next()){
                registros.add(new Ingreso(rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getInt(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getDate(9),rs.getDouble(10),rs.getDouble(11),rs.getString(12)));
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

    @Override
    //Met para listar detalles de un ingreso a almacen
    public List<DetalleIngreso> listarDetalle(int id) { 
             List<DetalleIngreso> registros=new ArrayList();
        try {
            ps=CON.conectar().prepareStatement("SELECT a.id,a.codigo,a.nombre,d.cantidad,d.precio,(d.cantidad*precio) as sub_total FROM detalle_ingreso d INNER JOIN articulo a ON d.articulo_id=a.id WHERE d.ingreso_id=?");
            ps.setInt(1,id); //le envio el valor que recibo como parametro (id ingreso)
            rs=ps.executeQuery();
            while(rs.next()){ //envio al const creado de 6 parametros
                registros.add(new DetalleIngreso(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getDouble(5),rs.getDouble(6)));
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
        return registros; //Devuelve detalles de un ingreso
    }
    
    /*Para insertar un ingreso se trabajará con transacciones
    En insertar las haremos de manera manual(hasta ahora fueron automaticas)
    Si inserto ingreso podré insertar los detalles, y transaccion será exitosa
    de lo contrario se cancelará todo el ingreso (será nulo)*/
    @Override  
    public boolean insertar(Ingreso obj) {
         resp=false;
         //Objeto que hace nueva instancia a clase Connection
         //Para trabajar con trans manuales; esta nueva conexion está basandose en la existente
        Connection conn=null; //será nulo
        try {
            conn=CON.conectar();//Llamo met conectar del obj CON (instancia de la clase conexion)
            /*Por defecto el AutoCommit es true (como los demas mantenimientos)
            
            Para iniciar transaccion se deshabilita AutoCommit controlar de lo que se hace y cuando
            Met Commit es para realizar instrucciones emitidas
            Si hay error haremos rollback para deshacer peticiones*/
            conn.setAutoCommit(false);
            
            //Var para almacenar la instruccion SQL de insertar
            String sqlInsertIngreso="INSERT INTO ingreso (persona_id,usuario_id,fecha,tipo_comprobante,serie_comprobante,num_comprobante,impuesto,total,estado) VALUES (?,?,now(),?,?,?,?,?,?)";
            
            //Envio sentencia al ps, y retorna ID (PK) del ingreso
            ps=conn.prepareStatement(sqlInsertIngreso,Statement.RETURN_GENERATED_KEYS);
            //Envio valores a los parametros que corresponden en el const
            ps.setInt(1,obj.getPersonaId());
            ps.setInt(2, obj.getUsuarioId());
            ps.setString(3,obj.getTipoComprobante());
            ps.setString(4, obj.getSerieComprobante());
            ps.setString(5, obj.getNumComprobante());
            ps.setDouble(6, obj.getImpuesto());
            ps.setDouble(7, obj.getTotal());
            ps.setString(8, "Aceptado"); //valor por defecto
            
            //Var para ejecutar el ps; en caso se ejecute el ingreso su valor será 1
            int filasAfectadas=ps.executeUpdate();
            //Obtengo ID ingreso autogenerado
            rs=ps.getGeneratedKeys();
            int idGenerado=0;
            //Recorro rs
            if (rs.next()){
                //Var id generado; lo obtengo del rs
                idGenerado=rs.getInt(1);
            }
            
            //Si es 1 (insertó ingreso)
            if (filasAfectadas==1){
                //Se inserta detalles
                //Var para instruccion SQL del detalle
                String sqlInsertDetalle="INSERT INTO detalle_ingreso (ingreso_id,articulo_id,cantidad,precio) VALUES (?,?,?,?)";
                //Envio sentencia al ps
                ps=conn.prepareStatement(sqlInsertDetalle);
                //Un ingreso tiene varios detalles
                //Recorro detalles representados en el List creado en entidad Ingreso
                for (DetalleIngreso item : obj.getDetalles()){
                   //Envio valores al ps, a los parametros que corresponden 
                    ps.setInt(1,idGenerado);
                    ps.setInt(2,item.getArticuloId());
                    ps.setInt(3, item.getCantidad());
                    ps.setDouble(4, item.getPrecio());
                    //Booleana para enviar a negocio cuando ejecuta insertar
                    //Si es mayor a 0 es porque insertó detalle
                    resp=ps.executeUpdate()>0;
                }
                conn.commit();//Si inserta ejecuta commit 
                
            }else{//Sino ingresó
                conn.rollback();//Anule detalle al no insertar ingreso
            }
        }  catch (SQLException e) {
            try {
                if (conn!=null){//si la conexion no es nula
                    conn.rollback();//ejecuto rollback para no afectar la db
                }
                JOptionPane.showMessageDialog(null, e.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(IngresoDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } finally{
            try {
                
                if (rs!=null) rs.close();//cierro rs,ps y conn
                if (ps!=null) ps.close();
                if (conn!=null) conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(IngresoDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return resp; //1 o 0
    }

    @Override
    public boolean anular(int id) {
         resp=false;
        try {
            ps=CON.conectar().prepareStatement("UPDATE ingreso SET estado='Anulado' WHERE id=?");
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
        return resp;
    }

    @Override
    public int total() {
         int totalRegistros=0;
        try {
            ps=CON.conectar().prepareStatement("SELECT COUNT(id) FROM ingreso");            
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
    public boolean existe(String texto1, String texto2) {
       resp=false;
        try {
            ps=CON.conectar().prepareStatement("SELECT id FROM ingreso WHERE serie_comprobante=? AND num_comprobante=?");
            ps.setString(1, texto1);
            ps.setString(2, texto2);
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
        return resp;
    }
    
     public List<Ingreso> consultaFechas(Date fechaInicio, Date fechaFin) {
        List<Ingreso> registros = new ArrayList();
        try {
            ps = CON.conectar().prepareStatement("SELECT i.id,i.usuario_id,u.nombre as usuario_nombre,i.persona_id,p.nombre as persona_nombre,i.tipo_comprobante,i.serie_comprobante,i.num_comprobante,i.fecha,i.impuesto,i.total,i.estado FROM ingreso i INNER JOIN persona p ON i.persona_id=p.id INNER JOIN usuario u ON i.usuario_id=u.id WHERE i.fecha>=? AND i.fecha<=?");
            ps.setDate(1, fechaInicio);
            ps.setDate(2, fechaFin);
            rs = ps.executeQuery();
            while (rs.next()) {
                registros.add(new Ingreso(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getInt(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getDate(9), rs.getDouble(10), rs.getDouble(11), rs.getString(12)));
            }
            ps.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            ps = null;
            rs = null;
            CON.desconectar();
        }
        return registros;
    }
    
}
