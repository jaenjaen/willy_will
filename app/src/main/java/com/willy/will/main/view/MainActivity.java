package com.willy.will.main.view;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.willy.will.R;
import com.willy.will.add.view.AddItemActivity;
import com.willy.will.calander.view.CalendarActivity;
import com.willy.will.common.controller.AdMobController;
import com.willy.will.common.model.Group;
import com.willy.will.common.view.GroupManagementActivity;
import com.willy.will.database.DBAccess;
import com.willy.will.database.GroupDBController;
import com.willy.will.search.view.SearchActivity;
import com.willy.will.setting.view.TaskManagementActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity{
    public static AdRequest adRequest;
    public static DBAccess dbHelper;
    private Resources resources;
    private AdMobController adMobController = new AdMobController(this);

    private GroupDBController dbController;
    private Spinner sp_group;
    private ArrayList<String> spgroupList;
    private ArrayList<Group> groupList;
    private ArrayAdapter<String> spgroupAdapter;
    private Group sendGroup;
    private int groupId;

    private DrawerLayout drawer;
    private View drawerView;

    private TextView tv_date;
    private Calendar todayDate;
    private SimpleDateFormat sdf;
    private SimpleDateFormat sdf2;
    private String baseDate; // mm.dd
    private String sendDate; // yyyy-MM-dd

    MainFragment fragmentmain;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = DBAccess.getDbHelper();
        resources = getResources();
        adRequest = new AdRequest.Builder().build();

        /**load ad**/
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adMobController.callingAdmob();
        /*~load ad*/

        /**set navigation Drawer**/
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View) findViewById(R.id.nav_view);
        /*~set navigation Drawer*/

        /**setDate**/
        tv_date = (TextView) findViewById(R.id.tv_date);
        todayDate = Calendar.getInstance();
        sdf = new SimpleDateFormat("MM.dd");
        sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        baseDate = sdf.format(todayDate.getTime());
        sendDate = sdf2.format(todayDate.getTime());
        tv_date.setText(baseDate);
        /*~setDate*/

        /** language setting**/
        Locale.setDefault(Locale.KOREAN);
        /**open picker & change txt**/
        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(MainActivity.this, datepicker,
                        todayDate.get(Calendar.YEAR), todayDate.get(Calendar.MONTH), todayDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        /*~open picker & change txt*/

        /*Set sp_group*/

        /**set fragment**/
        dbController = new GroupDBController(resources);
        groupList = dbController.getAllGroups(groupList);
        groupId = -1;
        fragmentmain = MainFragment.getInstance(sendDate,groupId);

        //add the fragment to container(frame layout)
        getSupportFragmentManager()
                .beginTransaction().add(R.id.fragmentcontainer,fragmentmain).commit();
        /*~set fragment*/

        /**set sp_group **/
        spgroupList = new ArrayList<String>();
        spgroupList.add(0,resources.getString(R.string.all));
        for (Group a: groupList) {
            spgroupList.add(a.getGroupName());
        }


        spgroupAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                spgroupList);

        sp_group = (Spinner)findViewById(R.id.sp_group);
        sp_group.setAdapter(spgroupAdapter);
        sp_group.setSelection(0,false);

        //sp_group clickListener
        sp_group.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int i, long id) {
                deleteFragment();

                groupId = i-1;
                if(i == 0){ sendGroup = null;}
                else{ sendGroup = groupList.get(i-1);}

                updateFragement(groupId,sendDate);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        /** set fab event Listener **/
        fab = findViewById(R.id.fabItemAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set fab(Add button) disabled
                view.setClickable(false);
                // show Item add Activity
                Intent intent = new Intent(MainActivity.this , AddItemActivity.class);
                int code = resources.getInteger(R.integer.add_item_request_code);
                intent.putExtra(resources.getString(R.string.request_code), code);
                startActivityForResult(intent, code);
            }
        });
        /* ~set fab event Listener */
