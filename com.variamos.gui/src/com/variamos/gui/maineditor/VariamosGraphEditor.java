package com.variamos.gui.maineditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Collection;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;

import com.cfm.common.AbstractModel;
import com.cfm.productline.AbstractElement;
import com.cfm.productline.Editable;
import com.cfm.productline.ProductLine;
import com.cfm.productline.VariabilityElement;
import com.cfm.productline.Variable;
import com.cfm.productline.io.SXFMReader;
import com.cfm.productline.type.DomainRegister;
//import com.cfm.productline.type.IntegerType;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.variamos.gui.maineditor.BasicGraphEditor;
import com.variamos.gui.maineditor.EditorPalette;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.shape.mxStencilShape;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUtils;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphSelectionModel;

import java.util.ArrayList;

import com.variamos.gui.pl.editor.ConfiguratorPanel;
import com.variamos.gui.pl.editor.PLEditorToolBar;
import com.variamos.gui.pl.editor.PLGraphEditorFunctions;
import com.variamos.gui.pl.editor.ProductLineGraph;
import com.variamos.gui.pl.editor.SpringUtilities;
import com.variamos.gui.pl.editor.VariabilityAttributeList;
import com.variamos.gui.pl.editor.widgets.Widget;
import com.variamos.gui.pl.editor.widgets.WidgetFactory;
import com.variamos.gui.refas.editor.RefasGraph;
import com.variamos.gui.refas.editor.RefasGraphEditorFunctions;
//import com.variamos.pl.editor.logic.PaletteDatabase;
//import com.variamos.pl.editor.logic.PaletteDatabase.PaletteDefinition;
//import com.variamos.pl.editor.logic.PaletteDatabase.PaletteEdge;
//import com.variamos.pl.editor.logic.PaletteDatabase.PaletteNode;
//import com.variamos.pl.editor.logic.PaletteDatabase.ScriptedVariabilityElement;

import com.variamos.refas.concepts.Refas;

import fm.FeatureModelException;

/**
 * @author jcmunoz
 *
 */
/**
 * @author jcmunoz
 *
 */
@SuppressWarnings("serial")
public class VariamosGraphEditor extends BasicGraphEditor {

	static {
		try {
			mxResources.add("com/variamos/gui/maineditor/resources/editor");
		} catch (Exception e) {
			// ignore
		}
	}
	private int modelViewIndex = 0;
	private ArrayList<String> validElements = null;

	protected DomainRegister domainRegister = new DomainRegister();
	protected GraphTree productLineIndex;
	protected ConfiguratorPanel configurator;
	protected JTextArea messagesArea;
	protected JPanel propertiesPanel;
	protected PerspectiveToolBar perspectiveToolBar;
	// Bottom tabs
	protected JTabbedPane extensionTabs;

	protected int mode = 0;

	public VariamosGraphEditor(String appTitle,
			VariamosGraphComponent component, int perspective, AbstractModel abstractModel) {
		super(appTitle, component, perspective);

		// loadRegularPalette();
		loadScriptedPalettes();
		// loadPalettes();
		registerEvents();
		((AbstractGraph)graphComponent.getGraph()).setModel(abstractModel);
		if (perspective == 0) {
			setPerspective(0);
			graphEditorFunctions = new PLGraphEditorFunctions(this);
			graphEditorFunctions.updateEditor(validElements,
					getGraphComponent(), modelViewIndex);
			// loadRegularPalette(insertPalette(mxResources.get("productLinePalette")));
		}
	}

	public AbstractGraphEditorFunctions getGraphEditorFunctions() {
		return graphEditorFunctions;
	}

	public void setGraphEditorFunctions(AbstractGraphEditorFunctions gef) {
		graphEditorFunctions = gef;
	}

	public int getModelViewIndex() {
		return modelViewIndex;
	}

	public void setVisibleModel(int modelIndex) {
		setDefaultButton();
		modelViewIndex = modelIndex;
		RefasGraph mode = ((RefasGraph) getGraphComponent().getGraph());
		validElements = mode.getValidElements(modelViewIndex);
		mode.setModelViewIndex(modelIndex);
		mode.showElements();

		propertiesPanel.repaint();
	}

