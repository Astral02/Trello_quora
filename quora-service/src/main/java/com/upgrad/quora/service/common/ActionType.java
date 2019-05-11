package com.upgrad.quora.service.common;

/**
 * Enum describing various various actions of the user for questions and answer
 * Based on the type of action specific exceptions will be thrown.
 */
public enum ActionType {
    //Question related actions
    EDIT_QUESTION, DELETE_QUESTION, CREATE_QUESTION, ALL_QUESTION, ALL_QUESTION_FOR_USER,
    EDIT_ANSWER, DELETE_ANSWER, CREATE_ANSWER, GET_ALL_ANSWER_TO_QUESTION;
}