package org.derekfountain.dofc.v;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.derekfountain.dofc.m.StopRange;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Widget to present a slider which jumps between a set of
 * pre-set values, in this case them being the standard
 * aperture settings.
 */
public class ApertureSlider extends Slider {

	/**
	 * Array of aperture values the slider will currently move between.
	 */
	protected int[] validValues = null;
		
	// Tables of supported values - 400 is f/4.0, etc. There needs to be one entry in
	// the table of exact values in the model code for each entry here.
	//
	protected static final int[] fullStopValues    = new int[] { 100,
													  		     140,
															     200,
															     280,
															     400,
															     560,
															     800,
															     1100,
															     1600,
															     2200,
															     3200,
															     4500,
															     6400 };
	
	protected static final int[] quarterStopValues = new int[] { 260,
																 340,
																 370,
																 440,
																 520,
																 620,
																 730,
																 870,
																 1200,
																 1500,
																 1700,
																 2100 };

	protected static final int[] thirdStopValues   = new int[] { 110,
		                                                         120,
		                                                         160,
		                                                         180,
		                                                         220,
		                                                         250,
		                                                         320,
		                                                         350,
		                                                         450,
		                                                         500,
		                                                         630,
		                                                         710,
		                                                         900,
		                                                         1000,
		                                                         1300,
		                                                         1400,
		                                                         1800,
		                                                         2000,
		                                                         2500,
		                                                         2800 };
	
	protected static final int[] halfStopValues    = new int[] { 170,
		                                                         240,
		                                                         330,
		                                                         480,
		                                                         670,
		                                                         950,
		                                                         1900 };
		
	
	/**
	 * Constructor, by default sets the valid values to the range
	 * of full stops.
	 * 
	 * @param context
	 * @param attrs
	 */
	public ApertureSlider(Context context, AttributeSet attrs) {
		super(context, attrs);
		validValues = fullStopValues;
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
	
	/**
	 * Change the slider so it only responds to stops in the stop ranges given.
	 * <p>
	 * For example, if this is given [FULL, THIRD] the slider will be set to
	 * move between all the aperture values found on lenses which have full
	 * and one-third stops in their range.  
	 * 
	 * @param stopRanges A collection of StopRange objects.
	 */
	protected void setStops( Collection<StopRange> stopRanges )
	{
		// I have to copy the values for one for more of the static
		// integer arrays into the output integer array, keeping the
		// final array in order. There are several ways to do this,
		// but no one looked any simplier than the others. So I opted
		// to build a sorted set of all the values I require, then
		// copy it out out into a new array. I have this nagging
		// feeling I've missed the obvious and efficient way of
		// doing this. :o}
		//
		SortedSet<Integer> validStops = new TreeSet<Integer>();
		
		for ( StopRange stopRange : stopRanges ) {
			
			int[] arrayToAdd = null;
			switch ( stopRange ) {
				case FULL:
					arrayToAdd = fullStopValues;
					break;
				case QUARTER:
					arrayToAdd = quarterStopValues;
					break;
				case THIRD:
					arrayToAdd = thirdStopValues;
					break;
				case HALF:
					arrayToAdd = halfStopValues;
					break;
			}

			// Copy all the values in the array specified as required
			// into the output set
			//
			for( int i=0; i<arrayToAdd.length; i++)
				validStops.add(arrayToAdd[i]);
		}

		// Now copy the contents of the output set into the final array
		//
		int i = 0;
		validValues = new int[validStops.size()];
		for ( Integer stop : validStops )
			validValues[i++] = stop.intValue();
	}
}
