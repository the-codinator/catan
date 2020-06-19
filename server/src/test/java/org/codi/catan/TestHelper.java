/*
 * @author the-codinator
 * created on 2020/6/19
 */

package org.codi.catan;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import org.codi.catan.model.game.Board;
import org.codi.catan.model.game.State;

public class TestHelper {

    private static final ObjectMapper mapper = new ObjectMapper();

    private static StackTraceElement getCallingStackTraceElement() {
        boolean thisClass = false;
        for (var element : Thread.currentThread().getStackTrace()) {
            if (!thisClass) {
                if (TestHelper.class.getName().equals(element.getClassName())) {
                    thisClass = true;
                }
            } else {
                if (!TestHelper.class.getName().equals(element.getClassName())) {
                    return element;
                }
            }
        }
        throw new RuntimeException("Could not resolve calling class from StackTrace");
    }

    private static String getDataFilePath(String type) {
        var caller = getCallingStackTraceElement();
        String className = caller.getClassName();
        className = className.replaceAll("(\\w+\\.)+", "");
        String methodName = caller.getMethodName();
        return className + File.separator + methodName + File.separator + type + ".json";
    }

    public static <T> T getResource(Class<T> clazz) {
        String path = getDataFilePath(clazz.getSimpleName());
        try {
            return mapper.readValue(new File(path), clazz);
        } catch (IOException e) {
            throw new RuntimeException("Error reading test resource - " + path, e);
        }
    }

    public static Board getBoard() {
        return getResource(Board.class);
    }

    public static State getState() {
        return getResource(State.class);
    }
}
