package lecture.mobile.final_project.ma02_20150975;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class WishListActivity extends AppCompatActivity {
    ArrayList<MyListDTO> list;
    SQLiteDatabase db;
    CustomAdapter adapter;
    DBHelper helper = new DBHelper(this);
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);
        listView = (ListView)findViewById(R.id.lvWish);
        list = new ArrayList<>();

        showList();

        adapter = new CustomAdapter(R.layout.custom_layout, WishListActivity.this, list);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(WishListActivity.this, DetailActivity.class);
                intent.putExtra("_id", list.get(i).get_id());
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int pos = i;

                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(WishListActivity.this);
                alert_confirm.setMessage("위시리스트를 삭제 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                helper = new DBHelper(WishListActivity.this);
                                db = helper.getWritableDatabase();

                                String delete = "DELETE from " + "ma_table"
                                        + " WHERE _id=" + list.get(pos).get_id();

                                Log.d("delete" , list.get(pos).getTitle());

                                db.execSQL(delete);

                                onResume();

                                helper.close();
                                db.close();
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();     //닫기
                                return;
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();


                return true;
            }
        });
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.wishBtnAdd:
                startActivity(new Intent(this, AddActivity.class));
                break;

            case R.id.wishBtnCancel:
                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        showList();

        adapter = new CustomAdapter(R.layout.custom_layout, WishListActivity.this, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    protected void showList(){
        db = helper.getReadableDatabase();

        if(!list.isEmpty())
            list.clear();

        Cursor cursor = db.rawQuery("select * from " + helper.TABLE_NAME, null, null);

        while(cursor.moveToNext()){
            MyListDTO dto = new MyListDTO();

            dto.set_id(cursor.getInt(0));
            dto.setTitle(cursor.getString(1));
            dto.setTell(cursor.getString(2));
            dto.setAddress(cursor.getString(3));
            dto.setPicture(cursor.getString(4));
            dto.setLat(cursor.getFloat(5));
            dto.setLng(cursor.getFloat(6));
            dto.setIsChecked(cursor.getInt(7));

            if(cursor.getString(4) !=null)
             Log.d("wish Picture : " , cursor.getString(4));

            list.add(dto);
        }   //list 삽입 확인

        db.close();
        helper.close();
    }


}
