package com.example.juego_crit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {
    EditText name, mail, password;
    TextView date;
    Button register;

    FirebaseAuth auth;          //AUTENTICACION DE FIREBASE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        name = (EditText) findViewById(R.id.etName);
        mail = (EditText) findViewById(R.id.etMail);
        password = (EditText) findViewById(R.id.etPassword);
        date = (TextView) findViewById(R.id.tvDate);
        register = (Button) findViewById(R.id.btnRegister);

        auth = FirebaseAuth.getInstance();
        Date fecha = new Date();
        SimpleDateFormat fechaformato = new SimpleDateFormat("d 'de' MMM 'del' yyyy");
        String fechastring = fechaformato.format(fecha);
        date.setText(fechastring);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mail.getText().toString();
                String psw = password.getText().toString();

                //Firebase acepta contraseñas mayor o igual a 6 caracteres
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){           //Validar correo
                    mail.setError("Correo no válido");
                    mail.setFocusable(true);
                } else if (psw.length()<6) {
                    password.setError("Contraseña mínima de 6 caracteres");     //Validar contraseña
                    password.setFocusable(true);
                }else{
                    PlayerRegister(email, psw);

                }
            }
        });

    }
    //Metodo para registrar un jugador
    private void PlayerRegister(String email, String psw){
        auth.createUserWithEmailAndPassword(email, psw)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Si el jugador se registró correctamente
                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            int contador = 0;
                            assert user != null;                //El usuario no es nulo
                            //Strings a guardar en firebase
                            String UidString = user.getUid();
                            String correoString = mail.getText().toString();
                            String pswString = password.getText().toString();
                            String nombreString = name.getText().toString();
                            String fechaString = date.getText().toString();

                            HashMap<Object, Object> DatosJugador = new HashMap<>();

                            DatosJugador.put("Uid", UidString);
                            DatosJugador.put("Email", correoString);
                            DatosJugador.put("Contraseña", pswString);
                            DatosJugador.put("Nombre", nombreString);
                            DatosJugador.put("Fecha", fechaString);
                            DatosJugador.put("Puntuacion", contador);

                            FirebaseDatabase database =FirebaseDatabase.getInstance();
                            DatabaseReference reference = database.getReference("MI DATA BASE"); //nombre de la base de datos
                            reference.child(UidString).setValue(DatosJugador);
                            startActivity(new Intent(Register.this, Menu.class));
                            Toast.makeText(Register.this, "USUARIO REGISTRADO EXITOSAMENTE", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            Toast.makeText(Register.this, "ERROR AL REGISTRAR USUARIO", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                //Si falla el registro
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}