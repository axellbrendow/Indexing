import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import hash.Hash;
import hash.serializaveis.SerializavelAbstract;
import hash.serializaveis.IntSerializavel;
import static hash.serializaveis.StringSerializavel.PADRAO_TAMANHO_MAXIMO_EM_BYTES;

// classpath: "hash/Hash.java;hash/serializaveis/*;."

class Student extends SerializavelAbstract
{
    int id;
    String name;

    public Student(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    @Override
    public int obterTamanhoMaximoEmBytes()
    {
        // Essa função deve calcular o quanto o id e o nome podem gastar em bytes.
        // Dentro da classe StringSerializavel, criei uma constante com o valor 300,
        // pois em casos mais simples, a sua string não gastará mais de 300 bytes.
        // Se a sua string for gastar mais de 300 bytes ou menos, fique a vontade
        // para colocar o melhor valor para você.
        // A expressão Integer.BYTES retorna o tamanho em bytes de um inteiro no Java
        // que é o que o id vai gastar.
        return PADRAO_TAMANHO_MAXIMO_EM_BYTES + Integer.BYTES;
    }

    @Override
    public byte[] obterBytes()
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(out);

        try
        {
            dataOutputStream.writeInt(id);
            dataOutputStream.writeUTF(name);
            dataOutputStream.close();
        }

        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        return out.toByteArray();
    }

    @Override
    public void lerBytes(byte[] bytes)
    {
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        DataInputStream dataInputStream = new DataInputStream(in);

        try
        {
            id = dataInputStream.readInt();
            name = dataInputStream.readUTF();
            dataInputStream.close();
        }

        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        return "{id: " + id + ", nome: " + name + "}";
    }
}

public class test
{
    public static void main(String[] args)
    {
        new File("diretorio.dir").delete();
        new File("buckets.db").delete();

        Hash<IntSerializavel, Student> hash = new Hash<IntSerializavel, Student>("diretorio.dir", "buckets.db", 2);
        Student s0 = new Student(0, "A0");
        Student s1 = new Student(1, "A1");
        Student s2 = new Student(2, "A2");

        hash.inserir(new IntSerializavel(s0.id), s0);
        hash.inserir(new IntSerializavel(s1.id), s1);
        hash.inserir(new IntSerializavel(s2.id), s2);

        System.out.println(hash);
    }
}
