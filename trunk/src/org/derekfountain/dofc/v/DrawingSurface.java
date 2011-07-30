package org.derekfountain.dofc.v;

import org.derekfountain.dofc.R;
import org.derekfountain.dofc.v.MVCView.Units;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

/**
 * This is the view that carries the diagram. It's cut out of raw
 * Java code and drawing primitives.
 */
public class DrawingSurface extends View {

	/*
	 * I've set the manifest's target SDK version to 4 (Android 1.6) which turns
	 * on the anyDensity attribute. That means Android does the graphics scaling.
	 * 
	 * http://developer.android.com/guide/practices/screens_support.html
	 */
    	
	// These are the size related values. See the constructor for the values assigned.
	//
	protected final int   FIELD_HEIGHT;         // FoV height, on the right (taller) side, in DIPs
	protected final int   V_LINE_HEIGHT;        // Height of a marker point line, in DIPs
	protected final int   V_LINE_SPACING;       // Vertical gap between various things, in DIPs
	protected final int   ORIGIN;               // Point, both x,y, inside the view to draw, in DIPs
	protected final int   SUBJECT_SIZE;         // Size of the blob in the middle of the diagram, in DIPs
	protected final int   ARROW_SPACING;        // Horizontal gap between an arrow and the thing it's pointing at, in DIPs
	protected final int   WIDTH_ARROW_HEAD;     // Width of an arrow head, in DIPs
	protected final int   HEIGHT_ARROW_HEAD;    // Height of an arrow head, in DIPs

   	// Values to show on the diagram. null means infinity.
	//
	protected Double nearLimit          = null;
    protected Double farLimit           = null;
    protected Double dof                = null;
    protected Double inFront            = null;
    protected Double behind             = null;
    protected Double hyperfocalDistance = null;
    protected Double hyperfocalMin      = null;
    
    // Unit of measurement to display in. This is only used here
    // to display the correct unit in the displayed output strings
    //
    protected Units  units         = Units.METRIC;
    
	public DrawingSurface( Context context, AttributeSet attrSet )
	{
		super(context, attrSet);

		Resources res = getResources();
		
		// I scale up the height of the drawing my the density scaler. This
		// makes the diagram a bit taller which looks a bit better.
		//
		FIELD_HEIGHT      = (int)(res.getInteger(R.integer.field_height) * getResources().getDisplayMetrics().density + 0.5f);
		V_LINE_HEIGHT     = (int)(res.getInteger(R.integer.v_line_height) * getResources().getDisplayMetrics().density + 0.5f);
		V_LINE_SPACING    = (int)(res.getInteger(R.integer.v_line_spacing) * getResources().getDisplayMetrics().density + 0.5f);
		ORIGIN            = res.getInteger(R.integer.view_padding);
		SUBJECT_SIZE      = res.getInteger(R.integer.subject_size);
		ARROW_SPACING     = res.getInteger(R.integer.arrow_spacing);
		WIDTH_ARROW_HEAD  = res.getInteger(R.integer.width_arrow_head);
		HEIGHT_ARROW_HEAD = res.getInteger(R.integer.height_arrow_head);
	}
	
	/**
	 * Set the values to be represented on screen.
	 * <p>
	 * The distances are just numbers at this level. Whatever
	 * is passed in in these distances will be printed to the
	 * screen as is, with a suitable abbreviated string like
	 * "m" or "ft" following. This display code gives no meaning
	 * to what the value actually means - that's done in the
	 * view.
	 * 
	 * @param nearLimit
	 * @param farLimit
	 * @param total
	 * @param frontDistance
	 * @param behindDistance
	 * @param hyperfocalDistance
	 */
	protected void setValues( Double nearLimit, Double farLimit,
			                  Double total, Double frontDistance, Double behindDistance,
			                  Double hyperfocalDistance,
			                  Units units )
	{
		this.nearLimit          = nearLimit;
		this.farLimit           = farLimit;
		this.dof                = total;
		this.inFront            = frontDistance;
		this.behind             = behindDistance;
		this.hyperfocalDistance = hyperfocalDistance;
		
		if ( this.hyperfocalDistance != null )
			hyperfocalMin  = hyperfocalDistance/2;
		else
			hyperfocalMin  = null;
		
		this.units = units;
	}
	
