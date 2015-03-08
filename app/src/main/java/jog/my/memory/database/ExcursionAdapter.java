package jog.my.memory.database;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import jog.my.memory.R;

/**
 * Created by Steve on 3/5/2015.
 */
public class ExcursionAdapter extends ArrayAdapter<Excursion> {

    private static final String TAG = "ExcursionAdapter";
    Context mContext;

    public ExcursionAdapter(Context mContext, int resource, List objects){
        super(mContext, resource , objects);
        this.mContext = mContext;
    }

    ExcursionAdapter(Context mContext, int resource, int textViewResourceId, List objects){
        super(mContext,resource,textViewResourceId,objects);
        this.mContext = mContext;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.item_text);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(this.getItem(position).getmName());
        TextView date = (TextView) convertView.findViewById(R.id.item_date);
        date.setText(this.getItem(position).getmTimeStamp());
        TextView privacy = (TextView) convertView.findViewById(R.id.item_privacy);
        privacy.setTypeface(null, Typeface.BOLD);
        privacy.setText("private");

        return convertView;
    }
}