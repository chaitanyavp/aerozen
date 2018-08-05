package me.chaitanyavp.aerozen;

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

  public Task(String creator, String text, int priority, int points) {
    this.creationDate = System.currentTimeMillis();
    this.creator = creator;
    this.id = creator + this.creationDate;
    this.priority = priority;
    this.takers = new ArrayList<String>();
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
}
