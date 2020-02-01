package lecture.mobile.final_project.ma02_20150975;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

public class DetailUpdateActivity extends ActionBarActivity {
    public static int REQ_CODE_SELECT_IMAGE = 200;

    EditText etTitle;
    EditText etTell;
    EditText etAddress;
    EditText etReview;
    ImageView imageView;
    SQLiteDatabase db;
    CheckBox checkBox;
    DBHelper helper;
    RatingBar ratingBar;
    Uri name_uri;

    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_update);

        etTitle = (EditText)findViewById(R.id.etTitle);
        etTell = (EditText)findViewById(R.id.etTell);
        etAddress = (EditText)findViewById(R.id.mapEtAddress);
        etReview = (EditText)findViewById(R.id.etReview);
        imageView = (ImageView)findViewById(R.id.ivUpdate);
        checkBox = (CheckBox)findViewById(R.id.cbUpdate);
        ratingBar = (RatingBar)findViewById(R.id.rbUpdate);

        Intent intent = getIntent();
        id = intent.getIntExtra("_id", 1);

        helper = new DBHelper(this);
        db = helper.getReadableDatabase();

        Cursor c = db.rawQuery("select * from " + helper.TABLE_NAME + " where _id = " + id, null, null);

        while(c.moveToNext()){
            etTitle.setText(c.getString(1));
            etTell.setText(c.getString(2));
            etAddress.setText(c.getString(3));
            etReview.setText(c.getString(8));
            ratingBar.setRating((float)c.getDouble(9));

            if(c.getString(4) != null) {
                Uri uri = Uri.parse(c.getString(4));
                if (uri != null)
                    imageView.setImageURI(uri);
            }

            if(c.getInt(7) == 1)
                checkBox.setChecked(true);
            else
                checkBox.setChecked(false);

        }

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.ivUpdate:
                Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                galleryIntent.setType("image/*, video/*");
                if (galleryIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser(galleryIntent, "Select File"), REQ_CODE_SELECT_IMAGE);
                }

                break;

            case R.id.btnOk:
                helper = new DBHelper(this);
                db = helper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put("name", etTitle.getText().toString());
                values.put("tell", etTell.getText().toString());
                values.put("address", etAddress.getText().toString());
                values.put("review", etReview.getText().toString());
                values.put("rating", ratingBar.getRating());

                Log.d("rating", ratingBar.getRating()+"");

                if(name_uri != null)
                    values.put("picture", name_uri.toString());
                if(checkBox.isChecked())
                    values.put("isChecked", 1);
                else
                    values.put("isChecked", 0);

                db.update("ma_table", values, "_id=?", new String[]{ id+"" });

                db.close();
                helper.close();

                Intent intent = new Intent();
                intent.putExtra("_id", id);

                setResult(MapsActivity.RESULT_OK);
                setResult(DetailActivity.RESULT_OK);

                finish();
                break;
            case R.id.btnBack:
                setResult(DetailActivity.RESULT_CANCELED);
                finish();
                break;
        }
    }

    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQ_CODE_SELECT_IMAGE){
            if(resultCode== Activity.RESULT_OK){
                try {
                    name_uri= data.getData();
                    ImageView image = (ImageView)findViewById(R.id.ivUpdate);
                    if(name_uri != null)
                        image.setImageURI(name_uri);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
