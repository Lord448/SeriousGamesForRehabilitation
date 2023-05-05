package com.example.juego_crit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Menu extends AppCompatActivity {
    TextView Points, Uid, Email, Name, Menu, Regis_Date;
    Button Play, AboutApp, Logout;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference Players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        Players = firebaseDatabase.getReference("MI DATA BASE");

        Points = (TextView)findViewById(R.id.time);
        Uid = (TextView)findViewById(R.id.uid);
        Email = (TextView)findViewById(R.id.email);
        Name = (TextView)findViewById(R.id.name);
        Menu = (TextView)findViewById(R.id.txtMenu);
        Regis_Date = (TextView)findViewById(R.id.registration_date);

        Play = (Button) findViewById(R.id.btnPlay);
        AboutApp = (Button) findViewById(R.id.btnAboutApp);
        Logout = (Button) findViewById(R.id.btnLogout);

        Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Menu.this, Game.class);

                String strUid = Uid.getText().toString();
                String strName = Name.getText().toString();
                String strPoints = Points.getText().toString();

                intent.putExtra("UID", strUid);
                intent.putExtra("NAME", strName);
                intent.putExtra("POINTS", strPoints);

                startActivity(intent);
                Toast.makeText(Menu.this, "enviando datos...", Toast.LENGTH_SHORT).show();
            }
        });

        AboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log_Out();
            }
        });
    }
    protected void onStart(){
        LoggedUser();
        super.onStart();
    }
    private void LoggedUser(){
        if(user != null){
            InfoPlayer();
        }else{
            startActivity(new Intent(Menu.this, MainActivity.class));
            finish();
        }
    }
    //METODO PARA INFORMACION DE JUGADORES
    private void InfoPlayer(){
        //CONSULTA - compara correo electronico con todos los correos que se encuentren en base de datos
        Query query = Players.orderByChild("Email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    //OBTENCION DE LOS DATOS
                    String strPoints = "" + ds.child("Puntuacion").getValue();
                    String strUid = "" + ds.child("Uid").getValue();
                    String strEmail = "" + ds.child("Email").getValue();
                    String strName = "" + ds.child("Nombre").getValue();
                    String strDate = "" + ds.child("Fecha").getValue();
                    //ESCRITURA DE DATOS DEL JUGADOR
                    Points.setText(strPoints);
                    Uid.setText(strUid);
                    Email.setText(strEmail);
                    Name.setText(strName);
                    Regis_Date.setText(strDate);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Menu.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //MÉTODO PARA CERRAR SESIÓN
    private void Log_Out(){
        auth.signOut();
        startActivity(new Intent(Menu.this, MainActivity.class));
        Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show();
    }
}