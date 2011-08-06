package org.derekfountain.dofc.m;

import java.util.HashMap;

import org.derekfountain.dofc.v.MVCView;

import android.util.Log;

/**
 * Model code.
 * <p>
 * All model code holds distances in metric. The view converts as required.
 * 
 */
public class MVCModel {
	
	// This is a static table of the exact values to use in the model calculations
	// for each supported aperture.
	//
	protected static HashMap<Integer, Double> apertureValues;
	static {
		
		// Fill in the exact values used to make calculation based on f-numbers.
		// These scales came from here: http://en.wikipedia.org/wiki/F-number
		// Where there's overlap or duplicated values I've used the one-third
		// stop scale. That was an arbitrary decision.
		//
		final double SQUARE_ROOT_OF_2 = Math.sqrt(2);
		
		apertureValues = new HashMap<Integer, Double>();
		
		// Full stops
		//
		apertureValues.put(100,  Math.pow(SQUARE_ROOT_OF_2, 0));
		apertureValues.put(140,  Math.pow(SQUARE_ROOT_OF_2, 1));
		apertureValues.put(200,  Math.pow(SQUARE_ROOT_OF_2, 2));
		apertureValues.put(280,  Math.pow(SQUARE_ROOT_OF_2, 3));
		apertureValues.put(400,  Math.pow(SQUARE_ROOT_OF_2, 4));
		apertureValues.put(560,  Math.pow(SQUARE_ROOT_OF_2, 5));
		apertureValues.put(800,  Math.pow(SQUARE_ROOT_OF_2, 6));
		apertureValues.put(1100, Math.pow(SQUARE_ROOT_OF_2, 7));
		apertureValues.put(1600, Math.pow(SQUARE_ROOT_OF_2, 8));
		apertureValues.put(2200, Math.pow(SQUARE_ROOT_OF_2, 9));
		apertureValues.put(3200, Math.pow(SQUARE_ROOT_OF_2, 10));
		apertureValues.put(4500, Math.pow(SQUARE_ROOT_OF_2, 11));
		apertureValues.put(6400, Math.pow(SQUARE_ROOT_OF_2, 12));
		
		// Quarter stops
		//
		// supportedApertures.put(220,  Math.pow(SQUARE_ROOT_OF_2, 2.25));
		apertureValues.put(260,  Math.pow(SQUARE_ROOT_OF_2, 2.75));
		apertureValues.put(340,  Math.pow(SQUARE_ROOT_OF_2, 3.5));
		apertureValues.put(370,  Math.pow(SQUARE_ROOT_OF_2, 3.75));
		apertureValues.put(440,  Math.pow(SQUARE_ROOT_OF_2, 4.25));
		apertureValues.put(520,  Math.pow(SQUARE_ROOT_OF_2, 4.75));
		apertureValues.put(620,  Math.pow(SQUARE_ROOT_OF_2, 5.25));
		apertureValues.put(730,  Math.pow(SQUARE_ROOT_OF_2, 5.75));
		apertureValues.put(870,  Math.pow(SQUARE_ROOT_OF_2, 6.25));
		// supportedApertures.put(1000, Math.pow(SQUARE_ROOT_OF_2, 6.75));
		apertureValues.put(1200, Math.pow(SQUARE_ROOT_OF_2, 7.25));
		// supportedApertures.put(1400, Math.pow(SQUARE_ROOT_OF_2, 7.5));
		apertureValues.put(1500, Math.pow(SQUARE_ROOT_OF_2, 7.75));
		apertureValues.put(1700, Math.pow(SQUARE_ROOT_OF_2, 8.25));
		apertureValues.put(2100, Math.pow(SQUARE_ROOT_OF_2, 8.75));

		// Half stops.
		// Power of 3.5 is labelled as f/3.3 or f/3.4 depending on lens/manufacturer
		// Power of 7.5 is labelled as f/13  or f/14  depending on lens/manufacturer
		// Hence the duplicate values with different labels
		//
		// supportedApertures.put(120,  Math.pow(SQUARE_ROOT_OF_2, 0.5));
		apertureValues.put(170,  Math.pow(SQUARE_ROOT_OF_2, 1.5));
		apertureValues.put(240,  Math.pow(SQUARE_ROOT_OF_2, 2.5));
		apertureValues.put(330,  Math.pow(SQUARE_ROOT_OF_2, 3.5));
		apertureValues.put(480,  Math.pow(SQUARE_ROOT_OF_2, 4.5));
		apertureValues.put(670,  Math.pow(SQUARE_ROOT_OF_2, 5.5));
		apertureValues.put(950,  Math.pow(SQUARE_ROOT_OF_2, 6.5));
		// supportedApertures.put(1300, Math.pow(SQUARE_ROOT_OF_2, 7.5));
		apertureValues.put(1900, Math.pow(SQUARE_ROOT_OF_2, 8.5));

		// Third stops
		// f/1.2 is used to label both power of 0.666 and power of 0.5  depending on lens/manufacturer
		// f/2.2 is used to label both power of 2.333 and power of 2.25 depending on lens/manufacturer
		// f/10  is used to label both power of 6.666 and power of 6.75 depending on lens/manufacturer
		// f/13  is used to label both power of 7.333 and power of 7.5  depending on lens/manufacturer
		// f/14  is used to label both power of 7.666 and power of 7.5  depending on lens/manufacturer
		// In these cases I favour the value produced by the one third stop scale
		//
		apertureValues.put(110,  Math.pow(SQUARE_ROOT_OF_2, 0.3333333333));
		apertureValues.put(120,  Math.pow(SQUARE_ROOT_OF_2, 0.6666666666));
		apertureValues.put(160,  Math.pow(SQUARE_ROOT_OF_2, 1.3333333333));
		apertureValues.put(180,  Math.pow(SQUARE_ROOT_OF_2, 1.6666666666));
		apertureValues.put(220,  Math.pow(SQUARE_ROOT_OF_2, 2.3333333333));
		apertureValues.put(250,  Math.pow(SQUARE_ROOT_OF_2, 2.6666666666));
		apertureValues.put(320,  Math.pow(SQUARE_ROOT_OF_2, 3.3333333333));
		apertureValues.put(350,  Math.pow(SQUARE_ROOT_OF_2, 3.6666666666));
		apertureValues.put(450,  Math.pow(SQUARE_ROOT_OF_2, 4.3333333333));
		apertureValues.put(500,  Math.pow(SQUARE_ROOT_OF_2, 4.6666666666));
		apertureValues.put(630,  Math.pow(SQUARE_ROOT_OF_2, 5.3333333333));
		apertureValues.put(710,  Math.pow(SQUARE_ROOT_OF_2, 5.6666666666));
		apertureValues.put(900,  Math.pow(SQUARE_ROOT_OF_2, 6.3333333333));
		apertureValues.put(1000, Math.pow(SQUARE_ROOT_OF_2, 6.6666666666));
		apertureValues.put(1300, Math.pow(SQUARE_ROOT_OF_2, 7.3333333333));
		apertureValues.put(1400, Math.pow(SQUARE_ROOT_OF_2, 7.6666666666));
		apertureValues.put(1800, Math.pow(SQUARE_ROOT_OF_2, 8.3333333333));
		apertureValues.put(2000, Math.pow(SQUARE_ROOT_OF_2, 8.6666666666));
		apertureValues.put(2500, Math.pow(SQUARE_ROOT_OF_2, 9.3333333333));
		apertureValues.put(2800, Math.pow(SQUARE_ROOT_OF_2, 9.6666666666));

	}
	
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
        
