package modelo ; 
 
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatter; // Importa a classe DateTimeFormatter para formatar a data e hora

public class Nota {

    private String titulo;
    private String contenido;
    private LocalDateTime fechaCreacion;

    public Nota(String titulo, String contenido) {
        this.titulo = titulo; 
        this.contenido = contenido;
        this.fechaCreacion = LocalDateTime.now(); // Define a data e hora de criação
    }

        public Nota(String titulo, String contenido, LocalDateTime fechaCreacion) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.fechaCreacion = fechaCreacion;
    }

    public String getTitulo() {
        return titulo;
    }

        public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public String getFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaCreacion.format(formatter);
    }

    @Override
    public String toString() {
        return titulo + " (" + getFechaFormateada() + ")";
    }
}

