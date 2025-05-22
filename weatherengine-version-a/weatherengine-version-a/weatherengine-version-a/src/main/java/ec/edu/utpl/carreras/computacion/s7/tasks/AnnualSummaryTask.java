package ec.edu.utpl.carreras.computacion.s7.tasks;

import ec.edu.utpl.carreras.computacion.s7.model.AnnualClimateSummary;
import ec.edu.utpl.carreras.computacion.s7.model.ClimateRecord;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Tarea Callable para calcular el resumen climático (promedios y extremos)
 * para un conjunto de registros climáticos que pertenecen a un año específico.
 * Esta tarea realiza una única pasada sobre los datos para optimizar el rendimiento.
 */
public class AnnualSummaryTask implements Callable<AnnualClimateSummary> {
    private final int year;
    private final List<ClimateRecord> yearRecords;

    /**
     * Constructor para AnnualSummaryTask.
     *
     * @param year        El año para el cual se procesarán los registros.
     * @param yearRecords Una lista de registros climáticos para ese año.
     */
    public AnnualSummaryTask(int year, List<ClimateRecord> yearRecords) {
        this.year = year;
        this.yearRecords = yearRecords;
    }

    /**
     * El método principal de la tarea que realiza el cálculo.
     *
     * @return Un objeto AnnualClimateSummary que contiene los promedios y extremos del año.
     */
    @Override
    public AnnualClimateSummary call() {
        // Si no hay registros para el año, devuelve un resumen vacío para evitar errores.
        if (yearRecords.isEmpty()) {
            return new AnnualClimateSummary(year, 0.0, 0.0, 0.0, 0.0, 0.0,
                    null, null, null, null, null, null, null, null);
        }

        // Variables para acumular las sumas de los valores para calcular promedios.
        double sumTemp = 0;
        double sumHumidity = 0;
        double sumWindSpeed = 0;
        double sumVisibility = 0;
        double sumPressure = 0;

        // Inicializa los registros extremos con el primer registro disponible.
        // Esto evita problemas con valores iniciales como Double.MAX_VALUE o Double.MIN_VALUE.
        ClimateRecord coldestRecord = yearRecords.get(0);
        ClimateRecord hottestRecord = yearRecords.get(0);
        ClimateRecord minVisibilityRecord = yearRecords.get(0);
        ClimateRecord maxVisibilityRecord = yearRecords.get(0);
        ClimateRecord minHumidityRecord = yearRecords.get(0);
        ClimateRecord maxHumidityRecord = yearRecords.get(0);
        ClimateRecord minWindSpeedRecord = yearRecords.get(0);
        ClimateRecord maxWindSpeedRecord = yearRecords.get(0);

        // Bucle de una sola pasada: calcula sumas y encuentra extremos simultáneamente.
        for (ClimateRecord record : yearRecords) {
            sumTemp += record.temp();
            sumHumidity += record.humidity();
            sumWindSpeed += record.windSpeed();
            sumVisibility += record.visibility();
            sumPressure += record.pressure();

            // Actualiza los registros de temperatura extrema
            if (record.temp() < coldestRecord.temp()) {
                coldestRecord = record;
            }
            if (record.temp() > hottestRecord.temp()) {
                hottestRecord = record;
            }

            // Actualiza los registros de visibilidad extrema
            if (record.visibility() < minVisibilityRecord.visibility()) {
                minVisibilityRecord = record;
            }
            if (record.visibility() > maxVisibilityRecord.visibility()) {
                maxVisibilityRecord = record;
            }

            // Actualiza los registros de humedad extrema
            if (record.humidity() < minHumidityRecord.humidity()) {
                minHumidityRecord = record;
            }
            if (record.humidity() > maxHumidityRecord.humidity()) {
                maxHumidityRecord = record;
            }

            // Actualiza los registros de velocidad del viento extrema
            if (record.windSpeed() < minWindSpeedRecord.windSpeed()) {
                minWindSpeedRecord = record;
            }
            if (record.windSpeed() > maxWindSpeedRecord.windSpeed()) {
                maxWindSpeedRecord = record;
            }
        }

        // Calcula los promedios.
        int count = yearRecords.size();
        double tempAvg = sumTemp / count;
        double humidityAvg = sumHumidity / count;
        double windSpeedAvg = sumWindSpeed / count;
        double visibilityAvg = sumVisibility / count;
        double pressureAvg = sumPressure / count;

        // Devuelve el resumen anual completo.
        return new AnnualClimateSummary(
                year,
                tempAvg,
                humidityAvg,
                windSpeedAvg,
                visibilityAvg,
                pressureAvg,
                coldestRecord,
                hottestRecord,
                minVisibilityRecord,
                maxVisibilityRecord,
                minHumidityRecord,
                maxHumidityRecord,
                minWindSpeedRecord,
                maxWindSpeedRecord
        );
    }
}