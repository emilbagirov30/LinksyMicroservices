package com.emil.linksy_user.model;

public class EmailRequest {
    private String to;
    private String title;
    private String body;

    public EmailRequest() {
    }
    public EmailRequest(String to, String title, String body) {
        this.to = to;
        this.title = title;
        this.body = body;
    }


    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String subject) {
        this.title = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
