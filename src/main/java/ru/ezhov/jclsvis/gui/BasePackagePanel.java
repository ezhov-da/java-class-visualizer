package ru.ezhov.jclsvis.gui;

import clsvis.model.Class_;
import clsvis.model.RelationDirection;
import clsvis.model.RelationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ezhov.jclsvis.core.domain.JavaResource;
import ru.ezhov.jclsvis.core.domain.Package;
import ru.ezhov.jclsvis.gui.utils.distance.CenterPoints;
import ru.ezhov.jclsvis.gui.utils.distance.Distance;
import ru.ezhov.jclsvis.gui.utils.distance.MinimalDistance;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;


public class BasePackagePanel extends JPanel {
    private static final Logger LOG = LoggerFactory.getLogger(BasePackagePanel.class);
    private JavaResource javaResource;
    private Package defaultPackage;
    private ClassPanelLocationStorage classPanelLocationStorage;
    private int defaultClassWidth = 50;
    private int defaultClassHeight = 50;

    private List<PackagePanel> packagePanelsAll = new ArrayList<>();
    private ClassBandlePanel classBandlePanel;

    public BasePackagePanel(JavaResource javaResource, Collection<Package> packages) {
        Package newDefaultPackage = new Package("DEFAULT");
        packages.forEach(p -> newDefaultPackage.addPackageName(p.getName()));
        this.javaResource = javaResource;
        this.defaultPackage = newDefaultPackage;
        this.classPanelLocationStorage = new ClassPanelLocationStorage();
        init();
    }

