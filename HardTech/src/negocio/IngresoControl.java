
package negocio;


import datos.ArticuloDAO;
import datos.IngresoDAO;
import entidades.Articulo;
import entidades.DetalleIngreso;
import entidades.Ingreso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/*import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;*/


public class IngresoControl {
    private final IngresoDAO DATOS; //Obj DATOS que instancia IngresoDAO
    private final ArticuloDAO DATOSART; //Obj DATOS que instancia ArticuloDAO
    private Ingreso obj;//Instancia clase Ingreso
    private DefaultTableModel modeloTabla;
    public int registrosMostrados;
    
    //Constructor
    public IngresoControl(){
        //Creo instancias
        this.DATOS=new IngresoDAO();
        this.DATOSART=new ArticuloDAO();
        this.obj=new Ingreso();
        this.registrosMostrados=0;
    }
    
  //Devuelve un DefaultTableModel
    public DefaultTableModel listar(String texto,int totalPorPagina,int numPagina){
        //Objeto lista para ingresos
        List<Ingreso> lista=new ArrayList();
        lista.addAll(DATOS.listar(texto,totalPorPagina,numPagina));
        
        //Inserto titulos de cabecera (campos de Ingreso)
        String[] titulos={"Id","Usuario ID","Usuario Nombre","Proveedor ID","Proveedor","Tipo Comprobante","Serie","Número","Fecha","Impuesto","Total","Estado"};
        this.modeloTabla=new DefaultTableModel(null,titulos);        
        
        
        String[] registro = new String[12];
        //Objeto sdf e instancio a la clase SimpleDateFormat; envio formato
        SimpleDateFormat sdf =new SimpleDateFormat("dd/MM/yyyy");
        
        this.registrosMostrados=0;
        //Recorro lista (teniendo en cuenta Ingreso)
        for (Ingreso item:lista){
         
            registro[0]=Integer.toString(item.getId());
            registro[1]=Integer.toString(item.getUsuarioId());
            registro[2]=item.getUsuarioNombre();
            registro[3]=Integer.toString(item.getPersonaId());
            registro[4]=item.getPersonaNombre();
            registro[5]=item.getTipoComprobante();
            registro[6]=item.getSerieComprobante();
            registro[7]=item.getNumComprobante();
            registro[8]=sdf.format(item.getFecha());
            registro[9]=Double.toString(item.getImpuesto());
            registro[10]=Double.toString(item.getTotal());
            registro[11]=item.getEstado();
           
            //Añado al modelo los registros obtenidos
            this.modeloTabla.addRow(registro);
            this.registrosMostrados=this.registrosMostrados+1;
        }
        return this.modeloTabla;
    }
    

    
   public DefaultTableModel listarDetalle(int id){ //Espera ID de ingreso
        //Cada item de la lista se va crear teniendo en cuenta clase DetalleIngreso
        List<DetalleIngreso> lista=new ArrayList();
        //invoco al met listarDetalle (Datos) y le envio el id de ingreso del cual quiero listar sus detalles
        lista.addAll(DATOS.listarDetalle(id));
        
        //cabecera del detalle
        String[] titulos={"ID","CÓDIGO","ARTÍCULO","CANTIDAD","PRECIO","SUBTOTAL"};
        //le agrego a la tabla los titulos creados
        this.modeloTabla=new DefaultTableModel(null,titulos);        
        
        String[] registro = new String[6]; //array registro de 6 indices
        
        //Recorrre DetalleIngreso
        for (DetalleIngreso item:lista){
            registro[0]=Integer.toString(item.getArticuloId());
            registro[1]=item.getArticuloCodigo();
            registro[2]=item.getArticuloNombre();
            registro[3]=Integer.toString(item.getCantidad());
            registro[4]=Double.toString(item.getPrecio());
            registro[5]=Double.toString(item.getSubTotal());  
            
            //Agrego al table model toda las filas del detalle
            this.modeloTabla.addRow(registro);
        }
        return this.modeloTabla;//Devuelvo modeloTabla
    }
   
  
    public Articulo obtenerArticuloCodigoIngreso(String codigo){
        //Objeto de tipo Articulo y llamo al met (de la clase ArticuloDAO)
        Articulo art=DATOSART.obtenerArticuloCodigoIngreso(codigo); //le envio codigo
        return art;//retorno objeto encontrado
    }
    
    //Parametros que se enviará a este metodo (se envian los detalles, un DefaultTableModel desde form)
    public String insertar(int personaId, String tipoComprobante, String serieComprobante, String numComprobante, double impuesto, double total, DefaultTableModel modeloDetalles){
         //Valido si existe serie y número
        if (DATOS.existe(serieComprobante,numComprobante)){
            return "El registro ya existe.";
        }else{//Sino envio valores al objeto
             //Usuario que registra el ingreso se guardará en clase variables; le envio ese valor a propiedad UsuarioId del objeto
            obj.setUsuarioId(Variables.usuarioId);
            obj.setPersonaId(personaId);
            obj.setTipoComprobante(tipoComprobante);
            obj.setSerieComprobante(serieComprobante);
            obj.setNumComprobante(numComprobante);
            obj.setImpuesto(impuesto);
            obj.setTotal(total);
            
            //Lista de detalles, cada elemento es un artículo agregado
            List<DetalleIngreso> detalles = new ArrayList();
             //Var para guardar valores del detalle
            int articuloId;
            int cantidad;
            double precio;
            
            //Recorro modeloDetalles de uno en uno
            for (int i=0;i<modeloDetalles.getRowCount();i++){
                
                //Guardo valores obtenidos del modeloDetalles (lista de objetos)
                articuloId=Integer.parseInt(String.valueOf(modeloDetalles.getValueAt(i, 0)));
                cantidad=Integer.parseInt(String.valueOf(modeloDetalles.getValueAt(i, 3)));
                precio=Double.parseDouble(String.valueOf(modeloDetalles.getValueAt(i, 4)));
                
                //Agrego valores a lista detalles y lo recibirá cons de 3 parametros
                detalles.add(new DetalleIngreso(articuloId,cantidad,precio));
            }
            
            //A obj Ingreso le envio lista de detalles
            obj.setDetalles(detalles);
            
            if (DATOS.insertar(obj)){
                return "OK";
            }else{
                return "Error en el registro.";
            }
        }
    }
    
  
    public String anular(int id){
        if (DATOS.anular(id)){
            return "OK";
        }else{
            return "No se puede anular el registro";
        }
    }
    
    public int total(){
        return DATOS.total();
    }
    
    public int totalMostrados(){
        return this.registrosMostrados;
    }
}
