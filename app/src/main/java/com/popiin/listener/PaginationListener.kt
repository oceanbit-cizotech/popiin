package com.popiin.listener

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationListener(private val layoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        if (!isLoading() && !isLastPage()) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                && firstVisibleItemPosition >= 0 /*&& totalItemCount >= PAGE_SIZE*/) {
                loadMoreItems()
            }
        }
    }

    protected abstract fun loadMoreItems()
    protected abstract fun isLastPage(): Boolean
    protected abstract fun isLoading(): Boolean

    companion object {
        const val PAGE_START = 1
        private const val PAGE_SIZE = 10
    }
}