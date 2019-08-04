/**
 * @file mytest.cpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo para testes
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

//#pragma once

#include "templates/serializavel.hpp"
#include "ArvoreB.hpp"

#include <fstream>
#include <iostream>
#include <cstring>

using namespace std;

/**
 * @brief Classe para estudantes.
 */
class Student : public Serializavel
{
public:
    string nome;
    int idade;
    
    Student(const char* nome, int idade) : nome(nome), idade(idade) {}
    Student(string nome, int idade) : nome(nome), idade(idade) {}
    Student() : Student("Desconhecido", 0) {}

    /** Recomendo a leitura do link abaixo para entender o porquê de eu usar "using" aqui.
     * 
     * https://stackoverflow.com/questions/14212190/c-issue-with-function-overloading-in-an-inherited-class
     */
    using Serializavel::gerarDataOutputStream;

    virtual int obterTamanhoMaximoEmBytes() override
    {
        return sizeof(str_size_type) + constantes::tamanhoMaximoStrings + sizeof(int);
    }

    virtual DataOutputStream& gerarDataOutputStream(DataOutputStream& out) override
    {
        return out << nome << idade;
    }

    virtual void lerBytes(DataInputStream& input) override
    {
        input >> nome >> idade;
    }

    Student &imprimir(ostream &ostream = cout)
    {
        ostream << "{ nome: " << nome << ", idade: " << idade << " }";

        return *this;
    }

    DataOutputStream imprimirVetor()
    {
        auto out = gerarDataOutputStream();

        cout << out;

        return out;
    }
};

ostream& operator<<(ostream& ostream, Student& student)
{
    student.imprimir(ostream);

    return ostream;
}

int main()
{
    string fileName("TesteArvore.txt");
    
    ArvoreB<int, Student> arvore(fileName, 4);

    arvore.inserir(1000, Student("1000", 18));
    arvore.inserir(2000, Student("2000", 18));
    arvore.inserir(3000, Student("3000", 18));
    arvore.inserir(200, Student("200", 18));
    arvore.inserir(400, Student("400", 18));
    arvore.inserir(1500, Student("1500", 18));
    arvore.inserir(600, Student("600", 18));
    arvore.inserir(50, Student("50", 18));
    arvore.inserir(12, Student("12", 18));
    arvore.inserir(4, Student("4", 18));

    // arvore.excluir(12);
    // arvore.excluir(4);
    // arvore.excluir(600);
    // arvore.excluir(400);
    // arvore.excluir(50);
    // arvore.excluir(200);
    // arvore.excluir(2000);
    // arvore.excluir(1500);

    arvore.mostrar();

    auto dados = arvore.listarDadosComAChaveEntre(10, 250);
    cout << endl;

    for (auto &&i : dados)
    {
        cout << i << endl;
    }
    
    Student &&me = arvore.pesquisar(4);
    cout << endl << me << endl;

    return 0;
}