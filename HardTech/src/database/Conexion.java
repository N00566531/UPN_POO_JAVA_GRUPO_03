
package database;

//Librerias

import java.sql.Connection; //clase para conexion a db usando driver jdbc
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

//clase pública
public class Conexion {
    
    //variables constantes
    private final String DRIVER="com.mysql.jdbc.Driver"; //hago referencia a la clase driver jdbc
    private final String URL="jdbc:mysql://localhost:3306/"; //url conexion
    private final String DB="dbsistema"; //base de datos
    private final String USER="root"; //para produccion se debe crear usuario y clave
    private final String PASSWORD=""; //pass blanco
     
    //var pública de tipo Connection que almacena cadena de la conexion
    public Connection cadena;
    
    //var estática de tipo Conexion (solo almacena la única instancia)
    public static Conexion instancia;
    
   //Const. para iniciar la cadena como null
   private  Conexion(){
        this.cadena=null;
    }
    
    
   //Método público de tipo Connection
    public Connection conectar(){
        try { //capturador de excepciones
            
            Class.forName(DRIVER); //llamo al driver de conexión
            //almaceno la conexion a traves de la clase DriverManager por su metodo getConnection
            this.cadena=DriverManager.getConnection(URL+DB,USER,PASSWORD);
        } catch (ClassNotFoundException | SQLException e) { //exception de tipo SQL
            //muestro el error en una ventana
            JOptionPane.showMessageDialog(null, e.getMessage());
            System.exit(0); //que se cierre el proyecto de no hallar la conexion
        }
        return this.cadena;//devuelvo la cadena de conexion si todo está ok
    }
    
    //Método para desconectar
    public void desconectar(){
        try {
            this.cadena.close(); //cierro la cadena
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    
   //Singleton
   //Método sincronizado de tipo Conexion (devuelve un objeto de tipo Conexion)
   public synchronized static Conexion getInstancia(){
        if (instancia==null){ //si no existe ninguna instancia de esta clase 
            //lo instancio por primera vez
            instancia=new Conexion();
        }
        return instancia; //devuelvo la instancia existente
    }
}

//sincronizado evita inconsistencia de datos