	public void updateEditor() {
		graphEditorFunctions.updateEditor(this.validElements,
				getGraphComponent(), modelViewIndex);
		perspectiveToolBar.updateButtons();
	}

	public void updateView() {
		graphEditorFunctions.updateView(this.validElements,
				getGraphComponent(), modelViewIndex);
		// perspectiveToolBar.updateButtons();
	}

	/**
	 * @param appTitle
	 * @param component
	 *            New constructor to load directly files and perspectives
	 * @throws FeatureModelException
	 */
	public static VariamosGraphEditor loader(String appTitle, String file,
			String perspective) throws FeatureModelException {
		AbstractModel abstractModel = null;

		int persp = 0;
		if (perspective.equals("ProductLine")) {
			persp = 0;
			if (file != null) {
				SXFMReader reader = new SXFMReader();
				abstractModel = reader.readFile(file);
			} else

				abstractModel = new ProductLine();
			ProductLineGraph plGraph = new ProductLineGraph();
			// plGraph.add
			VariamosGraphEditor vge = new VariamosGraphEditor(
					"Configurator - VariaMos", new VariamosGraphComponent(
							plGraph), persp, abstractModel);
			return vge;
		} else if (perspective.equals("modeling")) {
			persp = 2;
			RefasGraph refasGraph = null;
			if (file != null) {
				SXFMReader reader = new SXFMReader();
				abstractModel = reader.readRefasFile(file);
				refasGraph = new RefasGraph();
			} else {
				{
					abstractModel = new Refas();
					refasGraph = new RefasGraph();

				}

				// ProductLineGraph plGraph2 = new ProductLineGraph();
				VariamosGraphEditor vge2 = new VariamosGraphEditor(
						"Configurator - VariaMos", new VariamosGraphComponent(
								refasGraph), persp, abstractModel);
				vge2.createFrame().setVisible(true);
				vge2.setVisibleModel(0);
				vge2.setPerspective(2);
				vge2.setGraphEditorFunctions(new RefasGraphEditorFunctions(vge2));
				vge2.updateEditor();
				mxCell root = new mxCell();
				root.insert(new mxCell());
				refasGraph.getModel().setRoot(root);
				refasGraph.addCell(new mxCell("mv0"));
				refasGraph.addCell(new mxCell("mv1"));
				refasGraph.addCell(new mxCell("mv2"));
				refasGraph.addCell(new mxCell("mv3"));
				refasGraph.addCell(new mxCell("mv4"));

				return vge2;
			}
		} else if (perspective.equals("metamodeling")) {
			// todo: change for metamodeling
			persp = 3;
			RefasGraph refasGraph = null;
			if (file != null) {
				SXFMReader reader = new SXFMReader();
				abstractModel = reader.readRefasFile(file);
				refasGraph = new RefasGraph();
			} else {
				{
					abstractModel = new Refas();
					refasGraph = new RefasGraph();

				}

				// ProductLineGraph plGraph2 = new ProductLineGraph();
				VariamosGraphEditor vge2 = new VariamosGraphEditor(
						"Configurator - VariaMos", new VariamosGraphComponent(
								refasGraph), persp, abstractModel);
				vge2.createFrame().setVisible(true);
				vge2.setVisibleModel(0);
				vge2.setPerspective(3);
				vge2.setGraphEditorFunctions(new RefasGraphEditorFunctions(vge2));
				vge2.updateEditor();
				mxCell root = new mxCell();
				root.insert(new mxCell());
				refasGraph.getModel().setRoot(root);
				return vge2;
			}
		}
		return null;
	}

