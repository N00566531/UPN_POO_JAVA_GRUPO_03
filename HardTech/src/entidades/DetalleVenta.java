
package entidades;


public class DetalleVenta {
    //Una venta puede tener uno o varios detalles (articulos)
    private int id;
    private int ventaId;
    private int articuloId;
    private String articuloCodigo;
    private String articuloNombre;
    private int articuloStock; //El Stock no debe superar a lo que existe al vender
    private int cantidad;
    private double precio;
    private double descuento;
    private double subTotal;

    //Constructores
    public DetalleVenta() {
    }

    public DetalleVenta(int id, int ventaId, int articuloId, String articuloCodigo, String articuloNombre, int articuloStock, int cantidad, double precio, double descuento, double subTotal) {
        this.id = id;
        this.ventaId = ventaId;
        this.articuloId = articuloId;
        this.articuloCodigo = articuloCodigo;
        this.articuloNombre = articuloNombre;
        this.articuloStock = articuloStock;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descuento = descuento;
        this.subTotal = subTotal;
    }

    public DetalleVenta(int articuloId, String articuloCodigo, String articuloNombre, int articuloStock, int cantidad, double precio, double descuento, double subTotal) {
        this.articuloId = articuloId;
        this.articuloCodigo = articuloCodigo;
        this.articuloNombre = articuloNombre;
        this.articuloStock = articuloStock;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descuento = descuento;
        this.subTotal = subTotal;
    }

    public DetalleVenta(int articuloId, int cantidad, double precio, double descuento) {
        this.articuloId = articuloId;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descuento = descuento;
    }
    
    //Get y Set

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVentaId() {
        return ventaId;
    }

    public void setVentaId(int ventaId) {
        this.ventaId = ventaId;
    }

    public int getArticuloId() {
        return articuloId;
    }

    public void setArticuloId(int articuloId) {
        this.articuloId = articuloId;
    }

    public String getArticuloCodigo() {
        return articuloCodigo;
    }

    public void setArticuloCodigo(String articuloCodigo) {
        this.articuloCodigo = articuloCodigo;
    }

    public String getArticuloNombre() {
        return articuloNombre;
    }

    public void setArticuloNombre(String articuloNombre) {
        this.articuloNombre = articuloNombre;
    }

    public int getArticuloStock() {
        return articuloStock;
    }

    public void setArticuloStock(int articuloStock) {
        this.articuloStock = articuloStock;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }
    
    
    
}
