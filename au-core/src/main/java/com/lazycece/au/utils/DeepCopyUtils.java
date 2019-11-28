package com.lazycece.au.utils;

import java.io.*;

/**
 * Deep copy of an Object by serialize, so the Object which will be copied must be Serializable
 *
 * @author lazycece
 * @date 2019/11/09
 */
public class DeepCopyUtils {

    public static Object copy(Object object) throws NotSerializableException {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            out.close();

            ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(bos.toByteArray()));
            return in.readObject();
        } catch (NotSerializableException e) {
            // if (e instance of NotSerializableException) throw e
            throw e;
        } catch (IOException | ClassNotFoundException e) {
            // if (e instance of IOException and ClassNotFoundException)
            // print message and return null
            e.printStackTrace();
            return null;
        }
    }
}

