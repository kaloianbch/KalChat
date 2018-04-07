package xyz.chokanov.kalchat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by User on 1/1/2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mMessages = new ArrayList<>();
    private ArrayList<String> mUserNames = new ArrayList<>();
    private ArrayList<String> mTimeStamps= new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(Context context, ArrayList<String> messages, ArrayList<String> userNames, ArrayList<String> timeStamps ) {
        mMessages = messages;
        mUserNames = userNames;
        mTimeStamps = timeStamps;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        holder.mMessage.setText(mMessages.get(position));
        holder.mUserName.setText(mUserNames.get(position));
        holder.mTimeStamp.setText(mTimeStamps.get(position));
        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mUserName;
        TextView mMessage;
        TextView mTimeStamp;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mUserName = itemView.findViewById(R.id.txtUser);
            mMessage = itemView.findViewById(R.id.txtMsg);
            mTimeStamp = itemView.findViewById(R.id.txtTime);
            parentLayout = itemView.findViewById(R.id.layoutItem);
        }
    }
}




