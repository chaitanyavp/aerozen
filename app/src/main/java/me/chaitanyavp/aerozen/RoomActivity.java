package me.chaitanyavp.aerozen;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class RoomActivity extends AppCompatActivity {

  /**
   * The {@link android.support.v4.view.PagerAdapter} that will provide
   * fragments for each of the sections. We use a
   * {@link FragmentPagerAdapter} derivative, which will keep every
   * loaded fragment in memory. If this becomes too memory intensive, it
   * may be best to switch to a
   * {@link android.support.v4.app.FragmentStatePagerAdapter}.
   */
  private SectionsPagerAdapter mSectionsPagerAdapter;
  private ArrayList<String> boardList;
  private HashMap<String, ChildEventListener> boardChildListeners;

  /**
   * The {@link ViewPager} that will host the section contents.
   */
  private ViewPager mViewPager;
  private FirebaseDatabase database;
  private String roomID;
  private TextView t;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_room);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    // Create the adapter that will return a fragment for each of the three
    // primary sections of the activity.
    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

    // Set up the ViewPager with the sections adapter.
    mViewPager = (ViewPager) findViewById(R.id.container);
    mViewPager.setAdapter(mSectionsPagerAdapter);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });

    Intent intent = getIntent();
    roomID = intent.getStringExtra("room_id");

    t = findViewById(R.id.test);
    t.setText("Started" + roomID);

    database = FirebaseDatabase.getInstance();
    boardList = new ArrayList<String>();
    boardChildListeners = new HashMap<String, ChildEventListener>();

    database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/rooms/"+roomID).addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
          addBoard(dataSnapshot.getKey(), s);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
          updateBoard(dataSnapshot.getKey());
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
          removeBoard(dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
          moveBoard(dataSnapshot.getKey(), s);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
  }

  private void addBoard(String key, String prev){
    if (prev == null){
      boardList.add(0, key);
    }
    else{
      boardList.add( boardList.indexOf(prev)+1, key);
    }
    t.setText(t.getText()+ "added"+key);
    ChildEventListener boardEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            addBoard(dataSnapshot.getKey(), s);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            updateBoard(dataSnapshot.getKey());
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            removeBoard(dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            moveBoard(dataSnapshot.getKey(), s);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    boardChildListeners.put(key, boardEventListener);
    database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/boards/"+roomID+"_"+key).addChildEventListener(boardEventListener);
    mSectionsPagerAdapter.notifyDataSetChanged();
  }

  private void removeBoard(String key){
    boardList.remove(key);
    ChildEventListener childEventListener = boardChildListeners.remove(key);
    database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/boards/"+roomID+"_"+key).removeEventListener(childEventListener);
    t.setText(t.getText() + "Removed"+key);
    mSectionsPagerAdapter.notifyDataSetChanged();
  }

  private void moveBoard(String key, String prev){
    boardList.remove(key);
    if (prev == null){
      boardList.add(0, key);
    }
    else{
      boardList.add(boardList.indexOf(prev) + 1, key);
    }
    t.setText(t.getText() + "moved"+key);
    mSectionsPagerAdapter.notifyDataSetChanged();
  }

  private void updateBoard(String key){
    t.setText(t.getText() + "updated"+key);
    mSectionsPagerAdapter.notifyDataSetChanged();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
//    getMenuInflater().inflate(R.menu.menu_room, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /**
   * A placeholder fragment containing a simple view.
   */
  public static class PlaceholderFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static FirebaseDatabase databaseRef;

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber, ArrayList<String> boards, FirebaseDatabase databaseToUse) {
      PlaceholderFragment fragment = new PlaceholderFragment();
      Bundle args = new Bundle();
      databaseRef = databaseToUse;
      args.putInt(ARG_SECTION_NUMBER, sectionNumber);
      args.putStringArrayList("boardList", boards);
      fragment.setArguments(args);
      return fragment;
    }

    public static CardView addCard(LinearLayout parent, String text, Context context){
      CardView newCard = new CardView(context);
//      newCard.setOnClickListener(new OnClickListener() {
//          @Override
//          public void onClick(View view) {
//              goToRoom(room_id);
//          }
//      });
      TextView textView = new TextView(context);
      textView.setText(text);
      newCard.addView(textView);
      parent.addView(newCard);
      ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) newCard.getLayoutParams();
      params.width = 200; params.leftMargin = 100; params.topMargin = 200;
      return newCard;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_room, container, false);
      TextView textView = (TextView) rootView.findViewById(R.id.section_label);
      textView
          .setText(getArguments().getStringArrayList("boardList").get(getArguments().getInt(ARG_SECTION_NUMBER)));
      TextView view = new TextView(rootView.getContext());

      return rootView;
    }

//    @Override
//    public View onDestroyView(){
//
//    }
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, ArrayList<String>){
//
//    }
  }

  /**
   * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
   * one of the sections/tabs/pages.
   */
  public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
      super(fm);

    }

    @Override
    public Fragment getItem(int position) {
      // getItem is called to instantiate the fragment for the given page.
      // Return a PlaceholderFragment (defined as a static inner class below).
      return PlaceholderFragment.newInstance(position, boardList, database);
    }

    @Override
    public int getCount() {
      if(boardList == null) {
        return 0;
      }
      return boardList.size();
    }
  }
}
