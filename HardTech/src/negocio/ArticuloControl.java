package negocio;

import database.Conexion;
import datos.ArticuloDAO;
import datos.CategoriaDAO;
import entidades.Articulo;
import entidades.Categoria;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.table.DefaultTableModel;

/*import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;*/


//Gestionar la lógica de mantenimiento de Articulo
public class ArticuloControl {
    //Creo los objetos para las clases
    private final ArticuloDAO DATOS;
    private final CategoriaDAO DATOSCAT;
    private Articulo obj;
    private DefaultTableModel modeloTabla;
    public int registrosMostrados;
    
    public ArticuloControl(){
        //Creo las instancias a las clases
        this.DATOS=new ArticuloDAO();
        this.DATOSCAT=new CategoriaDAO();
        this.obj=new Articulo();
        this.registrosMostrados=0;
    }
    
    /*
    Metodo que devolverá al front una tabla modelo para incluirlo en un JTable 
    */
    public DefaultTableModel listar(String texto,int totalPorPagina,int numPagina){
        /*
         crea una lista de objetos Articulo utilizando ArrayList 
         almacenará las Articulos que se recuperan de la base de datos
        */

        List<Articulo> lista=new ArrayList();
         /*
        llama a método listar de DATOS, pasando los parametros; 
        listar devuelve una lista de Articulos que coincidan con el texto
        y se añade a var lista
        */
        lista.addAll(DATOS.listar(texto,totalPorPagina,numPagina));
        
        //vector que almacena los titulos de las columnas
        String[] titulos={"Id","Categoría ID","Categoría","Código","Nombre","Precio Venta","Stock","Descripción","Imagen","Estado"};
        this.modeloTabla=new DefaultTableModel(null,titulos);        
        
        //almacena el estado del Articulo
        String estado;
        //vector String de 10 indices para almacenar valores de cada fila 
        //que se añadirá al modelo de la tabla
        String[] registro = new String[10];
        
        this.registrosMostrados=0;
        //itera sobre cada objeto de Articulo en la lista
        for (Articulo item:lista){
            //si el campo es activo
            if (item.isActivo()){
                estado="Activo";
            } else{
                estado="Inactivo";
            }
            //envio los valores obtenidos del obj item, y relleno el vector registro 
            //con los datos del Articulo actual
            registro[0]=Integer.toString(item.getId());
            registro[1]=Integer.toString(item.getCategoriaId());
            registro[2]=item.getCategoriaNombre();
            registro[3]=item.getCodigo();
            registro[4]=item.getNombre();
            registro[5]=Double.toString(item.getPrecioVenta());
            registro[6]=Integer.toString(item.getStock());
            registro[7]=item.getDescripcion();
            registro[8]=item.getImagen();
            registro[9]=estado;
            
             //agrego los registros obtenidos al modeloTabla
            this.modeloTabla.addRow(registro);
            
            //comienzo a contar registros mostrados
            this.registrosMostrados=this.registrosMostrados+1;
        }
        return this.modeloTabla; //retorno el modelotabla con los registros
    }
    
    public DefaultTableModel listarArticuloVenta(String texto,int totalPorPagina,int numPagina){
        List<Articulo> lista=new ArrayList();
        lista.addAll(DATOS.listarArticuloVenta(texto,totalPorPagina,numPagina));
        
        String[] titulos={"Id","Categoría ID","Categoría","Código","Nombre","Precio Venta","Stock","Descripción","Imagen","Estado"};
        this.modeloTabla=new DefaultTableModel(null,titulos);        
        
        String estado;
        String[] registro = new String[10];
        
        this.registrosMostrados=0;
        for (Articulo item:lista){
            if (item.isActivo()){
                estado="Activo";
            } else{
                estado="Inactivo";
            }
            registro[0]=Integer.toString(item.getId());
            registro[1]=Integer.toString(item.getCategoriaId());
            registro[2]=item.getCategoriaNombre();
            registro[3]=item.getCodigo();
            registro[4]=item.getNombre();
            registro[5]=Double.toString(item.getPrecioVenta());
            registro[6]=Integer.toString(item.getStock());
            registro[7]=item.getDescripcion();
            registro[8]=item.getImagen();
            registro[9]=estado;
            this.modeloTabla.addRow(registro);
            this.registrosMostrados=this.registrosMostrados+1;
        }
        return this.modeloTabla;
    }
    
