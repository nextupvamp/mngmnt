package im.infmngmt.ui;

import im.infmngmt.entity.Diagnosis;
import im.infmngmt.entity.Patient;
import im.infmngmt.service.DiagnosisService;
import im.infmngmt.service.PatientService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class DiagnosisPanel extends JPanel {
    private final DiagnosisService diagnosisService;
    private final PatientService patientService;
    private final JTable table = new JTable();

    public DiagnosisPanel(DiagnosisService diagnosisService, PatientService patientService) {
        this.diagnosisService = diagnosisService;
        this.patientService = patientService;
        setLayout(new BorderLayout());
        updateTable();

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Добавить");
        JButton editButton = new JButton("Изменить");
        JButton deleteButton = new JButton("Удалить");
        JButton historyButton = new JButton("История лечения");

        addButton.addActionListener(e -> addDiagnosis());
        editButton.addActionListener(e -> editDiagnosis());
        deleteButton.addActionListener(e -> deleteDiagnosis());
        historyButton.addActionListener(e -> showTreatmentHistory());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(historyButton);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateTable() {
        table.setModel(new DiagnosisTableModel(diagnosisService.getAllDiagnoses()));
    }

    private void addDiagnosis() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Добавить диагноз");
        dialog.setLayout(new GridLayout(0, 2));

        JComboBox<Patient> patientCombo = new JComboBox<>(
                patientService.getAll().toArray(new Patient[0]));

        JTextField icdField = new JTextField();
        JTextField descField = new JTextField();
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextArea anamnesisArea = new JTextArea(3, 20);
        JTextField conditionField = new JTextField();

        dialog.add(new JLabel("Пациент:"));
        dialog.add(patientCombo);
        dialog.add(new JLabel("Код МКБ-10:"));
        dialog.add(icdField);
        dialog.add(new JLabel("Описание:"));
        dialog.add(descField);
        dialog.add(new JLabel("Дата:"));
        dialog.add(dateField);
        dialog.add(new JLabel("Анамнез:"));
        dialog.add(new JScrollPane(anamnesisArea));
        dialog.add(new JLabel("Состояние:"));
        dialog.add(conditionField);

        JButton saveButton = new JButton("Сохранить");
        saveButton.addActionListener(e -> {
            Diagnosis diagnosis = new Diagnosis();
            diagnosis.setPatient((Patient) patientCombo.getSelectedItem());
            diagnosis.setIcd10Code(icdField.getText());
            diagnosis.setDescription(descField.getText());
            diagnosis.setDiagnosisDate(LocalDate.parse(dateField.getText()));
            diagnosis.setAnamnesis(anamnesisArea.getText());
            diagnosis.setPatientCondition(conditionField.getText());

            diagnosisService.saveDiagnosis(diagnosis);
            updateTable();
            dialog.dispose();
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);
        dialog.pack();
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    private void editDiagnosis() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите диагноз для редактирования");
            return;
        }

        Diagnosis diagnosis = ((DiagnosisTableModel) table.getModel()).getDiagnosisAt(selectedRow);
        JDialog dialog = new JDialog();
        dialog.setTitle("Редактировать диагноз");
        dialog.setLayout(new GridLayout(0, 2));

        JLabel patientLabel = new JLabel(diagnosis.getPatient().getFullName());
        JTextField icdField = new JTextField(diagnosis.getIcd10Code());
        JTextField descField = new JTextField(diagnosis.getDescription());
        JTextField dateField = new JTextField(diagnosis.getDiagnosisDate().toString());
        JTextArea anamnesisArea = new JTextArea(diagnosis.getAnamnesis(), 3, 20);
        JTextField conditionField = new JTextField(diagnosis.getPatientCondition());

        dialog.add(new JLabel("Пациент:"));
        dialog.add(patientLabel);
        dialog.add(new JLabel("Код МКБ-10:"));
        dialog.add(icdField);
        dialog.add(new JLabel("Описание:"));
        dialog.add(descField);
        dialog.add(new JLabel("Дата:"));
        dialog.add(dateField);
        dialog.add(new JLabel("Анамнез:"));
        dialog.add(new JScrollPane(anamnesisArea));
        dialog.add(new JLabel("Состояние:"));
        dialog.add(conditionField);

        JButton saveButton = new JButton("Сохранить");
        saveButton.addActionListener(e -> {
            diagnosis.setIcd10Code(icdField.getText());
            diagnosis.setDescription(descField.getText());
            diagnosis.setDiagnosisDate(LocalDate.parse(dateField.getText()));
            diagnosis.setAnamnesis(anamnesisArea.getText());
            diagnosis.setPatientCondition(conditionField.getText());

            diagnosisService.saveDiagnosis(diagnosis);
            updateTable();
            dialog.dispose();
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);
        dialog.pack();
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    private void deleteDiagnosis() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите диагноз для удаления");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Удалить выбранный диагноз?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Diagnosis diagnosis = ((DiagnosisTableModel) table.getModel()).getDiagnosisAt(selectedRow);
            diagnosisService.deleteDiagnosis(diagnosis.getId());
            updateTable();
        }
    }

    private void showTreatmentHistory() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите диагноз для просмотра истории");
            return;
        }

        Diagnosis diagnosis = ((DiagnosisTableModel) table.getModel()).getDiagnosisAt(selectedRow);
        JOptionPane.showMessageDialog(
                this,
                "История лечения для диагноза: " + diagnosis.getDescription(),
                "История лечения",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static class DiagnosisTableModel extends AbstractTableModel {
        private final List<Diagnosis> diagnoses;
        private final String[] columns = {"ID", "Пациент", "Код МКБ-10", "Описание", "Дата"};

        public DiagnosisTableModel(List<Diagnosis> diagnoses) {
            this.diagnoses = diagnoses;
        }

        @Override public int getRowCount() { return diagnoses.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }

        @Override
        public Object getValueAt(int row, int column) {
            Diagnosis d = diagnoses.get(row);
            return switch (column) {
                case 0 -> d.getId();
                case 1 -> d.getPatient().getFullName();
                case 2 -> d.getIcd10Code();
                case 3 -> d.getDescription();
                case 4 -> d.getDiagnosisDate();
                default -> null;
            };
        }

        public Diagnosis getDiagnosisAt(int row) {
            return diagnoses.get(row);
        }
    }
}