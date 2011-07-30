/**
 * Attempting to run this on my system crashes the JVM(!).
 * Tried with both OpenJDK and Sun's. :(

http://code.google.com/p/android/issues/detail?id=2271

 */
/*

Some test values:

20D f/4   17mm  3m   1.68m 13.9m 12.2m 3.82m
20D f/1.8 50mm  15m  12.5m 18.9m 6.4m  73.1m
20D f/5.6 200mm 25m  23.5m 26.8m 3.31m 376.1m

5D  f/9   180mm 100m 54.6m 594.6m 540.1m 120.2m

1D  f/16  70mm  10m  5.73m 39.3m 33.6m 13.4m

CoC 0.006 f/4 70mm 10m 9.54m 10.5m 0.98m 204.2m

 */

package org.derekfountain.dofc.m;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author derek
 *
 */
public class ModelTest {

    MVCModel testModel = new MVCModel( null, null, null );

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.derekfountain.dofc.m.MVCModel#stateChange(int, int, double)}.
	 */
	@Test
	public void testStateChange() {

	    //	    testModel.setBody( new Body("20D", (double)0.019) );
	    fail("Not yet implemented");
	}

}
