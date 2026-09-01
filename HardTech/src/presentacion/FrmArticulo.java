
package presentacion;

import entidades.Categoria;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.TableRowSorter;
import negocio.ArticuloControl;

public class FrmArticulo extends javax.swing.JInternalFrame {
   
    private final ArticuloControl CONTROL;
    //Para controlar las acciones de guardar y editar
    private String accion; 
    //Para guardar nombre que se quiere editar
    private String nombreAnt; 
    
    private String rutaOrigen; //var para alm la ruta de donde selecciona imagen
    private String rutaDestino; //var para alm ruta a donde van las imagenes
    private final String DIRECTORIO="src/files/articulos/"; //carpeta de almacenamiento
    
    private String imagen="";
    private String imagenAnt; //var para almecenar el valor del indice 8 de la tabla
    
    //Var con valores por defecto
    private int totalPorPagina = 10;
    private int numPagina = 1;
    private boolean primeraCarga = true;
    private int totalRegistros;
  
    
    //Constructor
    public FrmArticulo() {
        initComponents();
        this.CONTROL=new ArticuloControl(); //inicializo objeto
        this.paginar();
        this.listar("",false);//que no pagine al inicio
        this.primeraCarga=false;
        tabGeneral.setEnabledAt(1, false);
        this.accion="guardar";
        txtId.setVisible(false);
        this.cargarCategorias(); //cargo el combo con la lista
    }
    
    //OCULTAR EL ID DE CATEGORIA
     private void ocultarColumnas(){ 
        tablaListado.getColumnModel().getColumn(1).setMaxWidth(0); //ancho max 0 
        tablaListado.getColumnModel().getColumn(1).setMinWidth(0); //ancho minimo 0
        tablaListado.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(0); //en la cabecera ancho max 0
        tablaListado.getTableHeader().getColumnModel().getColumn(1).setMinWidth(0); //ancho min 0
    }
     //Método calcula y configura el núm total de páginas necesarias para mostrar registros
     private void paginar(){
        //almacena total de páginas para mostrar registros
        int totalPaginas;  
        
        //obtener el total de registros de la tabla
        this.totalRegistros=this.CONTROL.total();
        
        //registros que por pagina debe mostrarse (convertir a entero)
        this.totalPorPagina=Integer.parseInt((String)cboTotalPorPagina.getSelectedItem());
        
        //totalRegistros / totalPorPagina; ceil para redondeo arriba; lo convierte en entero
        totalPaginas=(int)(Math.ceil((double)this.totalRegistros/this.totalPorPagina));
        
        //si el total es 0 (no hay registros)
        if (totalPaginas==0){
            //que sea 1 por lo menos
            totalPaginas=1;
        }
        cboNumPagina.removeAllItems(); //remuevo items por defecto del combo
        
        //recorre desde 1 hasta totalPaginas
        for (int i = 1; i <= totalPaginas; i++) {
            //En cada vuelta, convierte el núm de página (i) a String y lo agrega al combo
            cboNumPagina.addItem(Integer.toString(i));
        }
        //por defecto mostrará pag 0 (primera pagina)
        cboNumPagina.setSelectedIndex(0);
    }
    
     //mostrar lista de registros aplicando paginación
    private void listar(String texto, boolean paginar){
        //Lo que seleccione en el combo (registros mostrados por pagina)
        this.totalPorPagina=Integer.parseInt((String)cboTotalPorPagina.getSelectedItem());
        
        //si cboNumPagina no es null convierto su valor a entero y lo asigna a var
        if ((String)cboNumPagina.getSelectedItem()!=null){
            //representa la página específica que el usuario desea ver
            this.numPagina=Integer.parseInt((String)cboNumPagina.getSelectedItem());
        }
        
        if (paginar==true){//listar utiliza numPagina para mostrar registros de esa página
            //llama método listar con los parámetros
            tablaListado.setModel(this.CONTROL.listar(texto,this.totalPorPagina,this.numPagina));
            
        }else{
            //se pasa 1 en lugar de numPagina; muestraprimera página sin importar el valor de numPagina
            tablaListado.setModel(this.CONTROL.listar(texto,this.totalPorPagina,1));
        }
        
        
        TableRowSorter orden= new TableRowSorter(tablaListado.getModel());
        tablaListado.setRowSorter(orden);
        this.ocultarColumnas();
        lblTotalRegistros.setText("Mostrando " + this.CONTROL.totalMostrados() + " de un total de " + this.CONTROL.total() + " registros");
    }
    
