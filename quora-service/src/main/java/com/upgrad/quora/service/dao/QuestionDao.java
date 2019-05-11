package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Question;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;


/**
 * DAO class for Question related database operations.
 */
@Repository
public class QuestionDao {

    private static final String GETQUESTION_OF_SAME_OWNER = "getquestionOfSameOwner";
    private static final String GET_ALL_QUESTIONS = "getAllQuestions";
    private static final String GET_ALL_QUESTIONS_FOR_USER = "getAllQuestionsForUser";
    private static final String GET_QUESTION = "getQuestion";

    @PersistenceContext
    private EntityManager entityManager;


    /**
     * method used for creating question instance in database.
     *
     * @param question question object to be stored
     * @return created question object
     */
    public Question createQuestion(Question question) {
        entityManager.persist(question);
        return question;
    }

    /**
     * method used for getting all questions for a specific users uuid.
     *
     * @param uuId authorized user uuid
     * @return List of questions pertaining to the user
     */
    public List<Question> getAllQuestionsForUser(String uuId) {
        try {
            return entityManager
                    .createNamedQuery(GET_ALL_QUESTIONS_FOR_USER)
                    .setParameter("uuid", uuId)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * method used for getting all questions.
     *
     * @return list of all questions
     */
    public List<Question> getAllQuestions() {
        try {
            return entityManager
                    .createNamedQuery(GET_ALL_QUESTIONS)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * method used for getting question for the same owner
     *
     * @param questionUuId
     * @return Question object
     */
    public Question getQuestion(String questionUuId) {
        try {
            return entityManager
                    .createNamedQuery(GET_QUESTION, Question.class)
                    .setParameter("uuid", questionUuId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * method used for editing the question details in database.
     *
     * @param question question object to be updated
     */
    public Question editQuestion(Question question) {
        entityManager.persist(question);
        return question;
    }

    /**
     * method used for deleting the question
     *
     * @param question question object to be deleted
     */
    public void deleteQuestion(Question question) {
        entityManager.remove(question);
    }
}