package org.derekfountain.dofc;

import java.util.ArrayList;
import java.util.HashMap;

import org.derekfountain.dofc.v.MVCView;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Class to hold the state of the application. Global
 * configuration, plus a list of the tabs.
 * <p>
 * This used to be an inner class of DepthOfFieldCalc, and
 * logically that's where it belongs. But I think things
 * are a bit more readable if this is extracted out into
 * it's own class.
 * 
 */
public class ApplicationState {
	/**
	 * Inner class holds application wide options.
	 */
	protected class Options {
		protected MVCView.Units units             = MVCView.Units.METRIC;
		protected String        lastUsedTabName   = null;
		protected String        lastUsedBodyName  = null;
		protected String        lastUsedLensName  = null;
		protected String        lastUsedRangeName = null;

		public MVCView.Units getUnits() {
			return units;
		}
		public void setUnits(MVCView.Units units) {
			this.units = units;
		}
		public String getLastUsedTabName() {
			return lastUsedTabName;
		}
		public void setLastUsedTabName(String lastUsedTabName) {
			this.lastUsedTabName = lastUsedTabName;
		}
		public String getLastUsedBodyName() {
			return lastUsedBodyName;
		}
		public void setLastUsedBodyName(String lastUsedBodyName) {
			this.lastUsedBodyName = lastUsedBodyName;
		}
		public String getLastUsedLensName() {
			return lastUsedLensName;
		}
		public void setLastUsedLensName(String lastUsedLensName) {
			this.lastUsedLensName = lastUsedLensName;
		}
		public String getLastUsedRangeName() {
			return lastUsedRangeName;
		}
		public void setLastUsedRangeName(String lastUsedRangeName) {
			this.lastUsedRangeName = lastUsedRangeName;
		}
		
	}
	protected Options options = new Options();
	
	// knownPages holds the complete set of Pages the application
	// knows about. This includes the inactive and destroyed ones
	// which the system might have clobbered, but which the user
	// thinks are there. This map is keyed on the name of the tab,
	// and is used to restore the application state to the
	// position the user expects. The pages in it are updated by
	// the Page class at onPause() - i.e. when the Page activity
	// is paused, it writes its current values in here.
	//
	// The HashMap comes back in no particular order so at save
	// time the tabs get saved in random order, which means at
	// restore time the user's tabs get rearranged. So I need
	// a separate array list (which does retain order) to keep
	// the order of the user's tabs consistent.
	//
	protected ArrayList<String>         knownTabs   = new ArrayList<String>();
	protected HashMap<String,PageState> knownPages  = new HashMap<String,PageState>();
	
	// activePage holds the active page if the app has focus.
	// This is used to update the page with new global option
	// settings, etc. The Page will remove itself when it pauses.
	//
	protected Page activePage = null;

	public Options getOptions() {
		return options;
	}
	public void setKnownTabs(ArrayList<String> knownTabs) {
		this.knownTabs = knownTabs;
	}
	public ArrayList<String> getKnownTabs() {
		return knownTabs;
	}
	public HashMap<String,PageState> getKnownPages() {
		return knownPages;
	}
	public Page getActivePage() {
		return activePage;
	}
	public void setActivePage(Page activePage) {
		this.activePage = activePage;
		
		// Ensure the activity which has just come to the front and has
		// resumed running has up to date global options. If the activity
		// has just paused itself, don't do this.
		//
		if ( activePage != null )
			activePage.getView().setUnits( DepthOfFieldCalc.getApplicationState().getOptions().getUnits() );
	}
	
	/**
	 * Writes out the current application settings to the android bundle
	 * 
	 * @param bundle
	 */
	protected void saveToBundle( Bundle bundle )
	{
		// Save global options
		//
		bundle.putBoolean("global_metric_units",  (options.getUnits() == MVCView.Units.METRIC));
		bundle.putString("last_used_tab_name",    options.getLastUsedTabName());
		bundle.putString("last_used_body_name",   options.getLastUsedBodyName());
		bundle.putString("last_used_lens_name",   options.getLastUsedLensName());
		bundle.putString("last_used_range_name",  options.getLastUsedRangeName());
		
		// Save the list of tab names. This comes from the array of
		// tab names, not the hash key, in order to preserve order.
		//
		String[] knownPageNames = knownTabs.toArray( new String[]{} );
		bundle.putStringArray("tab_names", knownPageNames );
				
		// Now loop over the pages the application is currently holding
		// and save each one's last known state. This will save out all
		// the pages the user has up, paused or destroyed, or not.
		//
		Log.v("Application state, saveToBundle", "Saving "+knownTabs.size()+" tabs ");
		for ( String tabName : knownTabs ) {

			Log.v("Application state, saveToBundle", "Saving tab "+tabName);
			PageState pageStateToSave = knownPages.get(tabName);
			
			String uriString = pageStateToSave.getUri().toString();
			bundle.putString(tabName, uriString);
		}
		
	}
	
	/**
	 * Restores the current application settings from the given android bundle
	 * 
	 * @param bundle
	 */
	protected void restoreFromBundle( Bundle bundle )
	{
		// Restore global options
		//
		if ( bundle.getBoolean("global_metric_units") ) {
			options.setUnits(MVCView.Units.METRIC);
		}
		else {
			options.setUnits(MVCView.Units.IMPERIAL);				
		}
		options.setLastUsedTabName( bundle.getString("last_used_tab_name") );
		options.setLastUsedBodyName( bundle.getString("last_used_body_name") );
		options.setLastUsedLensName( bundle.getString("last_used_lens_name") );
		options.setLastUsedRangeName( bundle.getString("last_used_range_name") );

		// Get back the list of tab names. They were saved in the order
		// the user created them, and that's how I pull them out, back
		// into the array list.
		//
		String[] tabNames = bundle.getStringArray("tab_names");

		knownTabs = new ArrayList<String>();
		for ( String tabName : tabNames ) {
			knownTabs.add(tabName);
		}
		Log.v("Application state, restoreFromBundle", "Restoring "+knownTabs.size()+" tabs");
		
		// For each tab, find the state of the Page in it and
		// restore the known pages
		//
		for ( String tabName : knownTabs ) {
			Log.v("Application state, restoreFromBundle", "Restoring tab "+tabName);
			String restoredUriString = bundle.getString(tabName); 
			Uri pageUri = Uri.parse( restoredUriString );
			
			PageState newPageState = new PageState( pageUri );
			
			knownPages.put(tabName, newPageState);
		}
		
	}
	
	/**
	 * Restores the current application settings from a sensible set of
	 * default values.
	 * <p>
	 * Name of tab comes from resources file, and is set to use metric.
	 * The page it contains gets the page default settings which ultimately
	 * come via the 'default' attributes in the XML data files.
	 * 
	 * @param context Context object used to find resources
	 */
	protected void restoreFromDefaults( Context context )
	{
		// Name of tab to create comes from the resources file
		//
		String defaultName = context.getResources().getString(R.string.first_tab_name);
		
		options.setUnits( MVCView.Units.METRIC );
		options.setLastUsedTabName(defaultName);
		
		PageState defaultPage = new PageState().setDefaults( context );
		
		knownTabs.add(defaultName);
		knownPages.put(defaultName, defaultPage);
	}

}
