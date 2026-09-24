package negocio;

import database.Conexion;
import datos.ArticuloDAO;
import datos.VentaDAO;
import entidades.Articulo;
import entidades.DetalleVenta;
import entidades.Venta;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;
import java.sql.Date;

public class VentaControl {

    private final VentaDAO DATOS; //Obj DATOS que instancia a clase IngresoDAO
    private final ArticuloDAO DATOSART;
    private Venta obj;//Instancio a clase Ingreso de capa entidades
    private DefaultTableModel modeloTabla;
    public int registrosMostrados;

    public VentaControl() {
        //creo las instancias
        this.DATOS = new VentaDAO();
        this.DATOSART = new ArticuloDAO();
        this.obj = new Venta();
        this.registrosMostrados = 0;
    }

    //Devuelve un DefaultTableModel
    public DefaultTableModel listar(String texto, int totalPorPagina, int numPagina) {
        List<Venta> lista = new ArrayList();
        lista.addAll(DATOS.listar(texto, totalPorPagina, numPagina));

        String[] titulos = {"Id", "Usuario ID", "Usuario Nombre", "Cliente ID", "Cliente", "Tipo Comprobante", "Serie", "Número", "Fecha", "Impuesto", "Total", "Estado"};
        this.modeloTabla = new DefaultTableModel(null, titulos);

        String[] registro = new String[12];
        //creo objeto e instancio a la clase SimpleDateFormat
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        this.registrosMostrados = 0;
        for (Venta item : lista) {

            registro[0] = Integer.toString(item.getId());
            registro[1] = Integer.toString(item.getUsuarioId());
            registro[2] = item.getUsuarioNombre();
            registro[3] = Integer.toString(item.getPersonaId());
            registro[4] = item.getPersonaNombre();
            registro[5] = item.getTipoComprobante();
            registro[6] = item.getSerieComprobante();
            registro[7] = item.getNumComprobante();
            registro[8] = sdf.format(item.getFecha());
            registro[9] = Double.toString(item.getImpuesto());
            registro[10] = Double.toString(item.getTotal());
            registro[11] = item.getEstado();

            this.modeloTabla.addRow(registro);
            this.registrosMostrados = this.registrosMostrados + 1;
        }
        return this.modeloTabla;
    }

    public DefaultTableModel listarDetalle(int id) {
        List<DetalleVenta> lista = new ArrayList();
        lista.addAll(DATOS.listarDetalle(id));

        //cabecera del detalle
        String[] titulos = {"ID", "CÓDIGO", "ARTÍCULO", "STOCK", "CANTIDAD", "PRECIO", "DESCUENTO", "SUBTOTAL"};
        this.modeloTabla = new DefaultTableModel(null, titulos);

        String[] registro = new String[8]; //array de 6 indices

        for (DetalleVenta item : lista) { //recorro detalleingreso
            registro[0] = Integer.toString(item.getArticuloId());
            registro[1] = item.getArticuloCodigo();
            registro[2] = item.getArticuloNombre();
            registro[3] = Integer.toString(item.getArticuloStock());
            registro[4] = Integer.toString(item.getCantidad());
            registro[5] = Double.toString(item.getPrecio());
            registro[6] = Double.toString(item.getDescuento());
            registro[7] = Double.toString(item.getSubTotal());
            //agrego al default table model los detalles
            this.modeloTabla.addRow(registro);
        }
        return this.modeloTabla;//devuelvo
    }

    public Articulo obtenerArticuloCodigoVenta(String codigo) {
        //Objeto de tipo Articulo y llamo al met (de la clase ArticuloDAO)
        Articulo art = DATOSART.obtenerArticuloCodigoVenta(codigo); //le envio la var codigo
        return art;//retorno objeto
    }

