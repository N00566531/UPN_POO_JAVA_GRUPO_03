package datos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.Conexion;
import datos.interfaces.CrudSimpleInterface;
import entidades.Categoria;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/*
Acceso a Datos es para acceder a los datos de la tabla
Implementa el CrudSimpleInterface (se le envia el obj Categoria)
Se implementa los metodos abstractos de la interface
*/
public class CategoriaDAO implements CrudSimpleInterface<Categoria> {
   
    //Constante CON de tipo conexion que instancia a la conexion a la BD
    private final Conexion CON;
    //Obj que instancia clase PreparedStatement: compila sentencias SQL y las envia al gestor
    private PreparedStatement ps;
    //Obj que instancia clase ResultSet, para almacenar resultado de la sentencia SQL ejecutadas
    private ResultSet rs;
    //Almacena resultado de la operación
    private boolean resp;
    
    //Constructor para iniciar la var conexion
    public CategoriaDAO(){
        CON=Conexion.getInstancia();//Obtengo conexion y la almaceno en el obj CON
    }
    
    
    @Override
    //Método devuelve una lista de objetos Categoria
    public List<Categoria> listar(String texto) { //Recibe texto para filtrar  
        
        /*
        Inicializa lista que almacena categorias; instancia a ArrayList
        */
        List<Categoria> registros=new ArrayList();
        
        try {
            //Llamo a conectar y envio la sentencia select al MySQL
            ps=CON.conectar().prepareStatement("SELECT * FROM categoria WHERE nombre LIKE ?");
            //Al inicio, centro o final puede ir cualquier cadena de texto del nombre a filtrar
            ps.setString(1,"%" + texto +"%"); 
            //Almacena respuesta (listado) de consulta ejecutada por ps
            rs=ps.executeQuery();
            //Recorre rs (hasta que no hayan más filas)
            while(rs.next()){ 
                /*
                Agrega al arraylist los registros obtenidos; 
                del rs obtengo valores y los envio al constructor (con los 4 parametros) 
                */
                registros.add(new Categoria(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getBoolean(4)));
            }
            //Cierro el ps y el rs para limpiar memoria
            ps.close(); 
            rs.close();
            
        } catch (SQLException e) { //Excepciones
            JOptionPane.showMessageDialog(null, e.getMessage());
            
        } finally{ //Codigo que siempre se ejecutará
            ps=null; //nulo
            rs=null; //nulo
            CON.desconectar(); //Se desconecta de bd, liberA memoria
        }
        return registros; //Retorno registros del array, es decir, la lista
    }
    
    //Método propio de la Clase; devuelve lista de objetos Categoria
    public List<Categoria> seleccionar() {
        
        //ArrayList que se usará para almacenar cada objeto de Categoria
        List<Categoria> registros=new ArrayList();
        try {
            ps=CON.conectar().prepareStatement("SELECT id, nombre FROM categoria ORDER BY nombre asc");
            rs=ps.executeQuery();
            //recorro el rs
            while(rs.next()){
                /*
                al recorrer agrega al arraylist los registros obtenidos de categoria; 
                del rs obtengo los valores y los envio al constructor (con los 2 parametros) 
                */
                registros.add(new Categoria(rs.getInt(1),rs.getString(2)));
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
        return registros; //devuelvo lista de registros
    }

    @Override
    //Método insertar al que se envia obj Categoria
    public boolean insertar(Categoria obj) {
        resp=false;//No inserta nada al inicio
        
        try {
            //Se conecta  y se prepara sentencia SQL
            ps=CON.conectar().prepareStatement("INSERT INTO categoria (nombre,descripcion,activo) VALUES (?,?,1)");
           //Envio nombre y desc al ps (obtenidos del objeto Categoria) para el INSERT
            ps.setString(1, obj.getNombre());
            ps.setString(2, obj.getDescripcion());
            
            //Si al ejecutar y obtengo mayor a 0 filas es porque afectó 1 fila por lo menos
            if (ps.executeUpdate()>0){
                resp=true; //Logró registrar
            }
            ps.close(); //Cierro el ps
            
        }  catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            
        } finally{ //Siempre ejecuta haya o no excepción
            ps=null;
            rs=null;
            CON.desconectar();
        }
        return resp; //Retorno respuesta booleana
    }

    @Override
    public boolean actualizar(Categoria obj) {
        resp=false;
        
        try {
           
            ps=CON.conectar().prepareStatement("UPDATE categoria SET nombre=?, descripcion=? WHERE id=?");
            //Envio los 3 parametros al ps para UPDATE
            ps.setString(1, obj.getNombre());
            ps.setString(2, obj.getDescripcion());
            ps.setInt(3, obj.getId());
            
            if (ps.executeUpdate()>0){
                resp=true; //Logró actualizar
            }
            ps.close();
            
        }  catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            
        } finally{
            ps=null;
            rs=null;
            CON.desconectar();
        }
        return resp;
    }

    @Override
    public boolean desactivar(int id) { 
        resp=false;
        try {
            ps=CON.conectar().prepareStatement("UPDATE categoria SET activo=0 WHERE id=?");
            //Envio el id para desactivar
            ps.setInt(1, id);
            
            if (ps.executeUpdate()>0){
                resp=true; //Logró desactivar
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
    public boolean activar(int id) {
        resp=false;
        try {
            ps=CON.conectar().prepareStatement("UPDATE categoria SET activo=1 WHERE id=?");
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
        int totalRegistros=0; //Contador
        try {
            ps=CON.conectar().prepareStatement("SELECT COUNT(id) FROM categoria");  
            /*
            Ejecuta consulta y devuelve un ResultSet (rs) que contiene una única fila con el total de registros
            */
            rs=ps.executeQuery();
            
            while(rs.next()){ //Recorro rs
                //Le envio el conteo obtenido
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
        return totalRegistros; //Retorno contador
    }

    //Determina si existe o no un registro en función del nombre
    @Override
    public boolean existe(String texto) {
        
        resp=false; //No existe inicialmente
        try {
            ps=CON.conectar().prepareStatement("SELECT nombre FROM categoria WHERE nombre=?");
            //Al ps envio cadena (el parametro que se recibe)
            ps.setString(1, texto);
            //Ejecuta consulta y devuelve un ResultSet que contendrá los resultados
            rs=ps.executeQuery();
            //Mueve cursor al último registro del rs para comprobar cuántas filas se han recuperado
            rs.last();
            
            //Si obtiene al menos una fila (usa el met getRow)
            if(rs.getRow()>0){
                resp=true; //Existe registro con ese nombre y retorna true
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
}
