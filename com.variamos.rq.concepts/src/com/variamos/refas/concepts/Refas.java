package com.variamos.refas.concepts;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cfm.common.AbstractModel;
import com.cfm.productline.AbstractElement;
import com.cfm.productline.Asset;
import com.cfm.productline.AssetModel;
import com.cfm.productline.Constraint;
import com.cfm.productline.VariabilityElement;
import com.cfm.productline.constraints.GroupConstraint;
import com.cfm.productline.constraints.MandatoryConstraint;
import com.cfm.productline.constraints.OptionalConstraint;
import com.cfm.productline.constraints.RequiresConstraint;
import com.cfm.productline.constraints.TwoOperandConstraint;
import com.mxgraph.util.mxResources;

/**
 * @author jcmunoz
 * Initially Copied from ProductLine
 */
public class Refas extends AbstractModel{
	
	/**
	 * Adds required resources for i18n
	 */
	static {
		try {
			mxResources.add("com/mxgraph/examples/swing/resources/editor");
		} catch (Exception e) {
			// ignore
		}
	}
	
	protected Map<String, VariabilityElement> vElements;
	protected Map<String,Constraint> constraints;
	protected String name;
	protected ModelView[] modelViews;
	public ModelView[] getModelViews() {
		return modelViews;
	}

	protected AssetModel assetModel;
	
	public Refas(){
		vElements = new HashMap<String, VariabilityElement>();
		constraints = new HashMap<>(); //old, from ProductLine
		assetModel = new AssetModel(); //old, from ProductLine
		defaultModelViews();
		name="";
	}
	
	/**
	 * jcmunoz: temporal method to fill valid element by models
	 * model order: Goals, SG, Context, SG Satisficing, Assets
	 */
	
	public boolean[] elementsValidation(String element)
	{
		boolean [] valid = new boolean[5];
		for (int i = 0; i< Integer.parseInt(mxResources.get("modelViews")); i++)
		{
			if (modelViews[i].getValidElements().contains(element))
				valid[i]=true;
		}
		
		return valid;
	}
	
	public ArrayList<String> getValidElements(int modelView)
	{
		if (modelView>=modelViews.length)
			return null;
		else
			return modelViews[modelView].getValidElements();
	}
	
	public void defaultModelViews()
	{
		ArrayList<String> validElements[] = new ArrayList[Integer.parseInt(mxResources.get("modelViews"))];
		modelViews = new ModelView[Integer.parseInt(mxResources.get("modelViews"))];
		
		for (int i = 0 ; i < Integer.parseInt(mxResources.get("modelViews")); i++)
		{
			validElements[i]	= new ArrayList<String>();
		}
		
		//todo: load from metamodel concepts and relations associated to each model view
		validElements[0].add("Goal");
		validElements[0].add("Operationalization");
		validElements[0].add("Assumption");
		validElements[0].add("GroupGConstraint");
		
		validElements[1].add("SoftGoal");
		
		validElements[2].add("ContextGroup");
		validElements[2].add("GlobalContextVariable");
		validElements[2].add("LocalContextVariable");
		
		validElements[3].add("Claim");
		validElements[3].add("SoftGoal");
		validElements[3].add("SoftDependency");
		validElements[3].add("Operationalization");
		validElements[3].add("ContextVariable");
		validElements[3].add("GroupGConstraint");
		
		validElements[4].add("Asset");
		validElements[4].add("Operationalization");
		validElements[4].add("GroupGConstraint");

		for (int i = 0 ; i < Integer.parseInt(mxResources.get("modelViews")); i++)
		{
			modelViews[i] = new ModelView(validElements[i]);
		}
		

	}


	public Collection<Constraint> getConstraints() {
		return constraints.values();
	}

	public void setConstraints(Map<String,Constraint> constraints) {
		this.constraints = constraints;
	}
	
	public Collection<VariabilityElement> getVariabilityElements(){
		return vElements.values();
	}
	
	public void printDebug(PrintStream out){
		System.out.println("#============= VariabilityElements : " + getVariabilityElements().size() + " =============");
		for(VariabilityElement vp : getVariabilityElements()){
			vp.printDebug(out);
		}
		
		System.out.println("#============= Constraints : " + getConstraints().size() + " =============");
		for(Constraint c : getConstraints()){
			c.printDebug(out);
		}
	}
	
	
	//GARA
	public void printDebugC(PrintStream out){
		System.out.println("#============= Constraints : " + getConstraints().size() + " =============");
		for(Constraint c : getConstraints()){
			c.printDebug(out);
		}
	}
	
