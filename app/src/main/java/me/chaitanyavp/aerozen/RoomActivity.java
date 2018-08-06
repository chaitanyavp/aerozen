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
import android.view.View.OnClickListener;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
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
  private HashMap<String, HashMap<String, Task>> boardTaskList;
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
    System.out.println("VERY GOOD 1");
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

    final AlertDialog dialog = createTaskDialog(null);

    FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
    fab3.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dialog.show();
        Snackbar.make(view, "Replace with your own action bt3", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show();
      }
    });
    System.out.println("VERY GOOD");
    Intent intent = getIntent();
    roomID = intent.getStringExtra("room_id");
    userID = intent.getStringExtra("user_id");

    t = findViewById(R.id.test);
    t.setText("Started" + roomID);

    database = FirebaseDatabase.getInstance();
    boardList = new ArrayList<String>();
    boardChildListeners = new HashMap<String, ChildEventListener>();
    boardTaskList = new HashMap<String, HashMap<String, Task>>();

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

  private void addBoard(final String boardName, String prev) {
    if (prev == null) {
      boardList.add(0, boardName);
    } else {
      boardList.add(boardList.indexOf(prev) + 1, boardName);
    }
    t.setText(t.getText() + "added" + boardName);
    boardTaskList.put(boardName, new HashMap<String, Task>());
    ChildEventListener boardEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        HashMap<String, Task> tasks = boardTaskList.get(boardName);
        Task task = getTaskFromDatabaseAndAddListeners(dataSnapshot.getKey(), (String) dataSnapshot.getValue());
        task.setText((String) dataSnapshot.getValue());
        tasks.put(dataSnapshot.getKey(), task);
        Log.w("BAD", "task child added");
        t.setText(t.getText() + " task child added");
        mSectionsPagerAdapter.updateTasks(boardTaskList);
      }

      @Override
      public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        HashMap<String, Task> tasks = boardTaskList.get(boardName);
        tasks.get(dataSnapshot.getKey()).setText((String) dataSnapshot.getValue());
        t.setText(t.getText() + " task child changed");
        mSectionsPagerAdapter.notifyDataSetChanged();
      }

      @Override
      public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        //TODO: Handle child removals (clear event listeners)
        HashMap<String, Task> tasks = boardTaskList.get(boardName);
        Task removed = tasks.remove(dataSnapshot.getKey());
        removed.removeAllListeners(database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/"));
        t.setText(t.getText() + " task child removed");
        mSectionsPagerAdapter.notifyDataSetChanged();
      }

      @Override
      public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    };
    boardChildListeners.put(boardName, boardEventListener);
    database.getReferenceFromUrl(
        "https://kanban-f611c.firebaseio.com/boards/" + roomID + "_" + boardName + "/tasks")
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

  private void addTaskToDatabase(Task task) {
    int position = mViewPager.getCurrentItem();

    database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/boards/"
        + roomID + "_" + boardList.get(position) + "/").child("tasks").child(task.getId()).setValue(task.getText());

    DatabaseReference taskRef =  database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/");
    taskRef.child("task_duedate").child(task.getId()).setValue(task.getDueDate());
    taskRef.child("task_priority").child(task.getId()).setValue(task.getPriority());
    taskRef.child("task_takers").child(task.getId()).setValue(task.getTakerString());
    taskRef.child("task_points").child(task.getId()).setValue(task.getPoints());
    taskRef.child("task_creator").child(task.getId()).setValue(task.getCreator());
    taskRef.child("task_room").child(task.getId()).setValue(roomID);
  }

  private Task getTaskFromDatabaseAndAddListeners(String taskID, String text){
    String creator = taskID.substring(0, taskID.length() - 13);
    long creationDate = Long.parseLong(taskID.substring(taskID.length() - 13, taskID.length()));
    final Task existingTask = new Task(creator, creationDate);

    DatabaseReference taskRef =  database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/");

    existingTask.addTakersListener(taskRef, mSectionsPagerAdapter);
    existingTask.addPriorityListener(taskRef, mSectionsPagerAdapter);
    existingTask.addPointsListener(taskRef, mSectionsPagerAdapter);
    existingTask.addDueDateListener(taskRef, mSectionsPagerAdapter);
    return existingTask;

//    Task task = new Task(userID, "djowiadjowiadjioaw", 5, 5, 45324532);
//    return task;
  }

  private AlertDialog createTaskDialog(final Task task) {
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
    dueDateLayout.setGravity(Gravity.CENTER_HORIZONTAL);
    dueDateLayout.setOrientation(LinearLayout.HORIZONTAL);
    dueDateLayout.setLayoutParams(dueDateLayoutParams);

    final HashMap<String,Integer> dateTime = new HashMap<>();
    dateTime.put("year", -1);
    dateTime.put("month", -1);
    dateTime.put("day", -1);
    dateTime.put("hour", -1);
    dateTime.put("min", -1);

    final CheckBox dueDateCheckBox = new CheckBox(this);

    final TimePickerDialog timePickerDialog = new TimePickerDialog(this, new OnTimeSetListener() {
      @Override
      public void onTimeSet(TimePicker timePicker, int i, int i1) {
         dateTime.put("hour", i);
         dateTime.put("minute", i1);
         dueDateCheckBox.setChecked(true);
      }
    }, 23, 59, false);

    final DatePickerDialog datePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {
      @Override
      public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
          dateTime.put("year", i);
          dateTime.put("month", i1);
          dateTime.put("day", i2);
          timePickerDialog.show();
      }
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

    dueDateCheckBox.setOnCheckedChangeListener(
          new CompoundButton.OnCheckedChangeListener() {
             @Override
             public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 if(isChecked){
                     dueDateCheckBox.setChecked(false);
                     datePickerDialog.show();
//                     dueDateCheckBox.setChecked(dateTime.values().contains((long) -1));
                 }
                 else{
                    dateTime.put("year", -1);
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
    priority.setMax(100);
    alertLayout.addView(taskInput);
    alertLayout.addView(dueDateLayout);
    alertLayout.addView(priority);
    builder.setView(alertLayout);

    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
          if(task != null){
            task.setText(taskInput.getText().toString());
            task.setPriority(priority.getProgress());
            if(dueDateCheckBox.isChecked() && !dateTime.values().contains(-1)){
              task.setDueDate(dateTime);
            }
            else if (!dueDateCheckBox.isChecked()){
              task.setDueDate(0);
            }
            addTaskToDatabase(task);
          }
          else{
              Task newTask;
              if(dueDateCheckBox.isChecked() && !dateTime.values().contains(-1)){
                  newTask = new Task(userID, taskInput.getText().toString(), priority.getProgress(), 3, dateTime);
              }
              else{
                  newTask = new Task(userID, taskInput.getText().toString(), priority.getProgress(), 3);
              }
            newTask.addTaker(userID);
            addTaskToDatabase(newTask);
          }

      }
    });
    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
          dialog.cancel();
      }
    });
      if(task != null){
          builder.setTitle("Update Task");
          taskInput.setText(task.getText());
          priority.setProgress(task.getPriority());
          if(task.getDueDate() != 0){
            dueDateCheckBox.setChecked(true);
          }
      }
      else{
          builder.setTitle("Add task");
      }

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
    private static HashMap<String, HashMap<String,Task>> taskMapList;
    private static RoomActivity roomActivity;

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber, ArrayList<String> boards,
        HashMap<String, HashMap<String,Task>> initialTaskMapList, RoomActivity room) {
      PlaceholderFragment fragment = new PlaceholderFragment();
      Bundle args = new Bundle();
      taskMapList = initialTaskMapList;
      args.putInt(ARG_SECTION_NUMBER, sectionNumber);
      args.putStringArrayList("boardList", boards);
      fragment.setArguments(args);
      roomActivity = room;
      return fragment;
    }

    public static void updateTaskMapList(HashMap<String, HashMap<String, Task>> newTaskMapList) {
      taskMapList = newTaskMapList;
    }

    public static CardView addCard(LinearLayout parent, final Task task, Context context) {
      CardView newCard = new CardView(context);
      newCard.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View view) {
              roomActivity.createTaskDialog(task).show();
          }
      });
      TextView textView = new TextView(context);
      textView.setText(task.getText());
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

      for (Task task : taskMapList.get(boardName).values()) {
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
      return PlaceholderFragment.newInstance(position, boardList, boardTaskList, RoomActivity.this);
    }

    @Override
    public int getCount() {
      if (boardList == null) {
        return 0;
      }
      return boardList.size();
    }

    public void updateTasks(HashMap<String, HashMap<String, Task>> newTaskList) {
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
