package com.willy.will.detail.controller;

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.willy.will.database.ToDoItemDBController;
import com.willy.will.detail.model.Item;

import java.util.ArrayList;
import java.util.List;

import static com.willy.will.main.view.MainActivity.dbHelper;

public class DetailController {

    private SQLiteDatabase db;

    public DetailController() {
        db = dbHelper.getReadableDatabase();
    }

    /** get Item by itemId from DB **/
    public Item getToDoItemByItemId(int itemId){
        String selectQuery =
                "SELECT i.item_name, i.item_location_x, i.item_location_y, i.done_date, " +
                        "i.start_date, i.end_date, g.group_name, g.group_color, c.calendar_date, l.loop_week, i.item_id, i.to_do_id\n" +
                        "FROM _ITEM i, _GROUP g, _LOOP_INFO l, _CALENDAR c\n" +
                        "WHERE i.group_id = g.group_id \n" +
                        "AND i.to_do_id = l.to_do_id \n" +
                        "AND i.item_id = c.item_id \n" +
                        "AND i.item_id = "+itemId+";";

        Cursor cursor = db.rawQuery(selectQuery,null);
        Item item = new Item();

        if(cursor.moveToFirst()){
            do {
                item.setItemName(cursor.getString(0));
                item.setLocationX(cursor.getString(1));
                item.setLocationY(cursor.getString(2));
                item.setDoneDate(cursor.getString(3));
                item.setStartDate(cursor.getString(4));
                item.setEndDate(cursor.getString(5));
                item.setGroupName(cursor.getString(6));
                item.setGroupColor(cursor.getString(7));
                item.setCalenderDate(cursor.getString(8));
                item.setLoopWeek(cursor.getString(9));
                item.setItemId(cursor.getInt(10));
                item.setTodoId(cursor.getInt(11));
            }while (cursor.moveToNext());
        }
        return item;
    }
    /*~ get Item by itemId from DB */



    /** get getloopItem by itemId from DB **/
    public List<Item> getloopItem(int itemId, String startOfWeek, String endOfWeek){
        List<Item> list = new ArrayList<>();

        String selectQuery =
                "SELECT i.done_date , c.calendar_date " +
                        "FROM _ITEM i, _CALENDAR c " +
                        "WHERE i.item_id = c.item_id " +
                        "AND  c.calendar_date BETWEEN \"2020-02-12\" AND \"2020-02-17\"" + //수정
                        "AND i.to_do_id = (SELECT to_do_id " +
                        "FROM _ITEM " +
                        "WHERE item_id="+itemId+");";

        Cursor cursor = db.rawQuery(selectQuery,null);
        if(cursor.moveToFirst()){
            do {
                Item item = new Item();
                item.setDoneDate(cursor.getString(0));
                item.setCalenderDate(cursor.getString(1));
                list.add(item);
            }while (cursor.moveToNext());
        }
        return list;
    }
    /*~ get getloopItem by itemId from DB */


    public boolean deleteItemByTodoId(int todoId){
        return true;
    }
}