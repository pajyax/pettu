package com.petgroomer.dto;

import java.time.LocalDateTime;

public class DownvoteDTO {

    private Long id;
    private Long groomerId;
    private Long customerId;
    private Long questionId;
    private Long answerId;
    private LocalDateTime createdAt;

    // Getters and Setters
    // ...


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getGroomerId() {
        return groomerId;
    }
    public void setGroomerId(Long groomerId) {
        this.groomerId = groomerId;
    }
    public Long getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }
    public Long getQuestionId() {
        return questionId;
    }
    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
    public Long getAnswerId() {
        return answerId;
    }
    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
