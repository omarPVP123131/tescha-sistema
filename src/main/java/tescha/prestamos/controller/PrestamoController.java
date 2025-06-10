package tescha.prestamos.controller;

import tescha.prestamos.dto.PrestamoDTO;
import tescha.prestamos.service.PrestamoService;
import tescha.Components.AlertUtils;
import javafx.concurrent.Task;
import javafx.application.Platform;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

/**
 * Controlador para el módulo de préstamos
 */
public class PrestamoController {
    
    private final PrestamoService prestamoService;
    
    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }
    
    // Operaciones CRUD con manejo asíncrono
    public void crearPrestamo(PrestamoDTO prestamo, Consumer<Integer> onSuccess, Consumer<String> onError) {
        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                return prestamoService.crearPrestamo(prestamo);
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> onSuccess.accept(getValue()));
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    Throwable exception = getException();
                    String message = exception.getMessage();
                    if (exception instanceof IllegalArgumentException) {
                        onError.accept(message);
                    } else {
                        onError.accept("Error al crear el préstamo: " + message);
                    }
                });
            }
        };
        
        new Thread(task).start();
    }
    
    public void obtenerTodosLosPrestamos(Consumer<List<PrestamoDTO>> onSuccess, Consumer<String> onError) {
        Task<List<PrestamoDTO>> task = new Task<List<PrestamoDTO>>() {
            @Override
            protected List<PrestamoDTO> call() throws Exception {
                return prestamoService.obtenerTodosLosPrestamos();
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> onSuccess.accept(getValue()));
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> onError.accept("Error al cargar préstamos: " + getException().getMessage()));
            }
        };
        
        new Thread(task).start();
    }
    
    public void obtenerPrestamoPorId(int id, Consumer<PrestamoDTO> onSuccess, Consumer<String> onError) {
        Task<PrestamoDTO> task = new Task<PrestamoDTO>() {
            @Override
            protected PrestamoDTO call() throws Exception {
                return prestamoService.obtenerPrestamoPorId(id);
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> onSuccess.accept(getValue()));
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> onError.accept("Error al obtener préstamo: " + getException().getMessage()));
            }
        };
        
        new Thread(task).start();
    }
    
    public void actualizarPrestamo(PrestamoDTO prestamo, Runnable onSuccess, Consumer<String> onError) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return prestamoService.actualizarPrestamo(prestamo);
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (getValue()) {
                        onSuccess.run();
                    } else {
                        onError.accept("No se pudo actualizar el préstamo");
                    }
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> onError.accept("Error al actualizar préstamo: " + getException().getMessage()));
            }
        };
        
        new Thread(task).start();
    }
    
    public void eliminarPrestamo(int id, Runnable onSuccess, Consumer<String> onError) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return prestamoService.eliminarPrestamo(id);
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (getValue()) {
                        onSuccess.run();
                    } else {
                        onError.accept("No se pudo eliminar el préstamo");
                    }
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> onError.accept("Error al eliminar préstamo: " + getException().getMessage()));
            }
        };
        
        new Thread(task).start();
    }
    
    // Operaciones específicas
    public void procesarDevolucion(int prestamoId, String devueltoPor, String recibidoPor, String estadoDevuelto, 
                                 Runnable onSuccess, Consumer<String> onError) {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return prestamoService.procesarDevolucion(prestamoId, devueltoPor, recibidoPor, estadoDevuelto);
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (getValue()) {
                        onSuccess.run();
                    } else {
                        onError.accept("No se pudo procesar la devolución");
                    }
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> onError.accept("Error al procesar devolución: " + getException().getMessage()));
            }
        };
        
        new Thread(task).start();
    }
    
    // Consultas filtradas
    public void obtenerPrestamosActivos(Consumer<List<PrestamoDTO>> onSuccess, Consumer<String> onError) {
        executeListTask(() -> prestamoService.obtenerPrestamosActivos(), onSuccess, onError);
    }
    
    public void obtenerPrestamosVencidos(Consumer<List<PrestamoDTO>> onSuccess, Consumer<String> onError) {
        executeListTask(() -> prestamoService.obtenerPrestamosVencidos(), onSuccess, onError);
    }
    
    public void obtenerPrestamosDevueltos(Consumer<List<PrestamoDTO>> onSuccess, Consumer<String> onError) {
        executeListTask(() -> prestamoService.obtenerPrestamosDevueltos(), onSuccess, onError);
    }
    
    public void obtenerPrestamosQueVencenHoy(Consumer<List<PrestamoDTO>> onSuccess, Consumer<String> onError) {
        executeListTask(() -> prestamoService.obtenerPrestamosQueVencenHoy(), onSuccess, onError);
    }
    
    public void buscarPrestamos(String termino, Consumer<List<PrestamoDTO>> onSuccess, Consumer<String> onError) {
        executeListTask(() -> prestamoService.buscarPrestamos(termino), onSuccess, onError);
    }
    
    public void filtrarPorEstado(String estado, Consumer<List<PrestamoDTO>> onSuccess, Consumer<String> onError) {
        executeListTask(() -> prestamoService.filtrarPorEstado(estado), onSuccess, onError);
    }
    
    public void obtenerEstadisticas(Consumer<PrestamoService.EstadisticasPrestamos> onSuccess, Consumer<String> onError) {
        Task<PrestamoService.EstadisticasPrestamos> task = new Task<PrestamoService.EstadisticasPrestamos>() {
            @Override
            protected PrestamoService.EstadisticasPrestamos call() throws Exception {
                return prestamoService.obtenerEstadisticas();
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> onSuccess.accept(getValue()));
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> onError.accept("Error al obtener estadísticas: " + getException().getMessage()));
            }
        };
        
        new Thread(task).start();
    }
    
    // Método auxiliar para ejecutar tareas que retornan listas
    private void executeListTask(TaskSupplier<List<PrestamoDTO>> supplier, 
                               Consumer<List<PrestamoDTO>> onSuccess, 
                               Consumer<String> onError) {
        Task<List<PrestamoDTO>> task = new Task<List<PrestamoDTO>>() {
            @Override
            protected List<PrestamoDTO> call() throws Exception {
                return supplier.get();
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> onSuccess.accept(getValue()));
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> onError.accept("Error al ejecutar consulta: " + getException().getMessage()));
            }
        };
        
        new Thread(task).start();
    }
    
    @FunctionalInterface
    private interface TaskSupplier<T> {
        T get() throws Exception;
    }
}
