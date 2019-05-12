package com.upgrad.quora.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.ZonedDateTime;

/**
 * POJO class for ANSWER table.
 * Updates for Answer entity
 */
@Entity
@Table(name = "answer", schema = "public")
@NamedQueries(
        {
                @NamedQuery(name = "getAnswerForUuId", query = "select ans from Answer ans where uuid=:uuid"),
                @NamedQuery(name = "getAnsersForQuestion", query = "select ans from Answer ans where ans.question.uuid=:uuid")
        }
)
public class Answer {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "ans")
    @Size(max = 255)
    @NotNull
    private String answer;

    @Column(name = "date")
    private ZonedDateTime date;

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @OneToOne
    @JoinColumn(name = "question_id")
    private Question question;

    /**
     * Accessore method for property id
     *
     * @return id value
     */
    public Integer getId() {
        return id;
    }

    /**
     * Modifier method for property id
     *
     * @param id id value
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * accessor method for property uuid
     *
     * @return uuid value
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Modifier method for property uuid
     *
     * @param uuid uuid string value
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * accessor method for property answer
     *
     * @return Answer String value
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * modifier method for property answer
     *
     * @param answer String value
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Accessore method for property date
     *
     * @return date value
     */
    public ZonedDateTime getDate() {
        return date;
    }

    /**
     * Modifier method for property date
     *
     * @param date date value
     */
    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    /**
     * Accessore method for property user
     *
     * @return UserEntity value
     */
    public UserEntity getUser() {
        return user;
    }

    /**
     * Modifier method for property user
     *
     * @param user UserEntity object
     */
    public void setUser(UserEntity user) {
        this.user = user;
    }

    /**
     * Accessore method for property question
     *
     * @return Question object
     */
    public Question getQuestion() {
        return question;
    }

    /**
     * Modfier method for property question.
     *
     * @param question question object
     */
    public void setQuestion(Question question) {
        this.question = question;
    }
}