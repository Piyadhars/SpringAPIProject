package com.api.springapiproject.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class Account {
    @Id
    private String mailId;

    private List<String> friends;

    private List<String> requests;
    private List<String> follow_request;
    private List<String> following;

    private long result;


    public Account(String mailId, List<String> friends, List<String> requests, List<String> follow_request, List<String> follwing, long result) {
        this.mailId = mailId;
        this.friends = friends;
        this.requests = requests;
        this.follow_request=follow_request;
        this.following=follwing;
        this.result = result;
    }

    public Account() {
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<String> getRequests() {
        return requests;
    }

    public void setRequests(List<String> requests) {
        this.requests = requests;
    }

    public List<String> getFollow_request() {
        return follow_request;
    }

    public void setFollow_request(List<String> follow_request) {
        this.follow_request = follow_request;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public long getResult() {
        return result;
    }

    public void setResult(long result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Account{" +
                "mailId='" + mailId + '\'' +
                ", friends=" + friends +
                ", requests=" + requests +
                ", follow_requests=" + follow_request +
                ", following=" + following +
                '}';
    }
}
