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

package org.egov.employee.activity;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import offices.org.egov.egovemployees.R;

public class SearchableActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    CoordinatorLayout rootLayout;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rootLayout=(CoordinatorLayout)findViewById(R.id.rootLayout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.DKGRAY);
            startAnimation(savedInstanceState);
        }


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startAnimation(Bundle savedInstanceState)
    {
            final int xyLocations[]= getIntent().getIntArrayExtra("xyLocations");
            if (savedInstanceState == null) {
                rootLayout.setVisibility(View.INVISIBLE);
                final ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
                if (viewTreeObserver.isAlive()) {

                    final ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            circularRevealActivity(xyLocations[0], xyLocations[1]);
                            rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    };
                    viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener);
                }
            }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void circularRevealActivity(int cx, int cy) {

        if(cx==0 && cy==0) {
            //center of the layout finding
            cx = rootLayout.getWidth() / 2;
            cy = rootLayout.getHeight() / 2;
        }

        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        circularReveal.setDuration(700);

        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_searchactivity, menu);
        MenuItem searchItem=menu.findItem(R.id.action_search);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return false;
            }
        });

        searchItem.expandActionView();
        searchView=(SearchView)menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    protected int getLayoutResource() {
        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
        return R.layout.activity_searchable;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return true;
    }
}
