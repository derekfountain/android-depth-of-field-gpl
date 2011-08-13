package org.derekfountain.dofc.m;

/**
 * Enumeration defining the ranges of f-stops a lens can use.
 * <p>
 *   http://en.wikipedia.org/wiki/F-number#Fractional_stops
 * <p>
 * Most (all?) lenses use the standard full stop scale. My Canon
 * ones use the third stop scale. I added quarter and half stop
 * scales too.
 * <p>
 * Lenses are defined as having one or more of these ranges, and
 * the GUI's aperture slider needs the array of values found
 * in them so it knows which values to jump between.
 */
public enum StopRange {
	FULL,
	QUARTER,
	THIRD,
	HALF
}
