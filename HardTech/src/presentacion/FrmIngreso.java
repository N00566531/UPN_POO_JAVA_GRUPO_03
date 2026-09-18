
package presentacion;

import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import negocio.IngresoControl;


public class FrmIngreso extends javax.swing.JInternalFrame {
    
    private final IngresoControl CONTROL;
    private String accion;
    private String nombreAnt;
    
    private int totalPorPagina = 10;
    private int numPagina = 1;
    private boolean primeraCarga = true;
    private int totalRegistros;
    
    //Objeto para almacenar los detalles del ingreso al almacen
    public DefaultTableModel modeloDetalles;
    //Objeto de tipo JFrame
    public JFrame contenedor;

    
    
    //Espera parametro llamado frmP que es el form principal
    public FrmIngreso(JFrame frmP) {
        initComponents();
        //Contenedor recibe form principal
        this.contenedor=frmP;
        
        //Instancio
        this.CONTROL=new IngresoControl();
        this.paginar();
        this.listar("",false);
        this.primeraCarga=false;
        tabGeneral.setEnabledAt(1, false);
        this.accion="guardar";
        this.crearDetalles();//llamo a met crear detalles
    }
    
    private void paginar(){
        int totalPaginas;
        
        this.totalRegistros=this.CONTROL.total();
        this.totalPorPagina=Integer.parseInt((String)cboTotalPorPagina.getSelectedItem());
        totalPaginas=(int)(Math.ceil((double)this.totalRegistros/this.totalPorPagina));
        if (totalPaginas==0){
            totalPaginas=1;
        }
        cboNumPagina.removeAllItems();
        
        for (int i = 1; i <= totalPaginas; i++) {
            cboNumPagina.addItem(Integer.toString(i));
        }
        cboNumPagina.setSelectedIndex(0);
    }
    
    private void listar(String texto, boolean paginar){
        this.totalPorPagina=Integer.parseInt((String)cboTotalPorPagina.getSelectedItem());
        if ((String)cboNumPagina.getSelectedItem()!=null){
            this.numPagina=Integer.parseInt((String)cboNumPagina.getSelectedItem());
        }
        
        if (paginar==true){
            tablaListado.setModel(this.CONTROL.listar(texto,this.totalPorPagina,this.numPagina));
        }else{
            tablaListado.setModel(this.CONTROL.listar(texto,this.totalPorPagina,1));
        }
        
        
        TableRowSorter orden= new TableRowSorter(tablaListado.getModel());
        tablaListado.setRowSorter(orden);
        this.ocultarColumnas();
        lblTotalRegistros.setText("Mostrando " + this.CONTROL.totalMostrados() + " de un total de " + this.CONTROL.total() + " registros");
    }
    
    private void ocultarColumnas(){
        //Oculto id del usuario
        tablaListado.getColumnModel().getColumn(1).setMaxWidth(0);
        tablaListado.getColumnModel().getColumn(1).setMinWidth(0);
        tablaListado.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(0);
        tablaListado.getTableHeader().getColumnModel().getColumn(1).setMinWidth(0);
        
        //Oculto el persona id
        tablaListado.getColumnModel().getColumn(3).setMaxWidth(0);
        tablaListado.getColumnModel().getColumn(3).setMinWidth(0);
        tablaListado.getTableHeader().getColumnModel().getColumn(3).setMaxWidth(0);
        tablaListado.getTableHeader().getColumnModel().getColumn(3).setMinWidth(0);
    }
    
