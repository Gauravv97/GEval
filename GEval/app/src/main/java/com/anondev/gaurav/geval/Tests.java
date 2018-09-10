package com.anondev.gaurav.geval;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anondev.gaurav.geval.data.Question;
import com.anondev.gaurav.geval.data.Test;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Tests extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ProgressBar progressBar;
    private ListView listView;
    private TestAdapter testAdapter;
    ArrayList<Test> tests=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        database=FirebaseDatabase.getInstance();
        myRef=database.getReference();
        listView=findViewById(R.id.test_listview);
        testAdapter=new TestAdapter(Tests.this,tests);
        listView.setAdapter(testAdapter);
        getQues();

    }

    public void getQues(){

        myRef.child("tests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tests.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Test t=new Test();
                    t.setName(snapshot.getKey());
                    t.setTime(Long.parseLong(snapshot.child("Time").getValue().toString()));
                    ArrayList<Question> ques=new ArrayList<>();
                    for (DataSnapshot qSnap:snapshot.child("Questions").getChildren()){
                        ques.add(qSnap.getValue(Question.class));
                    }
                    t.setQuestions(ques);
                    tests.add(t);

                }
                testAdapter.dataList=tests;
                testAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                Log.e("The read success: " ,"su"+tests.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });
    }
    class TestAdapter extends ArrayAdapter<Test>{
        private Context mContext;
        ArrayList<Test> dataList;
        public TestAdapter( Context context,ArrayList<Test> list) {
            super(context, 0 , list);
            mContext = context;
            dataList = list;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if(listItem == null)
                listItem = LayoutInflater.from(mContext).inflate(R.layout.test_item,parent,false);
            ((ImageView)listItem.findViewById(R.id.item_imageView)).setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_school_black_24dp));
            ((TextView)listItem.findViewById(R.id.item_textView)).setText(dataList.get(position).getName()+" : "+dataList.get(position).getTime()+"Min");
            ((Button)listItem.findViewById(R.id.item_button)).setText("Attempt");
            ((Button)listItem.findViewById(R.id.item_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(mContext,AttemptTest.class);
                    intent.putExtra("Questions",dataList.get(position));
                    startActivity(intent);
                }
            });

            return listItem;
        }
    }


}
