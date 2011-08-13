package org.derekfountain.dofc;

import java.util.ArrayList;
import java.util.regex.Pattern;

import org.derekfountain.dofc.m.Body;
import org.derekfountain.dofc.m.Lens;
import org.derekfountain.dofc.m.Range;
import org.derekfountain.dofc.v.MVCView;
import org.derekfountain.dofc.v.RotatingSpinner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

/**
 * This is currently the all encompassing activity - just a simple multipaged shell.
 * 
 * Page is the main activity which fills each page.
 * 
 * This code gets the main menu and handles the creation of a new Page activity.
 * 
 * Recent Android versions have deprecated TabActivity, which is a good move. All
 * through development of this code I was fighting it, rather than working with it.
 */

public class DepthOfFieldCalc extends TabActivity {
	
	protected static final int NEW_TAB_DIALOG_ID  = 0;
	protected static final int ABOUT_DIALOG_ID    = 1;
	protected static final int UNITS_DIALOG_ID    = 2;
	
	/**
	 * applicationState holds the state of the entire application,
	 * including all tabs and their contents. It's this object that
	 * needs persisting when the application gets paused.
	 */
	protected static ApplicationState applicationState = new ApplicationState();
	public static ApplicationState getApplicationState() {
		return applicationState;
	}

	/**
	 * This is a workaround for issue
	 * https://code.google.com/p/android-depth-of-field-gpl/issues/detail?id=1&can=1
	 * If you create a tab in the Android TabWidget, then delete it
	 * by removing all tabs, then recreate it, then switch to it - and
	 * that's a sequence that's perfectly possible with this app - then
	 * you get a crash deep in the tab widget. The workaround is to
	 * store in this array a list of the previously deleted tab names
	 * so I can tell if the user recreates one, and so something about
	 * it.
	 */
	protected static ArrayList<String> previouslyDeletedTabs   = new ArrayList<String>();
	
	/**
	 * Workaround for issue
	 *  http://code.google.com/p/android-depth-of-field-gpl/issues/detail?id=2
	 * I've created a Spinner class which extends the regular Android
	 * spinner, only giving it the ability to withstand the phone being
	 * rotated while the spinner's list dialog is dropped.
	 */
    public RotatingSpinner bodySpinner  = null;
    public RotatingSpinner lensSpinner  = null;
    public RotatingSpinner rangeSpinner = null;
	
	/**
	 * The main "wrapping" Activity start point.
	 * <p>
	 * When this starts up there are no Pages running. They will
	 * all have been torn down along with the wrapper activity
	 * when the wrapper was destroyed. So this is the "start from
	 * scratch" point.
	 * <p>
	 * This restores all the previous tabs from saved state.
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
        setContentView(R.layout.depthoffieldcalc);
        
        // Rebuild application state, either from saved bundle if we're
        // restoring after being destroyed, or from scratch if this is
        // a new run
        //
        if ( savedInstanceState != null )
        	applicationState.restoreFromBundle(savedInstanceState);
        else
        	applicationState.restoreFromDefaults( this );
        
        // Start off this list as empty
        //
        previouslyDeletedTabs = new ArrayList<String>();
        
        rebuildTabs();       
    }
        
    /**
     * Regenerate the view's tabs from the application's known pages.
     * <p>
     * This is called with knownTabs containing the list of tabs names,
     * in order, to be recreated, and a set of matching entries in
     * knownPages which describe the states of each Page.
     */
    protected void rebuildTabs()
    {
        // Loop over the known tabs recreating a tab in the GUI for each one
        //
        TabHost tabHost = getTabHost();

        // tabsToRebuild is the ordered list of tab names
        //
        ArrayList<String> tabsToRebuild = applicationState.getKnownTabs();
        Log.v("rebuildTabs", "Rebuilding "+tabsToRebuild.size()+" tabs");
        
        for ( String tabName : tabsToRebuild ) {
        	Log.v("rebuildTabs", "Rebuilding tab named "+tabName);
        	
        	// Each tab to rebuild has a matching entry in knownPages which
        	// describes the state of the page - slider values, etc
        	//
        	PageState pageState = applicationState.getKnownPages().get(tabName);
        	
	        // Create the the intent to make the page appear
	        //
	        Intent intent  = new Intent().setClass(this, Page.class);
	        Uri    pageUri = pageState.getUri();
	        intent.setData(pageUri);

	        // Create a new tab with Page activity
	        //
	        TabHost.TabSpec spec = tabHost.newTabSpec(tabName).setContent(intent);
	        spec.setIndicator(tabName);
	        tabHost.addTab(spec);
        }
    }
    
