package org.vorobjev.timetracker.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.vorobjev.timetracker.R;
import org.vorobjev.timetracker.TimeTrackerApplication;
import org.vorobjev.timetracker.dao.RecordDao;
import org.vorobjev.timetracker.db.TimeTrackerDatabaseHelper;
import org.vorobjev.timetracker.entity.RecordEntity;
import org.vorobjev.timetracker.fragments.RecordFragment;

import java.sql.SQLException;
import java.util.Date;

public class RecordActivity extends Activity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    TimeTrackerDatabaseHelper dbHelper;
    public RecordFragment recordFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        dbHelper = TimeTrackerApplication.getInstance().getDbHelper();
        if (savedInstanceState == null) {
            recordFragment = new RecordFragment();
            int recordId = getIntent().getStringExtra(RecordsActivity.RECORD_ID) != null ? Integer.valueOf(getIntent().getStringExtra(RecordsActivity.RECORD_ID)) : -1;
            if (recordId >= 0) {
                recordFragment.setRecordId(recordId);
            }
            getFragmentManager().beginTransaction().add(R.id.record_container, recordFragment).commit();
        } else {
            recordFragment = (RecordFragment)getFragmentManager().findFragmentById(R.id.record_container);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_record, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            RecordEntity entity = recordFragment.provideRecordEntity();
            if (entity.getCategoryEntity() == null) {
                Toast.makeText(this, "Select category", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            }
            try {
                dbHelper.<RecordDao, RecordEntity>getDao(RecordEntity.class).add(entity);
            } catch (SQLException e) {

            }
            finish();
        } else if (id == R.id.action_photo) {
            takePhoto();
        }
        return super.onOptionsItemSelected(item);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(recordFragment.tempPhotoFile));
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                recordFragment.showPhoto();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Taking photo failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