  //Método que devuelve un Modelo para agregarlo a un combo box  
  public DefaultComboBoxModel seleccionar(){ 
      
      //Obj que instancia la clase
      DefaultComboBoxModel items= new DefaultComboBoxModel();
      
      //Declaro lista para obtener las categorías
      List<Categoria> lista=new ArrayList();
      
        //Le asigno la lista de categorías retornadas
        lista=DATOSCAT.seleccionar(); 
        
        //recorre cada objeto Categoria en lista
        for (Categoria item: lista){ 
            //Para cada item, se crea un nuevo objeto con el id y nombre
            //y se agrega al modelo de combo items
            items.addElement(new Categoria(item.getId(),item.getNombre()));
        }
        return items; //retorno el obj items para mostrar en un JComboBox
    }
    
    public String insertar(int categoriaId, String codigo, String nombre, double precioVenta, int stock,String descripcion, String imagen){
        //si registro existe
        if (DATOS.existe(nombre)){
            return "El registro ya existe.";
            
        }else{ //envio los valores al Método Set de obj Articulo
            obj.setCategoriaId(categoriaId);
            obj.setCodigo(codigo);
            obj.setNombre(nombre);
            obj.setPrecioVenta(precioVenta);
            obj.setStock(stock);
            obj.setDescripcion(descripcion);
            obj.setImagen(imagen);
            
             //envio el objeto a insertar de la capa DATOS
            if (DATOS.insertar(obj)){ //si es true
                return "OK"; //si registra devuelve true al front
                
            }else{//de lo contrario no se registró
                return "Error en el registro."; //retorna error
            }
        }
    }
    
    public String actualizar(int id,int categoriaId, String codigo, String nombre, String nombreAnt, double precioVenta, int stock,String descripcion, String imagen){
        //si el nombre es igual al anterior es porque no quiere editarlo
        if (nombre.equals(nombreAnt)){
            //envio los valores a las propiedades del objeto para actualizar en la tabla
            obj.setId(id);
            obj.setCategoriaId(categoriaId);
            obj.setCodigo(codigo);
            obj.setNombre(nombre);
            obj.setPrecioVenta(precioVenta);
            obj.setStock(stock);
            obj.setDescripcion(descripcion);
            obj.setImagen(imagen);
            
            //llamo a actualizar y le envio obj
            if(DATOS.actualizar(obj)){//si es true
                return "OK"; //devuelvo OK al Front
                
            }else{
                return "Error en la actualización.";
            }
            
        }else{//si no es igual es porque lo está editando
            if (DATOS.existe(nombre)){//llamo a existe y envío el nuevo nombre
                return "El registro ya existe.";
                
            }else{//envio al obj los valores
                obj.setId(id);
                obj.setCategoriaId(categoriaId);
                obj.setCodigo(codigo);
                obj.setNombre(nombre);
                obj.setPrecioVenta(precioVenta);
                obj.setStock(stock);
                obj.setDescripcion(descripcion);
                obj.setImagen(imagen);
                
                //llamo a actualizar y envio el obj
                if (DATOS.actualizar(obj)){//si es true
                    return "OK";//OK al Front
                    
                }else{
                    return "Error en la actualización.";
                }
            }
        }
    
    }
    public String desactivar(int id){
        if (DATOS.desactivar(id)){
            return "OK";
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
        return DATOS.total();
    }
    
    public int totalMostrados(){
        return this.registrosMostrados;
    }
    
   /* public void reporteArticulos(){
        Map p=new HashMap();
        JasperReport report;
        JasperPrint print;
        
        Conexion cnn=Conexion.getInstancia();
        
        try {
            report=JasperCompileManager.compileReport(new File("").getAbsolutePath()+
                    "/src/reportes/RptArticulos.jrxml");
            print=JasperFillManager.fillReport(report, p,cnn.conectar());
            JasperViewer view=new JasperViewer(print,false);
            view.setTitle("Reporte de Artículos");
            view.setVisible(true);
        } catch (JRException e) {
            e.getMessage();
        }
    }
    
    public void reporteArticulosBarras(){
        Map p=new HashMap();
        JasperReport report;
        JasperPrint print;
        
        Conexion cnn=Conexion.getInstancia();
        
        try {
            report=JasperCompileManager.compileReport(new File("").getAbsolutePath()+
                    "/src/reportes/RptArticulosBarras.jrxml");
            print=JasperFillManager.fillReport(report, p,cnn.conectar());
            JasperViewer view=new JasperViewer(print,false);
            view.setTitle("Reporte de Artículos");
            view.setVisible(true);
        } catch (JRException e) {
            e.getMessage();
        }
    }*/
}

