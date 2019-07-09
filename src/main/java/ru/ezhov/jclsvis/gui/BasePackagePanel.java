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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class BasePackagePanel extends JPanel {
	private static final Logger LOG = LoggerFactory.getLogger(BasePackagePanel.class);
	private JavaResource javaResource;
	private Package defaultPackage;
	private ClassPanelLocationStorage classPanelLocationStorage;
	private int defaultClassWidth = 10;
	private int defaultClassHeight = 10;

	private List<PackagePanel> packagePanelsAll = new ArrayList<>();
	private ClassBandlePanel classBandlePanel;
	private JLabel dependensies;
	private List<Points> points;
	private List<Points> packagePoints;

	public BasePackagePanel(JavaResource javaResource, Collection<Package> packages) {
		Package newDefaultPackage = new Package("DEFAULT");
		packages.forEach(p -> newDefaultPackage.addPackageName(p.getName()));
		this.javaResource = javaResource;
		this.defaultPackage = newDefaultPackage;
		this.classPanelLocationStorage = new ClassPanelLocationStorage();
		init();
	}

	private void init() {
		setBackground(Color.WHITE);
		setOpaque(true);

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
						classPanelLocationStorage.add(packagePanelLast.getPackageName(), packagePanelLast);
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
		LOG.trace("method=drawDependencies action=\"начало отображения зависимостей\"");
		drawClassesDependencies(classes);
		drawPoints(points);
	}

	private void drawPoints(List<Points> points) {
		if (dependensies != null) {
			removeDependencies();
		}

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
					graphics2D.drawOval(p.point1.x - 5, p.point1.y - 5, 10, 10);
					graphics2D.drawOval(p.point2.x - 5, p.point2.y - 5, 10, 10);


					int x = (p.point1.x + p.point2.x) / 2;
					int y = (p.point1.y + p.point2.y) / 2;


					if (p.info != null && !"".equals(p.info)) {
						graphics2D.setColor(Color.RED);
						graphics2D.drawString(p.info, x, y);
						graphics2D.drawOval(x - 3, y - 3, 6, 6);
					}
				});
			}
		};

		label.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
