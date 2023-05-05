package com.example.juego_crit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    EditText MailLogin, PswLogin;
    Button btnLogin;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MailLogin = (EditText)findViewById(R.id.etMailLogin);
        PswLogin = (EditText)findViewById(R.id.etPswLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        auth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = MailLogin.getText().toString();
                String password = PswLogin.getText().toString();

                //Firebase acepta contraseñas mayor o igual a 6 caracteres
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){           //Validar correo
                    MailLogin.setError("Correo no válido");
                    MailLogin.setFocusable(true);
                } else if (PswLogin.length()<6) {
                    PswLogin.setError("Contraseña mínima de 6 caracteres");     //Validar contraseña
                    PswLogin.setFocusable(true);
                }else{
                    PlayerLogin(email, password);
                }
            }
        });
    }
    //METODO PARA EL LOGIN DEL JUGADOR
    private void PlayerLogin(String email, String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            startActivity(new Intent(Login.this, Menu.class));
                            assert user != null;    //AFIRMAMOS QUE EL USUARIO NO ES NULO
                            Toast.makeText(Login.this, "BIENVENIDO(A) "+user.getEmail(), Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}