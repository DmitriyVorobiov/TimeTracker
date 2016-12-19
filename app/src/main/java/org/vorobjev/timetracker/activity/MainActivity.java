package org.vorobjev.timetracker.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends Activity {

    @Bind(R.id.chart)
    PieChart mChart;

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

        setData(4, 100);
    }

    private void setData(int count, float range) {
        ArrayList<PieEntry> entries = categoriesTimes();
        PieDataSet dataSet = new PieDataSet(entries, "Election Results");
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
            entries.add(new PieEntry(time, cE.getName()));
        }
        return entries;
    }

}
