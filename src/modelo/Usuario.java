package modelo;

// Clase que representa un usuario del sistema
public class Usuario {

    private final String nombre;
    private final String passwordHash;

    public Usuario(String nombre, String passwordHash) {
        this.nombre = nombre;
        this.passwordHash = passwordHash;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public String toString() {
        return nombre;
    }
}