/*
        //set alarm
        SharedPreferences sharedPreferences = getSharedPreferences("ALARM", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String alarmState = sharedPreferences.getString("AlarmState", "default");
        if(alarmState.equals("default")){
            editor.putString("AlarmState", "on");
            editor.commit();
            AlarmSet.setAlarm(getApplicationContext());
        }
*/

    }


    /** updateFragment*/
    private void updateFragement(int groupId, String date){
        fragmentmain = MainFragment.getInstance(date, groupId);

        getSupportFragmentManager()
                .beginTransaction().add(R.id.fragmentcontainer,fragmentmain).commit();
    }
    /* ~updateFragment*/

    /** deleteFragment*/
    private void deleteFragment(){
        getSupportFragmentManager()
                .beginTransaction().remove(fragmentmain).commit();
    }
    /* ~deleteFragment*/

    /** Open Date picker and deleting original fragment
        and creating new fragment of selected date **/

    DatePickerDialog.OnDateSetListener datepicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int y, int m, int d) {
            todayDate.set(Calendar.YEAR, y);
            todayDate.set(Calendar.MONTH, m);
            todayDate.set(Calendar.DAY_OF_MONTH, d);
            updateLabel();

            deleteFragment();
            updateFragement(groupId,sendDate);
        }
    };
    /*~ Open Date picker and deleting original fragment
     and creating new fragment of selected date */

    /** Change Date Using from selected date by Date Picker **/
    private void updateLabel(){
        baseDate = sdf.format(todayDate.getTime());
        sendDate = sdf2.format(todayDate.getTime());
        tv_date.setText(baseDate);
    }
    /* ~ Change Date*/

    /** go to Today Fragment*/
    public void todayFragment(View view){
        String todayDate = sdf.format(Calendar.getInstance().getTime());
        String DBDate = sdf2.format(Calendar.getInstance().getTime());
        tv_date.setText(todayDate);
        deleteFragment();
        updateFragement(groupId,DBDate);
        sendDate = DBDate;
    }

    /** Function: Move to SearchView */
    public void btnSearchClick(View view){
        Intent intent = new Intent(MainActivity.this , SearchActivity.class);
        intent.putExtra(getResources().getString(R.string.current_date_key),sendDate);
        intent.putExtra(getResources().getString(R.string.current_group_key),sendGroup);
        startActivityForResult(intent, resources.getInteger(R.integer.search_item_request_code));
    }
    /*~ Function: Move to SearchView */

    /** Move to CalendarView */
    public void btnCalendarClick(View view) {
        Intent intent = new Intent(MainActivity.this , CalendarActivity.class);
        intent.putExtra(getResources().getString(R.string.current_date_key),sendDate);
        startActivityForResult(intent,resources.getInteger(R.integer.calender_item_request_code));
    }
    /*~Move to CalendarView*/

    /** Open navigation drawer */
    public void btnSettingClick(View view){
        if(!drawer.isDrawerOpen(drawerView)) {
            drawer.openDrawer(drawerView);
        }
    }
    /*~ Open navigation drawer */

    /** Move to GroupManagementActivity */
    public void btnGroupSettingClick(View view){
        Intent intent = new Intent(MainActivity.this , GroupManagementActivity.class);

        int code = resources.getInteger(R.integer.group_management_code);
        intent.putExtra(resources.getString(R.string.request_code), code);

        drawer.closeDrawer(drawerView);

        startActivityForResult(intent, code);
    }
    /* ~Move to GroupManagementActivity */

    /** Move to TaskManagementActivity */
    public void btnTaskSettingClick(View view){
        Intent intent = new Intent(MainActivity.this , TaskManagementActivity.class);
        drawer.closeDrawer(drawerView);
        startActivityForResult(intent, resources.getInteger(R.integer.task_management_code));
    }
    /* ~Move to GroupManagementActivity */

    /* Move to AlarmManagementActivity
    public void btnAlarmSettingClick(View view){
        Intent intent = new Intent(MainActivity.this , AlarmActivity.class);
        drawer.closeDrawer(drawerView);
        startActivity(intent);
    }
    /* ~Move to GroupManagementActivity */

    /** add Activity callback listner */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            // Return from CalendarView
            if(requestCode == resources.getInteger(R.integer.calender_item_request_code)) {
                String receivedDate = data.getStringExtra(resources.getString(R.string.current_date_key));
                //Log.d("receivedDateCheck", "*************receivedDate: " + receivedDate + "**************");
                try {
                    Date rdate = sdf2.parse(receivedDate); //"YYYY-mm-DD"
                    baseDate = sdf.format(rdate.getTime());
                    sendDate = sdf2.format(rdate.getTime());
                    todayDate.setTime(rdate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                tv_date.setText(baseDate);
                deleteFragment();
                updateFragement(groupId,receivedDate);
            }
        }
        // Return from GroupManagementActivity
        else if(resultCode == resources.getInteger(R.integer.group_change_return_code)) {
            refreshGroupSpinner();
            fragmentmain.refreshListDomain();
        }
        // Return from DetailActivity, AddItemActivity, or TaskManagementActivity
        else if(resultCode == resources.getInteger(R.integer.item_change_return_code)) {
            refreshGroupSpinner();
            fragmentmain.refreshListDomain();
        }

        // If fab(add button) disabled, set enabled
        if(!fab.isClickable()) {
            fab.setClickable(true);
        }
    }
    /* ~add Activity callback listner */

    public void refreshGroupSpinner() {
        groupList = dbController.getAllGroups(groupList);
        spgroupList.clear();
        spgroupList.add(0,resources.getString(R.string.all));
        for (Group a: groupList) {
            spgroupList.add(a.getGroupName());
        }
        spgroupAdapter.notifyDataSetChanged();
        sp_group.setSelection(0,false);
    }

    /** terminate application */
    @Override
    public void onBackPressed() {
        //checking open drawer
        if(drawer.isDrawerOpen(drawerView)){
            drawer.closeDrawer(drawerView);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.mipmap.will_launcher_round);
            builder.setMessage(getResources().getString(R.string.terminate_msg))
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dbHelper.close();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
    /* ~terminate application */

    public void onBackClick(View v){ }
}
