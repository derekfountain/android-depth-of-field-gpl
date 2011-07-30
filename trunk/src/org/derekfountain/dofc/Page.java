package org.derekfountain.dofc;

import org.derekfountain.dofc.c.MVCController;
import org.derekfountain.dofc.m.Body;
import org.derekfountain.dofc.m.Lens;
import org.derekfountain.dofc.m.MVCModel;
import org.derekfountain.dofc.m.Range;
import org.derekfountain.dofc.v.MVCView;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

/**
 * This is the complete single page object. It uses a standard
 * MVC pattern to handle the user interation: it contains
 * a model (the body, the lens, etc.), the view (a single
 * page of the GUI) and the controller that joins them.
 * 
 */
public class Page extends Activity {

	protected PageState     pageState  = null;

	protected MVCModel      model      = null;
	
	protected MVCView       view       = null;
	
	protected MVCController controller = null;

	public MVCView getView() {
		return view;
	}

	/**
	 * onCreate(), called when the page is either created from
	 * scratch, or when the activity is restored after being
	 * killed by the system.
	 * <p>
	 * This just rebuilds the underlying system blocks. The
	 * interesting stuff happens in onResume().
	 */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		
        // Restore values for the page. They're restored into the Page's state object, from where the
        // onResume() code will unpack them in due course.
        //
        if ( savedInstanceState != null &&
        	 savedInstanceState.containsKey("FocalLength") &&
        	 savedInstanceState.containsKey("Aperture") &&
        	 savedInstanceState.containsKey("Distance") &&
        	 savedInstanceState.containsKey("BodyName") &&        	 
        	 savedInstanceState.containsKey("LensName") &&     	 
        	 savedInstanceState.containsKey("RangeName")
           ) {
        	pageState = new PageState();
        	
        	// The values for the page are in the bundle. This means that this
        	// activity was saved then killed by the system. I restore from the
        	// Bundle passed in.
        	//
        	pageState.setFocalLength( new Integer ( savedInstanceState.getInt("FocalLength") ) );
        	pageState.setAperture(    new Integer ( savedInstanceState.getInt("Aperture") ) );
        	pageState.setDistance(    new Integer ( savedInstanceState.getInt("Distance") ) );
        	pageState.setBodyName(    new String  ( savedInstanceState.getString("BodyName") ) );
        	pageState.setLensName(    new String  ( savedInstanceState.getString("LensName") ) );
        	pageState.setRangeName(   new String  ( savedInstanceState.getString("RangeName") ) );
        }
        else {
    		// The values for the page are attached to the intent object that
    		// kicked this activity into life. The intent was created by the
    		// wrapping TabActivity when it restored the whole application
    		// from saved state.
    		//
        	Intent intent     = getIntent();

    		Uri    intentData = intent.getData();
        	pageState = new PageState( intentData );
        }
		
        // Create the MVC components and tie them all together
        //
        model         = new MVCModel( null, null, null );
		
		view          = new MVCView( this );
		setContentView(R.layout.main);
		
		controller    = new MVCController();
		
		model.setView( view );
		view.setModel( model );
		
		controller.setView( view );
		view.setController( controller );
		
