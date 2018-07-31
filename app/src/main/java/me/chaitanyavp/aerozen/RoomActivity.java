package me.chaitanyavp.aerozen;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TimePicker;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
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
  private HashMap<String, ArrayList<String>> boardTaskList;
  private Calendar calendar;
  private int TEN_DP;

  /**
   * The {@link ViewPager} that will host the section contents.
   */
  private ViewPager mViewPager;
  private FirebaseDatabase database;
  private String roomID;
  private String userID;
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

    calendar = Calendar.getInstance();

    Log.w("BAD", "we have began");
    TEN_DP = (int) TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        10,
        getResources().getDisplayMetrics()
    );

    FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
    fab1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action bt1", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });

    FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
    fab2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action bt2", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });

    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Add task");
    final LinearLayout alertLayout = new LinearLayout(this);
    LinearLayout.LayoutParams alertLayoutParams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    alertLayoutParams.setMargins(TEN_DP, TEN_DP, TEN_DP, 0);
    alertLayout.setGravity(Gravity.CENTER_HORIZONTAL);
    alertLayout.setOrientation(LinearLayout.VERTICAL);
    alertLayout.setLayoutParams(alertLayoutParams);
    alertLayout.setPadding(TEN_DP, TEN_DP, TEN_DP, TEN_DP);

    final LinearLayout dueDateLayout = new LinearLayout(this);
    LinearLayout.LayoutParams dueDateLayoutParams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//    dueDateLayoutParams.setMargins(TEN_DP, TEN_DP, TEN_DP, 0);
    dueDateLayout.setGravity(Gravity.CENTER_HORIZONTAL);
    dueDateLayout.setOrientation(LinearLayout.HORIZONTAL);
    dueDateLayout.setLayoutParams(dueDateLayoutParams);

    final TimePickerDialog timePickerDialog = new TimePickerDialog(this, new OnTimeSetListener() {
      @Override
      public void onTimeSet(TimePicker timePicker, int i, int i1) {

      }
    }, 23, 59, false);

    final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
      @Override
      public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        timePickerDialog.show();
      }
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

    CheckBox dueDateCheckBox = new CheckBox(this);
    dueDateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
       @Override
       public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          if(isChecked){
            datePickerDialog.show();
          }
          else{

          }
       }
     }
    );
    TextView dueDateLabel = new TextView(this);
    dueDateLabel.setText(" Due Date");
    dueDateLayout.addView(dueDateCheckBox);
    dueDateLayout.addView(dueDateLabel);

    final EditText taskInput = new EditText(this);
    taskInput.setInputType(InputType.TYPE_CLASS_TEXT);
    final SeekBar priority = new SeekBar(this);
    alertLayout.addView(taskInput);
    alertLayout.addView(dueDateLayout);
    alertLayout.addView(priority);
    builder.setView(alertLayout);

    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        createTask(taskInput.getText().toString());
      }
    });
    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    final AlertDialog dialog = builder.create();

    FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
    fab3.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dialog.show();
        Snackbar.make(view, "Replace with your own action bt3", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });

    Intent intent = getIntent();
    roomID = intent.getStringExtra("room_id");
    userID = intent.getStringExtra("user_id");

    t = findViewById(R.id.test);
    t.setText("Started" + roomID);

    database = FirebaseDatabase.getInstance();
    boardList = new ArrayList<String>();
    boardChildListeners = new HashMap<String, ChildEventListener>();
    boardTaskList = new HashMap<String, ArrayList<String>>();

    database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/rooms/" + roomID)
        .addChildEventListener(new ChildEventListener() {
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
    Log.w("BAD", "on child finished");
  }

  private void addBoard(final String key, String prev) {
    if (prev == null) {
      boardList.add(0, key);
    } else {
      boardList.add(boardList.indexOf(prev) + 1, key);
    }
    t.setText(t.getText() + "added" + key);
    boardTaskList.put(key, new ArrayList<String>());
    ChildEventListener boardEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        ArrayList<String> tasks = boardTaskList.get(key);
        tasks.add(tasks.indexOf(s) + 1, (String) dataSnapshot.getValue());
        Log.w("BAD", "task child added");
        t.setText(t.getText() + " task child added");
        mSectionsPagerAdapter.updateTasks(boardTaskList);
      }

      @Override
      public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        ArrayList<String> tasks = boardTaskList.get(key);
        tasks.remove(tasks.indexOf(s) + 1);
        tasks.add(tasks.indexOf(s) + 1, (String) dataSnapshot.getValue());
        t.setText(t.getText() + " task child changed");
        mSectionsPagerAdapter.notifyDataSetChanged();
      }

      @Override
      public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        ArrayList<String> tasks = boardTaskList.get(key);
        tasks.remove((String) dataSnapshot.getValue());
        t.setText(t.getText() + " task child removed");
        mSectionsPagerAdapter.notifyDataSetChanged();
      }

      @Override
      public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        ArrayList<String> tasks = boardTaskList.get(key);
        String value = (String) dataSnapshot.getValue();
        tasks.remove(value);
        if (s == null) {
          tasks.add(0, key);
        } else {
          tasks.add(tasks.indexOf(s) + 1, key);
        }
        t.setText(t.getText() + " task child moved");
        mSectionsPagerAdapter.notifyDataSetChanged();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    };
    boardChildListeners.put(key, boardEventListener);
    database.getReferenceFromUrl(
        "https://kanban-f611c.firebaseio.com/boards/" + roomID + "_" + key + "/tasks")
        .addChildEventListener(boardEventListener);
    mSectionsPagerAdapter.updateTasks(boardTaskList);
    mSectionsPagerAdapter.notifyDataSetChanged();
  }

  private void removeBoard(String key) {
    boardList.remove(key);
    ChildEventListener childEventListener = boardChildListeners.remove(key);
    database.getReferenceFromUrl(
        "https://kanban-f611c.firebaseio.com/boards/" + roomID + "_" + key + "/tasks")
        .removeEventListener(childEventListener);
    t.setText(t.getText() + "Removed" + key);
    mSectionsPagerAdapter.updateTasks(boardTaskList);
    mSectionsPagerAdapter.notifyDataSetChanged();
  }

  private void moveBoard(String key, String prev) {
    boardList.remove(key);
    if (prev == null) {
      boardList.add(0, key);
    } else {
      boardList.add(boardList.indexOf(prev) + 1, key);
    }
    t.setText(t.getText() + "moved" + key);
    mSectionsPagerAdapter.updateTasks(boardTaskList);
    mSectionsPagerAdapter.notifyDataSetChanged();
  }

  private void updateBoard(String key) {
    t.setText(t.getText() + "updated" + key);
    mSectionsPagerAdapter.updateTasks(boardTaskList);
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

  private void createTask(String task) {
    int position = mViewPager.getCurrentItem();
    database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/boards/"
        + roomID + "_" + boardList.get(position) + "/tasks/")
        .child(userID + System.currentTimeMillis())
        .setValue(task);
  }

  private AlertDialog createTaskDialog(Task task) {
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Update Task");
    final LinearLayout alertLayout = new LinearLayout(this);
    LinearLayout.LayoutParams alertLayoutParams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    alertLayoutParams.setMargins(TEN_DP, TEN_DP, TEN_DP, 0);
    alertLayout.setGravity(Gravity.CENTER_HORIZONTAL);
    alertLayout.setOrientation(LinearLayout.VERTICAL);
    alertLayout.setLayoutParams(alertLayoutParams);

    final EditText taskInput = new EditText(this);
    taskInput.setInputType(InputType.TYPE_CLASS_TEXT);

    taskInput.setText(task.getText());

    final SeekBar priority = new SeekBar(this);
    priority.setMax(100);
    priority.setProgress(task.getPriority());
    alertLayout.addView(taskInput);
    alertLayout.addView(priority);
    builder.setView(alertLayout);

    builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        createTask(taskInput.getText().toString());
      }
    });
    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    final AlertDialog dialog = builder.create();
    return dialog;
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
    private static HashMap<String, ArrayList<String>> taskMapList;

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber, ArrayList<String> boards,
        HashMap<String, ArrayList<String>> initialTaskMapList) {
      PlaceholderFragment fragment = new PlaceholderFragment();
      Bundle args = new Bundle();
      taskMapList = initialTaskMapList;
      args.putInt(ARG_SECTION_NUMBER, sectionNumber);
      args.putStringArrayList("boardList", boards);
      fragment.setArguments(args);
      return fragment;
    }

    public static void updateTaskMapList(HashMap<String, ArrayList<String>> newTaskMapList) {
      taskMapList = newTaskMapList;
    }

