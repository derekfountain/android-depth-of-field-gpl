package org.derekfountain.dofc.v;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Widget to present a slider which jumps between a set of
 * pre-set values, in this case them being the standard
 * aperture settings.
 */
public class ApertureSlider extends Slider {

	// Supported values - 400 is f/4.0, etc.
	//
	protected static int[] validValues = new int[] {100, 120, 140, 160, 180,
                                             200, 220, 240, 250, 280,
                                             320, 340, 360, 400, 450,
                                             480, 500, 560, 640, 670,
                                             710, 800, 900, 950, 1000,
                                             1100, 1270, 1350, 1430, 1600,
                                             1800, 1900, 2000, 2200, 2500,
                                             2800, 3200, 4500, 6400};
	
	public ApertureSlider(Context context, AttributeSet attrs) {
		super(context, attrs);
		setProgress(0);
	}
	
	/**
	 * Sets the low end of the range of the aperture slider.
	 * <p>
	 * Value will be forced to the lowest value supported by the
	 * widget if the value given is lower than that.
	 * 
	 * @param rangeMin
	 */
	public void setRangeMin(Integer rangeMin) {
		this.rangeMin = Math.max( rangeMin, validValues[0] );
		super.setMax( rangeMax-rangeMin );
	}
	
	/**
	 * Sets the high end of the range of the aperture slider.
	 * <p>
	 * Value will be forced to the highest value supported by the
	 * widget if the value given is higher than that.
	 * 
	 * @param rangeMax
	 */
	public void setRangeMax(Integer rangeMax) {
		this.rangeMin = Math.min( rangeMax, validValues[validValues.length-1] );
		this.rangeMax = rangeMax;
		super.setMax( rangeMax-rangeMin );
	}
	
	/**
	 * Sets the widget slider value to the closest support value as
	 * defined in the widget code.
	 * 
	 * @param newValue
	 */
	@Override
	public void setSliderValue( int newValue )
	{
		int progress = findClosestValidValue( newValue );
		super.setSliderValue( progress );
	}

	/**
	 * Answers the widget slider value as one of the supported values as
	 * defined in the widget code.
	 */
	@Override
	public int getSliderValue()
	{
		int sliderProgress = super.getSliderValue();
		return findClosestValidValue( sliderProgress );
	}
	
	/**
	 * Answers the closest value supported by the aperture widget
	 * to the value given.
	 * 
	 * @param input
	 * @return
	 */
	protected int findClosestValidValue( int input )
	{
		if ( input <= validValues[0] )
			return validValues[0];
		
		if ( input >= validValues[validValues.length-1] )
			return validValues[validValues.length-1];
		
		for ( int i = 0; i < validValues.length-1; i++ ) {
			
			if ( input >= validValues[i] && input <= validValues[i+1]) {
				if ( (input-validValues[i]) < (validValues[i+1]-input) )
					return validValues[i];
				else
					return validValues[i+1];
			}
			
		}
		
		// Keep compiler quiet
		//
		return validValues[0];
	}
}
