package com.canto.firstspirit.api.model;
/*
{
        "permissions":[
        "download",
        "upload"
        ],
        "userId":"bjoern@fernhomberg.de",
        "firstName":"Björn",
        "lastName":"Fernhomberg",
        "email":"bjoern@fernhomberg.de",
        "lastAccessVersion":"BKK",
        "name":"Björn Fernhomberg"
        }
*/

@SuppressWarnings("unused")
public class CantoUser {

  private String userId;
  private String name;
  private String email;
  private String firstName;
  private String lastName;
  private String[] permissions;
  private String lastAccessVersion;

  public CantoUser() {

  }

  public CantoUser(String userId, String name, String email, String firstName, String lastName, String[] permissions, String lastAccessVersion) {
    this.userId = userId;
    this.name = name;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.permissions = permissions;
    this.lastAccessVersion = lastAccessVersion;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String[] getPermissions() {
    return permissions;
  }

  public void setPermissions(String[] permissions) {
    this.permissions = permissions;
  }

  public String getLastAccessVersion() {
    return lastAccessVersion;
  }

  public void setLastAccessVersion(String lastAccessVersion) {
    this.lastAccessVersion = lastAccessVersion;
  }
}