    private void cargarCategorias(){
        //Obj que recibe lista de categorías (del método de la capa negocio)
        DefaultComboBoxModel items = this.CONTROL.seleccionar();
        //cargo al combo los items del modelo
        cboCategoria.setModel(items); 
    }
    
    //Método para copiar imagen desde una ubicación de origen a una de destino en el sistema
     private void subirImagenes(){
        //objetos de tipo File
        File origen=new File(this.rutaOrigen); //imagen en la ruta origen
        File destino=new File(this.rutaDestino); //imagen en al ruta destino
        try {
            //objeto para leer contenido desde origen 
            //lee bytes, para copiar archivos binarios como imágenes
            InputStream in= new FileInputStream(origen);
            
            //objeto para escribir contenido en destino
            //escribe bytes en el archivo de destino y crea copia del archivo
            OutputStream out=new FileOutputStream(destino);
            
            //array de bytes para almacenar recorrido desde origen hacia destino
            //para transferencia de datos en bloques
            byte[] buf = new byte[1024];
            
            // almacena número de bytes leídos en cada iteración
            int len; 
            
            //recorre contenido del archivo origen a destino
            //copiar los datos en fragmentos
            while ((len = in.read(buf)) > 0) { //asigna bytes leídos a len
                //Si len > 0, se han leído datos y el bucle continúa
                //si devuelve -1, while se detiene porque no hay datos para leer
                
                //escribe los bytes leídos en el archivo de destino
                // escribe desde el í 0 de buf hasta len bytes 
                //que son los datos leídos en la iteración actual
                out.write(buf, 0, len);
            }
            //cierro objetos
            in.close();
            out.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
     
      private void limpiar(){
        txtCodigo.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtId.setText("");
        txtPrecioVenta.setText("");
        txtStock.setText("");
        this.imagen="";
        this.imagenAnt="";
        lblImagen.setIcon(null);
        this.rutaDestino="";
        this.rutaOrigen="";
        this.accion="guardar";
    }
    
    private void mensajeError(String mensaje){
        JOptionPane.showMessageDialog(this, mensaje,"Sistema",JOptionPane.ERROR_MESSAGE);
    }
    
    private void mensajeOk(String mensaje){
        JOptionPane.showMessageDialog(this, mensaje,"Sistema",JOptionPane.INFORMATION_MESSAGE);
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabGeneral = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaListado = new javax.swing.JTable();
        lblTotalRegistros = new javax.swing.JLabel();
        btnNuevo = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        btnDesactivar = new javax.swing.JButton();
        btnActivar = new javax.swing.JButton();
        cboNumPagina = new javax.swing.JComboBox<>();
        cboTotalPorPagina = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        rptArticulos = new javax.swing.JButton();
        btnGenerarBarcode = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtDescripcion = new javax.swing.JTextArea();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cboCategoria = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtPrecioVenta = new javax.swing.JFormattedTextField();
        jLabel8 = new javax.swing.JLabel();
        txtStock = new javax.swing.JFormattedTextField();
        jLabel9 = new javax.swing.JLabel();
        lblImagen = new javax.swing.JLabel();
        btnAgregarImagen = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setTitle("Artículos");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setText("Nombre:");

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        tablaListado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tablaListado.setShowGrid(false);
        jScrollPane1.setViewportView(tablaListado);

        lblTotalRegistros.setText("Registros");

        btnNuevo.setText("Nuevo");
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        btnEditar.setText("Editar");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        btnDesactivar.setText("Desactivar");
        btnDesactivar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesactivarActionPerformed(evt);
            }
        });

