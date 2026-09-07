package negocio;


import datos.RolDAO;
import datos.UsuarioDAO;
import entidades.Rol;
import entidades.Usuario;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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


public class UsuarioControl {
    //creo los objetos para las instancias
    private final UsuarioDAO DATOS;
    private final RolDAO DATOSROL;
    private Usuario obj;
    private DefaultTableModel modeloTabla;
    public int registrosMostrados;
    
    public UsuarioControl(){//constructor
        //creo las instancias a las clases
        this.DATOS=new UsuarioDAO();
        this.DATOSROL=new RolDAO();
        this.obj=new Usuario();
        this.registrosMostrados=0;
    }
    
     /*
    Metodo que devolverá al front una tabla modelo para incluirlo en un JTable 
    */
    public DefaultTableModel listar(String texto,int totalPorPagina,int numPagina){
        /*
         crea una lista de objetos Articulo utilizando ArrayList 
         almacenará los usuarios que se recuperan de la base de datos
        */ 

        List<Usuario> lista=new ArrayList();
        /*
        llama a método listar de DATOS, pasando los parametros; 
        listar devuelve una lista de Articulos que coincidan con el texto
        y se añade a var lista
        */
        lista.addAll(DATOS.listar(texto,totalPorPagina,numPagina));
        
        //vector que almacena los titulos de las columnas
        String[] titulos={"Id","Rol ID","Rol","Usuario","Documento","# Documento","Dirección","Teléfono","Email","Clave","Estado"};
        this.modeloTabla=new DefaultTableModel(null,titulos);        
        
        //almacena el estado del usuario
        String estado;
        //vector String para almacenar valores de cada fila 
        //que se añadirá al modelo de la tabla
        String[] registro = new String[11];
        
        this.registrosMostrados=0;
        //itera sobre cada objeto  usuario en la lista
        for (Usuario item:lista){
             //si el campo es activo
            if (item.isActivo()){
                estado="Activo";
            } else{
                estado="Inactivo";
            }
            //envio los valores obtenidos del obj item, y relleno el vector registro 
            //con los datos del usuario actual
            registro[0]=Integer.toString(item.getId());
            registro[1]=Integer.toString(item.getRolId());
            registro[2]=item.getRolNombre();
            registro[3]=item.getNombre();
            registro[4]=item.getTipoDocumento();
            registro[5]=item.getNumDocumento();
            registro[6]=item.getDireccion();
            registro[7]=item.getTelefono();
            registro[8]=item.getEmail();
            registro[9]=item.getClave();
            registro[10]=estado;
            
            //agrego los registros obtenidos al modeloTabla
            this.modeloTabla.addRow(registro);
            
            //comienzo a contar registros mostrados
            this.registrosMostrados=this.registrosMostrados+1;
        }
        return this.modeloTabla; //retorno el modelotabla con los registros
    }
    
  //Método que devuelve un Modelo para agregarlo a un combo box  
  public DefaultComboBoxModel seleccionar(){ 
      
      //obj que instancia la clase
      DefaultComboBoxModel items= new DefaultComboBoxModel();
      
      //declaro lista para obtener los roles
      List<Rol> lista=new ArrayList();
      
         //Le asigno la lista de roles retornados
        lista=DATOSROL.seleccionar(); 
        
        //recorre cada objeto Rol en lista
        for (Rol item: lista){
            //Para cada item, se crea un nuevo objeto con el id y nombre
            //y se agrega al modelo de combo items
            items.addElement(new Rol(item.getId(),item.getNombre()));
        }
        return items; //retorno el obj items para mostrar en un JComboBox
    }
  
  /*
  public String login(String email, String clave){//espera dos parametros
      
      String resp="0";//usuario en un primer momento no existe
      //obj de tipo Usuario: instancio a la capa datos, al met login y envio email y clave encriptada
      Usuario usu = this.DATOS.login(email,this.encriptar(clave));
      
      if(usu!=null){ //si el usuario es váldio, es decir, diferente de null
          if(usu.isActivo()){ //si usuario es true es porque está activo
              //envio valores a cada variable de la clase publica Variables
              Variables.usuarioId=usu.getId(); //le envio lo que obtengo en el obj usu (el id)
              Variables.rolId=usu.getRolId(); 
              Variables.rolNombre=usu.getRolNombre(); 
              Variables.usuarioNombre=usu.getNombre();
              Variables.usuarioEmail=usu.getEmail();
              
              resp="1"; //Tiene acceso y está activo
          }else {
             resp="2"; //No está activo, sin acceso
          }
          
      }
      
      return resp; //retorno respuesta
      
  }
    */
  
