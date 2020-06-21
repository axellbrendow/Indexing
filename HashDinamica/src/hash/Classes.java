package hash;

import java.util.ArrayList;
import java.util.HashMap;

public class Classes
{
    public static ArrayList<Class<?>> primitivos = new ArrayList<>();
    static
    {
        primitivos.add(boolean.class);
        primitivos.add(char.class);
        primitivos.add(byte.class);
        primitivos.add(short.class);
        primitivos.add(int.class);
        primitivos.add(long.class);
        primitivos.add(float.class);
        primitivos.add(double.class);
        primitivos.add(void.class);
    }

    public static ArrayList<Class<?>> wrappers = new ArrayList<>();
    static
    {
        wrappers.add(Boolean.class);
        wrappers.add(Character.class);
        wrappers.add(Byte.class);
        wrappers.add(Short.class);
        wrappers.add(Integer.class);
        wrappers.add(Long.class);
        wrappers.add(Float.class);
        wrappers.add(Double.class);
        wrappers.add(Void.class);
    }

    public static HashMap<Class<?>, Class<?>> wrapperParaPrimitivo = new HashMap<>();
    static
    {
        wrapperParaPrimitivo.put(Boolean.class, boolean.class);
        wrapperParaPrimitivo.put(Character.class, char.class);
        wrapperParaPrimitivo.put(Byte.class, byte.class);
        wrapperParaPrimitivo.put(Short.class, short.class);
        wrapperParaPrimitivo.put(Integer.class, int.class);
        wrapperParaPrimitivo.put(Long.class, long.class);
        wrapperParaPrimitivo.put(Float.class, float.class);
        wrapperParaPrimitivo.put(Double.class, double.class);
        wrapperParaPrimitivo.put(Void.class, void.class);
    }

    /**
     * Checa se uma classe é embrulhadora.
     * <br>
     * (Boolean, Character, Byte, Short, Integer, Long, Float, Double, Void).
     *
     * @param classe Classe a ser analisada.
     *
     * @return {@code true} caso a classe seja alguma das listadas acima.
     * {@code false} caso contrário.
     */
    public static boolean isWrapper(Class<?> classe)
    {
        return wrappers.contains(classe);
    }

    /**
     * Checa se uma classe é primitiva.
     * <br>
     * (boolean, char, byte, short, int, long, float, double, void).
     *
     * @param classe Classe a ser analisada.
     *
     * @return {@code true} caso a classe seja alguma das listadas acima.
     * {@code false} caso contrário.
     */
    public static boolean isPrimitive(Class<?> classe)
    {
        return primitivos.contains(classe);
    }
}
