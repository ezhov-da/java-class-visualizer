package ru.ezhov.jclsvis.gui;

import clsvis.model.Class_;
import clsvis.process.importer.BaseProjectImporter;
import clsvis.process.importer.CompiledClassImporter;
import ru.ezhov.jclsvis.core.domain.JavaResource;
import ru.ezhov.jclsvis.core.domain.Package;
import ru.ezhov.jclsvis.gui.utils.ScreenImage;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class Application {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
        {
            Runner runner = (files, packageName) -> {
                if (files.isEmpty() && packageName == null) {
                    return;
                }


                JFrame frame = new JFrame("Java Class Visualizer");
                frame.setIconImage(new ImageIcon(Application.class.getResource("/images/app_16x16.png")).getImage());

                BaseProjectImporter baseProjectImporter = new BaseProjectImporter();
                baseProjectImporter.importProject(files.stream().map(File::new).collect(Collectors.toList()));
                CompiledClassImporter classImporter = baseProjectImporter.getClassImporter();

                Collection<Class_> importedClasses = classImporter.getImportedClasses();

                JavaResource javaResource = JavaResource.from(importedClasses);


                JPanel panelBasic = new JPanel(new BorderLayout());

                Collection<Package> packages = packageName == null ? javaResource.getRootPackages() : Collections.singleton(javaResource.getPackageByName(packageName));

                BasePackagePanel basePackagePanel = new BasePackagePanel(javaResource, packages);

                JToolBar toolBar = new JToolBar();
                toolBar.add(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(basePackagePanel::drawAllDependencies);
                    }

                    {
                        putValue(Action.NAME, "Показать все зависимости");
                    }
                });
                toolBar.add(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(basePackagePanel::drawOnlySelectedDependencies);
                    }

                    {
                        putValue(Action.NAME, "Показать только выбранные зависимости");
                    }
                });

                toolBar.add(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(basePackagePanel::drawAllPackageDependencies);
                    }

                    {
                        putValue(Action.NAME, "Отобразить все зависимости пакетов");
                    }
                });
                toolBar.add(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(basePackagePanel::draw4LevelPackageDependencies);
                    }

                    {
                        putValue(Action.NAME, "Отобразить 5-й уровень зависимости пакетов");
                    }
                });

                toolBar.addSeparator();
                toolBar.add(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SwingUtilities.invokeLater(basePackagePanel::removeDependencies);
                    }

                    {
                        putValue(Action.NAME, "Скрыть все зависимости");
                    }
                });

                toolBar.addSeparator();
                toolBar.add(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            JFileChooser fileChooser = new JFileChooser();
                            int showSaveDialog = fileChooser.showSaveDialog(panelBasic);
                            if (showSaveDialog == JFileChooser.OPEN_DIALOG) {
                                File selectedFile = fileChooser.getSelectedFile();
                                ScreenImage.writeImage(ScreenImage.createImage(basePackagePanel), selectedFile.getAbsolutePath());
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                    {
                        putValue(Action.NAME, "Сохранить изображение");
                    }
                });


                panelBasic.add(toolBar, BorderLayout.NORTH);

                JScrollPane scrollPane = new JScrollPane(basePackagePanel);
                panelBasic.add(scrollPane, BorderLayout.CENTER);
                frame.add(panelBasic);
                frame.setSize(1500, 600);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.setVisible(true);


            };

            final SelectedFrame selectedFrame = new SelectedFrame(runner);

            selectedFrame.setVisible(true);
        });

    }
}
