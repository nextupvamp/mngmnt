package im.infmngmt.ui;

import im.infmngmt.entity.*;
import im.infmngmt.service.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DiagnosisPanel extends JPanel {
    private final DiagnosisService diagnosisService;
    private final PatientService patientService;
    private final TreatmentSchemeService schemeService;
    private final JTable table = new JTable();
    private JComboBox<String> patientFilterCombo;

    public DiagnosisPanel(DiagnosisService diagnosisService,
                          PatientService patientService,
                          TreatmentSchemeService schemeService) {
        this.diagnosisService = diagnosisService;
        this.patientService = patientService;
        this.schemeService = schemeService;

        setLayout(new BorderLayout());
        initFilterPanel();
        initTable();
        initButtons();
        updateTable();
    }

    private void initFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Фильтр по пациенту:"));

        patientFilterCombo = new JComboBox<>();
        patientFilterCombo.addItem("Нет"); // Пустой элемент для отображения всех
        patientService.getAll().stream().map(Patient::getFullName).forEach(patientFilterCombo::addItem);
        patientFilterCombo.addActionListener(e -> updateTable());

        filterPanel.add(patientFilterCombo);
        add(filterPanel, BorderLayout.NORTH);
    }

    private void initTable() {
        table.setAutoCreateRowSorter(true);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void initButtons() {
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

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateTable() {
        Patient selectedPatient = patientService.getAll().stream().filter(
                it -> it.getFullName().equals(patientFilterCombo.getSelectedItem().toString())
        ).findFirst().orElse(null);
        List<Diagnosis> diagnoses;

        if (selectedPatient != null && selectedPatient.getId() != null) {
            diagnoses = diagnosisService.getDiagnosesByPatientId(selectedPatient.getId());
        } else {
            diagnoses = diagnosisService.getAllDiagnoses();
        }

        table.setModel(new DiagnosisTableModel(diagnoses));
    }

    private void addDiagnosis() {
        JDialog dialog = createDiagnosisDialog("Добавить диагноз", null);
        dialog.setVisible(true);
    }

    private void editDiagnosis() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите диагноз для редактирования");
            return;
        }

        Diagnosis diagnosis = ((DiagnosisTableModel) table.getModel()).getDiagnosisAt(selectedRow);
        JDialog dialog = createDiagnosisDialog("Редактировать диагноз", diagnosis);
        dialog.setVisible(true);
    }

    private JDialog createDiagnosisDialog(String title, Diagnosis existingDiagnosis) {
        JDialog dialog = new JDialog();
        dialog.setTitle(title);
        dialog.setLayout(new GridLayout(0, 2, 5, 5));
        dialog.setMinimumSize(new Dimension(400, 300));

        JComboBox<String> patientCombo = new JComboBox<>(
                patientService.getAll().stream().map(Patient::getFullName).toList().toArray(new String[0]));

        JTextField icdField = new JTextField();
        JTextField descField = new JTextField();
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextArea anamnesisArea = new JTextArea(3, 20);
        JTextField conditionField = new JTextField();

        if (existingDiagnosis != null) {
            patientCombo.setSelectedItem(existingDiagnosis.getPatient());
            icdField.setText(existingDiagnosis.getIcd10Code());
            descField.setText(existingDiagnosis.getDescription());
            dateField.setText(existingDiagnosis.getDiagnosisDate().toString());
            anamnesisArea.setText(existingDiagnosis.getAnamnesis());
            conditionField.setText(existingDiagnosis.getPatientCondition());
            patientCombo.setEnabled(false);
        }

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
            Diagnosis diagnosis = existingDiagnosis != null ? existingDiagnosis : new Diagnosis();
            diagnosis.setPatient(patientService.getAll().stream().filter(
                    it -> it.getFullName().equals(patientCombo.getSelectedItem().toString())
            ).findFirst().orElse(null));
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
        return dialog;
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
        List<TreatmentScheme> schemes = schemeService.getSchemesByDiagnosisId(diagnosis.getId());

        JDialog historyDialog = new JDialog();
        historyDialog.setTitle("История лечения: " + diagnosis.getDescription());
        historyDialog.setLayout(new BorderLayout());
        historyDialog.setSize(600, 400);

        // Таблица схем лечения
        JTable schemesTable = new JTable(new AbstractTableModel() {
            private final String[] columns = {"ID", "Тип", "Период", "Врач", "Препараты"};

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
            public Object getValueAt(int row, int column) {
                TreatmentScheme scheme = schemes.get(row);
                return switch (column) {
                    case 0 -> scheme.getId();
                    case 1 -> scheme.getTreatmentType();
                    case 2 -> scheme.getStartDate() + " - " + scheme.getEndDate();
                    case 3 -> scheme.getDoctor() != null ? scheme.getDoctor().getFullName() : "Не указан";
                    case 4 -> schemeService.getDrugsInScheme(scheme.getId()).stream()
                            .map(d -> d.getDrug().getCommercialName())
                            .collect(Collectors.joining(", "));
                    default -> null;
                };
            }
        });

        // Детали выбранной схемы
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);

        schemesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && schemesTable.getSelectedRow() >= 0) {
                TreatmentScheme selectedScheme = schemes.get(schemesTable.getSelectedRow());
                detailsArea.setText(formatSchemeDetails(selectedScheme));
            }
        });

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(schemesTable),
                new JScrollPane(detailsArea));
        splitPane.setResizeWeight(0.5);

        historyDialog.add(splitPane, BorderLayout.CENTER);
        historyDialog.setModal(true);
        historyDialog.setVisible(true);
    }

    private String formatSchemeDetails(TreatmentScheme scheme) {
        StringBuilder sb = new StringBuilder();
        sb.append("Схема лечения ID: ").append(scheme.getId()).append("\n");
        sb.append("Тип: ").append(scheme.getTreatmentType()).append("\n");
        sb.append("Период: ").append(scheme.getStartDate()).append(" - ").append(scheme.getEndDate()).append("\n");
        sb.append("Врач: ").append(scheme.getDoctor() != null ? scheme.getDoctor().getFullName() : "Не указан").append("\n\n");

        sb.append("Препараты:\n");
        List<TreatmentSchemeDrug> drugs = schemeService.getDrugsInScheme(scheme.getId());
        for (TreatmentSchemeDrug drug : drugs) {
            sb.append("- ").append(drug.getDrug().getCommercialName())
                    .append(" (").append(drug.getAdministrationScheme()).append(")")
                    .append(", ").append(drug.getFrequencyPerDay()).append(" раз/день")
                    .append(", ").append(drug.getDurationDays()).append(" дней\n");
        }

        sb.append("\nТочки контроля:\n");
        List<ControlPoint> points = schemeService.getControlPointsBySchemeId(scheme.getId());
        for (ControlPoint point : points) {
            sb.append("- ").append(point.getName())
                    .append(" (").append(point.getControlDate()).append(")")
                    .append(", метод: ").append(point.getMethod()).append("\n");
        }

        return sb.toString();
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