  //Metodo estático privado de tipo String
 private static String encriptar(String valor){ 
        MessageDigest md; //obj de tipo MessageDigest para encriptar (provee hashing)
	try {
		md = MessageDigest.getInstance("SHA-256"); //algoritmo de encriptado
	} 
	catch (NoSuchAlgorithmException e) {		
		return null;
	}
	/*
        convierte el texto en un arreglo de bytes;
        md solo trabaja con datos en formato de bytes
        */

	byte[] hash = md.digest(valor.getBytes());
        //sb para convertir cada byte del hash
        //y construir la cadena final del hash en un formato legible
	StringBuilder sb = new StringBuilder();
	    
        //recorre cada byte del hash
	for(byte b : hash) {
                /*convierte cada byte en un formato hexadecimal de dos dígitos 
                y lo añade al sb
                asegura que cada byte se represente con dos caracteres hexadecimales
                */
		sb.append(String.format("%02x", b));
	}
	    
        //convierte el sb a una cadena y devuelve el resultado
        //representación hexadecimal del valor (64)
	return sb.toString(); 
 }
  
    public String insertar(int RolId, String nombre, String tipoDocumento, String numDocumento, String direccion, String telefono, String email, String clave){
        //si existe el email
        if (DATOS.existe(email)){
            //no registra
            return "El registro ya existe.";
            
        }else{ //si no existe le envio los valores a cada propiedad del objeto
            obj.setRolId(RolId);
            obj.setNombre(nombre);
            obj.setTipoDocumento(tipoDocumento);
            obj.setNumDocumento(numDocumento);
            obj.setDireccion(direccion);
            obj.setTelefono(telefono);
            obj.setEmail(email);
            
            //llamamos al metodo encriptar y enviamos la clave a encriptar
            obj.setClave(this.encriptar(clave));
            
             //envio el objeto a insertar de la capa DATOS
            if (DATOS.insertar(obj)){ //si es true
                return "OK"; //devuelvo OK al Front
            }else{
                return "Error en el registro.";
            }
        }
    }
    
    public String actualizar(int id,int RolId, String nombre, String tipoDocumento, String numDocumento, String direccion, String telefono, String email, String emailAnt, String clave){
        //si email que edita es igual anterior (no modifica)
        if (email.equals(emailAnt)){
            //envio los valores a las propiedades del objeto para actualizar
            obj.setId(id);
            obj.setRolId(RolId);
            obj.setNombre(nombre);
            obj.setTipoDocumento(tipoDocumento);
            obj.setNumDocumento(numDocumento);
            obj.setDireccion(direccion);
            obj.setTelefono(telefono);
            obj.setEmail(email);
           
            //almacena la clave del usuario
            String encriptado;
            //si la clave tiene longitud de 64, no cambió clave
            if(clave.length()==64){
                encriptado = clave;
    
            }else {//sino (cuando la cambia encriptamos esa nueva)
                encriptado=this.encriptar(clave);
                
            }
            //envía clave en forma original (si no cambió) o 
            //en su nueva versión encriptada
            obj.setClave(encriptado);
            
             //envio el objeto a editar de la capa DATOS
            if(DATOS.actualizar(obj)){ //si devuelve true
                return "OK"; //retorno OK al front
            }else{
                return "Error en la actualización.";
            }
            
        }else{//sino, es porque modifica el email
            if (DATOS.existe(email)){ //en caso de que exista
                return "El registro ya existe.";
            }else{
                obj.setId(id);
                obj.setRolId(RolId);
                obj.setNombre(nombre);
                obj.setTipoDocumento(tipoDocumento);
                obj.setNumDocumento(numDocumento);
                obj.setDireccion(direccion);
                obj.setTelefono(telefono);
                obj.setEmail(email);

                String encriptado;

                //si la clave tiene longitud de 64, no cambió clave
                if (clave.length() == 64) {//no modifica la clave
                    encriptado = clave;

                } else {//sino (nueva se encripta)
                    encriptado = this.encriptar(clave);

                }
                obj.setClave(encriptado);
                if (DATOS.actualizar(obj)){
                    return "OK";
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

