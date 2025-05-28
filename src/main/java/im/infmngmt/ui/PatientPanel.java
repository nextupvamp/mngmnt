package im.infmngmt.ui;

import im.infmngmt.entity.Patient;
import im.infmngmt.service.PatientService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PatientPanel extends JPanel {
    private final PatientService service;
    private final JTable table = new JTable();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public PatientPanel(PatientService service) {
        this.service = service;
        setLayout(new BorderLayout());
        initTable();
        initButtons();
        updateTable();
    }

    private void initTable() {
        table.setAutoCreateRowSorter(true);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void initButtons() {
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Добавить");
        addButton.addActionListener(e -> addPatient());

        JButton editButton = new JButton("Изменить");
        editButton.addActionListener(e -> editPatient());

        JButton deleteButton = new JButton("Удалить");
        deleteButton.addActionListener(e -> deletePatient());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateTable() {
        table.setModel(new PatientTableModel(service.getAll()));
    }

    private void addPatient() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Добавить пациента");
        dialog.setLayout(new GridLayout(0, 2, 5, 5));

        JTextField fullNameField = new JTextField();
        JTextField birthDateField = new JTextField();
        JTextField passportField = new JTextField();
        JTextField snilsField = new JTextField();
        JTextField addressField = new JTextField();

        dialog.add(new JLabel("ФИО:"));
        dialog.add(fullNameField);
        dialog.add(new JLabel("Дата рождения (дд.мм.гггг):"));
        dialog.add(birthDateField);
        dialog.add(new JLabel("Паспорт:"));
        dialog.add(passportField);
        dialog.add(new JLabel("СНИЛС:"));
        dialog.add(snilsField);
        dialog.add(new JLabel("Адрес:"));
        dialog.add(addressField);

        JButton saveButton = new JButton("Сохранить");
        saveButton.addActionListener(e -> {
            try {
                Patient patient = new Patient();
                patient.setFullName(fullNameField.getText());
                patient.setBirthDate(LocalDate.parse(birthDateField.getText(), dateFormatter));
                patient.setPassport(passportField.getText());
                patient.setSnils(snilsField.getText());
                patient.setAddress(addressField.getText());

                service.save(patient);
                updateTable();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка ввода данных: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);
        dialog.pack();
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    private void editPatient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите пациента для редактирования");
            return;
        }

        Patient patient = ((PatientTableModel) table.getModel()).getPatientAt(selectedRow);
        JDialog dialog = new JDialog();
        dialog.setTitle("Редактировать пациента");
        dialog.setLayout(new GridLayout(0, 2, 5, 5));

        JTextField fullNameField = new JTextField(patient.getFullName());
        JTextField birthDateField = new JTextField(patient.getBirthDate().format(dateFormatter));
        JTextField passportField = new JTextField(patient.getPassport());
        JTextField snilsField = new JTextField(patient.getSnils());
        JTextField addressField = new JTextField(patient.getAddress());

        dialog.add(new JLabel("ФИО:"));
        dialog.add(fullNameField);
        dialog.add(new JLabel("Дата рождения:"));
        dialog.add(birthDateField);
        dialog.add(new JLabel("Паспорт:"));
        dialog.add(passportField);
        dialog.add(new JLabel("СНИЛС:"));
        dialog.add(snilsField);
        dialog.add(new JLabel("Адрес:"));
        dialog.add(addressField);

        JButton saveButton = new JButton("Сохранить");
        saveButton.addActionListener(e -> {
            try {
                patient.setFullName(fullNameField.getText());
                patient.setBirthDate(LocalDate.parse(birthDateField.getText(), dateFormatter));
                patient.setPassport(passportField.getText());
                patient.setSnils(snilsField.getText());
                patient.setAddress(addressField.getText());

                service.save(patient);
                updateTable();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка ввода данных: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);
        dialog.pack();
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    private void deletePatient() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите пациента для удаления");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Удалить выбранного пациента?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Patient patient = ((PatientTableModel) table.getModel()).getPatientAt(selectedRow);
            service.delete(patient.getId());
            updateTable();
        }
    }

    private static class PatientTableModel extends AbstractTableModel {
        private final List<Patient> patients;
        private final String[] columnNames = {"ФИО", "Дата рождения", "Паспорт", "СНИЛС", "Адрес"};

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
                case 0 -> patient.getFullName();
                case 1 -> patient.getBirthDate();
                case 2 -> patient.getPassport();
                case 3 -> patient.getSnils();
                case 4 -> patient.getAddress();
                default -> null;
            };
        }

        public Patient getPatientAt(int row) {
            return patients.get(row);
        }
    }
}