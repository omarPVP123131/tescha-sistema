package tescha.inventario.view;

import animatefx.animation.*;
import com.jfoenix.controls.*;
import com.jfoenix.controls.cells.editors.base.JFXTreeTableCell;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import tescha.inventario.controller.InventarioController;
import tescha.inventario.dto.EquipoDTO;
import tescha.inventario.view.components.EquipoCard;
import tescha.inventario.view.components.EquipoForm;
import tescha.inventario.view.components.CategoriaWindow;

import java.util.List;

public class InventarioView {
    private final InventarioController controller;
    private BorderPane view;
    private JFXTreeTableView<EquipoDTO> tablaEquipos;
    private JFXTextField campoBusqueda;
    private JFXComboBox<String> comboFiltroCategoria;
    private final StackPane dialogContainer = new StackPane();

    public InventarioView(InventarioController controller) {
        this.controller = controller;
        inicializarUI();
    }

    private void inicializarUI() {
        // Configuración del contenedor principal
        view = new BorderPane();
        view.getStyleClass().add("inventario-view");
        view.getStylesheets().add(
                getClass().getResource("/styles/inventario/styles.css")
                        .toExternalForm()
        );

        // Añadir contenedor para diálogos JFoenix
        dialogContainer.setAlignment(Pos.CENTER);
        view.getChildren().add(dialogContainer);

        // Crear barra superior con estilo material design
        configurarBarraSuperior();

        // Configurar la tabla con mejoras visuales
        configurarTabla();

        // Panel lateral que se mostrará al seleccionar un equipo
        VBox panelLateral = new VBox();
        panelLateral.getStyleClass().add("panel-lateral");
        view.setRight(panelLateral);

        // Cargar datos iniciales
        actualizarTabla();

        // Aplicar animación de entrada
        new FadeIn(view).setSpeed(1.5).play();
    }

    private void configurarBarraSuperior() {
        HBox barraSuperior = new HBox(15);
        barraSuperior.setPadding(new Insets(15));
        barraSuperior.setAlignment(Pos.CENTER_LEFT);
        barraSuperior.getStyleClass().add("barra-superior");

        // Campo de búsqueda con ícono
        HBox contenedorBusqueda = new HBox(10);
        contenedorBusqueda.setAlignment(Pos.CENTER_LEFT);

        FontIcon iconoBuscar = new FontIcon(FontAwesomeSolid.SEARCH);
        iconoBuscar.setIconColor(Color.web("#757575"));
        iconoBuscar.setIconSize(16);

        campoBusqueda = new JFXTextField();
        campoBusqueda.setPromptText("Buscar equipos...");
        campoBusqueda.getStyleClass().add("busqueda-field");
        campoBusqueda.setFocusColor(Color.web("#2196F3"));
        campoBusqueda.setPrefWidth(250);

        JFXButton botonBuscar = new JFXButton();
        botonBuscar.setGraphic(iconoBuscar);
        botonBuscar.setButtonType(JFXButton.ButtonType.FLAT);
        botonBuscar.setOnAction(e -> {
            new Pulse(campoBusqueda).play();
            buscarEquipos();
        });

        contenedorBusqueda.getChildren().addAll(iconoBuscar, campoBusqueda);

        // Combo de filtro por categoría
        comboFiltroCategoria = new JFXComboBox<>();
        comboFiltroCategoria.setPromptText("Filtrar por categoría");
        comboFiltroCategoria.getStyleClass().add("combo-categoria");
        comboFiltroCategoria.setFocusColor(Color.web("#2196F3"));
        comboFiltroCategoria.getItems().add("Todas las categorías");
        comboFiltroCategoria.getItems().addAll(controller.listarCategorias().stream()
                .map(c -> c.getNombre()).toList());
        comboFiltroCategoria.getSelectionModel().selectFirst();
        comboFiltroCategoria.setOnAction(e -> filtrarPorCategoria());

        // Botones de acción con íconos
        JFXButton botonAgregar = new JFXButton("Agregar Equipo");
        botonAgregar.getStyleClass().add("agregar-button");
        botonAgregar.setButtonType(JFXButton.ButtonType.RAISED);
        FontIcon iconoAgregar = new FontIcon(FontAwesomeSolid.PLUS_CIRCLE);
        iconoAgregar.setIconColor(Color.WHITE);
        botonAgregar.setGraphic(iconoAgregar);
        botonAgregar.setOnAction(e -> {
            new Bounce(botonAgregar).play();
            mostrarFormularioNuevoEquipo();
        });

        JFXButton botonGestionCategorias = new JFXButton("Gestionar Categorías");
        botonGestionCategorias.getStyleClass().add("categorias-button");
        botonGestionCategorias.setButtonType(JFXButton.ButtonType.RAISED);
        FontIcon iconoCategorias = new FontIcon(FontAwesomeSolid.TAGS);
        iconoCategorias.setIconColor(Color.WHITE);
        botonGestionCategorias.setGraphic(iconoCategorias);
        botonGestionCategorias.setOnAction(e -> {
            new Bounce(botonGestionCategorias).play();
            mostrarDialogoCategorias();
        });

        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        barraSuperior.getChildren().addAll(
                contenedorBusqueda,
                comboFiltroCategoria,
                espaciador,
                botonAgregar,
                botonGestionCategorias
        );

        view.setTop(barraSuperior);
    }