	public void editModel(AbstractModel pl) {
		// productLineIndex.reset();
		 AbstractGraph abstractGraph= null;
		 
		 //todo: review other perspectives
		 if (perspective ==0 || perspective ==1)
			 abstractGraph = new ProductLineGraph();
		 if (perspective ==2 || perspective ==3)
			 abstractGraph = new RefasGraph();
//		 abstractGraph = (AbstractGraph) getGraphComponent()
//				.getGraph();
		 ((VariamosGraphComponent)graphComponent).updateGraph(abstractGraph);
		 registerEvents();

		abstractGraph.setModel(pl);


	//	 productLineIndex.populate(pl);

	}

	
	public void resetView() {
		updateEditor();
		mxGraph graph = getGraphComponent().getGraph();
		// Check modified flag and display save dialog
		mxCell root = new mxCell();
		root.insert(new mxCell());
		graph.getModel().setRoot(root);
		if (perspective == 2) {
			setGraphEditorFunctions(new RefasGraphEditorFunctions(this));

			graph.addCell(new mxCell("mv0"));
			graph.addCell(new mxCell("mv1"));
			graph.addCell(new mxCell("mv2"));
			graph.addCell(new mxCell("mv3"));
			graph.addCell(new mxCell("mv4"));
		}

		setModified(false);
		setCurrentFile(null);
		getGraphComponent().zoomAndCenter();
	}

	private void registerEvents() {
		mxGraphSelectionModel selModel = getGraphComponent().getGraph()
				.getSelectionModel();
		selModel.addListener(mxEvent.CHANGE, new mxIEventListener() {

			@SuppressWarnings("unchecked")
			@Override
			public void invoke(Object sender, mxEventObject evt) {
				// Collection<mxCell> added = (Collection<mxCell>)
				// evt.getProperty("added");
				// System.out.println("Added: " + added);

				Collection<mxCell> removed = (Collection<mxCell>) evt
						.getProperty("removed");
				// System.out.println("Removed: " + removed);

				editProperties(null);

				if (removed == null)
					return;

				mxCell cell = null;
				if (removed.size() == 1)
					cell = removed.iterator().next();

				// Multiselection case
				if (cell == null)
					return;

				if (cell.getValue() instanceof Editable) {
					Editable elm = (Editable) cell.getValue();
					editProperties(elm);
					getGraphComponent().scrollCellToVisible(cell, true);
				}
			}
		});
	}

	private void loadScriptedPalettes() {
		// Load palette from file
		// try {
		// FileReader reader;
		// reader = new FileReader(new File("palettes.pal"));
		// Gson gson = new GsonBuilder().setPrettyPrinting()
		// .serializeNulls()
		// .registerTypeAdapter(Object.class, new NaturalDeserializer())
		// .create();
		// PaletteDatabase db = gson.fromJson(reader, PaletteDatabase.class);
		// loadPaletteDatabase(db);
		// reader.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

	}

	public static String loadShape(EditorPalette palette, File f)
			throws IOException {
		String nodeXml = mxUtils.readFile(f.getAbsolutePath());
		addStencilShape(palette, nodeXml, f.getParent() + File.separator);
		return nodeXml;
	}

	/**
	 * Loads and registers the shape as a new shape in mxGraphics2DCanvas and
	 * adds a new entry to use that shape in the specified palette
	 * 
	 * @param palette
	 *            The palette to add the shape to.
	 * @param nodeXml
	 *            The raw XML of the shape
	 * @param path
	 *            The path to the directory the shape exists in
	 * @return the string name of the shape
	 */
	public static String addStencilShape(EditorPalette palette, String nodeXml,
			String path) {

		// Some editors place a 3 byte BOM at the start of files
		// Ensure the first char is a "<"
		int lessthanIndex = nodeXml.indexOf("<");
		nodeXml = nodeXml.substring(lessthanIndex);
		mxStencilShape newShape = new mxStencilShape(nodeXml);
		String name = newShape.getName();
		ImageIcon icon = null;

		if (path != null) {
			String iconPath = path + newShape.getIconPath();
			icon = new ImageIcon(iconPath);
		}

		// Registers the shape in the canvas shape registry
		mxGraphics2DCanvas.putShape(name, newShape);

		if (palette != null && icon != null) {
			palette.addTemplate(name, icon, "shape=" + name, 80, 80, "");
		}

		return name;
	}

	@Override
	protected Component getLeftComponent() {
		productLineIndex = new GraphTree();
		productLineIndex.bind((AbstractGraph) getGraphComponent().getGraph());

		JSplitPane inner = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				productLineIndex, null);
		inner.setDividerLocation(250);
		inner.setResizeWeight(1);
		inner.setDividerSize(6);
		inner.setBorder(null);