    //Met para establecer estructura de tabla detalles
    //Al final el DefaultTableModel se lo paso a la tablaDetalles
    private void crearDetalles(){
        modeloDetalles = new DefaultTableModel(){
           //Sobreescribiendo metodos: DefaultTableModel tiene 3
            @Override
            //Met para editar celdas
            public boolean isCellEditable(int fila, int columna) { 
                //Si columna es CANTIDAD es editable
                if (columna==3){
                    return columna==3; //retorna 3; true
                }
                 //si columna es PRECIO es editable
                if (columna==4){
                    return columna==4;
                }
                //Sino es 3 ni 4 retorno, por defecto será 3
                return columna==3;
            }

            @Override
            //Met para obtener valores de la celda, teniendo en cuenta fila y columna
            public Object getValueAt(int row, int col) {
                //Si col es 5 (SUB TOTAL)
                if (col==5){
                    Double cantD;  //Var cantidad
                    try {
                        //Le asigno el valor (double) de columna CANTIDAD
                        cantD=Double.parseDouble((String)getValueAt(row, 3));
                    } catch (Exception e) {//Si no logro capturar cantidad
                        cantD=1.0; //Por defecto será 1
                    }
                    //Le asigno el valor (double) de columna PRECIO
                    Double precioD=Double.parseDouble((String)getValueAt(row, 4)); //var para precio
                    
                    //Valido que ambos valores no estén nulos
                    if (cantD!=null && precioD!=null){
                        //Retorno sub total
                        return String.format("%.2f",cantD*precioD);//2 decimales
                        //Clase String, met format (espera 2 par:formato y número de decimales)
                    }else{ //Si no obtengo ni precio ni cantidad ni subtotal
                        return 0; //retorno 0
                    }
                }
                
                return super.getValueAt(row, col);//Rretorno el valor teniendo en cuenta fila y colum
            }


            @Override
            //Met para establecer valor de celda teniendo en cuenta ese valor, la fila y columna
            public void setValueAt(Object aValue, int row, int col) {
                //Establezco valores a mostrar
                super.setValueAt(aValue, row, col);
                //Si modificamos precio o cantidad llamo a met calcular totales
                calcularTotales();
                //Met que estará pendiente a cualquier cambio en tabla
                fireTableDataChanged();
            }
        
        };
        
        //En estas columnas se podrá solo modificar la cantidad y el precio
        //Columnas de la tabla detalles
        modeloDetalles.setColumnIdentifiers(new Object[]{"ID","CÓDIGO","ARTÍCULO","CANTIDAD","PRECIO","SUBTOTAL"});
        //Le agrego a la tabla el modelo creado con las columnas
        tablaDetalles.setModel(modeloDetalles);
    }
    
    //Met público para acceder desde cualquier parte, desde el modal
    public void agregarDetalles(String id,String codigo, String nombre,String precio){
        
        String idT;
        boolean existe=false; //No existe todavía
        //Recorro modeloDetalles (cuenta sus filas), de 1 en 1
        for (int i = 0; i < this.modeloDetalles.getRowCount(); i++) {
            //Guardo el id obtenido de cada detalle
            idT=String.valueOf(this.modeloDetalles.getValueAt(i, 0));
            //Si el idT es igual al id (parametro recibido)
            if (idT.equals(id)){
                existe=true;//Entonces ya está registrado
            }
        }
        if (existe){ //Si existe es true
            this.mensajeError("El artículo ya ha sido agregado.");
        }
        else{//sino existe entonces lo agrego
            this.modeloDetalles.addRow(new Object[]{id,codigo,nombre,"1",precio,precio});
            this.calcularTotales(); //llamo al met calcular totales
        }
    }
    
    private void calcularTotales(){
        //Var total y subtotal
        double total=0;
        double subTotal;
        
        //Var items (almacena la cantidad de detalles del modeloDetalles)
        int items=modeloDetalles.getRowCount();
        //Si la cantidades de detalles es o
        if (items==0){
            total=0;
        }else{ //sino (tengo detalles)
            //recorro detalles hasta llegar a la cant items 
            for (int i = 0; i < items; i++) {
                //total = total + subtotal (columna 5 del modelo detalles) lo convierto a double
                total=total+Double.parseDouble(String.valueOf(modeloDetalles.getValueAt(i, 5)));
            }
        }
        //Calculo sub total
        subTotal=total/(1+Double.parseDouble(txtImpuesto.getText())); //lo convierto a double
        
        //Muestro resultados de salida
        txtTotal.setText(String.format("%.2f",total)); //formato de 2 decimales
        txtSubTotal.setText(String.format("%.2f",subTotal));
        txtTotalImpuesto.setText(String.format("%.2f",total-subTotal));
    }
       
