package org.derekfountain.dofc.c;

import org.derekfountain.dofc.m.Body;
import org.derekfountain.dofc.m.Lens;
import org.derekfountain.dofc.m.MVCModel;
import org.derekfountain.dofc.m.Range;
import org.derekfountain.dofc.v.MVCView;

/**
 * Controller part of the MVC pattern.
 * 
 */
public class MVCController {

	protected MVCView mView = null;
	
	protected MVCModel mModel = null;

	public MVCView getView() {
		return mView;
	}

	public void setView(MVCView view) {
		this.mView = view;
	}

	public MVCModel getModel() {
		return mModel;
	}

	public void setModel(MVCModel model) {
		this.mModel = model;
	}
	
	/**
	 * Responds to user gestures from the view.
	 * <p>
	 * Accept a description of the user's input, translates and checks
	 * the values are sensible, then tells the model code it needs to
	 * pick these up.
	 */
	public void userMadeInput( int focalLength, int aperture, double subjectDistance )
	{
		// Interpret the user's action and define what the
		// model code needs to know about.
		//
		
		mModel.stateChange( focalLength, aperture, subjectDistance );
	}
	
	/**
	 * Respond to the UI telling us the user has changed the body data
	 * 
	 * @param newBody
	 */
	public void userChangedBody( Body newBody )
	{
		mModel.bodyChange( newBody );
	}

	/**
	 * Respond to the UI telling us the user has changed the lens data
	 * 
	 * @param newLens
	 */
	public void userChangedLens( Lens newLens )
	{
		mModel.lensChange( newLens );
	}

	/**
	 * Respond to the UI telling us the user has changed the range data
	 * 
	 * @param newRange
	 */
	public void userChangedRange( Range newRange )
	{
		mModel.rangeChange( newRange );
	}

}
