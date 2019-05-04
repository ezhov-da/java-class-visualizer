package ru.ezhov.jclsvis.gui;

import clsvis.model.Class_;
import clsvis.process.importer.BaseProjectImporter;
import clsvis.process.importer.CompiledClassImporter;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ClassBandlePanelIT {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
        {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Throwable ex) {
                //
            }

            String path = "D:\\programmer\\java-library\\image4j-0.7.2.jar";

            BaseProjectImporter baseProjectImporter = new BaseProjectImporter();
            baseProjectImporter.importProject(Collections.singletonList(new File(path)));
            CompiledClassImporter classImporter = baseProjectImporter.getClassImporter();

            Collection<Class_> importedClasses = classImporter.getImportedClasses();

            System.out.println(importedClasses.size());

            List<Class_> list = new ArrayList<>(importedClasses);
            List<Class_> class_s = list.subList(0, 5);

            ClassBandlePanel classBandlePanel = new ClassBandlePanel(class_s, new ClassPanelLocationStorage());
            classBandlePanel.setLocation(0, 0);
            JFrame frame = new JFrame("_________");
            frame.add(classBandlePanel, BorderLayout.CENTER);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}