	public String addConstraint(int modelView, Constraint a ){
		return modelViews[modelView].addConstraint(a);
	}
	public void addConstraint(Constraint c){
		String id = c.getIdentifier();
		//System.out.println(id );
		//if( id == null ){			
			id = getNextConstraintId();
			//System.out.println("nueva" + id );
			//printDebugC(System.out);
			c.setIdentifier(id);
			constraints.put(id, c);
		/*}
		else{
			int j = Integer.parseInt(id);
			while( constraints.containsKey("cons" + j) ){
				j++;
			}
			id="cons" + j;
			System.out.println(id);
			constraints.put(id, c);
		}*/
		
		//constraints.put(id, c);
		//System.out.println("AFTER");
		//printDebugC(System.out);
	}
	
	public void addConstraintGARA(Constraint c){
		String id = c.getIdentifier();
		//System.out.println(id );
		if( id == null ){			
			id = getNextConstraintId();
			//System.out.println("nueva" + id );
			//printDebugC(System.out);
			c.setIdentifier(id);
			constraints.put(id, c);
		}
		else{
			int j = Integer.parseInt(id);
			while( constraints.containsKey("cons" + j) ){
				j++;
			}
			id="cons" + j;
			System.out.println(id);
			constraints.put(id, c);
		}
		
		//constraints.put(id, c);
		System.out.println("AFTER");
		printDebugC(System.out);
	}
	public String getNextConstraintId() {
		int id = constraints.size();
		while( constraints.containsKey("cons" + id) ){
			id ++;
		}
		return "cons" + id;
	}

	public VariabilityElement addVariabilityPoint(VariabilityElement vp){
		String id = vp.getIdentifier();
		
		if( id == null ){
			id = getNextVPId();
			vp.setIdentifier(id);
		}
		vElements.put(id, vp);
		
		return vp;
	}
	
	public String getNextVPId(){
		int id = vElements.size();
		while( vElements.containsKey("Vp" + id) ){
			id ++;
		}
		return "Vp" + id;
	}
		
	public List<Constraint> getConstraintsFor(VariabilityElement vp){
		List<Constraint> cons = new LinkedList<Constraint>();
		for(Constraint c : constraints.values())
			if( c.getRelatedIds().contains(vp.getIdentifier()))
				cons.add(c);

		return cons;
	}
	
	public List<Constraint> getConstraintsStartingFrom(VariabilityElement vp){
		List<Constraint> cons = new LinkedList<Constraint>();
		for(Constraint c : constraints.values()){
			if( 
					c instanceof GroupConstraint || 
					c instanceof MandatoryConstraint || 
					c instanceof OptionalConstraint || 
					c instanceof RequiresConstraint  
					)
				if( c.getConstraintSource().equals(vp.getIdentifier()))
					cons.add(c);
		}
		return cons;
	}

	public Constraint getConstraint(String consId) {
		return constraints.get(consId);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

	public Set<String> getRoots(){
		Set<String> roots = new HashSet<>();

		//All are possible roots
		roots.addAll( vElements.keySet() );
		
		//Iterate through the constraints
		for( Constraint c : constraints.values() ){
			//If the constraint is not mandatory or optional
			if( ( c instanceof MandatoryConstraint || c instanceof OptionalConstraint) ){
				//If is the one at the target side remove it
				TwoOperandConstraint tc = (TwoOperandConstraint)c;
				roots.remove( tc.getFeature2Id() );
			}
			//If it's a group constraint
			if( c instanceof GroupConstraint ){
				//Remove all except the parent
				GroupConstraint gc = (GroupConstraint)c;
				
				boolean parentIsRoot = roots.contains(gc.getParent());
				
				for( String child : gc.getRelatedIds() )
					roots.remove( child );
				
				if( parentIsRoot )
					roots.add(gc.getParent());
			}
		}
		
		return roots;
	}
	
	public void addAsset( Asset a ){
		assetModel.addAsset(a);
	}
	
	public Map<String, Asset> getAssets(){
		return assetModel.getAssets();
	}
	
	public String addElement(int modelView, AbstractElement a ){
		return modelViews[modelView].addElement(a);
	}
	
		public Map<String, AbstractElement> getElements(int modelView){
		return modelViews[modelView].getElements();

	}
	
	public VariabilityElement getVariabilityElement(String string) {
		return vElements.get(string);
	}

	@Override
	public String toString() {
		return getName();
	}
}
