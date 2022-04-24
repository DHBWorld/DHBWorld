package com.main.dhbworld.KVV;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.dhbworld.R;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;

public class KVVListAdapter extends RecyclerView.Adapter<KVVListAdapter.ViewHolder> {

   private final ArrayList<Departure> departures;

   public KVVListAdapter(ArrayList<Departure> departures) {
      this.departures = departures;
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kvv_list_item, parent, false);
      return new ViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      holder.getTextViewLine().setText(departures.get(position).getLine());
      holder.getTextViewDestination().setText(departures.get(position).getDestination());
      holder.getTextViewPlatform().setText(departures.get(position).getPlatform());

      DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
      holder.getTextViewDeparture().setText(departures.get(position).getDepartureTime().format(formatter));
   }

   @Override
   public int getItemCount() {
      return departures.size();
   }

   public static class ViewHolder extends RecyclerView.ViewHolder {
      private final TextView textViewLine;
      private final TextView textViewDestination;
      private final TextView textViewPlatform;
      private final TextView textViewDeparture;

      public ViewHolder(View view) {
         super(view);

         textViewLine = view.findViewById(R.id.tram_line);
         textViewDestination = view.findViewById(R.id.tram_destination);
         textViewPlatform = view.findViewById(R.id.tram_platform);
         textViewDeparture = view.findViewById(R.id.tram_departure);

      }

      public TextView getTextViewLine() {
         return textViewLine;
      }

      public TextView getTextViewDestination() {
         return textViewDestination;
      }

      public TextView getTextViewPlatform() {
         return textViewPlatform;
      }

      public TextView getTextViewDeparture() {
         return textViewDeparture;
      }
   }
}
