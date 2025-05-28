package im.infmngmt.ui;

import im.infmngmt.entity.Drug;
import im.infmngmt.service.DrugService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

public class DrugPanel extends JPanel {
    private final DrugService service;
    private final JTable table = new JTable();

    public DrugPanel(DrugService service) {
        this.service = service;
        setLayout(new BorderLayout());
        updateTable();

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(new JButton("Добавить"));
        buttonPanel.add(new JButton("Изменить"));
        buttonPanel.add(new JButton("Удалить"));

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateTable() {
        table.setModel(new DrugTableModel(service.getAllDrugs()));
    }

    private static class DrugTableModel extends AbstractTableModel {
        private final List<Drug> drugs;
        private final String[] columns = {"ID", "МНН", "Действующее вещество", "Форма выпуска"};

        public DrugTableModel(List<Drug> drugs) {
            this.drugs = drugs;
        }

        @Override public int getRowCount() { return drugs.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }

        @Override
        public Object getValueAt(int row, int column) {
            Drug drug = drugs.get(row);
            return switch (column) {
                case 0 -> drug.getId();
                case 1 -> drug.getMnn();
                case 2 -> drug.getActiveSubstance();
                case 3 -> drug.getDosageForm();
                default -> null;
            };
        }
    }
}