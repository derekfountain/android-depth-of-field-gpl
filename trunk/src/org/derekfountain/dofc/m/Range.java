package org.derekfountain.dofc.m;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;

/**
 * Class to define a distance range.
 */
public class Range {
	/**
	 * Static cache for the contents of the XML file
	 */
	protected static ArrayList<Range> mRangeCache = null;

	/**
	 * Name of range to use by default
	 */
	protected static String          mDefaultName = null;
	
	protected String name;
	
	protected int    minDistance;
	protected int    maxDistance;
	protected int    startingDistance;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getMinDistance() {
		return minDistance;
	}
	public void setMinDistance(int minDistance) {
		this.minDistance = minDistance;
	}
	public int getMaxDistance() {
		return maxDistance;
	}
	public void setMaxDistance(int maxDistance) {
		this.maxDistance = maxDistance;
	}
	public int getStartingDistance() {
		return startingDistance;
	}
	public void setStartingDistance(int startingDistance) {
		this.startingDistance = startingDistance;
	}
	
	/**
	 * Constructor for range.
	 * <p>
	 * Distances are in metres.
	 * 
	 * @param name
	 * @param minDistance
	 * @param maxDistance
	 * @param startingDistance
	 */
	public Range(String name, int minDistance, int maxDistance, int startingDistance) {
		super();
		this.name             = name;
		this.minDistance      = minDistance;
		this.maxDistance      = maxDistance;
		this.startingDistance = startingDistance;
	}

	/**
	 * Locate a named range in the distribution database and return an
	 * object representing that range.
	 * 
	 * @param context A context the application resources (specifically the
	 *                XML file) can be found from
	 * @param name
	 * @return A Range object, or null if not found
	 */
	public static Range findRange( Context context, String name )
	{
		for ( Range range : listRanges(context) ) {
			if ( range.getName().equals(name) )
				return range;
		}
		
		return null;
	}

	/**
	 * Answers a collection of Range objects representing all the
	 * ranges known to the system
	 * <p>
	 * The ranges come from the ranges.xml resource file.
	 * <p>
	 * A side effect of this method is the setting of the Range
	 * to use by default (at application startup for instance).
	 * 
	 * @param context A context the application resources can found from
	 * @return A collection of Range objects
	 */
	public static ArrayList<Range> listRanges( Context context )
	{
		if ( mRangeCache == null ) {
			mRangeCache = new ArrayList<Range>();
	
			XmlResourceParser parser = context.getResources().getXml(org.derekfountain.dofc.R.xml.ranges);
	
			try {
				
				String nameStore          = null;
				String minDistanceStore   = null;
				String maxDistanceStore   = null;
				
				boolean nextNameIsDefault = false;

				
				int eventType = parser.getEventType();
				while ( eventType != XmlPullParser.END_DOCUMENT ) {
					if ( eventType == XmlPullParser.START_TAG ) {
						
						String tagName = parser.getName();

						// If this tag marks the start of a range with include="false", skip it
						//
						if ( tagName.equals("range") && !parser.getAttributeBooleanValue(null, "include", true) ) {

							// Loop looking for the end tag which matches the start of this range
							//
							while ( true ) {
								while ( eventType != XmlPullParser.END_TAG ) {
									eventType = parser.next();
								}
								if ( parser.getName().equals("range") ) {
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
						else if ( tagName.equals("mindistance") ) {
							minDistanceStore = parser.nextText();
						}
						else if ( tagName.equals("maxdistance") ) {
							maxDistanceStore = parser.nextText();
						}
						else if ( tagName.equals("startingdistance") ) {
							Range range = new Range( nameStore, Integer.parseInt(minDistanceStore),
													            Integer.parseInt(maxDistanceStore),
													            Integer.parseInt( parser.nextText() ) );
							mRangeCache.add(range);
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
			throw new IllegalArgumentException("No default range in XML file");

		return mRangeCache;
	}

	/**
	 * Answers the Range object set as the default one in the
	 * ranges XML source file.
	 * 
	 * @param context Context used to find the XML file
	 * @return
	 */
	public static Range findDefaultRange( Context context )
	{
		if ( mDefaultName == null )
			listRanges( context );

		return findRange( context, mDefaultName );
	}
	
}
