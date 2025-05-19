package tescha.configuracion.view;

import tescha.configuracion.controller.ConfiguracionController;
import tescha.configuracion.dto.ConfiguracionDTO;
import tescha.configuracion.dto.UsuarioDTO;
import tescha.configuracion.view.components.UsuarioForm;
import tescha.configuracion.view.components.UsuarioCard;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import java.util.List;

public class ConfiguracionView {
    private final ConfiguracionController controller;
    private BorderPane view;
    private TableView<UsuarioDTO> tablaUsuarios;
    private TextField campoBusquedaUsuario;

    public ConfiguracionView(ConfiguracionController controller) {
        this.controller = controller;
        inicializarUI();
    }

    private void inicializarUI() {
        view = new BorderPane();
        view.getStyleClass().add("configuracion-view");
        view.getStylesheets().add(getClass().getResource("/styles/configuracion.css").toExternalForm());

        // Crear pestañas para las diferentes secciones
        TabPane tabPane = new TabPane();

        // Pestaña de Configuración del Sistema
        Tab tabConfig = new Tab("Configuración del Sistema");
        tabConfig.setContent(crearPanelConfiguracion());
        tabConfig.setClosable(false);

        // Pestaña de Gestión de Usuarios
        Tab tabUsuarios = new Tab("Gestión de Usuarios");
        tabUsuarios.setContent(crearPanelUsuarios());
        tabUsuarios.setClosable(false);

        tabPane.getTabs().addAll(tabConfig, tabUsuarios);
        view.setCenter(tabPane);
    }

