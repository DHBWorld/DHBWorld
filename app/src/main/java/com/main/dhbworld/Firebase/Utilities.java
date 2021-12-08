package com.main.dhbworld.Firebase;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class Utilities {
    private static final String emulatorIp = "193.41.237.154";

    public static final String CATEGORY_COFFEE = "coffee";
    public static final String CATEGORY_CAFETERIA = "cafeteria";
    public static final String CATEGORY_PRINTER = "printer";

    private static final long invalidateTime = 15 * 1000 * 60;
    private static final long invalidateTimeSwitch = 5 * 1000 * 60;
    private static final long invalidateTimeClicks = 1000 * 60;

    private final FirebaseAuth mAuth;
    private FirebaseUser user;
    private final FirebaseDatabase database;

    private SignedInListener signedInListener;
    private DataSendListener dataSendListener;
    private CurrentStatusListener currentStatusListener;
    private ReportCountListener reportCountListener;

    private long lastClick = 0;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    /**
     * Utilities constructor
     * @param context of the calling Class
     */

    public Utilities(Context context) {
        database = FirebaseDatabase.getInstance();
        database.useEmulator(emulatorIp, 9000);

        FirebaseAuth.getInstance().useEmulator(emulatorIp, 9099);
        mAuth = FirebaseAuth.getInstance();

        preferences = context.getApplicationContext().getSharedPreferences("firebase", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    /**
     * Sign in as anonymous user
     * @see #setSignedInListener(SignedInListener)
     */
    public void signIn() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        user = mAuth.getCurrentUser();
                        if (task.isSuccessful() && signedInListener != null) {
                            signedInListener.onSignedIn(mAuth.getCurrentUser());
                        } else if (signedInListener != null){
                            signedInListener.onSignInError();
                        }
                    }
                });
    }

    /**
     * Set a signedInListener
     * @param signedInListener gets set
     */
    public void setSignedInListener(SignedInListener signedInListener) {
        this.signedInListener = signedInListener;
    }

    /**
     * Set a dataSendListener
     * @param dataSendListener gets set
     */
    public void setDataSendListener(DataSendListener dataSendListener) {
        this.dataSendListener = dataSendListener;
    }

    /**
     * Set a currentStatusListener, gets called when the status is returned
     * @param currentStatusListener gets set
     */
    public void setCurrentStatusListener(CurrentStatusListener currentStatusListener) {
        this.currentStatusListener = currentStatusListener;
    }

    public void setReportCountListener(ReportCountListener reportCountListener) {
        this.reportCountListener = reportCountListener;
    }

    /**
     * Add an event to the database, too many clicks and other stuff already gets handled here
     * @param category is the category of the event
     * @param problem is true, if there is a problem and false if the problem is already solved
     * @see #setDataSendListener(DataSendListener)
     */
    public void addToDatabase(String category, int problem) {
        if (lastClick + invalidateTimeClicks > System.currentTimeMillis()) {
            lastClick = System.currentTimeMillis();
            if (dataSendListener != null) {
                dataSendListener.failed(new TooManyClicksException("Clicked too often"));
            }
            return;
        }
        lastClick = System.currentTimeMillis();
        DatabaseReference issuesDatabase = getIssueDatabaseWithUser(category);
        issuesDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    Issue issue = task.getResult().getValue(Issue.class);
                    if (issue == null || issue.getProblem() != problem || issue.getTimestamp() + invalidateTime < System.currentTimeMillis()) {
                        if (issue != null && issue.getProblem() != problem && issue.getTimestamp() + invalidateTimeSwitch >= System.currentTimeMillis()) {
                            if (preferences.getLong("last_switch_time", 0) + invalidateTimeSwitch >= System.currentTimeMillis()) {
                                if (dataSendListener != null) {
                                    dataSendListener.failed(new TooManyClicksException("Clicked too often"));
                                }
                                return;
                            }
                            editor.putLong("last_switch_time", System.currentTimeMillis());
                            editor.apply();
                        }
                        issuesDatabase.setValue(new Issue(System.currentTimeMillis(), problem));
                        dataSendListener.success();
                    }
                } else {
                    dataSendListener.failed(task.getException());
                }
            }
        });
    }

    /**
     * Removes an reported event from the databse
     * @param category is the category of the event
     */
    public void removeFromDatabase(String category) {
        DatabaseReference issuesDatabase = getIssueDatabaseWithUser(category);
        issuesDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && dataSendListener != null) {
                    dataSendListener.success();
                } else if (dataSendListener != null) {
                    dataSendListener.failed(task.getException());
                }
                if (task.isSuccessful() && task.getResult() != null) {
                    Issue issue = task.getResult().getValue(Issue.class);
                    if (issue == null) {
                        return;
                    }
                    if (issue.getTimestamp() + invalidateTime < System.currentTimeMillis()) {
                        issuesDatabase.setValue(null);
                    } else {
                        issuesDatabase.setValue(issue);
                    }
                }
            }
        });
    }

    /**
     * Gets the current Status of a set category
     * @param category is the category of which the status should be loaded
     * @see #setCurrentStatusListener(CurrentStatusListener)
     */
    public void getCurrentStatus(String category) {
        if (currentStatusListener == null) {
            return;
        }

        DatabaseReference issueDatabase = getIssueDatabase().child(category + "Status");
        issueDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    if (task.getResult().getValue() == null) {
                        currentStatusListener.onStatusReceived(category, 0);
                    } else {
                        currentStatusListener.onStatusReceived(category, task.getResult().getValue(int.class));
                    }
                }
            }
        });
    }

    /**
     * Get the report count of a set category
     * @param category of which the report count will be received
     * @see #setReportCountListener(ReportCountListener)
     */
    public void getReportCount(String category) {
        if (reportCountListener == null) {
            return;
        }
        DatabaseReference issueDatabase = getIssueDatabase(category);
        issueDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    reportCountListener.onReportCountReceived(category, task.getResult().getChildrenCount());
                } else {
                    reportCountListener.onReportCountReceived(category, 0);
                }
            }
        });
    }

    /**
     * Subscribe to a topic to which the push-notifications are send
     */
    public static void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("Test")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        System.out.println("subscribed");
                    }
                });
    }

    /**
     * @return the current FirebaseUser
     */
    public FirebaseUser getUser() {
        return user;
    }

    private DatabaseReference getIssueDatabaseWithUser(String category) {
        return database.getReference().child("issues").child(category).child(user.getUid() + System.currentTimeMillis());
    }

    private DatabaseReference getIssueDatabase(String category) {
        return database.getReference().child("issues").child(category);
    }

    private DatabaseReference getIssueDatabase() {
        return database.getReference().child("issues");
    }

}
