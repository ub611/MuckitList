package lecture.mobile.final_project.ma02_20150975;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by ub2n6 on 2017-12-27.
 */

public class DBHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "ma_db";
    public final static String TABLE_NAME = "ma_table";

    public DBHelper(Context context){
        super(context, DB_NAME, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( _id integer primary key autoincrement,"
                + "name TEXT, tell TEXT, address TEXT, picture TEXT, lat DOUBLE, lng DOUBLE, isChecked integer, review TEXT, rating double);");

//		샘플 데이터
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '오징어 매운 떡볶이', '0507-1420-5455', '서울특별시 성북구 화랑로13길 28', null, '37.6045660', '127.0422670', '0', '맛있어요', 5);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '지지고', '0507-1415-6031', '서울특별시 성북구 화랑로13길 20', null,'37.6042690',  '127.0424870', '0', '불친절해요', 2);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '이삭토스트', '02-943-8848', '서울특별시 성북구 하월곡동 21-66', null,'37.604942', '127.042223', '0', '', 0);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '파리바게트', '02-941-4991', '서울특별시 성북구 월곡2동', null,'37.603050', '127.041428', '0', '', 0);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES (null, '스시빈', '02-911-8080', '서울특별시 성북구 하월곡동 화랑로 105', null,'37.604381', '127.043227', '0', '', 0);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
