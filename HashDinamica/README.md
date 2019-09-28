# HashDinamica
Implementação da Hash Dinâmica, estrutura de dados para indexamento, em Java.

## Como usar ?

Encontre [aqui a documentação da hash](https://htmlpreview.github.io/?https://raw.githubusercontent.com/axell-brendow/Indexing/master/HashDinamica/doc/overview-summary.html).


### Usando a Hash com tipos primitivos

Há algumas classes da hash prontas para uso com tipos primitivos:
hash.hashs.HashIntInt
hash.hashs.HashIntLong
hash.hashs.HashStringInt

Nos dois exemplos com tipos primitivos, usarei string como chave e inteiro como dado e o código abaixo:

Arquivo Test.java
```Java
import java.io.File;

import hash.hashs.HashStringInt;

public class HashTest
{
    public static void main(String[] args)
    {
        new File("diretorio.dir").delete();
        new File("buckets.db").delete();

        HashStringInt hash = new HashStringInt("diretorio.dir", "buckets.db", 2);

        hash.inserir("a", 1);
        hash.inserir("b", 2);
        hash.inserir("c", 3);

        System.out.println(hash);
    }
}

```

### 1ª forma de usar, compilar diretamente o código fonte:

Os passos estão descritos na imagem abaixo:

![Exemplo de código com compilação e execução do código fonte do repositório](https://i.imgur.com/2YaZVRo.jpg)

Todos os comandos usados:

```PowerShell
ls  # Dentro da pasta Indexing/HashDinamica/tests já existem os arquivos de teste

javac Test.java -classpath "./hash/hashs/*;."  # Compile passando como classpath o caminho das classes que você estiver usando e o caminho do seu .java de teste. No meu caso, estou usando a classe hash.hashs.HashStringInt no código, então o classpath para ela pode ser ./hash/hashs/* que cobre todas as classes dentro dessa pasta. Já o caminho para o meu Test.java é a própria pasta onde estou, daí coloquei o ponto (.). Caso você esteja num sistema unix, troque o ; por : no classpath.

java Test -classpath "./hash/hashs/*;."  # Execute com o mesmo classpath

```


### 2ª forma de usar, compilar usando o arquivo hash.jar:

Primeiro, [baixe o arquivo hash.jar](https://github.com/axell-brendow/Indexing/raw/master/HashDinamica/hash.jar).

A versão do Java usada para criar o arquivo hash.jar é menor ou igual a 10. Caso você tenha problemas ao usá-lo, recomendo compilar diretamente o código fonte como foi mostrado na 1ª forma.

Os próximos passos estão descritos nas imagens a abaixo:

![Exemplo de código com compilação e execução](https://i.imgur.com/07BIMQ3.jpg)

Todos os comandos usados:

```PowerShell
ls  # Dentro da pasta Indexing/HashDinamica/tests já existem os arquivos de teste

javac -classpath hash.jar Test.java  # Compile passando como classpath o caminho de hash.jar e o do seu .java de teste. O caminho para o meu Test.java é a própria pasta onde estou, daí coloquei o ponto (.). Caso você esteja num sistema unix, troque o ; por : no classpath.

java -classpath hash.jar Test  # Execute com o mesmo classpath

```

Caso você não queira deixar o hash.jar na mesma pasta que a sua classe java, você pode seguir estes passos:

![Compilação e execução com o .jar na pasta lib](https://i.imgur.com/4dT4fqX.jpg)

Todos os comandos usados:

```PowerShell
ls  # Dentro da pasta Indexing/HashDinamica/tests já existem os arquivos de teste
ls lib  # Exemplo com o hash.jar dentro da pasta lib

javac -classpath "./lib/*;." Test.java  # Compile passando como classpath o caminho de hash.jar e o do seu .java de teste. O caminho para o meu Test.java é a própria pasta onde estou, daí coloquei o ponto (.). Caso você esteja num sistema unix, troque o ; por : no classpath.

java -classpath "./lib/*;." Test  # Execute com o mesmo classpath

```


### Usando a Hash com tipos personalizados

Antes de tudo, a Hash trabalha com chaves e dados [serializáveis](https://www.devmedia.com.br/serializacao-de-objetos-em-java/23413), ou seja, a chave e o dado dos registros devem ser capazes de gerar seus próprios bytes e de informar qual é a quantidade máxima de bytes que podem gastar.

Dessa forma, para cada tipo primitivo, float, int, long e para as strings, precisei criar classes que os tornam objetos serializáveis. Essas classes se encontram no pacote hash.serializaveis e são elas:

- FloatSerializavel
- IntSerializavel
- LongSerializavel
- StringSerializavel

Analogamente, para usar a Hash com uma classe sua, é preciso que a classe implemente a interface hash.Serializavel e seus 4 métodos. Isso tornará sua classe serializável:

- O primeiro deles, obterTamanhoMaximoEmBytes(), é um método onde você dirá qual é o tamanho máximo que a sua classe pode gastar em bytes juntando todos os campos internos que você for salvar em arquivo.
- O segundo é o obterBytes() onde você deve gerar um byte[] com os bytes da sua entidade.
- O terceiro é o lerBytes(byte[]) onde você receberá um byte[] e você deve lê-lo decodificando-o e atribuindo os campos internos da sua entidade.
- O quarto é o toString() onde você deve gerar um representação textual do seu objeto. Esse método é necessário para comparar se dois objetos da sua classe são iguais. Se dois objetos gerarem a mesma string, eles serão iguais.

Por algumas limitações do Java e por necessidades da Hash, a classe do seu objeto deve ser pública. Isso obriga também que ela tenha um arquivo .java só para ela. Irei dar um exemplo com a classe Student abaixo:

Código de teste:

Arquivo Student.java
```Java
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

    public Student()
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
        return "{id: " + id + ", name: " + name + "}";
    }
}

```

Arquivo principal TestPersonalizado.java
```Java
import java.io.File;

import hash.Hash;
import hash.serializaveis.IntSerializavel;

public class testPersonalizado
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
                // Função hash das chaves, recebo uma cache e tenho que retornar um
                // valor inteiro para ser o código hash dessa chave. Nesse caso, estou
                // colocando o código hash de uma chave IntSerializavel como o próprio
                // valor inteiro.
                (chave) -> chave.valor);

        Student s0 = new Student(0, "A0");
        Student s1 = new Student(1, "A1");
        Student s2 = new Student(2, "A2");

        hash.inserir(new IntSerializavel(s0.id), s0);
        hash.inserir(new IntSerializavel(s1.id), s1);
        hash.inserir(new IntSerializavel(s2.id), s2);

        System.out.println(hash);
    }
}

```

Exemplo rodando:

![Exemplo de código com compilação e execução com classe personalizada](https://i.imgur.com/em7HGhP.jpg)

Todos os comandos usados:

```PowerShell
ls  # Dentro da pasta Indexing/HashDinamica/tests já existem os arquivos de teste

javac TestPersonalizado.java -classpath "./hash/*;hash/serializaveis/*;."  # Compile passando como classpath o caminho das classes que você estiver usando e o caminho do seu .java de teste. No meu caso, estou usando a classe hash.hashs.HashStringInt no código, então o classpath para ela pode ser ./hash/hashs/* que cobre todas as classes dentro dessa pasta. Já o caminho para o meu TestPersonalizado.java é a própria pasta onde estou, daí coloquei o ponto (.). Caso você esteja num sistema unix, troque o ; por : no classpath.

java TestPersonalizado -classpath "./hash/*;hash/serializaveis/*;."  # Execute com o mesmo classpath

```
