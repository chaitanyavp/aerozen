package me.chaitanyavp.aerozen;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TaskViewHolder extends RecyclerView.ViewHolder {

//  private TextView name;
  private CheckBox completedBox;
  private TextView points;
  private TextView duedate;
  private View mainView;

  public TaskViewHolder(View itemView) {
    super(itemView);
//    name = itemView.findViewById(R.id.section_label);
    mainView = itemView;
    completedBox = itemView.findViewById(R.id.checkBox);
    points = itemView.findViewById(R.id.points);
    duedate = itemView.findViewById(R.id.duedate);

  }

  public void setTask(Task task, String currentUser){
    completedBox.setText(task.getText());
    points.setText(task.getPoints() + " points");
    long epochDate = task.getDueDate();
    if(epochDate != 0) {
        Date date = new Date(epochDate);
        DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
        duedate.setText(dateFormat.format(date));
    }
    if(task.hasTaker(currentUser)){
        itemView.setBackgroundColor(Color.BLUE);
    }
  }
}
