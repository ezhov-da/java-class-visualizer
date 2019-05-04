package ru.ezhov.jclsvis.gui;

import clsvis.model.Class_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ezhov.jclsvis.core.domain.JavaResource;

import javax.swing.*;

import ru.ezhov.jclsvis.core.domain.Package;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;


public class PackagePanel extends JPanel {
    private static final Logger LOG = LoggerFactory.getLogger(PackagePanel.class);
    private JavaResource javaResource;
    private Package aPackage;
    private ClassPanelLocationStorage classPanelLocationStorage;

    public PackagePanel(JavaResource javaResource, Collection<Package> packages) {
        Package aPackage = new Package("DEFAULT");
        packages.forEach(p -> aPackage.addPackageName(p.getName()));
        this.javaResource = javaResource;
        this.aPackage = aPackage;
        init();
    }


    public PackagePanel(JavaResource javaResource, Package aPackage, ClassPanelLocationStorage classPanelLocationStorage) {
        this.javaResource = javaResource;
        this.aPackage = aPackage;
        this.classPanelLocationStorage = classPanelLocationStorage;
        init();
    }

    private void init() {
        String packageName = aPackage.getName();
        setToolTipText(packageName);
        LOG.trace("Построение панели для пакета {}", packageName);
        setLayout(null);
        setBorder(BorderFactory.createTitledBorder(aPackage.getName()));

        int widthDefault = 100;
        int heightDefault = 100;

        int indent = 15;

        int width = indent;
        int height = indent;
        Set<String> packageNames = aPackage.getPackageNames();
        if (!packageNames.isEmpty()) {
            List<PackagePanel> packagePanels = new ArrayList<>();
            LOG.trace("{}. Количество подпакетов {}", packageName, packageNames.size());
            for (String pn : packageNames) {
                packagePanels.add(new PackagePanel(javaResource, javaResource.getPackageByName(pn), classPanelLocationStorage));
            }
            int columnAndRows = (int) Math.ceil(Math.sqrt(packagePanels.size()));
            LOG.trace("{}. Посчитанное количество строк и столбцов пакетов {}", packageName, columnAndRows);
            int sizePackageWidthClean = 0;
            int sizePackageHeightClean = 0;
            Iterator<PackagePanel> iterator = packagePanels.iterator();
            PackagePanel packagePanelLast = null;
            int maxWidth = 0;
            int maxHeight = 0;
            exit:
            for (int r = 0; r < columnAndRows; r++) {
                for (int c = 0; c < columnAndRows; c++) {
                    if (iterator.hasNext()) {
                        packagePanelLast = iterator.next();
                        int locationX = sizePackageWidthClean + indent;
                        int locationY = sizePackageHeightClean + indent;
                        LOG.trace("{}. Расположение пакета {} на групповой панели: X {} Y {}", packageName, packagePanelLast.getPackageName(), locationX, locationY);
                        packagePanelLast.setLocation(sizePackageWidthClean + indent, sizePackageHeightClean + indent);
                        sizePackageWidthClean += packagePanelLast.getWidth() + indent;
                        LOG.trace("{}. Ширина пакета {}: W {}", packageName, packagePanelLast.getPackageName(), sizePackageWidthClean);
                        maxWidth = Math.max(maxWidth, sizePackageWidthClean);
                        add(packagePanelLast);
                        maxHeight = Math.max(maxHeight, packagePanelLast.getHeight());
                    } else {
                        break exit;
                    }
                }
                sizePackageWidthClean = 0;
                sizePackageHeightClean += maxHeight;
                maxHeight = 0/*обнуляем, так как высота с каждым уровнем считается заново*/;
            }
            width = maxWidth;
            height = sizePackageHeightClean + maxHeight + indent;
        }

        ClassBandlePanel classBandlePanel = buildClassBandlePanel(aPackage.getClassNames());
        classBandlePanel.setLocation(indent, height + indent);
        add(classBandlePanel);

        int widthFinal = Math.max(widthDefault, Math.max(width, classBandlePanel.getWidth() + indent) + indent);
        int heightFinal = Math.max(heightDefault, height + classBandlePanel.getHeight() + indent + indent);
        Dimension dimension = new Dimension(widthFinal, heightFinal);
        setSize(dimension);
        setPreferredSize(dimension);
        LOG.trace("{}. Ширина и высота пакета: W {} H {}", packageName, widthFinal, heightFinal);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.RED),
                        BorderFactory.createTitledBorder(aPackage.getName())
                ));

            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createTitledBorder(aPackage.getName()));
            }

        });
    }

    private ClassBandlePanel buildClassBandlePanel(Set<String> classNames) {
        List<Class_> classes = new ArrayList<>();
        for (String className : classNames) {
            Class_ classByName = javaResource.getClassByName(className);
            classes.add(classByName);
        }
        return new ClassBandlePanel(classes, classPanelLocationStorage);
    }

    public String getPackageName() {
        return aPackage.getName();
    }
}
