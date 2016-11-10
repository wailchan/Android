package com.jmaker.securecredential.vo;

/**
 * Credential object holds credential attributes.
 * Created by wchan on 8/30/2016.
 */
public class Credential {
    private int id;  //credential id
    private String name;    //credential name
    private String userName;    //credential user name
    private String password;    //credential password
    private String description; //credential description
    private String uri; //credential URI

    /**
     * Constructor of the Credential value object. Use this constructor for creation of a brand new
     * Credential as it doesn't requires id.
     * @param name
     * @param userName
     * @param password
     * @param description
     * @param uri
     */
    public Credential(String name, String userName, String password, String description, String uri) {
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.description = description;
        this.uri = uri;
    }

    /**
     * Constructor of the Credential value object. Use this contructor for existing credetnail and it
     * requires id.
     * @param id
     * @param name
     * @param userName
     * @param password
     * @param description
     * @param uri
     */
    public Credential(int id, String name, String userName, String password, String description, String uri) {
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.description = description;
        this.uri = uri;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("{id=");
        sb.append(id);
        sb.append(", name=");
        sb.append(name);
        sb.append(", userName=");
        sb.append(userName);
        sb.append(", password=");
        sb.append(password);
        sb.append(", description=");
        sb.append(description);
        sb.append(", uri=");
        sb.append(uri);
        sb.append("}");
        return sb.toString();
    }
}