        // Aperture is an integer like 400 for f/4.0. So look up the precise value
        //
        mAperture = new Double(inputAperture);
        Double exactAperture = apertureValues.get(inputAperture);
        
        // Wikipedia says that adding the mFocalLength here is unnecessary, and in fact
        // you have to search the page for mention of its "negligible" effect. Hmmm.
        // All other implementations I've found add this value, and when you use the
        // Hf distance in later calculations this "negligible" effect multiplies up
        // to produce very wrong results in some cases. So I'm adding it, just like
        // everyone else does.
        //
        // http://en.wikipedia.org/wiki/Depth_of_field#DOF_formulas
        //        
        mHyperfocalDistance = (mFocalLength * mFocalLength) / (exactAperture * mCircleOfConfusion) + mFocalLength;
        
        // Distance is in metres, convert to mm
        //
        mDistance *= 1000.0;
        
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
		
        // Calculations complete - convert all values from mm to m, which is what the app expects
        //
        mHyperfocalDistance /= 1000.0;
        mDistance           /= 1000.0;
        mNearLimit          /= 1000.0;
        if ( mFarLimit != null )
        	mFarLimit       /= 1000.0;
        if ( mFrontDistance != null )
        	mFrontDistance /= 1000.0;
        if ( mBehindDistance != null )
        	mBehindDistance /= 1000.0;
        if ( mTotal != null )
        	mTotal          /= 1000.0;
        
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
