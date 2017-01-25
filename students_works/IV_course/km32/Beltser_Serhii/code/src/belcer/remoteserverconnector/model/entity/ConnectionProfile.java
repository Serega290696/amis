package belcer.remoteserverconnector.model.entity;

import java.sql.Date;

public class ConnectionProfile {

  private String title;
  private String host;
  private int port;
  private String connectionUser;
  private String connectionPass;
  private String protocol;
  private Date dateCreated;
  private Date dateModified;
  private Date lastConnectionDate;
  private int deleted;
  private int savedByUser;
  private String username;

  public ConnectionProfile(String title, String host, int port, String connectionUser, String connectionPass, String protocol, int savedByUser, String username) {
    this(title, host, port, connectionUser, connectionPass, protocol, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()), null,
        0, savedByUser, username);
  }

  public ConnectionProfile(String title,
                           String host, int port, String connectionUser, String connectionPass, String protocol,
                           Date dateCreated, Date dateModified, Date lastConnectionDate,
                           int deleted,
                           int savedByUser, String username) {
    this.title = title;
    this.host = host;
    this.port = port;
    this.connectionUser = connectionUser;
    this.connectionPass = connectionPass;
    this.protocol = protocol;
    this.dateCreated = dateCreated;
    this.dateModified = dateModified;
    this.lastConnectionDate = lastConnectionDate;
    this.deleted = deleted;
    this.savedByUser = savedByUser;
    this.username = username;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getConnectionUser() {
    return connectionUser;
  }

  public void setConnectionUser(String connectionUser) {
    this.connectionUser = connectionUser;
  }

  public String getConnectionPass() {
    return connectionPass;
  }

  public void setConnectionPass(String connectionPass) {
    this.connectionPass = connectionPass;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public String getUser() {
    return username;
  }

  public void setUser(String username) {
    this.username = username;
  }

  public Date getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }

  public Date getDateModified() {
    return dateModified;
  }

  public void setDateModified(Date dateModified) {
    this.dateModified = dateModified;
  }

  public Date getLastConnectionDate() {
    return lastConnectionDate;
  }

  public void setLastConnectionDate(Date lastConnectionDate) {
    this.lastConnectionDate = lastConnectionDate;
  }

  public int isDeleted() {
    return deleted;
  }

  public void setDeleted(int deleted) {
    this.deleted = deleted;
  }

  public int isSavedByUser() {
    return savedByUser;
  }

  public String getUsername() {
    return username;
  }

  @Override
  public String toString() {
    return "ConnectionProfile{" +
        "title='" + title + '\'' +
        ", host='" + host + '\'' +
        ", port='" + port + '\'' +
        ", connectionUser='" + connectionUser + '\'' +
        ", connectionPass='" + connectionPass + '\'' +
        ", protocol='" + protocol + '\'' +
        ", dateCreated=" + dateCreated +
        ", dateModified=" + dateModified +
        ", lastConnectionDate=" + lastConnectionDate +
        ", deleted=" + deleted +
        ", savedByUser=" + savedByUser +
        ", username='" + username + '\'' +
        '}';
  }
}
