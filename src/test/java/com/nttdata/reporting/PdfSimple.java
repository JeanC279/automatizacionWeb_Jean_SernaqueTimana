package com.nttdata.reporting;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Generador PDF A4 sencillo, Java puro (sin nuevas dependencias Maven). */
public final class PdfSimple {
    private static final Charset PDF_CHARSET = Charset.forName("windows-1252");

    private PdfSimple() { }

    public static void escribir(Path destino, List<String> lineas) throws IOException {
        List<String> ajustadas = new ArrayList<String>();
        for (String linea : lineas) {
            String normal = limpiar(linea == null ? "" : linea);
            if (normal.isEmpty()) {
                ajustadas.add("");
                continue;
            }
            // Espacio monoespaciado aproximado; recorte controlado y página nueva.
            while (normal.length() > 91) {
                int corte = normal.lastIndexOf(' ', 91);
                if (corte < 30) corte = 91;
                ajustadas.add(normal.substring(0, corte).trim());
                normal = "   " + normal.substring(corte).trim();
            }
            ajustadas.add(normal);
        }
        List<List<String>> paginas = new ArrayList<List<String>>();
        for (int i = 0; i < ajustadas.size(); i += 52) {
            paginas.add(new ArrayList<String>(ajustadas.subList(i, Math.min(i + 52, ajustadas.size()))));
        }
        if (paginas.isEmpty()) paginas.add(new ArrayList<String>());

        // Objetos PDF: catálogo(1), árbol de páginas(2), fuente(3), página y contenido (4...)
        List<byte[]> objetos = new ArrayList<byte[]>();
        objetos.add(bytes("<< /Type /Catalog /Pages 2 0 R >>"));
        StringBuilder hijos = new StringBuilder();
        for (int i = 0; i < paginas.size(); i++) {
            hijos.append(4 + i * 2).append(" 0 R ");
        }
        objetos.add(bytes("<< /Type /Pages /Kids [" + hijos + "] /Count " + paginas.size() + " >>"));
        objetos.add(bytes("<< /Type /Font /Subtype /Type1 /BaseFont /Courier /Encoding /WinAnsiEncoding >>"));
        for (int i = 0; i < paginas.size(); i++) {
            int paginaId = 4 + i * 2;
            int contenidoId = paginaId + 1;
            objetos.add(bytes("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] " +
                    "/Resources << /Font << /F1 3 0 R >> >> /Contents " + contenidoId + " 0 R >>"));
            StringBuilder comandos = new StringBuilder("BT\n/F1 9 Tf\n46 789 Td\n13 TL\n");
            for (String linea : paginas.get(i)) {
                comandos.append('(').append(escapar(linea)).append(") Tj\nT*\n");
            }
            comandos.append("ET\n");
            byte[] datos = bytes(comandos.toString());
            ByteArrayOutputStream contenido = new ByteArrayOutputStream();
            contenido.write(bytes("<< /Length " + datos.length + " >>\nstream\n"));
            contenido.write(datos);
            contenido.write(bytes("endstream"));
            objetos.add(contenido.toByteArray());
        }
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        salida.write(bytes("%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n"));
        List<Integer> posiciones = new ArrayList<Integer>();
        posiciones.add(0);
        for (int i = 0; i < objetos.size(); i++) {
            posiciones.add(salida.size());
            salida.write(bytes((i + 1) + " 0 obj\n"));
            salida.write(objetos.get(i));
            salida.write(bytes("\nendobj\n"));
        }
        int xref = salida.size();
        salida.write(bytes("xref\n0 " + (objetos.size() + 1) + "\n0000000000 65535 f \n"));
        for (int i = 1; i < posiciones.size(); i++) {
            salida.write(bytes(String.format(java.util.Locale.ROOT, "%010d 00000 n \n", posiciones.get(i))));
        }
        salida.write(bytes("trailer\n<< /Size " + (objetos.size() + 1) + " /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF\n"));
        Files.createDirectories(destino.toAbsolutePath().getParent());
        Files.write(destino, salida.toByteArray());
    }

    private static String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private static String limpiar(String texto) {
        StringBuilder salida = new StringBuilder();
        for (char caracter : texto.toCharArray()) {
            if (caracter == '\n' || caracter == '\r' || caracter == '\t') {
                salida.append(' ');
            } else if (PDF_CHARSET.newEncoder().canEncode(caracter)) {
                salida.append(caracter);
            } else {
                salida.append('?');
            }
        }
        return salida.toString();
    }

    private static byte[] bytes(String texto) {
        return texto.getBytes(PDF_CHARSET);
    }
}
