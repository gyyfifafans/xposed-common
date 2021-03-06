/*
 * Copyright (c) 2018 The sky Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sky.xposed.common.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.sky.xposed.common.ui.util.LayoutUtil;
import com.sky.xposed.common.ui.util.ViewUtil;
import com.sky.xposed.common.util.DisplayUtil;

/**
 * Created by sky on 2018/8/8.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class SwitchItemView extends XFrameItemView<Boolean> implements View.OnClickListener {

    private TextView tvName;
    private TextView tvDesc;
    private Switch mSwitch;
    private OnCheckedChangeListener mOnCheckedChangeListener;

    public SwitchItemView(Context context) {
        super(context);
    }

    public SwitchItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return mOnCheckedChangeListener;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener;
    }

    @Override
    protected void initView() {

        int left = DisplayUtil.dip2px(getContext(), 15);

        setPadding(left, 0, left, 0);
        setBackground(ViewUtil.newBackgroundDrawable());
        setLayoutParams(LayoutUtil.newViewGroupParams(
                LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(getContext(), 40)));

        LinearLayout tvLayout = new LinearLayout(getContext());
        tvLayout.setOrientation(LinearLayout.VERTICAL);

        tvName = new TextView(getContext());
        tvName.setTextColor(Color.BLACK);
        tvName.setTextSize(15);

        tvDesc = new TextView(getContext());
        tvDesc.setTextColor(Color.GRAY);
        tvDesc.setTextSize(9);
        tvDesc.setPadding(DisplayUtil.dip2px(getContext(), 1), 0, 0, 0);

        tvLayout.addView(tvName);
        tvLayout.addView(tvDesc);

        FrameLayout.LayoutParams params = LayoutUtil.newWrapFrameLayoutParams();
        params.gravity = Gravity.CENTER_VERTICAL;

        addView(tvLayout, params);

        mSwitch = new Switch(getContext());
        mSwitch.setClickable(false);
        mSwitch.setFocusable(false);
        mSwitch.setFocusableInTouchMode(false);

        params = LayoutUtil.newWrapFrameLayoutParams();
        params.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;

        addView(mSwitch, params);

        setOnClickListener(this);
    }

    public TextView getNameView() {
        return tvName;
    }

    public TextView getDescView() {
        return tvDesc;
    }

    public void setName(String title) {
        tvName.setText(title);
    }

    public String getName() {
        return tvName.getText().toString();
    }

    public void setDesc(String desc) {
        tvDesc.setText(desc);
        ViewUtil.setVisibility(tvDesc,
                TextUtils.isEmpty(desc) ? View.GONE : View.VISIBLE);
    }

    public String getDesc() {
        return tvDesc.getText().toString();
    }

    public void setChecked(boolean checked) {
        mSwitch.setChecked(checked);
        tvName.setTextColor(isChecked() ? Color.BLACK : Color.GRAY);
    }

    public boolean isChecked() {
        return mSwitch.isChecked();
    }

    @Override
    public void onClick(View v) {

        setChecked(!isChecked());

        if (mOnCheckedChangeListener != null)
            mOnCheckedChangeListener.onCheckedChanged(this, isChecked());
    }

    @Override
    protected void bindView() {

        // 设置状态
        setChecked(getKeyValue());
        setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View view, boolean isChecked) {

                if (mStatusChangeListener == null) {
                    // 保存状态信息
                    mPreferences.putBoolean(mKey, isChecked);
                    return;
                }

                if (mStatusChangeListener.onStatusChange(view, mKey, isChecked)) {
                    // 保存状态信息
                    mPreferences.putBoolean(mKey, isChecked);
                }
            }
        });
    }

    @Override
    public Boolean getKeyValue() {
        return mPreferences.getBoolean(mKey, mDefValue);
    }

    public interface OnCheckedChangeListener {

        void onCheckedChanged(View view, boolean isChecked);
    }
}
