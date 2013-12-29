/**
 * 
 */
package com.talent.nio.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.talent.nio.utils.clone.FastByteArrayOutputStream;

/**
 * 
 * @author 谭耀武
 * @date 2013-1-23
 * 
 */
public class CloneUtils
{
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(CloneUtils.class);

    /**
	 * 
	 */
    public CloneUtils()
    {
    }

    /**
     * 
     * @author tanyaowu
     * @param srcObject
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static final Object deepClone(java.io.Serializable srcObject) throws IOException, ClassNotFoundException
    {
        FastByteArrayOutputStream fbos = new FastByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(fbos);
        out.writeObject(srcObject);
        out.close();

        ObjectInputStream in = new ObjectInputStream(fbos.getInputStream());
        Object ret = in.readObject();
        in.close();
        return ret;

        // ByteArrayOutputStream bytearrayoutputstream = new
        // ByteArrayOutputStream(100);
        // ObjectOutputStream objectoutputstream = new
        // ObjectOutputStream(bytearrayoutputstream);
        // objectoutputstream.writeObject(srcObject);
        // byte abyte0[] = bytearrayoutputstream.toByteArray();
        // objectoutputstream.close();
        // ByteArrayInputStream bytearrayinputstream = new
        // ByteArrayInputStream(abyte0);
        // ObjectInputStream objectinputstream = new
        // ObjectInputStream(bytearrayinputstream);
        // Object clone = objectinputstream.readObject();
        // objectinputstream.close();
        // return clone;

    }

    /**
     * @author tanyaowu
     * @param args
     */
    public static void main(String[] args)
    {

    }
}
