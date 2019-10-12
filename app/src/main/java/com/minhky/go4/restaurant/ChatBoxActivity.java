package com.minhky.go4.restaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.minhky.go4.R;
import com.minhky.go4.model.Material;
import com.minhky.go4.model.chatbox;

import java.util.List;

import static com.minhky.go4.utils.Constant.refMaterial;

public class ChatBoxActivity extends AppCompatActivity {

    public static DatabaseReference refChat = FirebaseDatabase.getInstance().getReference().child("chat");

    private FirebaseRecyclerAdapter<chatbox, ChatBoxActivity.ChatBoxViewHolder> adapterFirebaseChatBox;

    private static final int CB_LEFT = 0;
    private static final int CB_RIGHT = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
    }

    @Override
    protected void onResume() {
        super.onResume();

        RecyclerView mtrList = findViewById(R.id.recycler_chatbox);

        //DividerItemDecoration itemDecorator = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        //itemDecorator.setDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.news_divider));
        //mtrList.addItemDecoration(itemDecorator);



        mtrList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mtrList.setLayoutManager(linearLayoutManager);
/*
        mtrList.addItemDecoration(
                new DividerItemDecoration(getApplicationContext(), linearLayoutManager.getOrientation()) {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        int position = parent.getChildAdapterPosition(view);
                        // hide the divider for the last child
                        if (position == parent.getAdapter().getItemCount() - 1) {
                            outRect.setEmpty();
                        } else {
                            super.getItemOffsets(outRect, view, parent, state);
                        }
                    }
                }
        );
*/

        adapterFirebaseChatBox = new FirebaseRecyclerAdapter<chatbox, ChatBoxActivity.ChatBoxViewHolder>(
                chatbox.class,
                R.layout.item_chatbox,
                ChatBoxActivity.ChatBoxViewHolder.class,
                refChat.child("Ky-ATuyen")
        ) {

            @Override
            public int getItemViewType(int position) {
                chatbox chatBox = getItem(position);
                String idChat = chatBox.getIdChat();


                if(idChat.equals("id1")){
                    Toast.makeText(getApplicationContext(), idChat+"", Toast.LENGTH_LONG).show();

                    return CB_LEFT;
                }else{
                    return CB_RIGHT;
                }
            }
            @Override
            public void onBindViewHolder(@NonNull ChatBoxActivity.ChatBoxViewHolder holder, int position, @NonNull List<Object> payloads) {

                super.onBindViewHolder(holder, position, payloads);
            }
            @Override
            public ChatBoxActivity.ChatBoxViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                if(viewType==CB_LEFT){
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_me,parent,false);

                    return new ChatBoxViewHolder(v);

                }else{
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_other,parent,false);
                    return new ChatBoxViewHolder(v);
                }
            }

            @SuppressLint("ResourceAsColor")
            @Override
            protected void populateViewHolder(final ChatBoxActivity.ChatBoxViewHolder viewHolder, final chatbox model, int position) {
                viewHolder.tvContent.setText(model.getContentChat());

            }
        };
        mtrList.setAdapter(adapterFirebaseChatBox);
        adapterFirebaseChatBox.notifyDataSetChanged();
    }

    private class ChatBoxViewHolder extends RecyclerView.ViewHolder {
        TextView tvContent;

        public ChatBoxViewHolder(View itemView) {
            super(itemView);

            tvContent = itemView.findViewById(R.id.tv_content);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    Toast.makeText(getApplicationContext(), position+"", Toast.LENGTH_LONG).show();

                }
            });
        }
    }
}
