package org.derekfountain.dofc.m;

import org.derekfountain.dofc.v.MVCView;

import android.util.Log;

/**
 * Model code.
 * <p>
 * All model code holds distances in metric. The view converts as required.
 * 
 */
public class MVCModel {

	// Internal model structures
	//
	protected Body  mBody = null;
	protected Lens  mLens = null;
	protected Range mRange = null;
	
	// View object, to notify changes to
	//
	protected MVCView mView = null;
	
	// Result set
	//
	protected Double mFocalLength = null;
	protected Double mAperture = null;
	protected Double mDistance = null;
	protected Double mNearLimit = null;
	protected Double mFarLimit = null;
	protected Double mTotal = null;
	protected Double mFrontDistance = null;
	protected Double mBehindDistance = null;
	protected Double mHyperfocalDistance = null;
	protected Double mCircleOfConfusion = null;
	
	public Body getBody() {
		return mBody;
	}
	public Lens getLens() {
		return mLens;
	}
	public Range getRange() {
		return mRange;
	}
	public Double getFocalLength() {
		return mFocalLength;
	}
	public Double getNearLimit() {
		return mNearLimit;
	}
	public Double getFarLimit() {
		return mFarLimit;
	}
	public Double getTotal() {
		return mTotal;
	}
	public Double getFrontDistance() {
		return mFrontDistance;
	}
	public Double getBehindDistance() {
		return mBehindDistance;
	}
	public Double getHyperfocalDistance() {
		return mHyperfocalDistance;
	}
	public Double getCircleOfConfusion() {
		return mCircleOfConfusion;
	}
	public boolean isValidState() {
		
		// These will always be valid if the calculations have happened
		//		
		return (mHyperfocalDistance != null) && (mCircleOfConfusion  != null);
	}
	public void setView(MVCView view) {
		this.mView = view;
	}
	
	public MVCModel(Body body, Lens lens, Range range) {
		super();
		this.mBody  = body;
		this.mLens  = lens;	
		this.mRange = range;	
	}

	/**
	 * Responds to user initiated state change and recalculates all the
	 * internal values.
	 * <p>
	 * The controller calls this method when the model's
	 * state needs to be changed, normally, but not necessarily,
	 * as a result of user input.
	 * <p>
	 * The controller will have made sure the values are sane, so
	 * they can be trusted.
	 */
	public void stateChange( int inputFocalLength, int inputAperture, double inputSubjectDistance )
	{
		/*
		 * Update the model state - do the calculations.
		 */
		Log.v("Model.stateChange", String.format("Inputs of focal length: %d, aperture: %d, distance: %f",
				                                 inputFocalLength, inputAperture, inputSubjectDistance));

		mFocalLength = new Double(inputFocalLength);                // In mm
		
        // Circle of confusion is in mm in the body information.
        // The camera's "crop factor" and the CoC are effectively the same thing.
		//
        mCircleOfConfusion = mBody.getCircleOfConfusion();			// In mm
        
        // The distance slider slides across the depth of the range. It returns the
        // correct represented distance in metres.
        //
        mDistance = new Double(inputSubjectDistance);
        
        // Aperture is an integer like 400 for f/4.0. So divide by 100.
        //
        mAperture = new Double(inputAperture);
        double apertureOver100 = mAperture / 100.00;
        
        // Wikipedia says that adding the mFocalLength here is unnecessary, and in fact
        // you have to search the page for mention of its "negligible" effect. Hmmm.
        // All other implementations I've found add this value, and when you use the
        // Hf distance in later calculations this "negligible" effect multiplies up
        // to produce very wrong results in some cases. So I'm adding it, just like
        // everyone else does.
        //
        // http://en.wikipedia.org/wiki/Depth_of_field#DOF_formulas
        //
        mHyperfocalDistance = (mFocalLength * mFocalLength) / (apertureOver100 * mCircleOfConfusion) + mFocalLength;
        
        // Inputs to the above are all mm, so so is the result; convert to metres.
        //
        mHyperfocalDistance /= 1000.0;
        
        double hypTimesDistance = (mHyperfocalDistance * mDistance);
        double hypPlusDistance  = (mHyperfocalDistance + mDistance);
        double hypMinusDistance = (mHyperfocalDistance - mDistance);
        if ( Math.abs(mDistance - mHyperfocalDistance) < 0.000001 ) {
        	
        	// Subject is at exactly hyperfocal distance
        	//
        	mNearLimit = new Double((mHyperfocalDistance / 2));
            mFarLimit = null;
            mBehindDistance = null;
            mTotal = null;
        }
        else if ( mDistance < mHyperfocalDistance ) {
        	
        	// Subject is closer than hyperfocal distance
        	//
        	mNearLimit = new Double( hypTimesDistance / hypPlusDistance );
        	mFarLimit  = new Double( hypTimesDistance / hypMinusDistance );
        	mBehindDistance = new Double( mFarLimit - mDistance );
        	mTotal = new Double( mFarLimit - mNearLimit );
        }            
        else {
        	
        	// Subject is beyond hyperfocal distance
        	//
        	mNearLimit = new Double( hypTimesDistance / hypPlusDistance );
        	mFarLimit = null;
        	mBehindDistance = null;
        	mTotal = null;
        }
        
        mFrontDistance = new Double( mDistance - mNearLimit );
		
		Log.v("Model.stateChange", String.format("Yields near limit of: %f, far limit: %f",
				                                 mNearLimit, mFarLimit));

		// Tell the view that the model state has changed
		//
		mView.modelHasChanged();
	}
	
	/**
	 * Update the model when the body information changes.
	 * 
	 * @param newBody
	 */
	public void bodyChange( Body newBody )
	{
		this.mBody = newBody;
		
		if ( mFocalLength != null && mAperture != null && mDistance != null )
			stateChange( (int)mFocalLength.doubleValue(), (int)mAperture.doubleValue(), (int)mDistance.doubleValue() );
	}

	/**
	 * Update the model when the lens information changes.
	 * 
	 * @param newLens
	 */
	public void lensChange( Lens newLens )
	{
		this.mLens = newLens;
		
		if ( mFocalLength != null && mAperture != null && mDistance != null )
			stateChange( (int)mFocalLength.doubleValue(), (int)mAperture.doubleValue(), (int)mDistance.doubleValue() );
	}

	/**
	 * Update the model when the range information changes.
	 * 
	 * @param newRange
	 */
	public void rangeChange( Range newRange )
	{
		this.mRange = newRange;
		
		if ( mFocalLength != null && mAperture != null && mDistance != null )
			stateChange( (int)mFocalLength.doubleValue(), (int)mAperture.doubleValue(), (int)mDistance.doubleValue() );
	}
}
