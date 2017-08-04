package com.scwang.refreshlayout.activity.using;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.scwang.refreshlayout.R;
import com.scwang.refreshlayout.adapter.BaseRecyclerAdapter;
import com.scwang.refreshlayout.adapter.SmartViewHolder;

import java.util.Arrays;
import java.util.Collection;

/**
 * 结合 SnapHelper 使用
 * Created by SCWANG on 2017/8/4.
 */

public class SnapHelperUsingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_using_snaphelper);

        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //初始化列表和监听
        View view = findViewById(R.id.recyclerView);
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(new BaseRecyclerAdapter<Integer>(loadModels(), R.layout.listitem_using_snaphelper) {
                @Override
                protected void onBindViewHolder(SmartViewHolder holder, Integer model, int position) {
                    holder.image(R.id.imageView, model);
                }
            });
            LinearSnapHelper mMySnapHelper = new LinearSnapHelper();
            mMySnapHelper.attachToRecyclerView(recyclerView);
        }

    }

    private Collection<Integer> loadModels() {
        return Arrays.asList(R.mipmap.ic_item1, R.mipmap.ic_item2, R.mipmap.ic_item3, R.mipmap.ic_item1);
    }


    public class MySnapHelper extends LinearSnapHelper {

        private OrientationHelper mHorizontalHelper;

        @Nullable
        @Override
        public int[] calculateDistanceToFinalSnap(RecyclerView.LayoutManager layoutManager, View targetView) {
            int[] out = new int[2];
            if (layoutManager.canScrollHorizontally()) {
                out[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager));
            } else {
                out[0] = 0;
            }

            return out;
        }

        private int distanceToStart(View targetView, OrientationHelper helper) {
            return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
        }

        @Nullable
        @Override
        public View findSnapView(RecyclerView.LayoutManager layoutManager) {
            return findStartView(layoutManager, getHorizontalHelper(layoutManager));
        }

        private View findStartView(RecyclerView.LayoutManager layoutManager,
                                   OrientationHelper helper) {

            if (layoutManager instanceof LinearLayoutManager) {
                int firstChild = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                int lastChild = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                if (firstChild == RecyclerView.NO_POSITION) {
                    return null;
                }
                if (lastChild == layoutManager.getItemCount() - 1) {
                    return layoutManager.findViewByPosition(lastChild);
                }

                View child = layoutManager.findViewByPosition(firstChild);

                if (helper.getDecoratedEnd(child) >= helper.getDecoratedMeasurement(child) / 2
                        && helper.getDecoratedEnd(child) > 0) {
                    return child;
                } else {
                    return layoutManager.findViewByPosition(firstChild + 1);
                }
            }

            return super.findSnapView(layoutManager);
        }


        private OrientationHelper getHorizontalHelper(
                @NonNull RecyclerView.LayoutManager layoutManager) {
            if (mHorizontalHelper == null) {
                mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
            }
            return mHorizontalHelper;
        }
    }

}