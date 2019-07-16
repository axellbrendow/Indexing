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

        int obterTamanhoMaximoEmBytes()
        {
            return sizeof(str_size_type) + padraoTamanhoMaximoStrings + sizeof(int);
        }

        DataOutputStream gerarDataOutputStream(DataOutputStream out)
        {
            return out << nome << idade;
        }

        void lerBytes(DataInputStream input)
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
    
	ofstream ofs("fifthgrade.ros", ios::binary);
    ofs << one;
    ofs.close();
    
    Student two;

    two.imprimir();

	ifstream ifs("fifthgrade.ros", ios::binary);
    ifs >> two;
    ifs.close();

    two.imprimir();

	return 0;
}