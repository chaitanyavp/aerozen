package me.chaitanyavp.aerozen;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    View rootView = inflater.inflate(R.layout.fragment_main, container, false);
    HashMap<String, String> roomMembers = ((RoomActivity) getActivity()).getRoomMembers();
    for (String user : roomMembers.values()){
      addCard((LinearLayout) rootView.findViewById(R.id.member_layout), user, rootView.getContext());
    }
    return rootView;
  }

  public CardView addCard(LinearLayout parent, final String memberName, Context context) {
    CardView newCard = new CardView(context);
    newCard.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        //TODO: member dialog
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

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    Bundle argBundle = getArguments();
//    HashMap<String,String> names = (HashMap<String,String>) argBundle.getSerializable("names");
//    ArrayList<String> list = (ArrayList<String>) argBundle.getSerializable("list");
//    FirebaseDatabase db = (FirebaseDatabase) argBundle.getSerializable("database");

    final RecyclerListAdapter adapter = new RecyclerListAdapter(((RoomActivity)getActivity()).getBoardList(),
        ((RoomActivity)getActivity()).getBoardNames(), (RoomActivity)getActivity());

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
    mIth.attachToRecyclerView(recyclerView);
  }

}
