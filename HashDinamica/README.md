# HashDinamica
Implementação da Hash Extensível (Dinâmica), estrutura de dados para indexamento, em Java.

## Como usar ?

Encontre [aqui a documentação da hash](https://htmlpreview.github.io/?https://raw.githubusercontent.com/axell-brendow/Indexing/master/HashDinamica/doc/overview-summary.html).

### Usando a Hash com tipos primitivos

A hash suporta fazer o indexamento dos seguintes tipos primitivos:

- Boolean
- Character
- Byte
- Short
- Integer
- Float
- Double
- String (não é um tipo primitivo mas é permitido)

Nos dois exemplos com tipos primitivos, usarei String como chave e Integer como dado:

Arquivo Test.java
```Java
import java.io.File;

import hash.Hash;

public class Test
{
    public static void main(String[] args)
    {
        new File("diretorio.dir").delete();
        new File("buckets.db").delete();

        Hash<String, Integer> hash = new Hash<>(
            "diretorio.dir",
            "buckets.db",
            2,
            String.class,
            Integer.class);

        hash.inserir("a", 1);
        hash.inserir("b", 2);
        hash.inserir("c", 3);

        System.out.println(hash);
    }
}

```

#### 1ª forma de usar, compilar diretamente o código fonte:

Caso você use uma IDE, basta copiar a pasta Indexing/HashDinamica/src/hash para dentro do pacote do seu projeto. (Geralmente Ctrl + C e Ctrl + V funciona :D). Pode ser necessário dar um clean ou rebuild em seu projeto.

Caso contrário, os passos estão descritos na imagem abaixo:

![Exemplo de código com compilação e execução do código fonte do repositório](https://i.imgur.com/kecckcB.png)

Todos os comandos usados:

```PowerShell
ls  # Dentro da pasta Indexing/HashDinamica/src já existem os arquivos de teste

javac -classpath . Test.java  # Compile passando como classpath o caminho para a pasta que contém a pasta hash. No meu caso, a pasta onde estou contém a pasta hash, por isso o ponto (.).

java -classpath . Test  # Execute com o mesmo classpath

```


#### 2ª forma de usar, compilar usando o arquivo hash.jar:

Primeiro, [baixe o arquivo hash.jar](https://github.com/axell-brendow/Indexing/raw/master/HashDinamica/hash.jar).

O código fonte da Hash exige Java 8 no mínimo para compilar. Porém o arquivo hash.jar pode ter sido compilado em versões mais novas do Java como 9, 10 ou 11.

Caso você use uma IDE, pesquise sobre "Add external jar <SUA_IDE>" para incluir a hash em seu projeto.

Caso contrário, os próximos passos estão descritos nas imagens a abaixo:

![Exemplo de código com compilação e execução](https://i.imgur.com/odqlORg.png)

Todos os comandos usados:

```PowerShell
ls  # Dentro da pasta Indexing/HashDinamica/src já existem os arquivos de teste

javac -classpath hash.jar Test.java  # Compile passando como classpath o caminho para o hash.jar.

java -classpath hash.jar Test  # Execute com o mesmo classpath

```

Caso você não queira deixar o hash.jar na mesma pasta que a sua classe java, você pode seguir estes passos:

![Compilação e execução com o .jar na pasta lib](https://i.imgur.com/F840adZ.png)

Todos os comandos usados:

```PowerShell
ls  # Dentro da pasta Indexing/HashDinamica/src já existem os arquivos de teste
ls lib  # Exemplo com o hash.jar dentro da pasta lib

javac -classpath lib/hash.jar Test.java  # Compile passando como classpath o caminho para o hash.jar.

java -classpath lib/hash.jar Test  # Execute com o mesmo classpath

```


### Usando a Hash com tipos personalizados

Antes de tudo, a hash.Hash trabalha com chaves e dados [serializáveis](https://www.devmedia.com.br/serializacao-de-objetos-em-java/23413), ou seja, a chave e o dado dos registros devem ser capazes de gerar seus próprios bytes e de informar qual é a quantidade máxima de bytes que podem gastar.

Para usar a Hash com uma classe sua, é preciso que sua classe implemente a interface hash.Serializavel e seus 4 métodos:

- O primeiro deles, obterTamanhoMaximoEmBytes(), é um método onde você dirá qual é o tamanho máximo que a sua classe pode gastar em bytes juntando todos os campos internos que você for salvar em arquivo.
- O segundo é o obterBytes() onde você deve gerar um byte[] com os bytes da sua entidade.
- O terceiro é o lerBytes(byte[]) onde você receberá um byte[] e você deve lê-lo decodificando-o e atribuindo os campos internos da sua entidade.
- O quarto é o toString() onde você deve gerar um representação textual do seu objeto. Esse método é necessário para comparar se dois objetos da sua classe são iguais. Se dois objetos gerarem a mesma string, eles serão iguais.

Por algumas limitações do Java e por necessidades da Hash, **a classe do seu objeto deve ter um construtor sem parâmetros e deve ser pública**. Isso obriga também que ela tenha um arquivo .java só para ela. Irei dar um exemplo com a classe Student abaixo:

Código de teste:

Arquivo Student.java
```Java
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import hash.RegistroDoIndice;
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

    /**
     * <b>IMPORTANTE:</b> É OBRIGATÓRIO QUE SUA CLASSE TENHA UM CONSTRUTOR SEM
     * PARÂMETROS.
     */
    public Student()
    {
        this(-1, "hello");
    }

    /**
     Essa função deve calcular o quanto o id e o name podem gastar em bytes.
     Dentro da classe {@link RegistroDoIndice}, criei uma constante para
     tamanho em bytes de uma string, pois em casos mais simples, a sua
     string não gastará mais de 300 bytes. Se a sua string for gastar mais
     de 300 bytes ou menos, fique a vontade para colocar o melhor valor para
     você. A expressão Integer.BYTES retorna o tamanho em bytes de um inteiro
     no Java que é o que o id vai gastar.
     */
    @Override
    public int obterTamanhoMaximoEmBytes()
    {
        return RegistroDoIndice.TAMANHO_MAXIMO_EM_BYTES_STRINGS + Integer.BYTES;
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

```

Arquivo principal TestPersonalizado.java
```Java
import java.io.File;

import hash.Hash;

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

        hash.inserir(s0.id, s0);
        hash.inserir(s1.id, s1);
        hash.inserir(s2.id, s2);

        System.out.println(hash);
    }
}

```

Exemplo rodando:

![Exemplo de código com compilação e execução com classe personalizada](https://i.imgur.com/7gaPqLU.png)

Todos os comandos usados:

```PowerShell
ls  # Dentro da pasta Indexing/HashDinamica/src já existem os arquivos de teste

javac -classpath . TestPersonalizado.java  # Compile passando como classpath o caminho para a pasta que contém a pasta hash. No meu caso, a pasta onde estou contém a pasta hash, por isso o ponto (.).

java -classpath . TestPersonalizado  # Execute com o mesmo classpath

```
