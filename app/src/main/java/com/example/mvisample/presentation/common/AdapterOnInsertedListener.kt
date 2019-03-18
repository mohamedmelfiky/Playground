package com.example.mvisample.presentation.common

import androidx.recyclerview.widget.RecyclerView

/**
 * This class is used to set the loading variable of the OnLoadMoreListener to false when
 * the data have been inserted because of the animation of the listAdapter
 */
class AdapterOnInsertedListener(private val loadMoreListener: OnLoadMoreListener) : RecyclerView.AdapterDataObserver() {

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        super.onItemRangeInserted(positionStart, itemCount)
        loadMoreListener.isLoading = false
    }

}