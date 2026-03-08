package org.example.models;

public class PrintJob {
    private String content;
    private String requester;
    private long timestamp;

    public PrintJob(String content, String requester) {
        this.content = content;
        this.requester = requester;
        this.timestamp = System.currentTimeMillis();
    }

    public String getContent() { return content; }
    public String getRequester() { return requester; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "PrintJob{requester='" + requester + "', content='" + content + "'}";
    }
}
