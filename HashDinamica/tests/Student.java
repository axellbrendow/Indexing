import static hash.serializaveis.StringSerializavel.PADRAO_TAMANHO_MAXIMO_EM_BYTES;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import hash.Serializavel;

public class Student implements Serializavel
{
    int id;
    String name;

    public Student(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public Student() // É OBRIGATÓRIO QUE SUA CLASSE TENHA UM CONSTRUTOR SEM PARÂMETROS
    {
        this(-1, "hello");
    }

    @Override
    public int obterTamanhoMaximoEmBytes()
    {
        // Essa função deve calcular o quanto o id e o name podem gastar em bytes.
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
