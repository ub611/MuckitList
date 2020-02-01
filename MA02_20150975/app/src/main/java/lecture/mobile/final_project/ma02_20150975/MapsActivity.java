package lecture.mobile.final_project.ma02_20150975;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.nearby.messages.internal.Update;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity{
    private final static int PERMISSION_REQ_CODE = 100;         // permission 요청 코드
    private GoogleMap mGoogleMap;           // 구글맵 객체 저장 멥버 변수
    private LocationManager locManager;     // 위치 관리자
    private Location lastLocation;          // 앱 실행 중 최종으로 수신한 위치 저장 멤버 변수
    private ArrayList<Marker> markerList;
    Geocoder geocoder;
    List<Address> listAddress;

    DBHelper helper = new DBHelper(this);
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION }, PERMISSION_REQ_CODE);
            return;
        }

//        locManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 3000, 5, locationListener);
        lastLocation = locManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        mapFragment.getMapAsync(mapReadyCallback);
    }

//    LocationListener locationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(Location location) {
//            Log.i("curr", "Current Location : " + location.getLatitude() + ", " + location.getLongitude());
//
//            LatLng currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
//            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, ZOOM_LEVEL));
//        }
//        @Override
//        public void onStatusChanged(String s, int i, Bundle bundle) {
//        }
//        @Override
//        public void onProviderEnabled(String s) {
//        }
//        @Override
//        public void onProviderDisabled(String s) {
//        }
//    };

    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            markerList = new ArrayList<>();
            final LatLng lastLatLng;


            if (lastLocation != null) {
                lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            } else {
                lastLatLng = new LatLng(37.606320, 127.041808);     // 최종 위치가 없을 경우 지정한 곳으로 위치 지정
            }


            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 15));

            mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    String loc = String.format("클릭 - 위도:%f, 경도:%f", latLng.latitude, latLng.longitude);
                    Toast.makeText(MapsActivity.this, loc, Toast.LENGTH_SHORT).show();
                }
            });

            makeMarker();

            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    Intent intent = new Intent(MapsActivity.this, DetailActivity.class);

                    MyListDTO dto = new MyListDTO();
                    dto = (MyListDTO) marker.getTag();

                    lastLocation.setLatitude(dto.getLat());
                    lastLocation.setLongitude(dto.getLng());

                    intent.putExtra("_id", dto.get_id());
                    startActivityForResult(intent, 400);
                }
            });

        }
    };

    public void onClick(View v){
        editText = (EditText)findViewById(R.id.mapEtAddress);
        String searchAdd = null;

        if(editText.getText() != null)
            searchAdd = editText.getText().toString();

        switch (v.getId()){
            case R.id.mapBtnSearch:
                geocoder = new Geocoder(this);
                final LatLng lastLatLng;

                if(searchAdd == null || searchAdd.equals(""))
                    Toast.makeText(this, "검색어를 입력해주세요", Toast.LENGTH_SHORT);

                else {
                    try {
                        listAddress = geocoder.getFromLocationName(searchAdd, 1);
                        lastLatLng = new LatLng(listAddress.get(0).getLatitude(), listAddress.get(0).getLongitude());
                        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatLng, 15));

                    } catch (IOException e) {

                    }
                }
                break;

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == MapsActivity.RESULT_OK) {
                onResume();
            }
        }
    }

    protected void makeMarker(){
        mGoogleMap.clear();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + helper.TABLE_NAME, null, null);
        MarkerOptions poiMarkerOptions = new MarkerOptions();

        ArrayList<MyListDTO> poList = new ArrayList<>();

        while(cursor.moveToNext()) {
            MyListDTO poi = new MyListDTO();

            poi.set_id(cursor.getInt(0));
            poi.setTitle(cursor.getString(1));
            poi.setTell(cursor.getString(2));
            poi.setAddress(cursor.getString(3));
            poi.setLat(cursor.getFloat(5));
            poi.setLng(cursor.getFloat(6));
            poi.setIsChecked(cursor.getInt(7));

            Log.d("poi", cursor.getString(1));

            poList.add(poi);
        }

        for(MyListDTO item : poList) {

            Float lat = item.getLat();
            Float lng = item.getLng();
            LatLng eachLoc = new LatLng(lat, lng);

            Log.d("title ", item.getTitle());
            poiMarkerOptions.title(item.getTitle());
            poiMarkerOptions.snippet(item.getTell());
            poiMarkerOptions.position(eachLoc);

            if(item.getIsChecked() == 1)
                poiMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.red));
            else
                poiMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.blue));

            Marker poiMarker = null;
            poiMarker = mGoogleMap.addMarker(poiMarkerOptions);

            poiMarker.setTag(item);
            poiMarker.showInfoWindow();

            markerList.add(poiMarker);
        }

        cursor.close();
        helper.close();

    }
}
