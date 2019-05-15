package ru.ezhov.jclsvis.gui;

import clsvis.model.Class_;
import clsvis.process.importer.BaseProjectImporter;
import clsvis.process.importer.CompiledClassImporter;
import ru.ezhov.jclsvis.core.domain.JavaResource;
import ru.ezhov.jclsvis.core.domain.Package;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Collection;
import java.util.Collections;

public class Application {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
        {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Throwable ex) {
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
            panelBasic.add(basePackagePanel, BorderLayout.CENTER);

            JScrollPane scrollPane = new JScrollPane(panelBasic);
            frame.add(scrollPane);
            frame.setSize(1500, 600);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        });
    }
}
