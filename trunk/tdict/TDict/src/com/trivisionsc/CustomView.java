/**
 * Demonstrate the custom View created in {@link MyView}.
 * */

package com.trivisionsc;

import android.app.Activity;
import android.os.Bundle;

public class CustomView extends Activity {
	@Override
	public void onCreate(Bundle icicle) {
	  super.onCreate(icicle);

	  ViewForMenu view = new ViewForMenu(this);
	  
	  setContentView(view);	  
	}
	
}