package vn.tlu.k64httt4.nhom24.udqlnhahangorderzone.Model;

import com.google.firebase.Timestamp;

public class paNotice {
    private String id;
    private String title; // Corresponds to txtTieuDe
    private String content; // Corresponds to txtNoiDung
    private String senderId; // Thêm trường để lưu userId của người gửi
    private Timestamp timestamp;

    public paNotice() {
    }

    public paNotice(String title, String content, String senderId) {
        this.title = title;
        this.content = content;
        this.senderId = senderId;
        this.timestamp = Timestamp.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}