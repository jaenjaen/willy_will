package com.willy.will.detail.view;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;

import com.willy.will.R;
import com.willy.will.add.view.AddItemActivity;
import com.willy.will.common.controller.AdMobController;
import com.willy.will.common.model.ToDoItem;
import com.willy.will.detail.controller.DetailController;
import com.willy.will.detail.model.Item;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DetailActivity extends Activity implements MapView.MapViewEventListener {

    private ImageView important, groupColor;
    private ImageButton editButton;
    private TextView itemName, groupName, startDate, endDate, doneDate, roof,achievementRate, address, roadAddress, userPlaceName, itemMemo;
    private RelativeLayout doneDateArea, mapArea;
    private LinearLayout achievementRateArea, locationArea, memoArea;
    private String roofDay = "";
    private String addressName, roadAddressName;
    private String[] days = {"일","월","화","수","목","금","토"};
    private String startDayOfWeek, endDayOfWeek;
    private double latitude, longitude;
    private int ImportanceValue;
    private String loopWeek;
    private List<TextView> day = new ArrayList<>();
    private static Item todoItem;
    private static List<Item> achievementList;
    private DetailController detailCtrl = null;
    private MapPoint markerPoint;
    private ScrollView scrollView;
    private MapView mapView;
    private ViewGroup mapViewContainer;
    private MapPOIItem marker;
    private Resources resources;
    private ClipboardManager clipboard;
    private ClipData clip;
    private String copyText = null;
    private Calendar today;
    private SimpleDateFormat dateFormat;
    private String calendarDateStr;
    private boolean itemChanged;
    private String userPlaceNameVal;
    private AdMobController adMobController = new AdMobController(this);


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        detailCtrl = new DetailController();
        resources = getResources();
        important = findViewById(R.id.important);
        itemName = findViewById(R.id.item_name);
        itemMemo = findViewById(R.id.item_memo);
        groupName = findViewById(R.id.group_name);
        startDate = findViewById(R.id.start_date);
        endDate = findViewById(R.id.end_date);
        doneDate = findViewById(R.id.done_date);
        achievementRate = findViewById(R.id.achievement_rate);
        achievementRateArea = findViewById(R.id.achievement_rate_area);
        doneDateArea = findViewById(R.id.done_date_area);
        locationArea = findViewById(R.id.location_area);
        memoArea = findViewById(R.id.memo_area);
        roof = findViewById(R.id.loof);
        address = findViewById(R.id.address);
        roadAddress = findViewById(R.id.road_address);
        userPlaceName = findViewById(R.id.user_place_name);
        mapViewContainer = findViewById(R.id.map_view);
        groupColor = findViewById(R.id.group_color);
        editButton = findViewById(R.id.edit_button);
        scrollView = findViewById(R.id.scroll_view);
        mapArea = findViewById(R.id.map_area);
        day.add(0,(TextView)findViewById(R.id.sunday));
        day.add(1,(TextView)findViewById(R.id.monday));
        day.add(2,(TextView)findViewById(R.id.tuesday));
        day.add(3,(TextView)findViewById(R.id.wednesday));
        day.add(4,(TextView)findViewById(R.id.thursday));
        day.add(5,(TextView)findViewById(R.id.friday));
        day.add(6,(TextView)findViewById(R.id.saturday));
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

         /** get intent(item_id) from mainActivity **/
        Intent intent = getIntent();
        ToDoItem item = (ToDoItem) intent.getSerializableExtra(getResources().getString(R.string.item_id));

         /** access DB **/
         todoItem = detailCtrl.getToDoItemByItemId(item.getItemId());
         calendarDateStr = detailCtrl.getCalendarDateByItemId(item.getItemId());

         //set start of the week, end of the week
         today = getCalendarDate(calendarDateStr);
         int dayOfWeek = today.get(Calendar.DAY_OF_WEEK)-1;
         today.add(Calendar.DATE,-dayOfWeek);
         startDayOfWeek = dateFormat.format(today.getTime());
         today.add(Calendar.DATE,6);
         endDayOfWeek = dateFormat.format(today.getTime());
        /*~ access DB ~*/

        setData();

        /** set itemName selected (for MARQUEE) **/
        itemName.setSelected(true);
        /*~ set itemName selected (for MARQUEE) */


        /** set loopWeek (ex : 안함, 매일, 월 수 금) **/
        loopWeek = todoItem.getLoopWeek();
        if(loopWeek ==null){
            achievementRateArea.setVisibility(View.GONE);
            roofDay += "반복 안함";
        }else {
            if(loopWeek.equals("1111111")){
                doneDateArea.setVisibility(View.GONE);
                roofDay += "매일";
            }else {
                doneDateArea.setVisibility(View.GONE);
                for (int i = 0; i < loopWeek.length(); i++) {
                    if (loopWeek.charAt(i)-'0'==1) roofDay += days[i] + " "; }
            }
            setAchievement();
        }
        /*~ set achievementArea(rate, loop day) */

        doneDate.setText((todoItem.getDoneDate()==null)?resources.getString(R.string.not_done):todoItem.getDoneDate());
        roof.setText(roofDay);

        itemChanged = false;

         /** loading Ad **/
         adMobController.callingAdmob();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_FIRST_USER) {
            if(requestCode == resources.getInteger(R.integer.modify_item_request_code)) {
                todoItem = data.getParcelableExtra(resources.getString(R.string.modified_item_key));

                for(int i=0;i<day.size();i++){
                    day.get(i).setActivated(false);
                    day.get(i).setSelected(false); }

                setData();
                setAchievement();
                itemChanged = true;
            }
            else if(requestCode == resources.getInteger(R.integer.delete_item_request_code)) {
                itemChanged = true;
                finish();
            }
        }
    }

    /** set Data **/
    public void setData(){
        itemName.setText(todoItem.getItemName());

        ImportanceValue = todoItem.getImportant();
        if(ImportanceValue==1) {
            important.setImageResource(R.drawable.important1);
        }else if(ImportanceValue==2){
            important.setImageResource(R.drawable.important2);
        }else if(ImportanceValue==3){
            important.setImageResource(R.drawable.important3);
        }else if(ImportanceValue==4){
            important.setVisibility(View.GONE); }

        groupName.setText((todoItem.getGroupName()==null)?resources.getString(R.string.no_group):todoItem.getGroupName());
        if(todoItem.getGroupId() == resources.getInteger(R.integer.no_group_id)){
            groupColor.setActivated(false);
            groupColor.getDrawable().mutate().setTint(resources.getColor(R.color.dark_gray, null));
        }else{
            groupColor.setActivated(true);
            groupColor.getDrawable().mutate().setTint(Color.parseColor(todoItem.getGroupColor())); }

        startDate.setText(todoItem.getStartDate());
        endDate.setText(todoItem.getEndDate());

        if (todoItem.getItemMemo()==null || todoItem.getItemMemo().equals("")) {
            memoArea.setVisibility(View.GONE);
        }else{
            memoArea.setVisibility(View.VISIBLE);
            itemMemo.setText(todoItem.getItemMemo()); }

        if (todoItem.getLatitude() == null || todoItem.getLongitude() == null) {
            addressName = "";
            address.setText(addressName);
            address.setVisibility(View.GONE);
            roadAddressName = "";
            roadAddress.setText(roadAddressName);
            roadAddress.setVisibility(View.GONE);
            mapArea.setVisibility(View.GONE);

            userPlaceNameVal = todoItem.getUserPlaceName();
            if (userPlaceNameVal == null || userPlaceNameVal.equals("")) {
                userPlaceNameVal = "";
                userPlaceName.setText(userPlaceNameVal);
                userPlaceName.setVisibility(View.GONE);
                locationArea.setVisibility(View.GONE);
            } else {
                userPlaceName.setText(userPlaceNameVal);
                userPlaceName.setVisibility(View.VISIBLE);
                locationArea.setVisibility(View.VISIBLE);
            }
        } else {
            latitude = Double.parseDouble(todoItem.getLatitude());
            longitude = Double.parseDouble(todoItem.getLongitude());
            markerPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
            getAddress(longitude, latitude);
            mapView = new MapView(this);
            mapView.setMapViewEventListener(this);
            mapViewContainer.addView(mapView);
            mapArea.setVisibility(View.VISIBLE);

            userPlaceNameVal = "";
            userPlaceName.setText(userPlaceNameVal);
            userPlaceName.setVisibility(View.GONE);
        }

        loopWeek = todoItem.getLoopWeek();
    }

    /** set Achievement **/
    public void setAchievement(){
        double rate = 0;
        achievementList = detailCtrl.getloopItem(todoItem.getItemId(), startDayOfWeek, endDayOfWeek + "");
        for(int i=0;i<achievementList.size();i++){
            String[] dateArr = achievementList.get(i).getCalenderDate().split("-");
            today.set(Calendar.YEAR, Integer.parseInt(dateArr[0]));
            today.set(Calendar.MONTH, Integer.parseInt(dateArr[1])-1);
            today.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArr[2]));
            int index = today.get(Calendar.DAY_OF_WEEK)-1;
            String doneDateValue = achievementList.get(i).getDoneDate();
            if(doneDateValue==null || doneDateValue==""){
                day.get(index).setActivated(true);
                day.get(index).setSelected(false);
            }else{
                rate++;
                day.get(index).setActivated(true);
                day.get(index).setSelected(true);
            }
        }
        achievementRate.setText(Math.round((rate/achievementList.size())*100) +"%");
    }

    /** convert String("yyyy-MM-dd") to Calendar **/
    public Calendar getCalendarDate(String dateStr){
        Calendar date = Calendar.getInstance();
        String[] dateArr = dateStr.split("-");
        date.set(Calendar.YEAR, Integer.parseInt(dateArr[0]));
        date.set(Calendar.MONTH, Integer.parseInt(dateArr[1])-1);
        date.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateArr[2]));
        return date;
    }


    /** open option menu -> item modify, item delete) **/
    public void openOptionMenu(View view){
        PopupMenu menu = new PopupMenu(DetailActivity.this,editButton);
        menu.getMenuInflater().inflate(R.menu.menu_edit,menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent;
                int code;
                switch (item.getItemId()){
                    case R.id.btn_modify:
                        intent = new Intent(DetailActivity.this, AddItemActivity.class);
                        intent.putExtra(resources.getString(R.string.selected_item_key), todoItem);
                        code = resources.getInteger(R.integer.modify_item_request_code);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra(resources.getString(R.string.request_code), code);
                        startActivityForResult(intent, code);
                        return true;
                    case R.id.btn_delete:
                        intent = new Intent(DetailActivity.this, DeletePopupActivity.class);
                        intent.putExtra("todoId",todoItem.getTodoId());
                        code = resources.getInteger(R.integer.delete_item_request_code);
                        intent.putExtra(resources.getString(R.string.request_code), code);
                        startActivityForResult(intent, code);
                        return true;
                }
                return false;
            }
        });
        menu.show();
    }
    /*~ open option menu -> item modify, item delete) */


    /** Back to MainActivity **/
    public void backToMain(View view) {
        View focusedView = getCurrentFocus();
        if(focusedView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        this.finish();
    }
    /*~ Back to MainActivity (Main View) */


    @Override
    public void finish() {
        if(itemChanged) {
            setResult(resources.getInteger(R.integer.item_change_return_code));
        } else {
            setResult(RESULT_CANCELED); }
        super.finish();
    }


    /**  get Address (rest api) **/
    private void getAddress(final double longitude, final double latitude) {

        new Thread(new Runnable() {
            String json = null;

            @Override
            public void run() {
                try {
                    String apiURL = "https://dapi.kakao.com/v2/local/geo/coord2address.json?x="+ longitude + "&y=" + latitude +"&input_coord=WGS84";
                    URL url = new URL(apiURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("Authorization", "KakaoAK b5ef8f50c799f2e913df5481ce88bd18");
                    int responseCode = con.getResponseCode();
                    BufferedReader br = null;

                    if (responseCode == 200) {
                        br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    }

                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = br.readLine()) != null) {
                        response.append(inputLine);
                    }
                    br.close();

                    json = response.toString();
                    if (json == null) {
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(json);

                    //jsonObject.getJSONArray("documents") 변수 하나로
                    if(jsonObject.getJSONArray("documents").length()>0){
                        JSONObject dataObject = jsonObject.getJSONArray("documents").getJSONObject(0);

                        if(dataObject.isNull("road_address")) {
                            roadAddressName = "";
                        }
                        else {
                            JSONObject roadAddressInfo = dataObject.getJSONObject("road_address");
                            roadAddressName = roadAddressInfo.getString("address_name");
                        }

                        if(dataObject.isNull("address")) {
                            addressName = "";
                        }
                        else {
                            JSONObject addressInfo = dataObject.getJSONObject("address");
                            addressName = addressInfo.getString("address_name");
                        }

                        marker = new MapPOIItem();
                        marker.setItemName("");
                        marker.setTag(0);
                        marker.setMapPoint(markerPoint);
                        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                        marker.setShowCalloutBalloonOnTouch(false);
                        mapView.addPOIItem(marker);

                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                if(addressName.isEmpty()) {
                                    address.setText("");
                                    address.setVisibility(View.GONE);
                                }
                                else {
                                    todoItem.setAddressName(addressName);
                                    address.setText(addressName);
                                    address.setVisibility(View.VISIBLE);
                                }

                                if(roadAddressName.isEmpty()) {
                                    roadAddress.setText("");
                                    roadAddress.setVisibility(View.GONE);
                                }else{
                                    todoItem.setRoadAddressName(roadAddressName);
                                    roadAddress.setText(roadAddressName);
                                    roadAddress.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    }else{
                        mapArea.setVisibility(View.GONE);
                    }

                } catch (UnknownHostException e) {
                    mapArea.setVisibility(View.GONE);
                    address.setText("네트워크에 연결할 수 없습니다.");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    /** item name copy **/
    public void itemCopy(View view){
        if(clipboard == null) clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if(view.getId() == R.id.item_copy_btn) {
            copyText = itemName.getText().toString();
        }
        else if(view.getId() == R.id.address_copy_btn) {
            if(userPlaceName.getVisibility() == View.VISIBLE) {
                copyText = userPlaceName.getText().toString();
            }
            else {
                copyText = address.getText().toString();
            }
        }
        else {
            copyText = "";
        }
        clip = ClipData.newPlainText("item", copyText);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(view.getContext(),resources.getString(R.string.copy_msg),Toast.LENGTH_LONG).show();
    }


    /** kakaomap EventListener **/
    @Override
    public void onMapViewInitialized(MapView mapView) {
        mapView.setMapCenterPoint(markerPoint, true);
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
        scrollView.requestDisallowInterceptTouchEvent(true);
    }

}