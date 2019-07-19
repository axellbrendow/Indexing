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
	// ------------------------- Typedefs

	/**
	 * @brief Padroniza o tipo da página da árvore. Typedefs dentro de classes ou structs
	 * são considerados como boa prática em C++.
	 */
	typedef PaginaB<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> Pagina

	// ------------------------- Campos

	const string nomeDoArquivo;
	const fstream arquivo;

	const int maximoDeBytesParaAChave;
	const int maximoDeBytesParaODado;
	const int numeroDeChavesPorPagina;
	const int ordemDaArvore;

	const Pagina paginaPai;
	const Pagina paginaFilha;
	const Pagina paginaIrma;

	// ------------------------- Construtores e destrutores

	ArvoreB(string nomeDoArquivo, int ordemDaArvore) :
		nomeDoArquivo(nomeDoArquivo),
		arquivo( fstream(nomeDoArquivo, ios::binary | ios::in | ios::out) )
	{
		// declval -> http://www.cplusplus.com/reference/utility/declval/?kw=declval
		maximoDeBytesParaAChave = declval<TIPO_DAS_CHAVES>().obterTamanhoMaximoEmBytes();
		maximoDeBytesParaODado = declval<TIPO_DOS_DADOS>().obterTamanhoMaximoEmBytes();
	}

	// ------------------------- Métodos
	
};