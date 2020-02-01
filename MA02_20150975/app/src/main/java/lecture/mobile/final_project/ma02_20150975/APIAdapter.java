package lecture.mobile.final_project.ma02_20150975;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 유빈 on 2017-12-28.
 */

public class APIAdapter extends BaseAdapter {
    LayoutInflater inflater;
    Context context;
    int layout;
    ArrayList<APIResultDTO> list;

    public APIAdapter(int resource, Context context, ArrayList<APIResultDTO> list){
        this.context = context;
        this.layout = resource;
        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    //    원본 데이터의 특정 항목 반환
    @Override
    public Object getItem(int i) {
        return list.get(i).get_id();
    }

    //    원본 데이터 특정 항목의 아이디 반환
    @Override
    public long getItemId(int i) {
        return list.get(i).get_id();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.api_layout, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.apiTvTitle);
        TextView address = (TextView) convertView.findViewById(R.id.apiTvAddress);

        Log.d("pos" , list.get(pos).getTitle());

        title.setText(list.get(pos).getTitle());
        address.setText(list.get(pos).getAddress());

        return convertView;
    }
}
