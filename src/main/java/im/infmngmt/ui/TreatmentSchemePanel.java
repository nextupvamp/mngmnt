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
    private final DrugService drugService;
    private final JTable schemesTable = new JTable();
    private final JTable drugsTable = new JTable();
    private final JTable controlPointsTable = new JTable();

    public TreatmentSchemePanel(TreatmentSchemeService schemeService,
                                DiagnosisService diagnosisService,
                                DrugService drugService) {
        this.schemeService = schemeService;
        this.diagnosisService = diagnosisService;
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
                updateControlPointsTable();
            }
        });

        // Основная панель с тремя таблицами
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JScrollPane(schemesTable), BorderLayout.CENTER);

        // Панель для препаратов и точек контроля
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));

        // Таблица препаратов
        JPanel drugsPanel = new JPanel(new BorderLayout());
        drugsPanel.add(new JLabel("Препараты в схеме:"), BorderLayout.NORTH);
        drugsPanel.add(new JScrollPane(drugsTable), BorderLayout.CENTER);
        bottomPanel.add(drugsPanel);

        // Таблица точек контроля
        JPanel controlPointsPanel = new JPanel(new BorderLayout());
        controlPointsPanel.add(new JLabel("Точки контроля:"), BorderLayout.NORTH);
        controlPointsPanel.add(new JScrollPane(controlPointsTable), BorderLayout.CENTER);
        bottomPanel.add(controlPointsPanel);

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
        updateControlPointsTable();
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

    private void updateControlPointsTable() {
        int selectedRow = schemesTable.getSelectedRow();
        if (selectedRow >= 0) {
            TreatmentScheme scheme = ((SchemeTableModel) schemesTable.getModel()).getSchemeAt(selectedRow);
            controlPointsTable.setModel(new ControlPointsTableModel(schemeService.getControlPointsBySchemeId(scheme.getId())));
        } else {
            controlPointsTable.setModel(new ControlPointsTableModel(List.of()));
        }
    }

    private void addNewScheme() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Новая схема лечения");
        dialog.setLayout(new GridLayout(0, 2, 5, 5));

        // Выбор диагноза
        JComboBox<String> diagnosisCombo = new JComboBox<>(
                diagnosisService.getAllDiagnoses().stream().map(Diagnosis::getDescription).toList().toArray(new String[0]));

        // Тип лечения (на русском)
        String[] treatmentTypes = {"Стационарное", "Амбулаторное"};
        JComboBox<String> typeCombo = new JComboBox<>(treatmentTypes);

        // Тип наблюдения (на русском)
        String[] supervisionTypes = {"Самостоятельное", "Под наблюдением"};
        JComboBox<String> supervisionCombo = new JComboBox<>(supervisionTypes);

        JTextField startDateField = new JTextField(LocalDate.now().toString());
        JTextField endDateField = new JTextField(LocalDate.now().plusDays(30).toString());

        dialog.add(new JLabel("Диагноз:"));
        dialog.add(diagnosisCombo);
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
            try {
                TreatmentScheme scheme = new TreatmentScheme();
                scheme.setDiagnosis(
                        diagnosisService.getAllDiagnoses().stream().filter(
                                it -> it.getDescription().equals(diagnosisCombo.getSelectedItem())
                        ).findFirst().orElse(null));

                // Конвертация русских названий в английские для хранения
                String treatmentType = typeCombo.getSelectedItem().equals("Стационарное") ?
                        "inpatient" : "outpatient";
                String supervisionType = supervisionCombo.getSelectedItem().equals("Под наблюдением") ?
                        "under supervision" : "independent";

                scheme.setTreatmentType(treatmentType);
                scheme.setSupervisionType(supervisionType);
                scheme.setStartDate(LocalDate.parse(startDateField.getText()));
                scheme.setEndDate(LocalDate.parse(endDateField.getText()));

                schemeService.saveScheme(scheme);
                updateTables();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка при сохранении: " + ex.getMessage());
            }
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
        private final String[] columns = {"ID", "Диагноз", "Тип лечения", "Тип наблюдения", "Период"};

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
                case 2 -> translateTreatmentType(s.getTreatmentType());
                case 3 -> translateSupervisionType(s.getSupervisionType());
                case 4 -> s.getStartDate() + " - " + s.getEndDate();
                default -> null;
            };
        }

        private String translateTreatmentType(String type) {
            return "inpatient".equals(type) ? "Стационарное" : "Амбулаторное";
        }

        private String translateSupervisionType(String type) {
            return "under supervision".equals(type) ? "Под наблюдением" : "Самостоятельное";
        }

        public TreatmentScheme getSchemeAt(int row) {
            return schemes.get(row);
        }
    }

    private static class ControlPointsTableModel extends AbstractTableModel {
        private final List<ControlPoint> controlPoints;
        private final String[] columns = {"Название", "Дата", "Контролер", "Метод"};

        public ControlPointsTableModel(List<ControlPoint> controlPoints) {
            this.controlPoints = controlPoints;
        }

        @Override public int getRowCount() { return controlPoints.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }

        @Override
        public Object getValueAt(int row, int column) {
            ControlPoint cp = controlPoints.get(row);
            return switch (column) {
                case 0 -> cp.getName();
                case 1 -> cp.getControlDate();
                case 2 -> cp.getController();
                case 3 -> cp.getMethod();
                default -> null;
            };
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