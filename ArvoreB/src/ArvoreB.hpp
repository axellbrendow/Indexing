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
protected:
	Pagina paginaPai;
	Pagina paginaFilha;
	Pagina paginaIrma;

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

	// ------------------------- Construtores e destrutores

	ArvoreB(string nomeDoArquivo, int ordemDaArvore) :
		nomeDoArquivo(nomeDoArquivo),
		arquivo( fstream(nomeDoArquivo, fstream::binary | fstream::in | fstream::out) ),
		numeroDeChavesPorPagina(ordemDaArvore - 1),
		ordemDaArvore(ordemDaArvore)
	{
		// is_fundamental<> checa se o tipo da chave é primitivo
		// https://stackoverflow.com/questions/580922/identifying-primitive-types-in-templates
		maximoDeBytesParaAChave = is_fundamental<TIPO_DAS_CHAVES>::value ?
			sizeof(TIPO_DAS_CHAVES) :
			// declval -> http://www.cplusplus.com/reference/utility/declval/?kw=declval
			declval<TIPO_DAS_CHAVES>().obterTamanhoMaximoEmBytes();
			
		maximoDeBytesParaODado = is_fundamental<TIPO_DOS_DADOS>::value ?
			sizeof(TIPO_DOS_DADOS) :
			declval<TIPO_DOS_DADOS>().obterTamanhoMaximoEmBytes();
	}

	// ------------------------- Métodos
	
};