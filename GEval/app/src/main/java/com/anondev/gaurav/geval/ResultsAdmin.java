package com.anondev.gaurav.geval;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.anondev.gaurav.geval.data.Result;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ResultsAdmin extends AppCompatActivity {

    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private DatabaseReference myRef;
    private ProgressBar progressBar;
    private ListView listView;
    private ResultsAdmin.TestAdapter testAdapter;
    ArrayList<String> result=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        auth=FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        database=FirebaseDatabase.getInstance();
        myRef=database.getReference();
        listView=findViewById(R.id.test_listview);
        testAdapter=new ResultsAdmin.TestAdapter(ResultsAdmin.this,result);
        listView.setAdapter(testAdapter);
        getResults();
    }

    public void getResults(){

        myRef.child("Results").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                result.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    result.add(snapshot.getKey());
                }
                testAdapter.dataList=result;
                testAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                Log.e("The read success: " ,"su"+result.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });
    }
    class TestAdapter extends ArrayAdapter<String> {
        private Context mContext;
        ArrayList<String> dataList;
        public TestAdapter( Context context,ArrayList<String> list) {
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
            ((ImageView)listItem.findViewById(R.id.item_imageView)).setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_assessment_black_24dp));
            ((TextView)listItem.findViewById(R.id.item_textView)).setText(dataList.get(position));
            ((Button)listItem.findViewById(R.id.item_button)).setText("View");
            ((Button)listItem.findViewById(R.id.item_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(ResultsAdmin.this,ResultsAdminDetailed.class);
                    intent.putExtra("test",dataList.get(position));
                    startActivity(intent);
                }
            });
            return listItem;
        }
    }

}
