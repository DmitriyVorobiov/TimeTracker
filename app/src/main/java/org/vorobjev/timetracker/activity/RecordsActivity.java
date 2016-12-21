package org.vorobjev.timetracker.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.vorobjev.timetracker.R;
import org.vorobjev.timetracker.TimeTrackerApplication;
import org.vorobjev.timetracker.dao.RecordDao;
import org.vorobjev.timetracker.db.TimeTrackerDatabaseHelper;
import org.vorobjev.timetracker.entity.RecordEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.OnItemSelected;

public class RecordsActivity extends Activity {

    public static final String RECORD_ID = "item_id";

    @Bind(R.id.histoty_list)
    ListView listView;

    TimeTrackerDatabaseHelper dbHelper;

    @OnItemClick(R.id.histoty_list)
    void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(RecordsActivity.this, RecordActivity.class).putExtra(RECORD_ID, ((String) parent.getItemAtPosition(position)).split(" ")[1]));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        ButterKnife.bind(this);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View v, int position,
//                                    long arg3) {
//                startActivity(new Intent(RecordsActivity.this, RecordActivity.class).putExtra(RECORD_ID, ((String) parent.getItemAtPosition(position)).split(" ")[1]));
//            }
//        });
        dbHelper = TimeTrackerApplication.getInstance().getDbHelper();
    }

    protected void onResume() {
        super.onResume();
        fillOverallStatisticsView();
    }

    void fillOverallStatisticsView() {
        List<RecordEntity> records = null;
        try {
            records = TimeTrackerApplication.getInstance().getDbHelper().<RecordDao, RecordEntity>getDao(RecordEntity.class).getRecords();
        } catch (SQLException e) {
        }
        ArrayList<String> entries = new ArrayList<String>();
        for (RecordEntity recordEntity : records) {
            entries.add("Record# " + recordEntity.getId());
        }
        String[] items = entries.toArray(new String[entries.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_selectable_list_item, items);
        listView.setAdapter(adapter);
    }

}


