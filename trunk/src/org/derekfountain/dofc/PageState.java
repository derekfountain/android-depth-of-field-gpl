package org.derekfountain.dofc;

import org.derekfountain.dofc.m.Body;
import org.derekfountain.dofc.m.Lens;
import org.derekfountain.dofc.m.Range;

import android.content.Context;
import android.net.Uri;

/**
 * Holds the state of a single page as a set of primitive values.
 * <p>
 * This is used to store a page's state - what body it's using,
 * the lens, the range, the values of the sliders, etc. - in a
 * text-based way which can be stored when the application is
 * paused or destroyed, and restored from when it comes back.
 * <p>
 * The content of this object can be passed into an intent as
 * a URI.
 * 
 */
public class PageState {
	protected Integer focalLength;
	protected Integer aperture;
	protected Integer distance;
	protected String  bodyName;
	protected String  lensName;
	protected String  rangeName;
	
	public Integer getFocalLength() {
		return focalLength;
	}
	public void setFocalLength(Integer focalLength) {
		this.focalLength = focalLength;
	}
	public Integer getAperture() {
		return aperture;
	}
	public void setAperture(Integer aperture) {
		this.aperture = aperture;
	}
	public Integer getDistance() {
		return distance;
	}
	public void setDistance(Integer distance) {
		this.distance = distance;
	}
	public String getBodyName() {
		return bodyName;
	}
	public void setBodyName(String bodyName) {
		this.bodyName = bodyName;
	}
	public String getLensName() {
		return lensName;
	}
	public void setLensName(String lensName) {
		this.lensName = lensName;
	}
	public String getRangeName() {
		return rangeName;
	}
	public void setRangeName(String rangeName) {
		this.rangeName = rangeName;
	}
	
	/**
	 * Default constructor provides (unusable) object with
	 * all null fields. 
	 */
	public PageState()
	{
	}

	/**
	 * Answers a new PageState with values set from the given URI
	 * 
	 * @param uri
	 */
	public PageState( Uri uri )
	{
		setFromUri( uri );
	}
	
	/**
	 * Sets a bunch of default values into the page state.
	 * 
	 * @param context Context object used to find resources
	 * @return
	 */
	public PageState setDefaults( Context context )
	{
		setBodyName( Body.findDefaultBody(context).getName() );
		setLensName( Lens.findDefaultLens(context).getName() );
		setRangeName(Range.findDefaultRange(context).getName() );
		setFocalLength( Lens.findDefaultLens(context).getStartingLength() );
		setAperture( Lens.findDefaultLens(context).getStartingAperture() );
		setDistance( Range.findDefaultRange(context).getStartingDistance() );

		return this;
	}
	
	/**
	 * Build a URI from the contents of the page state.
	 * <p>
	 * The result of this call can be attached to an intent in order
	 * to make a new Page object appear.
	 * 
	 * @return A Uri
	 */
	public Uri getUri()
	{
		// Build a Uri which describes the page - body, lens, values, etc.
		//
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.path("dofc:"+
	       		        bodyName+"|"+
	       		        lensName+"|"+
	       		        rangeName+"?");
		uriBuilder.appendQueryParameter("focallength", focalLength.toString());
		uriBuilder.appendQueryParameter("aperture",    aperture.toString());
        uriBuilder.appendQueryParameter("distance",    distance.toString());

        return uriBuilder.build();
	}

	/**
	 * Sets the members of the PageState object to the values found
	 * in the given URI
	 * 
	 * @param inputUri A PageState URI, as created by getUri()
	 * @return The object, updated
	 */
	public PageState setFromUri( Uri inputUri )
	{
		setFocalLength( new Integer( Integer.parseInt(inputUri.getQueryParameter("focallength")) ) );
		setAperture(    new Integer( Integer.parseInt(inputUri.getQueryParameter("aperture")) ) );
    	setDistance(    new Integer( Integer.parseInt(inputUri.getQueryParameter("distance")) ) );

    	String uriBody = inputUri.getPath();
    	uriBody = uriBody.substring(5, uriBody.lastIndexOf('?'));   // dofc: removed from start
    	String[] tabInfo = uriBody.split("\\|");

    	setBodyName(  tabInfo[0] );
    	setLensName(  tabInfo[1] );
    	setRangeName( tabInfo[2] );        		
		
		return this;
	}
}