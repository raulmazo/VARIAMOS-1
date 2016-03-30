package com.variamos.perspsupport.opers;

import java.util.List;

import com.variamos.perspsupport.opersint.IntOpersOverTwoRel;
import com.variamos.perspsupport.opersint.IntOpersRelType;

/**
 * A class to represent relations of more than two concepts at semantic level.
 * Part of PhD work at University of Paris 1
 * 
 * @author Juan C. Mu�oz Fern�ndez <jcmunoz@gmail.com>
 * 
 * @version 1.1
 * @since 2014-11-23
 * @see com.cfm.productline.
 */
public class OpersOverTwoRel extends OpersAbstractVertex implements
		IntOpersOverTwoRel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6309224856276191013L;
	private List<IntOpersRelType> semanticRelationTypes;

	public OpersOverTwoRel() {
	}

	public OpersOverTwoRel(String identifier,
			List<IntOpersRelType> semanticRelationTypes) {
		super(identifier);
		this.semanticRelationTypes = semanticRelationTypes;
	}

	@Override
	public List<IntOpersRelType> getSemanticRelationTypes() {
		return semanticRelationTypes;
	}

}