package com.jmaker.securecredential.file;

import android.os.Environment;

import com.jmaker.securecredential.exception.CSVException;
import com.jmaker.securecredential.vo.Credential;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by wchan on 10/31/2016.
 * Read and write the CSV credential file.
 */

public class CSVCredentialsFile extends CredentialsFile {

    //TODO make the CSV filename needs configurable
    final static private String filename = "credentials.csv";    //the CSV filename
    private final static String DELIMITER = ",";    //delimiter in the CSV file



    /**
     * Get the CSV file
     * @return the CSV file
     */
    private File getCSVFile() {
        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES);
        return new File(path, "/" + filename);
    }

    /**
     * Read all credentials from the CSV file.
     * @return List of credential objects.
     * @throws CSVException
     */
    public List<Credential> getCredentials() throws CSVException {
        List<Credential> credentials = new ArrayList<Credential>();

        BufferedReader br = null;
        try {
            String currentLine = "";
            br = new BufferedReader(new FileReader(getCSVFile()));
            while ((currentLine = br.readLine()) != null) {
                try {
                    String[] fields = currentLine.split(DELIMITER);

                    if (fields.length == 6) {
                        int id = Integer.valueOf(decrypt(fields[0]));
                        String name = decrypt(fields[1]);
                        String userName = decrypt(fields[2]);
                        String password = decrypt(fields[3]);
                        String description = decrypt(fields[4]);
                        String uri = decrypt(fields[5]);

                        Credential credential = new Credential(id, name, userName, password, description, uri);
                        credentials.add(credential);
                    }
                } catch (Exception e) {
                    System.err.print("Failed to parse the row:\t" + currentLine);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw new CSVException("Failed to load the credentials.", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    throw new CSVException("Failed to open the file.", e);
                }
            }
        }

        return credentials;
    }

    /**
     * Read credential by Id from the CSV file.
     * @param id the credential Id.
     * @return the credential object.
     */
    public Credential getCredential(int id) throws CSVException
    {
        Credential credential = null;

        BufferedReader br = null;
        try {
            String currentLine = "";
            br = new BufferedReader(new FileReader(getCSVFile()));
            while ((currentLine = br.readLine()) != null) {
                try {
                    String[] fields = currentLine.split(DELIMITER);

                    int currentId = Integer.valueOf(decrypt(fields[0]));
                    if (id == currentId) {
                        String name = decrypt(fields[1]);
                        String userName = decrypt(fields[2]);
                        String password = decrypt(fields[3]);
                        String description = decrypt(fields[4]);
                        String uri = decrypt(fields[5]);

                        credential = new Credential(currentId, name, userName, password, description, uri);
                        break;
                    }
                } catch (Exception e) {
                    System.err.print("Failed to parse the row:\t" + currentLine);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw new CSVException("Failed to load the credential.", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    throw new CSVException("Failed to open the file.", e);
                }
            }
        }

        return credential;
    }

    /**
     * Add a new credential to the CSV file.
     * @param credential the new credential.
     * @throws CSVException
     */
    public void addCredential(Credential credential) throws CSVException {
        FileWriter writer = null;
        BufferedWriter bw = null;

        try {
            writer = new FileWriter(getCSVFile(), true);
            bw = new BufferedWriter(writer);

            StringBuffer newLine = new StringBuffer();

            newLine.append(this.encrypt(String.valueOf(getNewId())));
            newLine.append(DELIMITER);
            newLine.append(this.encrypt(credential.getName()));
            newLine.append(DELIMITER);
            newLine.append(this.encrypt(credential.getUserName()));
            newLine.append(DELIMITER);
            newLine.append(this.encrypt(credential.getPassword()));
            newLine.append(DELIMITER);
            newLine.append(this.encrypt(credential.getDescription()));
            newLine.append(DELIMITER);
            newLine.append(this.encrypt(credential.getUri()));

            bw.write(newLine.toString());
            bw.newLine();
            bw.flush();
        } catch (Exception e) {
            throw new CSVException("Failed to add the new credential.", e);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {
                throw new CSVException("Failed to close the file.", e);
            }
        }
    }

    /**
     * Update the credential in the CSV file.
     * @param credential credential with the updated attributes.
     * @throws CSVException
     */
    public void updateCredential(Credential credential) throws CSVException
    {
        FileWriter writer = null;
        BufferedWriter bw = null;

        try {
            //get credentials from the credentials file before opening the file for writing.
            List<Credential> credentials = this.getCredentials();

            writer = new FileWriter(getCSVFile(), false);
            bw = new BufferedWriter(writer);

            for (int index = 0; index < credentials.size(); index++) {
                Credential currentCredential = credentials.get(index);
                if (currentCredential.getId() == credential.getId()) {
                    currentCredential = credential;
                }

                StringBuffer newLine = new StringBuffer();

                newLine.append(this.encrypt(String.valueOf(currentCredential.getId())));
                newLine.append(DELIMITER);
                newLine.append(this.encrypt(currentCredential.getName()));
                newLine.append(DELIMITER);
                newLine.append(this.encrypt(currentCredential.getUserName()));
                newLine.append(DELIMITER);
                newLine.append(this.encrypt(currentCredential.getPassword()));
                newLine.append(DELIMITER);
                newLine.append(this.encrypt(currentCredential.getDescription()));
                newLine.append(DELIMITER);
                newLine.append(this.encrypt(currentCredential.getUri()));

                bw.write(newLine.toString());
                bw.newLine();
                bw.flush();
            }
        } catch (Exception e) {
            throw new CSVException("Failed to save the credential.", e);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {
                throw new CSVException("Failed to close the file.", e);
            }
        }
    }

    /**
     * Delete credential by id from the CSV file.
     * @param id the credential id
     * @throws CSVException
     */
    public void deleteCredential(int id) throws CSVException
    {
        FileWriter writer = null;
        BufferedWriter bw = null;

        try {
            //get credentials from the credentials file before opening the file for writing.
            List<Credential> credentials = this.getCredentials();

            writer = new FileWriter(getCSVFile(), false);
            bw = new BufferedWriter(writer);

            for (int index = 0; index < credentials.size(); index++) {
                Credential currentCredential = credentials.get(index);
                if (currentCredential.getId() != id) {
                    StringBuffer newLine = new StringBuffer();

                    newLine.append(this.encrypt(String.valueOf(currentCredential.getId())));
                    newLine.append(DELIMITER);
                    newLine.append(this.encrypt(currentCredential.getName()));
                    newLine.append(DELIMITER);
                    newLine.append(this.encrypt(currentCredential.getUserName()));
                    newLine.append(DELIMITER);
                    newLine.append(this.encrypt(currentCredential.getPassword()));
                    newLine.append(DELIMITER);
                    newLine.append(this.encrypt(currentCredential.getDescription()));
                    newLine.append(DELIMITER);
                    newLine.append(this.encrypt(currentCredential.getUri()));

                    bw.write(newLine.toString());
                    bw.newLine();
                    bw.flush();
                }
            }
        } catch (Exception e) {
            throw new CSVException("Failed to delete the credential.", e);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {
                throw new CSVException("Failed to close the file.", e);
            }
        }
    }

    /**
     * Get the new credential id
     * @return the new credential id
     * @throws CSVException
     */
    protected int getNewId() throws CSVException {
        int newId = 0;
        List<Credential> credentials = this.getCredentials();
        for (int index = 0; index < credentials.size(); index++) {
            Credential credential = credentials.get(index);
            if (credential.getId() > newId) {
                newId = credential.getId();
            }
        }

        return ++newId; //increment the largest existing id as the new id
    }
}