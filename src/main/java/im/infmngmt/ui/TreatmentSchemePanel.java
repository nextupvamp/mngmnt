package im.infmngmt.ui;

import im.infmngmt.entity.*;
import im.infmngmt.service.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class TreatmentSchemePanel extends JPanel {
    private final TreatmentSchemeService schemeService;
    private final DiagnosisService diagnosisService;
//    private final DoctorService doctorService;
    private final DrugService drugService;
    private final JTable schemesTable = new JTable();
    private final JTable drugsTable = new JTable();

    public TreatmentSchemePanel(TreatmentSchemeService schemeService,
                                DiagnosisService diagnosisService,
//                                DoctorService doctorService,
                                DrugService drugService) {
        this.schemeService = schemeService;
        this.diagnosisService = diagnosisService;
//        this.doctorService = doctorService;
        this.drugService = drugService;

        setLayout(new BorderLayout());
        initTables();
        initButtons();
        updateTables();
    }

    private void initTables() {
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
    }

    private void initButtons() {
        JPanel buttonPanel = new JPanel();

        JButton addSchemeButton = new JButton("Добавить схему");
        JButton addDrugButton = new JButton("Добавить препарат");
        JButton deleteSchemeButton = new JButton("Удалить схему");
        JButton addControlPointButton = new JButton("Добавить точку контроля");

        addSchemeButton.addActionListener(e -> addNewScheme());
        addDrugButton.addActionListener(e -> addDrugToScheme());
        deleteSchemeButton.addActionListener(e -> deleteScheme());
        addControlPointButton.addActionListener(e -> addControlPoint());

        buttonPanel.add(addSchemeButton);
        buttonPanel.add(addDrugButton);
        buttonPanel.add(deleteSchemeButton);
        buttonPanel.add(addControlPointButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateTables() {
        schemesTable.setModel(new SchemeTableModel(schemeService.getAllSchemes()));
        updateDrugsTable();
    }

    private void updateDrugsTable() {
        int selectedRow = schemesTable.getSelectedRow();
        if (selectedRow >= 0) {
            TreatmentScheme scheme = ((SchemeTableModel) schemesTable.getModel()).getSchemeAt(selectedRow);
            drugsTable.setModel(new SchemeDrugsTableModel(schemeService.getDrugsInScheme(scheme.getId())));
        } else {
            drugsTable.setModel(new SchemeDrugsTableModel(List.of()));
        }
    }

    private void addNewScheme() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Новая схема лечения");
        dialog.setLayout(new GridLayout(0, 2));

        // Выбор диагноза
        JComboBox<String> diagnosisCombo = new JComboBox<>(
                diagnosisService.getAllDiagnoses()
                        .stream().map(Diagnosis::getDescription).toList().toArray(new String[0]));

        // Выбор врача
//        JComboBox<Doctor> doctorCombo = new JComboBox<>(
//                doctorService.getAllDoctors().toArray(new Doctor[0]));

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"inpatient", "outpatient"});
        JComboBox<String> supervisionCombo = new JComboBox<>(new String[]{"independent", "under supervision"});
        JTextField startDateField = new JTextField(LocalDate.now().toString());
        JTextField endDateField = new JTextField(LocalDate.now().plusDays(30).toString());

        dialog.add(new JLabel("Диагноз:"));
        dialog.add(diagnosisCombo);
//        dialog.add(new JLabel("Врач:"));
//        dialog.add(doctorCombo);
        dialog.add(new JLabel("Тип лечения:"));
        dialog.add(typeCombo);
        dialog.add(new JLabel("Тип наблюдения:"));
        dialog.add(supervisionCombo);
        dialog.add(new JLabel("Дата начала:"));
        dialog.add(startDateField);
        dialog.add(new JLabel("Дата окончания:"));
        dialog.add(endDateField);

        JButton saveButton = new JButton("Сохранить");
        saveButton.addActionListener(e -> {
            TreatmentScheme scheme = new TreatmentScheme();
            scheme.setDiagnosis((Diagnosis) diagnosisCombo.getSelectedItem());
//            scheme.setDoctor((Doctor) doctorCombo.getSelectedItem());
            scheme.setTreatmentType((String) typeCombo.getSelectedItem());
            scheme.setSupervisionType((String) supervisionCombo.getSelectedItem());
            scheme.setStartDate(LocalDate.parse(startDateField.getText()));
            scheme.setEndDate(LocalDate.parse(endDateField.getText()));

            schemeService.saveScheme(scheme);
            updateTables();
            dialog.dispose();
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);
        dialog.pack();
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    private void addDrugToScheme() {
        int selectedRow = schemesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите схему для добавления препарата");
            return;
        }

        TreatmentScheme scheme = ((SchemeTableModel) schemesTable.getModel()).getSchemeAt(selectedRow);

        JDialog dialog = new JDialog();
        dialog.setTitle("Добавить препарат в схему");
        dialog.setLayout(new GridLayout(0, 2));

        JComboBox<String> drugCombo = new JComboBox<>(
                drugService.getAllDrugs().stream().map(Drug::getCommercialName).toList().toArray(new String[0]));
        JTextField frequencyField = new JTextField("1");
        JTextField durationField = new JTextField("7");
        JTextArea schemeField = new JTextArea(3, 20);

        dialog.add(new JLabel("Препарат:"));
        dialog.add(drugCombo);
        dialog.add(new JLabel("Частота (раз/день):"));
        dialog.add(frequencyField);
        dialog.add(new JLabel("Длительность (дней):"));
        dialog.add(durationField);
        dialog.add(new JLabel("Схема приема:"));
        dialog.add(new JScrollPane(schemeField));

        JButton saveButton = new JButton("Сохранить");
        saveButton.addActionListener(e -> {
            try {
                TreatmentSchemeDrug schemeDrug = new TreatmentSchemeDrug();
                schemeDrug.setScheme(scheme);
                schemeDrug.setDrug(
                        drugService.getAllDrugs().stream().filter(
                                it -> it.getCommercialName().equals(drugCombo.getSelectedItem()))
                                .findFirst().get());
                schemeDrug.setFrequencyPerDay(Integer.parseInt(frequencyField.getText()));
                schemeDrug.setDurationDays(Integer.parseInt(durationField.getText()));
                schemeDrug.setAdministrationScheme(schemeField.getText());

                schemeService.addDrugToScheme(schemeDrug);
                updateDrugsTable();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Введите корректные числовые значения");
            }
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);
        dialog.pack();
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    private void deleteScheme() {
        int selectedRow = schemesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите схему для удаления");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Удалить выбранную схему лечения?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            TreatmentScheme scheme = ((SchemeTableModel) schemesTable.getModel()).getSchemeAt(selectedRow);
            schemeService.deleteScheme(scheme.getId());
            updateTables();
        }
    }

    private void addControlPoint() {
        int selectedRow = schemesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите схему для добавления точки контроля");
            return;
        }

        TreatmentScheme scheme = ((SchemeTableModel) schemesTable.getModel()).getSchemeAt(selectedRow);

        JDialog dialog = new JDialog();
        dialog.setTitle("Добавить точку контроля");
        dialog.setLayout(new GridLayout(0, 2));

        JTextField nameField = new JTextField();
        JTextField dateField = new JTextField(LocalDate.now().toString());
        JTextField controllerField = new JTextField();
        JTextArea methodArea = new JTextArea(3, 20);

        dialog.add(new JLabel("Название:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Дата контроля:"));
        dialog.add(dateField);
        dialog.add(new JLabel("Контролер:"));
        dialog.add(controllerField);
        dialog.add(new JLabel("Метод:"));
        dialog.add(new JScrollPane(methodArea));

        JButton saveButton = new JButton("Сохранить");
        saveButton.addActionListener(e -> {
            ControlPoint point = new ControlPoint();
            point.setScheme(scheme);
            point.setName(nameField.getText());
            point.setControlDate(LocalDate.parse(dateField.getText()));
            point.setController(controllerField.getText());
            point.setMethod(methodArea.getText());

            schemeService.addControlPoint(point);
            JOptionPane.showMessageDialog(dialog, "Точка контроля добавлена");
            dialog.dispose();
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);
        dialog.pack();
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    private static class SchemeTableModel extends AbstractTableModel {
        private final List<TreatmentScheme> schemes;
        private final String[] columns = {"ID", "Диагноз", "Тип", "Период", "Врач"};

        public SchemeTableModel(List<TreatmentScheme> schemes) {
            this.schemes = schemes;
        }

        @Override public int getRowCount() { return schemes.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }

        @Override
        public Object getValueAt(int row, int column) {
            TreatmentScheme s = schemes.get(row);
            return switch (column) {
                case 0 -> s.getId();
                case 1 -> s.getDiagnosis().getDescription();
                case 2 -> s.getTreatmentType();
                case 3 -> s.getStartDate() + " - " + s.getEndDate();
                case 4 -> s.getDoctor() != null ? s.getDoctor().getFullName() : "Не указан";
                default -> null;
            };
        }

        public TreatmentScheme getSchemeAt(int row) {
            return schemes.get(row);
        }
    }

    private static class SchemeDrugsTableModel extends AbstractTableModel {
        private final List<TreatmentSchemeDrug> drugs;
        private final String[] columns = {"ID", "Препарат", "Дозировка", "Частота", "Длительность"};

        public SchemeDrugsTableModel(List<TreatmentSchemeDrug> drugs) {
            this.drugs = drugs;
        }

        @Override public int getRowCount() { return drugs.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }

        @Override
        public Object getValueAt(int row, int column) {
            TreatmentSchemeDrug d = drugs.get(row);
            return switch (column) {
                case 0 -> d.getId();
                case 1 -> d.getDrug().getCommercialName();
                case 2 -> d.getAdministrationScheme();
                case 3 -> d.getFrequencyPerDay() + " раз/день";
                case 4 -> d.getDurationDays() + " дней";
                default -> null;
            };
        }
    }
}