		return inner;
	}

	@Override
	public Component getExtensionsTab() {
		if (extensionTabs != null)
			return extensionTabs;

		messagesArea = new JTextArea("Output");
		messagesArea.setEditable(false);

		propertiesPanel = new JPanel();
		propertiesPanel.setLayout(new SpringLayout());

		configurator = new ConfiguratorPanel();

		// Bottom panel : Properties, Messages and Configuration
		extensionTabs = new JTabbedPane(JTabbedPane.TOP,
				JTabbedPane.SCROLL_TAB_LAYOUT);
		extensionTabs.addTab(mxResources.get("propertiesTab"), new JScrollPane(
				propertiesPanel));
		extensionTabs.addTab(mxResources.get("messagesTab"), new JScrollPane(
				messagesArea));
		extensionTabs.addTab(mxResources.get("configurationTab"),
				new JScrollPane(configurator));

		return extensionTabs;
	}

	public void bringUpExtension(String name) {
		for (int i = 0; i < extensionTabs.getTabCount(); i++) {
			if (extensionTabs.getTitleAt(i).equals(name)) {
				extensionTabs.setSelectedIndex(i);
				return;
			}
		}
	}

	public void bringUpTab(String name) {
		for (int i = 0; i < extensionTabs.getTabCount(); i++) {
			if (extensionTabs.getTitleAt(i).equals(name)) {
				extensionTabs.setSelectedIndex(i);
				return;
			}
		}
	}

	public JTextArea getMessagesArea() {
		return messagesArea;
	}

	public ConfiguratorPanel getConfigurator() {
		return configurator;
	}

	public void editModelReset() {
		productLineIndex.reset();
		if (perspective == 0)
			editModel(new ProductLine());
		else

			editModel(new Refas());
	}

	public void populateIndex(ProductLine pl) {

		// productLineIndex.populate(pl);
		AbstractGraph plGraph = (AbstractGraph) getGraphComponent().getGraph();
		plGraph.buildFromProductLine2(pl, productLineIndex);
		// ((mxGraphModel) plGraph.getModel()).clear();
		// plGraph.setProductLine(pl);

	}


	public AbstractModel getEditedModel() {
		return ((AbstractGraph) getGraphComponent().getGraph())
				.getProductLine();
	}

	public void editProperties(final Editable elm) {
		propertiesPanel.removeAll();

		if (elm == null) {
			bringUpTab("Properties");
			propertiesPanel.repaint();
			return;
		}

		JPanel variablesPanel = new JPanel(new SpringLayout());

		Variable[] editables = elm.getEditableVariables();

		WidgetFactory factory = new WidgetFactory(this);
		for (Variable v : editables) {
			final Widget w = factory.getWidgetFor(v);
			if (w == null)
				// Check the problem and/or raise an exception
				return;

			// TODO: Add listeners to w.
			w.getEditor().addFocusListener(new FocusListener() {
				@Override
				public void focusLost(FocusEvent arg0) {
					// Makes it pull the values.
					Variable v = w.getVariable();
					if (v.getType().equals("String"))
						v.setValue(AbstractElement.multiLine(v.toString(), 15));
					System.out.println("Focus Lost: " + v.hashCode() + " val: "
							+ v.getValue());
					// v.setVariableValue("hola");
					onVariableEdited(elm);
				}

				@Override
				public void focusGained(FocusEvent arg0) {

				}
			});

			w.getEditor().addPropertyChangeListener(
					new PropertyChangeListener() {

						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if (Widget.PROPERTY_VALUE.equals(evt
									.getPropertyName())) {
								w.getVariable();
								onVariableEdited(elm);
							}
						}
					});
			w.getEditor().setMinimumSize(new Dimension(50, 30));
			w.getEditor().setMaximumSize(new Dimension(200, 30));
			w.getEditor().setPreferredSize(new Dimension(200, 30));
			w.editVariable(v);

			// GARA
			// variablesPanel.add(new JLabel(v.getName() + ":: "));
			variablesPanel.add(new JLabel(v.getName() + ": "));
			variablesPanel.add(w);
		}
		// variablesPanel.setPreferredSize(new Dimension(250, 25 *
		// editables.length));
		SpringUtilities.makeCompactGrid(variablesPanel, editables.length, 2, 4,
				4, 4, 4);

		propertiesPanel.add(variablesPanel);

		JPanel attPanel = new JPanel(new SpringLayout());
		// Fill Attributes Panel (Only for VariabilityElements ) in Properties
		// Panel
		if (elm instanceof VariabilityElement) {
			attPanel.setPreferredSize(new Dimension(150, 150));
			attPanel.add(new JLabel(mxResources.get("attributesPanel")));

			VariabilityAttributeList attList = new VariabilityAttributeList(
					this, (VariabilityElement) elm);
			attPanel.add(new JScrollPane(attList));

			SpringUtilities.makeCompactGrid(attPanel, 2, 1, 4, 4, 4, 4);

			propertiesPanel.add(attPanel);

			SpringUtilities.makeCompactGrid(propertiesPanel, 1, 2, 4, 4, 4, 4);
		}

		propertiesPanel.revalidate();
	}

	protected void onVariableEdited(Editable e) {
		((AbstractGraph) getGraphComponent().getGraph()).refreshVariable(e);
	}

	// public DomainRegister getDomainRegister(){
	// return domainRegister;
	// }

	// Not used method
	/*
	 * public void loadPalettes(){ //Load first palette PaletteDefinition pl =
	 * new PaletteDefinition(); pl.name = "Product Lines";
	 * 
	 * PaletteNode node = new PaletteNode(); ScriptedVariabilityElement elm =
	 * new ScriptedVariabilityElement(); List<Variable> atts = new
	 * ArrayList<>(); atts.add(new Variable("height", 0,
	 * IntegerType.IDENTIFIER)); elm.setVarAttributes(atts); node.prototype =
	 * elm; node.width = 80; node.height = 40; node.icon =
	 * "/com/variamos/pl/editor/images/plnode.png"; node.name =
	 * "Variability Element"; node.styleName = "plnode"; pl.nodes.add(node);
	 * 
	 * PaletteEdge edge = new PaletteEdge(); edge.name = "Optional"; edge.icon =
	 * "/com/variamos/pl/editor/images/ploptional.png"; edge.styleName =
	 * "ploptional"; edge.width = 80; edge.height = 40; edge.value =
	 * ConstraintMode.Optional;
	 * 
	 * pl.edges.add(edge);
	 * 
	 * PaletteDatabase db = new PaletteDatabase(); // db.palettes.add(pl);
	 * 
	 * loadPaletteDatabase(db);
	 * 
	 * //Load second palette try { FileWriter writer; writer = new
	 * FileWriter(new File("palettes.pal")); Gson gson = new
	 * GsonBuilder().setPrettyPrinting().serializeNulls().create();
	 * gson.toJson(db, writer); writer.close(); } catch (IOException e) {
	 * e.printStackTrace(); }
	 * 
	 * }
	 */
	/*
	 * private void loadPaletteDatabase(PaletteDatabase db) { for
	 * (PaletteDefinition pal : db.palettes) { EditorPalette palette =
	 * insertPalette(pal.name); for (PaletteNode node : pal.nodes) {
	 * palette.addTemplate( node.name, new
	 * ImageIcon(GraphEditor.class.getResource(node.icon)), node.styleName,
	 * node.width, node.height, node.prototype); }
	 * 
	 * for (PaletteEdge edge : pal.edges) { palette.addEdgeTemplate(edge.name,
	 * new ImageIcon( GraphEditor.class.getResource(edge.icon)), edge.styleName,
	 * edge.width, edge.height, edge.value); } } }
	 */
	// moved to functions classes
	// protected void showGraphPopupMenus(MouseEvent e)
	// {
	// Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
	// graphComponent);
	// PLEditorPopupMenu menu = new PLEditorPopupMenu(VariamosGraphEditor.this);
	// menu.show(graphComponent, pt.x, pt.y);
	//
	// e.consume();
	// }

	protected void installToolBar() {

		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		jp.add(new PLEditorToolBar(this, JToolBar.HORIZONTAL),
				BorderLayout.WEST);
		jp.add(new JLabel(), BorderLayout.CENTER);
		perspectiveToolBar = new PerspectiveToolBar(this, JToolBar.HORIZONTAL,
				perspective);
		jp.add(perspectiveToolBar, BorderLayout.EAST);
		add(jp, BorderLayout.NORTH);
	}
}