package org.derekfountain.dofc.v;

import org.derekfountain.dofc.R;
import org.derekfountain.dofc.c.MVCController;
import org.derekfountain.dofc.m.Body;
import org.derekfountain.dofc.m.Lens;
import org.derekfountain.dofc.m.MVCModel;
import org.derekfountain.dofc.m.Range;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * This is the GUI view structure part of the MVC pattern.
 * 
 */
public class MVCView {

	// Enum describes the result being shown in metres or feet.
	//
	public enum Units {
		METRIC,
		IMPERIAL
	}
	
	// Conversion factor
	//
	public final static double FEET_PER_METRE = 3.2808399;
	
	// Android activity this view runs under. The activity knows its main content
	// widget and from there Activity.findViewById() will yield any view in the
	// GUI. The activity also knows how to get resources, like the strings and
	// stuff that the MVC view code needs. This activity reference needs to be
	// removed when the activity disappears so to prevent memory leaks.
	//
	protected Activity mActivity = null;
	
	// Model object, to request state from
	//
	protected MVCModel mModel = null;
	
	// Controller object, to send user input to
	//
	protected MVCController mController = null;
	
	// Display in imperial or metric units
	//
	protected Units mUnits = Units.METRIC;
	
	public MVCModel getModel() {
		return mModel;
	}
	public void setModel(MVCModel model) {
		this.mModel = model;
	}
	public MVCController getController() {
		return mController;
	}
	public void setController(MVCController controller) {
		this.mController = controller;
	}
	public Units getUnits() {
		return mUnits;
	}
	public void setUnits(Units units) {
		this.mUnits = units;
	}
	/*
	 * Getters and setters for the data items. These aren't
	 * used in the MVC pattern, they're here so the Activity
	 * can save and restore their values.
	 */
	public Integer getFocalLength()
	{
		Slider wFocalLength = (Slider)mActivity.findViewById(R.id.FocalLength);
		return new Integer( wFocalLength.getSliderValue() );
	}
	public void setFocalLength( int newFocalLength )
	{
		Slider wFocalLength = (Slider)mActivity.findViewById(R.id.FocalLength);
		wFocalLength.setSliderValue(newFocalLength);
	}
	public Integer getAperture()
	{
		Slider wAperture = (Slider)mActivity.findViewById(R.id.Aperture);
		return new Integer( wAperture.getSliderValue() );
	}
	public void setAperture( int newAperture )
	{
		Slider wAperture = (Slider)mActivity.findViewById(R.id.Aperture);
		wAperture.setSliderValue(newAperture);
	}
	
	/**
	 * Answers the distance value showing in the GUI, expressed as metres.
	 * <p>
	 * Even if the GUI is showing imperial units, this answers in metres.
	 * 
	 * @return
	 */
	public Integer getDistance()
	{
		Slider wDistance = (Slider)mActivity.findViewById(R.id.Distance);
		
		// Slider might be showing imperial distance. This must return
		// the value in metres.
		//
		int sliderValue = wDistance.getSliderValue();
		if ( mUnits == MVCView.Units.IMPERIAL )
			sliderValue = (int)Math.rint((double)sliderValue / FEET_PER_METRE);
		
		return new Integer( sliderValue );
	}
	
	/**
	 * Sets the distance to be shown in the GUI to the given number
	 * of metres.
	 * <p>
	 * Even if the GUI is showing imperial units, this method expects
	 * to be given the distance in feet. It will do the conversion
	 * itself if required.
	 * 
	 * @param newDistance
	 */
	public void setDistance( int newDistance )
	{
		Slider wDistance = (Slider)mActivity.findViewById(R.id.Distance);

		if ( mUnits == MVCView.Units.IMPERIAL )
			newDistance = (int)Math.rint((double)newDistance * FEET_PER_METRE);

		wDistance.setSliderValue(newDistance);
	}
	
