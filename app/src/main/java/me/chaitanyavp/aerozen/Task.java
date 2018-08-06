package me.chaitanyavp.aerozen;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.ValueEventListener;
import java.lang.reflect.Array;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

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

  public Task(String creator, long creationDate, String text, int priority, int points){
    this.creator = creator;
    this.creationDate = creationDate;
    this.id = creator + this.creationDate;
    this.priority = priority;
    this.takers = new ArrayList<String>();
    this.text = text;
    this.points = points;
    this.eventListeners = new HashMap<String, ValueEventListener>();
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

  public void setPoints(int points) {
    this.points = points;
  }

  public void storeDueDateListener(ValueEventListener v){
    eventListeners.put("duedate", v);
  }
  public ValueEventListener getDueDateListener(){
    return eventListeners.get("duedate");
  }

  public void storeTakersListener(ValueEventListener v){
    eventListeners.put("takers", v);
  }
  public ValueEventListener getTakersListener(){
    return eventListeners.get("takers");
  }

  public void storePriorityListener(ValueEventListener v){
    eventListeners.put("priority", v);
  }
  public ValueEventListener getPriorityListener(){
    return eventListeners.get("priority");
  }

  public void storePointListener(ValueEventListener v){
    eventListeners.put("points", v);
  }
  public ValueEventListener getPointListener(){
    return eventListeners.get("points");
  }

}
