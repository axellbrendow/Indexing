import java.io.File;
import java.lang.reflect.Field;

import hash.Hash;
import hash.Serialize;

public class TestPersonalizado
{
    public static void main(String[] args)
    {
        new File("diretorio.dir").delete();
        new File("buckets.db").delete();

        Hash<Integer, Student> hash = new Hash<>(
                "diretorio.dir",
                "buckets.db",
                2, // Número de registros por bucket
                Integer.class,
                Student.class);

        Student s0 = new Student(0, "S0");
        Student s1 = new Student(1, "S1");
        Student s2 = new Student(2, "S2");

        System.out.println();

        for (Field field : Student.class.getDeclaredFields())
        {
            var annotation = field.getAnnotation(Serialize.class);
            System.out.println(
                    "FIELD: " + field.getName()
                            + ", TYPE: " + field.getType()
                            + ", VALUE: " + annotation.numMaxBytes()
                            + "\n");
        }

        hash.inserir(s0.id, s0);
        hash.inserir(s1.id, s1);
        hash.inserir(s2.id, s2);

        System.out.println(hash);
    }
}
