# HashDinamica
Implementação da Árvore B, estrutura de dados para indexamento, em C++.

### Como usar ?

Encontre [aqui a documentação da árvore B](https://htmlpreview.github.io/?https://raw.githubusercontent.com/axell-brendow/Indexing/master/ArvoreBMais/doc/html/files.html).

Já existe, no repositório, um arquivo .cpp com um código de teste:

![Exemplo de código com compilação e execução](https://i.imgur.com/6mgqFkM.png)

Comandos usados: (Estes comandos são compatíveis com Unix e Windows/PowerShell)

```PowerShell
git clone --depth=1 https://github.com/axell-brendow/Indexing.git

mv ./Indexing/ArvoreBMais/src ./ArvoreBMais
mv ./Indexing/ArvoreBMais/test.cpp ./test.cpp

rm -r ./Indexing 2>null; rm null

g++ test.cpp -o test.exe
./test.exe

ls

```

Código de teste:

```Cpp
#include "ArvoreBMais/ArvoreBMais.hpp"

#include <iostream>
#include <cstring>

using namespace std;

int main()
{
    string fileName("TesteArvore.txt");
    
    ArvoreBMais<int, float> arvore(fileName, 4);

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