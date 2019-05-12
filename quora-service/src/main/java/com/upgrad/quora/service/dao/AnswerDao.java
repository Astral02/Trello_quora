package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Answer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    EntityManager entityManager;


    public Answer createAnswer(Answer answer) {
        entityManager.persist(answer);
        return answer;
    }

    public Answer getAnswerForUuId(String answerUuId) {
        try {
            return entityManager
                    .createNamedQuery("getAnswerForUuId", Answer.class)
                    .setParameter("uuid", answerUuId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Answer editAnswer(Answer answer) {
        entityManager.persist(answer);
        return answer;
    }

    public void deleteAnswer(Answer answer) {
        entityManager.remove(answer);
    }

    public List<Answer> getAnswersForQuestion(String questionUuId) {
        try {
            return entityManager.createNamedQuery("getAnsersForQuestion", Answer.class)
                    .setParameter("uuid", questionUuId)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}