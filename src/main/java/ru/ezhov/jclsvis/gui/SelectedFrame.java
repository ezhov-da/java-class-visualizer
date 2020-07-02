package ru.ezhov.jclsvis.gui;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SelectedFrame extends JFrame {
    private FilesPanel filesPanel;
    private SelectedPackagePanel selectedPackagePanel;
    private JButton buttonOk;

    public SelectedFrame(Runner runner) {

        setTitle("Выбор");

        JPanel panel = new JPanel(new BorderLayout());

        filesPanel = new FilesPanel();
        selectedPackagePanel = new SelectedPackagePanel();
        buttonOk = new JButton("OK");
        buttonOk.addActionListener(a -> {
            SwingUtilities.invokeLater(() -> {
                SelectedFrame.this.setVisible(false);
                SelectedFrame.this.dispose();

                runner.run(filesPanel.getFiles(), selectedPackagePanel.inputPackage().orElse(null));
            });

        });

        panel.add(selectedPackagePanel, BorderLayout.NORTH);
        panel.add(filesPanel, BorderLayout.CENTER);
        panel.add(buttonOk, BorderLayout.SOUTH);

        add(panel, BorderLayout.CENTER);

        setMinimumSize(new Dimension(400, 300));

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }

    private static class FilesPanel extends JPanel {
        private JTextArea textArea;

        public FilesPanel() {
            super(new BorderLayout());
            this.textArea = new JTextArea();
            add(new JScrollPane(textArea), BorderLayout.CENTER);
        }

        public List<String> getFiles() {
            return Arrays.asList(textArea.getText().split("\n"));
        }
    }

    private static class SelectedPackagePanel extends JPanel {
        private final JRadioButton radioButtonDefault;
        private final JRadioButton radioButtonConcrete;
        private final JTextField textFieldPackage;

        public SelectedPackagePanel() {
            super(new BorderLayout());

            this.radioButtonDefault = new JRadioButton("Все");
            radioButtonDefault.setSelected(true);
            this.radioButtonConcrete = new JRadioButton("Указать");
            this.textFieldPackage = new JTextField();
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(radioButtonDefault);
            buttonGroup.add(radioButtonConcrete);

            textFieldPackage.setEnabled(false);

            radioButtonConcrete.addActionListener(l -> {
                textFieldPackage.setEnabled(radioButtonConcrete.isSelected());
            });

            radioButtonDefault.addActionListener(l -> {
                textFieldPackage.setEnabled(!radioButtonDefault.isSelected());
            });

            JPanel panel = new JPanel();

            panel.add(radioButtonDefault);
            panel.add(radioButtonConcrete);

            add(panel, BorderLayout.WEST);
            add(textFieldPackage, BorderLayout.CENTER);
        }

        public Optional<String> inputPackage() {
            if (radioButtonDefault.isSelected()) {
                return Optional.empty();
            } else {
                if (!"".equals(textFieldPackage.getText())) {
                    return Optional.ofNullable(textFieldPackage.getText());
                } else {
                    return Optional.empty();
                }
            }
        }
    }

}
