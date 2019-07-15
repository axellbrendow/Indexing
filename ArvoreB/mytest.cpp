/**
 * @file mytest.cpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo para testes
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#pragma once

#include "tipos.hpp"
#include "streams/DataInputStream.cpp"
#include "streams/DataOutputStream.cpp"

#include <fstream>
#include <iostream>
#include <cstring>

using namespace std;

/**
 * @brief Classe para estudantes.
 */

class Student
{
    public:
        string nome;
        int idade;
        
        Student(string nome, int idade) : nome(nome), idade(idade) {}

        vetor_de_bytes gerarBytes()
        {
            DataOutputStream out(15);

            return (out << nome << idade).obterVetor();
        }

        Student &imprimir()
        {
            cout << "nome = " << nome << ", idade = " << idade << endl;

            return *this;
        }

        vetor_de_bytes imprimirVetor()
        {
            auto vetor = gerarBytes();

            debugApenasPorIterador(vetor.begin(), vetor.end());

            return vetor;
        }
};

int main()
{
	Student one("axell", 19);
    one.imprimir();
    auto vetor = one.imprimirVetor();
    
	ofstream ofs("fifthgrade.ros", ios::binary);
    
	ofs.write(reinterpret_cast<char *>(vetor.begin().base()), vetor.size());

    ofs.close();

	return 0;
}