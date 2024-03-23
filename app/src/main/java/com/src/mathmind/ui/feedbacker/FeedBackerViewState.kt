package com.src.mathmind.ui.feedbacker

sealed class FeedBackerViewState {

    //    private var status: ViewStatus = ViewStatus.NEW
    data object NEW : FeedBackerViewState()

    data object WAITING : FeedBackerViewState()

    data object EVALUATING : FeedBackerViewState()

    data object END : FeedBackerViewState()

}