package com.upgrad.quora.service.dao;/* Create by Mansi Elhance */

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;


@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity){
        entityManager.persist(userEntity);
        return userEntity;
    }

    public UserEntity getUser(final String userUuid){
        try{
            return entityManager.createNamedQuery("userByUuid",UserEntity.class).setParameter("uuid",userUuid).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    public UserEntity getUserByEmail(final String email){
        try{
            return entityManager.createNamedQuery("userByEmail",UserEntity.class).setParameter("email",email).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    public UserEntity getUserByUsername(final String username){
        try{
            return entityManager.createNamedQuery("userByUsername",UserEntity.class).setParameter("username",username).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    public void updateUserEntity(final UserAuthEntity userAuthEntity){
        entityManager.merge(userAuthEntity);
    }

    public UserEntity getUserByUuid(final String uuid){
        try{
            return entityManager.createNamedQuery("userByUuid",UserEntity.class).setParameter("uuid",uuid).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    public UserAuthEntity createAuthToken(final UserAuthEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    public void updateUser(final UserEntity userEntity){
        entityManager.merge(userEntity);
    }

    public UserAuthEntity getUserAuthToken(final String access_token){
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken",UserAuthEntity.class).setParameter("access_token",access_token).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    public String deleteUser(final UserEntity userEntity){
        String uuid=userEntity.getUuid();
        entityManager.remove(userEntity);
        return uuid;

    }
    public UserAuthEntity getUserAuthTokenByUuid(final String uuid){
        try {
            return entityManager.createNamedQuery("userAuthTokenByUuid",UserAuthEntity.class).setParameter("uuid",uuid).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

}

