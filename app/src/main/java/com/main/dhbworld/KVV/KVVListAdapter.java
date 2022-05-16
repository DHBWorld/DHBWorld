package com.main.dhbworld.KVV;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.main.dhbworld.R;

import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Objects;

public class KVVListAdapter extends RecyclerView.Adapter<KVVListAdapter.ViewHolder> {

   private final ArrayList<Departure> departures;
   private final Context context;

   public KVVListAdapter(Context context, ArrayList<Departure> departures) {
      this.departures = departures;
      this.context = context;
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kvv_list_item, parent, false);
      return new ViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      String notServicedText = "";
      if (departures.get(position).isNotServiced()) {
         Drawable warning = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_info_24);
         Objects.requireNonNull(warning).setTint(context.getColor(R.color.red));
         holder.getImageView().setImageDrawable(warning);
         holder.getTramCardLayout().setStrokeColor(context.getColor(R.color.red));

         notServicedText = context.getString(R.string.canceled_in_parens);
      }

      holder.getTextViewLine().setText(departures.get(position).getLine());
      holder.getTextViewDestination().setText(departures.get(position).getDestination());
      holder.getTextViewPlatform().setText(departures.get(position).getPlatform());

      DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
      holder.getTextViewDeparture().setText(MessageFormat.format("{0} {1}", departures.get(position).getDepartureTime().format(formatter), notServicedText).trim());
   }

   @Override
   public int getItemCount() {
      return departures.size();
   }

   public static class ViewHolder extends RecyclerView.ViewHolder {
      private final MaterialCardView tramCardLayout;
      private final ImageView imageView;
      private final TextView textViewLine;
      private final TextView textViewDestination;
      private final TextView textViewPlatform;
      private final TextView textViewDeparture;

      public ViewHolder(View view) {
         super(view);

         tramCardLayout = view.findViewById(R.id.tram_card_layout);
         imageView = view.findViewById(R.id.tram_imageview);
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

      public ImageView getImageView() {
         return imageView;
      }

      public MaterialCardView getTramCardLayout() {
         return tramCardLayout;
      }
   }
}