    private void limpiar(){
        txtNombreProveedor.setText("");
        txtIdProveedor.setText("");
        txtSerieComprobante.setText("");
        txtNumComprobante.setText("");
        txtImpuesto.setText("0.18");

        this.accion="guardar";
        
        txtTotal.setText("0.00");
        txtSubTotal.setText("0.00");
        txtTotalImpuesto.setText("0.00");  
        //Llamo al met para reiniciar modelo detalles
        this.crearDetalles();
        btnGuardar.setVisible(true);
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
        btnDesactivar = new javax.swing.JButton();
        cboNumPagina = new javax.swing.JComboBox<>();
        cboTotalPorPagina = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        btnVerIngreso = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        txtIdProveedor = new javax.swing.JTextField();
        txtNombreProveedor = new javax.swing.JTextField();
        btnSeleccionarProveedor = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        txtImpuesto = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cboTipoComprobante = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        txtSerieComprobante = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtNumComprobante = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        btnVerArticulos = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tablaDetalles = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtSubTotal = new javax.swing.JTextField();
        txtTotalImpuesto = new javax.swing.JTextField();
        txtTotal = new javax.swing.JTextField();
        btnQuitar = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Ingresos Almacén");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setText("Nombre:");

        txtBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarActionPerformed(evt);
            }
        });

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

        btnDesactivar.setText("Anular");
        btnDesactivar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDesactivarActionPerformed(evt);
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

        btnVerIngreso.setText("Ver Ingreso");
        btnVerIngreso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerIngresoActionPerformed(evt);
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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(18, 18, 18)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 189, Short.MAX_VALUE)
                                .addComponent(btnVerIngreso, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(47, 47, 47)
                                .addComponent(cboNumPagina, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(88, 88, 88)
                                .addComponent(jLabel11)
                                .addGap(35, 35, 35)
                                .addComponent(cboTotalPorPagina, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnDesactivar, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTotalRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(70, 70, 70))))
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
                    .addComponent(btnVerIngreso))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboNumPagina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboTotalPorPagina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDesactivar)
                    .addComponent(lblTotalRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(44, Short.MAX_VALUE))
        );

        tabGeneral.addTab("Listado", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setText("Proveedor (*)");

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

        txtIdProveedor.setEditable(false);

        txtNombreProveedor.setEditable(false);

        btnSeleccionarProveedor.setText("...");
        btnSeleccionarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarProveedorActionPerformed(evt);
            }
        });

        jLabel3.setText("Impuesto (*)");

        txtImpuesto.setEditable(false);
        txtImpuesto.setText("0.18");

        jLabel5.setText("Tipo Comprobante (*)");

        cboTipoComprobante.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BOLETA", "FACTURA", "TICKET", "GUIA" }));

        jLabel6.setText("Serie Comprobante");

        jLabel7.setText("Número Comprobante (*):");

        jLabel8.setText("Artículo");

        txtCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCodigoActionPerformed(evt);
            }
        });
        txtCodigo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCodigoKeyReleased(evt);
            }
        });

        btnVerArticulos.setText("Ver");
        btnVerArticulos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerArticulosActionPerformed(evt);
            }
        });

        tablaDetalles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tablaDetalles);

        jLabel9.setText("Subtotal");

        jLabel12.setText("Total Impuesto");

        jLabel13.setText("Total");

        txtSubTotal.setEditable(false);

        txtTotalImpuesto.setEditable(false);

        txtTotal.setEditable(false);

        btnQuitar.setText("Quitar");
        btnQuitar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuitarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnGuardar)
                                .addGap(18, 18, 18)
                                .addComponent(btnCancelar)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtSubTotal)
                            .addComponent(txtTotalImpuesto)
                            .addComponent(txtTotal, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtIdProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtNombreProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnSeleccionarProveedor)
                            .addGap(145, 145, 145)
                            .addComponent(jLabel3)
                            .addGap(18, 18, 18)
                            .addComponent(txtImpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addGap(18, 18, 18)
                            .addComponent(cboTipoComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(20, 20, 20)
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtSerieComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(jLabel7)
                            .addGap(18, 18, 18)
                            .addComponent(txtNumComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addGap(18, 18, 18)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(btnVerArticulos)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnQuitar))
                        .addComponent(jScrollPane2)))
                .addContainerGap(101, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtIdProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombreProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSeleccionarProveedor)
                    .addComponent(jLabel3)
                    .addComponent(txtImpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cboTipoComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtSerieComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(txtNumComprobante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVerArticulos)
                    .addComponent(btnQuitar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtSubTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTotalImpuesto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel12))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(txtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4)
                        .addGap(19, 19, 19)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnGuardar)
                            .addComponent(btnCancelar))
                        .addGap(14, 14, 14))))
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

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        tabGeneral.setEnabledAt(0, true);
        tabGeneral.setEnabledAt(1, false);
        tabGeneral.setSelectedIndex(0);
        this.limpiar();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        //valido que las cajas no esten vacías o su cantidad de caracteres
        if (txtIdProveedor.getText().length()==0){
            JOptionPane.showMessageDialog(this, "Debes seleccionar un proveedor.","Sistema", JOptionPane.WARNING_MESSAGE);
            btnSeleccionarProveedor.requestFocus();
            return;
        }
        if (txtSerieComprobante.getText().length()>7){
            JOptionPane.showMessageDialog(this, "Debes ingresar una serie no mayor a 7 caracteres.","Sistema", JOptionPane.WARNING_MESSAGE);
            txtSerieComprobante.requestFocus();
            return;
        }
        if (txtNumComprobante.getText().length()==0 || txtNumComprobante.getText().length()>10){
            JOptionPane.showMessageDialog(this, "Debes ingresar un número de comprobante no mayor a 10 caracteres.","Sistema", JOptionPane.WARNING_MESSAGE);
            txtNumComprobante.requestFocus();
            return;
        }  
        //Debe tenerse por lo menos un detalle agregado
        if (modeloDetalles.getRowCount()==0){
            JOptionPane.showMessageDialog(this, "Debes agregar artículos al detalle.","Sistema", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String resp="";
        //Envío datos ingresados a capa negocio; los envio en orden, tbn el modelo detalles
        resp=this.CONTROL.insertar(Integer.parseInt(txtIdProveedor.getText()),(String)cboTipoComprobante.getSelectedItem(),txtSerieComprobante.getText(),txtNumComprobante.getText(),Double.parseDouble(txtImpuesto.getText()),Double.parseDouble(txtTotal.getText()),modeloDetalles);
        if (resp.equals("OK")) {
            this.mensajeOk("Registrado correctamente");
            this.limpiar();
            this.listar("", false);
            /*tabGeneral.setSelectedIndex(0);
                tabGeneral.setEnabledAt(1, false);
                tabGeneral.setEnabledAt(0, true);*/
        } else {
            this.mensajeError(resp);
        }       
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnDesactivarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDesactivarActionPerformed
        if (tablaListado.getSelectedRowCount() == 1) {
            //Capturo los datos a anular
            String id= String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),0));
            //Tipo, serie y núm de comprobante
            String comprobante= String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),5));
            String serie= String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),6));
            String numero= String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),7));
            
            //si la respuesa es si (YES)
            if(JOptionPane.showConfirmDialog(this,"¿Deseas anular el registro: " + comprobante+ " " + serie+ "-"+numero + " ?", "Anular", JOptionPane.YES_NO_OPTION)==0){
                //Llamo a anular y le envio el id de ingreso a anular
                String resp=this.CONTROL.anular(Integer.parseInt(id));
                //Si recibo de la capa de negocio un OK
                if (resp.equals("OK")){
                    this.mensajeOk("Registro anulado");
                    this.listar("",false);
                }else{
                    this.mensajeError(resp);
                }
            }
        } else {
            this.mensajeError("Seleccione 1 registro a desactivar.");
        }
    }//GEN-LAST:event_btnDesactivarActionPerformed

    private void cboNumPaginaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboNumPaginaActionPerformed
        if (this.primeraCarga==false){
            this.listar("",true);
        }
    }//GEN-LAST:event_cboNumPaginaActionPerformed

    private void cboTotalPorPaginaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTotalPorPaginaActionPerformed
        this.paginar();
    }//GEN-LAST:event_cboTotalPorPaginaActionPerformed

    
    
    private void btnSeleccionarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarProveedorActionPerformed

        //Le envio el principal, el FrmIngreso, y true para establecer el JDialog como vantana modal (no salir antes sin haber efectuado la operacion)
        FrmSeleccionarProveedorCompra frm=new FrmSeleccionarProveedorCompra(contenedor, this, true);
        frm.toFront();
    }//GEN-LAST:event_btnSeleccionarProveedorActionPerformed

    private void btnVerArticulosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerArticulosActionPerformed
      FrmSeleccionarArticuloCompra frm =new FrmSeleccionarArticuloCompra(contenedor, this, true);
      frm.toFront();
    }//GEN-LAST:event_btnVerArticulosActionPerformed
    
    
    //Captura tecla presionada
    private void txtCodigoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoKeyReleased
       //Si la caja no esta vacía
        if (txtCodigo.getText().length()>0){
            //Si tecla presionada es igual ENTER
            if (evt.getKeyCode()==KeyEvent.VK_ENTER){
                //creo obj art accediendo a entidades
                entidades.Articulo art;
                //llamo a CONTROL (capa negocio) y llamo al met y le envio lo que está en la caja del codigo
                art=this.CONTROL.obtenerArticuloCodigoIngreso(txtCodigo.getText());
                //si no existe el articulo
                if (art==null){
                    this.mensajeError("No existe un artículo con ese código");
                }else{ //de lo contrario agrego sus detalles (llamo el met agregarDetalles)
                    this.agregarDetalles(Integer.toString(art.getId()),art.getCodigo(),art.getNombre(),Double.toString(art.getPrecioVenta()));
                }
            }
            
        }else{
            this.mensajeError("Ingrese el código a buscar.");
        }
        
    }//GEN-LAST:event_txtCodigoKeyReleased

    private void btnQuitarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuitarActionPerformed
        //Si usuario selecciona un detalle que se quiere quitar
        if (tablaDetalles.getSelectedRowCount() == 1) {
            //Remover la fila seleccionada del modelo detalles
            this.modeloDetalles.removeRow(tablaDetalles.getSelectedRow());
            this.calcularTotales(); //Para actualizar los totales
        }else{
            this.mensajeError("Seleccione el detalle a quitar.");
        }
        
    }//GEN-LAST:event_btnQuitarActionPerformed

    private void btnVerIngresoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerIngresoActionPerformed
        //Valido que seleccione un registro de tabla
        if (tablaListado.getSelectedRowCount()==1){
            //Almaceno valores del detalle
            String id= String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),0));
            String idProveedor= String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),3));
            String nombreProveedor= String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),4));            
            String tipoComprobante=String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),5));
            String serie = String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),6));
            String numero=String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),7));
            String impuesto=String.valueOf(tablaListado.getValueAt(tablaListado.getSelectedRow(),9));
            
            //Valores pasados a las cajas
            txtIdProveedor.setText(idProveedor);
            txtNombreProveedor.setText(nombreProveedor);            
            cboTipoComprobante.setSelectedItem(tipoComprobante);
            txtSerieComprobante.setText(serie);
            txtNumComprobante.setText(numero); 
            txtImpuesto.setText(impuesto);
            
            //Modelo se llena con los detalles de ese ingreso especifico
            this.modeloDetalles=CONTROL.listarDetalle(Integer.parseInt(id));
            tablaDetalles.setModel(modeloDetalles);
            this.calcularTotales();
            
            tabGeneral.setEnabledAt(1, true);
            tabGeneral.setEnabledAt(0, false);
            tabGeneral.setSelectedIndex(1);
            btnGuardar.setVisible(false);
        }else{ //sino selecciona marca error
           this.mensajeError("Seleccione el ingreso a mostrar."); 
        }

    }//GEN-LAST:event_btnVerIngresoActionPerformed

    private void txtCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoActionPerformed

    private void txtBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBuscarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnDesactivar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnQuitar;
    private javax.swing.JButton btnSeleccionarProveedor;
    private javax.swing.JButton btnVerArticulos;
    private javax.swing.JButton btnVerIngreso;
    private javax.swing.JComboBox<String> cboNumPagina;
    private javax.swing.JComboBox<String> cboTipoComprobante;
    private javax.swing.JComboBox<String> cboTotalPorPagina;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
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
    private javax.swing.JLabel lblTotalRegistros;
    private javax.swing.JTabbedPane tabGeneral;
    private javax.swing.JTable tablaDetalles;
    private javax.swing.JTable tablaListado;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCodigo;
    public javax.swing.JTextField txtIdProveedor;
    private javax.swing.JTextField txtImpuesto;
    public javax.swing.JTextField txtNombreProveedor;
    private javax.swing.JTextField txtNumComprobante;
    private javax.swing.JTextField txtSerieComprobante;
    private javax.swing.JTextField txtSubTotal;
    private javax.swing.JTextField txtTotal;
    private javax.swing.JTextField txtTotalImpuesto;
    // End of variables declaration//GEN-END:variables
}