	/**
	 * Answers the string used to abbreviate the units being used.
	 * <p>
	 * In English that's "m" for metres, and "ft" for feet.
	 * 
	 * @return
	 */
	protected String getUnitsAbbreviation()
	{
		if ( units == Units.METRIC )
			return getContext().getResources().getString(R.string.metres_abb);
		else
			return getContext().getResources().getString(R.string.feet_abb);			
	}
	
	/**
	 * Define the width and height of my required drawing area.
	 */
	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
	{
		// Height is the bit above the field of view diagram, plus the field of view diagram,
		// plus the bit below the field of view diagram, plus the hyperfocal distance information.
		//
    	int requiredHeight = V_LINE_HEIGHT + V_LINE_SPACING +
    	                     FIELD_HEIGHT + 
    	                     V_LINE_SPACING + V_LINE_HEIGHT +
    	                     V_LINE_SPACING + V_LINE_HEIGHT;

    	// int measuredWidth  = (int)(View.MeasureSpec.getSize(widthMeasureSpec));
    	// int measuredHeight = (int)(View.MeasureSpec.getSize(heightMeasureSpec));
    	//
    	// The above gives 480x582 on my 480x800 phone.
    	//
    	setMeasuredDimension( View.MeasureSpec.getSize(widthMeasureSpec), requiredHeight );
	}
	
	/**
	 * Draw the diagram.
	 */
	@Override
    protected void onDraw(Canvas canvas) {
    	   	
		// getWidth() returns the value set in the setMeasuredDimension() call
		// in onMeasure().
		//
    	int viewWidth  = getWidth();
    	
    	// Bump up the size of the displayed numbers to make them
    	// a bit more readable
    	//
    	float fontScale = getResources().getDisplayMetrics().density;
    	
        Paint brightPaint = new Paint();
        brightPaint.setAntiAlias(true);
        brightPaint.setColor(0xffffffff); // Full alpha, white
        brightPaint.setTextSize( brightPaint.getTextSize() * fontScale );

        Paint dimPaint = new Paint();
        dimPaint.setAntiAlias(true);
        dimPaint.setColor(0xff555555);    // Full alpha, grey-ish
        dimPaint.setTextSize( brightPaint.getTextSize() * fontScale );

		// Field of view constants.
        //
              int FOV_LEFT_SIDE    = ORIGIN;
        final int FOV_TOP          = V_LINE_HEIGHT + V_LINE_SPACING;
        final int FOV_BOTTOM       = FOV_TOP+FIELD_HEIGHT;
        final int FOV_RIGHT_SIDE   = viewWidth-ORIGIN;
    	final int MIDDLE_Y         = (FOV_BOTTOM-FOV_TOP)/2 + FOV_TOP;

    	// Render the camera image at the left side, and move the DOF diagram in
    	// the width of that image from the left side
    	//
    	Bitmap bm = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.cam_side_on);
    	canvas.drawBitmap(bm, FOV_LEFT_SIDE, MIDDLE_Y-(bm.getHeight())/2, brightPaint);
    	FOV_LEFT_SIDE += bm.getWidth()+getContext().getResources().getInteger(R.integer.view_padding)/2;
    	
    	// Now calculate the rest of the constants
    	//
    	final int MIDDLE_X         = (FOV_RIGHT_SIDE-FOV_LEFT_SIDE)/2 + FOV_LEFT_SIDE;
        final int FOV_TOP_LEFT     = MIDDLE_Y-(FIELD_HEIGHT/4);
        final int FOV_BOTTOM_LEFT  = MIDDLE_Y+(FIELD_HEIGHT/4);
        final int FOV_TOP_RIGHT    = MIDDLE_Y-((FIELD_HEIGHT/4)*3);
        final int FOV_BOTTOM_RIGHT = MIDDLE_Y+((FIELD_HEIGHT/4)*3);
        
        // Fill in the gradient values
        //
		Paint shaderPaint = new Paint();
        shaderPaint.setAntiAlias(true);
        shaderPaint.setShader( new LinearGradient(FOV_LEFT_SIDE, MIDDLE_Y,
        		                                  FOV_RIGHT_SIDE, MIDDLE_Y,
        										  new int[] {0xffeeeeee, 0xff6384B5, 0xffeeeeee},
        										  null,
        										  Shader.TileMode.CLAMP) );
        shaderPaint.setStyle(Paint.Style.FILL);
        
