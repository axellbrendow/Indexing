# HashDinamica
Implementação da Hash Dinâmica, estrutura de dados para indexamento, em Java.

## Como usar ?

Encontre [aqui a documentação da hash](https://htmlpreview.github.io/?https://raw.githubusercontent.com/axell-brendow/Indexing/master/HashDinamica/doc/overview-summary.html).


### Usando a Hash com tipos primitivos

Há algumas classes da hash prontas para uso com tipos primitivos:
hash.hashs.HashIntInt
hash.hashs.HashIntLong
hash.hashs.HashStringInt

No exemplo que irei dar, usarei string como chave e inteiro como dado:

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

### 1ª forma de usar, usando diretamente o código fonte:

Os passos estão descritos na imagem abaixo:

![Exemplo de código com compilação e execução do código fonte do repositório](https://i.imgur.com/wOOGINf.png)

Todos os comandos usados (Windows/PowerShell):

```PowerShell
echo 'import java.io.File;

import hash.hashs.HashStringInt;

public class test
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
' | Out-File -Encoding default test.java  # Envia um código de teste para o arquivo test.java

ls | select Name   # Coloque sua classe .java e a pasta Indexing/HashDinamica/src/hash do meu repositório juntas como abaixo:
ls .\hash\hashs  # Apenas mostra as classes .java dentro da pasta ./hash/hashs

javac -classpath "./hash/hashs/*;." test.java   <# Compile passando como classpath o caminho da pasta hash e o caminho do seu .java.

No meu caso, o .java está no próprio diretório onde estou, por isso usei o . (ponto). Caso você esteja num sistema unix, troque o ; (ponto e vírgula) por : (dois pontos) no classpath. #>

java -classpath "./hash/hashs/*;." test   # Execute sua classe com o mesmo classpath de compilação
```


### 2ª forma de usar, usando o arquivo hash.jar:

Primeiro, [baixe o arquivo hash.jar](https://github.com/axell-brendow/Indexing/raw/master/HashDinamica/hash.jar).

Os próximos passos estão descritos nas imagens a abaixo:

![Exemplo de código com compilação e execução](https://i.imgur.com/VcqsxoW.png)

Caso você não queira deixar o hash.jar na mesma pasta que a sua classe java, você pode seguir estes passos que são uma continuação da primeira imagem:

![Compilação e execução com o .jar na pasta lib](https://i.imgur.com/WgV4c0J.png)

Todos os comandos usados (Windows/PowerShell):

```PowerShell
echo 'import java.io.File;

import hash.hashs.HashStringInt;

public class test
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
' | Out-File -Encoding default test.java  # Envia um código de teste para o arquivo test.java

ls | select Name   # Coloque sua classe .java e o arquivo hash.jar na mesma pasta como abaixo:
javac -classpath hash.jar test.java   # Compile passando como classpath o caminho de hash.jar

java -classpath hash.jar test   # Execute sua classe com o mesmo classpath de compilação
ls | select Name   # Estado do diretório depois da compilação e execução
mkdir lib > $null   # Cria a pasta lib

mv .\hash.jar .\lib\hash.jar   # Move hash.jar para ela

tree /f   # Mostra a organização dos arquivos
javac -classpath ".\lib\*;." test.java   <# Compile passando como classpath o caminho de hash.jar e o caminho do seu .java.

No meu caso, o .java está no próprio diretório onde estou, por isso usei o . (ponto). Caso você esteja num sistema unix, troque o ; (ponto e vírgula) por : (dois pontos) no classpath. #>

java -classpath ".\lib\*;." test   # Execute sua classe com o mesmo classpath de compilação
```


### Usando a Hash com tipos personalizados

Antes de tudo, a Hash trabalha com chaves e dados [serializáveis](https://www.devmedia.com.br/serializacao-de-objetos-em-java/23413), ou seja, a chave e o dado dos registros devem ser capazes de gerar seus próprios bytes e de informar qual é a quantidade máxima de bytes que podem gastar.

Dessa forma, para cada tipo primitivo, float, int, long e para as strings, precisei criar classes que os tornam objetos serializáveis. Essas classes se encontram no pacote hash.serializaveis e para construi-las basta passar o valor do primitivo em seus construtores.

Analogamente, para usar a Hash com uma classe sua, é preciso que ela herde a classe hash.serializaveis.SerializavelAbstract e implemente 4 métodos que tornarão sua classe serializável:

- O primeiro deles, obterTamanhoMaximoEmBytes(), é um método os você dirá qual é o tamanho máximo que a sua classe pode gastar em bytes juntando todos os campos internos que você for salvar em arquivo.
- O segundo é o obterBytes() onde você deve gerar um byte[] com os bytes da sua entidade.
- O terceiro é o lerBytes(byte[]) onde você receberá um byte[] e você deve lê-lo decodificando-o e atribuindo os campos internos da sua entidade.
- O quarto é o toString() onde você deve gerar um representação textual do seu objeto. Esse método é necessário para comparar se dois objetos da sua classe são iguais. Se dois objetos gerarem a mesma string, eles serão iguais.

Por algumas limitações do Java e pela construção da Hash, a classe do seu objeto deve ser pública. Isso obriga também que ela tenha um arquivo .java só para ela. Irei dar um exemplo com a classe Student abaixo:

Código de teste:

Arquivo Student.java
```Java
import static hash.serializaveis.StringSerializavel.PADRAO_TAMANHO_MAXIMO_EM_BYTES;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import hash.serializaveis.SerializavelAbstract;

public class Student extends SerializavelAbstract
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
        return "{id: " + id + ", nome: " + name + "}";
    }
}

```

Arquivo principal testSourceEntidade.java
```Java
import java.io.File;

import hash.Hash;
import hash.serializaveis.IntSerializavel;

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

```

#### Rodando o código:

Todos os comandos usados (Windows/PowerShell):

```PowerShell
ls | select Name   # Coloque sua classe .java e o arquivo hash.jar na mesma pasta como abaixo:

javac -classpath "hash/Hash.java;hash/serializaveis/*;." testSourceEntidade.java   <# Compile passando como classpath o caminho de hash.jar e o caminho do seu .java.

No meu caso, o .java está no próprio diretório onde estou, por isso usei o . (ponto). Caso você esteja num sistema unix, troque o ; (ponto e vírgula) por : (dois pontos) no classpath. #>

java -classpath "hash/Hash.java;hash/serializaveis/*;." testSourceEntidade   # Execute sua classe com o mesmo classpath de compilação
```
