# Árvore B
Implementação da Árvore B, estrutura de dados para indexamento, em C++.

### Como usar ?

Encontre [aqui a documentação da árvore B](https://htmlpreview.github.io/?https://raw.githubusercontent.com/axell-brendow/Indexing/master/ArvoreB/doc/html/files.html).

Já existe, no repositório, um arquivo .cpp com um código de teste:

![Exemplo de código com compilação e execução](https://i.imgur.com/mNlOCqA.png)

Comandos usados: (Toda a sintaxe e os comandos usados são compatíveis com Linux e Windows/PowerShell)

```PowerShell
mkdir meu_projeto > null; rm null   # Cria o diretório do projeto
pushd meu_projeto   # Entra nele
git clone --depth=1 https://github.com/axell-brendow/Indexing.git

mv ./Indexing/ArvoreB/src ./ArvoreB   # Extrai a árvore B
mv ./Indexing/ArvoreB/test.cpp ./test.cpp   # Extrai o código de teste
rm -r ./Indexing 2> null; rm null   # Deleta o resto do repositório

g++ test.cpp -o test.exe; ./test.exe   # Compila e já executa o teste
ls

```

Código de teste:

```Cpp
#include "ArvoreB/ArvoreB.hpp"

using namespace std;

int main()
{
    ArvoreB<int, float> arvore("TesteArvore.txt", 4);

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

    // Mostra a árvore rotacionada de lado
    arvore.mostrar();
    // Mostra as páginas da árvore com chaves, dados e ponteiros
    arvore.mostrarPre();

    return 0;
}

```