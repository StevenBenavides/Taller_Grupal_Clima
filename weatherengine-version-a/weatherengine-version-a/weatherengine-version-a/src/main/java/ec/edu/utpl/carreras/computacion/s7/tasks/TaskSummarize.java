package ec.edu.utpl.carreras.computacion.s7.tasks;

import ec.edu.utpl.carreras.computacion.s7.model.AnnualClimateSummary;
import ec.edu.utpl.carreras.computacion.s7.model.ClimateRecord;
import ec.edu.utpl.carreras.computacion.s7.model.OverallClimateSummary;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Tarea principal Callable que orquesta el análisis de datos climáticos.
 * Lee el archivo CSV, filtra los datos por un rango de años (2000-2025),
 * agrupa los datos por año, y envía tareas (AnnualSummaryTask)
 * a un ExecutorService para procesamiento paralelo. Finalmente, agrega los resultados
 * anuales para obtener un resumen climático general.
 */
public class TaskSummarize implements Callable<OverallClimateSummary> {
    private final String path2Data;
    private final ExecutorService executorService;

    // Definición de los años mínimos y máximos para el filtro.
    private static final int MIN_YEAR = 2006;
    private static final int MAX_YEAR = 2016;

    /**
     * Constructor para TaskSummarize.
     *
     * @param path2Data       La ruta al archivo CSV de datos climáticos.
     * @param executorService El ExecutorService a utilizar para la ejecución de tareas paralelas.
     */
    public TaskSummarize(String path2Data, ExecutorService executorService) {
        this.path2Data = path2Data;
        this.executorService = executorService;
    }

