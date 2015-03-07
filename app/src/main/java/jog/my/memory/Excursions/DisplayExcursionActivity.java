package jog.my.memory.Excursions;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

import jog.my.memory.R;
import jog.my.memory.database.Excursion;
import jog.my.memory.database.ExcursionDBHelper;
import jog.my.memory.database.Picture;
import jog.my.memory.database.PicturesDBHelper;

public class DisplayExcursionActivity extends Activity {
    public ExcursionDBHelper mydbHelper;
    public static final String EXTRA_ROW_ID = "rowId";
    private long rowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_excursion);
        rowId = getIntent().getLongExtra(EXTRA_ROW_ID, 1);
        //System.out.println(rowId);

        //Get the correct entry from the db
        mydbHelper = new ExcursionDBHelper(this);
        mydbHelper.open();
        Excursion entry = mydbHelper.fetchEntryByIndex(rowId);
        mydbHelper.close();

        //Deal with activity type

        EditText activityTypeEditText = (EditText) findViewById(R.id.excursion_name_id);
        String name = entry.getmName();
        activityTypeEditText.setText(name);

        //Deal with datetime
        EditText datetimeEdittext = (EditText) findViewById(R.id.datetime_id);
        datetimeEdittext.setText(entry.getmTimeStamp());
    }

    public void onDeleteExcursionButtonPress(View v){
        mydbHelper = new ExcursionDBHelper(this);
        mydbHelper.open();
        mydbHelper.removeEntry(rowId);
        mydbHelper.close();

        //Delete all pictures associated with excursion
        PicturesDBHelper picdbHelper = new PicturesDBHelper(this);
        picdbHelper.open();
        ArrayList<Picture> pics = picdbHelper.fetchEntriesByExcursionID(rowId);
        Log.d("DisplayExcursion", "" + pics);

        for (Picture pic : pics){
            picdbHelper.removeEntry(pic.getId());
            Log.d("DisplayExcursion", "A picture was removed!");
        }
        picdbHelper.close();

        finish();
    }
}
