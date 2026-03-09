package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Nota {

    private String titulo;
    private String contenido;
    private LocalDateTime fechaCreacion;

    // Constructor que pone la fecha automatica
    public Nota(String titulo, String contenido) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Constructor con fecha manual (para cuando cargamos del fichero)
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

    // Devuelve la fecha formateada 
    public String getFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return fechaCreacion.format(formatter);
    }

    @Override
    public String toString() {
        return titulo + " (" + getFechaFormateada() + ")";
    }
}

