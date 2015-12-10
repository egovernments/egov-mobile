package org.egov.android.view.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class EGovAutoCompleteTextView extends AutoCompleteTextView {

public EGovAutoCompleteTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
}
	
public EGovAutoCompleteTextView(Context context) {
     super(context);
}

@Override
protected void performFiltering(CharSequence text, int keyCode) {           
// nothing, block the default auto complete behavior
}


}
