package jog.my.memory.database;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

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
        TextView textView = new TextView(mContext);
        textView.setSingleLine(false);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        textView.setText(this.getItem(position).getmName() + "\n" + this.getItem(position).getmTimeStamp());
        return textView;
    }
}
