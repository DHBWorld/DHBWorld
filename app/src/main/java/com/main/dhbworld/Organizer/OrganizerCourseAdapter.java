package com.main.dhbworld.Organizer;
import android.annotation.SuppressLint;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.main.dhbworld.R;
import java.util.ArrayList;
import java.util.Objects;

public class OrganizerCourseAdapter extends RecyclerView.Adapter<OrganizerCourseAdapter.ViewHolder>{
    ArrayList<Course> courses;
    ArrayList<Course> filteredData;

    public OrganizerCourseAdapter(ArrayList<Course> courses) {
        this.courses = courses;
        filteredData = new ArrayList<>(courses);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // below line is to inflate our layout.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizer_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrganizerCourseAdapter.ViewHolder holder, int position) {
        // setting data to our views of recycler view.
        Course course = courses.get(position);
        holder.tvName.setText(course.getName());
        holder.tvHome.setText(course.getStudy());
        // Return the completed view to render on screen
        setOnClickListener(holder.itemView,course);
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return filteredData.size();
    }

    public Object getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void setOnClickListener(View newRow, Course course){
        newRow.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(newRow.getContext());

                bottomSheetDialog.setContentView(R.layout.organizercoursebottomsheet);
                bottomSheetDialog.show();

                try {
                    TextView entryView = bottomSheetDialog.findViewById(R.id.organizerCourseEntryText);
                    Objects.requireNonNull(entryView).setText(course.name);
                    TextView studyView = bottomSheetDialog.findViewById(R.id.organizerCourseStudyText);
                    Objects.requireNonNull(studyView).setText("Study: " + course.study);
                    TextView yearView = bottomSheetDialog.findViewById(R.id.organizerCourseYearText);
                    Objects.requireNonNull(yearView).setText("Year: " + course.year);
                    TextView roomView = bottomSheetDialog.findViewById(R.id.organizerCourseRoomText);
                    if(course.roomNo != null && roomView != null) {
                        (roomView).setText("Room: " + course.roomNo);
                    }
                    else{
                        assert roomView != null;
                        (roomView).setVisibility(View.GONE);
                    }
                    TextView urlText = bottomSheetDialog.findViewById(R.id.organizerCourseUrlText);
                    if(course.url != null && urlText != null) {
                        (urlText).setText("URL: " + Html.fromHtml(course.url,0));
                        urlText.setMovementMethod(LinkMovementMethod.getInstance());
                    }
                    else{
                        (roomView).setVisibility(View.GONE);
                    }


                    bottomSheetDialog.show();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our views.
        private final TextView tvName;
        private final TextView tvHome;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our views with their ids.
            tvName = itemView.findViewById(R.id.info_box1);
            tvHome = itemView.findViewById(R.id.info_box2);

        }
    }
}