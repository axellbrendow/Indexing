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
        return sizeof(str_size_type) + padraoTamanhoMaximoStrings + sizeof(int);
    }

    virtual DataOutputStream gerarDataOutputStream(DataOutputStream out) override
    {
        return out << nome << idade;
    }

    virtual void lerBytes(DataInputStream &input) override
    {
        input >> nome >> idade;
    }

    Student &imprimir()
    {
        cout << "nome = " << nome << ", idade = " << idade << endl;

        return *this;
    }

    DataOutputStream imprimirVetor()
    {
        auto out = gerarDataOutputStream();

        cout << out;

        return out;
    }
};

int main()
{
	Student one("axell", 19);
    one.imprimir();
    
    // Apenas para criar ou zerar o arquivo
    fstream("fifthgrade.ros", fstream::out | fstream::trunc).close();
	fstream stream("fifthgrade.ros", fstream::binary | fstream::in | fstream::out);

    // https://programmingdimension.wordpress.com/2015/04/30/seekg-tellg-seekp-tellp/
    stream.seekp(0);
    stream << one;

    one.nome = "batista";
    one.idade = 20;
    stream << one;
    
    Student two;
    two.imprimir();
    
    stream.seekg(0);
    stream >> two;
    stream >> two;

    two.imprimir();

	return 0;
}