    private void init() {
        String packageName = defaultPackage.getName();
        LOG.trace("Построение панели для пакета {}", packageName);
        setLayout(null);
        setBorder(BorderFactory.createTitledBorder(defaultPackage.getName()));

        int widthDefault = 100;
        int heightDefault = 100;

        int indent = 15;

        int width = indent;
        int height = indent;
        Set<String> packageNames = defaultPackage.getPackageNames();
        if (!packageNames.isEmpty()) {
            List<PackagePanel> packagePanels = new ArrayList<>();
            LOG.trace("{}. Количество подпакетов {}", packageName, packageNames.size());
            for (String pn : packageNames) {
                packagePanels.add(new PackagePanel(javaResource, javaResource.getPackageByName(pn), classPanelLocationStorage, defaultClassWidth, defaultClassHeight));
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
                        packagePanelsAll.add(packagePanelLast);
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

        classBandlePanel = buildClassBandlePanel(defaultPackage.getClassNames(), defaultClassWidth, defaultClassHeight);
        classBandlePanel.setLocation(indent, height + indent);

        packagePanelsAll.forEach(BasePackagePanel.this::add);
        add(classBandlePanel);

        int widthFinal = Math.max(widthDefault, Math.max(width, classBandlePanel.getWidth() + indent) + indent);
        int heightFinal = Math.max(heightDefault, height + classBandlePanel.getHeight() + indent + indent);
        Dimension dimension = new Dimension(widthFinal, heightFinal);
        setSize(dimension);
        setPreferredSize(dimension);
        LOG.trace("{}. Ширина и высота пакета: W {} H {}", packageName, widthFinal, heightFinal);
    }

    private ClassBandlePanel buildClassBandlePanel(Set<String> classNames, int width, int height) {
        List<Class_> classes = new ArrayList<>();
        for (String className : classNames) {
            Class_ classByName = javaResource.getClassByName(className);
            classes.add(classByName);
        }
        return new ClassBandlePanel(classes, classPanelLocationStorage, width, height);
    }

    public String getPackageName() {
        return defaultPackage.getName();
    }

    private JLabel dependensies;


    public void drawAllDependencies() {
        drawDependencies(classPanelLocationStorage.all());
    }

    public void drawOnlySelectedDependencies() {
        Map<String, ClassPanel> selectedClasses = new HashMap<>();
        Set<Map.Entry<String, ClassPanel>> entries = classPanelLocationStorage.all().entrySet();
        for (Map.Entry<String, ClassPanel> entry : entries) {
            if (entry.getValue().isSelected()) {
                selectedClasses.put(entry.getKey(), entry.getValue());
            }
        }
        drawDependencies(selectedClasses);
    }

    private void drawDependencies(Map<String, ClassPanel> classes) {
        if (dependensies != null) {
            removeDependencies();
        }
        drawClassesDependencies(classes);

        JLabel label = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                RenderingHints rh = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                rh.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));
                rh.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
                rh.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE));
                rh.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
                Graphics2D graphics2D = (Graphics2D) g;
                graphics2D.setRenderingHints(rh);

                points.forEach(p -> {
                    graphics2D.setColor(p.color);
                    graphics2D.drawLine(p.point1.x, p.point1.y, p.point2.x, p.point2.y);
                });

            }
        };

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                System.out.println(1);
            }
        });

        label.setSize(getSize());
        label.setOpaque(false);
        dependensies = label;
        add(label, 0);
        this.revalidate();
        this.repaint();
    }

    public void removeDependencies() {
        if (dependensies != null) {
            this.remove(dependensies);
            this.revalidate();
            this.repaint();
        }
    }

    private List<Points> points;

    private void drawClassesDependencies(Map<String, ClassPanel> classPanels) {
        points = new ArrayList<>();
        for (Map.Entry<String, ClassPanel> entry : classPanels.entrySet()) {
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
                        if (classPanelRelation != null) {
                            Point locationRelation = classPanelRelation.getLocationOnScreen();
                            Dimension sizeRelation = classPanelRelation.getSize();

                            CenterPoints centerPointsOriginal = CenterPoints.from(classPanel);
                            CenterPoints centerPointsRelation = CenterPoints.from(classPanelRelation);

//                            drawDebug(graphics2D, centerPointsOriginal, centerPointsRelation);
                            MinimalDistance minimalDistance = new MinimalDistance();
                            Distance distance = minimalDistance.find(centerPointsOriginal, centerPointsRelation);

                            Point p1 = distance.getFrom();
                            SwingUtilities.convertPointFromScreen(p1, this);
                            Point p2 = distance.getTo();
                            SwingUtilities.convertPointFromScreen(p2, this);

                            Color color;
                            if (classPanel.isSelected() || classPanelRelation.isSelected()) {
                                color = Color.RED;
                            } else {
                                color = Color.BLACK;
                            }
                            points.add(new Points(p1, p2, color));
                        }
                    }
                }
            }
        }
    }

    private void drawDebug(Graphics2D graphics2D, CenterPoints centerPointsOriginal, CenterPoints centerPointsRelation) {
        if ("true".equals(System.getProperty("isDebug", "false"))) {
            Point pointEast = centerPointsOriginal.getPointEast();
            SwingUtilities.convertPointFromScreen(pointEast, this);
            Point pointNorth = centerPointsOriginal.getPointNorth();
            SwingUtilities.convertPointFromScreen(pointNorth, this);
            Point pointSouth = centerPointsOriginal.getPointSouth();
            SwingUtilities.convertPointFromScreen(pointSouth, this);
            Point pointWest = centerPointsOriginal.getPointWest();
            SwingUtilities.convertPointFromScreen(pointWest, this);
            graphics2D.drawString("x: " + pointEast.x + " y: " + pointEast.y, pointEast.x, pointEast.y);
            graphics2D.drawString("x: " + pointNorth.x + " y: " + pointNorth.y, pointNorth.x, pointNorth.y);
            graphics2D.drawString("x: " + pointSouth.x + " y: " + pointSouth.y, pointSouth.x, pointSouth.y);
            graphics2D.drawString("x: " + pointWest.x + " y: " + pointWest.y, pointWest.x, pointWest.y);

            Point pointEastRelation = centerPointsRelation.getPointEast();
            SwingUtilities.convertPointFromScreen(pointEastRelation, this);
            Point pointNorthRelation = centerPointsRelation.getPointNorth();
            SwingUtilities.convertPointFromScreen(pointNorthRelation, this);
            Point pointSouthRelation = centerPointsRelation.getPointSouth();
            SwingUtilities.convertPointFromScreen(pointSouthRelation, this);
            Point pointWestRelation = centerPointsRelation.getPointWest();
            SwingUtilities.convertPointFromScreen(pointWestRelation, this);
            graphics2D.drawString("x: " + pointEastRelation.x + " y: " + pointEastRelation.y, pointEastRelation.x, pointEastRelation.y);
            graphics2D.drawString("x: " + pointNorthRelation.x + " y: " + pointNorthRelation.y, pointNorthRelation.x, pointNorthRelation.y);
            graphics2D.drawString("x: " + pointSouthRelation.x + " y: " + pointSouthRelation.y, pointSouthRelation.x, pointSouthRelation.y);
            graphics2D.drawString("x: " + pointWestRelation.x + " y: " + pointWestRelation.y, pointWestRelation.x, pointWestRelation.y);
        }
    }

//    @Override
//    protected void paintComponent(Graphics g) {
//        setOpaque(true);
//        super.paintComponent(g);
//        drawClassesDependencies();
//
//        points.forEach(p -> {
//            g.setColor(p.color);
//            g.drawLine(p.point1.x, p.point1.y, p.point2.x, p.point2.y);
//        });
//    }

    private class Points {
        private final Point point1;
        private final Point point2;
        private final Color color;

        public Points(Point point1, Point point2, Color color) {
            this.point1 = point1;
            this.point2 = point2;
            this.color = color;
        }
    }
}