		controller.setModel( model );
		
	}
	
	/**
	 * onSaveInstanceState(), called when the activity is about to be killed.
	 * <p>
	 * Just save the running values so they can be restored (via onCreated())
	 * when the user navigates back to the page.
	 */
	@Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	super.onSaveInstanceState(savedInstanceState);
    	
    	// Save UI state changes to the savedInstanceState.
    	// This bundle will be passed to onCreate if the activity is
    	// killed and restarted.
    	//
    	savedInstanceState.putInt("FocalLength",  view.getFocalLength() );
    	savedInstanceState.putInt("Aperture",     view.getAperture() );
    	savedInstanceState.putInt("Distance",     view.getDistance() );
    	savedInstanceState.putString("BodyName",  model.getBody().getName() );
    	savedInstanceState.putString("LensName",  model.getLens().getName() );
    	savedInstanceState.putString("RangeName", model.getRange().getName() );
    	
    	Log.v("Page.onSaveInstanceState", String.format("Saving Page values Body=\"%s\", Lens=\"%s\", Range=\"%s\", "+
    			                                        "focal length=%d, aperture=%d, distance=%d",
    			                                        model.getBody().getName(),
    			                                        model.getLens().getName(),
    			                                        model.getRange().getName(),
    			                                        view.getFocalLength(),
    			                                        view.getAperture(),
    			                                        view.getDistance() ));
    }

	/**
	 * onResume(), the top of the "inner loop".
	 * <p>
	 * This is called when the Page goes "active" as the user navigates
	 * to it.
	 */
    @Override
    public void onResume()
    {
		super.onResume();

		// Tell the wrapping activity (the TabActivity) that this page activity
    	// is running and will need updating if the user changes global app
    	// settings, etc.
    	//
    	DepthOfFieldCalc.getApplicationState().setActivePage( this );

		// Initialise the page by triggering the update code
		//
		view.changeBody(  Body.findBody(   this, pageState.getBodyName()) );
		view.changeLens(  Lens.findLens(   this, pageState.getLensName()) );
		view.changeRange( Range.findRange( this, pageState.getRangeName()) );
		
		// Kick the view drawing code to get the diagram on screen
		//
		view.initialiseView( pageState.getFocalLength(),
		                     pageState.getAperture(),
		                     pageState.getDistance() );
		 	
    }

    /**
     * Called as the user leaves the Page, it saves state.
     * <p>
     * The page might get destroyed by the OS before the user
     * come back to it, so this saves away the state ready in case
     * onResume() needs to put it all back together.
     */
    @Override
    public void onPause()
    {    	
    	super.onPause();

    	Integer saveFocalLength = new Integer( view.getFocalLength() );
    	Integer saveAperture    = new Integer( view.getAperture() );
    	Integer saveDistance    = new Integer( view.getDistance() );
    	String  saveBody        = model.getBody().getName();
    	String  saveLens        = model.getLens().getName();
    	String  saveRange       = model.getRange().getName();
    	
    	// Save UI state changes to the Page's state store object.
    	// This store will be retrieved by the onResume() when we
    	// wake up again.
    	//
    	pageState.setFocalLength( saveFocalLength );
    	pageState.setAperture(    saveAperture  );
    	pageState.setDistance(    saveDistance );
    	pageState.setBodyName(    saveBody );
    	pageState.setLensName(    saveLens );
    	pageState.setRangeName(   saveRange );
    	
    	Log.v("Page.onPause", String.format("Storing Page values Body=\"%s\", Lens=\"%s\", Range=\"%s\", "+
    			                            "focal length=%d, aperture=%d, distance=%d",
    			                            model.getBody().getName(),
    			                            model.getLens().getName(),
    			                            model.getRange().getName(),
    			                            view.getFocalLength(),
    			                            view.getAperture(),
    			                            view.getDistance() ));

    	// Remove this page from the parent activity (the TabActivity)'s
    	// Page to worry about when the user changes something.
    	//
    	DepthOfFieldCalc.getApplicationState().setActivePage( null );
    }
    
    /**
     * This activity will be the current one in the tabs, so
     * set the tab widget's understanding of the current activity
     * to null.
     */
    @Override
    public void onDestroy()
    {
    	DepthOfFieldCalc.getApplicationState().setActivePage( null );
    	super.onDestroy();
    }

    /**
     * Handle Page-related menu options.
     * <p>
     * The majority of menu options are relevant to the main application,
     * and so are passed up via 'super' to be dealt with by the main
     * TabActivity class.
     */
    @Override
    public boolean onOptionsItemSelected( MenuItem item )
    {
    	/**
    	 * The parent, DepthOfFieldCalc, is responsible for all the menu item handling,
    	 * with the exception of the Page deletion, which is handled here.
    	 */
    	switch ( item.getItemId() )
    	{
    	/*
    	 * Delete the current Page and the tab it sits on.
    	 * This is harder than it should be. There's no way to tell the
    	 * Android TabActivity widget to delete one of its sub-activities.
    	 * After much experimentation I decided on the following
    	 * approach:
    	 * 
    	 *  * send an intent to the parent TabActivity telling it to forget
    	 *    about its current Page. Calling parent methods via getParent()
    	 *    doesn't work, because that call returns here, and "here" will have
    	 *    gone! So I send an asynchronous instruction.
    	 *  * the parent is responsible for removing the current page of
    	 *    the TabActivity - see the onNewIntent() method which handles
    	 *    the incoming intent. It basically removes all tabs from the
    	 *    tab widget, then puts them all back minus the one being deleted.
    	 *  * this Page activity needs to finish, but finish() doesn't work
    	 *    when an activity is a child. The parent's clearup of the tabs
    	 *    doesn't seem to cause the Page to disappear either. So I suppress
    	 *    the normal behaviour of finishing children - which is to finish
    	 *    the entire application - (overriding onChildFinish() in the
    	 *    parent) and then use the local activity manager to destroy this
    	 *    Page activity.
    	 *    
    	 * I remain unsure whether the above is a good policy. It seems to work
    	 * and I couldn't find anything better. I note that recent Android versions
    	 * have deprecated TabActivity.
    	 */
    	case R.id.menu_delete:
    		// This approach just throws a class not found exception. I should probably
    		// learn why. The direct context/class constructor is the more efficient
    		// way to do it though.
    		//
    		// killMeIntent.setComponent( new ComponentName("org.derekfountain.dofc", "DepthOfFieldCalc") );

    		// Create an intent to send a message to the parent (the DepthOfFieldCalc TabActivity)
    		// to say that this page is about to disappear. The parent needs to delete the page
    		// from its view of the world.
    		//
    		// The intent goes straight to the parent activity
    		//
    		Intent forgetMeIntent = new Intent( getParent().getApplicationContext(), getParent().getClass() );
    		forgetMeIntent.setAction( android.content.Intent.ACTION_DELETE );
    		
    		// The parent activity is set in the manifest as singleTask, as described here:
    		// http://developer.android.com/guide/topics/fundamentals/tasks-and-back-stack.html
    		// Hence, with the NEW_TASK flag, the intent arrives at the onNewIntent() method
    		// of the parent
    		//
    		forgetMeIntent.setFlags( android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
    		getBaseContext().startActivity(forgetMeIntent);
    		
    		// Parent has now been told that this page is about to disappear. Now, make
    		// page disappear. :) Because this is a child, and the parent has had its
    		// finishFromChild() method overridden (to prevent the default behaviour of
    		// whacking the whole application), I need to destroy this Page activity
    		// using the local activity manager.
    		//    		
    		TabActivity          parent = (TabActivity)getParent();
    		LocalActivityManager lam    = parent.getLocalActivityManager();
    		lam.destroyActivity( lam.getCurrentId(), true);

    		// To be honest, I don't know quite what happens now. Following this through
    		// with the debugger, the thread comes back here and unwinds the menu selection,
    		// then goes through the destroy activity android code. I'm not actually sure
    		// if the activity goes away completely - how would I know? Must learn more...
    		//
    		return true;
    		
    	default:
    		// Everything else is passed up by the default handler to be handled by the TabActivity
    		//
    		return super.onOptionsItemSelected(item);
    	}
    }

}
