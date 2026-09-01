package negocio;

import datos.CategoriaDAO;
import entidades.Categoria;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;


public class CategoriaControl {
    
    private final CategoriaDAO DATOS;
    private  Categoria obj;
    //Para crear y gestionar modelos de datos de tabla
    private DefaultTableModel modeloTabla;
    //Var para controlar los regisros mostrados
    public int registrosMostrados;
    
    //Constructor para inicializar objetos
    public CategoriaControl(){
        //Inicializa DAO 
        this.DATOS=new CategoriaDAO();
        //Inicializa obj Categoria
        this.obj=new Categoria();
        this.registrosMostrados=0; 
    }
    
    /*
    Metodo que devolverá al front una tabla modelo para incluirlo en un JTable 
    */
    public DefaultTableModel listar(String texto){ //Espera parametro de busqueda desde front
        
        List<Categoria> lista=new ArrayList();
        /*
        Llama a listar de DATOS, pasando texto; devuelve lista que coincidan con texto
        */
        lista.addAll(DATOS.listar(texto));
        
        //Vector para titulos de columnas
        String[] titulos={"Id","Nombre","Descripción","Estado"};
        
        //Instancia de clase DefaultTableModel y envio columnas (titulos)
        this.modeloTabla=new DefaultTableModel(null,titulos);        
        
        String estado; //Almacena estado de categoría
        
        //Vector de 4 indices para almacenar valores de cada fila que se añadirá al modelo de tabla
        String[] registro = new String[4];
           
        //Reinicio var (se le llamará varias veces cada vez que se haga una busqueda)
        this.registrosMostrados=0;
        
        //Itera sobre cada objeto en lista
        for (Categoria item:lista){
            //Si el campo es activo
            if (item.isActivo()){
                //Var estado será activo (para no devolver 1 o 0)
                estado="Activo";
            } else{//sino
                estado="Inactivo";
            }
            
            //Envio valores obtenidos de item, y relleno vector con datos de la categoría
            registro[0]=Integer.toString(item.getId()); //id lo convierto a cadena
            registro[1]=item.getNombre();
            registro[2]=item.getDescripcion();
            registro[3]=estado;
            
            //Agrego registros obtenidos al modeloTabla
            this.modeloTabla.addRow(registro);
            
            //Comienza contar registros mostrados
            this.registrosMostrados=this.registrosMostrados+1;
        }
        return this.modeloTabla; //Retorno modelotabla con registros
    }
    
    public String insertar(String nombre,String descripcion){
        //Envío nombre para validar la existencia de registro
        if (DATOS.existe(nombre)){ 
            return "El registro ya existe.";//Envio al return (paro la ejecución)
            
        }else{
            //Envio valores al Método Set de obj Categoría
            obj.setNombre(nombre);
            obj.setDescripcion(descripcion);
            
            //Envio obj a insertar de la capa DATOS
            if (DATOS.insertar(obj)){ //Si registra devuelve true
                return "OK"; //OK al front
                
            }else{ 
                return "Error en el registro."; //Retorna error
            }
        }
    }
    
    public String actualizar(int id, String nombre,String nombreAnt,String descripcion){ 
        //Si nombre que está editando es igual al anterior es porque no quiere modificar
        if (nombre.equals(nombreAnt)){
            //Envio valores al obj para actualizar en tabla
            obj.setId(id);
            obj.setNombre(nombre);
            obj.setDescripcion(descripcion);
            
            //Llamo a actualizar y envio obj
            if(DATOS.actualizar(obj)){ //si es true
                return "OK"; //OK al Front
            }else{
                return "Error en la actualización."; 
            }
            
        }else{//Si el nombre no es igual es porque lo está modificando
            if (DATOS.existe(nombre)){//Llamo a existe y envío el nuevo nombre
                return "El registro ya existe."; //Si devuelve true es porque existe
                
            }else{//Envio al obj los valores
                obj.setId(id);
                obj.setNombre(nombre);
                obj.setDescripcion(descripcion);
                
                if (DATOS.actualizar(obj)){ //Llamo a actualizar y envio obj
                    return "OK"; //OK al Front
                    
                }else{
                    return "Error en la actualización.";
                }
            }
        }
    }
    
    public String desactivar(int id){ //espera el id que se desactiva
        if (DATOS.desactivar(id)){ //si devuelve true
            return "OK"; //se desactiva
        }else{
            return "No se puede desactivar el registro";
        }
    }
    
    public String activar(int id){
        if (DATOS.activar(id)){
            return "OK";
        }else{
            return "No se puede activar el registro";
        }
    }
    
    public int total(){
        //Obtener total de los registros de la tabla
        return DATOS.total(); //llamo al met total
    }
    
    
    public int totalMostrados(){
        //Obtener total de los registros mostrados
        return this.registrosMostrados;
    }
}
