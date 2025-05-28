package im.infmngmt.ui;

import im.infmngmt.entity.*;
import im.infmngmt.service.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PrescriptionPanel extends JPanel {
    private final PrescriptionService prescriptionService;
    private final PatientService patientService;
    private final DrugService drugService;
    private final JTable table = new JTable();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public PrescriptionPanel(PrescriptionService prescriptionService,
                             PatientService patientService,
                             DrugService drugService) {
        this.prescriptionService = prescriptionService;
        this.patientService = patientService;
        this.drugService = drugService;

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

        JButton newButton = new JButton("Новое назначение");
        newButton.addActionListener(e -> createNewPrescription());

        JButton printButton = new JButton("Печать рецепта");
        printButton.addActionListener(e -> printPrescription());

        JButton cancelButton = new JButton("Отменить");
        cancelButton.addActionListener(e -> cancelPrescription());

        JButton replaceButton = new JButton("Заменить препарат");
        replaceButton.addActionListener(e -> replaceDrug());

        buttonPanel.add(newButton);
        buttonPanel.add(printButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(replaceButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateTable() {
        table.setModel(new PrescriptionTableModel(prescriptionService.getAllPrescriptions()));
    }

    private void createNewPrescription() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Новое назначение");
        dialog.setLayout(new GridLayout(0, 2, 5, 5));

        // Выбор пациента
        JComboBox<String> patientCombo = new JComboBox<>(
                patientService.getAll().stream().map(Patient::getFullName).toList().toArray(new String[0]));

        // Выбор препарата
        JComboBox<String> drugCombo = new JComboBox<>(
                drugService.getAllDrugs().stream().map(Drug::getCommercialName).toList().toArray(new String[0]));

        JTextField startDateField = new JTextField(LocalDate.now().format(dateFormatter));
        JTextField endDateField = new JTextField(LocalDate.now().plusDays(7).format(dateFormatter));
        JTextField dosageField = new JTextField();
        JTextField frequencyField = new JTextField("1");
        JTextArea instructionsArea = new JTextArea(3, 20);

        dialog.add(new JLabel("Пациент:"));
        dialog.add(patientCombo);
        dialog.add(new JLabel("Препарат:"));
        dialog.add(drugCombo);
        dialog.add(new JLabel("Дата начала:"));
        dialog.add(startDateField);
        dialog.add(new JLabel("Дата окончания:"));
        dialog.add(endDateField);
        dialog.add(new JLabel("Дозировка:"));
        dialog.add(dosageField);
        dialog.add(new JLabel("Частота (раз/день):"));
        dialog.add(frequencyField);
        dialog.add(new JLabel("Инструкции:"));
        dialog.add(new JScrollPane(instructionsArea));

        JButton saveButton = new JButton("Сохранить");
        saveButton.addActionListener(e -> {
            try {
                Prescription prescription = new Prescription();
                prescription.setPatient(
                        patientService.getAll().stream().filter(
                                it -> it.getFullName().equals(patientCombo.getSelectedItem())
                        ).findFirst().orElse(null));
                prescription.setDrug(
                        drugService.getAllDrugs().stream().filter(
                                it -> it.getCommercialName().equals(drugCombo.getSelectedItem())
                        ).findFirst().orElse(null));
                prescription.setPrescriptionDate(LocalDate.now());
                prescription.setStartDate(LocalDate.parse(startDateField.getText(), dateFormatter));
                prescription.setEndDate(LocalDate.parse(endDateField.getText(), dateFormatter));
                prescription.setDosage(dosageField.getText());
                prescription.setFrequencyPerDay(Integer.parseInt(frequencyField.getText()));
                prescription.setAdministrationInstructions(instructionsArea.getText());

                prescriptionService.savePrescription(prescription);
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

    private void printPrescription() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите назначение для печати");
            return;
        }

        Prescription prescription = ((PrescriptionTableModel) table.getModel()).getPrescriptionAt(selectedRow);

        // Формируем текст рецепта
        String prescriptionText = String.format(
                "РЕЦЕПТ\n\n" +
                        "Пациент: %s\n" +
                        "Препарат: %s (%s)\n" +
                        "Дозировка: %s\n" +
                        "Способ применения: %s\n" +
                        "Частота: %d раз(а) в день\n" +
                        "Период: с %s по %s\n" +
                        "Дата назначения: %s\n\n" +
                        "Подпись врача: ________________",
                prescription.getPatient().getFullName(),
                prescription.getDrug().getCommercialName(),
                prescription.getDrug().getDosageForm(),
                prescription.getDosage(),
                prescription.getAdministrationInstructions(),
                prescription.getFrequencyPerDay(),
                prescription.getStartDate().format(dateFormatter),
                prescription.getEndDate().format(dateFormatter),
                prescription.getPrescriptionDate().format(dateFormatter)
        );

        // Показываем диалог с текстом рецепта
        JTextArea textArea = new JTextArea(prescriptionText, 15, 40);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Рецепт #" + prescription.getId(),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelPrescription() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите назначение для отмены");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Отменить выбранное назначение?",
                "Подтверждение отмены",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Prescription prescription = ((PrescriptionTableModel) table.getModel()).getPrescriptionAt(selectedRow);
            prescriptionService.cancelPrescription(prescription.getId());
            updateTable();
        }
    }

    private void replaceDrug() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Выберите назначение для замены препарата");
            return;
        }

        Prescription prescription = ((PrescriptionTableModel) table.getModel()).getPrescriptionAt(selectedRow);

        JDialog dialog = new JDialog();
        dialog.setTitle("Заменить препарат");
        dialog.setLayout(new GridLayout(0, 2, 5, 5));

        JLabel currentDrugLabel = new JLabel(
                "Текущий препарат: " + prescription.getDrug().getCommercialName());
        JComboBox<String> newDrugCombo = new JComboBox<>(
                drugService.getAllDrugs().stream().map(Drug::getCommercialName).toList().toArray(new String[0]));
        JTextArea reasonArea = new JTextArea(3, 20);

        dialog.add(currentDrugLabel);
        dialog.add(new JLabel());
        dialog.add(new JLabel("Новый препарат:"));
        dialog.add(newDrugCombo);
        dialog.add(new JLabel("Причина замены:"));
        dialog.add(new JScrollPane(reasonArea));

        JButton saveButton = new JButton("Сохранить");
        saveButton.addActionListener(e -> {
            Drug newDrug =
                    drugService.getAllDrugs().stream().filter(
                            it -> it.getCommercialName().equals(newDrugCombo.getSelectedItem().toString())
                    ).findFirst().orElse(null);
            String reason = reasonArea.getText();

            if (newDrug == null || newDrug.getId().equals(prescription.getDrug().getId())) {
                JOptionPane.showMessageDialog(dialog, "Выберите другой препарат");
                return;
            }

            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Укажите причину замены");
                return;
            }

            prescriptionService.replaceDrug(prescription.getId(), newDrug.getId(), reason);
            updateTable();
            dialog.dispose();
        });

        dialog.add(new JLabel());
        dialog.add(saveButton);
        dialog.pack();
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    private void showPrescriptionDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) return;

        Prescription prescription = ((PrescriptionTableModel) table.getModel()).getPrescriptionAt(selectedRow);

        JDialog detailsDialog = new JDialog();
        detailsDialog.setTitle("Детали назначения #" + prescription.getId());
        detailsDialog.setLayout(new BorderLayout());

        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setText(formatPrescriptionDetails(prescription));

        detailsDialog.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        detailsDialog.setSize(500, 400);
        detailsDialog.setModal(true);
        detailsDialog.setVisible(true);
    }

    private String formatPrescriptionDetails(Prescription p) {
        return String.format(
                "Детали назначения #%d\n\n" +
                        "Пациент: %s\n" +
                        "Препарат: %s (%s)\n" +
                        "Дозировка: %s\n" +
                        "Схема приема: %s\n" +
                        "Период: %s - %s\n" +
                        "Упаковка: %s\n" +
                        "Контроль: %s\n" +
                        "Статус: %s",
                p.getId(),
                p.getPatient().getFullName(),
                p.getDrug().getCommercialName(),
                p.getDrug().getDosageForm(),
                p.getDosage(),
                p.getAdministrationInstructions(),
                p.getStartDate().format(dateFormatter),
                p.getEndDate().format(dateFormatter),
                p.getDrug().getPackaging(),
                p.getScheme() != null ? p.getScheme().getControlScheme() : "Не указано",
                p.getIsReplaced() ? "Заменен (причина: " + p.getReplacementReason() + ")" : "Активен"
        );
    }

    private class PrescriptionTableModel extends AbstractTableModel {
        private final List<Prescription> prescriptions;
        private final String[] columns = {"Пациент", "Препарат", "Дата назначения", "Период", "Статус"};

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
                case 0 -> p.getPatient().getFullName();
                case 1 -> p.getDrug().getCommercialName();
                case 2 -> p.getPrescriptionDate().format(dateFormatter);
                case 3 -> p.getStartDate().format(dateFormatter) + " - " + p.getEndDate().format(dateFormatter);
                case 4 -> p.getIsReplaced() ? "ЗАМЕНЕН" : "Активен";
                default -> null;
            };
        }

        public Prescription getPrescriptionAt(int row) {
            return prescriptions.get(row);
        }
    }
}