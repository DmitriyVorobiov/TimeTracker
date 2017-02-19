package org.vorobjev.timetracker.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.vorobjev.timetracker.R;
import org.vorobjev.timetracker.TimeTrackerApplication;
import org.vorobjev.timetracker.dao.CategoryDao;
import org.vorobjev.timetracker.dao.RecordDao;
import org.vorobjev.timetracker.db.TimeTrackerDatabaseHelper;
import org.vorobjev.timetracker.entity.CategoryEntity;
import org.vorobjev.timetracker.entity.RecordEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity {

    @Bind(R.id.chart)
    PieChart mChart;
    @Bind(R.id.statistics1)
    ListView statListView1;
    @Bind(R.id.statistics2)
    ListView statListView2;
    TimeTrackerDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        dbHelper = TimeTrackerApplication.getInstance().getDbHelper();
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);
        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);
        mChart.setHoleRadius(40f);
        mChart.setTransparentCircleRadius(44f);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        setData();
    }

    protected void onResume() {
        super.onResume();
        setData();
        fillFrequentStatisticsView();
        fillOverallStatisticsView();
    }

    void fillOverallStatisticsView() {
        List<CategoryEntity> categoryEntities = null;
        List<RecordEntity> records = null;
        try {
            categoryEntities = TimeTrackerApplication.getInstance().getDbHelper().<CategoryDao, CategoryEntity>getDao(CategoryEntity.class).getCategories();
            records = TimeTrackerApplication.getInstance().getDbHelper().<RecordDao, RecordEntity>getDao(RecordEntity.class).getRecords();
        } catch (SQLException e) {
        }
        ArrayList<String> entries = new ArrayList<String>();
        for (CategoryEntity cE : categoryEntities) {
            long count = 0;
            for (RecordEntity recordEntity : records) {
                if (recordEntity.getCategoryEntity().getName().equals(cE.getName())) {
                    count += recordEntity.getDuration();
                }
            }
            entries.add(cE.getName() + ": " + count);
        }
        String[] items = entries.toArray(new String[entries.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        statListView2.setAdapter(adapter);
    }

    void fillFrequentStatisticsView() {
        List<CategoryEntity> categoryEntities = null;
        List<RecordEntity> records = null;
        try {
            categoryEntities = TimeTrackerApplication.getInstance().getDbHelper().<CategoryDao, CategoryEntity>getDao(CategoryEntity.class).getCategories();
            records = TimeTrackerApplication.getInstance().getDbHelper().<RecordDao, RecordEntity>getDao(RecordEntity.class).getRecords();
        } catch (SQLException e) {
        }
        ArrayList<String> entries = new ArrayList<String>();
        for (CategoryEntity cE : categoryEntities) {
            long count = 0;
            for (RecordEntity recordEntity : records) {
                if (recordEntity.getCategoryEntity().getName().equals(cE.getName())) {
                    count += 1;
                }
            }
            entries.add(cE.getName() + ": " + count);
        }
        String[] items = entries.toArray(new String[entries.size()]);
        Arrays.sort(items, new Comparator<String>() {
            public int compare(String str1, String str2) {
                String substr1 = str1.split(" ")[1];
                String substr2 = str2.split(" ")[1];
                return Integer.valueOf(substr2).compareTo(Integer.valueOf(substr1));
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, items);
        statListView1.setAdapter(adapter);
    }

    private void setData() {
        ArrayList<PieEntry> entries = categoriesTimes();
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        mChart.setData(data);
        mChart.highlightValues(null);
        mChart.invalidate();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            startActivity(new Intent(this, RecordActivity.class));
        } else if (id == R.id.action_history) {
            startActivity(new Intent(this, RecordsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<PieEntry> categoriesTimes() {
        List<CategoryEntity> categoryEntities = null;
        List<RecordEntity> records = null;
        try {
            categoryEntities = TimeTrackerApplication.getInstance().getDbHelper().<CategoryDao, CategoryEntity>getDao(CategoryEntity.class).getCategories();
            records = TimeTrackerApplication.getInstance().getDbHelper().<RecordDao, RecordEntity>getDao(RecordEntity.class).getRecords();
        } catch (SQLException e) {
        }
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        for (CategoryEntity cE : categoryEntities) {
            long time = 0;
            for (RecordEntity recordEntity : records) {
                if (recordEntity.getCategoryEntity().getName().equals(cE.getName())) {
                    time += recordEntity.getDuration();
                }
            }
            if (time > 0) {
                entries.add(new PieEntry(time, cE.getName()));
            }
        }
        return entries;
    }

}