        // Draw the field of view, gradient bar
        //
        Path leftPath = new Path();
        leftPath.moveTo( FOV_LEFT_SIDE,  FOV_TOP_LEFT );
        leftPath.lineTo( FOV_RIGHT_SIDE, FOV_TOP_RIGHT );
        leftPath.lineTo( FOV_RIGHT_SIDE, FOV_BOTTOM_RIGHT );
        leftPath.lineTo( FOV_LEFT_SIDE,  FOV_BOTTOM_LEFT );
        leftPath.lineTo( FOV_LEFT_SIDE,  FOV_TOP_LEFT );
        canvas.drawPath( leftPath, shaderPaint );
        
        // Heavier white lines to frame field of view.
        //
        shaderPaint.setColor(0xffffffff);
        shaderPaint.setStrokeWidth(1.5f);
        canvas.drawLine(FOV_LEFT_SIDE, FOV_TOP_LEFT,    FOV_RIGHT_SIDE, FOV_TOP_RIGHT,    shaderPaint);
        canvas.drawLine(FOV_LEFT_SIDE, FOV_BOTTOM_LEFT, FOV_RIGHT_SIDE, FOV_BOTTOM_RIGHT, shaderPaint);
        
        // Subject, a blob in the centre
        //
        canvas.drawRoundRect( new RectF(MIDDLE_X-SUBJECT_SIZE,
        						        MIDDLE_Y-SUBJECT_SIZE,
        						        MIDDLE_X+SUBJECT_SIZE,
        						        MIDDLE_Y+SUBJECT_SIZE), 2.0f, 2.0f, brightPaint);

        // Calculate some useful values for placing the numeric values and their separator lines
        // These are labelled "thirds", but in fact it works better if I use quarters, making the
        // middle section bigger than the two side ones. :)
        //
        final int FOV_ONE_THIRD_WIDTH     = (FOV_RIGHT_SIDE-FOV_LEFT_SIDE)/4;
        final int FOV_ONE_THIRD           = FOV_LEFT_SIDE + FOV_ONE_THIRD_WIDTH;
        final int FOV_TWO_THIRDS          = FOV_LEFT_SIDE + (FOV_ONE_THIRD_WIDTH*3);
        
        // 2 vertical lines above FOV mark near limit, dof and far limit thirds
        //
        canvas.drawLine(FOV_ONE_THIRD,  FOV_TOP-V_LINE_SPACING, FOV_ONE_THIRD,  FOV_TOP-V_LINE_SPACING-V_LINE_HEIGHT, dimPaint);
        canvas.drawLine(FOV_TWO_THIRDS, FOV_TOP-V_LINE_SPACING, FOV_TWO_THIRDS, FOV_TOP-V_LINE_SPACING-V_LINE_HEIGHT, dimPaint);
        
        // Vertical lines below FOV mark in front of and behind subject ranges
        //
        canvas.drawLine(FOV_ONE_THIRD,  FOV_BOTTOM+V_LINE_SPACING, FOV_ONE_THIRD,  FOV_BOTTOM+V_LINE_SPACING+V_LINE_HEIGHT, dimPaint);
        canvas.drawLine(FOV_TWO_THIRDS, FOV_BOTTOM+V_LINE_SPACING, FOV_TWO_THIRDS, FOV_BOTTOM+V_LINE_SPACING+V_LINE_HEIGHT, dimPaint);
        
        // Vertical line below the subject
        //
        canvas.drawLine(MIDDLE_X, MIDDLE_Y+(SUBJECT_SIZE/2)+V_LINE_SPACING, MIDDLE_X, FOV_BOTTOM+V_LINE_SPACING+V_LINE_HEIGHT, dimPaint);
        
        // Text strings - all infinity until the model supplies a real value
        //
        String infinityStr           = this.getContext().getString(R.string.infinity);
        
