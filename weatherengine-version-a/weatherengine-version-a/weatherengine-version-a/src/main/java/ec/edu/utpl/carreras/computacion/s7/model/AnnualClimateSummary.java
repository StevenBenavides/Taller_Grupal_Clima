package ec.edu.utpl.carreras.computacion.s7.model;

/**
 * Representa el resumen climático para un año específico, incluyendo promedios
 * y los registros extremos (más frío, más caluroso, etc.) dentro de ese año.
 *
 * @param year                   El año al que corresponde este resumen.
 * @param tempAvg                Temperatura promedio anual.
 * @param humidityAvg            Humedad promedio anual.
 * @param windSpeedAvg           Velocidad del viento promedio anual.
 * @param visibilityAvg          Visibilidad promedio anual.
 * @param pressureAvg            Presión promedio anual.
 * @param coldestRecord          El registro climático con la temperatura más baja del año.
 * @param hottestRecord          El registro climático con la temperatura más alta del año.
 * @param minVisibilityRecord    El registro climático con la visibilidad más baja del año.
 * @param maxVisibilityRecord    El registro climático con la visibilidad más alta del año.
 * @param minHumidityRecord      El registro climático con la humedad más baja del año.
 * @param maxHumidityRecord      El registro climático con la humedad más alta del año.
 * @param minWindSpeedRecord     El registro climático con la velocidad del viento más baja del año.
 * @param maxWindSpeedRecord     El registro climático con la velocidad del viento más alta del año.
 */
public record AnnualClimateSummary(
        int year,
        double tempAvg,
        double humidityAvg,
        double windSpeedAvg,
        double visibilityAvg,
        double pressureAvg,
        ClimateRecord coldestRecord,
        ClimateRecord hottestRecord,
        ClimateRecord minVisibilityRecord,
        ClimateRecord maxVisibilityRecord,
        ClimateRecord minHumidityRecord,
        ClimateRecord maxHumidityRecord,
        ClimateRecord minWindSpeedRecord,
        ClimateRecord maxWindSpeedRecord
) {
}