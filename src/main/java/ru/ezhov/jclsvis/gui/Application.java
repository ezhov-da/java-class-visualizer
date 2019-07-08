package ru.ezhov.jclsvis.gui;

import clsvis.model.Class_;
import clsvis.process.importer.BaseProjectImporter;
import clsvis.process.importer.CompiledClassImporter;
import ru.ezhov.jclsvis.core.domain.JavaResource;
import ru.ezhov.jclsvis.core.domain.Package;
import ru.ezhov.jclsvis.gui.utils.ScreenImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class Application {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
        {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                //
            }
            JFrame frame = new JFrame("Java Class Visualizer");
            frame.setIconImage(new ImageIcon(Application.class.getResource("/images/app_16x16.png")).getImage());


            String path = "D:\\programmer\\wildfly-12.0.0.Final\\modules\\system\\layers\\base\\org\\jboss\\vfs\\main\\jboss-vfs-3.2.12.Final.jar";
//            String path = "D:\\programmer\\java-library\\image4j-0.7.2.jar";
//            String path = "D:\\programmer\\java-library\\jd-gui-1.4.1.jar";

            BaseProjectImporter baseProjectImporter = new BaseProjectImporter();
            baseProjectImporter.importProject(Collections.singletonList(new File(path)));
            CompiledClassImporter classImporter = baseProjectImporter.getClassImporter();

            Collection<Class_> importedClasses = classImporter.getImportedClasses();

            JavaResource javaResource = JavaResource.from(importedClasses);

            JPanel panelBasic = new JPanel(new BorderLayout());

//            Package packageByNameJavaAwt = javaResource.getPackageByName("java.awt");
//            PackagePanel packagePanel = new PackagePanel(javaResource, packageByNameJavaAwt);

            Collection<Package> rootPackages = javaResource.getRootPackages();
            Package packageByName = javaResource.getPackageByName("java.util.logging");
            BasePackagePanel basePackagePanel = new BasePackagePanel(javaResource, rootPackages);
//            BasePackagePanel basePackagePanel = new BasePackagePanel(javaResource, Collections.singleton(packageByName));


            JToolBar toolBar = new JToolBar();
            toolBar.add(new AbstractAction() {
                {
                    putValue(Action.NAME, "Показать все зависимости");
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(basePackagePanel::drawAllDependencies);
                }
            });
            toolBar.add(new AbstractAction() {
                {
                    putValue(Action.NAME, "Скрыть все зависимости");
                }

                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(basePackagePanel::removeDependencies);
                }
            });

            toolBar.add(new AbstractAction() {
                {
                    putValue(Action.NAME, "Сохранить изображение");
                }

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
        });
    }
}
