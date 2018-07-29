package me.chaitanyavp.aerozen;

import java.util.ArrayList;

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

  public Task(String creator, String text, int priority, int points, long dueDate) {
    this(creator, text, priority, points);
    this.dueDate = dueDate;
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
}
