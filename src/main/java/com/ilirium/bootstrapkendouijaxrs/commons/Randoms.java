package com.ilirium.bootstrapkendouijaxrs.commons;

import java.util.Random;

/**
 *
 * @author DoDo <dopoljak@gmail.com>
 */
public class Randoms
{
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    
    public static String next(int length) 
    {
        final StringBuilder sb = new StringBuilder();
        final Random random = new Random();
        for (int i = 0; i < length; i++) 
        {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }
}
