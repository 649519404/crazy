package com.crazypeople.common.base.baseFragment;

import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.crazypeople.R;
import com.crazypeople.common.base.baseAdapter.RecycleViewAdapter;
import com.crazypeople.common.base.baseHolder.BaseViewHolder;
import com.crazypeople.common.base.basePresenter.BasePresenter;
import com.crazypeople.common.refresh.ProgressStyle;
import com.crazypeople.common.refresh.XRecyclerView;
import com.crazypeople.fuc.main.view.BaseListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class BaseListFragment<T extends BasePresenter, K> extends
        BaseFragment<T> implements
        BaseListView<K>, XRecyclerView.LoadingListener {

    protected XRecyclerView mRecyclerView;

    protected RecycleViewAdapter<K> mAdapter;

    protected Map<String, String> params;

    protected int PAGE = 1;

    @Override
    protected int getContentLayout() {
        return R.layout.fragment_main_common;
    }

    @Override
    public void baseInit() {
        params = new HashMap<>();
        showLoading();
        initData(PAGE, BasePresenter.RequestMode.FRIST);
            addHeader();
    }




    @Override
    protected void baseInitView() {
        mRecyclerView = (XRecyclerView) mRootView.findViewById(R.id.recycleView);

    }


    protected void addHeader() {



    }


    @Override
    protected abstract Map<String, String> getRequestParams();

    @Override
    protected View getLoadingTargetView() {
        return mRecyclerView;
    }


    @Override
    public void showFinishDates(List<K> dates) {
        refreshView();
        if (mAdapter == null) {
            mAdapter = new RecycleViewAdapter<K>(getItemLayout(), dates) {
                @Override
                protected void convert(BaseViewHolder helper, K item) {
                    fitDates(helper, item);
                }
            };

            mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));
            mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
            mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
            mRecyclerView.setArrowImageView(R.mipmap.iconfont_downgrey);
            mRecyclerView.setLoadingListener(BaseListFragment.this);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
        mAdapter.setOnRecyclerViewItemChildClickListener(
                (RecycleViewAdapter adapter, View v,int position) -> onItemClick(v,position));
    }

    protected void onItemClick(View v, int position) {

    }

    @Override
    public void hasNoMoreDate() {
        mRecyclerView.setNoMore(true);
        PAGE = PAGE - 1;
    }

    protected abstract void fitDates(BaseViewHolder helper, K item);
    protected abstract void initData(int page, BasePresenter.RequestMode mode);
    protected abstract int getItemLayout();

    @Override
    public void loadMoreFinish(List dates) {
        mRecyclerView.loadMoreComplete();
        mAdapter.addDataAndNotify(dates);
    }

    @Override
    public void showRefreshFinish(List score) {
        mRecyclerView.refreshComplete();
        mAdapter.setNewData(score);
    }

    @Override
    public void showToastError() {
        mRecyclerView.reset();
        PAGE = PAGE - 1;
        Toast.makeText(mContext, "网络环境不好", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showNetError() {
        if (mVaryViewHelperController == null) {
            throw new IllegalStateException("no ViewHelperController");
        }
        mVaryViewHelperController.showNetworkError(v -> {
            showLoading();
            initData(PAGE, BasePresenter.RequestMode.FRIST);

        });
    }

    @Override
    public void onRefresh() {
        PAGE = 1;
        initData(PAGE, BasePresenter.RequestMode.REFRESH);

    }

    @Override
    public void onLoadMore() {
        PAGE = PAGE + 1;
//        mPresenter.requestDate(getRequestParams(), BasePresenter.RequestMode.LOAD_MORE);
    }
}
