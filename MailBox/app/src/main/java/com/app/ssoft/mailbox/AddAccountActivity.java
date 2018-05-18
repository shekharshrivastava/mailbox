package com.app.ssoft.mailbox;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ssoft.mailbox.model.AuthenticationDetails;
import com.app.ssoft.mailbox.model.JavaxMailSettings;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

public class AddAccountActivity extends AppCompatActivity {
    private Message[] messages;
    private Store store;
    private Folder emailFolder;
    private RelativeLayout continue_btn;
    private Folder inbox;
    private Session session;
    private ProgressBar progressBar;
    private String port = "993";
    private String protocol = "IMAPS";
    private TextView tv_continue;
    private EditText password, emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        getSupportActionBar().setTitle("Add Account");
        continue_btn = findViewById(R.id.continue_btn);
        emailAddress = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        tv_continue = findViewById(R.id.tv_continue);
        progressBar = findViewById(R.id.progressBar);
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailText = emailAddress.getText().toString().trim();
                String passwordText = password.getText().toString();
                AuthenticationDetails.getInstance().setEmailID(emailText);
                AuthenticationDetails.getInstance().setPassword(passwordText);
                new startConnection().execute(emailText,passwordText);
              /*  Intent intent = new Intent(AddAccountActivity.this, MainActivity.class);
                startActivity(intent);*/
            }
        });

    }

    public class startConnection extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
          runOnUiThread(new Runnable() {
              @Override
              public void run() {
                  tv_continue.setVisibility(View.GONE);
                  progressBar.setVisibility(View.VISIBLE);
                  emailAddress.setEnabled(false);
                  password.setEnabled(false);
              }
          });

            Configuration configuration = new Configuration();
            Properties props = configuration.getImapProperties("GMAIL", port, true, protocol);
            Session session = Session.getDefaultInstance(props, null);
            try {
                store = session.getStore(protocol.toLowerCase());
                store.connect(props.getProperty("mail.host"), strings[0], strings[1]);

            } catch (final Exception ex) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddAccountActivity.this,ex.toString(),Toast.LENGTH_SHORT).show();
                    }
                });

                ex.printStackTrace();
            }
            return store.isConnected();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                JavaxMailSettings.getInstance().setStore(store);
                Intent intent = new Intent(AddAccountActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                tv_continue.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                emailAddress.setEnabled(true);
                password.setEnabled(true);
            }

        }
    }
}


