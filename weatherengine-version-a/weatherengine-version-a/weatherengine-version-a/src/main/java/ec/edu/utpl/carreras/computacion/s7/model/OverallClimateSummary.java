package ec.edu.utpl.carreras.computacion.s7.model;

import java.util.Map;

/**
 * Representa el resumen climático general de todos los años, incluyendo
 * un mapa de resúmenes anuales y los registros extremos globales (a través de todos los años).
 *
 * @param annualSummaries         Un mapa donde la clave es el año y el valor es el resumen climático anual.
 * @param overallColdestRecord    El registro climático con la temperatura más baja de todo el conjunto de datos.
 * @param overallHottestRecord    El registro climático con la temperatura más alta de todo el conjunto de datos.
 * @param overallMinVisibilityRecord El registro climático con la visibilidad más baja de todo el conjunto de datos.
 * @param overallMaxVisibilityRecord El registro climático con la visibilidad más alta de todo el conjunto de datos.
 * @param overallMinHumidityRecord El registro climático con la humedad más baja de todo el conjunto de datos.
 * @param overallMaxHumidityRecord El registro climático con la humedad más alta de todo el conjunto de datos.
 * @param overallMinWindSpeedRecord El registro climático con la velocidad del viento más baja de todo el conjunto de datos.
 * @param overallMaxWindSpeedRecord El registro climático con la velocidad del viento más alta de todo el conjunto de datos.
 */
public record OverallClimateSummary(
        Map<Integer, AnnualClimateSummary> annualSummaries,
        ClimateRecord overallColdestRecord,
        ClimateRecord overallHottestRecord,
        ClimateRecord overallMinVisibilityRecord,
        ClimateRecord overallMaxVisibilityRecord,
        ClimateRecord overallMinHumidityRecord,
        ClimateRecord overallMaxHumidityRecord,
        ClimateRecord overallMinWindSpeedRecord,
        ClimateRecord overallMaxWindSpeedRecord
) {
}