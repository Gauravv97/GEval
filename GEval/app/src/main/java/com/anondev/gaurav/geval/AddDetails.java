package com.anondev.gaurav.geval;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.anondev.gaurav.geval.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class AddDetails extends AppCompatActivity {


    private Spinner univ,sem,branch;
    private  ArrayAdapter<String> UnivAdapter,BranchAdapter,SemAdapter;
    private Button update;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private EditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_details);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();

        update=findViewById(R.id.update_button);

        name=findViewById(R.id.Name_details);

        univ=findViewById(R.id.Univ_spinner);
        sem=findViewById(R.id.Sem_spinner);
        branch=findViewById(R.id.Branch_spinner);

        UnivAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,this.getResources().getStringArray(R.array.College_Choice));
        UnivAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        BranchAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,this.getResources().getStringArray(R.array.Branch_Choice));
        BranchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SemAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,this.getResources().getStringArray(R.array.Sem_Choice));
        SemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        univ.setAdapter(UnivAdapter);
        branch.setAdapter(BranchAdapter);
        sem.setAdapter(SemAdapter);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user=new User(name.getText().toString(),sem.getSelectedItem().toString(),branch.getSelectedItem().toString(),univ.getSelectedItem().toString());
                if(name!=null&&!name.equals("")) {
                    mDatabase.child("users").child(auth.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AddDetails.this,"Updated",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }else Toast.makeText(AddDetails.this,"Enter Valid Name",Toast.LENGTH_SHORT).show();
            }
        });
        mDatabase.child("users").child(auth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(name.getText().toString().equals("")&&dataSnapshot.exists()){
                    User user=dataSnapshot.getValue(User.class);
                    name.setText(user.name);
                    univ.setSelection(Arrays.asList(AddDetails.this.getResources().getStringArray(R.array.College_Choice)).indexOf(user.college));
                    branch.setSelection(Arrays.asList(AddDetails.this.getResources().getStringArray(R.array.Branch_Choice)).indexOf(user.branch));
                    sem.setSelection(Arrays.asList(AddDetails.this.getResources().getStringArray(R.array.Sem_Choice)).indexOf(user.semester));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
