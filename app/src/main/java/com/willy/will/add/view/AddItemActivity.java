package com.willy.will.add.view;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.willy.will.R;
import com.willy.will.common.view.GroupManagementActivity;
import com.willy.will.common.view.GroupColorSetting;
import com.willy.will.database.DBAccess;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddItemActivity extends Activity{
    private SimpleDateFormat simpleDateFormat = null;
    private Calendar today = null;
    private Calendar calendar = null;
    private String start_date_key = null;
    private String end_date_key = null;
    private Resources resources = null;
    private DatePickerDialog datePickerDialog = null;
    private DateListener dateListener = null;
    private String start_date = null;
    private String end_date = null;
    private View checkBox_group;

    private Spinner important;
    private TextView important_result;

    public static DBAccess dbHelper;

    Switch repeat_switch;
    TextView Text_start;
    TextView Text_end;
    EditText Title_editText,Group_editText;

    long now = System.currentTimeMillis();
    Date date = new Date(now);
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
    String formatDate = sdfNow.format(date);
    TextView dateNow;

    private SQLiteDatabase readDatabase;
    private SQLiteDatabase writeDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemadd);

        dbHelper = DBAccess.getDbHelper();
        //dummyCreate();
        resources = getResources();


        important = (Spinner)findViewById(R.id.important);
        important_result =(TextView)findViewById(R.id.important_result);

        repeat_switch = (Switch) findViewById(R.id.repeat_switch);
        checkBox_group = findViewById(R.id.checkBox_group);

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        today = Calendar.getInstance();
        calendar = Calendar.getInstance();

        /** Set theme of Dialogs **/
        datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme);
        dateListener = new DateListener();
        datePickerDialog.setOnDateSetListener(dateListener);
        /* ~Set theme of Dialogs */

        /** set view **/
        Button btn_start = findViewById(R.id.btn_start);
        Button btn_end = findViewById(R.id.btn_end);
        Text_start = findViewById(R.id.Text_start);
        Text_end = findViewById(R.id.Text_end);

        Text_start.setText(formatDate);
        Text_end.setText(formatDate);

        /** set value **/
        resources = getResources();
        start_date_key = resources.getString(R.string.start_date_key);
        end_date_key = resources.getString(R.string.end_date_key);

        start_date = getString(R.string.start_date_key);
        end_date = getString(R.string.end_date_key);

        Title_editText = (EditText)findViewById(R.id.Title_editText);
        Group_editText = (EditText)findViewById(R.id.Group_editText);

        /** edit keyboard invisible 1 **/
        Title_editText.setInputType(0);
        Title_editText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Title_editText.setInputType(1);
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(Title_editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        /** edit keyboard invisible **/
        Group_editText.setInputType(0);
        Group_editText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Group_editText.setInputType(1);
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(Group_editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });


        /******* Group buuton -> moving ********************/
        Button bnt_group = findViewById(R.id.bnt_group);
        bnt_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddItemActivity.this, GroupManagementActivity.class);
                startActivity(intent);
            }
        });


        repeat_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // checked -> add_item_repeat
                if (repeat_switch.isChecked() == true) {
                    checkBox_group.setVisibility(View.VISIBLE);

                    final ScrollView scrollView=findViewById(R.id.AddScrollView);
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
                else {
                    checkBox_group.setVisibility(View.GONE);
                }
            }
        });



    }
    public void bringUpgroupcolor(View view) {
        Intent intent = new Intent(this, GroupColorSetting.class);
    }

    // Start Date Picker Dialog for start of start date
    public void setStart(View view) {
        dateListener.setKey(start_date_key);
        if(start_date.isEmpty()) {
            datePickerDialog.updateDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
        }
        else {
            try {
                Date date = simpleDateFormat.parse(start_date);
                calendar.setTime(date);
                datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            } catch (ParseException e) {
                Log.e("PeriodSearchSettingActivity", "Setting date: "+e.getMessage());
                e.printStackTrace();
            }
        }
        datePickerDialog.setMessage(resources.getString(R.string.start_date_text));
        datePickerDialog.show();
    }

    // Start Date Picker Dialog for start of start date
    public void setEnd(View view) {
        dateListener.setKey(end_date_key);

        if(end_date.isEmpty()) {
            datePickerDialog.updateDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DATE));
        }
        else {
            try {
                Date date = simpleDateFormat.parse(end_date);
                calendar.setTime(date);
                datePickerDialog.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            } catch (ParseException e) {
                Log.e("PeriodSearchSettingActivity", "Setting date: "+e.getMessage());
                e.printStackTrace();
            }
        }
        datePickerDialog.setMessage(resources.getString(R.string.end_date_text));
        datePickerDialog.show();
    }



    public void Tomain(View view) {
        // Check focusing
        View focusedView = getCurrentFocus();
        if(focusedView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        // ~Check focusing
        this.finish();
    }



    class DateListener implements DatePickerDialog.OnDateSetListener {
        private String key = null;

        public void setKey(String key) {this.key = key;}
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(year, month, dayOfMonth);

            if(key.equals(start_date_key)) {
                start_date = simpleDateFormat.format(calendar.getTime());
                Text_start.setText(start_date);
            }
            else if(key.equals(end_date_key)) {
                end_date = simpleDateFormat.format(calendar.getTime());
                Text_end.setText(end_date);
            }
            else {
                Log.e("DateListener", "Invalid Key");
            }
        }
    }

    public void add_insert(View view){
        //String Title = Title_editText.getText().toString();
        String Groupname = "테스트다";
        String Groupcolor = "#0000000";

        // 실제 sql문을 수행하기 위한
        SQLiteDatabase db=DBAccess.getDbHelper().getWritableDatabase();

        db.execSQL("insert into _GROUP (group_name, group_color) values(?,?)",
                new String[]{Groupname,Groupcolor});
        db.close();

        finish();
    }

}


