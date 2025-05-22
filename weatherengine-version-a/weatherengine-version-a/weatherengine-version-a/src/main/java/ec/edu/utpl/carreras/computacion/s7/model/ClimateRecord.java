package ec.edu.utpl.carreras.computacion.s7.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException; // Importa para manejo de excepciones

/**
 * Representa un registro climático individual con fecha, temperatura, humedad,
 * velocidad del viento, visibilidad y presión.
 *
 * @param fecha      La fecha y hora del registro.
 * @param temp       Temperatura en grados Celsius.
 * @param humidity   Humedad en porcentaje.
 * @param windSpeed  Velocidad del viento en km/h.
 * @param visibility Visibilidad en km.
 * @param pressure   Presión en millibars.
 */
public record ClimateRecord(LocalDateTime fecha, double temp, double humidity, double windSpeed, double visibility, double pressure) {
    // Formateadores estáticos para analizar la cadena de fecha del CSV.
    // Se definen múltiples formateadores para manejar variaciones en el formato de la fecha.
    // El orden importa: intenta el más específico primero.
    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = {
            // Formato con milisegundos y desplazamiento de zona horaria (ej. "2006-04-01 00:00:00.000 +0200")
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z"),
            // Formato sin milisegundos pero con desplazamiento de zona horaria (ej. "2006-04-01 00:00:00 +0200")
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z")
            // Se pueden añadir más formateadores aquí si se detectan otras variaciones (ej. sin desplazamiento, con 'T', etc.)
    };

    /**
     * Constructor que toma la fecha como una cadena y la parsea a LocalDateTime.
     * Esto es útil al leer los datos del archivo CSV.
     *
     * @param fechaStr   La fecha y hora como una cadena.
     * @param temp       Temperatura en grados Celsius.
     * @param humidity   Humedad en porcentaje.
     * @param windSpeed  Velocidad del viento en km/h.
     * @param visibility Visibilidad en km.
     * @param pressure   Presión en millibars.
     */
    public ClimateRecord(String fechaStr, double temp, double humidity, double windSpeed, double visibility, double pressure) {
        LocalDateTime parsedFecha = null;
        // Intenta parsear la fecha con cada formateador disponible.
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                parsedFecha = LocalDateTime.parse(fechaStr, formatter);
                break; // Si el parseo es exitoso, sal del bucle.
            } catch (DateTimeParseException e) {
                // Continúa con el siguiente formateador si este falla.
            }
        }

        // Si después de intentar con todos los formateadores la fecha sigue siendo nula,
        // significa que no se pudo parsear con ninguno de los patrones conocidos.
        if (parsedFecha == null) {
            System.err.println("Error: No se pudo parsear la fecha '" + fechaStr + "' con ningún formato conocido. Usando LocalDateTime.MIN.");
            parsedFecha = LocalDateTime.MIN; // Usar una fecha por defecto en caso de fallo.
        }

        this(parsedFecha, temp, humidity, windSpeed, visibility, pressure);
    }
}