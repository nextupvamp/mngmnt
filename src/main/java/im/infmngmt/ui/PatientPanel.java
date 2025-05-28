package im.infmngmt.ui;

import im.infmngmt.entity.Patient;
import im.infmngmt.service.PatientService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.List;

public class PatientPanel extends JPanel {
    private final PatientService service;
    private final JTable table = new JTable();

    public PatientPanel(PatientService service) {
        this.service = service;
        setLayout(new BorderLayout());
        updateTable();

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Добавить");
        JButton editButton = new JButton("Изменить");
        JButton deleteButton = new JButton("Удалить");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addPatient());
        editButton.addActionListener(e -> editPatient());
        deleteButton.addActionListener(e -> deletePatient());
    }

    private void updateTable() {
        table.setModel(new PatientTableModel(service.getAll()));
    }

    private void addPatient() {
        JTextField nameField = new JTextField();
        JTextField birthDateField = new JTextField();
        JTextField snilsField = new JTextField();

        Object[] fields = {
                "ФИО:", nameField,
                "Дата рождения (ГГГГ-ММ-ДД):", birthDateField,
                "СНИЛС:", snilsField
        };

        int result = JOptionPane.showConfirmDialog(
                this,
                fields,
                "Добавить пациента",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Patient patient = new Patient();
            patient.setFullName(nameField.getText());
            patient.setBirthDate(java.time.LocalDate.parse(birthDateField.getText()));
            patient.setSnils(snilsField.getText());
            service.save(patient);
            updateTable();
        }
    }

    private void editPatient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Patient patient = ((PatientTableModel) table.getModel()).getPatientAt(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Выберите пациента для редактирования");
        }
    }

    private void deletePatient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            Patient patient = ((PatientTableModel) table.getModel()).getPatientAt(selectedRow);
            service.delete(patient.getId());
            updateTable();
        } else {
            JOptionPane.showMessageDialog(this, "Выберите пациента для удаления");
        }
    }

    private static class PatientTableModel extends AbstractTableModel {
        private final List<Patient> patients;
        private final String[] columnNames = {"ID", "ФИО", "Дата рождения", "СНИЛС"};

        public PatientTableModel(List<Patient> patients) {
            this.patients = patients;
        }

        @Override
        public int getRowCount() {
            return patients.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Patient patient = patients.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> patient.getId();
                case 1 -> patient.getFullName();
                case 2 -> patient.getBirthDate();
                case 3 -> patient.getSnils();
                default -> null;
            };
        }

        public Patient getPatientAt(int row) {
            return patients.get(row);
        }
    }
}