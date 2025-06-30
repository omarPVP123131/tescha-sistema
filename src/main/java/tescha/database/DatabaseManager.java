package tescha.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DatabaseManager gestiona la conexión a SQLite, activa
 * las FKs (PRAGMA foreign_keys = ON) y crea las tablas
 * con claves foráneas para garantizar la integridad referencial.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:tescha.db";
    private static Connection connection;

    /**
     * Obtiene una conexión singleton, activa FKs y crea/actualiza
     * las tablas si no existen.
     */
    public static synchronized Connection connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            // Activar integridad referencial en SQLite
            try (Statement pragma = connection.createStatement()) {
                pragma.execute("PRAGMA foreign_keys = ON;");
            }
            connection.setAutoCommit(true);
            createTablesIfNotExist();
            insertDefaultData();
        }
        return connection;
    }

    /**
     * Crea las tablas con sus claves foráneas.
     * Si la BD se crea desde cero, usará las definiciones con FKs.
     */
    public static void createTablesIfNotExist() {
        String[] tables = {
                // Usuarios: base para FK en muchas tablas
                """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                nombre TEXT,
                telefono TEXT,
                departamento TEXT,
                rol TEXT DEFAULT 'usuario' CHECK (rol IN ('admin','usuario')),
                activo INTEGER DEFAULT 1,
                ultimo_acceso TEXT,
                fecha_registro TEXT,
                imagen TEXT
            )
            """,

                // Departamentos
                """
CREATE TABLE IF NOT EXISTS departamentos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT UNIQUE NOT NULL,
    descripcion TEXT,
    estado TEXT DEFAULT 'Activo',
    fecha TEXT DEFAULT CURRENT_TIMESTAMP
);

            """,

                """
CREATE TABLE IF NOT EXISTS departamentos_historial (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    departamento_id INTEGER NOT NULL,
    nombre TEXT,
    descripcion TEXT,
    estado TEXT,
    fecha_cambio TEXT DEFAULT CURRENT_TIMESTAMP,
    usuario TEXT,
    accion TEXT NOT NULL,
    FOREIGN KEY (departamento_id) REFERENCES departamentos(id)
);

""",
                // Categorías de inventario
                """
            CREATE TABLE IF NOT EXISTS categorias (
                id INTEGER PRIMARY KEY,
                nombre TEXT UNIQUE NOT NULL,
                descripcion TEXT,
                color TEXT,
                icono TEXT
            )
            """,

                // Inventario
                """
        CREATE TABLE IF NOT EXISTS inventario (
            id INTEGER PRIMARY KEY,
            nombre TEXT,
            categoria TEXT,
            subcategoria TEXT,
            cantidad INTEGER,
            cantidad_minima INTEGER DEFAULT 1,
            status TEXT,
            ubicacion TEXT,
            numero_serie TEXT,
            marca TEXT,
            modelo TEXT,
            notas TEXT,
            imagen TEXT,
            qrcode TEXT
        )
        """,

                """
CREATE TABLE IF NOT EXISTS clientes (
    id INTEGER PRIMARY KEY,
    nombre TEXT NOT NULL,
    imagen_credencial TEXT,
    descripcion TEXT
)
""",
                // Préstamos: FK a usuarios e inventario
                """
CREATE TABLE IF NOT EXISTS prestamos (
                 id INTEGER PRIMARY KEY,
                 fecha_prestamo TEXT,
                 solicitante TEXT,
                 devuelto INTEGER DEFAULT 0,
                 fecha_devuelto TEXT,
                 comentarios TEXT
)
        
             
            """,

                """
CREATE TABLE IF NOT EXISTS prestamos_detalle (
    id INTEGER PRIMARY KEY,
    prestamo_id INTEGER,
    id_equipo INTEGER,
    cantidad INTEGER DEFAULT 1,
    FOREIGN KEY(prestamo_id) REFERENCES prestamos(id),
    FOREIGN KEY(id_equipo) REFERENCES inventario(id)
)
""",

                // Historial de movimientos: FK a inventario
                """
            CREATE TABLE IF NOT EXISTS mov_hist (
                id INTEGER PRIMARY KEY,
                id_item INTEGER,
                fecha TEXT,
                hora TEXT,
                tipo TEXT,
                qty INTEGER,
                usuario TEXT,
                descripcion TEXT,
                FOREIGN KEY(id_item) REFERENCES inventario(id)
            )
            """,

                // Notificaciones: FK a usuarios
                """
            CREATE TABLE IF NOT EXISTS notificaciones (
                id INTEGER PRIMARY KEY,
                usuario_id INTEGER,
                titulo TEXT,
                mensaje TEXT,
                tipo TEXT,
                prioridad TEXT,
                fecha TEXT,
                hora TEXT,
                leido INTEGER DEFAULT 0,
                FOREIGN KEY(usuario_id) REFERENCES usuarios(id)
            )
            """,

                // Configuración: opcional puede vincularse a usuario
                """
            CREATE TABLE IF NOT EXISTS configuracion (
                id INTEGER PRIMARY KEY,
                usuario_id INTEGER,
                respaldos_automaticos INTEGER DEFAULT 0,
                frecuencia_respaldo TEXT,
                ruta_respaldo TEXT,
                FOREIGN KEY(usuario_id) REFERENCES usuarios(id)
            )
            """
        };

        try (Statement stmt = connection.createStatement()) {
            for (String sql : tables) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            System.err.println("Error creating tables with FKs: " + e.getMessage());
        }
    }

    /**
     * Inserta datos por defecto (admin) si no existen.
     */
    private static void insertDefaultData() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                        INSERT OR IGNORE INTO usuarios (username, password, nombre, rol)
                        VALUES ('admin', '1234', 'Administrador', 'admin')
                    """);
        } catch (SQLException e) {
            System.err.println("Error inserting default data: " + e.getMessage());
        }
    }

    /**
     * Cierra la conexión a la BD.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
