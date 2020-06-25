import java.io.File;
import java.lang.reflect.Field;
import java.util.function.BiConsumer;

import hash.Hash;
import hash.Serialize;

public class TestPersonalizado
{
    private static void percorrerAnotacoesSerialize(Class<?> classe,
            BiConsumer<Field, Serialize> funcao)
    {
        for (Field campo : classe.getDeclaredFields())
        {
            Serialize anotacao = campo.getAnnotation(Serialize.class);

            if (anotacao != null)
            {
                funcao.accept(campo, anotacao);
            }
        }
    }

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

        percorrerAnotacoesSerialize(Student.class, (campo, anotacao) ->
        {
            System.out.println(
                    "FIELD: " + campo.getName()
                            + ", TYPE: " + campo.getType()
                            + ", VALUE: " + anotacao.numMaxBytes()
                            + "\n");
        });

        hash.inserir(s0.id, s0);
        hash.inserir(s1.id, s1);
        hash.inserir(s2.id, s2);

        System.out.println(hash);
    }
}
