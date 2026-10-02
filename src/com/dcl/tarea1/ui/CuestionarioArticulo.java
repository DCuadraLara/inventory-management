package com.dcl.tarea1.ui;

import com.dcl.tarea1.model.Articulo;

import java.awt.Color;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;

/**
 * Gestiona todo el funcionamiento del cuestionario para la creación de
 * artículos, además maneja toda la UI, su funcionamiento y su estilo.
 *
 * Contiene todos los eventos y listeners asociados a su JDialog, además de sus
 * JOptionPane para los eventos necesarios.
 *
 * @author David Cuadra Lara
 * @version 1.6
 */
public class CuestionarioArticulo extends javax.swing.JDialog {

    private final Date fechaInicial;
    private JComponent primerCampoConError; // Foco de error.

    private final Color TEXTO_DEFAULT = Color.BLACK;
    private final Color CELDA_DEFAULT = Color.WHITE;

    public CuestionarioArticulo(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        setIconImage(new ImageIcon(
                getClass().getResource("/resources/IconHogar.png")
        ).getImage());

        cambiarFormatoSpinner(); // dd/MM/yyyy

        // Asignamos valor fecha inicial para validar si tiene cambios luego.
        fechaInicial = (Date) spFechaAct.getValue();
    }

    /**
     * Método para cambiar el formato del JSpinner a dd/MM/yyyy.
     *
     */
    private void cambiarFormatoSpinner() {
        JSpinner.DateEditor tempSpinner
                = new JSpinner.DateEditor(spFechaAct, "dd/MM/yyyy");

        spFechaAct.setEditor(tempSpinner);
    }

    /**
     * Método para validar el Articulo entrante del formulario, manejo de
     * errores e impresión en pantalla con un JOptionPane.
     *
     * @param articulo Articulo
     */
    private void validarFormulario() {

        limpiarErrores();
        primerCampoConError = null;

        StringBuilder errores = new StringBuilder();

        Articulo articulo = obtenerDatosFormulario();

        // Validación código
        if (articulo.getCodigo().isBlank()) {
            errores.append("El campo código no puede estar vacío.\n");
            marcarErrorCelda(txtCodigo);
        } else if (articulo.getCodigo().length() > 8) {
            errores.append("El campo código no puede ser de longitud mayor a 8 carácteres.\n");
            marcarErrorCelda(txtCodigo);
        }

        // Validación denominación
        if (articulo.getDenominacion().isBlank()) {
            errores.append("El campo denominación no puede estar vacío.\n");
            marcarErrorCelda(txtDenominacion);
        } else if (articulo.getDenominacion().length() > 25) {
            errores.append("El campo denominación no puede ser de longitud mayor a 25 carácteres.\n");
            marcarErrorCelda(txtDenominacion);
        }

        // Validación marca
        if (articulo.getMarca().isBlank()) {
            errores.append("El campo marca no puede estar vacío.\n");
            marcarErrorCelda(txtMarca);
        }

        // Validación gama
        if (cmbGama.getSelectedIndex() == 0) {
            errores.append("Seleccione una gama válida.\n");
            marcarErrorCelda(cmbGama);
        }

        // Validación tipo
        if (articulo.getTipo().isBlank()) {
            errores.append("Seleccione un tipo válido.\n");
            marcarErrorCelda(cmbTipo);
        }

        // Validación eficiencia energética
        if (cmbEficienciaEnergia.getSelectedIndex() == 0) {
            errores.append("Seleccione una eficiencia energética válida.\n");
            marcarErrorCelda(cmbEficienciaEnergia);
        }

        // Validación precioUnd
        if (articulo.getPrecioUnd().isBlank()) {
            errores.append("El campo precio no puede estar vacío.\n");
            marcarErrorCelda(txtPrecioUnd);
        } else {
            try {
                double precio = Double.parseDouble(articulo.getPrecioUnd());

                if (precio <= 0 || precio > 10000) {
                    errores.append("El campo precioUnd no puede ser negativo ni superar 10000.\n");
                    marcarErrorCelda(txtPrecioUnd);
                }
            } catch (NumberFormatException e) {
                errores.append("El campo precioUnd tiene que contener un precio válido.\n");
                marcarErrorCelda(txtPrecioUnd);
            }
        }

        // Validación stock
        if (articulo.getStock().isBlank()) {
            errores.append("El campo stock no puede estar vacío.\n");
            marcarErrorCelda(txtStock);
        } else {
            try {
                int stock = Integer.parseInt(articulo.getStock());

                if (stock <= 0) {
                    errores.append("El campo stock no puede ser negativo o 0.\n");
                    marcarErrorCelda(txtStock);
                }
            } catch (NumberFormatException e) {
                errores.append("El campo stock debe de contener un número válido.\n");
                marcarErrorCelda(txtStock);
            }
        }

        // Validación garantía
        if (btngGarantia.getSelection() == null) {
            errores.append("Selecciona una garantía.\n");

            marcarErrorTexto(
                    rbtnLegal,
                    rbtnAmpliada,
                    rbtnSegundaMano
            );
        }

        // Validación estado
        if (btngEstado.getSelection() == null) {
            errores.append("Seleccione un estado.\n");

            marcarErrorTexto(
                    rbtnNuevo,
                    rbtnOferta,
                    rbtnReacondicionado
            );
        }

        // Validación servicios
        if (!serviciosSeleccionados()) {
            errores.append("Seleccione al menos un servicio.\n");
            
            marcarErrorTexto(
                    chkEntrega,
                    chkInstalacionMontaje,
                    chkRecogida,
                    chkRetiradaUsado
            );
        }

        // Si errores tiene de longitud > 0 tiene errores, por tanto los muestra.
        if (errores.length() > 0) {
            JOptionPane.showMessageDialog(
                    this,
                    errores.toString(),
                    "Errores",
                    JOptionPane.ERROR_MESSAGE
            );

            if (primerCampoConError != null) {
                primerCampoConError.requestFocusInWindow();
            }

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Registro guardado",
                    "Confirmación guardado",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Mostrar JOptionPane con articulo creado y datos.
            mostrarArticuloGuardado(articulo);

            // Volvemos a poner el background blanco.
            limpiarErrores();
        }
    }

