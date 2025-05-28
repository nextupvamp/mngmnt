package im.infmngmt.ui;

import im.infmngmt.entity.TreatmentScheme;
import im.infmngmt.entity.TreatmentSchemeDrug;
import im.infmngmt.service.TreatmentSchemeService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

public class TreatmentSchemePanel extends JPanel {
    private final TreatmentSchemeService service;
    private final JTable schemesTable = new JTable();
    private final JTable drugsTable = new JTable();

    public TreatmentSchemePanel(TreatmentSchemeService service) {
        this.service = service;
        setLayout(new BorderLayout());

        schemesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateDrugsTable();
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JScrollPane(schemesTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(new JLabel("Препараты в схеме:"), BorderLayout.NORTH);
        bottomPanel.add(new JScrollPane(drugsTable), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        splitPane.setResizeWeight(0.5);

        add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addSchemeButton = new JButton("Добавить схему");
        JButton addDrugButton = new JButton("Добавить препарат");

        addSchemeButton.addActionListener(e -> addNewScheme());
        addDrugButton.addActionListener(e -> addDrugToScheme());

        buttonPanel.add(addSchemeButton);
        buttonPanel.add(addDrugButton);

        add(buttonPanel, BorderLayout.SOUTH);
        updateTables();
    }

    private void updateTables() {
        schemesTable.setModel(new SchemeTableModel(service.getAllSchemes()));
        updateDrugsTable();
    }

    private void updateDrugsTable() {
        int selectedRow = schemesTable.getSelectedRow();
        if (selectedRow >= 0) {
            TreatmentScheme scheme = ((SchemeTableModel) schemesTable.getModel()).getSchemeAt(selectedRow);
            //drugsTable.setModel(new SchemeDrugsTableModel(service.getDrugsInScheme(scheme.getId())));
        } else {
            drugsTable.setModel(new SchemeDrugsTableModel(List.of()));
        }
    }

    private void addNewScheme() {
        JOptionPane.showMessageDialog(this, "Функциональность в разработке");
    }

    private void addDrugToScheme() {
        int selectedRow = schemesTable.getSelectedRow();
        if (selectedRow >= 0) {
            JOptionPane.showMessageDialog(this, "Функциональность в разработке");
        } else {
            JOptionPane.showMessageDialog(this, "Выберите схему для добавления препарата");
        }
    }

    private static class SchemeTableModel extends AbstractTableModel {
        private final List<TreatmentScheme> schemes;
        private final String[] columns = {"ID", "Тип", "Период", "Врач"};

        public SchemeTableModel(List<TreatmentScheme> schemes) {
            this.schemes = schemes;
        }

        @Override
        public int getRowCount() {
            return schemes.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            TreatmentScheme s = schemes.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> s.getId();
                case 1 -> s.getTreatmentType();
                case 2 -> s.getStartDate() + " - " + s.getEndDate();
                case 3 -> s.getDoctor() != null ? s.getDoctor().getFullName() : "Не указан";
                default -> null;
            };
        }

        public TreatmentScheme getSchemeAt(int row) {
            return schemes.get(row);
        }
    }

    private static class SchemeDrugsTableModel extends AbstractTableModel {
        private final List<TreatmentSchemeDrug> drugs;
        private final String[] columns = {"Препарат", "Дозировка", "Частота", "Длительность"};

        public SchemeDrugsTableModel(List<TreatmentSchemeDrug> drugs) {
            this.drugs = drugs;
        }

        @Override
        public int getRowCount() {
            return drugs.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            TreatmentSchemeDrug d = drugs.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> d.getDrug().getCommercialName();
                case 1 -> d.getAdministrationScheme();
                case 2 -> d.getFrequencyPerDay() + " раз/день";
                case 3 -> d.getDurationDays() + " дней";
                default -> null;
            };
        }
    }
}