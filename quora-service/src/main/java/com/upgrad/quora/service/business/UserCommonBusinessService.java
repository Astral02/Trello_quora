package com.upgrad.quora.service.business;/* Create by Mansi Elhance */

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCommonBusinessService  {

    @Autowired
    private UserDao userDao;

    /**
     * Get the users details
     * @param userUuid
     * @param authorizationToken
     * @return
     * @throws AuthorizationFailedException
     * @throws UserNotFoundException
     */
    public UserEntity getUser(final String userUuid, final String authorizationToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity userAuthEntity=userDao.getUserAuthToken(authorizationToken);
        if(userAuthEntity == null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        } else if(userAuthEntity.getLogout_at() != null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        } else if(userDao.getUserByUuid(userUuid) == null){
            throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
        } else{
            return userDao.getUserByUuid(userAuthEntity.getUuid());
        }

    }

    /**
     * Gets the user by access token
     * @param authorizationToken
     * @return
     * @throws AuthorizationFailedException
     */
    public UserAuthEntity getUserByAccessToken(String authorizationToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthTokenEntity = userDao.getUserAuthToken(authorizationToken);
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthTokenEntity.getLogout_at() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        }
        //UserEntity userEntity =  userDao.getUser(userAuthTokenEntity.getUuid());
        //if (userEntity.getRole().equalsIgnoreCase("nonadmin")) {
        //    throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        //}
        return userAuthTokenEntity;
    }
}
