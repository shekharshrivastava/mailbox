package com.app.ssoft.mailbox;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.app.ssoft.mailbox.Utils.DividerItemDecoration;
import com.app.ssoft.mailbox.model.AuthenticationDetails;
import com.app.ssoft.mailbox.model.JavaxMailSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,SwipeRefreshLayout.OnRefreshListener, MessagesAdapter.MessageAdapterListener {

    private Message[] messages;
    private Store store;
    private Folder emailFolder;
    private Folder folder;
    private String port = "993";
    private String protocol = "IMAPS";
    private Folder[] defaultFolders;
    private ProgressBar progressBar;
    private TextView emailTV;
    private TextView userNameTv;
    private InputStream inStream;
    private ArrayList<Message> messagesList;
    private MessagesAdapter mAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        progressBar = findViewById(R.id.progressBar);
        store = JavaxMailSettings.getInstance().getStore();

//        check(host, mailStoreType, username, password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout =
                navigationView.inflateHeaderView(R.layout.nav_header_main);
        emailTV = headerLayout.findViewById(R.id.emailTV);
        userNameTv = headerLayout.findViewById(R.id.userNameTV);
        userNameTv.setVisibility(View.GONE);
        emailTV.setText(AuthenticationDetails.getInstance().getEmailID());
        new getFolders().execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        String folderName = (String) item.getTitle();
        new getMessages().execute(folderName);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onIconClicked(int position) {

    }

    @Override
    public void onIconImportantClicked(int position) {

    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onRowLongClicked(int position) {

    }


    public class getFolders extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });

            try {
                defaultFolders = store.getDefaultFolder().list();
                for (Folder fd : defaultFolders)
                    System.out.println(">> " + fd.getName());

            } catch (Exception ex) {
                ex.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                addMenuItemInNavMenuDrawer(defaultFolders);
                progressBar.setVisibility(View.GONE);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }


    public class getMessages extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });

            try {
                messagesList = new ArrayList<>();
                folder = store.getFolder(strings[0]);
                folder.open(Folder.READ_ONLY);
                javax.mail.Message messages[] = folder.getMessages();
//                messagesList.addAll(Arrays.asList(messages));
//                javax.mail.Message messages[]   = folder.search(new FlagTerm(
//                        new Flags(Flags.Flag.SEEN), false));
                for (int i = 0; i <messages.length; i++) {
                    javax.mail.Message message = messages[i];
                  /*  Address[] from = message.getFrom();
                    System.out.println("-------------------------------");
                    System.out.println("Date : " + message.getSentDate());
                    System.out.println("From : " + from[0]);
                    System.out.println("Subject: " + message.getSubject());
                    System.out.println("Content :" + message.getDescription());
                    getTextFromMessage(message);
                    System.out.println("--------------------------------");*/
                    messagesList.add(message);
                }
           /*     folder.close(true);
                store.close();*/


            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter = new MessagesAdapter(MainActivity.this, messagesList, MainActivity.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, LinearLayoutManager.VERTICAL));
            recyclerView.setAdapter(mAdapter);
            progressBar.setVisibility(View.GONE);
        }
    }

    public void processMessageBody(javax.mail.Message message) {
        try {
            Object content = message.getContent();
            // check for string
            // then check for multipart
            if (content instanceof String) {
                System.out.println(content);
            } else if (content instanceof Multipart) {
                Multipart multiPart = (Multipart) content;
                procesMultiPart(multiPart);
            } else if (content instanceof InputStream) {
                inStream = (InputStream) content;
                int ch;
                while ((ch = inStream.read()) != -1) {
                    System.out.write(ch);

                }
                inputStreamToString(inStream);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void procesMultiPart(Multipart content) {
        try {
            int multiPartCount = content.getCount();
            for (int i = 0; i < multiPartCount; i++) {
                BodyPart bodyPart = content.getBodyPart(i);
                Object o;
                o = bodyPart.getContent();
                if (o instanceof String) {
                    System.out.println(o);
                } else if (o instanceof Multipart) {
                    procesMultiPart((Multipart) o);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void addMenuItemInNavMenuDrawer(Folder[] folders) throws MessagingException {
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);

        Menu menu = navView.getMenu();
        Menu submenu = menu.addSubMenu("OUTLOOK");
        for (Folder folder : folders) {
            submenu.add(folder.getFullName());
        }


        navView.invalidate();

    }

    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
//                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }

    public String inputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            out.append(line);
        }
        System.out.println(out.toString());   //Prints the string content read from input stream
        reader.close();
        return out.toString();
    }
}