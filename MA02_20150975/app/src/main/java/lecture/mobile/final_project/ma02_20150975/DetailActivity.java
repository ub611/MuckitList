package lecture.mobile.final_project.ma02_20150975;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URLConnection;


public class DetailActivity extends AppCompatActivity {
    TextView tvTitle;
    TextView tvTell;
    TextView tvReview;
    TextView tvAddress;
    ImageView imageView;
    CheckBox checkBox;
    RatingBar ratingBar;
    LatLng latLng;

    DBHelper helper = new DBHelper(DetailActivity.this);

    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        id = intent.getIntExtra("_id", 2);

        Log.d("Id ", id+"");

        tvTell = (TextView)findViewById(R.id.tvDeTell);
        tvTitle = (TextView)findViewById(R.id.tvDetTitle);
        tvReview = (TextView)findViewById(R.id.tvDetReview);
        tvAddress = (TextView)findViewById(R.id.tvDeAddress);
        imageView = (ImageView)findViewById(R.id.ivDetail);
        checkBox = (CheckBox)findViewById(R.id.cbDetail);
        ratingBar = (RatingBar)findViewById(R.id.rbDetail);

        SQLiteDatabase db = helper.getReadableDatabase();


        Cursor cursor = db.rawQuery("select * from " + helper.TABLE_NAME + " where _id = " + id, null, null);

        while(cursor.moveToNext()){
            tvTitle.setText(cursor.getString(1));
            tvTell.setText(cursor.getString(2));
            tvAddress.setText(cursor.getString(3));
            tvReview.setText(cursor.getString(8));
            ratingBar.setRating((float) cursor.getDouble(9));
            latLng = new LatLng(cursor.getFloat(5),cursor.getFloat(6));

            Log.d("LatLng", latLng.latitude +"," );

            if(cursor.getString(4) != null) {
                Log.d("Detail ", cursor.getString(4));
                Uri uri = Uri.parse(cursor.getString(4));
                if (uri != null){
                    imageView.setImageURI(uri);
                    }

            }

            if(cursor.getInt(7) == 1)
                checkBox.setChecked(true);
            else
                checkBox.setChecked(false);

        }

        db.close();
        helper.close();
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnGoMap:
                Intent mapIntent = new Intent(this, WishMapsActivity.class);
                Log.d("putExtra", latLng.latitude +"," );
                mapIntent.putExtra("lat", latLng.latitude);
                mapIntent.putExtra("lng", latLng.longitude);
                mapIntent.putExtra("title", tvTitle.getText().toString());
                startActivity(mapIntent);

                break;
            case R.id.btnUpdate:
                Intent intent = new Intent(this, DetailUpdateActivity.class);
                intent.putExtra("_id", id);
                startActivityForResult(intent, 100);
                break;

            case R.id.btnShare:
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");

                String subject = "My wish :";
                String text = tvTitle.getText() + "\n" + tvAddress.getText();

                shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                shareIntent.putExtra(Intent.EXTRA_TEXT, text);

                Intent chooser = Intent.createChooser(shareIntent, "친구에게 공유하기");
                startActivity(chooser);

                break;

            case R.id.btnCancel:
                finish();
                break;

        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + helper.TABLE_NAME + " where _id = " + id, null, null);

        Log.d("Resume " , id +"");



        while(cursor.moveToNext()){
            tvTitle.setText(cursor.getString(1));
            tvTell.setText(cursor.getString(2));
            tvAddress.setText(cursor.getString(3));
            tvReview.setText(cursor.getString(8));
            ratingBar.setRating((float)cursor.getDouble(9));

            if(cursor.getInt(7) == 1)
                checkBox.setChecked(true);
            else
                checkBox.setChecked(false);

            if(cursor.getString(4) != null) {
                Uri uri = Uri.parse(cursor.getString(4));
                if (uri != null)
                    imageView.setImageURI(uri);
            }
        }
        db.close();
        helper.close();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == DetailActivity.RESULT_OK) {
                onResume();
            }
        }
    }

}
