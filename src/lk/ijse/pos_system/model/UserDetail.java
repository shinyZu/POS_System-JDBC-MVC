package lk.ijse.pos_system.model;

import lk.ijse.pos_system.controller.User;

public class UserDetail {
    private String userId;
    private String userName;
    private String userPassword;
    private User userType;

    public UserDetail() {}

    public UserDetail(String userId, String userName, String userPassword, User userType) {
        this.setUserId(userId);
        this.setUserName(userName);
        this.setUserPassword(userPassword);
        this.setUserType(userType);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public User getUserType() {
        return userType;
    }

    public void setUserType(User userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "UserDetail{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", userType=" + userType +
                '}';
    }
}
