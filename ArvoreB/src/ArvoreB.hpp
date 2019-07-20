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
	// ------------------------- Campos

	Pagina paginaPai;
	Pagina paginaFilha;
	Pagina paginaIrma;

	// ------------------------- Métodos
	
	bool inserirRecursivo(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado);

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
	
	/**
	 * @brief Insere o par (chave, dado) na árvore.
	 * 
	 * @param chave Chave a ser inserida.
	 * @param dado Dado a ser inserido.
	 * 
	 * @return true Caso tudo corra bem.
	 * @return false Caso um erro ocorra.
	 */
	bool inserir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado);
	
	/**
	 * @brief Remove o par (chave, dado) da árvore.
	 * 
	 * @param chave Chave a ser removida.
	 * @param dado Dado a ser removido.
	 * 
	 * @return true Caso tudo corra bem.
	 * @return false Caso um erro ocorra.
	 */
	bool excluir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado);
	
	TIPO_DOS_DADOS excluir(TIPO_DAS_CHAVES chave);
	
	vector<TIPO_DOS_DADOS> excluirRegistrosComAChave(TIPO_DAS_CHAVES chave);

	TIPO_DOS_DADOS pesquisar(TIPO_DAS_CHAVES chave);

	vector<TIPO_DOS_DADOS> listarDadosComAChave(TIPO_DAS_CHAVES chave);

	vector<TIPO_DOS_DADOS> listarDadosComAChaveEntre(
		TIPO_DAS_CHAVES chaveMenor,
		TIPO_DAS_CHAVES chaveMaior);

	void printar();
};