//				System.out.println(1);
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

	public void drawAllPackageDependencies() {
		LOG.trace("method=drawAllPackageDependencies action=\"начало отображения зависимостей\"");
		drawPackageDependencies(classPanelLocationStorage.all());
		drawPoints(packagePoints);
	}

	public void draw4LevelPackageDependencies() {
		LOG.trace("method=drawAllPackageDependencies action=\"начало отображения зависимостей\"");
		drawPackageLevelDependencies(classPanelLocationStorage.all(), 5);
		drawPoints(packagePoints);
	}

	private void drawClassesDependencies(Map<String, ClassPanel> classPanels) {
		LOG.trace("method=drawClassesDependencies action=\"получение точек для '{}' классов\"", classPanels.size());
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
						ClassPanel classPanelRelation = classPanelLocationStorage.get(classRelation.getOriginalTypeName());
						if (classPanelRelation != null) {
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
							points.add(new Points(p1, p2, color, ""/*class_.name + " : " + classRelation.name*/, classPanel, classPanelRelation));
						}
					}
				}
			}
		}
	}

	private void drawPackageDependencies(Map<String, ClassPanel> classPanels) {
		LOG.trace("method=drawPackageDependencies action=\"получение точек для '{}' классов\"", classPanels.size());
		packagePoints = new ArrayList<>();
		Map<String, Set<String>> packages = new HashMap<>();
		for (Map.Entry<String, ClassPanel> entry : classPanels.entrySet()) {
			ClassPanel classPanel = entry.getValue();
			Class_ class_ = classPanel.getClass_();
			Map<RelationDirection, Map<RelationType, Collection<Class_>>> relationsMap = class_.getRelationsMap();
			for (Map.Entry<RelationDirection, Map<RelationType, Collection<Class_>>> directionMapEntry : relationsMap.entrySet()) {
				RelationDirection relationDirection = directionMapEntry.getKey();
				Map<RelationType, Collection<Class_>> relationTypeCollectionMap = directionMapEntry.getValue();
				for (Map.Entry<RelationType, Collection<Class_>> typeCollectionEntry : relationTypeCollectionMap.entrySet()) {
					Collection<Class_> classes = typeCollectionEntry.getValue();
					for (Class_ classRelation : classes) {
						String packageNameFrom = class_.getPackageName();
						String packageNameTo = classRelation.getPackageName();
						if (!packageNameFrom.startsWith(packageNameTo) || !packageNameTo.startsWith(packageNameFrom)) {
							Set<String> packagesTo = packages.get(packageNameFrom);
							if (packagesTo == null) {
								packagesTo = new HashSet<>();
								packages.put(packageNameFrom, packagesTo);
							}
							packagesTo.add(packageNameTo);
						}
					}
				}
			}
		}

		for (Map.Entry<String, Set<String>> packagee : packages.entrySet()) {
			PackagePanel packagePanel = classPanelLocationStorage.getPackagePanel(packagee.getKey());
			if (packagePanel != null) {
				for (String relationPanel : packagee.getValue()) {
					PackagePanel packagePanelRelation = classPanelLocationStorage.getPackagePanel(relationPanel);
					if (packagePanelRelation != null) {
						CenterPoints centerPointsOriginal = CenterPoints.from(packagePanel);
						CenterPoints centerPointsRelation = CenterPoints.from(packagePanelRelation);

						MinimalDistance minimalDistance = new MinimalDistance();
						Distance distance = minimalDistance.find(centerPointsOriginal, centerPointsRelation);

						Point p1 = distance.getFrom();
						SwingUtilities.convertPointFromScreen(p1, this);
						Point p2 = distance.getTo();
						SwingUtilities.convertPointFromScreen(p2, this);

						Color color = Color.BLACK;
						packagePoints.add(new Points(p1, p2, color, "" /*packagee.getKey() + " : " + relationPanel*/, packagePanel, packagePanelRelation));
					}
				}
			}
		}
	}


	private void drawPackageLevelDependencies(Map<String, ClassPanel> classPanels, int level) {
		LOG.trace("method=drawPackageDependencies action=\"получение точек для '{}' классов\"", classPanels.size());
		packagePoints = new ArrayList<>();
		Map<String, Set<String>> packages = new HashMap<>();
		for (Map.Entry<String, ClassPanel> entry : classPanels.entrySet()) {
			ClassPanel classPanel = entry.getValue();
			Class_ class_ = classPanel.getClass_();
			Map<RelationDirection, Map<RelationType, Collection<Class_>>> relationsMap = class_.getRelationsMap();
			for (Map.Entry<RelationDirection, Map<RelationType, Collection<Class_>>> directionMapEntry : relationsMap.entrySet()) {
				RelationDirection relationDirection = directionMapEntry.getKey();
				Map<RelationType, Collection<Class_>> relationTypeCollectionMap = directionMapEntry.getValue();
				for (Map.Entry<RelationType, Collection<Class_>> typeCollectionEntry : relationTypeCollectionMap.entrySet()) {
					Collection<Class_> classes = typeCollectionEntry.getValue();
					for (Class_ classRelation : classes) {
						String packageNameFrom = class_.getPackageName();
						String[] splitFrom = packageNameFrom.split("\\.");
						if (splitFrom.length >= level) {
							String[] splitFromCopy = new String[level];
							System.arraycopy(splitFrom, 0, splitFromCopy, 0, level);
							packageNameFrom = String.join(".", splitFromCopy);
						}


						String packageNameTo = classRelation.getPackageName();
						String[] splitTo = packageNameTo.split("\\.");
						if (splitTo.length >= level) {
							String[] splitToCopy = new String[level];
							System.arraycopy(splitTo, 0, splitToCopy, 0, level);
							packageNameTo = String.join(".", splitToCopy);
						}
						if (!packageNameFrom.startsWith(packageNameTo) || !packageNameTo.startsWith(packageNameFrom)) {
							Set<String> packagesTo = packages.get(packageNameFrom);
							if (packagesTo == null) {
								packagesTo = new HashSet<>();
								packages.put(packageNameFrom, packagesTo);
							}
							packagesTo.add(packageNameTo);
						}
					}
				}
			}
		}

		for (Map.Entry<String, Set<String>> packagee : packages.entrySet()) {
			PackagePanel packagePanel = classPanelLocationStorage.getPackagePanel(packagee.getKey());
			if (packagePanel != null) {
				for (String relationPanel : packagee.getValue()) {
					PackagePanel packagePanelRelation = classPanelLocationStorage.getPackagePanel(relationPanel);
					if (packagePanelRelation != null) {
						CenterPoints centerPointsOriginal = CenterPoints.from(packagePanel);
						CenterPoints centerPointsRelation = CenterPoints.from(packagePanelRelation);

						MinimalDistance minimalDistance = new MinimalDistance();
						Distance distance = minimalDistance.find(centerPointsOriginal, centerPointsRelation);

						Point p1 = distance.getFrom();
						SwingUtilities.convertPointFromScreen(p1, this);
						Point p2 = distance.getTo();
						SwingUtilities.convertPointFromScreen(p2, this);

						Color color = Color.BLACK;
						packagePoints.add(new Points(p1, p2, color, ""/*packagee.getKey() + " : " + relationPanel*/, packagePanel, packagePanelRelation));
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

	private class Points {
		private final Point point1;
		private final Point point2;
		private final Color color;

		private JComponent componentOne;
		private JComponent componentTwo;

		private String info;

		public Points(Point point1, Point point2, Color color, String info, JComponent componentOne, JComponent componentTwo) {
			this.point1 = point1;
			this.point2 = point2;
			this.color = color;
			this.info = info;
			this.componentOne = componentOne;
			this.componentTwo = componentTwo;
		}
	}
}
