import java.io.File;

import hash.Hash;
import hash.serializaveis.IntSerializavel;

// classpath: "hash/Hash.java;hash/serializaveis/*;."

public class testSourceEntidade
{
    public static void main(String[] args)
    {
        new File("diretorio.dir").delete();
        new File("buckets.db").delete();

        Hash<IntSerializavel, Student> hash = new Hash<>(
                "diretorio.dir", "buckets.db",
                2, // Número de registros por bucket
                IntSerializavel.class, // Classe da chave dos registros
                Student.class, // Classe do dado dos registros
                (chave) -> chave.valor); // Função hash das chaves

        Student s0 = new Student(0, "A0");
        Student s1 = new Student(1, "A1");
        Student s2 = new Student(2, "A2");

        hash.inserir(new IntSerializavel(s0.id), s0);
        hash.inserir(new IntSerializavel(s1.id), s1);
        hash.inserir(new IntSerializavel(s2.id), s2);

        System.out.println(hash);
    }
}
