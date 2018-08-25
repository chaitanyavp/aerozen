package me.chaitanyavp.aerozen;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Resources;
import android.renderscript.Sampler.Value;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import java.util.Collections;
import java.util.Comparator;
//import java.util.TreeMap;

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
  private HashMap<String, ValueEventListener> boardOrderListeners;
  private HashMap<String, ArrayList<String>> boardTaskList;
  private HashMap<String, Task> allTasks;
  private HashMap<String, String> boardNames;
  private HashMap<String, Integer> boardOrder;
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
  private Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_room);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
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


    FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
    fab2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        addBoardDialog().show();
      }
    });

    FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fab1);
    fab1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        addRoomMemberDialog().show();
      }
    });

    FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fab3);
    fab3.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        int position = mViewPager.getCurrentItem() - 1;
        if(position >= 0 && position < boardList.size()) {
          createTaskDialog(null, boardList.get(position)).show();
        }
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
    boardOrderListeners = new HashMap<String, ValueEventListener>();

    boardTaskList = new HashMap<String, ArrayList<String>>();
    allTasks = new HashMap<String, Task>();

    boardNames = new HashMap<String, String>();
    boardOrder = new HashMap<String, Integer>();

    database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/rooms/" + roomID)
        .addChildEventListener(new ChildEventListener() {
          @Override
          public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            addBoard(dataSnapshot.getKey(), s);
            boardNames.put(dataSnapshot.getKey(), (String) dataSnapshot.getValue());
          }

          @Override
          public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            updateBoard(dataSnapshot.getKey());
            boardNames.put(dataSnapshot.getKey(), (String) dataSnapshot.getValue());
          }

          @Override
          public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            removeBoard(dataSnapshot.getKey());
            boardNames.remove(dataSnapshot.getKey());
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

  private void addBoard(final String boardName, String prev) {
    if (prev == null) {
      boardList.add(0, boardName);
    } else {
      boardList.add(boardList.indexOf(prev) + 1, boardName);
    }
    boardTaskList.put(boardName, new ArrayList<String>());
    ChildEventListener boardEventListener = new ChildEventListener() {
      @Override
      public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Task task = getTaskFromDatabaseAndAddListeners(dataSnapshot.getKey(),
            (String) dataSnapshot.getValue());
        task.setText((String) dataSnapshot.getValue());

        allTasks.put(dataSnapshot.getKey(), task);
        boardTaskList.get(boardName).add(dataSnapshot.getKey());

        Log.w("BAD", "task child added");
        mSectionsPagerAdapter.updateTasks(boardTaskList);
      }

      @Override
      public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        allTasks.get(dataSnapshot.getKey()).setText((String) dataSnapshot.getValue());
        mSectionsPagerAdapter.notifyDataSetChanged();
      }

      @Override
      public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        //TODO: Handle child removals (clear event listeners)
        ArrayList<String> tasks = boardTaskList.get(boardName);
        tasks.remove(dataSnapshot.getKey());
        Task removed = allTasks.remove(dataSnapshot.getKey());
        removed.removeAllListeners(
            database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/"));
        mSectionsPagerAdapter.notifyDataSetChanged();
      }

      @Override
      public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    };

    ValueEventListener orderListener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        boardOrder.put(boardName, (int) (long) dataSnapshot.getValue());
        Collections.sort(boardList, new Comparator<String>(){
          @Override
          public int compare(final String s1, final String s2) {
            int p1 = boardOrder.get(s1);
            int p2 = boardOrder.get(s2);
            if(p1 < p2){
              return -1;
            }
            else if(p1==p2){
              return 0;
            }
            else{
              return 1;
            }
          }
        });
        mSectionsPagerAdapter.notifyDataSetChanged();
      }

      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    };

    boardOrderListeners.put(boardName, orderListener);
    boardOrder.put(boardName, 5);
    boardChildListeners.put(boardName, boardEventListener);
    database.getReferenceFromUrl(
        "https://kanban-f611c.firebaseio.com/boards/" + boardName + "/tasks")
        .addChildEventListener(boardEventListener);
    database.getReferenceFromUrl(
        "https://kanban-f611c.firebaseio.com/boards/" + boardName + "/order")
        .addValueEventListener(orderListener);
    mSectionsPagerAdapter.updateTasks(boardTaskList);
    mSectionsPagerAdapter.notifyDataSetChanged();
  }

  private void removeBoard(String key) {
    boardList.remove(key);
    boardNames.remove(key);
    boardOrder.remove(key);
    ChildEventListener childEventListener = boardChildListeners.remove(key);
    database.getReferenceFromUrl(
        "https://kanban-f611c.firebaseio.com/boards/" + key + "/tasks")
        .removeEventListener(childEventListener);

    ValueEventListener orderListener = boardOrderListeners.remove(key);
    database.getReferenceFromUrl(
        "https://kanban-f611c.firebaseio.com/boards/" + key + "/order")
        .removeEventListener(orderListener);

    ArrayList<String> removedTasks = boardTaskList.remove(key);
    for(String taskName : removedTasks){
      allTasks.remove(taskName);
    }

    t.setText(t.getText() + "Removed" + key);
    mSectionsPagerAdapter.updateTasks(boardTaskList);
    mSectionsPagerAdapter.notifyDataSetChanged();
  }

  public void setBoardPosition(String boardID, int newPos){
    database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/boards/"
            + boardID + "/order").setValue(newPos);
  }

  private void moveBoard(String key, String prev) {
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

  public Task getExistingTask(String name){
    if(allTasks.containsKey(name)){
      return allTasks.get(name);
    }
    else{
      return null;
    }
  }

  private void addTaskToDatabase(Task task) {
    int position = mViewPager.getCurrentItem() - 1;

    database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/boards/"
        + boardList.get(position) + "/").child("tasks").child(task.getId())
        .setValue(task.getText());

    DatabaseReference taskRef = database
        .getReferenceFromUrl("https://kanban-f611c.firebaseio.com/");
    taskRef.child("task_duedate").child(task.getId()).setValue(task.getDueDate());
    taskRef.child("task_priority").child(task.getId()).setValue(task.getPriority());
    taskRef.child("task_takers").child(task.getId()).setValue(task.getTakerString());
    taskRef.child("task_points").child(task.getId()).setValue(task.getPoints());
    taskRef.child("task_creator").child(task.getId()).setValue(task.getCreator());
    taskRef.child("task_room").child(task.getId()).setValue(roomID);
  }

  private Task getTaskFromDatabaseAndAddListeners(String taskID, String text) {
    String creator = taskID.substring(0, taskID.length() - 13);
    long creationDate = Long.parseLong(taskID.substring(taskID.length() - 13, taskID.length()));
    final Task existingTask = new Task(creator, creationDate);

    DatabaseReference taskRef = database
        .getReferenceFromUrl("https://kanban-f611c.firebaseio.com/");

    existingTask.addTakersListener(taskRef, mSectionsPagerAdapter);
    existingTask.addPriorityListener(taskRef, mSectionsPagerAdapter);
    existingTask.addPointsListener(taskRef, mSectionsPagerAdapter);
    existingTask.addDueDateListener(taskRef, mSectionsPagerAdapter);
    return existingTask;
  }

  private AlertDialog addRoomMemberDialog() {
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Add member by email");
    final LinearLayout alertLayout = new LinearLayout(this);
    LinearLayout.LayoutParams alertLayoutParams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    alertLayoutParams.setMargins(TEN_DP, TEN_DP, TEN_DP, 0);
    alertLayout.setGravity(Gravity.CENTER_HORIZONTAL);
    alertLayout.setOrientation(LinearLayout.VERTICAL);
    alertLayout.setLayoutParams(alertLayoutParams);
    alertLayout.setPadding(TEN_DP, TEN_DP, TEN_DP, TEN_DP);

    final EditText memberInput = new EditText(this);
    memberInput.setInputType(InputType.TYPE_CLASS_TEXT);

    alertLayout.addView(memberInput);
    builder.setView(alertLayout);

    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String email = memberInput.getText().toString().replace('.', ',');
        database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/emailToUid/" + email)
            .addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                  Snackbar.make(toolbar, "No user with that email", Snackbar.LENGTH_LONG).show();
                } else {
                  String newUserID = (String) dataSnapshot.getValue();
                  database.getReferenceFromUrl(
                      "https://kanban-f611c.firebaseio.com/room_members/" + roomID + "/"
                          + newUserID).setValue(true);
                  database.getReferenceFromUrl(
                      "https://kanban-f611c.firebaseio.com/user_rooms/" + newUserID + "/" + roomID)
                      .setValue(true);
                  Snackbar.make(toolbar, "User added", Snackbar.LENGTH_LONG).show();
                }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
            });
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

  private AlertDialog addBoardDialog() {
    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Add new board");
    final LinearLayout alertLayout = new LinearLayout(this);
    LinearLayout.LayoutParams alertLayoutParams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    alertLayoutParams.setMargins(TEN_DP, TEN_DP, TEN_DP, 0);
    alertLayout.setGravity(Gravity.CENTER_HORIZONTAL);
    alertLayout.setOrientation(LinearLayout.VERTICAL);
    alertLayout.setLayoutParams(alertLayoutParams);
    alertLayout.setPadding(TEN_DP, TEN_DP, TEN_DP, TEN_DP);

    final EditText boardInput = new EditText(this);
    boardInput.setInputType(InputType.TYPE_CLASS_TEXT);

    alertLayout.addView(boardInput);
    builder.setView(alertLayout);

    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String boardName = boardInput.getText().toString();
        String boardID = roomID + "_" + userID + "_" + System.currentTimeMillis();
        database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/boards/" + boardID + "/order").setValue(boardList.size());
        database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/boards/" + boardID + "/room_id").setValue(roomID);
        database.getReferenceFromUrl("https://kanban-f611c.firebaseio.com/rooms/" + roomID + "/"
            + boardID).setValue(boardName);

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


  private AlertDialog createMessageDialog(String message) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(message)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int id) {
          }
        });
    return builder.create();
  }

  private AlertDialog createTaskDialog(final Task task, final String boardName) {
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

    final HashMap<String, Integer> dateTime = new HashMap<>();
    dateTime.put("year", -1);
    dateTime.put("month", -1);
    dateTime.put("day", -1);
    dateTime.put("hour", -1);
    dateTime.put("minute", -1);

    final CheckBox dueDateCheckBox = new CheckBox(this);

    final TimePickerDialog timePickerDialog = new TimePickerDialog(this, new OnTimeSetListener() {
      @Override
      public void onTimeSet(TimePicker timePicker, int i, int i1) {
        dateTime.put("hour", i);
        dateTime.put("minute", i1);
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
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH));

    OnDismissListener dateDismiss = new OnDismissListener() {
      public void onDismiss(DialogInterface dialog) {
        if (dateTime.get("year") == -1 || dateTime.get("month") == -1
            || dateTime.get("day") == -1) {
          dueDateCheckBox.setChecked(false);
        }
      }
    };
    OnDismissListener timeDismiss = new OnDismissListener() {
      public void onDismiss(DialogInterface dialog) {
        if (dateTime.get("hour") == -1 || dateTime.get("minute") == -1) {
          dueDateCheckBox.setChecked(false);
        }
      }
    };
    datePickerDialog.setOnDismissListener(dateDismiss);
    timePickerDialog.setOnDismissListener(timeDismiss);

    dueDateCheckBox.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
              datePickerDialog.show();
            } else {
              dateTime.put("year", -1);
              dateTime.put("month", -1);
              dateTime.put("day", -1);
              dateTime.put("hour", -1);
              dateTime.put("minute", -1);
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
    final SeekBar pointSlider = new SeekBar(this);
    pointSlider.setMax(100);
    alertLayout.addView(taskInput);
    alertLayout.addView(dueDateLayout);
    alertLayout.addView(pointSlider);
    builder.setView(alertLayout);

    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if (task != null) {
          task.setText(taskInput.getText().toString());
          task.setPriority(pointSlider.getProgress());
          if (dueDateCheckBox.isChecked() && !dateTime.values().contains(-1)) {
            task.setDueDate(dateTime);
          } else if (!dueDateCheckBox.isChecked()) {
            task.setDueDate(0);
          }
          addTaskToDatabase(task);
        } else {
          int order = boardTaskList.get(boardName).size();
          Task newTask;
          if (dueDateCheckBox.isChecked() && !dateTime.values().contains(-1)) {
            newTask = new Task(userID, taskInput.getText().toString(), order, pointSlider.getProgress(),
                dateTime);
          } else {
            newTask = new Task(userID, taskInput.getText().toString(), order, pointSlider.getProgress());
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
    if (task != null) {
      builder.setTitle("Update Task");
      taskInput.setText(task.getText());
      pointSlider.setProgress(task.getPriority());
      if (task.getDueDate() != 0) {
        dueDateCheckBox.setChecked(true);
      }
    }
    return builder.create();
  }

  public ArrayList<String> getBoardList() {
    return boardList;
  }

  public HashMap<String, String> getBoardNames() {
    return boardNames;
  }

  public HashMap<String, ArrayList<String>> getBoardTaskList() {
    return boardTaskList;
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
    private static HashMap<String, String> boardNames;
    private static RoomActivity roomActivity;

    public PlaceholderFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber, ArrayList<String> boards,
        HashMap<String, ArrayList<String>> initialTaskMapList,
        HashMap<String, String> initialBoardNames, RoomActivity room) {
      PlaceholderFragment fragment = new PlaceholderFragment();
      Bundle args = new Bundle();
      taskMapList = initialTaskMapList;
      boardNames = initialBoardNames;
      args.putInt(ARG_SECTION_NUMBER, sectionNumber);
      args.putStringArrayList("boardList", boards);
      fragment.setArguments(args);
      roomActivity = room;
      return fragment;
    }

    public static void updateTaskMapList(HashMap<String, ArrayList<String>> newTaskMapList,
        HashMap<String, String> newBoardNames) {
      taskMapList = newTaskMapList;
      boardNames = newBoardNames;
    }

    public static CardView addCard(LinearLayout parent, final Task task, final String boardName,
        Context context) {
      CardView newCard = new CardView(context);
      newCard.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          roomActivity.createTaskDialog(task, boardName).show();
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
      View rootView;
      int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

      if (sectionNumber == 0) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        Context context = rootView.getContext();
        LinearLayout parentLayout = rootView.findViewById(R.id.task_layout);
//        CardView newCard = new CardView(context);
//        newCard.setOnClickListener(new OnClickListener() {
//          @Override
//          public void onClick(View view) {
//          }
//        });
//          TextView test = new TextView(context);
//          test.setText("manage boards");
//        newCard.addView(test);
//
//        CardView newCard1 = new CardView(context);
//        newCard1.setOnClickListener(new OnClickListener() {
//          @Override
//          public void onClick(View view) {
//          }
//        });
//        TextView test1 = new TextView(context);
//        test1.setText("see user contributions");
//        newCard1.addView(test1);
//
//        CardView newCard2 = new CardView(context);
//        newCard2.setOnClickListener(new OnClickListener() {
//          @Override
//          public void onClick(View view) {
//          }
//        });
//        TextView test2 = new TextView(context);
//        test2.setText("manage members");
//        newCard2.addView(test2);
//
//        CardView newCard3 = new CardView(context);
//        newCard3.setOnClickListener(new OnClickListener() {
//          @Override
//          public void onClick(View view) {
//          }
//        });
//        TextView test3 = new TextView(context);
//        test3.setText("Change rooms");
//          parentLayout.addView(test);
//        newCard3.addView(test3);
      } else {
        rootView = inflater.inflate(R.layout.fragment_room, container, false);
        Context context = rootView.getContext();
        sectionNumber--;
        String boardID = getArguments().getStringArrayList("boardList").get(sectionNumber);
        String boardName = boardNames.get(boardID);

        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(boardName);
        LinearLayout parentLayout = rootView.findViewById(R.id.task_layout);

        for (String taskName : taskMapList.get(boardID)) {
          addCard(parentLayout, ((RoomActivity) getActivity()).getExistingTask(taskName),
              boardName, context);
        }
      }

      return rootView;
    }
    @Override
    public void onViewCreated(View view, Bundle bundle){
      super.onViewCreated(view, bundle);
      int position = getArguments().getInt(ARG_SECTION_NUMBER);
      if (position != 0) {
        String currBoard = ((RoomActivity) getActivity()).getBoardList().get(position-1);
        final TaskRecyclerListAdapter adapter = new TaskRecyclerListAdapter(
                ((RoomActivity) getActivity()).getBoardTaskList().get(currBoard),
                (RoomActivity) getActivity());

        RecyclerView recyclerView = view.findViewById(R.id.tasklist_rec);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
      }
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
      if(position == 0){
        Bundle bundle = new Bundle();
        bundle.putSerializable("names", boardNames);
        bundle.putSerializable("list", boardList);
        RecyclerListFragment frag = new RecyclerListFragment();
        frag.setArguments(bundle);
        return frag;
      }
      return PlaceholderFragment
          .newInstance(position, boardList, boardTaskList, boardNames, RoomActivity.this);
    }

    @Override
    public int getCount() {
      if (boardList == null) {
        return 1;
      }
      return boardList.size() + 1;
    }

    public void updateTasks(HashMap<String, ArrayList<String>> newTaskList) {
      PlaceholderFragment.updateTaskMapList(newTaskList, boardNames);
      this.notifyDataSetChanged();
    }
  }
}
