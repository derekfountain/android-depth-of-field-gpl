package org.derekfountain.dofc.v;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * This is a nasty workaround for a bug in Android. The issue is
 * dealt with here:
 * <p>
 *  http://stackoverflow.com/questions/6534643/android-spinner-on-dialog-crashes-if-the-spinner-is-open-list-up-and-you-rotate
 *  <p>
 * Basically, if you bring up the new tab dialog, then drop one of
 * the list spinners (body, lens or range) then rotate the screen,
 * the application crashes.
 * <p>
 * There's no real workaround, other than to reimplement the dialog
 * mechanism properly. The underlying dialog that's used is private
 * to the spinner, so can't be accessesd.
 * <p>
 * But I did find this nasty hack when poring through the source: the
 * Spinner onDetachedFromWindow() method happens to do exactly what
 * is required: if the dialog is popped up, dismiss it and close things
 * down neatly. I'm not sure when onDetachedFromWindow() is supposed
 * to be called, but it's obviously not called when I need it in this
 * instance. So this class extends the normal spinner and adds a public
 * method which just calls the protected onDetachFromWindow(). It works!
 * <p>
 * Of course, if the implementation of onDetachFromWindow() changes this
 * will stop working. Not much I can do about that until someone implements
 * the spinner dialog properly. 
 */
public class RotatingSpinner extends Spinner {

	public RotatingSpinner(Context context) {
		super(context);
	}

	public RotatingSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public RotatingSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Dismisses the popped up list dialog the spinner uses to display
	 * its options.
	 * <p>
	 * Abuses the super class's onDetachedFromWindow. This is not
	 * guaranteed to remain unchanged, so this should be considered
	 * a temporary solution.  
	 */
	public void dismissPoppedUpList()
	{
		onDetachedFromWindow();
	}

}
