package org.vorobjev.timetracker.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public static final int TYPE_START_TIME = 1;
    public static final int TYPE_END_TIME = 2;
    public int type;

    public interface OnFragmentInteractionListener {
        void onSelectStartTime(int hourOfDay, int minute);
        void onSelectEndTime(int hourOfDay, int minute);
    }

    public OnFragmentInteractionListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (type == TYPE_START_TIME) {
            mListener.onSelectStartTime(hourOfDay, minute);
        } else {
            mListener.onSelectEndTime(hourOfDay, minute);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}