    private void configurarTabla() {
        // Crear una JFXTreeTableView en lugar de TableView estándar
        JFXTreeTableColumn<EquipoDTO, String> columnaNombre = new JFXTreeTableColumn<>("Nombre");
        columnaNombre.setCellValueFactory(param -> param.getValue().getValue().nombreProperty());

        JFXTreeTableColumn<EquipoDTO, String> columnaCategoria = new JFXTreeTableColumn<>("Categoría");
        columnaCategoria.setCellValueFactory(param -> param.getValue().getValue().categoriaProperty());

        JFXTreeTableColumn<EquipoDTO, String> columnaUbicacion = new JFXTreeTableColumn<>("Ubicación");
        columnaUbicacion.setCellValueFactory(param -> param.getValue().getValue().ubicacionProperty());

        JFXTreeTableColumn<EquipoDTO, Number> columnaStock = new JFXTreeTableColumn<>("Stock");
        columnaStock.setCellValueFactory(param -> param.getValue().getValue().cantidadProperty());

        JFXTreeTableColumn<EquipoDTO, Number> columnaMinimo = new JFXTreeTableColumn<>("Mínimo");
        columnaMinimo.setCellValueFactory(param -> param.getValue().getValue().cantidadMinimaProperty());

        // Añadir columna de estado con ícono basado en el stock
        JFXTreeTableColumn<EquipoDTO, String> columnaEstado = new JFXTreeTableColumn<>("Estado");
        columnaEstado.setCellFactory(column -> new JFXTreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    EquipoDTO equipo = getTreeTableRow().getItem();
                    if (equipo != null) {
                        FontIcon icono;
                        if (equipo.getCantidad() <= equipo.getCantidadMinima()) {
                            icono = new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE);
                            icono.setIconColor(Color.web("#FFC107"));
                        } else {
                            icono = new FontIcon(FontAwesomeSolid.CHECK_CIRCLE);
                            icono.setIconColor(Color.web("#4CAF50"));
                        }
                        setGraphic(icono);
                    }
                }
            }
        });

        tablaEquipos = new JFXTreeTableView<>();
        tablaEquipos.getStyleClass().add("equipos-table");
        tablaEquipos.setShowRoot(false);
        tablaEquipos.setEditable(false);

        tablaEquipos.getColumns().setAll(
                columnaNombre,
                columnaCategoria,
                columnaUbicacion,
                columnaStock,
                columnaMinimo,
                columnaEstado
        );

        // Configurar el comportamiento de selección
        tablaEquipos.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        EquipoDTO equipo = newSelection.getValue();
                        mostrarDetalleEquipo(equipo);
                    }
                });

        // Placeholder para tabla vacía
        Label placeholderLabel = new Label("No hay equipos disponibles");
        placeholderLabel.getStyleClass().add("placeholder-label");
        FontIcon iconoVacio = new FontIcon(FontAwesomeSolid.BOX_OPEN);
        iconoVacio.setIconSize(48);
        placeholderLabel.setGraphic(iconoVacio);
        tablaEquipos.setPlaceholder(placeholderLabel);

        // Contenedor para la tabla con padding
        VBox contenedorTabla = new VBox(tablaEquipos);
        contenedorTabla.setPadding(new Insets(0, 15, 15, 15));
        contenedorTabla.setSpacing(10);
        VBox.setVgrow(tablaEquipos, Priority.ALWAYS);

        view.setCenter(contenedorTabla);
    }

    private void buscarEquipos() {
        String criterio = campoBusqueda.getText();
        List<EquipoDTO> resultados;

        if (criterio.isEmpty()) {
            resultados = controller.listarEquipos();
        } else {
            resultados = controller.buscarEquipos(criterio);
        }

        actualizarTablaConDatos(resultados);
        new Flash(tablaEquipos).play();
    }

    private void filtrarPorCategoria() {
        String categoriaSeleccionada = comboFiltroCategoria.getValue();

        if (categoriaSeleccionada == null || categoriaSeleccionada.equals("Todas las categorías")) {
            actualizarTabla();
        } else {
            List<EquipoDTO> equipos = controller.buscarEquiposPorCategoria(categoriaSeleccionada);
            actualizarTablaConDatos(equipos);
        }

        new Flash(tablaEquipos).play();
    }

    private void mostrarFormularioNuevoEquipo() {
        JFXDialogLayout contenido = new JFXDialogLayout();
        contenido.setHeading(new Label("Nuevo Equipo"));

        EquipoForm formulario = new EquipoForm(controller, null);
        contenido.setBody(formulario.getDialogPane()); // Usamos getDialogPane() del Dialog

        JFXDialog dialog = new JFXDialog(dialogContainer, contenido, JFXDialog.DialogTransition.CENTER);

        JFXButton btnCerrar = new JFXButton("Cerrar");
        btnCerrar.setOnAction(event -> {
            dialog.close();
            actualizarTabla();
        });

        contenido.setActions(btnCerrar);
        dialog.show();

        new ZoomIn(contenido).play();
    }

    private void mostrarDialogoCategorias() {
        Stage ownerStage = (Stage) view.getScene().getWindow();
        CategoriaWindow dialog = new CategoriaWindow(controller, ownerStage);
        dialog.mostrar();
        actualizarCombosCategorias();
    }

    private void mostrarDetalleEquipo(EquipoDTO equipo) {
        if (equipo != null) {
            EquipoCard tarjetaDetalle = new EquipoCard(equipo, controller);

            // Aplicar animación al mostrar el detalle
            view.setRight(tarjetaDetalle);
            new SlideInRight(tarjetaDetalle).play();
        } else {
            if (view.getRight() != null) {
                Node panelActual = view.getRight();
                SlideOutRight salida = new SlideOutRight(panelActual);
                salida.setOnFinished(e -> view.setRight(null));
                salida.play();
            }
        }
    }

    private void actualizarTabla() {
        List<EquipoDTO> equipos = controller.listarEquipos();
        actualizarTablaConDatos(equipos);
    }

    private void actualizarTablaConDatos(List<EquipoDTO> equipos) {
        // Convertir lista de equipos a formato de TreeItem para JFXTreeTableView
        TreeItem<EquipoDTO> root = new RecursiveTreeItem<EquipoDTO>(FXCollections.observableArrayList(equipos),
                RecursiveTreeObject::getChildren);
        tablaEquipos.setRoot(root);
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