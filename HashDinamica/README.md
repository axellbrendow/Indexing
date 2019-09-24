# HashDinamica
Implementação da Hash Dinâmica, estrutura de dados para indexamento, em Java.

## Como usar ?

Encontre [aqui a documentação da hash](https://htmlpreview.github.io/?https://github.com/axell-brendow/Indexing/blob/master/HashDinamica/doc/index.html).


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

Para usar a Hash com uma classe sua, é preciso que ela herde a classe hash.serializaveis.SerializavelAbstract e implemente 4 métodos:

- O primeiro deles, obterTamanhoMaximoEmBytes(), é um método os você dirá qual é o tamanho máximo que a sua classe pode gastar em bytes juntando todos os campos internos.
- O segundo é o obterBytes() onde você deve gerar um byte[] com os bytes da sua entidade.
- O terceiro é o lerBytes(byte[]) onde você receberá um byte[] e você deve lê-lo e ir atribuindo os campos internos da sua entidade.
- O quarto é o toString() onde você deve gerar um representação textual do seu objeto. Esse método será utilizado para comparar se duas chaves ou dois dados são iguais.

Código de teste:

```Java
import java.io.File;
import hash.Hash;
import hash.serializaveis.SerializavelAbstract;

class Student extends SerializavelAbstract
{
    String nome;
    int idade;

    public Student(String nome, int idade)
    {
        this.nome = nome;
        this.idade = idade;
    }

    public int obterTamanhoMaximoEmBytes()
    {
        return 
    }
}

public class HashTest
{
    public static void main(String[] args)
    {
        new File("diretorio.dir").delete();
        new File("buckets.db").delete();

        Hash<Student> hash = new Hash<>("diretorio.dir", "buckets.db", 2);

        hash.inserir("a", 1);
        hash.inserir("b", 2);
        hash.inserir("c", 3);

        System.out.println(hash);
    }
}

```
