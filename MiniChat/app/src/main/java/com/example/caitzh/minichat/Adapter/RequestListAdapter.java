package com.example.caitzh.minichat.Adapter;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.caitzh.minichat.R;
import com.example.caitzh.minichat.view.SortModel;

import java.util.List;

/**
 * Created by littlestar on 2017/6/30.
 */
public class RequestListAdapter extends BaseAdapter {
    private Context context;
    private List<SortModel> list;
    public RequestListAdapter(Context context, List<SortModel> list) {
        this.context = context;
        this.list = list;
    }
    private class Viewholder {
        public ImageView imageView;
        public TextView nickname;
        public TextView status;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list == null) return null;
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View converView;
        Viewholder viewholder;
        if (view == null) {
            converView = LayoutInflater.from(context).inflate(R.layout.request_item, null);
            viewholder = new Viewholder();
            viewholder.imageView = (ImageView) converView.findViewById(R.id.RequestItemImg);
            viewholder.nickname = (TextView) converView.findViewById(R.id.RequestItemText);
            viewholder.status = (TextView) converView.findViewById(R.id.RequestItemStatus);
            converView.setTag(viewholder);
        } else {
            converView = view;
            viewholder = (Viewholder)converView.getTag();
        }
        viewholder.imageView.setImageBitmap(list.get(position).getBm());
        viewholder.nickname.setText(list.get(position).getName());
        viewholder.status.setText(list.get(position).getSortLetters());
        if ((list.get(position).getSortLetters()).equals("去看看")) {
            viewholder.status.setTextColor(context.getResources().getColor(R.color.colorBlue));
        }
        return converView;
    }
}
