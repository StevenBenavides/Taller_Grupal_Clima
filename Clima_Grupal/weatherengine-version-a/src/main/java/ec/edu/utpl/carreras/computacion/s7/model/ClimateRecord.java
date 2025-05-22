package ec.edu.utpl.carreras.computacion.s7.model;
import java.time.format.DateTimeFormatter;

public record ClimateRecord(DateTimeFormatter fecha,double temp, double humidity, double windSpeed, double visibility, double pressure) {

}