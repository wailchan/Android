package com.jmaker.securecredential.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jmaker.securecredential.R;
import com.jmaker.securecredential.exception.CSVException;
import com.jmaker.securecredential.file.CSVCredentialsFile;
import com.jmaker.securecredential.vo.Credential;
import com.jmaker.securecredential.file.CredentialsFile;

import java.util.List;


/**
 * Display all credentials by showing the credential names.
 */
public class ListCredentialsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_credentials);

        //add listener to the add button
        Button addButton = (Button) findViewById(R.id.addButton);
        addButton.setClickable(true);
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //display in the credential detail page
                Intent credentialDetailActivity = new Intent(getBaseContext(), CredentialDetailActivity.class);
                startActivity(credentialDetailActivity);
            }
        });

        this.listCredentials();
    }

    /**
     * List the credential names on the table.
     */
    private void listCredentials() {
        try {
            TableLayout listTableLayout= (TableLayout) findViewById(R.id.listTableLayout);

            //Get credentials from the credentials file.
            CredentialsFile cf = new CSVCredentialsFile();
            List<Credential> credentials = cf.getCredentials();

            for (int index = 0; index < credentials.size(); index++) {
                final Credential credential = credentials.get(index);

                TableRow tr = new TableRow(this);
                tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                TextView textview = new TextView(this);
                textview.setText(credential.getName());
                textview.setTextColor(Color.BLACK);
                tr.addView(textview);
                listTableLayout.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                //add listener to the table row
                tr.setClickable(true);
                tr.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        //display in the credential detail page
                        Intent credentialDetailActivity = new Intent(getBaseContext(), CredentialDetailActivity.class);
                        credentialDetailActivity.putExtra("id", String.valueOf(credential.getId()));
                        startActivity(credentialDetailActivity);
                    }
                });
            }
        } catch (CSVException csvException) {
            //if no credential entry was created, the CSV file will not exist.
            csvException.printStackTrace();
        }
    }
}
