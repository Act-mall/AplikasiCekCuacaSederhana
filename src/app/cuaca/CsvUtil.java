package app.cuaca;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {

    public static void saveTableToCSV(JTable table, File file) throws IOException {
        TableModel m = table.getModel();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {

            // header
            for (int c = 0; c < m.getColumnCount(); c++) {
                if (c > 0) pw.print(",");
                pw.print(escape(m.getColumnName(c)));
            }
            pw.println();

            // rows
            for (int r = 0; r < m.getRowCount(); r++) {
                for (int c = 0; c < m.getColumnCount(); c++) {
                    if (c > 0) pw.print(",");
                    Object v = m.getValueAt(r, c);
                    pw.print(escape(v == null ? "" : v.toString()));
                }
                pw.println();
            }
        }
    }

    public static DefaultTableModel loadCSV(File file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) rows.add(parseLine(line));
        }
        if (rows.isEmpty()) return new DefaultTableModel();
        String[] header = rows.get(0);
        DefaultTableModel model = new DefaultTableModel(header, 0);
        for (int i = 1; i < rows.size(); i++) model.addRow(rows.get(i));
        return model;
    }

    // --- helper ---
    private static String escape(String s) {
        boolean need = s.contains(",") || s.contains("\"") || s.contains("\n");
        return need ? "\"" + s.replace("\"", "\"\"") + "\"" : s;
    }

    private static String[] parseLine(String line) {
        List<String> out = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (inQuotes) {
                if (ch == '\"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '\"') { sb.append('\"'); i++; }
                    else inQuotes = false;
                } else sb.append(ch);
            } else {
                if (ch == '\"') inQuotes = true;
                else if (ch == ',') { out.add(sb.toString()); sb.setLength(0); }
                else sb.append(ch);
            }
        }
        out.add(sb.toString());
        return out.toArray(new String[0]);
    }
}