//    public static void updateTaskList(String key){
//        for (String task : taskMapList.get(key)){
//            addCard()
//        }
//    }

    public static CardView addCard(LinearLayout parent, String text, Context context) {
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

      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.MATCH_PARENT,
          LinearLayout.LayoutParams.WRAP_CONTENT
      );
      Resources r = context.getResources();
      final int TEN_DP = (int) TypedValue.applyDimension(
          TypedValue.COMPLEX_UNIT_DIP,
          10,
          r.getDisplayMetrics()
      );
      params.setMargins(TEN_DP, TEN_DP, TEN_DP, 0);
      newCard.setLayoutParams(params);

      CardView.LayoutParams cardParams = new CardView.LayoutParams(
          CardView.LayoutParams.WRAP_CONTENT,
          CardView.LayoutParams.WRAP_CONTENT
      );
      final int SIXTEEN_DP = (int) TypedValue.applyDimension(
          TypedValue.COMPLEX_UNIT_DIP,
          16,
          r.getDisplayMetrics()
      );
      cardParams.setMargins(SIXTEEN_DP, SIXTEEN_DP, SIXTEEN_DP, SIXTEEN_DP);
      textView.setLayoutParams(cardParams);

      return newCard;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
      View rootView = inflater.inflate(R.layout.fragment_room, container, false);
      Context context = rootView.getContext();
      int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
      String boardName = getArguments().getStringArrayList("boardList").get(sectionNumber);

      TextView textView = (TextView) rootView.findViewById(R.id.section_label);
      textView.setText(boardName);
      LinearLayout parentLayout = rootView.findViewById(R.id.task_layout);

      for (String task : taskMapList.get(boardName)) {
        addCard(parentLayout, task, context);
      }

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
    public int getItemPosition(Object object) {
      return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
      // getItem is called to instantiate the fragment for the given page.
      // Return a PlaceholderFragment (defined as a static inner class below).
      return PlaceholderFragment.newInstance(position, boardList, boardTaskList);
    }

    @Override
    public int getCount() {
      if (boardList == null) {
        return 0;
      }
      return boardList.size();
    }

    public void updateTasks(HashMap<String, ArrayList<String>> newTaskList) {
      PlaceholderFragment.updateTaskMapList(newTaskList);
      this.notifyDataSetChanged();
//      FragmentTransaction tr = getFragmentManager().beginTransaction();
//      tr.replace(R.id.container, PlaceholderFragment.this);
//      tr.commit();
//        Fragment currentFragment = this.getActivity().getFragmentManager().findFragmentById(R.id.container);
//        if (currentFragment instanceof PlaceholderFragment) {
//            FragmentTransaction fragTransaction =   (getActivity()).getFragmentManager().beginTransaction();
//            fragTransaction.detach(currentFragment);
//            fragTransaction.attach(currentFragment);
//            fragTransaction.commit();}
//        }
    }
  }
}
