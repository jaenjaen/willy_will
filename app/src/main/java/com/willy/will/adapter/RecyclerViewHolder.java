package com.willy.will.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import com.willy.will.R;
import com.willy.will.common.controller.App;
import com.willy.will.common.model.Group;
import com.willy.will.common.model.RecyclerViewItemType;
import com.willy.will.common.model.ToDoItem;
import com.willy.will.search.model.Distance;

public class RecyclerViewHolder extends RecyclerView.ViewHolder{

    private static BackgroundColorSpan inactiveColorSpan = new BackgroundColorSpan(App.getContext().getColor(R.color.colorInactive));
    private BackgroundColorSpan activeColorSpan = null;

    private RecyclerViewAdapter rcyclerVAdapter = null;

    // View of Item
    private TextView textOnlyView;

    private ImageView imgRank;
    private TextView tvName;
    private ImageView imgRoutine;
    private TextView tvTime;
    private CheckBox cbDone;
    // ~View of Item

    // Initialization of THE item (Called for each item)
    public <T> RecyclerViewHolder(RecyclerViewAdapter adapter, RecyclerViewItemType type, View view) {
        super(view);
        rcyclerVAdapter = adapter;
        Resources resources = view.getContext().getResources();

        // To-do
        if(type == RecyclerViewItemType.TO_DO) {
            tvTime = view.findViewById(R.id.tv_time);
            imgRank = view.findViewById(R.id.img_rank);
            tvName = view.findViewById(R.id.tv_name);
            imgRoutine = view.findViewById(R.id.img_routine);
            cbDone = view.findViewById(R.id.cb_done);

            activeColorSpan = new BackgroundColorSpan(resources.getColor(R.color.colorGroup7));
            cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if(cbDone.isPressed()) {
                        setActivation(b);
                        int position = Math.toIntExact((Long) getItemId());
                        ToDoItem toDoItem = (ToDoItem) rcyclerVAdapter.getData(position);
                        toDoItem.setDone(b);
                        rcyclerVAdapter.notifyItemChanged(position);
                    }
                }
            });
        }
        // Text-only (Group, Done, Distance)
        else if(type == RecyclerViewItemType.GROUP_SEARCH ||
                type == RecyclerViewItemType.DONE_SEARCH ||
                type == RecyclerViewItemType.DISTANCE_SEARCH) {
            textOnlyView = view.findViewById(R.id.text_recycler_item);
        }
        // ERROR: Wrong type
        else {
            Log.e("RecyclerViewHolder", "Initializing: Wrong type");
        }
    }

    // Get Item Details to build Tracker (Called for each item)
    public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
        ItemDetailsLookup.ItemDetails<Long> details = new ItemDetailsLookup.ItemDetails<Long>() {
            @Override
            public int getPosition() {
                return getAdapterPosition();
            }

            @Nullable
            @Override
            public Long getSelectionKey() {
                return getItemId();
            }
        };

        return details;
    }

    // Bind data to THE item (Called for each item)
    public <T> void bind(RecyclerViewItemType type, T data, boolean isSelected) {
        // To-do
        if(type == RecyclerViewItemType.TO_DO) {
            ToDoItem mitem = (ToDoItem) data;
            tvTime.setText(mitem.getEndDate());
            tvName.setText(mitem.getName());
            cbDone.setChecked(mitem.getDone());
            int rank = mitem.getRank();
            if(rank == 1) {
                imgRank.setImageDrawable(ResourcesCompat.getDrawable(App.getContext().getResources(),
                        R.drawable.important1, null));
            }
            else if(rank == 2) {
                imgRank.setImageDrawable(ResourcesCompat.getDrawable(App.getContext().getResources(),
                        R.drawable.important2, null));
            }
            else if(rank == 3) {
                imgRank.setImageDrawable(ResourcesCompat.getDrawable(App.getContext().getResources(),
                        R.drawable.important3, null));
            }
            else {
                imgRank.setImageDrawable(null);
            }
            //imgRoutine.setImageDrawable(mitem.getToDoId());
            setActivation(cbDone.isChecked());
        }
        // Group
        else if(type == RecyclerViewItemType.GROUP_SEARCH) {
            Group group = (Group) data;
            textOnlyView.setText(group.getGroupName());
            // Hide a ghost item for changing selection mode of multiple selection
            if(group.getGroupName().equals("")) {
                if(textOnlyView.getVisibility() != View.GONE) {
                    textOnlyView.setVisibility(View.GONE);
                }
            }
        }
        // Done
        else if(type == RecyclerViewItemType.DONE_SEARCH) {
            String text = (String) data;
            textOnlyView.setText(text);
        }
        // Distance
        else if(type == RecyclerViewItemType.DISTANCE_SEARCH) {
            Distance distance = (Distance) data;
            textOnlyView.setText(distance.getText());
        }
        // ERROR: Wrong type
        else {
            Log.e("RecyclerViewHolder", "Binding: Wrong type");
        }

        itemView.setSelected(isSelected);
    }

    // Activation of to-do item
    private void setActivation(boolean activated) {
        Context context = App.getContext();

        Spannable span = (Spannable) tvName.getText();

        if(activated) {
            span.setSpan(inactiveColorSpan
                    , 0, tvName.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            tvName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            tvTime.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            imgRoutine.getBackground().setTint(ContextCompat.getColor(context, R.color.colorInactive));
            imgRank.getDrawable().setTint(ContextCompat.getColor(context, R.color.colorInactive));
        }
        else {
            span.setSpan(activeColorSpan
                    , 0, tvName.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            tvName.setPaintFlags(0);
            tvTime.setPaintFlags(0);
            imgRoutine.getBackground().setTint(ContextCompat.getColor(context, R.color.colorPrimary));
            imgRank.getDrawable().setTint(ContextCompat.getColor(context, R.color.colorPrimary));
        }
    }

}