        String nearLimitStr          = infinityStr;
        String dofStr                = infinityStr;
        String farLimitStr           = infinityStr;
        String inFrontStr            = infinityStr;
        String behindStr             = infinityStr;
        String hyperfocalDistanceStr = infinityStr;
        String hyperfocalMinStr      = infinityStr;

        if ( nearLimit != null )
        	nearLimitStr = String.format("%.2f%s", nearLimit, getUnitsAbbreviation());
        	
        if ( dof != null )
        	dofStr       = String.format("%.2f%s", dof, getUnitsAbbreviation());
        
        if (farLimit != null )
        	farLimitStr  = String.format("%.2f%s", farLimit, getUnitsAbbreviation());

        if ( inFront != null )
        	inFrontStr   = String.format("%.2f%s", inFront, getUnitsAbbreviation());
        
        if ( behind != null )
        	behindStr    = String.format("%.2f%s", behind, getUnitsAbbreviation());
        
        if ( hyperfocalDistance != null )
        	hyperfocalDistanceStr = String.format("%.2f%s", hyperfocalDistance, getUnitsAbbreviation());
        
        if ( hyperfocalMin != null )
        	hyperfocalMinStr      = String.format("%.2f%s", hyperfocalMin, getUnitsAbbreviation());

        // Display the text values
        //
        arrowedString( canvas, nearLimitStr,
        		       FOV_LEFT_SIDE, FOV_ONE_THIRD, FOV_TOP-V_LINE_SPACING-(V_LINE_HEIGHT/2), brightPaint,
        			   false, true, dimPaint );

    	arrowedString( canvas, dofStr,
    			       FOV_ONE_THIRD, FOV_TWO_THIRDS, FOV_TOP-V_LINE_SPACING-(V_LINE_HEIGHT/2), brightPaint,
    			       true, true, dimPaint );

    	arrowedString( canvas, farLimitStr,
    			       FOV_TWO_THIRDS, FOV_RIGHT_SIDE, FOV_TOP-V_LINE_SPACING-(V_LINE_HEIGHT/2), brightPaint,
    			       true, false, dimPaint );
    	
    	arrowedString( canvas, inFrontStr,
    			       FOV_ONE_THIRD, MIDDLE_X, FOV_BOTTOM+V_LINE_SPACING+(V_LINE_HEIGHT/2), brightPaint,
    			       true, true, dimPaint );

    	arrowedString( canvas, behindStr,
    			       MIDDLE_X, FOV_TWO_THIRDS, FOV_BOTTOM+V_LINE_SPACING+(V_LINE_HEIGHT/2), brightPaint,
    			       true, true, dimPaint );

    	// Hyperfocal distance bits at the bottom
    	//
    	final int HYPERFOCAL_Y = MIDDLE_Y+(SUBJECT_SIZE/2)+(V_LINE_SPACING*3)+V_LINE_HEIGHT; 
    	
        // Another vertical line below the one third point
        //
        canvas.drawLine(FOV_ONE_THIRD, HYPERFOCAL_Y, FOV_ONE_THIRD, HYPERFOCAL_Y+V_LINE_HEIGHT, dimPaint);
        
        // Hyperfocal minimum distance
        //
        arrowedString( canvas, hyperfocalMinStr,
        		       FOV_LEFT_SIDE, FOV_ONE_THIRD, HYPERFOCAL_Y+(V_LINE_HEIGHT/2), brightPaint,
        			   false, true, dimPaint );

        // Hyperfocal distance (with left side arrow)
        //
        arrowedString( canvas, hyperfocalDistanceStr,
        		       FOV_ONE_THIRD, FOV_TWO_THIRDS, HYPERFOCAL_Y+(V_LINE_HEIGHT/2), brightPaint,
        			   true, false, dimPaint );

    	// Infinity symbol on far right side
    	//
    	Rect textRect = new Rect();
        brightPaint.getTextBounds("W", 0, 1, textRect); // Width of \u22e1 is 5 pixels, which is wrong! Puzzlement. Use a W instead.
        int infinityTextLeftSide = FOV_RIGHT_SIDE-textRect.width(); 
        canvas.drawText(getContext().getString(R.string.infinity), infinityTextLeftSide, HYPERFOCAL_Y+(V_LINE_HEIGHT/2)+(textRect.height()/2), brightPaint);
        infinityTextLeftSide -= ARROW_SPACING;
        
