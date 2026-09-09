
package negocio;

import datos.RolDAO;

import entidades.Rol;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class RolControl {
    
    //constante que instancia a RolDAO
    private final RolDAO DATOS;
    //obj que instnacia a entidad Categoria
    private Rol obj;
    //Obj para crar un modelo de tabla
    private DefaultTableModel modeloTabla;
    //var para controlar los reg mostrados
    public int registrosMostrados;
    
    
    public RolControl(){
        
         //inicializo DATOS que instancia a clase RolDAO
        this.DATOS= new RolDAO();
        //inicializo a obj que instancia Categoria
        this.obj=new Rol();
        this.registrosMostrados=0; //inicio en 0
        
    }
    
    //Metodo de tipo DefaultTableModel (devolverá al front una tabla para incluirlo en un JTable 
    public DefaultTableModel listar(){ 
        //obj de tipo List, que implemente a Rol e instancio a una Lista de arreglos
        List<Rol> lista=new ArrayList();
        //agrego al arreglo una lista de registros (metodo listar de objeto DATOS)
        lista.addAll(DATOS.listar());
        
        //vector que almacena los titulos de las columnas de la tabla
        String[] titulos={"Id","Nombre","Descripción"};
        //creo instancia de la clase DefaultTableModel y envio las columnas
        //no envio registros, solo titulos
        this.modeloTabla=new DefaultTableModel(null,titulos);        
        
       
        //vector de 3 indices
        String[] registro = new String[3];
           
       //reinicio var (se le llamará varias veces cada vez que se haga una busqueda)
        this.registrosMostrados=0;
        //recorro los items del array lista e instancio a Categoria
        for (Rol item:lista){
     
            //envio valores a los indices del vector obtenidos del obj rol
            registro[0]=Integer.toString(item.getId());
            registro[1]=item.getNombre();
            registro[2]=item.getDescripcion();
            
            //agrego ahora si los registros al modeloTabla
            this.modeloTabla.addRow(registro);
            
            //comienzo a contar
            this.registrosMostrados=this.registrosMostrados+1;
        }
        return this.modeloTabla; //retorno el modelotabla con los registros
    }
    
     public int total(){
        //es para obtener el total de los registros almacenados en la tabla
        return DATOS.total(); //llamo al met total
    }
    
    //es para obtener total de los registros mostrados
    public int totalMostrados(){
        return this.registrosMostrados; //retorno
    }
    
    
}
