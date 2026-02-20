package com.example.projectuiprototype.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.projectuiprototype.R;
import com.example.projectuiprototype.models.Shift;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ShiftListAdapter extends BaseAdapter {

    public interface OnDeleteClick {
        void onDelete(Shift shift);
    }

    private final List<Shift> shifts;
    private final OnDeleteClick onDeleteClick;

    public ShiftListAdapter(List<Shift> shifts, OnDeleteClick onDeleteClick) {
        this.shifts = shifts;
        this.onDeleteClick = onDeleteClick;
    }

    @Override
    public int getCount() {
        return shifts.size();
    }

    @Override
    public Object getItem(int position) {
        return shifts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_shift_row, parent, false);
        }

        Shift shift = shifts.get(position);

        TextView title = row.findViewById(R.id.txtShiftTitle);
        TextView time = row.findViewById(R.id.txtShiftTime);
        MaterialButton deleteBtn = row.findViewById(R.id.btnDeleteShift);

        title.setText(shift.day);
        time.setText(shift.time);

        deleteBtn.setOnClickListener(v -> onDeleteClick.onDelete(shift));

        return row;
    }
}
