package tescha.MainWindow.components;

import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.BarChart;

public class CustomBarChart extends BarChart<String, Number> {
    public CustomBarChart(CategoryAxis xAxis, NumberAxis yAxis) {
        super(xAxis, yAxis);
        this.getStyleClass().add("modern-bar-chart");
    }
}