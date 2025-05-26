package tescha.inventario.view;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import tescha.inventario.controller.InventarioController;
import tescha.inventario.dto.EquipoDTO;
import javafx.scene.control.*;
        import javafx.scene.layout.*;
        import javafx.geometry.*;
        import tescha.inventario.view.components.EquipoCard;
import tescha.inventario.view.components.EquipoForm;
import tescha.inventario.view.components.CategoriaWindow;

import java.util.List;

public class InventarioView {
    private final InventarioController controller;
    private BorderPane view;
    private TableView<EquipoDTO> tablaEquipos;
    private TextField campoBusqueda;
    private ComboBox<String> comboFiltroCategoria;
    private final StackPane dialogContainer = new StackPane();

    public InventarioView(InventarioController controller) {
        this.controller = controller;
        inicializarUI();
    }

    private void inicializarUI() {
        view = new BorderPane();
        view.getStyleClass().add("inventario-view");
        view.getStylesheets().add(
                getClass().getResource("/styles/inventario/styles.css")
                        .toExternalForm()
        );
        // Crear barra superior con buscador y filtros
        HBox barraSuperior = new HBox(10);
        barraSuperior.setPadding(new Insets(10));
        barraSuperior.setAlignment(Pos.CENTER_LEFT);

        campoBusqueda = new TextField();
        campoBusqueda.setPromptText("Buscar equipos...");
        campoBusqueda.getStyleClass().add("busqueda-field");

        Button botonBuscar = new Button("Buscar");
        botonBuscar.getStyleClass().add("buscar-button");
        botonBuscar.setOnAction(e -> buscarEquipos());

        comboFiltroCategoria = new ComboBox<>();
        comboFiltroCategoria.setPromptText("Filtrar por categoría");
        comboFiltroCategoria.getStyleClass().add("combo-categoria");
        comboFiltroCategoria.getItems().add("Todas las categorías");
        comboFiltroCategoria.getItems().addAll(controller.listarCategorias().stream()
                .map(c -> c.getNombre()).toList());
        comboFiltroCategoria.getSelectionModel().selectFirst();
        comboFiltroCategoria.setOnAction(e -> filtrarPorCategoria());

        Button botonAgregar = new Button("Agregar Equipo");
        botonAgregar.getStyleClass().add("agregar-button");
        botonAgregar.setOnAction(e -> mostrarFormularioNuevoEquipo());

        Button botonGestionCategorias = new Button("Gestionar Categorías");
        botonGestionCategorias.getStyleClass().add("categorias-button");
        botonGestionCategorias.setOnAction(e -> mostrarDialogoCategorias());

        barraSuperior.getChildren().addAll(
                campoBusqueda,
                botonBuscar,
                comboFiltroCategoria,
                botonAgregar,
                botonGestionCategorias
        );
        view.setTop(barraSuperior);

        // Configurar la tabla con mejoras visuales
        tablaEquipos = new TableView<>();
        tablaEquipos.getStyleClass().add("equipos-table");
        tablaEquipos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // Configurar columnas
        TableColumn<EquipoDTO, String> columnaNombre = new TableColumn<>("Nombre");
        columnaNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());

        TableColumn<EquipoDTO, String> columnaCategoria = new TableColumn<>("Categoría");
        columnaCategoria.setCellValueFactory(cellData -> cellData.getValue().categoriaProperty());

        TableColumn<EquipoDTO, String> columnaUbicacion = new TableColumn<>("Ubicación");
        columnaUbicacion.setCellValueFactory(cellData -> cellData.getValue().ubicacionProperty());

        TableColumn<EquipoDTO, Number> columnaStock = new TableColumn<>("Stock");
        columnaStock.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty());

        TableColumn<EquipoDTO, Number> columnaMinimo = new TableColumn<>("Mínimo");
        columnaMinimo.setCellValueFactory(cellData -> cellData.getValue().cantidadMinimaProperty());

        tablaEquipos.getColumns().addAll(columnaNombre, columnaCategoria, columnaUbicacion, columnaStock, columnaMinimo);

        // Agregar listener para selección
        tablaEquipos.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> mostrarDetalleEquipo(newSelection));

        view.setCenter(tablaEquipos);

        // Cargar datos iniciales
        actualizarTabla();
    }

    private void buscarEquipos() {
        String criterio = campoBusqueda.getText();
        List<EquipoDTO> resultados;

        if (criterio.isEmpty()) {
            resultados = controller.listarEquipos();
        } else {
            resultados = controller.buscarEquipos(criterio);
        }

        tablaEquipos.getItems().setAll(resultados);
    }

    private void filtrarPorCategoria() {
        String categoriaSeleccionada = comboFiltroCategoria.getValue();

        if (categoriaSeleccionada == null || categoriaSeleccionada.equals("Todas las categorías")) {
            actualizarTabla();
        } else {
            List<EquipoDTO> equipos = controller.buscarEquiposPorCategoria(categoriaSeleccionada);
            tablaEquipos.getItems().setAll(equipos);
        }
    }

    private void mostrarFormularioNuevoEquipo() {
        EquipoForm formulario = new EquipoForm(controller, null);
        formulario.showAndWait(); // Use showAndWait() instead of mostrar()
        actualizarTabla();
    }


    private void mostrarDialogoCategorias() {
        // Obtener el Stage actual desde la vista
        Stage ownerStage = (Stage) view.getScene().getWindow();

        CategoriaWindow dialog = new CategoriaWindow(controller, ownerStage);
        dialog.mostrar();
        actualizarCombosCategorias();
    }

    private void mostrarDetalleEquipo(EquipoDTO equipo) {
        if (equipo != null) {
            EquipoCard tarjetaDetalle = new EquipoCard(equipo, controller);
            view.setRight(tarjetaDetalle);
        } else {
            view.setRight(null);
        }
    }

    private void actualizarTabla() {
        tablaEquipos.getItems().setAll(controller.listarEquipos());
    }

    private void actualizarCombosCategorias() {
        String seleccionActual = comboFiltroCategoria.getValue();
        comboFiltroCategoria.getItems().clear();
        comboFiltroCategoria.getItems().add("Todas las categorías");
        comboFiltroCategoria.getItems().addAll(controller.listarCategorias().stream()
                .map(c -> c.getNombre()).toList());

        if (seleccionActual != null && comboFiltroCategoria.getItems().contains(seleccionActual)) {
            comboFiltroCategoria.getSelectionModel().select(seleccionActual);
        } else {
            comboFiltroCategoria.getSelectionModel().selectFirst();
        }
    }

    public BorderPane getView() {
        return view;
    }
}