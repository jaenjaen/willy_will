package com.willy.will.common.controller;

import android.view.View;

public interface ListViewHolder<T> {

    void setView(int position, View convertView);
    void bindData(T data, boolean selected);

}
