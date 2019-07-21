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

/**
 * @brief Classe da árvore B, uma estrutura eficiente para indexamento de registros
 * em disco.
 * 
 * <p>Caso for usar o método print() dá classe, é necessário que a chave e o dado
 * sejam tipos primitivos ou então o operador << deve ser sobrecarregado para que
 * seja possível inserir esses itens num ostream (output stream).</p>
 * 
 * @see [Sobrecarregando o operador <<](https://docs.microsoft.com/pt-br/cpp/standard-library/overloading-the-output-operator-for-your-own-classes?view=vs-2019)
 * 
 * @tparam TIPO_DAS_CHAVES Tipo da chave dos registros. <b>É necessário que a chave
 * seja um tipo primitivo ou então que a sua classe/struct herde de Serializavel e
 * tenha um construtor sem parâmetros.</b>
 * @tparam TIPO_DOS_DADOS Tipo do dado dos registros. <b>É necessário que o dado
 * seja um tipo primitivo ou então que a sua classe/struct herde de Serializavel e
 * tenha um construtor sem parâmetros.</b>
 */
template<typename TIPO_DAS_CHAVES, typename TIPO_DOS_DADOS>
class ArvoreB
{
public:
	// ------------------------- Typedefs

	/**
	 * @brief Padroniza o tipo da página da árvore. Typedefs dentro de classes ou
	 * structs são considerados como boa prática em C++.
	 */
	typedef PaginaB<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> Pagina

protected:
	// ------------------------- Campos

	string erro;

	Pagina paginaPai;
	Pagina paginaFilha;
	Pagina paginaIrma;

	// ------------------------- Métodos
	
	bool inserirRecursivo(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado);

public:
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
			declval<TIPO_DAS_CHAVES>().obterTamanhoMaximoEmBytes();
			// declval -> http://www.cplusplus.com/reference/utility/declval/?kw=declval
			
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

	/**
	 * @brief Exclui o primeiro registro que for encontrado com a chave informada.
	 * 
	 * @param chave Chave a ser procurada.
	 * 
	 * @return TIPO_DOS_DADOS Caso tudo corra bem, retorna o dado correspondente
	 * à chave. Caso contrário, retorna
	 * 
	 * @code{.cpp}
	 * TIPO_DOS_DADOS() // Ex.: se os dados são inteiros, retorna int(), que é 0.
	 * @endcode
	 * 
	 * Em casos de erro, uma flag interna é ativada. Dessa forma, você pode usar
	 * qualquer um dos dois ifs abaixo para checar erros:
	 * 
	 * @code{.cpp}
	 * if (ArvoreB.excluir(chave) == TIPO_DOS_DADOS()) ArvoreB.printError();
	 * if (ArvoreB.erro) ArvoreB.printError();
	 * @endcode
	 */
	TIPO_DOS_DADOS excluir(TIPO_DAS_CHAVES chave);
	
	/**
	 * @brief Exclui todos os registros que forem encontrados com a chave informada.
	 * 
	 * @param chave Chave a ser procurada.
	 * 
	 * @return vector<TIPO_DOS_DADOS> Vetor com cada dado correspondente à chave.
	 */
	vector<TIPO_DOS_DADOS> excluirRegistrosComAChave(TIPO_DAS_CHAVES chave);

	/**
	 * @brief Procura o primeiro registro com a chave informada e pega o dado
	 * correspondente à ela.
	 * 
	 * @param chave Chave a ser procurada.
	 * 
	 * @return TIPO_DOS_DADOS Caso tudo corra bem, retorna o dado correspondente
	 * à chave. Caso contrário, retorna
	 * 
	 * @code{.cpp}
	 * TIPO_DOS_DADOS() // Ex.: se os dados são inteiros, retorna int(), que é 0.
	 * @endcode
	 * 
	 * Em casos de erro, uma flag interna é ativada. Dessa forma, você pode usar
	 * qualquer um dos dois ifs abaixo para checar erros:
	 * 
	 * @code{.cpp}
	 * if (ArvoreB.excluir(chave) == TIPO_DOS_DADOS()) ArvoreB.printError();
	 * if (ArvoreB.erro) ArvoreB.printError();
	 * @endcode
	 */
	TIPO_DOS_DADOS pesquisar(TIPO_DAS_CHAVES chave);
	
	/**
	 * @brief Procura todos os registros que forem encontrados com a chave informada.
	 * 
	 * @param chave Chave a ser procurada.
	 * 
	 * @return vector<TIPO_DOS_DADOS> Vetor com cada dado correspondente à chave.
	 */
	vector<TIPO_DOS_DADOS> listarDadosComAChave(TIPO_DAS_CHAVES chave);

	/**
	 * @brief Procura todos os registros que forem encontrados com a chave entre
	 * as chaves informadas. Inclui as próprias chaves. O intervalo é
	 * [chaveMenor, chaveMaior].
	 * 
	 * <p><b>Caso TIPO_DAS_CHAVES e TIPO_DOS_DADOS forem primitivos, ignore o
	 * próximo parágrafo.</b></p>
	 * 
	 * <p><b>Esta função exige que TIPO_DAS_CHAVES e TIPO_DOS_DADOS sobrecarreguem
	 * os operadores <= (menor ou igual) e >= (maior ou igual).</b></p>
	 * 
	 * @param chaveMenor Valor do limite inferior.
	 * @param chaveMaior Valor do limite superior.
	 * 
	 * @return vector<TIPO_DOS_DADOS> Vetor com cada dado correspondente à chave.
	 */
	vector<TIPO_DOS_DADOS> listarDadosComAChaveEntre(
		TIPO_DAS_CHAVES chaveMenor,
		TIPO_DAS_CHAVES chaveMaior);

	/**
	 * @brief Imprime, na saída padrão, uma representação vertical da árvore.
	 * A saída é similar à do comando "tree /f" do windows.
	 */
	void print();
};