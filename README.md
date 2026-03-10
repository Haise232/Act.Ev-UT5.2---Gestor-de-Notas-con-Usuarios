# Gestor de Notas con Usuarios

Aplicación de escritorio desarrollada en Java con interfaz gráfica **Swing** que permite gestionar notas personales por usuario. Cada usuario tiene su propio espacio de notas con persistencia en ficheros y contraseñas protegidas mediante hash.

---

## Características

### Gestión de usuarios
- Registro de nuevos usuarios con validación de datos (nombre mínimo 3 caracteres, contraseña mínimo 4)
- Inicio de sesión con verificación de credenciales
- Contraseñas almacenadas como hash **SHA-256** (nunca en texto plano)
- Opción de cerrar sesión y volver a la pantalla de login

### Gestión de notas
- Crear, ver, editar y eliminar notas
- Limpiar los campos de entrada sin afectar a las notas guardadas
- Buscar y filtrar notas **en tiempo real** mientras se escribe (por título y contenido)
- Borrar todas las notas con doble confirmación de seguridad
- Exportar todas las notas a un fichero `.txt`

### Persistencia
- Las notas de cada usuario se guardan automáticamente tras cada acción (crear, editar, eliminar)
- Los datos se conservan entre sesiones (ficheros en la carpeta `data/`)

### Experiencia de usuario
- Barra de estado inferior con mensajes informativos tras cada acción
- Contador de notas (muestra "X de Y notas" al filtrar)
- Botones deshabilitados cuando no aplican (por ejemplo, "Eliminar" sin nota seleccionada)
- Confirmaciones con diálogos antes de acciones destructivas

---

## Estructura del proyecto

```
src/
├── App.java                        # Punto de entrada, lanza la ventana Swing
├── modelo/
│   ├── Nota.java                   # Clase que representa una nota
│   └── Usuario.java                # Clase que representa un usuario
├── persistencia/
│   └── GestorDatos.java            # Lectura y escritura de ficheros
├── seguridad/
│   └── HashUtil.java               # Hash y verificación SHA-256
└── ui/
    └── VentanaNotas.java           # Interfaz gráfica completa (Swing)

data/                               # Carpeta generada en ejecución
├── usuarios.dat                    # Registro de usuarios (nombre|hash)
└── notas_<usuario>.dat             # Notas de cada usuario
```

---

## Requisitos

- **Java 11** o superior (desarrollado y probado con Java 21)
- No requiere dependencias externas

---

## Cómo ejecutar

### Desde VS Code
Abre `App.java` y pulsa el botón **Run** (o `F5`).

### Desde la terminal
```bash
# Compilar
javac -d bin $(find src -name "*.java")

# Ejecutar
java -cp bin App
```

---

## Datos almacenados

Los ficheros se crean automáticamente en la carpeta `data/` al arrancar la aplicación:

| Fichero | Contenido |
|---|---|
| `usuarios.dat` | `nombre\|hashSHA256` por línea |
| `notas_<usuario>.dat` | `titulo\|contenido\|fecha` por línea |

---

## Seguridad

Las contraseñas **nunca** se guardan en texto plano. Al registrarse se genera un hash SHA-256 y solo ese hash se almacena. En el login se hashea la contraseña introducida y se compara con el hash guardado.

---

## Autor

Joaquín — 1º DAM
