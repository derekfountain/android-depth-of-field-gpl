package org.derekfountain.dofc.v;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Widget to present a value Slider, ranging from a specified
 * minimum value to a specified maximum value.
 */
public class Slider extends SeekBar {

	// Default values
	//
	protected Integer rangeMin = new Integer(0);
	protected Integer rangeMax = new Integer(100);
	
	/**
	 * Answers the minimum value this slider can inclusively represent.
	 * 
	 * @return
	 */
	public Integer getRangeMin() {
		return rangeMin;
	}
	
	/**
	 * Answers the maximum value this slider can inclusively represent.
	 * 
	 * @return
	 */
	public Integer getRangeMax() {
		return rangeMax;
	}
	
	/**
	 * Sets the minimum and maximum range values this slider
	 * can inclusively represent.
	 * 
	 * @param rangeMin
	 * @param rangeMax
	 */
	public void setRangeMinMax(Integer rangeMin, Integer rangeMax) {
		this.rangeMin = new Integer(rangeMin);
		this.rangeMax = new Integer(rangeMax);
		super.setMax( rangeMax-rangeMin );
	}
	

	public Slider(Context context) {
		super(context);
	}
	public Slider(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	public Slider(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * Answers the current slider value, somewhere between the minimum
	 * and maximum values, inclusive.
	 * 
	 * @return
	 */
	public int getSliderValue()
	{
		// Slider holds a value in the valid range, say 17 to 40.
		// The seekbar will hold a value from 0 to 23. So if the
		// seekbar returns me 0, I need to give the user 17.
		// So I need to take the seekbar's value and add the lower
		// range to it.
		//
		return rangeMin + super.getProgress();
	}
	
	/**
	 * Sets the current slider value, somewhere between the minimum
	 * and maximum values, inclusive.
	 * 
	 * @throws IllegalArgumentException
	 */
	public void setSliderValue( int newValue ) throws IllegalArgumentException
	{
		// Slider holds a value in the valid range, say 17 to 40.
		// If the user wants to set the slider to 17 - that's the
		// lowest value - I need to actually set the seekbar to 0. I need
		// to take the value given (17) and subtract the minimum
		// range (17) leaving the correct position for the seekbar
		// (0).
		//
		
		// This protection is needed at inflate time, before the range
		// is set sensibly
		//
		if ( rangeMin == null )
			return;

		if ( newValue < rangeMin || newValue > rangeMax )
			throw new IllegalArgumentException("Value out of range: "+newValue+" range is "+rangeMin+","+rangeMax);
		
		int adjustedValue = newValue - rangeMin;
		super.setProgress( adjustedValue );
	}
	
	/**
	 * According to the Android docs I should just be able to override the
	 * progress setter and getter with my revised versions which handle the
	 * adjusted values. Only, that doesn't work. Although none of my
	 * application code calls those methods, some things in the Android
	 * system seem to know there's a Seekbar under here and will try to
	 * do things with it, things which get confused with the values I want
	 * to provide.
	 * 
	 * So I switched to a couple of new methods for what I need, dealing with
	 * the "application" progress. That works because that's what my app uses
	 * but other things still seem to insist on calling setProgress()
	 * directly. When this happens it messes up the appearance of the Seekbar
	 * widget. In particular this seems to happen during inflation, which
	 * means when the user rotates the phone, and the app restarts, the
	 * sliders are presented incorrectly.
	 * 
	 * I tried lots of ways of working around this, but couldn't find any
	 * sensible looking way to keeping the whole system happy. So in the end
	 * I just stubbed out the normal setProgress() method; whatever calls it
	 * for my Slider is happy, and since it's not actually used by my app
	 * (which always uses "application progress") no harm is done.
	 */
	@Override
	public void setProgress (int newValue)
	{
	}
}
