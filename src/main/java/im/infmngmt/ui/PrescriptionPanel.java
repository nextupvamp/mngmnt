package im.infmngmt.ui;

import im.infmngmt.entity.Prescription;
import im.infmngmt.service.PrescriptionService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PrescriptionPanel extends JPanel {
    private final PrescriptionService service;
    private final JTable table = new JTable();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public PrescriptionPanel(PrescriptionService service) {
        this.service = service;
        setLayout(new BorderLayout());
        updateTable();

        JPanel buttonPanel = new JPanel();
        JButton newButton = new JButton("Новое назначение");
        JButton printButton = new JButton("Печать рецепта");
        JButton cancelButton = new JButton("Отменить");

        newButton.addActionListener(e -> createNewPrescription());
        printButton.addActionListener(e -> printPrescription());
        cancelButton.addActionListener(e -> cancelPrescription());

        buttonPanel.add(newButton);
        buttonPanel.add(printButton);
        buttonPanel.add(cancelButton);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateTable() {
        table.setModel(new PrescriptionTableModel(service.getAllPrescriptions()));
    }

    private void createNewPrescription() {
        JOptionPane.showMessageDialog(this, "Функциональность в разработке");
    }

    private void printPrescription() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Prescription prescription = ((PrescriptionTableModel) table.getModel()).getPrescriptionAt(selectedRow);
            JOptionPane.showMessageDialog(this, "Печать рецепта для: " + prescription.getId());
        } else {
            JOptionPane.showMessageDialog(this, "Выберите назначение для печати");
        }
    }

    private void cancelPrescription() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Prescription prescription = ((PrescriptionTableModel) table.getModel()).getPrescriptionAt(selectedRow);
            service.cancelPrescription(prescription.getId());
            updateTable();
        } else {
            JOptionPane.showMessageDialog(this, "Выберите назначение для отмены");
        }
    }

    private class PrescriptionTableModel extends AbstractTableModel {
        private final List<Prescription> prescriptions;
        private final String[] columns = {"ID", "Пациент", "Препарат", "Дата назначения", "Период"};

        public PrescriptionTableModel(List<Prescription> prescriptions) {
            this.prescriptions = prescriptions;
        }

        @Override
        public int getRowCount() {
            return prescriptions.size();
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
            Prescription p = prescriptions.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> p.getId();
                case 1 -> p.getPatient().getFullName();
                case 2 -> p.getDrug().getCommercialName();
                case 3 -> p.getPrescriptionDate().format(dateFormatter);
                case 4 -> p.getStartDate().format(dateFormatter) + " - " + p.getEndDate().format(dateFormatter);
                default -> null;
            };
        }

        public Prescription getPrescriptionAt(int row) {
            return prescriptions.get(row);
        }
    }
}