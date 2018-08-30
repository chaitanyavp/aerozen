package me.chaitanyavp.aerozen;

import android.support.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import me.chaitanyavp.aerozen.RoomActivity.SectionsPagerAdapter;

public class Task {

  private String id;
  private String creator;
  private String text;
  private ArrayList<String> takers;
  private long creationDate;
  private long dueDate;
  private int priority;
  private int points;

  private HashMap<String, ValueEventListener> eventListeners;

  public Task(String creator, long creationDate){
    this.creator = creator;
    this.creationDate = creationDate;
    this.id = creator + this.creationDate;
    this.takers = new ArrayList<String>();
    this.eventListeners = new HashMap<String, ValueEventListener>();
  }

  public Task(String creator, long creationDate, String text, int priority, int points){
    this(creator, creationDate);
    this.priority = priority;
    this.text = text;
    this.points = points;
  }

  public Task(String creator, long creationDate, String text, int priority, int points, long dueDateEpoch){
    this(creator, creationDate, text, priority, points);
    this.dueDate = dueDateEpoch;
  }

  public Task(String creator, String text, int priority, int points) {
    this(creator, System.currentTimeMillis(), text, priority, points);
  }

  public Task(String creator, String text, int priority, int points, HashMap<String, Integer> dueDate) {
    this(creator, text, priority, points);
    setDueDate(dueDate);
  }

  public Task(String creator, String text, int priority, int points, long dueDateEpoch) {
    this(creator, text, priority, points);
    setDueDate(dueDateEpoch);
  }

  public String getId() {
    return id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  public void addTaker(String taker) {
    takers.add(taker);
  }

  public void removeTaker(String taker) {
    takers.remove(taker);
  }

  public void setDueDate(HashMap<String,Integer> dateTime){
    Calendar dueDate = Calendar.getInstance();
    dueDate.set(dateTime.get("year"), dateTime.get("month"), dateTime.get("day"), dateTime.get("hour"), dateTime.get("minute"));
    this.dueDate = dueDate.getTimeInMillis();
  }

  public void setDueDate(long dateTimeEpoch){
    this.dueDate = dateTimeEpoch;
  }

  public long getDueDate() {
    return dueDate;
  }

  public String getCreator() {
    return creator;
  }

  public String getTakerString(){
    StringBuilder takerString = new StringBuilder();
    for (String s : takers)
    {
      takerString.append(s);
      takerString.append(" ");
    }
    return takerString.toString();
  }

  public int getPoints() {
    return points;
  }

  public boolean hasTaker(String uid){
    return takers.contains(uid);
  }

  public void setPoints(int points) {
    this.points = points;
  }

  // Created dueDateListener for Database at <<taskRef>> and notifies <<adapter>>.
  public void addDueDateListener(DatabaseReference taskRef, final SectionsPagerAdapter adapter){
    ValueEventListener listener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        setDueDate((long) dataSnapshot.getValue());
        adapter.notifyDataSetChanged();
      }
      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) { }
    };
    taskRef.child("task_duedate").child(this.id).addValueEventListener(listener);
    eventListeners.put("duedate", listener);
  }

  public void addTakersListener(DatabaseReference taskRef, final SectionsPagerAdapter adapter){
    ValueEventListener listener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        takers = new ArrayList<String>(Arrays.asList(((String) dataSnapshot.getValue()).split("\\s")));
        adapter.notifyDataSetChanged();
      }
      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) { }
    };
    taskRef.child("task_takers").child(this.id).addValueEventListener(listener);
    eventListeners.put("takers", listener);
  }

  public void addPriorityListener(DatabaseReference taskRef, final SectionsPagerAdapter adapter){
    ValueEventListener listener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        setPriority(Integer.parseInt(dataSnapshot.getValue().toString()));
        adapter.updateOrder(getId());
        adapter.notifyDataSetChanged();
      }
      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) { }
    };
    taskRef.child("task_priority").child(this.id).addValueEventListener(listener);
    eventListeners.put("priority", listener);
  }

  public void addPointsListener(DatabaseReference taskRef, final SectionsPagerAdapter adapter){
    ValueEventListener listener = new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        setPoints(Integer.parseInt(dataSnapshot.getValue().toString()));
        adapter.notifyDataSetChanged();
      }
      @Override
      public void onCancelled(@NonNull DatabaseError databaseError) { }
    };
    taskRef.child("task_points").child(this.id).addValueEventListener(listener);
    eventListeners.put("points", listener);
  }

  public void removeAllListeners(DatabaseReference taskRef){
    if(eventListeners.containsKey("points")) {
      taskRef.child("task_points").child(this.id).removeEventListener(eventListeners.get("points"));
      eventListeners.remove("points");
    }
    if(eventListeners.containsKey("priority")) {
      taskRef.child("task_priority").child(this.id).removeEventListener(eventListeners.get("priority"));
      eventListeners.remove("priority");
    }
    if(eventListeners.containsKey("takers")) {
      taskRef.child("task_takers").child(this.id).removeEventListener(eventListeners.get("takers"));
      eventListeners.remove("takers");
    }
    if(eventListeners.containsKey("duedate")) {
      taskRef.child("task_duedate").child(this.id).removeEventListener(eventListeners.get("duedate"));
      eventListeners.remove("duedate");
    }
  }

}