	/**
	 * Constructor.
	 * <p>
	 * Pick up the layout from the XML-generated class and makes
	 * the Android activity available so the findViewBy...()
	 * methods can be used.
	 * 
	 * @param activity
	 */
	public MVCView( Activity activity )
	{
		this.mActivity    = activity;
	}

	/**
	 * Private class to link the seekbars to the user gesture
	 * emitting event. Trivial, but I chose to isolate it.
	 */
	private class SeekBarListener implements SeekBar.OnSeekBarChangeListener
	{
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

			// There's only 3 UI widgets so I don't try to work with what's
			// changed - I just do a complete update from all widgets
			//
			userInput();			
		}

		public void onStartTrackingTouch(SeekBar seekBar) {}
		public void onStopTrackingTouch(SeekBar seekBar) {}
	}
	
	/**
	 * Initialise the view widgets with values from
	 * storage, or somewhere sensible.
	 * <p>
	 * This updates the widgets with the values provided, then
	 * broadcasts the new information.
	 * <p>
	 * This is also responsible for connecting up the listeners
	 * for the GUI widgets.
	 * <p>
	 * Distance must be passed into this method as metres, regardless
	 * of the units the GUI might actually be using. Any necessary
	 * conversion will happen automatically.
	 *
	 * @param focalLength
	 * @param aperture
	 * @param subjectDistance
	 */
	public void initialiseView( int focalLength, int aperture, int subjectDistance )
	{
		Log.i("initialiseView", String.format("Focal length=%d, aperture=%d, distance=%d", focalLength, aperture, subjectDistance));
		
		SeekBarListener seekBarListener = new SeekBarListener();

		Slider         wFocalLength     = (Slider)mActivity.findViewById(R.id.FocalLength);
		ApertureSlider wAperture        = (ApertureSlider)mActivity.findViewById(R.id.Aperture);
		Slider         wSubjectDistance = (Slider)mActivity.findViewById(R.id.Distance);
		
		// All the sliders (seekbars) use the same listener object
		//
		wFocalLength.setOnSeekBarChangeListener(seekBarListener);
		wAperture.setOnSeekBarChangeListener(seekBarListener);
		wSubjectDistance.setOnSeekBarChangeListener(seekBarListener);

		// Prime the sliders with the values passed in
		//
		wFocalLength.setSliderValue(focalLength);
		wAperture.setSliderValue(aperture);
		
		// Value passed in comes as metric. Set the slider to the imperial
		// number if requested
		//
		if ( mUnits == Units.IMPERIAL )
			subjectDistance = (int)Math.rint((double)subjectDistance * FEET_PER_METRE);
		
		wSubjectDistance.setSliderValue(subjectDistance);
		
		// Synthesise user input so the new values get processed
		//
		userInput();
	}
	
	/**
	 * Broadcast point for user gestures. This receives input from all the widgets.
	 * <p>
	 * The on screen labels are updated to reflect the sliders' new values, then
	 * the slider values are sent to the controller.
	 */
	protected void userInput()
	{
		Context appContext = mActivity.getApplicationContext();
		
		// These don't need conversions - "mm" and "f/" are universal
		//
		Slider wFocalLength            = (Slider)mActivity.findViewById(R.id.FocalLength);
		int focalLength                = wFocalLength.getSliderValue();
		TextView wFocalLengthLabel     = (TextView)mActivity.findViewById(R.id.FocalLengthLabel);
		wFocalLengthLabel.setText( appContext.getString(R.string.focal_length) + String.format(" %dmm", focalLength) );		
		
		ApertureSlider wAperture       = (ApertureSlider)mActivity.findViewById(R.id.Aperture);
		int aperture                   = wAperture.getSliderValue();
		TextView wApertureLabel        = (TextView)mActivity.findViewById(R.id.ApertureLabel);
		wApertureLabel.setText( appContext.getString(R.string.aperture) + String.format(" f/%2.1f", aperture / 100.0) );		

		Slider wSubjectDistance        = (Slider)mActivity.findViewById(R.id.Distance);
		int subjectDistance            = wSubjectDistance.getSliderValue();
		TextView wSubjectDistanceLabel = (TextView)mActivity.findViewById(R.id.SubjectDistanceLabel);
		wSubjectDistanceLabel.setText( appContext.getString(R.string.subject_distance) +
									   String.format(" %d"+convertUnitsFormat(), subjectDistance) );

		// If the widget is showing feet, this needs to be converted to metres before
		// it's inserted into the model (which uses metric throughout). i.e. if the
		// slider provides "30" and it's showing feet, that needs to go into the calculation
		// engine as 10(ish) metres.
		//			
		if ( mUnits == Units.IMPERIAL )
			mController.userMadeInput( focalLength, aperture, ((double)subjectDistance / FEET_PER_METRE) );
		else
			mController.userMadeInput( focalLength, aperture, (double)subjectDistance );
	}
	
	/**
	 * Receive a notification that the model has changed.
	 * <p>
	 * This is called by the model code when the model wants
	 * to tell the view to update the output drawing. (The widgets
	 * don't get updated here, or in fact at all).
	 * <p>
	 * Receive a set of results, calculated by model code, and
	 * update the on screen result widgets to show those values.
	 * <p>
	 * This is normally called in response to an event which
	 * tells the view that the model has a new state.
	 */
	public void modelHasChanged()
	{
		// Don't try to update the view if the model data is still being
		// sorted out (as happens at initialisation time)
		//
		if ( mModel.isValidState() == false )
			return;
		
		Double nearLimit          = mModel.getNearLimit();
		Double farLimit           = mModel.getFarLimit();
		Double total              = mModel.getTotal();
		Double frontDistance      = mModel.getFrontDistance();
		Double behindDistance     = mModel.getBehindDistance();
		Double hyperfocalDistance = mModel.getHyperfocalDistance();
		Double circleOfConfusion  = mModel.getCircleOfConfusion();

		// Values come from the model as metres. Convert values to imperial if required
		//
		if ( mUnits == Units.IMPERIAL ) {
			nearLimit          = nearLimit          != null ? nearLimit * FEET_PER_METRE          : null;
			farLimit           = farLimit           != null ? farLimit * FEET_PER_METRE           : null;
			total              = total              != null ? total * FEET_PER_METRE              : null;
			frontDistance      = frontDistance      != null ? frontDistance * FEET_PER_METRE      : null;
			behindDistance     = behindDistance     != null ? behindDistance * FEET_PER_METRE     : null;
			hyperfocalDistance = hyperfocalDistance != null ? hyperfocalDistance * FEET_PER_METRE : null;
		}
		
		Context appContext = mActivity.getApplicationContext();
		
		/* TODO
		 * As of this version, all these text fields are not shown in the GUI.
		 * The plan is for a touch to replace the diagram with text fields, so
		 * for now I'm going to keep these functioning.
		 */		
		View textTable = (View)mActivity.findViewById(R.id.TextTable);
		if ( textTable.isShown() ) {
			// Near limit
			//
			TextView wNearLimit = (TextView)mActivity.findViewById(R.id.NearLimitValue);
			try {
				wNearLimit.setText( String.format("%.2f"+convertUnitsFormat(), nearLimit ) );
			} catch (Exception e) {
				wNearLimit.setText( appContext.getString(R.string.not_a_number) );
			}
	
			// Far limit
			//
			TextView wFarLimit = (TextView)mActivity.findViewById(R.id.FarLimitValue);
			try {
				if ( farLimit == null )
					wFarLimit.setText( appContext.getString(R.string.infinite) );
				else
					wFarLimit.setText( String.format("%.2f"+convertUnitsFormat(), farLimit ) );
			} catch (Exception e) {
				wFarLimit.setText( appContext.getString(R.string.not_a_number) );
			}
	
			// Total DoF
			//
			TextView wTotal = (TextView)mActivity.findViewById(R.id.TotalValue);
			try {
				if ( total == null )
					wTotal.setText( appContext.getString(R.string.infinite) );
				else
					wTotal.setText( String.format("%.2f"+convertUnitsFormat(), total ) );
			} catch (Exception e) {
				wTotal.setText( appContext.getString(R.string.not_a_number) );
			}
	
			// Front distance
			//
			TextView wInFront = (TextView)mActivity.findViewById(R.id.InFrontValue);
			try {
				wInFront.setText( String.format("%.2f"+convertUnitsFormat(), frontDistance ) );
			} catch (Exception e) {
				wInFront.setText( appContext.getString(R.string.not_a_number) );
			}
	
			// Behind distance
			//
			TextView wBehindSubject = (TextView)mActivity.findViewById(R.id.BehindSubjectValue);
			try {
				if ( behindDistance == null )
					wTotal.setText( appContext.getString(R.string.infinite) );
				else
					wBehindSubject.setText( String.format("%.2f"+convertUnitsFormat(), behindDistance ) );
			} catch (Exception e) {
				wBehindSubject.setText( appContext.getString(R.string.not_a_number) );
			}
	
			TextView wHyperfocal = (TextView)mActivity.findViewById(R.id.HyperfocalDistanceValue);
			try {
				// HfD arrives in metres
				//
				wHyperfocal.setText( String.format("%.2f"+convertUnitsFormat(), hyperfocalDistance ) );
			} catch (Exception e) {
				wHyperfocal.setText( appContext.getString(R.string.not_a_number) );
			}
	
			TextView wCircleOfConfusion = (TextView)mActivity.findViewById(R.id.CircleOfConfusionValue);
			try {
				// CoC arrives in metres - convert to mm
				//
				wCircleOfConfusion.setText( String.format("%04.3fmm", circleOfConfusion * 1000.0) );
			} catch (Exception e) {
				wCircleOfConfusion.setText( appContext.getString(R.string.not_a_number) );
			}
		}
		
		TextView wTitle = (TextView)mActivity.findViewById(R.id.title);
		wTitle.setText( mModel.getBody().getName() );
			
		DrawingSurface drawingSurface = (DrawingSurface)mActivity.findViewById(R.id.DrawingSurface);
		drawingSurface.setValues( nearLimit,
								  farLimit,
								  total,
								  frontDistance,
								  behindDistance,
								  hyperfocalDistance,
								  mUnits );
		drawingSurface.invalidate();
	}
	
	/**
	 * Change displayed units, imperial or metric.
	 * 
	 * @param newUnits
	 */
	public void changeUnits( Units newUnits )
	{
		// If the user didn't actually change the units, don't bother
		// doing anything
		//
		if ( this.mUnits == newUnits )
			return;
		
		// The changeRange() call below resizes the distance slider.
		// (e.g. if it's showing 5-10 (metres), it will be resized to
		// 15-30 (feet)). When the underlying seekbar widget changes
		// its min and max values it does something with the position
		// of the seekbar - i.e. the widget value itself. I'm not
		// quite sure what it's doing, but I need to keep control of
		// that value myself.
		// So, I read the current value of the widget in the current
		// units, calculate that value as per the new units, change
		// the widget range, then force the widget to show the old
		// value in the new units.
		//
		Slider wSubjectDistance = (Slider)mActivity.findViewById(R.id.Distance);
		int subjectDistance     = wSubjectDistance.getSliderValue();

		if ( this.mUnits == Units.METRIC )
			subjectDistance = (int)(Math.rint((double)subjectDistance * FEET_PER_METRE));
		else
			subjectDistance = (int)(Math.rint((double)subjectDistance / FEET_PER_METRE));
		
		this.mUnits = newUnits;

		// Synthesize a range change so the range limits on the slider
		// widget gets redrawn in the new units. changeRange() expects
		// metric, so taking the value from the model is fine.
		//
		changeRange( mModel.getRange() ) ;
		
		// Now force the value of the slider to be the original value
		// expressed in the new units.
		//
		wSubjectDistance.setSliderValue(subjectDistance);
		
		// Synthesize user input to trigger recalculations and redrawing
		// of slider values and the graphic
		//
		userInput();
	}
	
	/**
	 * Broadcast point for body change event
	 */
	public void changeBody( Body body )
	{
		mController.userChangedBody( body );
	}
	
	/**
	 * Broadcast point for lens change event.
	 * <p>
	 * The view is updated with the new lens information before
	 * the broadcast so when the computed results are dropped into
	 * place, the UI is ready to interpret them correctly.
	 */
	public void changeLens( Lens newLens )
	{
		// Update the labels in the widgets to match lens
		//
		TextView wMinFocalLengthLabel = (TextView)mActivity.findViewById(R.id.MinFocalLengthLabel);
		wMinFocalLengthLabel.setText( String.format("%dmm", newLens.getMinLength()) );
		
		TextView wMaxFocalLengthLabel = (TextView)mActivity.findViewById(R.id.MaxFocalLengthLabel);
		wMaxFocalLengthLabel.setText( String.format("%dmm", newLens.getMaxLength()) );

		TextView wMinAperture = (TextView)mActivity.findViewById(R.id.MinApertureLabel);
		wMinAperture.setText( String.format("f/%2.1f", newLens.getMinAperture() / 100.0) );

		TextView wMaxAperture = (TextView)mActivity.findViewById(R.id.MaxApertureLabel);
		wMaxAperture.setText( String.format("f/%2.1f", newLens.getMaxAperture() / 100.0) );
		
		// Update the focal length slider so it represents the
		// lens range correctly.
		//
		Slider wFocalLength = (Slider)mActivity.findViewById(R.id.FocalLength);
		wFocalLength.setRangeMinMax( newLens.getMinLength(), newLens.getMaxLength() );

		// Update the aperture slider so it understands the range it's representing
		//
		ApertureSlider wAperture = (ApertureSlider)mActivity.findViewById(R.id.Aperture);
		wAperture.setRangeMinMax( newLens.getMinAperture(), newLens.getMaxAperture() );
		wAperture.setStops( newLens.getStopRanges() );
		
		// UI is ready - tell the controller the new details
		//
		mController.userChangedLens( newLens );
	}
	
	/**
	 * Broadcast point for range change event.
	 * <p>
	 * The user has selected a new range from the GUI, so this view needs
	 * to send the details of it to the controller.
	 */
	public void changeRange( Range newRange )
	{
		// Range comes in with distances in metres - convert to feet if necessary
		//
		int minDistance = newRange.getMinDistance();
		int maxDistance = newRange.getMaxDistance();
		if ( mUnits == Units.IMPERIAL ) {
			minDistance = (int)Math.rint((double)minDistance * FEET_PER_METRE);
			maxDistance = (int)Math.rint((double)maxDistance * FEET_PER_METRE);
		}

		// Update the labels in the widgets to match the new range
		//
		TextView wMinDistance = (TextView)mActivity.findViewById(R.id.MinDistanceLabel);
		wMinDistance.setText( String.format("%d"+convertUnitsFormat(), minDistance) );

		TextView wMaxDistance = (TextView)mActivity.findViewById(R.id.MaxDistanceLabel);
		wMaxDistance.setText( String.format("%d"+convertUnitsFormat(), maxDistance) );
		
		// Update the Slider so it understands the range it's representing
		//
		Slider wDistance = (Slider)mActivity.findViewById(R.id.Distance);
		wDistance.setRangeMinMax( minDistance, maxDistance );

		// UI is ready - tell the controller the new details
		//
		mController.userChangedRange( newRange );
	}

	/**
	 * Return a string to be used in the formatting of a distance, as
	 * set by the user's options.
	 * <p>
	 * For example, in English this will return either "m" or "ft".
	 * 
	 * @return Suitable abbreviation for metres or feet, from resources file
	 */
	protected String convertUnitsFormat()
	{
		Context appContext = mActivity.getApplicationContext();
		Resources res = appContext.getResources();
		
		if ( mUnits == Units.METRIC )
			return res.getString(R.string.metres_abb);
		else
			return res.getString(R.string.feet_abb);
	}
}
