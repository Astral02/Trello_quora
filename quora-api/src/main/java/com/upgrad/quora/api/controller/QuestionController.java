package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionService;
import com.upgrad.quora.service.business.UserCommonBusinessService;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.QuestionNotFoundException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import com.upgrad.quora.service.common.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Controller class for defining question related operations.
 */
@RestController
@RequestMapping("/")
public class QuestionController {

    @Autowired
    UserCommonBusinessService userCommonBusinessService;

    @Autowired
    QuestionService questionService;

    /**
     * Rest Endpoint method implementation used for creating question for authorized user.
     * Only logged-in user is allowed to create a question.
     *
     * @param questionRequest request object of question instance
     * @param authorization   access token of user
     * @return ResponseEntity object with response details of question
     * @throws AuthorizationFailedException if user is not signed then this exception is thrown
     */
    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> createQuestion(final QuestionRequest questionRequest, @RequestHeader final String authorization) throws AuthorizationFailedException {
        UserAuthEntity authorizedUser;
        try {
            authorizedUser = userCommonBusinessService.getUserByAccessToken(authorization);
        }catch(AuthorizationFailedException authFE){
            ErrorResponse errorResponse = new ErrorResponse().message(authFE.getErrorMessage()).code(authFE.getCode()).rootCause(authFE.getMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.FORBIDDEN);
        }
        UserEntity user = authorizedUser.getUser_id();
        Question question = new Question();
        question.setUser(authorizedUser.getUser_id());
        question.setUuid(UUID.randomUUID().toString());
        question.setContent(questionRequest.getContent());
        final ZonedDateTime now = ZonedDateTime.now();
        question.setDate(now);
        Question createdQuestion = questionService.createQuestion(question);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    /**
     * Rest Endpoint method implementation used for getting all questions for authorized user.
     * Only logged in user is allowed to get the details.
     *
     * @param authorization authorized user
     * @return ResponseEntity object with response details of question
     * @throws AuthorizationFailedException if user is not signed then this exception is thrown
     * @throws QuestionNotFoundException    thrown if there are o questions for the user
     */
    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getAllQuestions(@RequestHeader final String authorization) throws AuthorizationFailedException, QuestionNotFoundException {
        try {
            UserAuthEntity authorizedUser = userCommonBusinessService.getUserByAccessToken(authorization);
        }catch(AuthorizationFailedException authFE){
            ErrorResponse errorResponse = new ErrorResponse().message(authFE.getErrorMessage()).code(authFE.getCode()).rootCause(authFE.getMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.FORBIDDEN);
        }
        //Since the user is authorized, go for extracting questions for all users
        List<Question> questionList;
        try {
            questionList = questionService.getAllQuestions();
        }catch(QuestionNotFoundException quesFE ){
            ErrorResponse errorResponse = new ErrorResponse().message(quesFE.getErrorMessage()).code(quesFE.getCode()).rootCause(quesFE.getMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        StringBuilder builder = new StringBuilder();
        getContentsString(questionList, builder);
        StringBuilder uuIdBuilder = new StringBuilder();
        getUuIdString(questionList, uuIdBuilder);
        QuestionDetailsResponse questionResponse = new QuestionDetailsResponse()
                .id(uuIdBuilder.toString())
                .content(builder.toString());
        return new ResponseEntity<QuestionDetailsResponse>(questionResponse, HttpStatus.OK);
    }


    /**
     * Rest Endpoint method implementation used for getting all questions for any user.
     * Only logged-in user and the owner of the question is allowed to use this endpoint.
     *
     * @param questionEditRequest request for question to be edited
     * @param questionId          question to be edited
     * @param authorization       Authorized user
     * @return Response Entity of type QuestionEditResponse
     * @throws AuthorizationFailedException if user is not signed then this exception is thrown
     * @throws InvalidQuestionException     if question does not exist
     */
    @RequestMapping(method = RequestMethod.PUT, path = "/question/edit/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> editQuestionContent(QuestionEditRequest questionEditRequest, @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity authorizedUser;
        try {
            authorizedUser = userCommonBusinessService.getUserByAccessToken(authorization);
        }catch(AuthorizationFailedException authFE){
            ErrorResponse errorResponse = new ErrorResponse().message(authFE.getErrorMessage()).code(authFE.getCode()).rootCause(authFE.getMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.FORBIDDEN);
        }
        //Check if the user himself is the owner and trying to edit it and return the question object
        Question question;
        try {
            question = questionService.isUserQuestionOwner(questionId, authorizedUser, ActionType.EDIT_QUESTION);
        }catch(InvalidQuestionException iQE){
            ErrorResponse errorResponse = new ErrorResponse().message(iQE.getErrorMessage()).code(iQE.getCode()).rootCause(iQE.getMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
        }catch(AuthorizationFailedException authFE){
            ErrorResponse errorResponse = new ErrorResponse().message(authFE.getErrorMessage()).code(authFE.getCode()).rootCause(authFE.getMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.FORBIDDEN);
        }
        question.setContent(questionEditRequest.getContent());
        questionService.editQuestion(question);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(question.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse, HttpStatus.OK);
    }


    /**
     * Rest Endpoint method implementation used for deleting question by question id.
     * Only logged-in user who is owner of the question or admin is allowed to delete a question
     *
     * @param questionUuId  questionid to be deleted
     * @param authorization user to be authorized
     * @return ResponseEnitty object of type QuestionDeleteResponse
     * @throws AuthorizationFailedException if user is not signed then this exception is thrown
     * @throws InvalidQuestionException     if question does not exist
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/question/delete/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> userDelete(@PathVariable("questionId") final String questionUuId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, InvalidQuestionException {

        UserAuthEntity authorizedUser;
        try {
            authorizedUser = userCommonBusinessService.getUserByAccessToken(authorization);
        }catch(AuthorizationFailedException authFE){
            ErrorResponse errorResponse = new ErrorResponse().message(authFE.getErrorMessage()).code(authFE.getCode()).rootCause(authFE.getMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.FORBIDDEN);
        }
        //Check if the user himself is the owner and trying to edit it and return the question object
        Question question;
        try {
            question = questionService.isUserQuestionOwner(questionUuId, authorizedUser, ActionType.DELETE_QUESTION);
        }catch(InvalidQuestionException iQE){
            ErrorResponse errorResponse = new ErrorResponse().message(iQE.getErrorMessage()).code(iQE.getCode()).rootCause(iQE.getMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
        }catch(AuthorizationFailedException authFE){
            ErrorResponse errorResponse = new ErrorResponse().message(authFE.getErrorMessage()).code(authFE.getCode()).rootCause(authFE.getMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.FORBIDDEN);
        }
        questionService.deleteQuestion(question);
        QuestionDeleteResponse questionDeleteResponse = new QuestionDeleteResponse()
                .id(question.getUuid())
                .status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questionDeleteResponse, HttpStatus.OK);
    }

    //getAllQuestionsByUser

    @RequestMapping(method = RequestMethod.GET, path = "/question/all/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> getAllQuestionsByUser(@PathVariable("userId") final String uuId, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException, QuestionNotFoundException, UserNotFoundException {
        UserAuthEntity authorizedUser;
        try {
            authorizedUser = userCommonBusinessService.getUserByAccessToken(authorization);
        }catch(AuthorizationFailedException authFE){
            ErrorResponse errorResponse = new ErrorResponse().message(authFE.getErrorMessage()).code(authFE.getCode()).rootCause(authFE.getMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.FORBIDDEN);
        }
        //Get the list of questions for the user
        List<Question> questionList;
        try {
            questionList = questionService.getQuestionsForUser(uuId);
        }catch(UserNotFoundException userNFE){
            ErrorResponse errorResponse = new ErrorResponse().message(userNFE.getErrorMessage()).code(userNFE.getCode()).rootCause(userNFE.getMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
        }catch(QuestionNotFoundException quesNFE){
            ErrorResponse errorResponse = new ErrorResponse().message(quesNFE.getErrorMessage()).code(quesNFE.getCode()).rootCause(quesNFE.getMessage());
            return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.NOT_FOUND);
        }
        StringBuilder contentBuilder = new StringBuilder();
        StringBuilder uuIdBuilder = new StringBuilder();
        getContentsString(questionList, contentBuilder);
        getUuIdString(questionList, uuIdBuilder);
        QuestionDetailsResponse questionResponse = new QuestionDetailsResponse()
                .id(uuIdBuilder.toString())
                .content(contentBuilder.toString());
        return new ResponseEntity<QuestionDetailsResponse>(questionResponse, HttpStatus.OK);
    }


    /**
     * private utility method for appending the uuid of questions.
     *
     * @param questionList List of questions
     * @param uuIdBuilder  StringBuilder object
     */
    public static final StringBuilder getUuIdString(List<Question> questionList, StringBuilder uuIdBuilder) {

        for (Question questionObject : questionList) {
            uuIdBuilder.append(questionObject.getUuid()).append(",");
        }
        return uuIdBuilder;
    }

    /**
     * private utility method for providing contents string in appended format
     *
     * @param questionList list of questions
     * @param builder      StringBuilder with appended content list.
     */
    public static final StringBuilder getContentsString(List<Question> questionList, StringBuilder builder) {
        for (Question questionObject : questionList) {
            builder.append(questionObject.getContent()).append(",");
        }
        return builder;
    }
}