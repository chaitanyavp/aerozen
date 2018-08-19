package me.chaitanyavp.aerozen;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Paul Burke (ipaulpro)
 */
public class RecyclerListFragment extends Fragment {

  private ItemTouchHelper mItemTouchHelper;

  public RecyclerListFragment() {
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_main, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    Bundle argBundle = getArguments();
//    HashMap<String,String> names = (HashMap<String,String>) argBundle.getSerializable("names");
//    ArrayList<String> list = (ArrayList<String>) argBundle.getSerializable("list");
//    FirebaseDatabase db = (FirebaseDatabase) argBundle.getSerializable("database");

    final RecyclerListAdapter adapter = new RecyclerListAdapter(((RoomActivity)getActivity()).getBoardList(),
        ((RoomActivity)getActivity()).getBoardNames());

    RecyclerView recyclerView = view.findViewById(R.id.boardlist_rec);
    recyclerView.setHasFixedSize(true);
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    ItemTouchHelper mIth = new ItemTouchHelper(
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
          public boolean isLongPressDragEnabled() {
            return true;
          }

          @Override
          public boolean isItemViewSwipeEnabled() {
            return false;
          }
        });
    mIth.attachToRecyclerView(recyclerView);
  }

}
