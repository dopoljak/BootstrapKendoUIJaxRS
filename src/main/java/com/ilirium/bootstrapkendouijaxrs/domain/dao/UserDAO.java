package com.ilirium.bootstrapkendouijaxrs.domain.dao;

import com.ilirium.bootstrapkendouijaxrs.commons.NameGenerator;
import com.ilirium.bootstrapkendouijaxrs.domain.entity.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Simple in-memory dao just for showcase ...
 * 
 * @author DoDo <dopoljak@gmail.com>
 */
public class UserDAO
{
    public static final AtomicLong currentId = new AtomicLong(0);
    public static final Map<Long, User> users = new ConcurrentHashMap<Long, User>();
    static 
    {
        final NameGenerator gen = new NameGenerator();
        final Random r = new Random();
        for (int i = 0; i < 30; i++) 
        { 
            long id = currentId.incrementAndGet();
            users.put(id, new User(id, gen.getName(), r.nextInt(99)));
        }
    }
    
    public List<User> findAll() 
    {
        final List<User> all = new ArrayList<User>();
        all.addAll(users.values());
        
        Collections.sort(all, new Comparator<User>()
        {
            public int compare(User o1, User o2)
            {
                return (int) (o1.getId() - o2.getId());
            }
        }); 
        
        return all;
    }
    
    public void create(User u)
    {
        long id = currentId.incrementAndGet();
        u.setId(id);
        users.put(id, u);
    }
    
    public User findById(Long id)
    {
        return users.get(id);
    }
    
    public void delete(Long id)
    {
        users.remove(id);
    }
}