    private VBox crearPanelConfiguracion() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));

        ConfiguracionDTO config = controller.obtenerConfiguracion();

        TitledPane paneRespaldo = new TitledPane();
        paneRespaldo.setText("Configuración de Respaldos");
        paneRespaldo.setExpanded(true);

        GridPane gridRespaldo = new GridPane();
        gridRespaldo.setHgap(10);
        gridRespaldo.setVgap(10);
        gridRespaldo.setPadding(new Insets(10));

        CheckBox checkRespaldo = new CheckBox("Respaldos automáticos");
        checkRespaldo.setSelected(config != null && config.isRespaldosAutomaticos());

        ComboBox<String> comboFrecuencia = new ComboBox<>();
        comboFrecuencia.getItems().addAll("Diario", "Semanal", "Mensual");
        if (config != null && config.getFrecuenciaRespaldo() != null) {
            comboFrecuencia.setValue(config.getFrecuenciaRespaldo());
        } else {
            comboFrecuencia.setValue("Diario");
        }

        TextField campoRuta = new TextField();
        if (config != null && config.getRutaRespaldo() != null) {
            campoRuta.setText(config.getRutaRespaldo());
        }
        campoRuta.setPromptText("Ruta de respaldo");

        Button botonSeleccionarRuta = new Button("Seleccionar...");
        botonSeleccionarRuta.setOnAction(e -> {
            // Implementar selector de directorio
        });

        Button botonGuardar = new Button("Guardar Configuración");
        botonGuardar.setOnAction(e -> {
            ConfiguracionDTO nuevaConfig = new ConfiguracionDTO();
            nuevaConfig.setId(config != null ? config.getId() : 1);
            nuevaConfig.setRespaldosAutomaticos(checkRespaldo.isSelected());
            nuevaConfig.setFrecuenciaRespaldo(comboFrecuencia.getValue());
            nuevaConfig.setRutaRespaldo(campoRuta.getText());

            if (controller.guardarConfiguracion(nuevaConfig)) {
                mostrarAlerta("Configuración guardada", Alert.AlertType.INFORMATION);
            } else {
                mostrarAlerta("Error al guardar configuración", Alert.AlertType.ERROR);
            }
        });

        gridRespaldo.add(checkRespaldo, 0, 0, 2, 1);
        gridRespaldo.add(new Label("Frecuencia:"), 0, 1);
        gridRespaldo.add(comboFrecuencia, 1, 1);
        gridRespaldo.add(new Label("Ruta:"), 0, 2);
        gridRespaldo.add(campoRuta, 1, 2);
        gridRespaldo.add(botonSeleccionarRuta, 2, 2);
        gridRespaldo.add(botonGuardar, 0, 3, 3, 1);

        paneRespaldo.setContent(gridRespaldo);
        panel.getChildren().add(paneRespaldo);

        return panel;
    }

    private BorderPane crearPanelUsuarios() {
        BorderPane panel = new BorderPane();
        panel.getStyleClass().add("panel-usuarios");

        // Barra superior con buscador y botones
        HBox barraSuperior = new HBox(10);
        barraSuperior.setPadding(new Insets(10));
        barraSuperior.setAlignment(Pos.CENTER_LEFT);

        campoBusquedaUsuario = new TextField();
        campoBusquedaUsuario.setPromptText("Buscar usuarios...");

        Button botonBuscar = new Button("Buscar");
        botonBuscar.setOnAction(e -> buscarUsuarios());

        Button botonAgregar = new Button("Agregar Usuario");
        botonAgregar.setOnAction(e -> mostrarFormularioUsuario(null));

        barraSuperior.getChildren().addAll(campoBusquedaUsuario, botonBuscar, botonAgregar);
        panel.setTop(barraSuperior);

        // Configurar tabla de usuarios
        tablaUsuarios = new TableView<>();
        tablaUsuarios.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<UsuarioDTO, String> colUsername = new TableColumn<>("Usuario");
        colUsername.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());

        TableColumn<UsuarioDTO, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());

        TableColumn<UsuarioDTO, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty());

        TableColumn<UsuarioDTO, String> colRol = new TableColumn<>("Rol");
        colRol.setCellValueFactory(cellData -> cellData.getValue().rolProperty());

        TableColumn<UsuarioDTO, Boolean> colActivo = new TableColumn<>("Activo");
        colActivo.setCellValueFactory(cellData -> cellData.getValue().activoProperty());

        // Columna de acciones
        TableColumn<UsuarioDTO, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEditar.setOnAction(event -> {
                    UsuarioDTO usuario = getTableView().getItems().get(getIndex());
                    mostrarFormularioUsuario(usuario);
                });

                btnEliminar.setOnAction(event -> {
                    UsuarioDTO usuario = getTableView().getItems().get(getIndex());
                    if (mostrarConfirmacion("¿Eliminar usuario " + usuario.getUsername() + "?")) {
                        if (controller.eliminarUsuario(usuario.getId())) {
                            actualizarTablaUsuarios();
                            mostrarAlerta("Usuario eliminado", Alert.AlertType.INFORMATION);
                        } else {
                            mostrarAlerta("Error al eliminar usuario", Alert.AlertType.ERROR);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox botones = new HBox(5, btnEditar, btnEliminar);
                    setGraphic(botones);
                }
            }
        });

        tablaUsuarios.getColumns().addAll(colUsername, colNombre, colTelefono, colRol, colActivo, colAcciones);

        // Listener para selección de usuario
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> mostrarDetalleUsuario(newSelection));

        panel.setCenter(tablaUsuarios);

        // Cargar datos iniciales
        actualizarTablaUsuarios();

        return panel;
    }

    private void buscarUsuarios() {
        String criterio = campoBusquedaUsuario.getText().toLowerCase();
        List<UsuarioDTO> usuarios = controller.listarUsuarios();

        if (!criterio.isEmpty()) {
            usuarios = usuarios.stream()
                    .filter(u -> u.getUsername().toLowerCase().contains(criterio) ||
                            u.getNombre().toLowerCase().contains(criterio))
                    .toList();
        }

        tablaUsuarios.getItems().setAll(usuarios);
    }

    private void mostrarFormularioUsuario(UsuarioDTO usuario) {
        UsuarioForm formulario = new UsuarioForm(controller, usuario);
        formulario.showAndWait();
        actualizarTablaUsuarios();
    }

    private void mostrarDetalleUsuario(UsuarioDTO usuario) {
        Tab tabUsuarios = ((TabPane)view.getCenter()).getTabs().get(1);
        BorderPane panelUsuarios = (BorderPane)tabUsuarios.getContent();

        if (usuario != null) {
            UsuarioCard tarjetaDetalle = new UsuarioCard(usuario);
            panelUsuarios.setRight(tarjetaDetalle);
        } else {
            panelUsuarios.setRight(null);
        }
    }

    private void actualizarTablaUsuarios() {
        tablaUsuarios.getItems().setAll(controller.listarUsuarios());
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == Alert.AlertType.ERROR ? "Error" : "Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private boolean mostrarConfirmacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    public BorderPane getView() {
        return view;
    }
}