/*
 * ******************************************************************************
 *  eGov suite of products aim to improve the internal efficiency,transparency,
 *      accountability and the service delivery of the government  organizations.
 *
 *        Copyright (C) <2016>  eGovernments Foundation
 *
 *        The updated version of eGov suite of products as by eGovernments Foundation
 *        is available at http://www.egovernments.org
 *
 *        This program is free software: you can redistribute it and/or modify
 *        it under the terms of the GNU General Public License as published by
 *        the Free Software Foundation, either version 3 of the License, or
 *        any later version.
 *
 *        This program is distributed in the hope that it will be useful,
 *        but WITHOUT ANY WARRANTY; without even the implied warranty of
 *        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *        GNU General Public License for more details.
 *
 *        You should have received a copy of the GNU General Public License
 *        along with this program. If not, see http://www.gnu.org/licenses/ or
 *        http://www.gnu.org/licenses/gpl.html .
 *
 *        In addition to the terms of the GPL license to be adhered to in using this
 *        program, the following additional terms are to be complied with:
 *
 *    	1) All versions of this program, verbatim or modified must carry this
 *    	   Legal Notice.
 *
 *    	2) Any misrepresentation of the origin of the material is prohibited. It
 *    	   is required that all modified versions of this material be marked in
 *    	   reasonable ways as different from the original version.
 *
 *    	3) This license does not grant any rights to any user of the program
 *    	   with regards to rights under trademark law for use of the trade names
 *    	   or trademarks of eGovernments Foundation.
 *
 *      In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *  *****************************************************************************
 */

package org.egov.employee.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import offices.org.egov.egovemployees.R;

/**
 * Created by egov on 23/9/16.
 */
public class PinView extends View {

    Context context;

    int circleStartY=66;
    int circleRadius=8;

    int lineStartY=70;
    int lineEndY=100;

    Boolean isDragging=false;

    private static final String titleText="Pick Your Complaint Location";
    private static final String loadingAddressText="Getting address...";
    private String addressText=loadingAddressText;


    Paint paintLine;
    Paint paintCircle;
    Paint paintDottedLine;
    TextPaint textPaint;
    Path pathDown;
    Paint paintBox;
    Path path;
    RectF ovalBottom;

    private void init()
    {
        paintLine = new Paint();
        paintLine.setAntiAlias(true);
        paintLine.setColor(ContextCompat.getColor(context, R.color.pinviewlinecolor));
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(2f);

        paintCircle = new Paint();
        paintCircle.setStyle(Paint.Style.FILL);
        paintCircle.setColor(ContextCompat.getColor(context,R.color.pinviewlinecolor));

        paintBox = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBox.setShadowLayer(5, 0, 0, ContextCompat.getColor(context,R.color.pinviewlinecolor));
        paintBox.setColor(ContextCompat.getColor(context,android.R.color.white));

        paintDottedLine = new Paint();
        paintDottedLine.setStyle(Paint.Style.STROKE);
        paintDottedLine.setStrokeWidth(2f);
        paintDottedLine.setColor(ContextCompat.getColor(context, R.color.pinviewlinecolor));
        paintDottedLine.setPathEffect(new DashPathEffect(new float[]{4, 2, 4, 2}, 0));

        path = new Path();
        pathDown = new Path();

        textPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(DpToPixels(12));
        textPaint.setLinearText(true);

        ovalBottom = new RectF(0, -DpToPixels(2), DpToPixels(10), DpToPixels(2));

    }

    public PinView(Context context) {
        this(context, null);
        this.context=context;
        init();
    }

    public PinView(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context=context;
        init();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Important for certain APIs
        setLayerType(LAYER_TYPE_SOFTWARE, paintBox);

        int centerX = (canvas.getWidth() / 2);
        /*int centerY = (int) ((canvas.getHeight() / 2) - ((paintLine.descent() + paintLine.ascent()) / 2)) ;*/

        path.moveTo(centerX, DpToPixels(20));
        path.lineTo(centerX, DpToPixels(66));
        canvas.drawPath(path, paintDottedLine);

        canvas.drawRect(0, 0, DpToPixels(200), DpToPixels(40), paintBox);

        textPaint.setColor(ContextCompat.getColor(context, R.color.colorAccent));
        canvas.drawText(titleText, DpToPixels(10), DpToPixels(15), textPaint);

        textPaint.setColor(Color.BLACK);
        CharSequence txt = TextUtils.ellipsize(addressText, textPaint, DpToPixels(170), TextUtils.TruncateAt.END);
        canvas.drawText(txt, 0, txt.length(), DpToPixels(10), DpToPixels(30), textPaint);

        if(!isDragging) {
            canvas.drawLine(centerX, DpToPixels(lineStartY), centerX, DpToPixels(lineEndY), paintLine);
            //draw bottom circle
            ovalBottom.offset(centerX-DpToPixels(5), DpToPixels(lineEndY));
            canvas.drawOval(ovalBottom, paintCircle);
        }
        else {
            //on drag draw dotted line
            pathDown.moveTo(centerX, DpToPixels(lineStartY));
            pathDown.lineTo(centerX, DpToPixels(lineEndY));
            canvas.drawPath(pathDown, paintDottedLine);
            canvas.drawLine(DpToPixels(90), DpToPixels(95), DpToPixels(110), DpToPixels(104), paintLine);
            canvas.drawLine(DpToPixels(110), DpToPixels(95), DpToPixels(90), DpToPixels(104), paintLine);
        }

        paintCircle.setColor(ContextCompat.getColor(context,R.color.colorAccent));
        canvas.drawCircle(centerX, DpToPixels(circleStartY), DpToPixels(circleRadius), paintCircle);
        canvas.drawCircle(centerX, DpToPixels(circleStartY), DpToPixels(circleRadius), paintLine);

    }

    public void setDragging(Boolean dragging) {
        isDragging = dragging;
        addressText=loadingAddressText;
        invalidate();
    }

    public Boolean isDragging() {
        return isDragging;
    }

    public void setAddressText(String addressText) {
        this.addressText = addressText;
        invalidate();
    }

    public String getAddressText() {
        return (addressText.equals(loadingAddressText)?"":addressText);
    }

    float DpToPixels(int dp)
    {
        return dp * getResources().getDisplayMetrics().density;
    }

    @Override
    public void invalidate() {
        init();
        super.invalidate();
    }
}
