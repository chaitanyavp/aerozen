package me.chaitanyavp.aerozen;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class WelcomeActivity extends AppCompatActivity {

  private static final int RC_SIGN_IN = 123;
  private FirebaseUser user;
  private FirebaseAuth mAuth;
  private GoogleSignInClient mGoogleSignInClient;
  private FirebaseDatabase database;

  private HashMap<String, String> userRooms;

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
    userRooms = new HashMap<>();
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
    for (HashMap.Entry<String, String> room : userRooms.entrySet()) {
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
    database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/rooms/emailToUid/"+formattedEmail).setValue(user.getUid());
    database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/rooms/uidToEmail/"+user.getUid()).setValue(user.getEmail());
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
        database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/user_rooms/" + user.getUid())
            .addChildEventListener(new ChildEventListener() {
              @Override
              public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                userRooms.put(dataSnapshot.getValue().toString(), dataSnapshot.getKey());
                updateUI();
              }

              @Override
              public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                userRooms.put(dataSnapshot.getValue().toString(), dataSnapshot.getKey());
                updateUI();
              }

              @Override
              public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                userRooms.remove(dataSnapshot.getValue().toString());
                updateUI();
              }

              @Override
              public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {
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

