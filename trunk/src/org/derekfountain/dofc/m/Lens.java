package org.derekfountain.dofc.m;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;

/**
 * Lens class, defines a user's lens.
 * 
 */
public class Lens {
	/**
	 * Static cache for the contents of the XML file
	 */
	protected static ArrayList<Lens> mLensCache = null;

	/**
	 * Name of lens to use by default
	 */
	protected static String          mDefaultName = null;

	protected String mName; 
	protected int    mMinLength;
	protected int    mMaxLength;
	protected int    mStartingLength;
	protected int    mMinAperture;
	protected int    mMaxAperture;
	protected int    mStartingAperture;
	
	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}
	public int getMinLength() {
		return mMinLength;
	}
	public void setMinLength(int minLength) {
		this.mMinLength = minLength;
	}
	public int getMaxLength() {
		return mMaxLength;
	}
	public void setMaxLength(int maxLength) {
		this.mMaxLength = maxLength;
	}
	public int getStartingLength() {
		return mStartingLength;
	}
	public void setStartingLength(int startingLength) {
		this.mStartingLength = startingLength;
	}
	public int getMinAperture() {
		return mMinAperture;
	}
	public void setMinAperture(int minAperture) {
		this.mMinAperture = minAperture;
	}
	public int getMaxAperture() {
		return mMaxAperture;
	}
	public void setMaxAperture(int maxAperture) {
		this.mMaxAperture = maxAperture;
	}	
	public int getStartingAperture() {
		return mStartingAperture;
	}
	public void setStartingAperture(int startingAperture) {
		this.mStartingAperture = startingAperture;
	}
	
	public Lens(String name,
			    int minLength,   int maxLength,   int startingLength,
			    int minAperture, int maxAperture, int startingAperture ) {
		super();
		this.mName             = name;
		this.mMinLength        = minLength;
		this.mMaxLength        = maxLength;
		this.mStartingLength   = startingLength;
		this.mMinAperture      = minAperture;
		this.mMaxAperture      = maxAperture;
		this.mStartingAperture = startingAperture;
	}
	
	/**
	 * Locate a named lens in the distribution database and return an
	 * object representing that lens.
	 * 
	 * @param context A context the application resources (specifically the
	 *                XML file) can be found from
	 * @param name
	 * @return A Lens object, or null if not found
	 */
	public static Lens findLens( Context context, String name )
	{
		for ( Lens lens : listLenses(context) ) {
			if ( lens.getName().equals(name) )
				return lens;
		}
		
		return null;
	}

	/**
	 * Answers a collection of Lens objects representing all the
	 * lenses known to the system
	 * <p>
	 * The lenses come from the lenses.xml resource file.
	 * <p>
	 * A side effect of this method is the setting of the Lens
	 * to use by default (at application startup for instance).
	 * 
	 * @param context A context the application resources can found from
	 * @return A collection of Lens objects
	 */
	public static ArrayList<Lens> listLenses( Context context )
	{
		if ( mLensCache == null ) {
			mLensCache = new ArrayList<Lens>();
			
			XmlResourceParser parser = context.getResources().getXml(org.derekfountain.dofc.R.xml.lenses);
	
			try {
				
				String nameStore           = null;
				String minLengthStore      = null;
				String maxLengthStore      = null;
				String startingLengthStore = null;
				String minApertureStore    = null;
				String maxApertureStore    = null;
				
				boolean nextNameIsDefault  = false;

				
				int eventType = parser.getEventType();
				while ( eventType != XmlPullParser.END_DOCUMENT ) {
					if ( eventType == XmlPullParser.START_TAG ) {
						String tagName = parser.getName();

						// If this tag marks the start of a lens with include="false", skip it
						//
						if ( tagName.equals("lens") && !parser.getAttributeBooleanValue(null, "include", true) ) {

							// Loop looking for the end tag which matches the start of this lens
							//
							while ( true ) {
								while ( eventType != XmlPullParser.END_TAG ) {
									eventType = parser.next();
								}
								if ( parser.getName().equals("lens") ) {
									break;
								}
								else {
									eventType = parser.next();									
								}
							}
							continue;
						}
						
						if ( parser.getAttributeBooleanValue(null, "default", false) ) {
							nextNameIsDefault = true;
						}

						if ( tagName.equals("name") ) {
							nameStore = parser.nextText();
							if ( nextNameIsDefault ) {
								mDefaultName = nameStore;
								nextNameIsDefault = false;
							}
						}
						else if ( tagName.equals("minlength") ) {
							minLengthStore = parser.nextText();
						}
						else if ( tagName.equals("maxlength") ) {
							maxLengthStore = parser.nextText();
						}
						else if ( tagName.equals("startinglength") ) {
							startingLengthStore = parser.nextText();
						}
						else if ( tagName.equals("minaperture") ) {
							minApertureStore = parser.nextText();
						}
						else if ( tagName.equals("maxaperture") ) {
							maxApertureStore = parser.nextText();
						}
						else if ( tagName.equals("startingaperture") ) {
							Lens lens = new Lens( nameStore, Integer.parseInt(minLengthStore),
													         Integer.parseInt(maxLengthStore),
													         Integer.parseInt(startingLengthStore),
													         Integer.parseInt(minApertureStore),
													         Integer.parseInt(maxApertureStore),
													         Integer.parseInt( parser.nextText() ) );
							mLensCache.add(lens);
						}
					}
					
					eventType = parser.next();
				}
				
			}
			catch (XmlPullParserException e) {}
			catch (IOException e) {}
	
			parser.close();
		}

		if ( mDefaultName == null )
			throw new IllegalArgumentException("No default lens in XML file");
		
		return mLensCache;
	}

	/**
	 * Answers the Lens object set as the default one in the
	 * lenses XML source file.
	 * 
	 * @param context Context used to find the XML file
	 * @return
	 */
	public static Lens findDefaultLens( Context context )
	{
		if ( mDefaultName == null )
			listLenses( context );

		return findLens( context, mDefaultName );
	}

}
