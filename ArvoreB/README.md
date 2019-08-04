# HashDinamica
Implementação da Árvore B, estrutura de dados para indexamento, em C++.

### Como usar ?

Encontre [aqui a documentação da árvore](http://htmlpreview.github.io/?https://github.com/axell-brendow/Indexing/blob/master/ArvoreB/doc/html/index.html).

Primeiro, [baixe o arquivo arvoreb.dll](https://github.com/axell-brendow/Indexing/raw/master/ArvoreB/build/arvoreb.dll).

Os próximos passos estão descritos nas imagens a abaixo:

![Exemplo de código com compilação e execução](http://i66.tinypic.com/10fp2k8.jpg)

Caso você não queira deixar o hash.jar na mesma pasta que a sua classe java, você pode seguir estes passos que são uma continuação da primeira imagem:

![Compilação e execução com o .jar na pasta lib](http://i67.tinypic.com/2v1ofgj.jpg)

Todos os comandos usados:

```PowerShell
pushd ~   # vai para a pasta do seu usuário

git clone https://github.com/axell-brendow/Indexing.git   # baixa o repositório

mv ./Indexing/ArvoreB/build ./build   # extrai o código da árvore B do repositório

rm -r ./Indexing   # deleta o repositório

pushd ./build   # entra na pasta da árvore B

cat ./test.cpp   # mostra o código de teste

g++ test.cpp -o test.exe   # compila o código

./test.exe   # executa

```

Código de teste:

```Java
import java.io.File;

import hash.hashs.HashDinamicaStringInt;

public class HashTest
{
    public static void main(String[] args)
    {
        try
        {
            new File("diretorio.dir").delete();
            new File("buckets.db").delete();

            HashDinamicaStringInt hash = new HashDinamicaStringInt("diretorio.dir", "buckets.db", 2);

            hash.inserir("a", 1);
            hash.inserir("b", 2);
            hash.inserir("c", 3);
    
            System.out.println(hash);
        }
        
        catch (SecurityException | NoSuchMethodException e)
        {
            e.printStackTrace();
        }
    }
}

```