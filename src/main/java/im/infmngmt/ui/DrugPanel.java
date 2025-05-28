package im.infmngmt.ui;

import im.infmngmt.entity.Drug;
import im.infmngmt.service.DrugService;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class DrugPanel extends JPanel {
    private final DrugService drugService;
    private final JTable mainDrugTable = new JTable();
    private final JTextField searchField = new JTextField(20);

    public DrugPanel(DrugService drugService) {
        this.drugService = drugService;
        setLayout(new BorderLayout());
        initSearchPanel();
        initMainTable();
        initButtons();
        updateTables();
    }

    private void initSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Поиск по МНН, действующему веществу или производителю:"));
        searchPanel.add(searchField);

        JButton searchButton = new JButton("Найти");
        searchButton.addActionListener(e -> searchDrugs());

        JButton showAllButton = new JButton("Все");
        showAllButton.addActionListener(e -> updateTables());

        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);
        add(searchPanel, BorderLayout.NORTH);
    }

    private void initMainTable() {
        add(new JScrollPane(mainDrugTable), BorderLayout.CENTER);
    }

    private void initButtons() {
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Добавить");
        addButton.addActionListener(e -> addDrug());

        JButton editButton = new JButton("Изменить");
        editButton.addActionListener(e -> editDrug());

        JButton deleteButton = new JButton("Удалить");
        deleteButton.addActionListener(e -> deleteDrug());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateTables() {
        mainDrugTable.setModel(new MainDrugTableModel(drugService.getAllDrugs()));
    }

    private void searchDrugs() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            updateTables();
            return;
        }

        List<Drug> foundDrugs = drugService.searchDrugs(query);
        mainDrugTable.setModel(new MainDrugTableModel(foundDrugs));
    }

    private void addDrug() {
        DrugDialog dialog = new DrugDialog(null, drugService);
        if (dialog.showDialog()) {
            updateTables();
        }
    }

    private void editDrug() {
        int selectedRow = mainDrugTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите препарат для редактирования");
            return;
        }

        Drug selectedDrug = ((MainDrugTableModel) mainDrugTable.getModel()).getDrugAt(selectedRow);
        DrugDialog dialog = new DrugDialog(selectedDrug, drugService);
        if (dialog.showDialog()) {
            updateTables();
        }
    }

    private void deleteDrug() {
        int selectedRow = mainDrugTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите препарат для удаления");
            return;
        }

        Drug selectedDrug = ((MainDrugTableModel) mainDrugTable.getModel()).getDrugAt(selectedRow);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Удалить выбранный препарат?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            drugService.deleteDrug(selectedDrug.getId());
            updateTables();
        }
    }

    private static class MainDrugTableModel extends AbstractTableModel {
        private final List<Drug> drugs;
        private final String[] columns = {"МНН", "Действующее вещество", "Форма", "Цена", "Наличие"};

        public MainDrugTableModel(List<Drug> drugs) {
            this.drugs = drugs;
        }

        @Override public int getRowCount() { return drugs.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }

        @Override
        public Object getValueAt(int row, int column) {
            Drug drug = drugs.get(row);
            return switch (column) {
                case 0 -> drug.getMnn();
                case 1 -> drug.getActiveSubstance();
                case 2 -> drug.getDosageForm();
                case 3 -> String.format("%.2f - %.2f", drug.getMinPrice(), drug.getMaxPrice());
                case 4 -> drug.getAvailable() ? "Да" : "Нет";
                default -> null;
            };
        }

        public Drug getDrugAt(int row) {
            return drugs.get(row);
        }
    }

    private static class AnalogsTableModel extends AbstractTableModel {
        private final List<Drug> analogs;
        private final String[] columns = {"МНН", "Цена", "Наличие"};

        public AnalogsTableModel(List<Drug> analogs) {
            this.analogs = analogs;
        }

        @Override public int getRowCount() { return analogs.size(); }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }

        @Override
        public Object getValueAt(int row, int column) {
            Drug drug = analogs.get(row);
            return switch (column) {
                case 0 -> drug.getMnn();
                case 1 -> String.format("%.2f - %.2f", drug.getMinPrice(), drug.getMaxPrice());
                case 2 -> drug.getAvailable() ? "Да" : "Нет";
                default -> null;
            };
        }
    }

    private static class DrugDialog extends JDialog {
        private final Drug drug;
        private final DrugService drugService;
        private boolean saved = false;

        public DrugDialog(Drug existingDrug, DrugService drugService) {
            this.drug = existingDrug != null ? existingDrug : new Drug();
            this.drugService = drugService;

            setTitle(existingDrug != null ? "Редактировать препарат" : "Добавить препарат");
            setLayout(new GridLayout(0, 2, 5, 5));
            setModal(true);

            initFields();
            pack();
            setLocationRelativeTo(null);
        }

        private void initFields() {
            JTextField mnnField = new JTextField(drug.getMnn());
            JTextField activeSubstanceField = new JTextField(drug.getActiveSubstance());
            JTextField dosageFormField = new JTextField(drug.getDosageForm());
            JTextField packagingField = new JTextField(drug.getPackaging());
            JTextField dosageField = new JTextField(drug.getDosage());
            JTextField commercialNameField = new JTextField(drug.getCommercialName());
            JTextField minPriceField = new JTextField(drug.getMinPrice() != null ? drug.getMinPrice().toString() : "");
            JTextField maxPriceField = new JTextField(drug.getMaxPrice() != null ? drug.getMaxPrice().toString() : "");
            JTextField manufacturerField = new JTextField(drug.getManufacturer());
            JTextField countryField = new JTextField(drug.getCountry());
            JTextArea sideEffectsArea = new JTextArea(drug.getSideEffects(), 3, 20);
            JTextArea contraindicationsArea = new JTextArea(drug.getContraindications(), 3, 20);
            JTextArea interactionsArea = new JTextArea(drug.getInteractions(), 3, 20);
            JTextArea incompatibilityArea = new JTextArea(drug.getIncompatibility(), 3, 20);
            JCheckBox availableCheckbox = new JCheckBox("", drug.getAvailable() != null ? drug.getAvailable() : true);

            add(new JLabel("МНН:"));
            add(mnnField);
            add(new JLabel("Действующее вещество:"));
            add(activeSubstanceField);
            add(new JLabel("Форма выпуска:"));
            add(dosageFormField);
            add(new JLabel("Упаковка:"));
            add(packagingField);
            add(new JLabel("Дозировка:"));
            add(dosageField);
            add(new JLabel("Коммерческое название:"));
            add(commercialNameField);
            add(new JLabel("Минимальная цена:"));
            add(minPriceField);
            add(new JLabel("Максимальная цена:"));
            add(maxPriceField);
            add(new JLabel("Производитель:"));
            add(manufacturerField);
            add(new JLabel("Страна:"));
            add(countryField);
            add(new JLabel("Побочные эффекты:"));
            add(new JScrollPane(sideEffectsArea));
            add(new JLabel("Противопоказания:"));
            add(new JScrollPane(contraindicationsArea));
            add(new JLabel("Взаимодействия:"));
            add(new JScrollPane(interactionsArea));
            add(new JLabel("Несовместимость:"));
            add(new JScrollPane(incompatibilityArea));
            add(new JLabel("Доступен:"));
            add(availableCheckbox);

            JButton saveButton = new JButton("Сохранить");
            saveButton.addActionListener(e -> {
                try {
                    drug.setMnn(mnnField.getText());
                    drug.setActiveSubstance(activeSubstanceField.getText());
                    drug.setDosageForm(dosageFormField.getText());
                    drug.setPackaging(packagingField.getText());
                    drug.setDosage(dosageField.getText());
                    drug.setCommercialName(commercialNameField.getText());
                    drug.setMinPrice(new BigDecimal(minPriceField.getText()));
                    drug.setMaxPrice(new BigDecimal(maxPriceField.getText()));
                    drug.setManufacturer(manufacturerField.getText());
                    drug.setCountry(countryField.getText());
                    drug.setSideEffects(sideEffectsArea.getText());
                    drug.setContraindications(contraindicationsArea.getText());
                    drug.setInteractions(interactionsArea.getText());
                    drug.setIncompatibility(incompatibilityArea.getText());
                    drug.setAvailable(availableCheckbox.isSelected());

                    drugService.saveDrug(drug);
                    saved = true;
                    dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Введите корректные числовые значения для цен");
                }
            });

            add(new JLabel());
            add(saveButton);
        }

        public boolean showDialog() {
            setVisible(true);
            return saved;
        }
    }
}