    //Parametros que se enviará a este metodo
    public String insertar(int personaId, String tipoComprobante, String serieComprobante, String numComprobante, double impuesto, double total, DefaultTableModel modeloDetalles) {
        //valido si existe con la serie y el número (metodo existe espera estos dos par)
        if (DATOS.existe(serieComprobante, numComprobante)) {
            return "El registro ya existe.";
        } else {
            //el usuario que registra el ingreso se guardará en clase variables; le envio ese valor a propiedad a propiedad UsuarioId del objeto
            obj.setUsuarioId(Variables.usuarioId);
            obj.setPersonaId(personaId);
            obj.setTipoComprobante(tipoComprobante);
            obj.setSerieComprobante(serieComprobante);
            obj.setNumComprobante(numComprobante);
            obj.setImpuesto(impuesto);
            obj.setTotal(total);

            //Creo obj que será el arraylist, cada elemento de la lista será teniendo en cuenta la entidad DetalleIngreso
            List<DetalleVenta> detalles = new ArrayList();
            //Envio de valores al arraylist detalles
            int articuloId;
            int cantidad;
            double precio;
            double descuento;

            //recorro todo el defaulttablemodel modeloDetalles, de uno en uno
            for (int i = 0; i < modeloDetalles.getRowCount(); i++) {

                //var articuloId será igual a lo que obtengo del modelo en su fila 0  
                articuloId = Integer.parseInt(String.valueOf(modeloDetalles.getValueAt(i, 0)));
                cantidad = Integer.parseInt(String.valueOf(modeloDetalles.getValueAt(i, 4)));
                precio = Double.parseDouble(String.valueOf(modeloDetalles.getValueAt(i, 5)));
                descuento = Double.parseDouble(String.valueOf(modeloDetalles.getValueAt(i, 6)));

                //le agrego los valores al arraylist detalles
                detalles.add(new DetalleVenta(articuloId, cantidad, precio, descuento));
            }

            //al arraylist detalles (del obj ingreso) le envio ese arraylist creado
            obj.setDetalles(detalles);

            if (DATOS.insertar(obj)) {
                return "OK";
            } else {
                return "Error en el registro.";
            }
        }
    }

    public String anular(int id) {
        if (DATOS.anular(id)) {
            return "OK";
        } else {
            return "No se puede anular el registro";
        }
    }

    public int total() {
        return DATOS.total();
    }

    public int totalMostrados() {
        return this.registrosMostrados;
    }

    public void reporteComprobante(String idventa) {
        Map p = new HashMap();
        p.put("idventa", idventa);
        JasperReport report;
        JasperPrint print;

        Conexion cnn = Conexion.getInstancia();

        try {
            report = JasperCompileManager.compileReport(new File("").getAbsolutePath()
                    + "/src/reportes/RptComprobante.jrxml");
            print = JasperFillManager.fillReport(report, p, cnn.conectar());
            JasperViewer view = new JasperViewer(print, false);
            view.setTitle("Reporte de Ventas");
            view.setVisible(true);
        } catch (JRException e) {
            e.getMessage();
        }
    }

    public String ultimoSerie(String tipoComprobante) {
        return this.DATOS.ultimoSerie(tipoComprobante);
    }

    public String ultimoNumero(String tipoComprobante, String serieComprobante) {
        return this.DATOS.ultimoNumero(tipoComprobante, serieComprobante);
    }

    public DefaultTableModel consultaFechas(Date fechaInicio, Date fechaFin) {
        List<Venta> lista = new ArrayList();
        lista.addAll(DATOS.consultaFechas(fechaInicio, fechaFin));

        String[] titulos = {"Id", "Usuario ID", "Usuario", "Cliente ID", "Cliente", "Tipo Comprobante", "Serie", "Número", "Fecha", "Impuesto", "Total", "Estado"};
        this.modeloTabla = new DefaultTableModel(null, titulos);

        String[] registro = new String[12];
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        this.registrosMostrados = 0;
        for (Venta item : lista) {
            registro[0] = Integer.toString(item.getId());
            registro[1] = Integer.toString(item.getUsuarioId());
            registro[2] = item.getUsuarioNombre();
            registro[3] = Integer.toString(item.getPersonaId());
            registro[4] = item.getPersonaNombre();
            registro[5] = item.getTipoComprobante();
            registro[6] = item.getSerieComprobante();
            registro[7] = item.getNumComprobante();
            registro[8] = sdf.format(item.getFecha());
            registro[9] = Double.toString(item.getImpuesto());
            registro[10] = Double.toString(item.getTotal());
            registro[11] = item.getEstado();

            this.modeloTabla.addRow(registro);
            this.registrosMostrados = this.registrosMostrados + 1;
        }
        return this.modeloTabla;
    }

}
