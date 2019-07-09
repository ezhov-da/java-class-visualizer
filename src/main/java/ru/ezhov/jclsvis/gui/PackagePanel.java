package ru.ezhov.jclsvis.gui;

import clsvis.model.Class_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ezhov.jclsvis.core.domain.JavaResource;
import ru.ezhov.jclsvis.core.domain.Package;
import ru.ezhov.jclsvis.gui.utils.MouseMoveWindowListener;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class PackagePanel extends JPanel {
	private static final Logger LOG = LoggerFactory.getLogger(PackagePanel.class);
	private JavaResource javaResource;
	private Package aPackage;
	private ClassPanelLocationStorage classPanelLocationStorage;
	private int widthClass;
	private int heightClass;

	private Border defaultBorder;

	public PackagePanel(JavaResource javaResource, Collection<Package> packages, int widthClass, int heightClass) {
		Package aPackage = new Package("DEFAULT");
		packages.forEach(p -> aPackage.addPackageName(p.getName()));
		this.javaResource = javaResource;
		this.aPackage = aPackage;
		this.widthClass = widthClass;
		this.heightClass = heightClass;
		init();
	}


	public PackagePanel(JavaResource javaResource, Package aPackage, ClassPanelLocationStorage classPanelLocationStorage, int widthClass, int heightClass) {
		this.javaResource = javaResource;
		this.aPackage = aPackage;
		this.classPanelLocationStorage = classPanelLocationStorage;
		this.widthClass = widthClass;
		this.heightClass = heightClass;
		init();
	}

	private void init() {
		setBackground(Color.WHITE);
		setOpaque(true);

		String packageName = aPackage.getName();
		setToolTipText(packageName);
		LOG.trace("Построение панели для пакета {}", packageName);
		setLayout(null);

		defaultBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), aPackage.getSimpleName());
		if (aPackage.getName().split("\\.").length == 5) {
			defaultBorder = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.RED, 5), aPackage.getSimpleName());
		}

		setBorder(defaultBorder);

		int widthDefault = 5;
		int heightDefault = 5;

		int indent = 15;

		int width = indent;
		int height = indent;
		Set<String> packageNames = aPackage.getPackageNames();
		if (!packageNames.isEmpty()) {
			List<PackagePanel> packagePanels = new ArrayList<>();
			LOG.trace("{}. Количество подпакетов {}", packageName, packageNames.size());
			for (String pn : packageNames) {
				packagePanels.add(new PackagePanel(javaResource, javaResource.getPackageByName(pn), classPanelLocationStorage, widthClass, heightClass));
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

		ClassBandlePanel classBandlePanel = buildClassBandlePanel(aPackage.getClassNames(), widthClass, heightClass);
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
				SwingUtilities.invokeLater(() -> {
					setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(Color.RED),
						BorderFactory.createTitledBorder(aPackage.getSimpleName())
					));
				});
			}

			@Override
			public void mouseExited(MouseEvent e) {
				SwingUtilities.invokeLater(() -> {
					setBorder(defaultBorder);
				});
			}
		});

		MouseMoveWindowListener mouseMoveWindowListener = new MouseMoveWindowListener(this);
		this.addMouseMotionListener(mouseMoveWindowListener);
		this.addMouseListener(mouseMoveWindowListener);
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
		return aPackage.getName();
	}
}
