package ec.edu.utpl.carreras.computacion.s7;

import ec.edu.utpl.carreras.computacion.s7.model.AnnualClimateSummary;
import ec.edu.utpl.carreras.computacion.s7.model.OverallClimateSummary;
import ec.edu.utpl.carreras.computacion.s7.tasks.TaskSummarize;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; // Importa Executors para crear ExecutorService
import java.util.concurrent.Future;

/**
 * Clase principal de la aplicación para analizar datos climáticos.
 * Utiliza ExecutorService, Callable y Future para procesar los datos de forma concurrente,
 * ahora aprovechando los Virtual Threads de Java 21+.
 */
public class App {
    public static void main(String[] args) {
        // Define la ruta a tu archivo CSV.
        // IMPORTANTE: Reemplaza "./weatherHistory_1.csv" con la ruta real a tu archivo CSV.
        // Ejemplos: "C:/Users/TuUsuario/Documentos/weatherHistory_1.csv" o "/home/tuusuario/datos/weatherHistory_1.csv"
        String csvFilePath = "C:\\Users\\julio\\Downloads\\weatherengine-version-a\\weatherengine-version-a\\weatherHistory.csv"; // Asume que el CSV está en la raíz del proyecto o en una ruta accesible.

        // Crea un ExecutorService que utiliza Virtual Threads.
        // Cada tarea enviada a este ExecutorService se ejecutará en un nuevo Virtual Thread.
        // Esto es ideal para tareas que pueden estar ligadas a I/O (como la lectura de archivos)
        // ya que los Virtual Threads son muy ligeros y no agotan los hilos de plataforma.
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

        try {
            // Crea la tarea principal TaskSummarize, pasándole la ruta del archivo y el ExecutorService.
            var task = new TaskSummarize(csvFilePath, executorService);

            // Envía la tarea al servicio de ejecución y obtiene un objeto Future.
            // Future representa el resultado de una computación asíncrona.
            Future<OverallClimateSummary> futureSummary = executorService.submit(task);

            // Obtiene el resultado del Future. Este método se bloqueará hasta que la tarea se complete.
            OverallClimateSummary summary = futureSummary.get();

            // --- Impresión de los resultados ---
            System.out.println("--- Resumen Climático General ---");

            // Imprime los registros extremos globales, verificando que no sean nulos.
            if (summary.overallColdestRecord() != null) {
                System.out.printf("Fecha y Hora más Fría: %s (Temperatura: %.2f C)%n",
                        summary.overallColdestRecord().fecha(), summary.overallColdestRecord().temp());
            }
            if (summary.overallHottestRecord() != null) {
                System.out.printf("Fecha y Hora más Calurosa: %s (Temperatura: %.2f C)%n",
                        summary.overallHottestRecord().fecha(), summary.overallHottestRecord().temp());
            }
            if (summary.overallMinVisibilityRecord() != null) {
                System.out.printf("Fecha y Hora con Menor Visibilidad: %s (Visibilidad: %.2f km)%n",
                        summary.overallMinVisibilityRecord().fecha(), summary.overallMinVisibilityRecord().visibility());
            }
            if (summary.overallMaxVisibilityRecord() != null) {
                System.out.printf("Fecha y Hora con Mayor Visibilidad: %s (Visibilidad: %.2f km)%n",
                        summary.overallMaxVisibilityRecord().fecha(), summary.overallMaxVisibilityRecord().visibility());
            }
            if (summary.overallMinHumidityRecord() != null) {
                System.out.printf("Fecha y Hora con Menor Humedad: %s (Humedad: %.2f %%)%n",
                        summary.overallMinHumidityRecord().fecha(), summary.overallMinHumidityRecord().humidity());
            }
            if (summary.overallMaxHumidityRecord() != null) {
                System.out.printf("Fecha y Hora con Mayor Humedad: %s (Humedad: %.2f %%)%n",
                        summary.overallMaxHumidityRecord().fecha(), summary.overallMaxHumidityRecord().humidity());
            }
            if (summary.overallMinWindSpeedRecord() != null) {
                System.out.printf("Fecha y Hora con Menor Velocidad del Viento: %s (Velocidad: %.2f km/h)%n",
                        summary.overallMinWindSpeedRecord().fecha(), summary.overallMinWindSpeedRecord().windSpeed());
            }
            if (summary.overallMaxWindSpeedRecord() != null) {
                System.out.printf("Fecha y Hora con Mayor Velocidad del Viento: %s (Velocidad: %.2f km/h)%n",
                        summary.overallMaxWindSpeedRecord().fecha(), summary.overallMaxWindSpeedRecord().windSpeed());
            }

            System.out.println("\n--- Resumen Climático Anual ---");
            // Itera sobre los resúmenes anuales, ordenándolos por año para una mejor lectura.
            summary.annualSummaries().entrySet().stream()
                    .sorted(Comparator.comparingInt(Map.Entry::getKey)) // Ordena por año
                    .forEach(entry -> {
                        int year = entry.getKey();
                        AnnualClimateSummary annualSummary = entry.getValue();
                        System.out.printf("\nAño: %d%n", year);
                        System.out.printf("  Temperatura Promedio: %.2f C%n", annualSummary.tempAvg());
                        System.out.printf("  Humedad Promedio: %.2f %%%n", annualSummary.humidityAvg());
                        System.out.printf("  Velocidad del Viento Promedio: %.2f km/h%n", annualSummary.windSpeedAvg());
                        System.out.printf("  Visibilidad Promedio: %.2f km%n", annualSummary.visibilityAvg());
                        System.out.printf("  Presión Promedio: %.2f millibars%n", annualSummary.pressureAvg());

                        // Imprime los extremos anuales
                        if (annualSummary.coldestRecord() != null) {
                            System.out.printf("  Más Frío (Año %d): %s (Temperatura: %.2f C)%n", year, annualSummary.coldestRecord().fecha(), annualSummary.coldestRecord().temp());
                        }
                        if (annualSummary.hottestRecord() != null) {
                            System.out.printf("  Más Caluroso (Año %d): %s (Temperatura: %.2f C)%n", year, annualSummary.hottestRecord().fecha(), annualSummary.hottestRecord().temp());
                        }
                        if (annualSummary.minVisibilityRecord() != null) {
                            System.out.printf("  Menor Visibilidad (Año %d): %s (Visibilidad: %.2f km)%n", year, annualSummary.minVisibilityRecord().fecha(), annualSummary.minVisibilityRecord().visibility());
                        }
                        if (annualSummary.maxVisibilityRecord() != null) {
                            System.out.printf("  Mayor Visibilidad (Año %d): %s (Visibilidad: %.2f km)%n", year, annualSummary.maxVisibilityRecord().fecha(), annualSummary.maxVisibilityRecord().visibility());
                        }
                        if (annualSummary.minHumidityRecord() != null) {
                            System.out.printf("  Menor Humedad (Año %d): %s (Humedad: %.2f %%)%n", year, annualSummary.minHumidityRecord().fecha(), annualSummary.minHumidityRecord().humidity());
                        }
                        if (annualSummary.maxHumidityRecord() != null) {
                            System.out.printf("  Mayor Humedad (Año %d): %s (Humedad: %.2f %%)%n", year, annualSummary.maxHumidityRecord().fecha(), annualSummary.maxHumidityRecord().humidity());
                        }
                        if (annualSummary.minWindSpeedRecord() != null) {
                            System.out.printf("  Menor Velocidad del Viento (Año %d): %s (Velocidad: %.2f km/h)%n", year, annualSummary.minWindSpeedRecord().fecha(), annualSummary.minWindSpeedRecord().windSpeed());
                        }
                        if (annualSummary.maxWindSpeedRecord() != null) {
                            System.out.printf("  Mayor Velocidad del Viento (Año %d): %s (Velocidad: %.2f km/h)%n", year, annualSummary.maxWindSpeedRecord().fecha(), annualSummary.maxWindSpeedRecord().windSpeed());
                        }
                    });

        } catch (Exception e) {
            System.err.println("Ocurrió un error durante el procesamiento de los datos climáticos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Es CRUCIAL apagar el ExecutorService para liberar los recursos del hilo.
            // Esto asegura que todos los hilos se detengan correctamente.
            executorService.shutdown();
            System.out.println("\nExecutorService apagado.");
        }
    }
}