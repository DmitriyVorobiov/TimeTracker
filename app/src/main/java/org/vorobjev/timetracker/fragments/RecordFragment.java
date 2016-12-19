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
import org.vorobjev.timetracker.db.TimeTrackerDatabaseHelper;
import org.vorobjev.timetracker.entity.CategoryEntity;
import org.vorobjev.timetracker.entity.PhotoEntity;
import org.vorobjev.timetracker.entity.RecordEntity;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecordFragment extends Fragment implements TimePickerFragment.OnFragmentInteractionListener, CategoryChooserDialog.OnCategoryChoosedListener {

    private final String TEMP_FILE_NAME = "temp";

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
    RecordEntity record;
    TimeTrackerDatabaseHelper dbHelper;

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
        record.setDuration((record.getTimeEnd() - record.getTimeStart()) / 1000 * 60);
        if (photo != null) {
            PhotoEntity photoEntity = new PhotoEntity();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] byteArray = stream.toByteArray();
            try {
//                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(tempPhotoFile));
//                buf.read(bytes, 0, bytes.length);
//                buf.close();
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
        record = new RecordEntity();
        record.setTimeStart(System.currentTimeMillis());
        record.setTimeEnd(System.currentTimeMillis());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onSelectStartTime(int hourOfDay, int minute) {
        startTimeView.setText(hourOfDay + " : " + minute);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        record.setTimeStart(calendar.getTimeInMillis());
    }

    @Override
    public void onSelectEndTime(int hourOfDay, int minute) {
        endTimeView.setText(hourOfDay + " : " + minute);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        record.setTimeEnd(calendar.getTimeInMillis());
    }

    @Override
    public void onSelectCategory(String category) {
        categoryView.setText(category);
        try {
            record.setCategoryEntity(TimeTrackerApplication.getInstance().getDbHelper().<CategoryDao, CategoryEntity>getDao(CategoryEntity.class).findCategory(category).get(0));
        } catch (SQLException e){

        }
    }
}
