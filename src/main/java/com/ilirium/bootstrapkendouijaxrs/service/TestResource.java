package com.ilirium.bootstrapkendouijaxrs.service;

import com.ilirium.bootstrapkendouijaxrs.domain.dao.UserDAO;
import com.ilirium.bootstrapkendouijaxrs.domain.entity.User;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author DoDo <dopoljak@gmail.com>
 */
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TestResource
{
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(TestResource.class);    
    private static final UserDAO userDAO = new UserDAO();
    
    @GET @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String getHello() throws InterruptedException 
    {
        log.info(">> getHello()");

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        final String message = "HelloWorld, " + sdf.format(new Date());

        log.info("<< getHello('{}')", message);
        
        return message;
    }
   
    
    @POST @Path("/users")    
    public User createUser(User user)
    {
        log.info(">> createUser(id = {}, username = {})", user.getId(), user.getUsername());

        userDAO.create(user);
        
        log.info("<< createUser()");        
        return user;
    }
    
    @GET @Path("/users")
    public List<User> getAllUsers()
    {
        log.info(">> getAllUsers()");
        
        final List<User> users = userDAO.findAll();
        
        log.info("<< getAllUsers('#{}')", users.size());
        
        return users;
    }
    
    @GET @Path("/users/{userId}")
    public User getUserById(@PathParam("userId") Long userId)
    {
        log.info(">> getUserById(userId = '{}')", userId);
        
        final User user = userDAO.findById(userId);        
        if(user == null) {
            throw new RuntimeException("User with id = " + userId + " not found in database!");
        }
        
        log.info("<< getUserById(userId = '{}', username = '{}')", user.getId(), user.getUsername());
        
        return user;
    }
    
    @PUT @Path("/users/{userId}")
    public User updateUser(@PathParam("userId") Long userId, User usera)
    {
        log.info(">> updateUser(userId = '{}')", userId);
        
        final User user = userDAO.findById(userId);        
        if(user == null) {
            throw new RuntimeException("User with id = " + userId + " not found in database!");
        }
        
        
        log.info("<< updateUser(userId = '{}', username = '{}')", user.getId(), user.getUsername());
        
        return user;
    }
    
    @DELETE @Path("/users/{userId}")
    public User deleteUser(@PathParam("userId") Long userId, User usera)
    {
        log.info(">> deleteUser(userId = '{}')", userId);
        
        final User user = userDAO.findById(userId);        
        if(user == null) {
            throw new RuntimeException("User with id = " + userId + " not found in database!");
        }
        
        userDAO.delete(userId);
        
        log.info("<< deleteUser(userId = '{}', username = '{}')", user.getId(), user.getUsername());
        
        return user;
    }
    
}