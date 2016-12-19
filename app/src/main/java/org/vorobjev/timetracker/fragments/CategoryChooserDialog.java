package org.vorobjev.timetracker.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.vorobjev.timetracker.R;
import org.vorobjev.timetracker.TimeTrackerApplication;
import org.vorobjev.timetracker.dao.CategoryDao;
import org.vorobjev.timetracker.entity.CategoryEntity;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CategoryChooserDialog extends DialogFragment {

    public interface OnCategoryChoosedListener {
        void onSelectCategory(String category);
    }

    public OnCategoryChoosedListener mListener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View sizeChooser = getActivity().getLayoutInflater().inflate(R.layout.view_choose_cat, null);
        List<CategoryEntity> cats = null;
        try {
            cats = TimeTrackerApplication.getInstance().getDbHelper().<CategoryDao, CategoryEntity>getDao(CategoryEntity.class).getCategories();
        } catch (SQLException e) {
        }
        List<String> catNames = new ArrayList<>();
        for (CategoryEntity categoryEntity : cats){
            catNames.add(categoryEntity.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, catNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner mapSpinner = ((Spinner) sizeChooser.findViewById(R.id.cat_spinner));
        mapSpinner.setAdapter(adapter);

        return new AlertDialog.Builder(getActivity())
                .setTitle("Select category")
                .setView(sizeChooser)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                mListener.onSelectCategory(mapSpinner.getSelectedItem().toString());
                            }
                        }
                ).create();
    }
}
