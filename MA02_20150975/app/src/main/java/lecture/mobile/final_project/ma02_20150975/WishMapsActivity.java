package lecture.mobile.final_project.ma02_20150975;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class WishMapsActivity extends AppCompatActivity {
    LatLng latLng;
    private final static int PERMISSION_REQ_CODE = 100;         // permission 요청 코드
    GoogleMap mGoogleMap;
    MarkerOptions markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_maps);
        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("lat", 0);
        double lng = intent.getDoubleExtra("lng", 0);

        TextView tv = (TextView)findViewById(R.id.wishMaptvTitle);

        tv.setText(intent.getStringExtra("title"));

        Log.d("getIntent", lat +"");

        latLng = new LatLng(lat, lng);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.wishMap);

        if (ActivityCompat.checkSelfPermission(WishMapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(WishMapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(WishMapsActivity.this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION }, PERMISSION_REQ_CODE);
            return;
        }

        mapFragment.getMapAsync(mapReadyCallback);
    }

    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {@Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;
            markerOptions = new MarkerOptions();

            markerOptions.position(latLng);

            mGoogleMap.addMarker(markerOptions);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        }
    };

    public void onClick(View v){
        switch (v.getId()){
            case R.id.wishBtnBack:
                finish();
                break;
        }
    }
}
