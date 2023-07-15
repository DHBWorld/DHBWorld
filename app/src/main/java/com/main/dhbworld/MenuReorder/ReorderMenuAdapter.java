package com.main.dhbworld.MenuReorder;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.main.dhbworld.R;

import java.util.ArrayList;
import java.util.Collections;

public class ReorderMenuAdapter extends RecyclerView.Adapter<ReorderMenuAdapter.ViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {

    private final Activity activity;
    private final ArrayList<MenuItem> menuItems;

    public ReorderMenuAdapter(Activity activity, ArrayList<MenuItem> menuItems) {
        this.activity = activity;
        this.menuItems = menuItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reorder_menu_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReorderMenuAdapter.ViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);
        holder.textView.setText(menuItem.getTitle());
        if (menuItem.getTitle().equals(activity.getResources().getString(R.string.home))) {
            holder.imageView.setVisibility(View.GONE);
        }

        styleItems(holder, menuItem);

        holder.itemView.setOnClickListener(v -> {
            if (menuItem.getTitle().equals(activity.getResources().getString(R.string.home))) {
                Snackbar.make(activity.findViewById(android.R.id.content), R.string.cannot_hide_home, BaseTransientBottomBar.LENGTH_SHORT).show();
                return;
            }
            MenuItem menuItem1 = menuItems.get(holder.getAdapterPosition());
            menuItem1.setHidden(!menuItem1.isHidden());
            notifyItemChanged(holder.getAdapterPosition());
        });
    }

    private void styleItems(@NonNull ViewHolder holder, MenuItem menuItem) {
        if (menuItem.isHidden()) {
            holder.textView.setPaintFlags(holder.textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.imageView.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.baseline_visibility_off_24));
        } else {
            holder.textView.setPaintFlags(holder.textView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            holder.imageView.setImageDrawable(AppCompatResources.getDrawable(activity, R.drawable.baseline_visibility_24));
        }
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(menuItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(menuItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onRowSelected(ViewHolder viewHolder) {
        viewHolder.itemView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(ViewHolder viewHolder) {
        TypedValue typedValue = new TypedValue();
        activity.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
        viewHolder.itemView.setBackgroundResource(typedValue.resourceId);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textView;
        private final ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.reorder_menu_item_text);
            imageView = itemView.findViewById(R.id.reorder_menu_item_visibility_image);
        }
    }
}