package me.chaitanyavp.aerozen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class WelcomeActivity extends AppCompatActivity {

  private static final int RC_SIGN_IN = 123;
  private FirebaseUser user;
  private FirebaseAuth mAuth;
  private GoogleSignInClient mGoogleSignInClient;
  private FirebaseDatabase database;

  private HashMap<String, String> user_rooms;

  // UI references.
  private LinearLayout roomLayout;
  private TextView title;

  private static final String TAG = "WelcomeActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);
    System.out.println("STARTING");
    title = findViewById(R.id.title_text);
    roomLayout = findViewById(R.id.roomlist);
//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference myRef = database.getReference("message");
//    DatabaseReference test = database.getReference("test").setValue("verybad");
//    test.setValue("verybad");
//
//    myRef.setValue("Hello, World!");

    List<AuthUI.IdpConfig> providers = Arrays.asList(
        new AuthUI.IdpConfig.EmailBuilder().build(),
        new AuthUI.IdpConfig.PhoneBuilder().build(),
        new AuthUI.IdpConfig.GoogleBuilder().build());
    startActivityForResult(
        AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build(),
        RC_SIGN_IN);

    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getString(R.string.proper_web_client_id))
        .requestEmail()
        .build();
    mAuth = FirebaseAuth.getInstance();
    mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
  }

  protected void onStart() {
    super.onStart();
//    if(database != null){
//      updateUI();
//    }
  }

  private void goToRoom(String roomKey){
    Log.w(TAG, "going to "+roomKey);
    title.setText("Going to "+roomKey);
    Intent intent = new Intent(this, RoomActivity.class);
    intent.putExtra("room_id", roomKey);
    intent.putExtra("user_id", user.getUid());
    intent.putExtra("user_email", user.getEmail());
    startActivity(intent);
  }

  private void updateUI() {
    roomLayout.removeAllViews();
    for (HashMap.Entry<String, String> room : user_rooms.entrySet()) {
      final String room_name = room.getKey();
      final String room_id = room.getValue();
      Button roomButton = new Button(this);
      roomButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          goToRoom(room_id);
        }
      });
      roomButton.setText(room_name);
      roomLayout.addView(roomButton);
    }
  }

  private void createUser(){
    final String formattedEmail = user.getEmail().replace('.', ',');
    database.getReference().child("emailToUid").child(formattedEmail).setValue(user.getUid());
    database.getReference().child("uidToEmail").child(user.getUid()).setValue(user.getEmail());
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    System.out.println("onActivityResult");
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RC_SIGN_IN) {
      IdpResponse response = IdpResponse.fromResultIntent(data);

      if (resultCode == RESULT_OK) {
        // Successfully signed in
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        createUser();

        database.getReference().child("user_rooms").child(user.getUid()).addValueEventListener(
            new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                user_rooms = new HashMap<String,String>();
                title.setText(title.getText() + " onDataChange");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                  user_rooms.put((String) snapshot.getValue(), snapshot.getKey());
                }
                updateUI();
              }
              @Override
              public void onCancelled(DatabaseError error) {

              }
            });

        Button my_room_button = findViewById(R.id.my_room_button);
        my_room_button.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View view) {
            goToRoom(user.getUid());
          }
        });
      } else {
        // response.getError().getErrorCode() and handle the error.
        System.out.println("bad user" + response);
      }
    }
  }
}

