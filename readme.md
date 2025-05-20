# TESCHA

Sistema de Gestión de Préstamos e Inventario

## Descripción

Este proyecto es una aplicación de escritorio desarrollada en JavaFX que permite gestionar el préstamo e inventario de equipos en el área de TI de una institución. Incluye funcionalidades como generación y lectura de QR, control de roles (admin y usuario), reportes y dashboard.

## Requisitos previos

* Java JDK 17 o superior (se recomienda JDK 24)
* JavaFX SDK 24
* Maven 3.6+ o Gradle
* IDE: IntelliJ IDEA, Eclipse o similar

## Configuración de VM Options

Para ejecutar la aplicación correctamente en tu IDE, asegúrate de agregar las siguientes opciones de VM en la configuración de ejecución:

```
--module-path "C:\javafx-sdk-24\lib"
--add-modules javafx.controls,javafx.fxml
--add-opens=java.base/sun.misc=ALL-UNNAMED
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.io=ALL-UNNAMED
--add-opens=java.base/sun.nio.ch=ALL-UNNAMED
--add-exports=java.base/sun.util=ALL-UNNAMED
--add-exports=javafx.base/com.sun.javafx.event=ALL-UNNAMED
--add-exports=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED
--add-opens=javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED
--add-opens=javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED
--add-opens=javafx.graphics/javafx.scene=ALL-UNNAMED
```

> **Nota:** Ajusta la ruta `C:\javafx-sdk-24\lib` según la ubicación en tu máquina.

## Instalación y compilación

1. Clona el repositorio:

   ```bash
   git clone <https://github.com/omarPVP123131/tescha-sistema>
   ```
2. Navega al directorio del proyecto:

   ```bash
   cd tescha-sistema
   ```
3. Compila con Maven:

   ```bash
   mvn clean install
   ```

   o con Gradle:

   ```bash
   gradle build
   ```

## Ejecución

En tu IDE, crea una nueva configuración de ejecución para la clase principal (`Main` o equivalente) e incluye las VM Options indicadas.

También puedes ejecutar desde línea de comandos:

```bash
mvn javafx:run \
    -Dexec.args="--module-path 'C:/javafx-sdk-24/lib' --add-modules javafx.controls,javafx.fxml "
```

## Estructura del proyecto

```
├───src
│   └───main
│       ├───java
│       │   └───tescha
│       │       │   Main.java
│       │       │
│       │       ├───Components
│       │       │       AdvancedToast.java
│       │       │       AlertUtils.java
│       │       │
│       │       ├───config
│       │       │       ConfigManager.java
│       │       │
│       │       ├───configuracion
│       │       │   ├───controller
│       │       │   │       ConfiguracionController.java
│       │       │   │
│       │       │   ├───dao
│       │       │   │       ConfiguracionDAO.java
│       │       │   │       ConfiguracionSQLiteDAO.java
│       │       │   │
│       │       │   ├───dto
│       │       │   │       ConfiguracionDTO.java
│       │       │   │       UsuarioDTO.java
│       │       │   │
│       │       │   ├───service
│       │       │   │       ConfiguracionService.java
│       │       │   │
│       │       │   └───view
│       │       │       │   ConfiguracionView.java
│       │       │       │
│       │       │       └───components
│       │       │               UsuarioCard.java
│       │       │               UsuarioForm.java
│       │       │
│       │       ├───database
│       │       │       DatabaseManager.java
│       │       │
│       │       ├───inventario
│       │       │   ├───controller
│       │       │   │       InventarioController.java
│       │       │   │
│       │       │   ├───dao
│       │       │   │       InventarioDAO.java
│       │       │   │       InventarioSQLiteDAO.java
│       │       │   │
│       │       │   ├───dto
│       │       │   │       CategoriaDTO.java
│       │       │   │       EquipoDTO.java
│       │       │   │
│       │       │   ├───model
│       │       │   │       Producto.java
│       │       │   │
│       │       │   ├───service
│       │       │   │       InventarioService.java
│       │       │   │
│       │       │   └───view
│       │       │       │   InventarioView.java
│       │       │       │
│       │       │       └───components
│       │       │               CategoriaWindow.java
│       │       │               EquipoCard.java
│       │       │               EquipoForm.java
│       │       │
│       │       ├───Login
│       │       │   ├───controller
│       │       │   │       LoginController.java
│       │       │   │
│       │       │   ├───dao
│       │       │   │       UserDAO.java
│       │       │   │       UserDAOImpl.java
│       │       │   │
│       │       │   ├───dto
│       │       │   │       UserDTO.java
│       │       │   │
│       │       │   ├───model
│       │       │   │       UserModel.java
│       │       │   │
│       │       │   └───view
│       │       │           LoginView.java
│       │       │
│       │       ├───MainWindow
│       │       │   ├───components
│       │       │   │       CustomBarChart.java
│       │       │   │       CustomTextField.java
│       │       │   │
│       │       │   └───view
│       │       │           MainWindow.java
│       │       │
│       │       ├───users
│       │       │   ├───controller
│       │       │   │       UserController.java
│       │       │   │
│       │       │   ├───dao
│       │       │   │       UserDAO.java
│       │       │   │       UserSQLiteDAO.java
│       │       │   │
│       │       │   ├───dto
│       │       │   │       UserDTO.java
│       │       │   │
│       │       │   ├───service
│       │       │   │       UserService.java
│       │       │   │
│       │       │   └───view
│       │       │       │   UserListView.java
│       │       │       │
│       │       │       └───components
│       │       │               UserCard.java
│       │       │               UserFilter.java
│       │       │               UserForm.java

```

## Contribuciones

¡Bienvenido a colaboraciones! Por favor, abre un issue o un pull request.

## Licencia

Este proyecto está bajo la licencia MIT. Consulte el archivo `LICENSE` para más detalles.
