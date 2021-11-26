package com.main.dhbworld.Firebase;

import com.google.firebase.auth.FirebaseUser;

public interface SignedInListener {
    void onSignedIn(FirebaseUser user);
    void onSignInError();
}