        btnActivar.setText("Activar");
        btnActivar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActivarActionPerformed(evt);
            }
        });

        cboNumPagina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboNumPaginaActionPerformed(evt);
            }
        });

        cboTotalPorPagina.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "10", "20", "50", "100", "200", "500" }));
        cboTotalPorPagina.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTotalPorPaginaActionPerformed(evt);
            }
        });

        jLabel10.setText("# Página");

        jLabel11.setText("Total de registros por página ");

        rptArticulos.setText("Reporte Artículos");
        rptArticulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rptArticulosActionPerformed(evt);
            }
        });

        btnGenerarBarcode.setText("Generar BARCODE");
        btnGenerarBarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarBarcodeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnDesactivar, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnActivar, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTotalRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(47, 47, 47)
                        .addComponent(cboNumPagina, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(88, 88, 88)
                        .addComponent(jLabel11)
                        .addGap(35, 35, 35)
                        .addComponent(cboTotalPorPagina, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditar, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(rptArticulos, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGenerarBarcode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscar)
                    .addComponent(btnNuevo)
                    .addComponent(btnEditar)
                    .addComponent(rptArticulos)
                    .addComponent(btnGenerarBarcode))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboNumPagina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTotalPorPagina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDesactivar)
                    .addComponent(btnActivar))
                .addContainerGap())
        );

        tabGeneral.addTab("Listado", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setText("Nombre (*)");

        jLabel3.setText("Descripción");

        txtDescripcion.setColumns(20);
        txtDescripcion.setRows(5);
        jScrollPane2.setViewportView(txtDescripcion);

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        jLabel4.setText("(*) Indica que es un campo obligatorio.");

        jLabel5.setText("Categoría (*)");

        jLabel6.setText("Código");

        jLabel7.setText("Precio Venta (*)");

        txtPrecioVenta.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));

        jLabel8.setText("Stock (*)");

        txtStock.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        jLabel9.setText("Imagen");

        lblImagen.setBackground(new java.awt.Color(255, 255, 102));
        lblImagen.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btnAgregarImagen.setText("Agregar");
        btnAgregarImagen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarImagenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel2)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel4)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btnGuardar)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelar))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtCodigo, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cboCategoria, javax.swing.GroupLayout.Alignment.LEADING, 0, 205, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblImagen, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAgregarImagen))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(txtPrecioVenta, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE)
                        .addComponent(txtStock, javax.swing.GroupLayout.Alignment.LEADING)))
                .addContainerGap(490, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cboCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtPrecioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblImagen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnAgregarImagen)
                            .addComponent(jLabel9))
                        .addGap(0, 131, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(19, 19, 19)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCancelar))
                .addGap(21, 21, 21))
        );

        tabGeneral.addTab("Mantenimiento", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabGeneral)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabGeneral)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        this.listar(txtBuscar.getText(),false);
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        tabGeneral.setEnabledAt(1, true);
        tabGeneral.setEnabledAt(0, false);
        tabGeneral.setSelectedIndex(1);
        this.accion="guardar";
        btnGuardar.setText("Guardar");
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        //si se selecciona un registro a editar
        if (tablaListado.getSelectedRowCount()==1){
            //var para obtener el id articulo
            String id= String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),0));
            //var para almacenar el id categoria, obteniendolo de la tabla
            int categoriaId=Integer.parseInt(String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),1)));
            //var para obtener el  nombre de la cat
            String categoriaNombre=String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),2));
            //var para obtener el  codigo 
            String codigo=String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),3));
             //var para obtener el nombre y el nombre anterior
            String nombre= String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),4));
            this.nombreAnt= String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),4));
             //var para obtener el  precio_venta
            String precioVenta= String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),5));
            //Var para obtener el stock
            String stock= String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),6));
             //Var para obtener la descripcion
            String descripcion= String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),7));
              //Var para obtener valor del indice 8 (archivo actual)
            this.imagenAnt=String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),8));

            //envio los valores de la fila seleccionada a los controles respectivos
            txtId.setText(id);
            //obj de tipo Categoria; la instancio y le envio al constructor el id y el nombre
            Categoria seleccionado=new Categoria(categoriaId,categoriaNombre);
            //envio el item seleccionado del combo
            cboCategoria.setSelectedItem(seleccionado);
            txtCodigo.setText(codigo);
            txtNombre.setText(nombre);
            txtPrecioVenta.setText(precioVenta);
            txtStock.setText(stock);
            txtDescripcion.setText(descripcion);

            //Se crea un ImageIcon con la ruta completa
            ImageIcon im=new ImageIcon(this.DIRECTORIO+this.imagenAnt);
            Icon icono=new ImageIcon(im.getImage().getScaledInstance(lblImagen.getWidth(),lblImagen.getHeight(),Image.SCALE_DEFAULT));
            lblImagen.setIcon(icono);
            lblImagen.repaint();

            tabGeneral.setEnabledAt(0, false);
            tabGeneral.setEnabledAt(1, true);
            tabGeneral.setSelectedIndex(1);
            this.accion="editar";
            btnGuardar.setText("Editar");
        }else{
            this.mensajeError("Seleccione 1 registro a editar.");
        }
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnDesactivarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesactivarActionPerformed
        if (tablaListado.getSelectedRowCount() == 1) {
            String id= String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),0));
            String nombre= String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),4));

            if(JOptionPane.showConfirmDialog(this,"Deseas desactivar el registro: " + nombre + " ?", "Desactivar", JOptionPane.YES_NO_OPTION)==0){
                String resp=this.CONTROL.desactivar(Integer.parseInt(id));
                if (resp.equals("OK")){
                    this.mensajeOk("Registro desactivado");
                    this.listar("",false);
                }else{
                    this.mensajeError(resp);
                }
            }
        } else {
            this.mensajeError("Seleccione 1 registro a desactivar.");
        }
    }//GEN-LAST:event_btnDesactivarActionPerformed

    private void btnActivarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActivarActionPerformed
        if (tablaListado.getSelectedRowCount() == 1) {
            String id= String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),0));
            String nombre= String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),4));

            if(JOptionPane.showConfirmDialog(this,"Deseas activar el registro: " + nombre + " ?", "Activar", JOptionPane.YES_NO_OPTION)==0){
                String resp=this.CONTROL.activar(Integer.parseInt(id));
                if (resp.equals("OK")){
                    this.mensajeOk("Registro activado");
                    this.listar("",false);
                }else{
                    this.mensajeError(resp);
                }
            }
        } else {
            this.mensajeError("Seleccione 1 registro a activar.");
        }
    }//GEN-LAST:event_btnActivarActionPerformed

    private void cboNumPaginaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboNumPaginaActionPerformed
        if (this.primeraCarga==false){
            this.listar("",true);
        }
    }//GEN-LAST:event_cboNumPaginaActionPerformed

    private void cboTotalPorPaginaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTotalPorPaginaActionPerformed
        this.paginar();
    }//GEN-LAST:event_cboTotalPorPaginaActionPerformed

    private void rptArticulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rptArticulosActionPerformed
       // this.CONTROL.reporteArticulos();
    }//GEN-LAST:event_rptArticulosActionPerformed

    private void btnGenerarBarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarBarcodeActionPerformed
       // this.CONTROL.reporteArticulosBarras();
    }//GEN-LAST:event_btnGenerarBarcodeActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        //validamos cajas (obligatorias) no estén vacías
        if (txtNombre.getText().length()==0 || txtNombre.getText().length()>100){
            JOptionPane.showMessageDialog(this, "Debes ingresar un nombre y no debe ser mayor a 100 caracteres, es obligatorio.","Sistema", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }
        if (txtPrecioVenta.getText().length()==0){
            JOptionPane.showMessageDialog(this, "Debes ingresar un precio de venta, es obligatorio.","Sistema", JOptionPane.WARNING_MESSAGE);
            txtPrecioVenta.requestFocus();
            return;
        }
        if (txtStock.getText().length()==0){
            JOptionPane.showMessageDialog(this, "Debes ingresar un stock del artículo, es obligatorio.","Sistema", JOptionPane.WARNING_MESSAGE);
            txtStock.requestFocus();
            return;
        }
        if (txtDescripcion.getText().length()>255){
            JOptionPane.showMessageDialog(this, "Debes ingresar una descripción no mayor a 255 caracteres.","Sistema", JOptionPane.WARNING_MESSAGE);
            txtDescripcion.requestFocus();
            return;
        }
        
        //Almacena respuesta de capa negocio (exitoso o no) tras guardar o editar
        String resp;
        if (this.accion.equals("editar")){
            //Editar
            String imagenActual=""; //para almacenar img actual
            
            //si imagen es vacia (se mantiene la anterior)
            if (this.imagen.equals("")){
                //imagen actual es igual a la anterior
                imagenActual=this.imagenAnt;
                
            }else{
                //sino imagen actual es igual a la nueva
                imagenActual=this.imagen;
            }
            //obtiene ID de la cat seleccionada del combo (lo casteo)
            Categoria seleccionado = (Categoria)cboCategoria.getSelectedItem();
            //obtengo valores de las cajas y lo envia a actualizar de negocio
            resp=this.CONTROL.actualizar(Integer.parseInt(txtId.getText()),seleccionado.getId(),txtCodigo.getText(),txtNombre.getText(),this.nombreAnt,Double.parseDouble(txtPrecioVenta.getText()),Integer.parseInt(txtStock.getText()), txtDescripcion.getText(),imagenActual);
            //si negocio devuelve OK 
            if(resp.equals("OK")){
                //si la imagen no es vacía (selecciona nueva img)
                if (!this.imagen.equals("")){
                    this.subirImagenes();
                }
                this.mensajeOk("Actualizado correctamente");
                this.limpiar();
                this.listar("",false);
                tabGeneral.setSelectedIndex(0);
                tabGeneral.setEnabledAt(1, false);
                tabGeneral.setEnabledAt(0, true);
            }else{
                this.mensajeError(resp);
            }
        }else{
            //Guardar
            //obtiene ID de la cat seleccionada del combo (lo casteo)
            Categoria seleccionado = (Categoria)cboCategoria.getSelectedItem();
            //obtengo valores de las cajas y lo envia a insertar de negocio
            resp=this.CONTROL.insertar(seleccionado.getId(),txtCodigo.getText(),txtNombre.getText(),Double.parseDouble(txtPrecioVenta.getText()),Integer.parseInt(txtStock.getText()), txtDescripcion.getText(),this.imagen);
            //si negocio devuelve OK 
            if(resp.equals("OK")){
                //si imagen no es vacía, la subo al servidor
                if (!this.imagen.equals("")){
                    //llamo a met subir imagenes
                    this.subirImagenes();
                }
                this.mensajeOk("Registrado correctamente");
                this.limpiar();
                this.listar("",false);
                /*tabGeneral.setSelectedIndex(0);
                tabGeneral.setEnabledAt(1, false);
                tabGeneral.setEnabledAt(0, true);*/
            }else{
                this.mensajeError(resp);
            }
        }

    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        tabGeneral.setEnabledAt(0, true);
        tabGeneral.setEnabledAt(1, false);
        tabGeneral.setSelectedIndex(0);
        this.limpiar();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnAgregarImagenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarImagenActionPerformed
        
        //obj para seleccionar archivo del sistema (cuadro de dialogo)
        JFileChooser file = new JFileChooser();
        
        //Se muestra en este from; almacena si selecciona o no archivo
        int estado=file.showOpenDialog(this); 
        
        //si seleccionó archivo
        if (estado==JFileChooser.APPROVE_OPTION){
            
            //obtengo nombre del archivo
            this.imagen=file.getSelectedFile().getName();
            
            //guarda toda la ruta de imagen seleccionada
            this.rutaOrigen=file.getSelectedFile().getAbsolutePath();
            
            //almaceno el valor directorio + nombre de imagen
            this.rutaDestino=this.DIRECTORIO + this.imagen;
            
            //obj que representa una imagen; le envio la ruta origen
            ImageIcon im=new ImageIcon(this.rutaOrigen);
            
            //obj para crear icono; redimensiona imagen que se ajuste al label
            Icon icono=new ImageIcon(im.getImage().getScaledInstance(lblImagen.getWidth(),lblImagen.getHeight(),Image.SCALE_DEFAULT));
            
            //agrega al label la imagen que está en el icono
            lblImagen.setIcon(icono);
            
            //forzar imagen a mostrar
            lblImagen.repaint();
        }
    }//GEN-LAST:event_btnAgregarImagenActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActivar;
    private javax.swing.JButton btnAgregarImagen;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnDesactivar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnGenerarBarcode;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JComboBox<String> cboCategoria;
    private javax.swing.JComboBox<String> cboNumPagina;
    private javax.swing.JComboBox<String> cboTotalPorPagina;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblImagen;
    private javax.swing.JLabel lblTotalRegistros;
    private javax.swing.JButton rptArticulos;
    private javax.swing.JTabbedPane tabGeneral;
    private javax.swing.JTable tablaListado;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextArea txtDescripcion;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JFormattedTextField txtPrecioVenta;
    private javax.swing.JFormattedTextField txtStock;
    // End of variables declaration//GEN-END:variables
}
