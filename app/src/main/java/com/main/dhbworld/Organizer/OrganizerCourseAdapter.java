package com.main.dhbworld.Organizer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.*;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.dhbworld.ProgressIndicator;
import com.main.dhbworld.R;
import java.util.ArrayList;
import java.util.Objects;

public class OrganizerCourseAdapter extends RecyclerView.Adapter<OrganizerCourseAdapter.ViewHolder>{
    ArrayList<Course> courses;
    Context context;
    FirebaseFirestore firestore;

    public OrganizerCourseAdapter(Context context, ArrayList<Course> courses) {
        this.context = context;
        this.courses = courses;
        firestore= FirebaseFirestore.getInstance();
        firestore.collection("Courses").get();

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
        // holder.tvName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_book_24,0,0,0);
        holder.tvHome.setText(course.getStudy());
        // Return the completed view to render on screen
        setOnClickListener(holder.itemView,course);
    }

    @Override
    public int getItemCount() {
        // returning the size of array list.
        return courses.size();
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
              //  bottomSheetDialog.show();

                DocumentReference contact= firestore.collection("Courses").document(course.name.toLowerCase());
                contact.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot doc= task.getResult();
                            course.setCourseDirector(doc.getString("CourseDirector"));
                            if (course.url==null){
                                course.setUrl(doc.getString("URL"));
                            }
                        }
                        try {
                            TextView entryView = bottomSheetDialog.findViewById(R.id.organizerCourseEntryText);
                            Objects.requireNonNull(entryView).setText(course.name);
                            TextView studyView = bottomSheetDialog.findViewById(R.id.organizerCourseStudyText);
                            Objects.requireNonNull(studyView).setText(context.getString(R.string.study, course.study));
                            TextView yearView = bottomSheetDialog.findViewById(R.id.organizerCourseYearText);
                            Objects.requireNonNull(yearView).setText(context.getString(R.string.year, String.valueOf(course.year)));
                            TextView roomView = bottomSheetDialog.findViewById(R.id.organizerCourseRoomText);


                            if(course.roomNo != null && roomView != null) {
                                (roomView).setText(context.getString(R.string.room_placeholder, course.roomNo));
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
                            TextView directorView = bottomSheetDialog.findViewById(R.id.organizerCourseDirectorText);
                            if(course.courseDirector != null && directorView != null) {
                                (directorView).setText(context.getString(R.string.course_director, course.courseDirector));
                            }
                            else{
                                assert directorView != null;
                                (directorView).setVisibility(View.GONE);
                            }
                            bottomSheetDialog.show();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                });




            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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