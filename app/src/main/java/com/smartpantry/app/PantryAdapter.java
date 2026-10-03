package com.smartpantry.app;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Custom adapter that binds PantryItem objects to item_pantry.xml rows. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.VH> {

    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private List<PantryItem> items = new ArrayList<>();
    private boolean alertsOn = true;
    private final Listener listener;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItem> items, boolean alertsOn) {
        this.items = items;
        this.alertsOn = alertsOn;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        PantryItem item = items.get(position);
        h.tvName.setText(item.getName());
        h.tvQty.setText(UnitUtil.format(item.getQuantity()) + " " + item.getUnit());

        String exp = item.getExpiry();
        if (exp == null || exp.isEmpty()) {
            h.tvExpiry.setText("No expiry date");
            h.tvExpiry.setTextColor(Color.parseColor("#666666"));
        } else {
            long days = daysUntil(exp);
            if (alertsOn && days < 0) {
                h.tvExpiry.setText("Expired on " + exp);
                h.tvExpiry.setTextColor(Color.parseColor("#C62828"));
            } else if (alertsOn && days <= 3) {
                h.tvExpiry.setText("Expires in " + days + " day(s)");
                h.tvExpiry.setTextColor(Color.parseColor("#E65100"));
            } else {
                h.tvExpiry.setText("Expires: " + exp);
                h.tvExpiry.setTextColor(Color.parseColor("#666666"));
            }
        }

        h.itemView.setOnClickListener(v -> listener.onEdit(item));
        h.btnEdit.setOnClickListener(v -> listener.onEdit(item));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private static long daysUntil(String yyyyMmDd) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(yyyyMmDd);
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            return Math.round((date.getTime() - today.getTimeInMillis()) / 86400000.0);
        } catch (ParseException e) {
            return Long.MAX_VALUE;
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvExpiry;
        ImageButton btnEdit, btnDelete;

        VH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvQty = v.findViewById(R.id.tvQty);
            tvExpiry = v.findViewById(R.id.tvExpiry);
            btnEdit = v.findViewById(R.id.btnEdit);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}
