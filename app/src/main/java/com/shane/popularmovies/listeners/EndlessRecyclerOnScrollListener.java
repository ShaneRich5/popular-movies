package com.shane.popularmovies.listeners;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Disclaimer: A lot of the code from this class came from stackoverflow and a github wiki tutorial
 * (linked below) that I came across while researching the problem.
 *
 * https://github.com/codepath/android_guides/wiki/Endless-Scrolling-with-AdapterViews-and-RecyclerView
 */

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 5;
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    private int currentPage = 1;

    private GridLayoutManager gridLayoutManager;

    public EndlessRecyclerOnScrollListener(GridLayoutManager manager) {
        this.gridLayoutManager = manager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = gridLayoutManager.getItemCount();
        firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {

            currentPage++;

            onLoadMore(currentPage);

            loading = true;
        }
    }

    public void setCurrentPage(int page) {
        this.currentPage = page;
    }

    public void reset() {
        previousTotal = 0;
        loading = true;
        visibleThreshold = 1;
        firstVisibleItem = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
        currentPage = 1;
    }

    public abstract void onLoadMore(int currentPage);
}