        // Hyperfocal distance string size
        //
        textRect = new Rect();
        dimPaint.getTextBounds(hyperfocalDistanceStr, 0, hyperfocalDistanceStr.length(), textRect);
        
        // Line from right side of hyperfocal distance string to the infinity sign, with arrow
        //
        canvas.drawLine(MIDDLE_X+((textRect.width())/2)+ARROW_SPACING, HYPERFOCAL_Y+(V_LINE_HEIGHT/2),
        		        infinityTextLeftSide, HYPERFOCAL_Y+(V_LINE_HEIGHT/2), dimPaint);
		Path rightArrowHead = new Path();
		rightArrowHead.moveTo(infinityTextLeftSide, HYPERFOCAL_Y+(V_LINE_HEIGHT/2));
		rightArrowHead.lineTo(infinityTextLeftSide-WIDTH_ARROW_HEAD, HYPERFOCAL_Y+(V_LINE_HEIGHT/2)+HEIGHT_ARROW_HEAD);
		rightArrowHead.lineTo(infinityTextLeftSide-WIDTH_ARROW_HEAD, HYPERFOCAL_Y+(V_LINE_HEIGHT/2)-HEIGHT_ARROW_HEAD);
		rightArrowHead.lineTo(infinityTextLeftSide-ARROW_SPACING, HYPERFOCAL_Y+(V_LINE_HEIGHT/2));
		canvas.drawPath(rightArrowHead, dimPaint);
    }
	
    /**
     * Draw a line with a text string in the middle and optional left and right side arrows
     * 
     * @param canvas
     * @param str
     * @param leftX
     * @param rightX
     * @param centreY
     * @param textPaint
     * @param leftArrow
     * @param rightArrow
     * @param arrowPaint
     */
	protected void arrowedString( Canvas canvas, String str,
    		                      int leftX, int rightX, int centreY, Paint textPaint,
    						      boolean leftArrow, boolean rightArrow, Paint arrowPaint )
    {
    	Rect textRect = new Rect();
        textPaint.getTextBounds(str, 0, str.length(), textRect);
        
        // drawText() draws the text with baseline on the given y-coordinate.
        // To make the text's horizontal centre line the y-coordinate I need
        // to move the text screen location down half the text's height.
        //
        int textCentreX = ((rightX-leftX)/2) + leftX;
        int xPos        = textCentreX-textRect.centerX();
        int yPos        = centreY+(textRect.height()/2);
    	canvas.drawText(str, xPos, yPos, textPaint);
    	
    	if ( leftArrow ) {
    		canvas.drawLine(leftX+ARROW_SPACING, centreY, textCentreX-(textRect.width()/2)-ARROW_SPACING, centreY, arrowPaint);
    		Path leftArrowHead = new Path();
    		leftArrowHead.moveTo(leftX+ARROW_SPACING, centreY);
    		leftArrowHead.lineTo(leftX+ARROW_SPACING+WIDTH_ARROW_HEAD, centreY+HEIGHT_ARROW_HEAD);
    		leftArrowHead.lineTo(leftX+ARROW_SPACING+WIDTH_ARROW_HEAD, centreY-HEIGHT_ARROW_HEAD);
    		leftArrowHead.lineTo(leftX+ARROW_SPACING, centreY);
    		canvas.drawPath(leftArrowHead, arrowPaint);
    	}

    	if ( rightArrow ) {
    		canvas.drawLine(textCentreX+(textRect.width()/2)+ARROW_SPACING, centreY, rightX-ARROW_SPACING, centreY, arrowPaint);
    		Path rightArrowHead = new Path();
    		rightArrowHead.moveTo(rightX-ARROW_SPACING, centreY);
    		rightArrowHead.lineTo(rightX-ARROW_SPACING-WIDTH_ARROW_HEAD, centreY+HEIGHT_ARROW_HEAD);
    		rightArrowHead.lineTo(rightX-ARROW_SPACING-WIDTH_ARROW_HEAD, centreY-HEIGHT_ARROW_HEAD);
    		rightArrowHead.lineTo(rightX-ARROW_SPACING, centreY);
    		canvas.drawPath(rightArrowHead, arrowPaint);
    	}
    }
    
}