    /**
     * Método encargado de marcar las celdas erróneas.
     *
     * @param componentes Array de componentes JComponent
     */
    private void marcarErrorCelda(JComponent... componentes) {

        for (JComponent componente : componentes) {
            componente.setBackground(new Color(255, 220, 220));

            if (primerCampoConError == null) {
                primerCampoConError = componente;
            }
        }

    }

    /**
     * Método encargado de cambiar el foreground al texto para visualizar el
     * error.
     *
     * @param componentes Array de componentes JComponent
     */
    private void marcarErrorTexto(JComponent... componentes) {

        for (JComponent componente : componentes) {
            componente.setForeground(Color.red);

            if (primerCampoConError == null) {
                primerCampoConError = componente;
            }
        }

    }

    /**
     * Método encargado de limpiar las celdas a su color default.
     *
     * @param componentes Array de componentes JComponent
     */
    private void limpiarErrorCelda(JComponent... componentes) {

        for (JComponent componente : componentes) {
            componente.setBackground(CELDA_DEFAULT);
        }
    }

    /**
     * Método encargado de limpiar el foreground del texto a su valor default.
     *
     * @param componentes Array de componentes JComponent
     */
    private void limpiarErrorTexto(JComponent... componentes) {

        for (JComponent componente : componentes) {
            componente.setForeground(TEXTO_DEFAULT);
        }
    }

    /**
     * Método encargado de pasar los componentes a la limpieza de errores,
     * resetea todos los formatos a default para la siguiente lectura.
     *
     */
    private void limpiarErrores() {

        limpiarErrorCelda(
                txtCodigo,
                txtDenominacion,
                txtMarca,
                cmbGama,
                cmbTipo,
                cmbEficienciaEnergia,
                txtPrecioUnd,
                txtStock,
                spFechaAct,
                txtDescripcion
        );

        limpiarErrorTexto(rbtnLegal,
                rbtnAmpliada,
                rbtnSegundaMano,
                rbtnNuevo,
                rbtnOferta,
                rbtnReacondicionado,
                chkEntrega,
                chkInstalacionMontaje,
                chkRecogida,
                chkRetiradaUsado
        );
    }

    /**
     * Método encargado de limpiar el formulario. Pone todos los datos en su
     * valor default.
     *
     */
    private void limpiarFormulario() {
        txtCodigo.setText("");
        txtDenominacion.setText("");
        txtMarca.setText("");

        cmbGama.setSelectedIndex(0);
        cmbTipo.setModel(new DefaultComboBoxModel<>());
        cmbTipo.setEnabled(false);
        cmbEficienciaEnergia.setSelectedIndex(0);

        txtPrecioUnd.setText("");
        txtStock.setText("");
        spFechaAct.setValue(new Date());

        btngGarantia.clearSelection();
        btngEstado.clearSelection();

        chkEntrega.setSelected(false);
        chkInstalacionMontaje.setSelected(false);
        chkRecogida.setSelected(false);
        chkRetiradaUsado.setSelected(false);

        txtDescripcion.setText("");

        limpiarErrores();
    }

    /**
     * Método encargado de mostrar todos los artículos guardados en un
     * JTextArea. No se trabaja con persistencia de datos, todo es temporal para
     * visualizar una UI final y poder continuar con el trabajo.
     *
     * @param articulo Articulo articulo
     */
    private void mostrarArticuloGuardado(Articulo articulo) {

        JTextArea texto = new JTextArea();

        texto.setEditable(false);
        texto.setBackground(Color.white);

        texto.append("=================================\n");
        texto.append("Código: " + articulo.getCodigo() + "\n");
        texto.append("Denominación: " + articulo.getDenominacion() + "\n");
        texto.append("Marca: " + articulo.getMarca() + "\n");
        texto.append("=================================\n");
        texto.append("Gama: " + articulo.getGama() + "\n");
        texto.append("Tipo: " + articulo.getTipo() + "\n");
        texto.append("Eficiencia energética: "
                + articulo.getEficienciaEnergetica() + "\n");
        texto.append("=================================\n");
        texto.append("Precio und: " + articulo.getPrecioUnd() + "\n");
        texto.append("Stock: " + articulo.getStock() + "\n");
        texto.append("Fecha: " + articulo.getFecha() + "\n");
        texto.append("=================================\n");
        texto.append("Garantía: " + articulo.getGarantia() + "\n");
        texto.append("Estado: " + articulo.getEstado() + "\n");

        texto.append("=================================\n");
        texto.append("Servicios:\n");
        texto.append(articulo.getServicios());

        texto.append("=================================\n");
        texto.append("Descripción:\n");
        texto.append(articulo.getDescripcion() + "\n");
        texto.append("=================================\n");

        JOptionPane.showMessageDialog(
                this,
                texto,
                "Datos guardados por el formulario. No se almacenan, es una maqueta.",
                JOptionPane.INFORMATION_MESSAGE
        );

        limpiarFormulario();
    }

