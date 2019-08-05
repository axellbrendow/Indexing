# HashDinamica
Implementação da Árvore B, estrutura de dados para indexamento, em C++.

### Como usar ?

Encontre [aqui a documentação da árvore](http://htmlpreview.github.io/?https://github.com/axell-brendow/Indexing/blob/master/ArvoreB/doc/html/index.html).

Os próximos passos estão descritos nas imagens a abaixo:

![Exemplo de código com compilação e execução](http://i66.tinypic.com/10fp2k8.jpg)

Todos os comandos usados:

```PowerShell
pushd ~   # vai para a pasta do seu usuário

git clone --depth=1 https://github.com/axell-brendow/Indexing.git   # baixa os arquivos

mv ./Indexing/ArvoreB/src ./ArvoreB   # extrai o código da árvore B do repositório

pushd ./ArvoreB   # entra na pasta da árvore B

cat ./test.cpp   # mostra o código de teste

g++ test.cpp -o test.exe   # compila o código

./test.exe   # executa

```

Código de teste:

```Cpp
#include "ArvoreB.hpp"

#include <iostream>
#include <cstring>

using namespace std;

int main()
{
    string fileName("TesteArvore.txt");
    
    ArvoreB<int, float> arvore(fileName, 4);

    arvore.inserir(1000 , 1000.5);
    arvore.inserir(2000 , 2000.5);
    arvore.inserir(3000 , 3000.5);
    arvore.inserir(200  , 200.5 );
    arvore.inserir(400  , 400.5 );
    arvore.inserir(1500 , 1500.5);
    arvore.inserir(600  , 600.5 );
    arvore.inserir(50   , 50.5  );
    arvore.inserir(12   , 12.5  );
    arvore.inserir(4    , 4.5   );

    arvore.mostrar();

    return 0;
}

```