    /**
     * Last chance to save state before getting killed.
     * <p>
     * Saves out the application state, ready to be restored in the
     * onCreate().
     */
    @Override
    public void onSaveInstanceState( Bundle savedInstanceState )
    {
    	super.onSaveInstanceState(savedInstanceState);
    	applicationState.saveToBundle(savedInstanceState);
    }
    
    /**
     * Destroy the whole application.
     * <p>
     * Unused for now.
     */
    @Override
    public void onDestroy()
    {
    	super.onDestroy();
    }
    
    /**
     * Replace default child finishing behaviour, which is to kill the
     * entire application, with NOP.
     */
    @Override
    public void finishFromChild( Activity c )
    {
    }
    
    /**
     * Resume the application.
     * <p>
     * Unused for now.
     */
    @Override
    public void onResume()
    {
    	super.onResume();
    }

    /**
     * Application is losing focus. It might be destroyed before
     * we see it again.
     */
    @Override
    public void onPause()
    {
    	super.onPause();
    	
    	// If any of the new tab dialog's spinners are dropped,
    	// dismiss them.
    	//
    	if ( bodySpinner != null ) {
    		bodySpinner.dismissPoppedUpList();
    	}
    	if ( lensSpinner != null ) {
    		lensSpinner.dismissPoppedUpList();
    	}
    	if ( rangeSpinner != null ) {
    		rangeSpinner.dismissPoppedUpList();
    	}
    }

