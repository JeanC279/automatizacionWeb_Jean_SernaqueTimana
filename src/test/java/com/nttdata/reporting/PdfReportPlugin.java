package com.nttdata.reporting;

import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestRunFinished;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** PDF generado en TestRunFinished, incluso cuando hay escenarios fallidos. */
public class PdfReportPlugin implements EventListener {

    private final List<String> detalles = new ArrayList<String>();
    private int aprobados;
    private int fallidos;
    private int otros;
    private final String inicio = ahora();

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseFinished.class, this::registrarEscenario);
        publisher.registerHandlerFor(TestRunFinished.class, event -> generarReporte());
    }

    private void registrarEscenario(TestCaseFinished evento) {
        String estado = evento.getResult().getStatus().toString();
        if ("PASSED".equals(estado)) {
            aprobados++;
        } else if ("FAILED".equals(estado)) {
            fallidos++;
        } else {
            otros++;
        }
        detalles.add("[" + estado + "] " + evento.getTestCase().getName());
        if (evento.getResult().getError() != null) {
            String motivo = String.valueOf(evento.getResult().getError().getMessage());
            motivo = motivo.replace('\n', ' ').replace('\r', ' ');
            if (motivo.length() > 240) motivo = motivo.substring(0, 240) + "...";
            detalles.add("   Incidencia: " + motivo);
        }
    }

    private void generarReporte() {
        List<String> lineas = new ArrayList<String>();
        lineas.add("NTT | REPORTE DE REGRESION - TIENDA QALAB");
        lineas.add("Inicio: " + inicio + "     Fin: " + ahora());
        lineas.add("URL: https://qalab.bensg.com/store/pe/");
        lineas.add("Estado general: " + ((fallidos == 0 && otros == 0 && aprobados > 0) ? "APROBADO" : "REVISAR"));
        lineas.add("Escenarios: " + (aprobados + fallidos + otros) + " | Aprobados: " + aprobados
                + " | Fallidos: " + fallidos + " | Otros: " + otros);
        lineas.add("");
        lineas.add("DETALLE DE ESCENARIOS");
        lineas.addAll(detalles);
        lineas.add("");
        lineas.add("NOTA DE ALCANCE");
        lineas.add("La prueba de catalogo confirma productos visibles desde la web.");
        lineas.add("No verifica una conexion directa a la base de datos.");
        lineas.add("Las pruebas negativas APRUEBAN cuando el sistema rechaza la accion invalida.");
        lineas.add("Consultar target/cucumber/cucumber-report.html para pasos y capturas.");
        try {
            PdfSimple.escribir(Paths.get("target", "cucumber", "reporte-regresion.pdf"), lineas);
            System.out.println("Reporte PDF generado: target/cucumber/reporte-regresion.pdf");
        } catch (IOException ex) {
            System.err.println("ERROR al generar reporte PDF: " + ex.getMessage());
        }
    }

    private static String ahora() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
