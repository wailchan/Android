package com.jmaker.securecredential.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jmaker.securecredential.R;
import com.jmaker.securecredential.exception.CSVException;
import com.jmaker.securecredential.file.CSVCredentialsFile;
import com.jmaker.securecredential.vo.Credential;
import com.jmaker.securecredential.file.CredentialsFile;

/**
 * Display existing credential detail or show an empty credential form.
 */
public class CredentialDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            //nfcAdapter = NfcAdapter.getDefaultAdapter(this);

            setContentView(R.layout.activity_credential_detail);

            //get credential id
            Intent intent = getIntent();
            final String id = intent.getStringExtra("id");

            //load credential from file.
            if (id != null) {
                CredentialsFile cf = new CSVCredentialsFile();
                Credential credential = cf.getCredential(Integer.parseInt(id));

                EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
                nameEditText.setText(credential.getName(), TextView.BufferType.EDITABLE);

                EditText userNameEditText = (EditText) findViewById(R.id.userNameEditText);
                userNameEditText.setText(credential.getUserName(), TextView.BufferType.EDITABLE);

                EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
                passwordEditText.setText(credential.getPassword(), TextView.BufferType.EDITABLE);

                EditText descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
                descriptionEditText.setText(credential.getDescription(), TextView.BufferType.EDITABLE);

                EditText uriEditText = (EditText) findViewById(R.id.uriEditText);
                uriEditText.setText(credential.getUri(), TextView.BufferType.EDITABLE);
            }

            //add listener for the save button
            Button saveButton = (Button) findViewById(R.id.saveButton);
            saveButton.setClickable(true);
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent listCredentialsActivity = new Intent(getBaseContext(), ListCredentialsActivity.class);
                        save(id);
                        startActivity(listCredentialsActivity);
                    } catch (CSVException csvException) {
                        //TODO
                        csvException.printStackTrace();
                    }
                }
            });

            //add listener for the delete button
            Button deleteButton = (Button) findViewById(R.id.deleteButton);
            deleteButton.setClickable(id != null);  //if the credential exists, make the delete button clickable.
            deleteButton.setEnabled(id != null);  //if the credential exists, enable the delete button.
            if (id != null) {
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent listCredentialsActivity = new Intent(getBaseContext(), ListCredentialsActivity.class);
                            delete(id);
                            startActivity(listCredentialsActivity);
                        } catch (CSVException csvException) {
                            //TODO
                            csvException.printStackTrace();
                        }
                    }
                });
            }

            //add listener for the cancel button
            Button cancelButton = (Button) findViewById(R.id.cancelButton);
            cancelButton.setClickable(true);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go back to the list credentials page
                    Intent listCredentialsActivity = new Intent(getBaseContext(), ListCredentialsActivity.class);
                    startActivity(listCredentialsActivity);
                }
            });
        } catch (CSVException csvException) {
            //TODO
            csvException.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //this.save(null);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Add or update credential by credential id.
     * @param id credential id
     */
    private void save(String id) throws CSVException {
        CredentialsFile cf = new CSVCredentialsFile();

        EditText nameEditText = (EditText) findViewById(R.id.nameEditText);
        String name = nameEditText.getText().toString();

        EditText userNameEditText = (EditText) findViewById(R.id.userNameEditText);
        String userName = userNameEditText.getText().toString();

        EditText passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        String password = passwordEditText.getText().toString();

        EditText descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        String description = descriptionEditText.getText().toString();

        EditText uriEditText = (EditText) findViewById(R.id.uriEditText);
        String uri = uriEditText.getText().toString();

        if (id == null) {
            //add credential
            Credential credential = new Credential(name, userName, password, description, uri);
            cf.addCredential(credential);
        } else {
            //update credential
            Credential credential = new Credential(Integer.parseInt(id), name, userName, password, description, uri);
            cf.updateCredential(credential);
        }
    }

    /**
     * Delete credential by id
     * @param id the credential id
     * @throws CSVException
     */
    private void delete(String id) throws CSVException {
        CredentialsFile cf = new CSVCredentialsFile();
        cf.deleteCredential(Integer.parseInt(id));
    }
}