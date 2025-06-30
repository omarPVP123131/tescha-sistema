package tescha.prestamos.util;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import org.json.*;

public class SolicitanteManager {
    private static final String JSON_FILE = "solicitantes.json";
    private List<String> solicitantes = new ArrayList<>();

    public SolicitanteManager() {
        cargarSolicitantes();
    }

    private void cargarSolicitantes() {
        try {
            if (Files.exists(Paths.get(JSON_FILE))) {
                String content = new String(Files.readAllBytes(Paths.get(JSON_FILE)));
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i < jsonArray.length(); i++) {
                    solicitantes.add(jsonArray.getString(i));
                }
            }
        } catch (Exception e) {
            System.err.println("Error al cargar solicitantes: " + e.getMessage());
        }
    }

    public List<String> buscarSolicitantes(String inicio) {
        List<String> resultados = new ArrayList<>();
        for (String nombre : solicitantes) {
            if (nombre.toLowerCase().startsWith(inicio.toLowerCase())) {
                resultados.add(nombre);
            }
        }
        return resultados;
    }

    public void agregarSolicitante(String nombre) {
        if (!solicitantes.contains(nombre)) {
            solicitantes.add(nombre);
            guardarSolicitantes();
        }
    }

    private void guardarSolicitantes() {
        try (FileWriter file = new FileWriter(JSON_FILE)) {
            JSONArray jsonArray = new JSONArray(solicitantes);
            file.write(jsonArray.toString());
        } catch (Exception e) {
            System.err.println("Error al guardar solicitantes: " + e.getMessage());
        }
    }

    public List<String> obtenerTodos() {
        return new ArrayList<>(solicitantes);
    }
}
