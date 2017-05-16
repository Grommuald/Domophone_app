package com.domophone.domophone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class FirstLoginActivity extends AppCompatActivity {
    EditText user_name, user_surname, user_id;
    Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);

        user_name = (EditText) findViewById(R.id.user_name);
        user_surname = (EditText) findViewById(R.id.user_surname);
        user_id = (EditText) findViewById(R.id.user_id);

        nextButton = (Button) findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty(user_id) || isEmpty(user_surname) || isEmpty(user_id)) {
                    Toast.makeText(FirstLoginActivity.this, "Proszę, wypełnij wszystkie pola.", Toast.LENGTH_SHORT).show();
                } else {
                    if (NetworkUtils.isNetworkConnected(getApplicationContext())) {
                        ParseUser newUser = new ParseUser();

                        final String username = user_name.getText().toString() + " " + user_surname.getText().toString();
                        final String password = user_id.getText().toString();
                        newUser.setUsername(username);
                        newUser.setPassword(password);
                        /*(user_name.getText().toString(),
                                user_surname.getText().toString(),
                                user_id.getText().toString());*/
                        newUser.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(FirstLoginActivity.this, "Pomyślnie utworzono użytkownika.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(FirstLoginActivity.this, ChatActivity.class);
                                    intent.putExtra("username", username);
                                    intent.putExtra("password", password);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(FirstLoginActivity.this, "Nie udało się utworzyć użytkownika.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(FirstLoginActivity.this, "Brak połączenia z Internetem.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;
        return true;
    }
}
