package lecture.mobile.final_project.ma02_20150975;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class AddActivity extends AppCompatActivity {
    EditText etSearch;
    ListView listView;
    APIAdapter adapter;
    ArrayList<APIResultDTO> list;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etSearch = (EditText)findViewById(R.id.etSearch);
        listView = (ListView)findViewById(R.id.lvAdd);

        list = new ArrayList<>();
        adapter = new APIAdapter(R.layout.api_layout, AddActivity.this, list);

        listView.setAdapter(adapter);
        address = getResources().getString(R.string.server_url);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                DBHelper helper = new DBHelper(AddActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();

                ContentValues values = new ContentValues();

                values.put("lng", list.get(i).getX());
                values.put("lat", list.get(i).getY());
                values.put("address",  list.get(i).getAddress());
                values.put("name", list.get(i).getTitle());
                values.put("tell", list.get(i).getTell());
                values.put("isChecked",  0);

                db.insert("ma_table", null, values);

                AlertDialog.Builder alert = new AlertDialog.Builder(AddActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });

                alert.setMessage(list.get(i).getTitle() + "이 위시리스트에 추가 되었습니다.");
                alert.show();

                Log.d("longClick", list.get(i).getTitle() );


                db.close();
                helper.close();

                return false;
            }
        });
    }

    public void onClick(View v) throws UnsupportedEncodingException {

        switch(v.getId()) {
            case R.id.AddbtnSearch:
                String targetKeyword = etSearch.getText().toString();

                if (targetKeyword.equals(""))
                    targetKeyword = "김밥";

                String resultTarget = URLEncoder.encode(targetKeyword, "UTF-8");

                new NetworkAsyncTask().execute(address + resultTarget);
                break;

            case R.id.addBtnCancel:
                finish();
                break;
        }
    }

    class NetworkAsyncTask extends AsyncTask<String, Integer, String> {

        public final static String TAG = "NetworkAsyncTask";
        public final static int TIME_OUT = 10000;

        ProgressDialog progressDlg;
        String address;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(AddActivity.this, "Wait", "Searching...");
        }

        @Override
        protected String doInBackground(String... strings) {
            address = strings[0];
            StringBuilder resultBuilder = new StringBuilder();

            try {
                URL url = new URL(address);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                if (conn != null) {
                    conn.setConnectTimeout(TIME_OUT);
                    conn.setUseCaches(false);
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        for (String line = br.readLine(); line != null; line = br.readLine()) {
                            resultBuilder.append(line + '\n');
                        }

                        br.close();
                    }
                    conn.disconnect();
                }

            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                // Toast.makeText(MainActivity.this, "Malformed URL", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return resultBuilder.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            MyXmlParser parser = new MyXmlParser();

            // 어댑터에 이전에 보여준 데이터가 있을 경우 클리어
            if (!list.isEmpty()) list.clear();

            //parsing 수행
            list = parser.parse(result);

            //리스트뷰에 연결되어 있는 어댑터에 parsing 결과 ArrayList 를 추가

            adapter = new APIAdapter(R.layout.api_layout, AddActivity.this, list);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            progressDlg.dismiss();
        }
    }

}
