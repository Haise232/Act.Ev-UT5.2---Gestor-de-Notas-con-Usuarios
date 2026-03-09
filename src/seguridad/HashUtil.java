package seguridad;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Clase para hashear contraseñas con SHA-256
public class HashUtil {

    // Metodo que convierte una contraseña a hash SHA-256
    public static String hashear(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());

            // Convertimos los bytes a hexadecimal
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error al hashear: " + e.getMessage());
            return null;
        }
    }

    // Comprueba si una contraseña coincide con un hash guardado
    public static boolean verificar(String password, String hashGuardado) {
        String hashNuevo = hashear(password);
        if (hashNuevo == null) {
            return false;
        }
        return hashNuevo.equals(hashGuardado);
    }
}