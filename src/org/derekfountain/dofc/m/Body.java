package org.derekfountain.dofc.m;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;

/**
 * Body class, defines a camera body.
 * 
 */
public class Body {
	
	/**
	 * Static cache for the contents of the XML file
	 */
	protected static ArrayList<Body> mBodyCache   = null;
	
	/**
	 * Name of body to use by default
	 */
	protected static String          mDefaultName = null;

	
	protected String  mName       = null;
	
	/**
	 * coc is stored in mm. Suitable values are found here:
	 * http://en.wikipedia.org/wiki/Circle_of_confusion#Circle_of_confusion_diameter_limit_based_on_d.2F1500
	 */
	protected double  mCircleOfConfusion = 0;

	public String getName() {
		return mName;
	}
	public void setName(String name) {
		this.mName = name;
	}	
	public double getCircleOfConfusion() {
		return mCircleOfConfusion;
	}
	public void setCircleOfConfusion(double circleOfConfusion) {
		this.mCircleOfConfusion = circleOfConfusion;
	}
	
	public Body( String name, double circleOfConfusion )
	{
		this.mName = name;
		this.mCircleOfConfusion = circleOfConfusion;	
	}
	
	/**
	 * Locate a named camera body in the distribution database
	 * and return an object representing that body.
	 * 
	 * @param context A context the application resources (specifically the
	 *                XML file) can be found from
	 * @param name
	 * @return A Body object, or null if not found
	 */
	public static Body findBody( Context context, String name )
	{
		for ( Body body : listBodies(context) ) {
			if ( body.getName().equals(name) )
				return body;
		}
		
		return null;
	}
	
	/**
	 * Answers a collection of Body objects representing all the
	 * bodies known to the system
	 * <p>
	 * The bodies come from the bodies.xml resource file.
	 * <p>
	 * A side effect of this method is the setting of the Body
	 * to use by default (at application startup for instance).
	 * 
	 * @param context A context the application resources can found from
	 * @return A collection of Body objects
	 */
	public static ArrayList<Body> listBodies( Context context )
	{
		if ( mBodyCache == null ) {
			mBodyCache = new ArrayList<Body>();
			
			XmlResourceParser parser = context.getResources().getXml(org.derekfountain.dofc.R.xml.bodies);
	
			try {
				
				String nameStore          = null;
				boolean nextNameIsDefault = false;
				
				int eventType = parser.getEventType();
				while ( eventType != XmlPullParser.END_DOCUMENT ) {
					if ( eventType == XmlPullParser.START_TAG ) {
						
						String tagName = parser.getName();

						// If this tag marks the start of a body with include="false", skip it
						//
						if ( tagName.equals("body") && !parser.getAttributeBooleanValue(null, "include", true) ) {

							// Loop looking for the end tag which matches the start of this body
							//
							while ( true ) {
								while ( eventType != XmlPullParser.END_TAG ) {
									eventType = parser.next();
								}
								if ( parser.getName().equals("body") ) {
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

						if ( tagName.equals("name")  ) {
							nameStore = parser.nextText();
							if ( nextNameIsDefault ) {
								mDefaultName = nameStore;
								nextNameIsDefault = false;
							}
						}
						else if ( tagName.equals("circleofconfusion") ) {
							Body body = new Body( nameStore, Double.parseDouble(parser.nextText()) );
							mBodyCache.add(body);
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
			throw new IllegalArgumentException("No default body in XML file");
		
		return mBodyCache;
	}
	
	/**
	 * Answers the Body object set as the default one in the
	 * bodies XML source file.
	 * 
	 * @param context Context used to find the XML file
	 * @return
	 */
	public static Body findDefaultBody( Context context )
	{
		if ( mDefaultName == null )
			listBodies( context );

		return findBody( context, mDefaultName );
	}
}