    /**
     * Método encargado de la confirmación de salida.
     *
     */
    private void confirmarSalida() {

        if (!hayDatosIntroducidos()) {
            dispose(); // No cierra la App, devuelve a la anterior pantalla.
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que quieres salir?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    /**
     * Método para revisar si hay algún servicio seleccionado, mínimo uno.
     *
     * @return Boolean
     */
    private boolean serviciosSeleccionados() {

        if (chkEntrega.isSelected()) {
            return true;
        } else if (chkInstalacionMontaje.isSelected()) {
            return true;
        } else if (chkRecogida.isSelected()) {
            return true;
        } else if (chkRetiradaUsado.isSelected()) {
            return true;
        }

        return false;
    }

    /**
     * Método encargado de revisar que hay algún dato modificado, para la
     * confirmación de la salida de la App.
     *
     * @return Boolean
     */
    private boolean hayDatosIntroducidos() {

        return !txtCodigo.getText().trim().isEmpty()
                || !txtDenominacion.getText().trim().isEmpty()
                || !txtMarca.getText().trim().isEmpty()
                || cmbGama.getSelectedIndex() != 0
                || !obtenerTipo().isEmpty()
                || cmbEficienciaEnergia.getSelectedIndex() != 0
                || !txtPrecioUnd.getText().trim().isEmpty()
                || !txtStock.getText().trim().isEmpty()
                || !spFechaAct.getValue().equals(fechaInicial)
                || !obtenerEstadoArticulo().isEmpty()
                || !obtenerGarantia().isEmpty()
                || !obtenerServicios().isEmpty()
                || !txtDescripcion.getText().trim().isEmpty();
    }

    /**
     * Método para obtener los datos del formulario y devolver un Articulo.
     *
     * @return Articulo
     */
    private Articulo obtenerDatosFormulario() {

        return new Articulo(
                txtCodigo.getText().trim(),
                txtDenominacion.getText().trim(),
                txtMarca.getText().trim(),
                obtenerGama(),
                obtenerTipo(),
                cmbEficienciaEnergia.getSelectedItem().toString(),
                txtPrecioUnd.getText().trim(),
                txtStock.getText().trim(),
                obtenerFecha(),
                obtenerGarantia(),
                obtenerEstadoArticulo(),
                obtenerServicios(),
                txtDescripcion.getText().trim()
        );
    }

    /**
     * Método encargado de obtener el campo de garantía seleccionado.
     *
     * @return String garantia
     */
    private String obtenerGarantia() {
        if (rbtnLegal.isSelected()) {
            return rbtnLegal.getText();
        }

        if (rbtnAmpliada.isSelected()) {
            return rbtnAmpliada.getText();
        }

        if (rbtnSegundaMano.isSelected()) {
            return rbtnSegundaMano.getText();
        }

        return "";
    }

    /**
     * Método encargado de obtener el campo de estado seleccionado.
     *
     * @return String estado
     */
    private String obtenerEstadoArticulo() {
        if (rbtnNuevo.isSelected()) {
            return rbtnNuevo.getText();
        }

        if (rbtnOferta.isSelected()) {
            return rbtnOferta.getText();
        }

        if (rbtnReacondicionado.isSelected()) {
            return rbtnReacondicionado.getText();
        }

        return "";
    }

    /**
     * Método encargado de devolver en formato String la fecha seleccionada.
     *
     * @return
     */
    private String obtenerFecha() {
        JSpinner.DateEditor editor
                = (JSpinner.DateEditor) spFechaAct.getEditor();

        return editor.getTextField().getText();
    }

    /**
     * Método para capturar NullPointerException en la conversion de cmb a
     * String.
     *
     * @param comboBox cmb.
     * @return String gama.
     */
    private String obtenerGama() {
        try {
            String gama = cmbGama.getSelectedItem().toString();

            return gama;
        } catch (NullPointerException e) {
            return "";
        }
    }

    /**
     * Método para capturar NullPointerException en la conversion de cmb a
     * String.
     *
     * @param comboBox cmb.
     * @return String tipo.
     */
    private String obtenerTipo() {
        try {
            String tipo = cmbTipo.getSelectedItem().toString();

            return tipo;
        } catch (NullPointerException e) {
            return "";
        }
    }

    /**
     * Método encargado de obtener los campos seleccionados en servicio. Maneja
     * un StringBuilder para construir un String con todos ellos.
     *
     * @return String servicios
     */
    private String obtenerServicios() {

        StringBuilder servicios = new StringBuilder();

        if (chkEntrega.isSelected()) {
            servicios.append(chkEntrega.getText()).append("\n");
        }

        if (chkInstalacionMontaje.isSelected()) {
            servicios.append(chkInstalacionMontaje.getText()).append("\n");
        }

        if (chkRecogida.isSelected()) {
            servicios.append(chkRecogida.getText()).append("\n");
        }

        if (chkRetiradaUsado.isSelected()) {
            servicios.append(chkRetiradaUsado.getText()).append("\n");
        }

        return servicios.toString();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btngGarantia = new javax.swing.ButtonGroup();
        btngEstado = new javax.swing.ButtonGroup();
        pnlPrincipal = new javax.swing.JPanel();
        lpIdentificacion = new javax.swing.JLayeredPane();
        lblCodigo = new javax.swing.JLabel();
        lblDenominacion = new javax.swing.JLabel();
        lblMarca = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        txtDenominacion = new javax.swing.JTextField();
        txtMarca = new javax.swing.JTextField();
        lpClasificacion = new javax.swing.JLayeredPane();
        lblGama = new javax.swing.JLabel();
        lblTipo = new javax.swing.JLabel();
        lblEficienciaEnergia = new javax.swing.JLabel();
        cmbGama = new javax.swing.JComboBox<>();
        cmbTipo = new javax.swing.JComboBox<>();
        cmbEficienciaEnergia = new javax.swing.JComboBox<>();
        lpPrecioystock = new javax.swing.JLayeredPane();
        lblPrecioUnd = new javax.swing.JLabel();
        lblStock = new javax.swing.JLabel();
        lblFechaEntrada = new javax.swing.JLabel();
        txtPrecioUnd = new javax.swing.JTextField();
        txtStock = new javax.swing.JTextField();
        spFechaAct = new javax.swing.JSpinner();
        lpGarantia = new javax.swing.JLayeredPane();
        rbtnLegal = new javax.swing.JRadioButton();
        rbtnAmpliada = new javax.swing.JRadioButton();
        rbtnSegundaMano = new javax.swing.JRadioButton();
        lpEstado = new javax.swing.JLayeredPane();
        rbtnNuevo = new javax.swing.JRadioButton();
        rbtnOferta = new javax.swing.JRadioButton();
        rbtnReacondicionado = new javax.swing.JRadioButton();
        lpServicioVenta = new javax.swing.JLayeredPane();
        chkRecogida = new javax.swing.JCheckBox();
        chkEntrega = new javax.swing.JCheckBox();
        chkInstalacionMontaje = new javax.swing.JCheckBox();
        chkRetiradaUsado = new javax.swing.JCheckBox();
        lpDescripcion = new javax.swing.JLayeredPane();
        spDescripcion = new javax.swing.JScrollPane();
        txtDescripcion = new javax.swing.JTextArea();
        sepPrincipal = new javax.swing.JSeparator();
        btnReset = new javax.swing.JButton();
        btnSalir = new javax.swing.JButton();
        btnGuardar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Alta de Artículo - ElectroHogar");
        setBackground(new java.awt.Color(204, 204, 204));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        pnlPrincipal.setBackground(new java.awt.Color(140, 181, 186));

        lpIdentificacion.setBackground(new java.awt.Color(204, 204, 204));
        lpIdentificacion.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(102, 102, 102), new java.awt.Color(102, 102, 102)), "Identificación", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Historic", 0, 12), new java.awt.Color(0, 51, 51))); // NOI18N
        lpIdentificacion.setToolTipText("");
        lpIdentificacion.setPreferredSize(new java.awt.Dimension(350, 120));

        lblCodigo.setBackground(new java.awt.Color(255, 255, 255));
        lblCodigo.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        lblCodigo.setForeground(new java.awt.Color(51, 51, 51));
        lblCodigo.setText("Código:");
        lblCodigo.setToolTipText("Código");

        lblDenominacion.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        lblDenominacion.setForeground(new java.awt.Color(51, 51, 51));
        lblDenominacion.setText("Denominación");
        lblDenominacion.setToolTipText("Denominación");

        lblMarca.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        lblMarca.setForeground(new java.awt.Color(51, 51, 51));
        lblMarca.setText("Marca:");
        lblMarca.setToolTipText("Marca");

        txtCodigo.setBackground(new java.awt.Color(255, 255, 255));
        txtCodigo.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        txtCodigo.setForeground(new java.awt.Color(0, 0, 0));
        txtCodigo.setToolTipText("Introduce el código max 8 carácteres");
        txtCodigo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        txtCodigo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        txtCodigo.setPreferredSize(new java.awt.Dimension(64, 20));

        txtDenominacion.setBackground(new java.awt.Color(255, 255, 255));
        txtDenominacion.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        txtDenominacion.setForeground(new java.awt.Color(0, 0, 0));
        txtDenominacion.setToolTipText("Introduce el nombre o denominación del artículo");
        txtDenominacion.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        txtDenominacion.setMinimumSize(new java.awt.Dimension(64, 20));
        txtDenominacion.setPreferredSize(new java.awt.Dimension(64, 20));

        txtMarca.setBackground(new java.awt.Color(255, 255, 255));
        txtMarca.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        txtMarca.setForeground(new java.awt.Color(0, 0, 0));
        txtMarca.setToolTipText("Introduce la marca del artículo");
        txtMarca.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        txtMarca.setPreferredSize(new java.awt.Dimension(64, 20));

        lpIdentificacion.setLayer(lblCodigo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpIdentificacion.setLayer(lblDenominacion, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpIdentificacion.setLayer(lblMarca, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpIdentificacion.setLayer(txtCodigo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpIdentificacion.setLayer(txtDenominacion, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpIdentificacion.setLayer(txtMarca, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout lpIdentificacionLayout = new javax.swing.GroupLayout(lpIdentificacion);
        lpIdentificacion.setLayout(lpIdentificacionLayout);
        lpIdentificacionLayout.setHorizontalGroup(
            lpIdentificacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lpIdentificacionLayout.createSequentialGroup()
                .addGroup(lpIdentificacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lpIdentificacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(lpIdentificacionLayout.createSequentialGroup()
                            .addGap(42, 42, 42)
                            .addComponent(lblCodigo))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lpIdentificacionLayout.createSequentialGroup()
                            .addGap(48, 48, 48)
                            .addComponent(lblMarca)))
                    .addGroup(lpIdentificacionLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblDenominacion)))
                .addGap(18, 18, 18)
                .addGroup(lpIdentificacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDenominacion, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(txtMarca, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        lpIdentificacionLayout.setVerticalGroup(
            lpIdentificacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lpIdentificacionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lpIdentificacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCodigo)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(lpIdentificacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDenominacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDenominacion, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(lpIdentificacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMarca)
                    .addComponent(txtMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );

        lpClasificacion.setBackground(new java.awt.Color(204, 204, 204));
        lpClasificacion.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(102, 102, 102), new java.awt.Color(102, 102, 102)), "Clasificación", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Historic", 0, 12), new java.awt.Color(0, 51, 51))); // NOI18N
        lpClasificacion.setPreferredSize(new java.awt.Dimension(350, 120));

        lblGama.setBackground(new java.awt.Color(255, 255, 255));
        lblGama.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        lblGama.setForeground(new java.awt.Color(51, 51, 51));
        lblGama.setText("Gama:");
        lblGama.setToolTipText("Gama");

        lblTipo.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        lblTipo.setForeground(new java.awt.Color(51, 51, 51));
        lblTipo.setText("Tipo de Artículo:");
        lblTipo.setToolTipText("TipoArtículo");

        lblEficienciaEnergia.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        lblEficienciaEnergia.setForeground(new java.awt.Color(51, 51, 51));
        lblEficienciaEnergia.setText("Eficiencia energética:");
        lblEficienciaEnergia.setToolTipText("EficieciaEnergía");

        cmbGama.setBackground(new java.awt.Color(255, 255, 255));
        cmbGama.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        cmbGama.setForeground(new java.awt.Color(0, 0, 0));
        cmbGama.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Seleccione gama --", "Gama blanca", "Gama marrón", "PAE" }));
        cmbGama.setToolTipText("Selecciona una gama de las disponibles");
        cmbGama.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        cmbGama.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        cmbGama.setOpaque(true);
        cmbGama.setPreferredSize(new java.awt.Dimension(149, 20));
        cmbGama.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbGamaItemStateChanged(evt);
            }
        });

        cmbTipo.setBackground(new java.awt.Color(255, 255, 255));
        cmbTipo.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        cmbTipo.setForeground(new java.awt.Color(0, 0, 0));
        cmbTipo.setToolTipText("Seleciona el tipo de artículo en la gama");
        cmbTipo.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        cmbTipo.setEnabled(false);
        cmbTipo.setLightWeightPopupEnabled(false);
        cmbTipo.setPreferredSize(new java.awt.Dimension(149, 20));

        cmbEficienciaEnergia.setBackground(new java.awt.Color(255, 255, 255));
        cmbEficienciaEnergia.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        cmbEficienciaEnergia.setForeground(new java.awt.Color(0, 0, 0));
        cmbEficienciaEnergia.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Seleccione --", "A", "B", "C", "D", "E", "F", "G", "No aplica" }));
        cmbEficienciaEnergia.setToolTipText("Escoge la eficiencia energética del artículo");
        cmbEficienciaEnergia.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        cmbEficienciaEnergia.setPreferredSize(new java.awt.Dimension(149, 20));

        lpClasificacion.setLayer(lblGama, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpClasificacion.setLayer(lblTipo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpClasificacion.setLayer(lblEficienciaEnergia, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpClasificacion.setLayer(cmbGama, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpClasificacion.setLayer(cmbTipo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpClasificacion.setLayer(cmbEficienciaEnergia, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout lpClasificacionLayout = new javax.swing.GroupLayout(lpClasificacion);
        lpClasificacion.setLayout(lpClasificacionLayout);
        lpClasificacionLayout.setHorizontalGroup(
            lpClasificacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lpClasificacionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lpClasificacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblEficienciaEnergia)
                    .addComponent(lblTipo, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblGama, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(lpClasificacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbTipo, 0, 195, Short.MAX_VALUE)
                    .addComponent(cmbGama, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(lpClasificacionLayout.createSequentialGroup()
                        .addComponent(cmbEficienciaEnergia, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        lpClasificacionLayout.setVerticalGroup(
            lpClasificacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lpClasificacionLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(lpClasificacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGama)
                    .addComponent(cmbGama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(lpClasificacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTipo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(lpClasificacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbEficienciaEnergia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEficienciaEnergia))
                .addGap(43, 43, 43))
        );

        lpPrecioystock.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(102, 102, 102), new java.awt.Color(102, 102, 102)), "Precio y stock", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Historic", 0, 12), new java.awt.Color(0, 51, 51))); // NOI18N
        lpPrecioystock.setPreferredSize(new java.awt.Dimension(350, 120));

        lblPrecioUnd.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        lblPrecioUnd.setForeground(new java.awt.Color(51, 51, 51));
        lblPrecioUnd.setText("Precio unitario (€):");
        lblPrecioUnd.setToolTipText("Precio Und");

        lblStock.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        lblStock.setForeground(new java.awt.Color(51, 51, 51));
        lblStock.setText("Stock disponible:");
        lblStock.setToolTipText("Stock");

        lblFechaEntrada.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        lblFechaEntrada.setForeground(new java.awt.Color(51, 51, 51));
        lblFechaEntrada.setText("Fecha de entrada:");
        lblFechaEntrada.setToolTipText("FechaEntrada");

        txtPrecioUnd.setBackground(new java.awt.Color(255, 255, 255));
        txtPrecioUnd.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        txtPrecioUnd.setForeground(new java.awt.Color(0, 0, 0));
        txtPrecioUnd.setToolTipText("Precio por unidad en euros.");
        txtPrecioUnd.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        txtPrecioUnd.setPreferredSize(new java.awt.Dimension(64, 20));

        txtStock.setBackground(new java.awt.Color(255, 255, 255));
        txtStock.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        txtStock.setForeground(new java.awt.Color(0, 0, 0));
        txtStock.setToolTipText("Stock disponible del artículo");
        txtStock.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        txtStock.setPreferredSize(new java.awt.Dimension(64, 20));

        spFechaAct.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        spFechaAct.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), null, new java.util.Date(), java.util.Calendar.DAY_OF_WEEK));
        spFechaAct.setToolTipText("Fecha de entrada del artículo");
        spFechaAct.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        spFechaAct.setMinimumSize(new java.awt.Dimension(64, 20));

        lpPrecioystock.setLayer(lblPrecioUnd, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpPrecioystock.setLayer(lblStock, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpPrecioystock.setLayer(lblFechaEntrada, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpPrecioystock.setLayer(txtPrecioUnd, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpPrecioystock.setLayer(txtStock, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpPrecioystock.setLayer(spFechaAct, javax.swing.JLayeredPane.PALETTE_LAYER);

        javax.swing.GroupLayout lpPrecioystockLayout = new javax.swing.GroupLayout(lpPrecioystock);
        lpPrecioystock.setLayout(lpPrecioystockLayout);
        lpPrecioystockLayout.setHorizontalGroup(
            lpPrecioystockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lpPrecioystockLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lpPrecioystockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblFechaEntrada)
                    .addComponent(lblStock)
                    .addComponent(lblPrecioUnd))
                .addGap(18, 18, 18)
                .addGroup(lpPrecioystockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtPrecioUnd, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                    .addComponent(txtStock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spFechaAct, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        lpPrecioystockLayout.setVerticalGroup(
            lpPrecioystockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lpPrecioystockLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lpPrecioystockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPrecioUnd)
                    .addComponent(txtPrecioUnd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(lpPrecioystockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStock)
                    .addComponent(txtStock, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(lpPrecioystockLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaEntrada)
                    .addComponent(spFechaAct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lpGarantia.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(102, 102, 102), new java.awt.Color(102, 102, 102)), "Garantía", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Historic", 0, 12), new java.awt.Color(0, 51, 51))); // NOI18N
        lpGarantia.setPreferredSize(new java.awt.Dimension(350, 120));

        btngGarantia.add(rbtnLegal);
        rbtnLegal.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        rbtnLegal.setForeground(new java.awt.Color(51, 51, 51));
        rbtnLegal.setText("3 años (garantía legal)");
        rbtnLegal.setToolTipText("Garantía legal");
        rbtnLegal.setContentAreaFilled(false);
        rbtnLegal.setFocusPainted(false);

        btngGarantia.add(rbtnAmpliada);
        rbtnAmpliada.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        rbtnAmpliada.setForeground(new java.awt.Color(51, 51, 51));
        rbtnAmpliada.setText("5 años (garantía ampliada)");
        rbtnAmpliada.setToolTipText("Garantía ampliada");
        rbtnAmpliada.setContentAreaFilled(false);
        rbtnAmpliada.setFocusPainted(false);

        btngGarantia.add(rbtnSegundaMano);
        rbtnSegundaMano.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        rbtnSegundaMano.setForeground(new java.awt.Color(51, 51, 51));
        rbtnSegundaMano.setText("1 año (garantía de segunda mano)");
        rbtnSegundaMano.setToolTipText("Garantía segunda mano");
        rbtnSegundaMano.setContentAreaFilled(false);
        rbtnSegundaMano.setFocusPainted(false);

        lpGarantia.setLayer(rbtnLegal, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpGarantia.setLayer(rbtnAmpliada, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpGarantia.setLayer(rbtnSegundaMano, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout lpGarantiaLayout = new javax.swing.GroupLayout(lpGarantia);
        lpGarantia.setLayout(lpGarantiaLayout);
        lpGarantiaLayout.setHorizontalGroup(
            lpGarantiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lpGarantiaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lpGarantiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbtnSegundaMano)
                    .addComponent(rbtnAmpliada)
                    .addComponent(rbtnLegal))
                .addContainerGap(126, Short.MAX_VALUE))
        );
        lpGarantiaLayout.setVerticalGroup(
            lpGarantiaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lpGarantiaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbtnLegal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbtnAmpliada)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbtnSegundaMano)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        lpEstado.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(102, 102, 102), new java.awt.Color(102, 102, 102)), "Estado del artículo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Historic", 0, 12), new java.awt.Color(0, 51, 51))); // NOI18N
        lpEstado.setPreferredSize(new java.awt.Dimension(350, 120));

        btngEstado.add(rbtnNuevo);
        rbtnNuevo.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        rbtnNuevo.setForeground(new java.awt.Color(51, 51, 51));
        rbtnNuevo.setText("Nuevo");
        rbtnNuevo.setToolTipText("Artículo nuevo");
        rbtnNuevo.setContentAreaFilled(false);
        rbtnNuevo.setFocusPainted(false);

        btngEstado.add(rbtnOferta);
        rbtnOferta.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        rbtnOferta.setForeground(new java.awt.Color(51, 51, 51));
        rbtnOferta.setText("Oferta");
        rbtnOferta.setToolTipText("Artículo de oferta");
        rbtnOferta.setContentAreaFilled(false);
        rbtnOferta.setFocusPainted(false);

        btngEstado.add(rbtnReacondicionado);
        rbtnReacondicionado.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        rbtnReacondicionado.setForeground(new java.awt.Color(51, 51, 51));
        rbtnReacondicionado.setText("Reacondicionado");
        rbtnReacondicionado.setToolTipText("Artículo reacondicionado");
        rbtnReacondicionado.setContentAreaFilled(false);
        rbtnReacondicionado.setFocusPainted(false);

        lpEstado.setLayer(rbtnNuevo, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpEstado.setLayer(rbtnOferta, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpEstado.setLayer(rbtnReacondicionado, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout lpEstadoLayout = new javax.swing.GroupLayout(lpEstado);
        lpEstado.setLayout(lpEstadoLayout);
        lpEstadoLayout.setHorizontalGroup(
            lpEstadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lpEstadoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbtnNuevo)
                .addGap(18, 18, 18)
                .addComponent(rbtnReacondicionado)
                .addGap(18, 18, 18)
                .addComponent(rbtnOferta)
                .addContainerGap(65, Short.MAX_VALUE))
        );
        lpEstadoLayout.setVerticalGroup(
            lpEstadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lpEstadoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lpEstadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtnNuevo)
                    .addComponent(rbtnOferta)
                    .addComponent(rbtnReacondicionado))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        lpServicioVenta.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(102, 102, 102), new java.awt.Color(102, 102, 102)), "Servicios asociados a la venta", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Historic", 0, 12), new java.awt.Color(0, 51, 51))); // NOI18N
        lpServicioVenta.setToolTipText("Servicio de recogida");
        lpServicioVenta.setPreferredSize(new java.awt.Dimension(350, 120));

        chkRecogida.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        chkRecogida.setForeground(new java.awt.Color(51, 51, 51));
        chkRecogida.setText("Recogida en tienda");
        chkRecogida.setToolTipText("Servicio de recogida en tienda");
        chkRecogida.setContentAreaFilled(false);
        chkRecogida.setFocusPainted(false);

        chkEntrega.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        chkEntrega.setForeground(new java.awt.Color(51, 51, 51));
        chkEntrega.setText("Entrega a domicilio");
        chkEntrega.setToolTipText("Servicio de entrega a domicilio");
        chkEntrega.setContentAreaFilled(false);
        chkEntrega.setFocusPainted(false);

        chkInstalacionMontaje.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        chkInstalacionMontaje.setForeground(new java.awt.Color(51, 51, 51));
        chkInstalacionMontaje.setText("Instalación y montaje");
        chkInstalacionMontaje.setToolTipText("Servicio de instalación y montaje");
        chkInstalacionMontaje.setContentAreaFilled(false);
        chkInstalacionMontaje.setFocusPainted(false);

        chkRetiradaUsado.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        chkRetiradaUsado.setForeground(new java.awt.Color(51, 51, 51));
        chkRetiradaUsado.setText("Retirada del aparato usado");
        chkRetiradaUsado.setToolTipText("Servicio de retirada de aparato usado");
        chkRetiradaUsado.setContentAreaFilled(false);
        chkRetiradaUsado.setFocusPainted(false);

        lpServicioVenta.setLayer(chkRecogida, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpServicioVenta.setLayer(chkEntrega, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpServicioVenta.setLayer(chkInstalacionMontaje, javax.swing.JLayeredPane.DEFAULT_LAYER);
        lpServicioVenta.setLayer(chkRetiradaUsado, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout lpServicioVentaLayout = new javax.swing.GroupLayout(lpServicioVenta);
        lpServicioVenta.setLayout(lpServicioVentaLayout);
        lpServicioVentaLayout.setHorizontalGroup(
            lpServicioVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lpServicioVentaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lpServicioVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(lpServicioVentaLayout.createSequentialGroup()
                        .addComponent(chkRecogida)
                        .addGap(18, 18, 18)
                        .addComponent(chkInstalacionMontaje))
                    .addGroup(lpServicioVentaLayout.createSequentialGroup()
                        .addComponent(chkEntrega)
                        .addGap(18, 18, 18)
                        .addComponent(chkRetiradaUsado)))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        lpServicioVentaLayout.setVerticalGroup(
            lpServicioVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lpServicioVentaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(lpServicioVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkRecogida)
                    .addComponent(chkInstalacionMontaje))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(lpServicioVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkEntrega)
                    .addComponent(chkRetiradaUsado)))
        );

        lpDescripcion.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(153, 153, 153), new java.awt.Color(153, 153, 153), new java.awt.Color(102, 102, 102), new java.awt.Color(102, 102, 102)), "Características técnicas / descripción", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Historic", 0, 12), new java.awt.Color(0, 0, 0))); // NOI18N
        lpDescripcion.setPreferredSize(new java.awt.Dimension(350, 120));

        txtDescripcion.setBackground(new java.awt.Color(255, 255, 255));
        txtDescripcion.setColumns(20);
        txtDescripcion.setFont(new java.awt.Font("Segoe UI Historic", 0, 12)); // NOI18N
        txtDescripcion.setForeground(new java.awt.Color(0, 0, 0));
        txtDescripcion.setRows(5);
        txtDescripcion.setToolTipText("Descripción técnica del artículo");
        spDescripcion.setViewportView(txtDescripcion);

        lpDescripcion.setLayer(spDescripcion, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout lpDescripcionLayout = new javax.swing.GroupLayout(lpDescripcion);
        lpDescripcion.setLayout(lpDescripcionLayout);
        lpDescripcionLayout.setHorizontalGroup(
            lpDescripcionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(lpDescripcionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spDescripcion)
                .addContainerGap())
        );
        lpDescripcionLayout.setVerticalGroup(
            lpDescripcionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, lpDescripcionLayout.createSequentialGroup()
                .addComponent(spDescripcion, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                .addContainerGap())
        );

        sepPrincipal.setBackground(new java.awt.Color(102, 102, 102));
        sepPrincipal.setForeground(new java.awt.Color(102, 102, 102));

        btnReset.setBackground(new java.awt.Color(102, 215, 215));
        btnReset.setForeground(new java.awt.Color(0, 0, 0));
        btnReset.setText("Reset");
        btnReset.setToolTipText("Reseteo de los valores añadidos en los campos a default");
        btnReset.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.gray, java.awt.Color.gray, java.awt.Color.darkGray, java.awt.Color.lightGray));
        btnReset.setFocusPainted(false);
        btnReset.setFocusable(false);
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        btnSalir.setBackground(new java.awt.Color(102, 215, 215));
        btnSalir.setForeground(new java.awt.Color(0, 0, 0));
        btnSalir.setText("Salir");
        btnSalir.setToolTipText("Salir de la App");
        btnSalir.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.gray, java.awt.Color.gray, java.awt.Color.darkGray, java.awt.Color.lightGray));
        btnSalir.setFocusPainted(false);
        btnSalir.setFocusable(false);
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        btnGuardar.setBackground(new java.awt.Color(102, 215, 215));
        btnGuardar.setForeground(new java.awt.Color(0, 0, 0));
        btnGuardar.setText("Guardar");
        btnGuardar.setToolTipText("Guarda el producto nuevo si los datos son válidos");
        btnGuardar.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.gray, java.awt.Color.gray, java.awt.Color.darkGray, java.awt.Color.lightGray));
        btnGuardar.setFocusPainted(false);
        btnGuardar.setFocusable(false);
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlPrincipalLayout = new javax.swing.GroupLayout(pnlPrincipal);
        pnlPrincipal.setLayout(pnlPrincipalLayout);
        pnlPrincipalLayout.setHorizontalGroup(
            pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrincipalLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lpDescripcion, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
                    .addComponent(sepPrincipal, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlPrincipalLayout.createSequentialGroup()
                        .addComponent(lpIdentificacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lpClasificacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, pnlPrincipalLayout.createSequentialGroup()
                        .addComponent(lpEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lpServicioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPrincipalLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(pnlPrincipalLayout.createSequentialGroup()
                                .addComponent(lpPrecioystock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50)
                                .addComponent(lpGarantia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(pnlPrincipalLayout.createSequentialGroup()
                                .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(25, 25, 25))
        );
        pnlPrincipalLayout.setVerticalGroup(
            pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPrincipalLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lpIdentificacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lpClasificacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPrincipalLayout.createSequentialGroup()
                        .addComponent(lpPrecioystock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lpEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPrincipalLayout.createSequentialGroup()
                        .addComponent(lpGarantia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lpServicioVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(lpDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sepPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 7, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed

        limpiarFormulario();

        JOptionPane.showMessageDialog(
                this,
                """
                Valores reseteados! :)
                """
        );
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        confirmarSalida();
    }//GEN-LAST:event_btnSalirActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        validarFormulario();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void cmbGamaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbGamaItemStateChanged

        String[] gamaBlanca = {"frigoríficos", "lavadoras", "lavavajillas", "hornos", "placas", "campanas", "secadoras", "aire acondicionado"};
        String[] gamaMarron = {"televisores", "barras de sonido", "equipos de música", "home cinema", "proyectores"};
        String[] pae = {"cafeteras", "tostadoras", "batidoras", "robots de cocina", "freidoras", "microondas", "plancha", "aspiradoras", "secadores de pelo", "afeitadoras"};

        switch (obtenerGama()) {

            case "Gama blanca" -> {
                cmbTipo.setEnabled(true);

                cmbTipo.setModel(new DefaultComboBoxModel<>(gamaBlanca));
            }

            case "Gama marrón" -> {
                cmbTipo.setEnabled(true);

                cmbTipo.setModel(new DefaultComboBoxModel<>(gamaMarron));
            }

            case "PAE" -> {
                cmbTipo.setEnabled(true);

                cmbTipo.setModel(new DefaultComboBoxModel<>(pae));
            }

            default -> {
                cmbTipo.setEnabled(false);
                cmbTipo.setModel(new DefaultComboBoxModel<>());
            }
        }
    }//GEN-LAST:event_cmbGamaItemStateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        confirmarSalida();
    }//GEN-LAST:event_formWindowClosing


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSalir;
    private javax.swing.ButtonGroup btngEstado;
    private javax.swing.ButtonGroup btngGarantia;
    private javax.swing.JCheckBox chkEntrega;
    private javax.swing.JCheckBox chkInstalacionMontaje;
    private javax.swing.JCheckBox chkRecogida;
    private javax.swing.JCheckBox chkRetiradaUsado;
    private javax.swing.JComboBox<String> cmbEficienciaEnergia;
    private javax.swing.JComboBox<String> cmbGama;
    private javax.swing.JComboBox<String> cmbTipo;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblDenominacion;
    private javax.swing.JLabel lblEficienciaEnergia;
    private javax.swing.JLabel lblFechaEntrada;
    private javax.swing.JLabel lblGama;
    private javax.swing.JLabel lblMarca;
    private javax.swing.JLabel lblPrecioUnd;
    private javax.swing.JLabel lblStock;
    private javax.swing.JLabel lblTipo;
    private javax.swing.JLayeredPane lpClasificacion;
    private javax.swing.JLayeredPane lpDescripcion;
    private javax.swing.JLayeredPane lpEstado;
    private javax.swing.JLayeredPane lpGarantia;
    private javax.swing.JLayeredPane lpIdentificacion;
    private javax.swing.JLayeredPane lpPrecioystock;
    private javax.swing.JLayeredPane lpServicioVenta;
    private javax.swing.JPanel pnlPrincipal;
    private javax.swing.JRadioButton rbtnAmpliada;
    private javax.swing.JRadioButton rbtnLegal;
    private javax.swing.JRadioButton rbtnNuevo;
    private javax.swing.JRadioButton rbtnOferta;
    private javax.swing.JRadioButton rbtnReacondicionado;
    private javax.swing.JRadioButton rbtnSegundaMano;
    private javax.swing.JSeparator sepPrincipal;
    private javax.swing.JScrollPane spDescripcion;
    private javax.swing.JSpinner spFechaAct;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtDenominacion;
    private javax.swing.JTextArea txtDescripcion;
    private javax.swing.JTextField txtMarca;
    private javax.swing.JTextField txtPrecioUnd;
    private javax.swing.JTextField txtStock;
    // End of variables declaration//GEN-END:variables
}
