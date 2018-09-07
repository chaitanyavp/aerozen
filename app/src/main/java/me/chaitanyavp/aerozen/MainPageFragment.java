package me.chaitanyavp.aerozen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @author Paul Burke (ipaulpro)
 */
public class MainPageFragment extends Fragment {

  private ItemTouchHelper mItemTouchHelper;

  public MainPageFragment() {
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);
    HashMap<String, String> roomMembers = ((RoomActivity) getActivity()).getRoomMembers();
    for (String memberID : roomMembers.keySet()){
      addCard((LinearLayout) rootView.findViewById(R.id.member_layout), memberID,
          roomMembers.get(memberID), rootView.getContext());
    }
    Button viewAllCompletedTasks = rootView.findViewById(R.id.all_completed_tasks);
    viewAllCompletedTasks.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        allCompletedTasksDialog("").show();
      }
    });

    Button viewArchivedBoards = rootView.findViewById(R.id.all_archived_boards);
    viewArchivedBoards.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        allArchivedBoardsDialog().show();
      }
    });
    return rootView;
  }

  private CardView addCard(LinearLayout parent, final String memberID, final String memberName,
      final Context context) {
    CardView newCard = new CardView(context);
    newCard.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        createMemberDialog(memberID, memberName, context).show();
      }
    });
    TextView textView = new TextView(context);
    textView.setText(memberName);
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

  private View createViewForCompletedTask(final String completedTaskID, final String completedTaskText,
      final String board, final RoomActivity roomActivity){
    Context context = roomActivity.getApplicationContext();
    CardView newCard = new CardView(context);
//    TextView textView = new TextView(context);
//    textView.setText(completedTask.getText());

    CheckBox completedBox = new CheckBox(context);
    completedBox.setText(completedTaskText);
    completedBox.setChecked(true);
    completedBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(!b) {
          roomActivity.unCompleteTask(completedTaskID, completedTaskText, board);
        }
      }
    });

    newCard.addView(completedBox);
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
    completedBox.setLayoutParams(cardParams);
    return newCard;
  }

  private View createViewForArchivedBoard(final String boardID, final String boardName,
      final RoomActivity roomActivity){
    Context context = roomActivity.getApplicationContext();
    CardView newCard = new CardView(context);

    CheckBox completedBox = new CheckBox(context);
    completedBox.setText(boardName);
    completedBox.setChecked(true);
    completedBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(!b) {
          roomActivity.unCompleteBoard(boardID);
        }
      }
    });
    newCard.addView(completedBox);
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
    completedBox.setLayoutParams(cardParams);
    return newCard;
  }

  private AlertDialog createMemberDialog(final String memberID, final String memberName,
      final Context context) {
    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
    final ScrollView scrollView = new ScrollView(context);
    final LinearLayout alertLayout = new LinearLayout(context);
    LinearLayout.LayoutParams alertLayoutParams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    final TextView pointsView = new TextView(context);
    pointsView.setText("0 points");
    builder.setTitle(memberName);
    alertLayout.addView(pointsView);
    int TEN_DP = (int) TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        10,
        getResources().getDisplayMetrics()
    );
    final RoomActivity roomActivity = ((RoomActivity) getActivity());

    final HashMap<Integer, Integer> totalPoints = new HashMap<>();
    totalPoints.put(0, 0);

    alertLayoutParams.setMargins(TEN_DP, TEN_DP, TEN_DP, 0);
    alertLayout.setGravity(Gravity.CENTER_HORIZONTAL);
    alertLayout.setOrientation(LinearLayout.VERTICAL);
    alertLayout.setLayoutParams(alertLayoutParams);
    alertLayout.setPadding(TEN_DP, TEN_DP, TEN_DP, TEN_DP);

    final HashMap<String, View> completedTaskViews = new HashMap<>();
    final HashMap<String, Integer> completedTaskPoints = new HashMap<>();

    ArrayList<String> boardsToGoThrough = new ArrayList<String>();
    boardsToGoThrough.addAll(roomActivity.getBoardList());
    boardsToGoThrough.addAll(roomActivity.getArchivedBoards().keySet());

    for(final String boardID : boardsToGoThrough) {
      final DatabaseReference dataRef =
          roomActivity.getRefFromUrl("https://kanban-f611c.firebaseio.com/boards/" + boardID + "/completed_tasks");
      dataRef.addChildEventListener(new ChildEventListener(){
        @Override
        public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
          final String taskID = dataSnapshot.getKey();
          final String taskText = dataSnapshot.getValue().toString();
          roomActivity.getRefFromUrl("https://kanban-f611c.firebaseio.com/task_takers/" + taskID)
              .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot takerSnapshot) {
              for(String taker : takerSnapshot.getValue().toString().split(" ")){
                if(taker.equals(memberID)){
                  roomActivity.getRefFromUrl("https://kanban-f611c.firebaseio.com/task_points/" + taskID)
                      .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot pointSnapshot) {
                      int taskPoints = 0;
                      if(pointSnapshot.getValue() != null) {
                        taskPoints = Integer.parseInt(pointSnapshot.getValue().toString());
                      }
                      View taskView = createViewForCompletedTask(taskID, taskText, boardID, roomActivity);
                      alertLayout.addView(taskView);
                      completedTaskPoints.put(taskID,taskPoints);
                      completedTaskViews.put(taskID,taskView);
                      totalPoints.put(0,totalPoints.get(0)+taskPoints);
                      pointsView.setText(totalPoints.get(0)+" points");
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                  });
                  break;
                }
              }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
          });
        }
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
          if(completedTaskViews.containsKey(dataSnapshot.getKey())) {
            int taskPoints = completedTaskPoints.remove(dataSnapshot.getKey());
            View taskView = completedTaskViews.remove(dataSnapshot.getKey());
            alertLayout.removeView(taskView);
            totalPoints.put(0, totalPoints.get(0) - taskPoints);
            pointsView.setText(totalPoints.get(0)+" points");
          }
        }
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
      });
    }
    scrollView.addView(alertLayout);
    builder.setView(scrollView);

    builder.setNeutralButton("Remove Member", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        if(!roomActivity.isCurrentUserOwner()) {
          if(roomActivity.getUserID().equals(memberID)){
            leaveRoomDialog(roomActivity, memberID).show();
          }
          else {
            roomActivity.showSnackBar("You do not have permission.");
          }
        }
        else if(memberID.equals(roomActivity.getRoomID())){
          roomActivity.showSnackBar("You can't remove owner.");
        }
        else{
          roomActivity.removeMember(memberID);
        }
      }
    });
    builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    return builder.create();
  }

  private AlertDialog allCompletedTasksDialog(final String boardToPut){
    Context context = getContext();
    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
    final ScrollView scrollView = new ScrollView(context);
    final LinearLayout alertLayout = new LinearLayout(context);
    LinearLayout.LayoutParams alertLayoutParams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    final TextView pointsView = new TextView(context);
    pointsView.setText("0 points");
    builder.setTitle("Completed Tasks");
    alertLayout.addView(pointsView);
    int TEN_DP = (int) TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        10,
        getResources().getDisplayMetrics()
    );
    final RoomActivity roomActivity = ((RoomActivity) getActivity());

    final HashMap<Integer, Integer> totalPoints = new HashMap<>();
    totalPoints.put(0, 0);

    alertLayoutParams.setMargins(TEN_DP, TEN_DP, TEN_DP, 0);
    alertLayout.setGravity(Gravity.CENTER_HORIZONTAL);
    alertLayout.setOrientation(LinearLayout.VERTICAL);
    alertLayout.setLayoutParams(alertLayoutParams);
    alertLayout.setPadding(TEN_DP, TEN_DP, TEN_DP, TEN_DP);

    final HashMap<String, View> completedTaskViews = new HashMap<>();
    final HashMap<String, Integer> completedTaskPoints = new HashMap<>();

    ArrayList<String> boardsToGoThrough = new ArrayList<String>();
    if(boardToPut.equals("")) {
      boardsToGoThrough.addAll(roomActivity.getBoardList());
      boardsToGoThrough.addAll(roomActivity.getArchivedBoards().keySet());
    }
    else{
      boardsToGoThrough.add(boardToPut);
    }

    for(final String boardID : boardsToGoThrough) {
      final DatabaseReference dataRef =
          roomActivity.getRefFromUrl("https://kanban-f611c.firebaseio.com/boards/" + boardID + "/completed_tasks");
      dataRef.addChildEventListener(new ChildEventListener(){
        @Override
        public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
          final String taskID = dataSnapshot.getKey();
          final String taskText = dataSnapshot.getValue().toString();
          roomActivity.getRefFromUrl("https://kanban-f611c.firebaseio.com/task_points/" + taskID)
            .addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot pointSnapshot) {
                int taskPoints = 0;
                if(pointSnapshot.getValue() != null) {
                  taskPoints = Integer.parseInt(pointSnapshot.getValue().toString());
                }
                View taskView = createViewForCompletedTask(taskID, taskText, boardID, roomActivity);
                alertLayout.addView(taskView);
                completedTaskPoints.put(taskID,taskPoints);
                completedTaskViews.put(taskID,taskView);
                totalPoints.put(0,totalPoints.get(0)+taskPoints);
                pointsView.setText(totalPoints.get(0)+" points");
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
            });
        }
        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        }
        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
          if(completedTaskViews.containsKey(dataSnapshot.getKey())) {
            int taskPoints = completedTaskPoints.remove(dataSnapshot.getKey());
            View taskView = completedTaskViews.remove(dataSnapshot.getKey());
            alertLayout.removeView(taskView);
            totalPoints.put(0, totalPoints.get(0) - taskPoints);
            pointsView.setText(totalPoints.get(0)+" points");
          }
        }
        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
      });
    }
    scrollView.addView(alertLayout);
    builder.setView(scrollView);

    builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    return builder.create();
  }

  private AlertDialog allArchivedBoardsDialog(){
    Context context = getContext();
    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
    final ScrollView scrollView = new ScrollView(context);
    final LinearLayout alertLayout = new LinearLayout(context);
    LinearLayout.LayoutParams alertLayoutParams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    builder.setTitle("Archived Boards");
    int TEN_DP = (int) TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        10,
        getResources().getDisplayMetrics()
    );
    final RoomActivity roomActivity = ((RoomActivity) getActivity());

    alertLayoutParams.setMargins(TEN_DP, TEN_DP, TEN_DP, 0);
    alertLayout.setGravity(Gravity.CENTER_HORIZONTAL);
    alertLayout.setOrientation(LinearLayout.VERTICAL);
    alertLayout.setLayoutParams(alertLayoutParams);
    alertLayout.setPadding(TEN_DP, TEN_DP, TEN_DP, TEN_DP);

    final HashMap<String, View> archivedBoardViews = new HashMap<>();

    final DatabaseReference dataRef =
        roomActivity.getRefFromUrl("https://kanban-f611c.firebaseio.com/rooms/" + roomActivity.getRoomID()
            + "/completed_boards");
    dataRef.addChildEventListener(new ChildEventListener(){
      @Override
      public void onChildAdded(@NonNull final DataSnapshot dataSnapshot, @Nullable String s) {
        final String boardID = dataSnapshot.getKey();
        final String boardName = dataSnapshot.getValue().toString();
        View boardView = createViewForArchivedBoard(boardID, boardName, roomActivity);
        alertLayout.addView(boardView);
        archivedBoardViews.put(boardID, boardView);
      }
      @Override
      public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
      }
      @Override
      public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        if(archivedBoardViews.containsKey(dataSnapshot.getKey())) {
          View boardView = archivedBoardViews.remove(dataSnapshot.getKey());
          alertLayout.removeView(boardView);
        }
      }
      @Override
      public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

      }
      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) {

      }
    });
    scrollView.addView(alertLayout);
    builder.setView(scrollView);

    builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    return builder.create();
  }

  private AlertDialog leaveRoomDialog(final RoomActivity roomActivity, final String memberID){
    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    builder.setTitle("Are you sure you want to leave the room?");
    builder.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        roomActivity.leaveRoom();
      }
    });
    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    return builder.create();
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    Bundle argBundle = getArguments();
//    HashMap<String,String> names = (HashMap<String,String>) argBundle.getSerializable("names");
//    ArrayList<String> list = (ArrayList<String>) argBundle.getSerializable("list");
//    FirebaseDatabase db = (FirebaseDatabase) argBundle.getSerializable("database");

    final RecyclerListAdapter adapter = new RecyclerListAdapter(((RoomActivity)getActivity()).getBoardList(),
        (RoomActivity)getActivity());

    RecyclerView recyclerView = view.findViewById(R.id.boardlist_rec);
    recyclerView.setHasFixedSize(true);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    mItemTouchHelper = new ItemTouchHelper(
        new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT) {

          public boolean onMove(RecyclerView recyclerView,
              ViewHolder viewHolder, ViewHolder target) {
            final int fromPos = viewHolder.getAdapterPosition();
            final int toPos = target.getAdapterPosition();
            // move item in `fromPos` to `toPos` in adapter.
            adapter.onItemMove(fromPos, toPos);
            return true;// true if moved, false otherwise
          }

          public void onSwiped(ViewHolder viewHolder, int direction) {
            // remove from adapter
            adapter.onItemDismiss(viewHolder.getAdapterPosition());
          }

          @Override
          public int getMovementFlags(RecyclerView recyclerView,
              RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
          }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            adapter.onDropped();
        }

          @Override
          public boolean isLongPressDragEnabled() {
            return true;
          }

          @Override
          public boolean isItemViewSwipeEnabled() {
            return false;
          }
        });
    mItemTouchHelper.attachToRecyclerView(recyclerView);
  }

}
