package org.vorobjev.timetracker.fragments;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.vorobjev.timetracker.R;
import org.vorobjev.timetracker.TimeTrackerApplication;
import org.vorobjev.timetracker.dao.CategoryDao;
import org.vorobjev.timetracker.dao.PhotoDao;
import org.vorobjev.timetracker.dao.RecordDao;
import org.vorobjev.timetracker.db.TimeTrackerDatabaseHelper;
import org.vorobjev.timetracker.entity.CategoryEntity;
import org.vorobjev.timetracker.entity.PhotoEntity;
import org.vorobjev.timetracker.entity.RecordEntity;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecordFragment extends Fragment implements TimePickerFragment.OnFragmentInteractionListener, CategoryChooserDialog.OnCategoryChoosedListener {

    private final String TEMP_FILE_NAME = "temp";
    public static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.UK);
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);

    @Bind(R.id.photoFrame)
    public ImageView photoFrame;
    @Bind(R.id.description)
    public EditText descriptionView;
    @Bind(R.id.startTime)
    public TextView startTimeView;
    @Bind(R.id.endTime)
    public TextView endTimeView;
    @Bind(R.id.category)
    public TextView categoryView;
    public File tempPhotoFile;
    public Bitmap photo;
    public int recordId;
    RecordEntity record;
    TimeTrackerDatabaseHelper dbHelper;

    public void deleteRecord(){
        try {
            dbHelper.<RecordDao, RecordEntity>getDao(RecordEntity.class).remove(record);
        } catch (Exception e){

        }
    }

    @OnClick(R.id.startTimeButton)
    public void showStartTimePickerDialog(View v) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.type = TimePickerFragment.TYPE_START_TIME;
        newFragment.mListener = this;
        newFragment.show(getFragmentManager(), "timePicker");
    }

    @OnClick(R.id.endTimeButton)
    public void showEndTimePickerDialog(View v) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.type = TimePickerFragment.TYPE_END_TIME;
        newFragment.mListener = this;
        newFragment.show(getFragmentManager(), "timePicker");
    }

    @OnClick(R.id.catButton)
    public void showCategoryPickerDialog(View v) {
        CategoryChooserDialog newFragment = new CategoryChooserDialog();
        newFragment.mListener = this;
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void showPhoto() {
        photo = decodeScaledImage(tempPhotoFile);
        photoFrame.setImageBitmap(photo);
        photoFrame.setVisibility(ImageView.VISIBLE);
    }

    public void setRecordId(int id) {
        recordId = id;
    }

    private Bitmap decodeScaledImage(File file) {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int targetW = metrics.widthPixels;
        int targetH = metrics.heightPixels;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
    }

    public RecordEntity provideRecordEntity() {
        record.setDescription(descriptionView.getText().toString());
        record.setDuration((record.getTimeEnd() - record.getTimeStart()) / (1000 * 60));
        if (photo != null) {
            PhotoEntity photoEntity = new PhotoEntity();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] byteArray = stream.toByteArray();
            try {
                photoEntity.setImageBytes(byteArray);
                dbHelper.<PhotoDao, PhotoEntity>getDao(PhotoEntity.class).add(photoEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            record.setPhotoEntity(photoEntity);
        }
        return record;
    }

    public static RecordFragment newInstance(String param1, String param2) {
        RecordFragment fragment = new RecordFragment();
        return fragment;
    }

    public RecordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        dbHelper = TimeTrackerApplication.getInstance().getDbHelper();
        tempPhotoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), TEMP_FILE_NAME);
        if (recordId > 0) {
            try {
                record = dbHelper.<RecordDao, RecordEntity>getDao(RecordEntity.class).findRecord(recordId).get(0);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (record == null) {
            record = new RecordEntity();
            record.setTimeStart(System.currentTimeMillis());
            record.setTimeEnd(System.currentTimeMillis());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        ButterKnife.bind(this, view);
        fillControls();
        return view;

    }

    private void fillControls() {
        categoryView.setText(record.getCategoryEntity() != null ? record.getCategoryEntity().getName() : "Select category");
        endTimeView.setText(TIME_FORMAT.format(new Date(record.getTimeEnd())));
        startTimeView.setText(TIME_FORMAT.format(new Date(record.getTimeStart())));
        descriptionView.setText(record.getDescription() != null ? record.getDescription() : "");

        if (record.getPhotoEntity() != null) {
            byte[] bytes = record.getPhotoEntity().getImageBytes();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            photoFrame.setImageBitmap(bitmap);
            photoFrame.setVisibility(ImageView.VISIBLE);
        }
    }

    @Override
    public void onSelectStartTime(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        record.setTimeStart(calendar.getTimeInMillis());
        fillControls();
    }

    @Override
    public void onSelectEndTime(int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        record.setTimeEnd(calendar.getTimeInMillis());
        fillControls();
    }

    @Override
    public void onSelectCategory(String category) {
        categoryView.setText(category);
        try {
            record.setCategoryEntity(TimeTrackerApplication.getInstance().getDbHelper().<CategoryDao, CategoryEntity>getDao(CategoryEntity.class).findCategory(category).get(0));
        } catch (SQLException e) {

        }
    }
}
