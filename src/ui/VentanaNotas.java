package ui;

import modelo.Nota;
import modelo.Usuario;
import persistencia.GestorDatos;
import seguridad.HashUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VentanaNotas extends JFrame {

    // Nombres de los paneles para el CardLayout
    private static final String PANEL_LOGIN = "login";
    private static final String PANEL_NOTAS = "notas";

    private CardLayout cardLayout;
    private JPanel panelContenedor;

    // -- Componentes del panel de login --
    private JTextField txtLoginUsuario;
    private JPasswordField txtLoginPassword;
    private JLabel lblLoginMensaje;

    // -- Componentes del panel de notas --
    private JLabel lblUsuario;
    private JTextField txtBuscar;
    private DefaultListModel<Nota> modeloLista;
    private JList<Nota> listaNotas;
    private JTextField txtTitulo;
    private JTextArea txtContenido;
    private JLabel lblEstado;
    private JLabel lblContador;

    private JButton btnNueva;
    private JButton btnGuardar;
    private JButton btnEliminar;
    private JButton btnLimpiar;
    private JButton btnBorrarTodo;
    private JButton btnExportar;

    // -- Datos de la sesión --
    private Usuario usuarioActual;

    private List<Nota> todasLasNotas;

    public VentanaNotas() {
        todasLasNotas = new ArrayList<>();
        inicializarVentana();
        crearPanelLogin();
        crearPanelNotas();
        setVisible(true);
    }

    private void inicializarVentana() {
        setTitle("Gestor de Notas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 620);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(700, 500));

        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);
        add(panelContenedor);
    }

    // =====================================================================
    //  PANEL DE LOGIN
    // =====================================================================

    private void crearPanelLogin() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 242, 248));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lblTitulo = new JLabel("Gestor de Notas", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTitulo.setForeground(new Color(40, 90, 150));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        JLabel lblSub = new JLabel("Inicia sesión o regístrate para continuar", SwingConstants.CENTER);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSub.setForeground(Color.GRAY);
        gbc.gridy = 1;
        panel.add(lblSub, gbc);

        gbc.gridy = 2;
        panel.add(new JSeparator(), gbc);

        // Campos usuario y contraseña
        gbc.gridwidth = 1;
        gbc.gridy = 3; gbc.gridx = 0;
        panel.add(new JLabel("Usuario:"), gbc);
        txtLoginUsuario = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtLoginUsuario, gbc);

        gbc.gridy = 4; gbc.gridx = 0;
        panel.add(new JLabel("Contraseña:"), gbc);
        txtLoginPassword = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(txtLoginPassword, gbc);

        // Mensaje informativo (errores, confirmaciones)
        lblLoginMensaje = new JLabel(" ", SwingConstants.CENTER);
        lblLoginMensaje.setForeground(Color.RED);
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(lblLoginMensaje, gbc);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panelBotones.setOpaque(false);
        JButton btnLogin = new JButton("Iniciar sesión");
        JButton btnRegistro = new JButton("Registrarse");
        btnLogin.setPreferredSize(new Dimension(140, 35));
        btnRegistro.setPreferredSize(new Dimension(140, 35));
        panelBotones.add(btnLogin);
        panelBotones.add(btnRegistro);
        gbc.gridy = 6;
        panel.add(panelBotones, gbc);

        // Eventos
        btnLogin.addActionListener(e -> accionLogin());
        btnRegistro.addActionListener(e -> accionRegistro());
        // Con Enter en el campo de contraseña también inicia sesión
        txtLoginPassword.addActionListener(e -> accionLogin());

        panelContenedor.add(panel, PANEL_LOGIN);
    }

    private void accionLogin() {
        String nombre = txtLoginUsuario.getText().trim();
        String password = new String(txtLoginPassword.getPassword()).trim();

        if (nombre.isEmpty() || password.isEmpty()) {
            setMensajeLogin("Rellena todos los campos.", Color.RED);
            return;
        }

        List<Usuario> usuarios = GestorDatos.cargarUsuarios();
        for (int i = 0; i < usuarios.size(); i++) {
            Usuario u = usuarios.get(i);
            if (u.getNombre().equalsIgnoreCase(nombre) && HashUtil.verificar(password, u.getPasswordHash())) {
                usuarioActual = u;
                txtLoginUsuario.setText("");
                txtLoginPassword.setText("");
                setMensajeLogin(" ", Color.RED);
                abrirPanelNotas();
                return;
            }
        }
        setMensajeLogin("Usuario o contraseña incorrectos.", Color.RED);
    }

    private void accionRegistro() {
        String nombre = txtLoginUsuario.getText().trim();
        String password = new String(txtLoginPassword.getPassword()).trim();

        if (nombre.isEmpty() || password.isEmpty()) {
            setMensajeLogin("Rellena todos los campos.", Color.RED);
            return;
        }
        if (nombre.length() < 3) {
            setMensajeLogin("El nombre debe tener al menos 3 caracteres.", Color.RED);
            return;
        }
        if (password.length() < 4) {
            setMensajeLogin("La contraseña debe tener al menos 4 caracteres.", Color.RED);
            return;
        }
        if (GestorDatos.existeUsuario(nombre)) {
            setMensajeLogin("Ese nombre de usuario ya existe.", Color.RED);
            return;
        }

        String hash = HashUtil.hashear(password);
        GestorDatos.guardarUsuario(new Usuario(nombre, hash));
        setMensajeLogin("Usuario registrado. Ya puedes iniciar sesion.", new Color(0, 140, 0));
    }

    private void setMensajeLogin(String texto, Color color) {
        lblLoginMensaje.setText(texto);
        lblLoginMensaje.setForeground(color);
    }

    // =====================================================================
    //  PANEL DE NOTAS
    // =====================================================================

    private void crearPanelNotas() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Barra superior con el nombre del usuario y botón de cerrar sesión
        JPanel barraTop = new JPanel(new BorderLayout());
        barraTop.setBackground(new Color(40, 90, 150));
        barraTop.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        lblUsuario = new JLabel("Usuario: -");
        lblUsuario.setForeground(Color.WHITE);
        lblUsuario.setFont(new Font("SansSerif", Font.BOLD, 13));
        barraTop.add(lblUsuario, BorderLayout.WEST);

        JButton btnCerrarSesion = new JButton("Cerrar sesion");
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        barraTop.add(btnCerrarSesion, BorderLayout.EAST);

        panel.add(barraTop, BorderLayout.NORTH);

        // ---- Panel izquierdo: lista de notas ----
        JPanel panelIzq = new JPanel(new BorderLayout(3, 3));
        panelIzq.setPreferredSize(new Dimension(220, 0));
        panelIzq.setBorder(BorderFactory.createTitledBorder("Mis notas"));

        // Buscador en tiempo real
        JPanel panelBuscar = new JPanel(new BorderLayout(4, 0));
        panelBuscar.add(new JLabel("Buscar:"), BorderLayout.WEST);
        txtBuscar = new JTextField();
        panelBuscar.add(txtBuscar, BorderLayout.CENTER);
        panelIzq.add(panelBuscar, BorderLayout.NORTH);

        modeloLista = new DefaultListModel<>();
        listaNotas = new JList<>(modeloLista);
        listaNotas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panelIzq.add(new JScrollPane(listaNotas), BorderLayout.CENTER);

        lblContador = new JLabel("0 notas");
        lblContador.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lblContador.setForeground(Color.GRAY);
        panelIzq.add(lblContador, BorderLayout.SOUTH);

        panel.add(panelIzq, BorderLayout.WEST);

        // ---- Panel central: editor de nota ----
        JPanel panelEditor = new JPanel(new BorderLayout(5, 5));
        panelEditor.setBorder(BorderFactory.createTitledBorder("Nota"));

        JPanel panelTitulo = new JPanel(new BorderLayout(5, 0));
        panelTitulo.add(new JLabel("Titulo:"), BorderLayout.WEST);
        txtTitulo = new JTextField();
        panelTitulo.add(txtTitulo, BorderLayout.CENTER);
        panelEditor.add(panelTitulo, BorderLayout.NORTH);

        txtContenido = new JTextArea();
        txtContenido.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtContenido.setLineWrap(true);
        txtContenido.setWrapStyleWord(true);
        panelEditor.add(new JScrollPane(txtContenido), BorderLayout.CENTER);

        panel.add(panelEditor, BorderLayout.CENTER);

        // ---- Panel derecho: botones ----
        JPanel panelBotones = new JPanel(new GridLayout(6, 1, 5, 8));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(30, 5, 5, 5));

        btnNueva    = new JButton("Nueva nota");
        btnGuardar  = new JButton("Guardar");
        btnEliminar = new JButton("Eliminar nota");
        btnLimpiar  = new JButton("Limpiar campos");
        btnBorrarTodo = new JButton("Borrar todas");
        btnExportar = new JButton("Exportar notas");

        panelBotones.add(btnNueva);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnBorrarTodo);
        panelBotones.add(btnExportar);

        panel.add(panelBotones, BorderLayout.EAST);

        // Barra de estado inferior
        lblEstado = new JLabel("Listo");
        lblEstado.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        panel.add(lblEstado, BorderLayout.SOUTH);

        // ---- Eventos ----
        listaNotas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarNotaSeleccionada();
            }
        });

        // Filtrado en tiempo real mientras el usuario escribe
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e)  { filtrarNotas(); }
            @Override
            public void removeUpdate(DocumentEvent e)  { filtrarNotas(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrarNotas(); }
        });

        btnNueva.addActionListener(e    -> accionNuevaNota());
        btnGuardar.addActionListener(e  -> accionGuardar());
        btnEliminar.addActionListener(e -> accionEliminar());
        btnLimpiar.addActionListener(e  -> accionLimpiar());
        btnBorrarTodo.addActionListener(e -> accionBorrarTodo());
        btnExportar.addActionListener(e -> accionExportar());

        panelContenedor.add(panel, PANEL_NOTAS);
    }

    // Abre el panel de notas cargando las del usuario autenticado
    private void abrirPanelNotas() {
        lblUsuario.setText("Usuario: " + usuarioActual.getNombre());
        todasLasNotas = GestorDatos.cargarNotas(usuarioActual.getNombre());
        limpiarCampos();
        actualizarLista();
        actualizarBotones();
        mostrarEstado("Bienvenido, " + usuarioActual.getNombre() + "! Tienes " + todasLasNotas.size() + " notas.");
        cardLayout.show(panelContenedor, PANEL_NOTAS);
        setTitle("Gestor de Notas — " + usuarioActual.getNombre());
    }

    private void cerrarSesion() {
        usuarioActual = null;
        todasLasNotas.clear();
        modeloLista.clear();
        limpiarCampos();
        setTitle("Gestor de Notas");
        setMensajeLogin(" ", Color.RED);
        cardLayout.show(panelContenedor, PANEL_LOGIN);
    }

    private void cargarNotaSeleccionada() {
        Nota nota = listaNotas.getSelectedValue();
        if (nota != null) {
            txtTitulo.setText(nota.getTitulo());
            txtContenido.setText(nota.getContenido());
            mostrarEstado("Nota seleccionada: \"" + nota.getTitulo() + "\"  |  Creada: " + nota.getFechaFormateada());
        }
        actualizarBotones();
    }

    // Filtra la lista según lo que escribe el usuario en el buscador
    private void filtrarNotas() {
        String filtro = txtBuscar.getText().trim().toLowerCase();
        modeloLista.clear();
        for (int i = 0; i < todasLasNotas.size(); i++) {
            Nota nota = todasLasNotas.get(i);
            if (filtro.isEmpty()
                    || nota.getTitulo().toLowerCase().contains(filtro)
                    || nota.getContenido().toLowerCase().contains(filtro)) {
                modeloLista.addElement(nota);
            }
        }
        actualizarContador();
    }

    private void actualizarLista() {
        txtBuscar.setText("");
        modeloLista.clear();
        for (int i = 0; i < todasLasNotas.size(); i++) {
            modeloLista.addElement(todasLasNotas.get(i));
        }
        actualizarContador();
    }

    private void actualizarContador() {
        int total     = todasLasNotas.size();
        int mostrando = modeloLista.size();
        if (total == mostrando) {
            lblContador.setText(total + (total == 1 ? " nota" : " notas"));
        } else {
            lblContador.setText(mostrando + " de " + total + " notas");
        }
    }

    private void limpiarCampos() {
        txtTitulo.setText("");
        txtContenido.setText("");
        listaNotas.clearSelection();
        actualizarBotones();
    }

    // Activa o desactiva botones según el estado actual
    private void actualizarBotones() {
        boolean haySeleccion = listaNotas.getSelectedValue() != null;
        boolean hayNotas     = !todasLasNotas.isEmpty();

        // Guardar: siempre disponible (crea o edita)
        btnGuardar.setEnabled(true);
        btnEliminar.setEnabled(haySeleccion);
        btnBorrarTodo.setEnabled(hayNotas);
        btnExportar.setEnabled(hayNotas);
    }

    private void mostrarEstado(String mensaje) {
        lblEstado.setText(mensaje);
    }

    // =====================================================================
    //  ACCIONES DE LOS BOTONES
    // =====================================================================

    private void accionNuevaNota() {
        limpiarCampos();
        txtTitulo.requestFocus();
        mostrarEstado("Escribe el titulo y el contenido de la nueva nota, luego pulsa Guardar.");
    }

    private void accionGuardar() {
        String titulo    = txtTitulo.getText().trim();
        String contenido = txtContenido.getText().trim();

        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El titulo no puede estar vacio.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            txtTitulo.requestFocus();
            return;
        }

        Nota seleccionada = listaNotas.getSelectedValue();

        if (seleccionada != null) {
            // Editar nota existente
            seleccionada.setTitulo(titulo);
            seleccionada.setContenido(contenido);
            GestorDatos.guardarNotas(usuarioActual.getNombre(), todasLasNotas);
            actualizarLista();
            // Volver a seleccionar la misma nota
            for (int i = 0; i < modeloLista.size(); i++) {
                if (modeloLista.get(i) == seleccionada) {
                    listaNotas.setSelectedIndex(i);
                    break;
                }
            }
            mostrarEstado("Nota actualizada y guardada correctamente.");
        } else {
            // Nota nueva
            Nota nueva = new Nota(titulo, contenido);
            todasLasNotas.add(nueva);
            GestorDatos.guardarNotas(usuarioActual.getNombre(), todasLasNotas);
            actualizarLista();
            listaNotas.setSelectedIndex(modeloLista.size() - 1);
            mostrarEstado("Nota creada y guardada correctamente.");
        }

        actualizarBotones();
    }

    private void accionEliminar() {
        Nota seleccionada = listaNotas.getSelectedValue();
        if (seleccionada == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una nota para eliminarla.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int resp = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres eliminar la nota \"" + seleccionada.getTitulo() + "\"?",
                "Confirmar eliminacion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (resp == JOptionPane.YES_OPTION) {
            todasLasNotas.remove(seleccionada);
            GestorDatos.guardarNotas(usuarioActual.getNombre(), todasLasNotas);
            actualizarLista();
            limpiarCampos();
            mostrarEstado("Nota eliminada.");
            actualizarBotones();
        }
    }

    private void accionLimpiar() {
        limpiarCampos();
        mostrarEstado("Campos limpiados. Selecciona una nota o crea una nueva.");
    }

    private void accionBorrarTodo() {
        if (todasLasNotas.isEmpty()) {
            mostrarEstado("No hay notas que borrar.");
            return;
        }

        int resp = JOptionPane.showConfirmDialog(this,
                "ATENCION: Esto eliminara TODAS tus notas (" + todasLasNotas.size() + " en total).\n"
                + "Esta accion no se puede deshacer. ¿Estas completamente seguro?",
                "Borrar todas las notas",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE);

        if (resp == JOptionPane.YES_OPTION) {
            todasLasNotas.clear();
            GestorDatos.guardarNotas(usuarioActual.getNombre(), todasLasNotas);
            actualizarLista();
            limpiarCampos();
            mostrarEstado("Todas las notas han sido eliminadas.");
            actualizarBotones();
        }
    }

    private void accionExportar() {
        if (todasLasNotas.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay notas para exportar.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("notas_" + usuarioActual.getNombre() + ".txt"));
        fc.setDialogTitle("Exportar notas");

        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fichero = fc.getSelectedFile();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(fichero))) {
                bw.write("=== Notas de " + usuarioActual.getNombre() + " ===");
                bw.newLine();
                bw.newLine();
                for (int i = 0; i < todasLasNotas.size(); i++) {
                    Nota nota = todasLasNotas.get(i);
                    bw.write("--- " + nota.getTitulo() + " (" + nota.getFechaFormateada() + ") ---");
                    bw.newLine();
                    bw.write(nota.getContenido());
                    bw.newLine();
                    bw.newLine();
                }
                mostrarEstado("Notas exportadas a: " + fichero.getName());
                JOptionPane.showMessageDialog(this,
                        "Exportacion completada:\n" + fichero.getAbsolutePath(),
                        "Listo", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al exportar: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}


