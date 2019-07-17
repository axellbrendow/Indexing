/**
 * @file ArvoreB.hpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo para testes
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#pragma once

#include "templates/tipos.hpp"
#include "PaginaB.hpp"

#include <iostream>
#include <fstream>

using namespace std;

template<typename TIPO_DAS_CHAVES, typename TIPO_DOS_DADOS>
class ArvoreB
{
public:
	// ------------------------- Campos

	const string nomeDoArquivo;
	const int maximoDeBytesParaAChave;
	const int maximoDeBytesParaODado;
	const int numeroDeChavesPorPagina;
	const int ordemDaArvore;

	PaginaB paginaPai;
	PaginaB paginaFilha;

	// ------------------------- Construtores e destrutores

	ArvoreB(string nomeDoArquivo,
		int maximoDeBytesParaAChave,
		int maximoDeBytesParaODado,
		int ordemDaArvore);

	~ArvoreB();

	// ------------------------- M�todos


private:
	// ------------------------- Campos

	fstream arquivo;
};

ArvoreB::ArvoreB(
	string nomeDoArquivo,
	int maximoDeBytesParaAChave,
	int maximoDeBytesParaODado,
	int ordemDaArvore) :
		
		nomeDoArquivo(nomeDoArquivo),
		arquivo( fstream(nomeDoArquivo, ios::binary | ios::in | ios::out) ),

		pagina(maximoDeBytesParaAChave,
			maximoDeBytesParaODado,
			ordemDaArvore) { }

ArvoreB::~ArvoreB()
{
}