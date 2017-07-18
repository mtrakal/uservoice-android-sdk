package com.uservoice.uservoicesdk.activity;

import android.annotation.SuppressLint;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.uservoice.uservoicesdk.R;
import com.uservoice.uservoicesdk.compatibility.FragmentListActivity;
import com.uservoice.uservoicesdk.ui.MixedSearchAdapter;
import com.uservoice.uservoicesdk.ui.PortalAdapter;
import com.uservoice.uservoicesdk.ui.SearchAdapter;
import com.uservoice.uservoicesdk.ui.SearchExpandListener;
import com.uservoice.uservoicesdk.ui.SearchQueryListener;

import java.util.Locale;

@SuppressLint("NewApi")
public abstract class SearchActivity extends FragmentListActivity {
    private int originalNavigationMode = -1;

    private TabLayout tabLayout;
    private TabLayout.Tab allTab;
    private TabLayout.Tab articlesTab;
    private TabLayout.Tab ideasTab;

    public SearchAdapter<?> getSearchAdapter() {
        return searchAdapter;
    }

    public void updateScopedSearch(int results, int articleResults, int ideaResults) {
        if (hasActionBar()) {
            allTab.setText(String.format(Locale.getDefault(), "%s (%d)", getString(R.string.uv_all_results_filter), results));
            articlesTab.setText(String.format(Locale.getDefault(), "%s (%d)", getString(R.string.uv_articles_filter), articleResults));
            ideasTab.setText(String.format(Locale.getDefault(), "%s (%d)", getString(R.string.uv_ideas_filter), ideaResults));
        }
    }

    public void showSearch() {
        ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.uv_view_flipper);
        viewFlipper.setDisplayedChild(1);
        if (hasActionBar()) {
            if (originalNavigationMode == -1)
                originalNavigationMode = actionBar.getNavigationMode();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }
    }

    public void hideSearch() {
        ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.uv_view_flipper);
        viewFlipper.setDisplayedChild(0);
        if (hasActionBar()) {
            actionBar.setNavigationMode(originalNavigationMode == -1 ? ActionBar.NAVIGATION_MODE_STANDARD : originalNavigationMode);
        }
    }

    protected void setupScopedSearch(Menu menu) {
        MenuItem searchItem = menu.findItem(R.id.uv_action_search);
        if (hasActionBar()) {
            MenuItemCompat.setOnActionExpandListener(searchItem, new SearchExpandListener(this));
            SearchView search = (SearchView) MenuItemCompat.getActionView(searchItem);
            search.setOnQueryTextListener(new SearchQueryListener(this));
            searchAdapter = new MixedSearchAdapter(this);
            ListView searchView = new ListView(this);
            searchView.setAdapter(searchAdapter);
            searchView.setOnItemClickListener(searchAdapter);

            // ensure that the viewflipper is set up
            getListView();

            ViewFlipper viewFlipper = (ViewFlipper) findViewById(R.id.uv_view_flipper);
            viewFlipper.addView(searchView, 1);

            TabLayout.OnTabSelectedListener listener = new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    searchAdapter.setScope((Integer) tab.getTag());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            };

            tabLayout = new TabLayout(this);
            // TODO: 18.07.2017 add tablayout to actionBar :/. Need to use toolbar and not old actionBar
            tabLayout.addOnTabSelectedListener(listener);

            allTab = tabLayout.newTab().setText(getString(R.string.uv_all_results_filter)).setTag(PortalAdapter.SCOPE_ALL);
            tabLayout.addTab(allTab);
            articlesTab = tabLayout.newTab().setText(getString(R.string.uv_articles_filter)).setTag(PortalAdapter.SCOPE_ARTICLES);
            tabLayout.addTab(articlesTab);
            ideasTab = tabLayout.newTab().setText(getString(R.string.uv_ideas_filter)).setTag(PortalAdapter.SCOPE_IDEAS);
            tabLayout.addTab(ideasTab);
        } else {
            searchItem.setVisible(false);
        }
    }
}
