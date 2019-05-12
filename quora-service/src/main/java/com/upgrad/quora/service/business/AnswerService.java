package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.Answer;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.common.ActionType;
import com.upgrad.quora.service.common.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnswerService {

    @Autowired
    AnswerDao answerDao;

    @Autowired
    QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public Answer createAnswer(Answer answer) {
        return answerDao.createAnswer(answer);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Answer getAnswerForUuId(String answerUuId) throws AnswerNotFoundException {
        Answer answer = answerDao.getAnswerForUuId(answerUuId);
        if (answer == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        } else {
            return answer;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Answer isUserAnswerOwner(String answerUuId, UserAuthEntity authorizedUser, ActionType actionType) throws AnswerNotFoundException, AuthorizationFailedException {
        Answer answer = answerDao.getAnswerForUuId(answerUuId);

        if (answer == null) {
            //if provided answer uuid is not present in database, then throw this exception
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        } else if (!authorizedUser.getUser_id().getUuid().equals(answer.getUser().getUuid())) {
            if (ActionType.EDIT_ANSWER.equals(actionType)) {
                //if users dont match and action is for editing then throw exception with below message
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
            } else {
                //means we are in DELETE action and we should throw separate message for the exception
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
            }
        } else if ((!authorizedUser.getUser_id().getRole().equals(RoleType.admin)
                && !authorizedUser.getUser_id().getUuid().equals(answer.getUser().getUuid()))
                && ActionType.DELETE_ANSWER.equals(actionType)) {
            //In delete mode if role is not admin or the user is not owner of the answer then throw below exception
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        } else {
            return answer;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Answer editAnswer(Answer answer) {
        return answerDao.editAnswer(answer);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnswer(Answer answer) {
        answerDao.deleteAnswer(answer);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<Answer> getAnswersForQuestion(String questionUuId) throws AnswerNotFoundException, InvalidQuestionException {
        //check if the question exists in question database
        Question question = questionDao.getQuestion(questionUuId);
        if (question == null) {
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }

        //get the list of Answers for question
        List<Answer> answerList = answerDao.getAnswersForQuestion(questionUuId);
        if (answerList == null) {
            throw new AnswerNotFoundException("OTHR-001", "No Answers available for the given question uuid");
        } else {
            return answerList;
        }
    }
}