    /**
     * El método principal de la tarea que coordina el análisis.
     *
     * @return Un objeto OverallClimateSummary con los resúmenes anuales y los extremos globales.
     */
    @Override
    public OverallClimateSummary call() {
        try {
            // 1. Leer todos los registros climáticos del archivo CSV.
            var allRecords = getDataAsList(path2Data);

            if (allRecords.isEmpty()) {
                System.out.println("No se encontraron datos en el archivo CSV.");
                return new OverallClimateSummary(new HashMap<>(), null, null, null, null, null, null, null, null);
            }

            // --- CAMBIO CLAVE: FILTRAR REGISTROS POR AÑO (2000-2025) ---
            List<ClimateRecord> filteredRecords = allRecords.stream()
                    .filter(record -> {
                        int year = record.fecha().getYear();
                        return year >= MIN_YEAR && year <= MAX_YEAR;
                    })
                    .collect(Collectors.toList());

            if (filteredRecords.isEmpty()) {
                System.out.println("No se encontraron datos dentro del rango de años " + MIN_YEAR + "-" + MAX_YEAR + ".");
                return new OverallClimateSummary(new HashMap<>(), null, null, null, null, null, null, null, null);
            }

            // 2. Agrupar los registros filtrados por año.
            Map<Integer, List<ClimateRecord>> recordsByYear = filteredRecords.stream()
                    .collect(Collectors.groupingBy(record -> record.fecha().getYear()));

            // 3. Crear y enviar tareas Callable (AnnualSummaryTask) para cada año al ExecutorService.
            List<Future<AnnualClimateSummary>> futures = new ArrayList<>();
            for (Map.Entry<Integer, List<ClimateRecord>> entry : recordsByYear.entrySet()) {
                int year = entry.getKey();
                List<ClimateRecord> yearRecords = entry.getValue();
                AnnualSummaryTask task = new AnnualSummaryTask(year, yearRecords);
                futures.add(executorService.submit(task)); // Envía la tarea y obtiene un Future.
            }

            // 4. Recolectar los resultados de los Futures y agregarlos en un mapa de resúmenes anuales.
            Map<Integer, AnnualClimateSummary> annualSummaries = new HashMap<>();
            for (Future<AnnualClimateSummary> future : futures) {
                try {
                    AnnualClimateSummary summary = future.get(); // Bloquea hasta que la tarea se complete.
                    annualSummaries.put(summary.year(), summary);
                } catch (Exception e) {
                    System.err.println("Error al procesar la tarea de resumen anual: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // 5. Encontrar los registros extremos generales (a través de todos los años filtrados)
            // Inicializa los registros extremos globales con el primer registro disponible del conjunto de datos filtrado.
            ClimateRecord overallColdestRecord = filteredRecords.get(0);
            ClimateRecord overallHottestRecord = filteredRecords.get(0);
            ClimateRecord overallMinVisibilityRecord = filteredRecords.get(0);
            ClimateRecord overallMaxVisibilityRecord = filteredRecords.get(0);
            ClimateRecord overallMinHumidityRecord = filteredRecords.get(0);
            ClimateRecord overallMaxHumidityRecord = filteredRecords.get(0);
            ClimateRecord overallMinWindSpeedRecord = filteredRecords.get(0);
            ClimateRecord overallMaxWindSpeedRecord = filteredRecords.get(0);

            // Itera a través de los resúmenes anuales para encontrar los extremos globales.
            for (AnnualClimateSummary summary : annualSummaries.values()) {
                if (summary.coldestRecord() != null && summary.coldestRecord().temp() < overallColdestRecord.temp()) {
                    overallColdestRecord = summary.coldestRecord();
                }
                if (summary.hottestRecord() != null && summary.hottestRecord().temp() > overallHottestRecord.temp()) {
                    overallHottestRecord = summary.hottestRecord();
                }
                if (summary.minVisibilityRecord() != null && summary.minVisibilityRecord().visibility() < overallMinVisibilityRecord.visibility()) {
                    overallMinVisibilityRecord = summary.minVisibilityRecord();
                }
                if (summary.maxVisibilityRecord() != null && summary.maxVisibilityRecord().visibility() > overallMaxVisibilityRecord.visibility()) {
                    overallMaxVisibilityRecord = summary.maxVisibilityRecord();
                }
                if (summary.minHumidityRecord() != null && summary.minHumidityRecord().humidity() < overallMinHumidityRecord.humidity()) {
                    overallMinHumidityRecord = summary.minHumidityRecord();
                }
                if (summary.maxHumidityRecord() != null && summary.maxHumidityRecord().humidity() > overallMaxHumidityRecord.humidity()) {
                    overallMaxHumidityRecord = summary.maxHumidityRecord();
                }
                if (summary.minWindSpeedRecord() != null && summary.minWindSpeedRecord().windSpeed() < overallMinWindSpeedRecord.windSpeed()) {
                    overallMinWindSpeedRecord = summary.minWindSpeedRecord();
                }
                if (summary.maxWindSpeedRecord() != null && summary.maxWindSpeedRecord().windSpeed() > overallMaxWindSpeedRecord.windSpeed()) {
                    overallMaxWindSpeedRecord = summary.maxWindSpeedRecord();
                }
            }

            // Devuelve el resumen climático general completo.
            return new OverallClimateSummary(
                    annualSummaries,
                    overallColdestRecord,
                    overallHottestRecord,
                    overallMinVisibilityRecord,
                    overallMaxVisibilityRecord,
                    overallMinHumidityRecord,
                    overallMaxHumidityRecord,
                    overallMinWindSpeedRecord,
                    overallMaxWindSpeedRecord
            );

        } catch (IOException ioe) {
            System.err.println("Error al leer el archivo CSV: " + ioe.getMessage());
            ioe.printStackTrace();
        } catch (Exception e) {
            System.err.println("Ocurrió un error inesperado durante el procesamiento: " + e.getMessage());
            e.printStackTrace();
        }
        // En caso de error, devuelve un resumen vacío o con valores por defecto.
        return new OverallClimateSummary(new HashMap<>(), null, null, null, null, null, null, null, null);
    }

    /**
     * Lee el archivo CSV y lo convierte en una lista de objetos ClimateRecord.
     * Utiliza Apache Commons CSV para un parseo robusto.
     *
     * @param path2Data La ruta al archivo CSV.
     * @return Una lista de ClimateRecord.
     * @throws IOException Si ocurre un error de lectura del archivo.
     */
    private List<ClimateRecord> getDataAsList(String path2Data) throws IOException {
        List<ClimateRecord> output = new ArrayList<>();
        // Configuración del formato CSV (RFC4180, con encabezado, saltar el encabezado).
        var csvFormat = CSVFormat
                .RFC4180
                .builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .get();

        // Abre el lector y el parser CSV en un bloque try-with-resources para asegurar el cierre.
        try (Reader reader = Files.newBufferedReader(Paths.get(path2Data));
             CSVParser parser = CSVParser.parse(reader, csvFormat)) {

            // Itera sobre cada registro CSV.
            for (CSVRecord csvRecord : parser) {
                try {
                    // Extrae los datos de las columnas.
                    // ATENCIÓN: Asegúrate que "Formatted Date" es el nombre exacto de la columna de fecha en tu CSV.
                    // Si es diferente (ej. "Date", "DateTime"), cámbialo aquí.
                    var dateStr = csvRecord.get("Formatted Date"); // Columna de fecha
                    var temp = Double.parseDouble(csvRecord.get("Temperature (C)"));
                    var humidity = Double.parseDouble(csvRecord.get("Humidity"));
                    var windSpeed = Double.parseDouble(csvRecord.get("Wind Speed (km/h)"));
                    var visibility = Double.parseDouble(csvRecord.get("Visibility (km)"));
                    var pressure = Double.parseDouble(csvRecord.get("Pressure (millibars)"));

                    // Crea un nuevo ClimateRecord y lo añade a la lista.
                    output.add(new ClimateRecord(dateStr, temp, humidity, windSpeed, visibility, pressure));
                } catch (NumberFormatException e) {
                    System.err.println("Saltando registro debido a error de parseo numérico: " + csvRecord + " - " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    System.err.println("Saltando registro debido a columna CSV faltante: " + csvRecord + " - " + e.getMessage());
                }
            }
        }
        return output;
    }
}