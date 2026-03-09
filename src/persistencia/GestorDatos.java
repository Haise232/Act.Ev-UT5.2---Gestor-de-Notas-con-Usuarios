package persistencia;

import modelo.Nota;
import modelo.Usuario;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GestorDatos {

    private static final String DIRECTORIO = "data";
    private static final String FICHERO_USUARIOS = DIRECTORIO + File.separator + "usuarios.dat";

    // Metodo para crear la carpeta data si no existe
    public static void crearDirectorio() {
        File carpeta = new File(DIRECTORIO);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
    }


    // Carga todos los usuarios del fichero
    public static List<Usuario> cargarUsuarios() {
        crearDirectorio();
        List<Usuario> lista = new ArrayList<>();
        File f = new File(FICHERO_USUARIOS);
        if (!f.exists()) {
            return lista;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) {
                    continue;
                }
                String[] partes = linea.split("\\|", 2);
                if (partes.length == 2) {
                    lista.add(new Usuario(partes[0], partes[1]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
        }
        return lista;
    }

    // Guarda un nuevo usuario al final del fichero
    public static void guardarUsuario(Usuario usuario) {
        crearDirectorio();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FICHERO_USUARIOS, true))) {
            bw.write(usuario.getNombre() + "|" + usuario.getPasswordHash());
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
        }
    }

    // Comprueba si ya existe un usuario con ese nombre
    public static boolean existeUsuario(String nombre) {
        List<Usuario> usuarios = cargarUsuarios();
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getNombre().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    // ========== NOTAS ==========

    // Devuelve la ruta del fichero de notas de un usuario
    private static String ficheroNotas(String nombreUsuario) {
        return DIRECTORIO + File.separator + "notas_" + nombreUsuario + ".dat";
    }

    // Carga las notas de un usuario desde su fichero
    public static List<Nota> cargarNotas(String nombreUsuario) {
        crearDirectorio();
        List<Nota> lista = new ArrayList<>();
        File f = new File(ficheroNotas(nombreUsuario));
        if (!f.exists()) {
            return lista;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) {
                    continue;
                }
                String[] partes = linea.split("\\|", 3);
                if (partes.length == 3) {
                    String titulo = partes[0];
                    String contenido = partes[1].replace("\\n", "\n");
                    DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                    LocalDateTime fecha = LocalDateTime.parse(partes[2], formato);
                    lista.add(new Nota(titulo, contenido, fecha));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar notas: " + e.getMessage());
        }
        return lista;
    }

    // Guarda todas las notas de un usuario (sobreescribe el fichero)
    public static void guardarNotas(String nombreUsuario, List<Nota> notas) {
        crearDirectorio();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ficheroNotas(nombreUsuario), false))) {
            for (int i = 0; i < notas.size(); i++) {
                Nota nota = notas.get(i);
                String contenidoEscapado = nota.getContenido().replace("\n", "\\n");
                String fecha = nota.getFechaCreacion().format(formato);
                bw.write(nota.getTitulo() + "|" + contenidoEscapado + "|" + fecha);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar notas: " + e.getMessage());
        }
    }
}
