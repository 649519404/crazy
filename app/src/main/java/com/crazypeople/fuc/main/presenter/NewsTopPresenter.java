package com.crazypeople.fuc.main.presenter;

import com.crazypeople.common.base.basePresenter.BasePresenter;
import com.crazypeople.common.inter.HttpResult;
import com.crazypeople.common.sub.SubPro;
import com.crazypeople.common.sub.SubscriberOnNextListener;
import com.crazypeople.fuc.main.entity.DataBean;
import com.crazypeople.fuc.main.view.MainView;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class NewsTopPresenter<T> extends BasePresenter implements SubscriberOnNextListener<List<T>>{

private MainView mMainView;
private Observable<HttpResult<List<DataBean>>>observable;
    private SubPro<T> subscriber;

    @Inject
public NewsTopPresenter(MainView mainView){
        mMainView=mainView;
        }

public void getAll(){
        observable=connectManager.getAll("1");
        subscriber=new SubPro<T>(this);
        requestDate(observable, RequestMode.FRIST,subscriber);

    }


    @Override
    public void onNext(HttpResult<List<T>> entity) {
        if (null != entity) {
            if (null != entity.getData()) {
                List<T> data = entity.getData();
                if (null != data && data.size() > 0) {
                    if (mode == RequestMode.FRIST) {
                        mMainView.showFinishDates(data);
                    } else if (mode == RequestMode.LOAD_MORE) {
                        mMainView.loadMoreFinish(data);
                    } else if (mode == RequestMode.REFRESH) {
                        mMainView.showRefreshFinish(data);
                    }
                } else {
                    if (mode == RequestMode.LOAD_MORE) {
                        mMainView.hasNoMoreDate();
                    } else {
                        mMainView.showEmptyView(null);
                    }
                }
            } else {
                mMainView.showEmptyView(null);
            }
        } else {
            mMainView.showEmptyView(null);
        }
    }
    @Override
    public void onFail() {
        if(mode==RequestMode.FRIST){
            mMainView.showNetError(observable,subscriber);
        }else{
            mMainView.showToastError();
        }
    }

    @Override
    public void onCompleted() {

    }
}