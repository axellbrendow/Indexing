/**
 * @file ArvoreB.hpp
 * @author Axell Brendow ( https://github.com/axell-brendow )
 * @brief Arquivo da classe ArvoreB.
 * 
 * @copyright Copyright (c) 2019 Axell Brendow Batista Moreira
 */

#pragma once

#include "templates/tipos.hpp"
#include "templates/debug.hpp"
#include "tiposArvore.hpp"
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
private:
	// ------------------------- Métodos
	
	void abrirArquivo(string nome)
	{
		arquivo = fstream(nome, fstream::binary | fstream::in | fstream::out);

		if (!arquivo) // Checa se o arquivo não existe ou não está acessível
		{
			// Caso não, cria um arquivo e o fecha
			fstream(nome, fstream::binary | fstream::out).close();
			// Agora sim reabre o arquivo nos modos de leitura e escrita
			arquivo = 
				fstream(nome, fstream::binary | fstream::in | fstream::out);
		}
	}

public:
	// ------------------------- Typedefs

	/**
	 * @brief Padroniza o tipo da página da árvore. Typedefs dentro de classes ou
	 * structs são considerados como boa prática em C++.
	 */
	typedef PaginaB<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> Pagina;

    /**
     * @brief Padroniza o tipo do par (chave, dado).
     */
    typedef struct Par<TIPO_DAS_CHAVES, TIPO_DOS_DADOS> Par;

protected:
	// ------------------------- Campos

	string erro;
	string nomeDoArquivo;
	fstream arquivo;

	int maximoDeBytesParaAChave;
	int maximoDeBytesParaODado;
	int numeroDeChavesPorPagina;
	int ordemDaArvore;

	Pagina *paginaPai; // falta link aqui
	Pagina *paginaFilha;
	Pagina *paginaIrma;

	// ------------------------- Métodos
	
	bool inserirRecursivo(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado);

public:
	// ------------------------- Campos

	const int tamanhoCabecalhoAntesDaRaiz = 0;
	const int tamanhoCabecalho = tamanhoCabecalhoAntesDaRaiz + sizeof(file_pointer_type);

	// ------------------------- Construtores e destrutores

	ArvoreB(string nomeDoArquivo, int ordemDaArvore) :
		nomeDoArquivo(nomeDoArquivo),
		numeroDeChavesPorPagina(ordemDaArvore - 1),
		ordemDaArvore(ordemDaArvore),
		paginaPai(new Pagina(ordemDaArvore) ),
		paginaFilha(new Pagina(ordemDaArvore) ),
		paginaIrma(new Pagina(ordemDaArvore) )
	{
		abrirArquivo(nomeDoArquivo);

		obterTamanhoEmBytesDaChaveEDoDado<TIPO_DAS_CHAVES, TIPO_DOS_DADOS>(
			maximoDeBytesParaAChave, maximoDeBytesParaODado
		);
	}

	~ArvoreB()
	{
		delete paginaPai;
		delete paginaFilha;
		delete paginaIrma;
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
	bool inserir(TIPO_DAS_CHAVES chave, TIPO_DOS_DADOS dado)
	{
		// Pula o cabeçalho do arquivo que vem antes do endereço raiz
		arquivo.seekg(tamanhoCabecalhoAntesDaRaiz);
		
		file_pointer_type enderecoDaRaiz;
		arquivo >> enderecoDaRaiz;
		arquivo.seekg(enderecoDaRaiz);
		arquivo >> paginaFilha; // Carrega a raiz

		if (arquivo.fail())
		{
			// cerr é a saída padrão de erros. Em alguns caso pode ser igual a cout.
			cerr << "[ArvoreB] Não foi possível ler a raiz do arquivo."
				<< endl << "Exceção lançada" << endl;

			throw length_error("[ArvoreB] Não foi possível ler a raiz do arquivo.");
		}

		else
		{
			int indiceDeInsercao = paginaFilha->obterIndiceDeInsercao(chave);

			// Checa se não há ponteiro de descida. Caso não, a raiz é uma folha.
			if (paginaFilha->ponteiros[indiceDeInsercao] == constantes::ptrNuloPagina)
			{
				// Checa se a inserção na raiz falhou. Acontece quando ela está cheia.
				if (!paginaFilha->inserir(chave, dado, indiceDeInsercao))
				{
					// Inicia o processo de duplicação da raiz
					paginaIrma->limpar();
					paginaFilha->transferirMetadePara(paginaIrma);
					
					Pagina* paginaDeInsercao =
						chave < paginaIrma->chaves.back() ?
						paginaFilha : paginaIrma;

					paginaDeInsercao->inserir(chave, dado);

					// Inicia o processo de criação da nova raiz
					paginaPai->limpar();
					paginaDeInsercao->promoverElementoPara(paginaPai, 0);
					paginaPai->ponteiros[0] = tamanhoCabecalhoAntesDaRaiz;
				}
			}
		}
	}
	
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