package com.main.dhbworld.Dualis.view.tabs.documents;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.main.dhbworld.R;

import java.util.ArrayList;

class DualisDocumentAdapter extends RecyclerView.Adapter<DualisDocumentAdapter.ViewHolder> implements View.OnClickListener {

   private final ArrayList<DualisDocument> documents;
   private final Context context;

   public DualisDocumentAdapter(Context context, ArrayList<DualisDocument> documents) {
      this.context = context;
      this.documents = documents;
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dualis_document_item, parent, false);
      view.setOnClickListener(this);
      return new ViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull DualisDocumentAdapter.ViewHolder holder, int position) {
      holder.textViewName.setText(documents.get(position).getName());
      holder.textViewDate.setText(documents.get(position).getDate());
      holder.textViewUrl.setText(documents.get(position).getUrl());
   }

   @Override
   public int getItemCount() {
      return documents.size();
   }

   @Override
   public void onClick(View view) {
      TextView textViewUrl = view.findViewById(R.id.dualis_document_id);
      String url = textViewUrl.getText().toString();

      Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://dualis.dhbw.de" + url));
      try {
         context.startActivity(browserIntent);
      } catch (Exception ignored) { }
   }

   public static class ViewHolder extends RecyclerView.ViewHolder {
      final TextView textViewName;
      final TextView textViewDate;
      final TextView textViewUrl;

      public ViewHolder(View view) {
         super(view);

         textViewName = view.findViewById(R.id.dualis_document_name);
         textViewDate = view.findViewById(R.id.dualis_document_date);
         textViewUrl = view.findViewById(R.id.dualis_document_id);
      }
   }
}
