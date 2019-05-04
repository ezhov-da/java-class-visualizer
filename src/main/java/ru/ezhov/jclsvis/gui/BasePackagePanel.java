package ru.ezhov.jclsvis.gui;

import clsvis.model.Class_;
import clsvis.model.RelationDirection;
import clsvis.model.RelationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ezhov.jclsvis.core.domain.JavaResource;
import ru.ezhov.jclsvis.core.domain.Package;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;


public class BasePackagePanel extends JPanel {
    private static final Logger LOG = LoggerFactory.getLogger(BasePackagePanel.class);
    private JavaResource javaResource;
    private Package aPackage;
    private ClassPanelLocationStorage classPanelLocationStorage;

    public BasePackagePanel(JavaResource javaResource, Collection<Package> packages) {
        Package aPackage = new Package("DEFAULT");
        packages.forEach(p -> aPackage.addPackageName(p.getName()));
        this.javaResource = javaResource;
        this.aPackage = aPackage;
        this.classPanelLocationStorage = new ClassPanelLocationStorage();
        init();
    }

    private void init() {
        String packageName = aPackage.getName();
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

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        rh.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));
        rh.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        rh.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE));
        rh.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setRenderingHints(rh);
        graphics2D.setColor(Color.GRAY);

        Map<String, ClassPanel> all = classPanelLocationStorage.all();
        for (Map.Entry<String, ClassPanel> entry : all.entrySet()) {
            String name = entry.getKey();
            ClassPanel classPanel = entry.getValue();
            Class_ class_ = classPanel.getClass_();
            Map<RelationDirection, Map<RelationType, Collection<Class_>>> relationsMap = class_.getRelationsMap();
            for (Map.Entry<RelationDirection, Map<RelationType, Collection<Class_>>> directionMapEntry : relationsMap.entrySet()) {
                RelationDirection relationDirection = directionMapEntry.getKey();
                Map<RelationType, Collection<Class_>> relationTypeCollectionMap = directionMapEntry.getValue();
                for (Map.Entry<RelationType, Collection<Class_>> typeCollectionEntry : relationTypeCollectionMap.entrySet()) {
                    RelationType relationType = typeCollectionEntry.getKey();
                    Collection<Class_> classes = typeCollectionEntry.getValue();
                    for (Class_ classRelation : classes) {
                        Point locationOriginal = classPanel.getLocationOnScreen();
                        Dimension sizeOriginal = classPanel.getSize();
                        ClassPanel classPanelRelation = classPanelLocationStorage.get(classRelation.getOriginalTypeName());
                        Point locationRelation = classPanelRelation.getLocationOnScreen();
                        Dimension sizeRelation = classPanelRelation.getSize();


                        Point p1 = new Point(locationOriginal.x, locationOriginal.y);
                        SwingUtilities.convertPointFromScreen(p1, this);
                        Point p2 = new Point(locationRelation.x, locationRelation.y);
                        SwingUtilities.convertPointFromScreen(p2, this);

                        graphics2D.drawLine(p1.x, p1.y, p2.x, p2.y);
                        //TODO: верно рисовать линии

                    }
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        repaint();
    }
}
