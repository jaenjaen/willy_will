package com.willy.will.database;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.willy.will.R;
import com.willy.will.common.model.ToDoItem;

import java.util.ArrayList;

public class ToDoItemDBController {

    private Resources resources;
    private SQLiteDatabase readDatabase;
    private SQLiteDatabase writeDatabase;

    public ToDoItemDBController(Resources resources) {
        this.resources = resources;
        readDatabase = DBAccess.getDbHelper().getReadableDatabase();
        writeDatabase = DBAccess.getDbHelper().getWritableDatabase();
    }

    public ArrayList<ToDoItem> searchToDoItems(ArrayList<ToDoItem> toDoItemList, String tempTable) {
        /** Initialize to-do item list **/
        if(toDoItemList == null) {
            toDoItemList = new ArrayList<>();
        }
        /* ~Initialize to-do item list */

        /** Read DB **/
        // Set columns
        String[] columns = {
                "*",
                resources.getString(R.string.done_column),
                resources.getString(R.string.loop_column)
        };

        // Set a column for GROUP BY
        String groupBy = resources.getString(R.string.to_do_id_column);
        // Set HAVING
        String having = "max(" + resources.getString(R.string.item_id_column) + ")";

        // Set order
        String order = resources.getString(R.string.to_do_item_order);

        Cursor cursor = readDatabase.query(
                tempTable, columns,
                null, null,
                groupBy, having,
                order);
        /* ~Read DB */

        /** Put data in ArrayList **/
        ToDoItem curToDoItem = null;
        int itemId = -1;
        int groupId = -1;
        String doneDate = null;
        boolean done = false;
        String endDate = null;
        int toDoId = -1;
        int rank = -1;
        String name = null;
        while(cursor.moveToNext()) {
            itemId = cursor.getInt(cursor.getColumnIndexOrThrow(resources.getString(R.string.item_id_column)));
            groupId = cursor.getInt(cursor.getColumnIndexOrThrow(resources.getString(R.string.group_id_column)));
            doneDate = cursor.getString(cursor.getColumnIndexOrThrow(resources.getString(R.string.done_date_column)));
            if(doneDate != null) {
                done = true;
            }
            else {
                done = false;
            }
            endDate = cursor.getString(cursor.getColumnIndexOrThrow(resources.getString(R.string.end_date_column)));
            toDoId = cursor.getInt(cursor.getColumnIndexOrThrow(resources.getString(R.string.to_do_id_column)));
            rank = cursor.getInt(cursor.getColumnIndexOrThrow(resources.getString(R.string.item_important_column)));
            name = cursor.getString(cursor.getColumnIndexOrThrow(resources.getString(R.string.item_name_column)));

            curToDoItem = new ToDoItem(itemId, groupId, doneDate, done, endDate, toDoId, rank, name);
            toDoItemList.add(curToDoItem);
        }
        /* ~Put data in ArrayList */

        return toDoItemList;
    }

}