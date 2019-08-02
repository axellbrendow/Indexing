/**
 * @file mytest.cpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo para testes
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

//#pragma once

#include "templates/serializavel.hpp"
#include "streams/DataInputStream.hpp"
#include "streams/DataOutputStream.hpp"
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
	Student one("voce", 18);
    
    ArvoreB<int, Student> arvore(fileName, 4);

    arvore.inserir(1000, one);
    arvore.inserir(2000, one);
    arvore.inserir(3000, one);
    arvore.inserir(200, one);
    arvore.inserir(400, one);
    arvore.inserir(1500, one);
    arvore.inserir(600, one);
    arvore.inserir(50, one);
    arvore.inserir(12, one);
    one.nome = "x";
    arvore.inserir(4, one);
    arvore.excluir(12);
    arvore.excluir(4);
    arvore.excluir(600);
    arvore.excluir(400);

    arvore.mostrarCentral();

    Student &&me = arvore.pesquisar(4);
    cout << me << endl;

    return 0;
}