    /**
     * This is called when the menu is created, at the start.
     */
    @Override
    public boolean onCreateOptionsMenu( Menu menu )
    {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.optionsmenu, menu);
    	return true;
    }
    
    /**
     * Called when the options menu is about to be shown, this disables
     * the delete tab menu item if there's only one tab left
     */
    public boolean onPrepareOptionsMenu( Menu menu )
    {
    	super.onPrepareOptionsMenu(menu);

        if ( applicationState.getKnownTabs().size() <= 1 ) {
        	menu.findItem(R.id.menu_delete).setEnabled(false);
        } else {
        	menu.findItem(R.id.menu_delete).setEnabled(true);        	
        }
    	return true;
    }
    
    /**
     * This is called when an item is selected from the options
     * menu.
     */
    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
    	switch( item.getItemId() )
    	{
    	case R.id.menu_new_item:
    		showDialog(NEW_TAB_DIALOG_ID);
    		return true;

    	case R.id.menu_units:
    		showDialog(UNITS_DIALOG_ID);
    		return true;
    		
    	case R.id.menu_about:
    		showDialog(ABOUT_DIALOG_ID);
    		return true;

    	default:
   			return false;
    	}

    }

    /**
     * This is called when one of the application's dialogs is requested.
     */
    @Override
    protected Dialog onCreateDialog( int id )
    {
    	Resources           res      = getResources();
    	AlertDialog         dialog   = null;

    	AlertDialog.Builder builder  = new AlertDialog.Builder( this );
    	LayoutInflater      inflater = getLayoutInflater();     // Activity window inflater, not the app one

    	switch( id )
    	{
    	case NEW_TAB_DIALOG_ID:
    		
    		// Inflate the new dialog layout XML.
    		//
    		View layout = inflater.inflate(R.layout.newdialog, null);
    		builder.setView(layout);

    		// Create the dialog
    		//
    		dialog = builder.create();
    		
    		/*
    		 * The spinners are populated once when the dialog is created.
    		 * The tab name TextEdit widget is populated each time the
    		 * dialog is raised via the onPrepareDialog() method.
    		 */
    		
    		// Populate an adapter for the bodies spinner, taking a note of position of the last used value  		
    		//    		
    		ArrayAdapter<CharSequence> bodiesAdapter = new ArrayAdapter<CharSequence>( this, android.R.layout.simple_spinner_item );
    		bodiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		String lastUsedBodyName = applicationState.getOptions().getLastUsedBodyName();
    		int lastUsedBodyPosition = 0;
    		int i = 0;
    		for ( Body body : Body.listBodies(this) ) {
    			bodiesAdapter.add( body.getName() );
    			if ( body.getName().equals(lastUsedBodyName) )
    				lastUsedBodyPosition = i;
    			i++;
    		}
    		bodySpinner = ((RotatingSpinner)layout.findViewById(R.id.BodySpinner));
    		bodySpinner.setAdapter(bodiesAdapter);
  
    		// Populate an adapter for the lens spinner, taking a note of position of the last used value    		
    		//    		
    		ArrayAdapter<CharSequence> lensAdapter = new ArrayAdapter<CharSequence>( this, android.R.layout.simple_spinner_item );
    		lensAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		String lastUsedLensName = applicationState.getOptions().getLastUsedLensName();
    		int lastUsedLensPosition = 0;
    		i = 0;
    		for ( Lens lens : Lens.listLenses(this) ) {
    			lensAdapter.add( lens.getName() );    			
    			if ( lens.getName().equals(lastUsedLensName) )
    				lastUsedLensPosition = i;
    			i++;
    		}
    		lensSpinner = ((RotatingSpinner)layout.findViewById(R.id.LensSpinner));
    		lensSpinner.setAdapter(lensAdapter);

    		// Populate an adapter for the range spinner, taking a note of position of the last used value    		
    		//    		
    		ArrayAdapter<CharSequence> rangeAdapter = new ArrayAdapter<CharSequence>( this, android.R.layout.simple_spinner_item );
    		rangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		String lastUsedRangeName = applicationState.getOptions().getLastUsedRangeName();
    		int lastUsedRangePosition = 0;
    		i = 0;
    		for ( Range range : Range.listRanges(this) ) {
    			rangeAdapter.add( range.getName() );    			
    			if ( range.getName().equals(lastUsedRangeName) )
    				lastUsedRangePosition = i;
    			i++;
    		}
    		rangeSpinner = ((RotatingSpinner)layout.findViewById(R.id.RangeSpinner));
    		rangeSpinner.setAdapter(rangeAdapter);
    		
    		// Prime the defaults, as used last time the dialog was accessed
    		//
    		bodySpinner.setSelection(lastUsedBodyPosition);
    		lensSpinner.setSelection(lastUsedLensPosition);
    		rangeSpinner.setSelection(lastUsedRangePosition);
    		
    		// The OK button is linked to a bit of code which adds a new tab to the main activity's tab host.
    		//
    		dialog.setButton(DialogInterface.BUTTON_POSITIVE, res.getString(R.string.ok), new NewTabClickListener() );
    		    		
    		// Cancel button just makes the dialog disappear
    		//
    		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, res.getString(R.string.cancel),
    				         (DialogInterface.OnClickListener)null);
    		
    		break;
    		
    	case UNITS_DIALOG_ID:
    		
    		final View settingsLayout = inflater.inflate(R.layout.unitsdialog, null);
    		builder.setView(settingsLayout);
    		dialog = builder.create();

    		// Prime the units radio buttons with the current application setting
    		//
    		RadioButton metricButton = (RadioButton)settingsLayout.findViewById(R.id.metricRadioButton);
    		RadioButton imperialButton = (RadioButton)settingsLayout.findViewById(R.id.imperialRadioButton);
    		if ( applicationState.getOptions().getUnits() == MVCView.Units.METRIC ) {
    			metricButton.setChecked(true);
    			imperialButton.setChecked(false);
    		}
    		else {
    			metricButton.setChecked(false);
    			imperialButton.setChecked(true);    			
    		}
					    		
    		dialog.setButton(DialogInterface.BUTTON_POSITIVE, res.getString(R.string.ok),
    				         new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					
					RadioButton metricButton = (RadioButton)settingsLayout.findViewById(R.id.metricRadioButton);
					if ( metricButton.isChecked() ) {
						applicationState.getOptions().setUnits(MVCView.Units.METRIC);
						applicationState.getActivePage().getView().changeUnits(MVCView.Units.METRIC);
					}
					else {
						applicationState.getOptions().setUnits(MVCView.Units.IMPERIAL);
						applicationState.getActivePage().getView().changeUnits(MVCView.Units.IMPERIAL);							
					}
				}
			});
    		
    		break;

    	case ABOUT_DIALOG_ID:

    		View aboutLayout = inflater.inflate(R.layout.aboutdialog, null);
    		builder.setView(aboutLayout);
    		dialog = builder.create();
    		dialog.setButton(DialogInterface.BUTTON_POSITIVE, res.getString(R.string.ok),(DialogInterface.OnClickListener)null);
    		
    		break;
    	}

    	return dialog;
    }
    
    /**
     * Last minute handling of dialog prep, just before the dialog
     * goes onto the screen.
     */
    @Override
    protected void onPrepareDialog( int id, Dialog dialog )
    {
    	super.onPrepareDialog(id, dialog);
    	
    	switch( id )
    	{
    	case NEW_TAB_DIALOG_ID:
    		// Fill in and select the default tab name with something usable, so
    		// if the user just hits OK, they get a tab as opposed to a blank-name
    		// error message.
    		//
    		// If the last tab name was , say, "20D", this results in "20D_1", "20D_2" etc.
    		//
    		String lastUsedTabName = applicationState.getOptions().getLastUsedTabName();
	    	String checkTabName = lastUsedTabName;
	    	
	    	String coreName;
	    	if ( Pattern.matches("^.*_\\d+$", lastUsedTabName) )
	    		coreName = lastUsedTabName.substring(0, lastUsedTabName.lastIndexOf('_'));
	    	else
	    		coreName = lastUsedTabName;

	    	int extensionNum = 1;
	    	boolean tabNameInUse = true;
	    	while ( tabNameInUse ) {
	    		checkTabName = coreName+"_"+extensionNum;
	    		if ( applicationState.getKnownTabs().contains(checkTabName) ) {
	    			extensionNum++;
	    		}
	    		else {
	    			tabNameInUse = false;
	    		}
	    	}
	    	
	    	EditText tabNameWidget = (EditText)dialog.findViewById(R.id.NewTabName);
	    	tabNameWidget.setText(checkTabName);
    		tabNameWidget.selectAll();
    	}
    }
    
    /**
     * Handler for new intents, currently only used to deal with simple
     * Page-related issues.
     */
    @Override
    protected void onNewIntent( Intent intent ) {
    	
    	// This is a singleTask activity (see manifest) and so intents come
    	// through here as part of the activity pause/resume. I need to be
    	// careful to only do what should be done for the given intent.
    	//
    	if ( intent.getAction().equals( android.content.Intent.ACTION_DELETE ) ) {
	    	TabHost tabHost = getTabHost();
	    	
	    	// Remove the current tab from both the list of pages in the app,
	    	// and the ordered list of tabs
	    	//
	    	String tagToDelete = tabHost.getCurrentTabTag();
	    	applicationState.getKnownPages().remove(tagToDelete);
	    	applicationState.getKnownTabs().remove(tagToDelete);
	    	applicationState.setActivePage(null);
	        
	    	// Add the name of the deleted tab to the list, so I can tell
	    	// if the user reuses it. See comments at the declaration of
	    	// previouslyDeletedTabs
	    	//
	    	previouslyDeletedTabs.add(tagToDelete);

	    	// This appears to be necessary, otherwise the app hits a NPE in the tab widget
	    	//
	    	tabHost.setCurrentTab(0);

	    	// Now wipe all tabs, then rebuild them all, minus the deleted one
	    	//
	    	tabHost.clearAllTabs();		
	   		rebuildTabs();
    	}
    }
    
    /**
     * Inner class to handle the click which makes a new tab appear.
     * <p>
     * This is called when the new tab dialog OK button is clicked.
     * It picks up the user supplied information from the dialog,
     * does appropriate sanity checks on it, then fires off an
     * intent to make the new tab appear.
     */
    protected class NewTabClickListener implements DialogInterface.OnClickListener {

		@Override
		public void onClick(DialogInterface iDialog, int which) {
			PageState newPage = new PageState();

			Dialog dialog = (Dialog)iDialog;
			
			// Find the dialog widgets and pick up the current text in each of them
			//
			EditText tabNameWidget = (EditText)(dialog.findViewById(R.id.NewTabName));
			String   tabName       = tabNameWidget.getText().toString();
			
			Spinner bodySpinner    = (Spinner)(dialog.findViewById(R.id.BodySpinner));
			String  bodyName       = ((CharSequence)bodySpinner.getSelectedItem()).toString();
			
			Spinner lensSpinner    = (Spinner)(dialog.findViewById(R.id.LensSpinner));
			String  lensName       = ((CharSequence)lensSpinner.getSelectedItem()).toString();

			Spinner rangeSpinner   = (Spinner)(dialog.findViewById(R.id.RangeSpinner));
			String  rangeName      = ((CharSequence)rangeSpinner.getSelectedItem()).toString();

			// Check to see if the tab name specified is already in use. (The tab
			// widget doesn't allow tabs with duplicate names)
			//
			boolean duplicateTabName = false;
			for ( String knownTabName : applicationState.getKnownTabs() ) {
				if ( tabName.equals(knownTabName) ) {
					duplicateTabName = true;
					break;
				}
			}
			
			// Throw up a warning, or if all is OK, create the new tab
			//
			if ( tabName.length() == 0 ) {
				Toast warningToast = Toast.makeText(getApplicationContext(), R.string.warning_blank_tab, Toast.LENGTH_LONG);
				warningToast.show();
			}
			else if ( duplicateTabName ) {
				Toast warningToast = Toast.makeText(getApplicationContext(), R.string.warning_duplicate_tab, Toast.LENGTH_LONG);
				warningToast.show();						
			}
			else {
				
				// If the user has reused a tab name that they've previously
				// deleted, sneakliy change it by prepending a space to it.
				// See the comments at the declaration of previouslyDeletedTabs
				// for the explanation of why this nonsense is necessary.
				//
				while ( previouslyDeletedTabs.contains(tabName) )
					tabName = " "+tabName;
				
				// Everything is OK - create a new tab
				//
				applicationState.getKnownTabs().add(tabName);
				applicationState.getKnownPages().put(tabName, newPage);
    	
				// Set the new page to have the user specified body, lens and range
				//
				newPage.setBodyName(bodyName);
				newPage.setLensName(lensName);
				newPage.setRangeName(rangeName);
				
				// Set appropriate default values on the sliders
				//
				newPage.setFocalLength( Lens.findLens(getApplicationContext(), lensName).getStartingLength() );
				newPage.setAperture( Lens.findLens(getApplicationContext(), lensName).getStartingAperture() );
				newPage.setDistance( Range.findRange(getApplicationContext(), rangeName).getStartingDistance() );
				
				// Create an intent and use it to create the new Page
				//
				Uri pageUri = newPage.getUri();

				Intent intent = new Intent().setClass(getApplicationContext(), Page.class);
				intent.setData(pageUri);
				
				TabHost tabHost = getTabHost();
				TabHost.TabSpec spec = tabHost.newTabSpec( tabName ).setContent(intent);
				spec.setIndicator( tabName );
				tabHost.addTab(spec);
				
				// Update last used values so the dialog has sensible defaults next time
				//
				applicationState.getOptions().setLastUsedTabName(tabName);
				applicationState.getOptions().setLastUsedBodyName(bodyName);
				applicationState.getOptions().setLastUsedLensName(lensName);
				applicationState.getOptions().setLastUsedRangeName(rangeName);
			}
		}
			
    	
    }
    

}